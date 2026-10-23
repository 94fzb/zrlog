import base64
import hashlib
import http.cookiejar
import http.server
import json
import os
from pathlib import Path
import secrets
import shutil
import socket
import sqlite3
import subprocess
import sys
import threading
import tempfile
import time
import urllib.error
import urllib.request
import zipfile

kind, package = sys.argv[1], Path(sys.argv[2])
digest = hashlib.sha256(package.read_bytes()).hexdigest()
runtime = Path(tempfile.mkdtemp(prefix='zrlog-cohesion-' + kind + '-' + digest[:7] + '-'))
with zipfile.ZipFile(package) as archive:
    archive.extractall(runtime)
evidence = Path(sys.argv[3]).resolve()
evidence.mkdir(parents=True, exist_ok=True)
report = {'kind': kind, 'package_sha256': digest, 'runtime': str(runtime), 'checks': [], 'provider_requests': []}

class Provider(http.server.BaseHTTPRequestHandler):
    queue = []
    def log_message(self, *args): pass
    def do_POST(self):
        body = json.loads(self.rfile.read(int(self.headers['Content-Length'])))
        report['provider_requests'].append({'path': self.path, 'model': body.get('model')})
        if self.path.endswith('/images/generations'):
            response = {'data': [{'b64_json': 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a9S8AAAAASUVORK5CYII='}]}
            payload = json.dumps(response).encode()
            content_type = 'application/json'
        else:
            output = self.queue.pop(0) if self.queue else {'type': 'message', 'id': 'msg_1', 'role': 'assistant',
                'status': 'completed', 'content': [{'type': 'output_text', 'text': 'Explicit actor response', 'annotations': []}]}
            payload = ('event: response.completed\ndata: ' + json.dumps({'type': 'response.completed',
                'response': {'status': 'completed', 'output': [output]}}) + '\n\n').encode()
            content_type = 'text/event-stream'
        self.send_response(200)
        self.send_header('Content-Type', content_type)
        self.send_header('Content-Length', str(len(payload)))
        self.end_headers()
        self.wfile.write(payload)

provider = http.server.ThreadingHTTPServer(('127.0.0.1', 0), Provider)
threading.Thread(target=provider.serve_forever, daemon=True).start()
with socket.socket() as reservation:
    reservation.bind(('127.0.0.1', 0))
    port = reservation.getsockname()[1]
origin = 'http://127.0.0.1:' + str(port)
base = origin + '/blog'
password = secrets.token_urlsafe(18)
cookies = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.ProxyHandler({}), urllib.request.HTTPCookieProcessor(cookies))
process = None
log = None

def check(name, condition):
    if not condition: raise AssertionError(name)
    report['checks'].append(name)

def request(path, body=None, accept='application/json'):
    headers = {'Origin': origin, 'Accept': accept}
    data = None
    if body is not None:
        headers['Content-Type'] = 'application/json'
        data = json.dumps(body).encode()
    req = urllib.request.Request(base + path, data=data, headers=headers)
    with opener.open(req, timeout=120) as response:
        return response.status, response.read().decode('utf-8')

def api(path, body=None):
    status, raw = request(path, body)
    try: value = json.loads(raw)
    except json.JSONDecodeError:
        raise AssertionError('Non-JSON response at ' + path + ': ' + raw[:200]) from None
    if status != 200 or value.get('error') != 0:
        raise AssertionError('API rejected ' + path.split('?')[0] + ': error=' + str(value.get('error')))
    return value.get('data')

def events(raw):
    return [json.loads(line[5:].strip()) for line in raw.splitlines() if line.startswith('data:')]

def start(phase):
    global process, log
    log = (evidence / (kind + '-runtime-' + phase + '.log')).open('w')
    env = os.environ.copy()
    for name in ['ZRLOG_INSTALL_TOKEN', 'DB_PROPERTIES', 'DB_PROPERTIES_FILE_NAME', 'INSTALL_LOCK_FILE_NAME', 'ZRLOG_HOME']:
        env.pop(name, None)
    if kind == 'java':
        command = ['java', '-Xmx256m', '-Dfile.encoding=UTF-8', '-jar', 'zrlog-starter.jar']
    else:
        binary = runtime / 'zrlog'
        binary.chmod(0o700)
        command = [str(binary)]
    command += ['--port=' + str(port), '--contextPath=/blog']
    process = subprocess.Popen(command, cwd=runtime, env=env, stdout=log, stderr=subprocess.STDOUT)
    for attempt in range(120):
        if process.poll() is not None: raise AssertionError('Application exited during ' + phase)
        try:
            status, _ = request('/api/install/installResource')
            if status == 200: return
        except (OSError, urllib.error.URLError): pass
        time.sleep(0.5)
    raise AssertionError('Application startup timeout')

def stop():
    global process, log
    if process is not None:
        process.terminate()
        try: process.wait(timeout=20)
        except subprocess.TimeoutExpired: process.kill(); process.wait(timeout=10)
        process = None
    if log is not None: log.close(); log = None

def login():
    api('/api/admin/login', {'userName': 'admin', 'password': hashlib.md5(password.encode()).hexdigest(), 'https': False})
    check('browser login cookie', any(cookie.name == 'admin-token' for cookie in cookies))

try:
    if kind == 'java':
        hashes = {}
        for jar in sorted((runtime / 'lib').glob('zrlog-admin-*.jar')):
            expected = evidence / 'inputs' / jar.name
            check('embedded current ' + jar.name.split('-4.')[0], expected.exists() and jar.read_bytes() == expected.read_bytes())
            hashes[jar.name] = hashlib.sha256(jar.read_bytes()).hexdigest()
        report['admin_jars'] = hashes
        service_jar = next((runtime / 'lib').glob('zrlog-service-*.jar'))
        check('embedded current plugin artifact validator', service_jar.read_bytes() == (evidence / 'inputs' / service_jar.name).read_bytes())
        report['service_jar_sha256'] = hashlib.sha256(service_jar.read_bytes()).hexdigest()
        report['install_jar_sha256'] = hashlib.sha256(next((runtime / 'lib').glob('zrlog-install-web-*.jar')).read_bytes()).hexdigest()
    start('install')
    check('non-root install page', request('/install')[0] == 200)
    check('uninstalled state', api('/api/install/installResource')['installed'] is False)
    check('environment probe', api('/api/install/probe')['status'] == 'pass')
    api('/api/install/testDbConn', {'dbType': 'sqlite'})
    check('connection test leaves no production database', not (runtime / 'conf/zrlog.db').exists())
    _, stream = request('/api/install/startInstall', {'dbType': 'sqlite', 'username': 'admin',
        'password': password, 'email': 'admin@example.invalid', 'title': 'Identity Boundary Site', 'second_title': 'Package validation'}, 'text/event-stream')
    check('real install completes', 'event: install-complete' in stream)
    check('installed state', api('/api/install/installResource')['installed'] is True)
    for name in ['db.properties', 'install.lock', 'zrlog.db']:
        path = runtime / 'conf' / name
        check('installed file ' + name, path.exists() and path.stat().st_mode & 0o777 == 0o600)
    check('blog home', request('/')[0] == 200)
    check('login page', request('/admin/login')[0] == 200)
    login()
    api('/api/admin/system')
    check('system API', True)
    check('dashboard SSR', request('/admin/index')[0] == 200)
    themes = api('/api/admin/website/template')
    check('theme catalog from production graph', isinstance(themes, list) and len(themes) > 0)
    theme = themes[0]
    check('theme settings from production graph', api('/api/admin/template-config?shortTemplate=' + theme['shortTemplate']) is not None)
    article_body = {'title': 'Explicit identity article', 'alias': 'identity-boundary-package', 'typeId': 1,
        'content': '<p>Persistent boundary content</p>', 'markdown': 'Persistent boundary content',
        'rubbish': True, 'privacy': False, 'canComment': True, 'recommended': False, 'transparentPublish': False}
    detail = api('/api/admin/article/create', article_body)
    article = detail['article']
    article_id = article['logId']
    report['article_id'] = article_id
    check('article written through production graph', int(article_id) > 0 and article['title'] == article_body['title'])
    api('/api/admin/website/ai', {'ai_provider': 'OPEN_AI', 'ai_model': 'gpt-4.1',
        'ai_base_url': 'http://127.0.0.1:' + str(provider.server_port) + '/v1', 'ai_reasoning_enabled': False,
        'ai_image_provider': 'OPEN_AI', 'ai_image_model': 'gpt-image-2',
        'ai_image_base_url': 'http://127.0.0.1:' + str(provider.server_port) + '/v1'})
    api('/api/admin/user/updatePreferences?section=assistant', {'assistant': {'knowledgeScope': 'off'}})
    _, wire = request('/api/admin/article/ai', {'input': 'Read this private input', 'articleId': int(article_id),
        'files': [{'name': 'note.txt', 'data': base64.b64encode(b'Private package input').decode()}]})
    check('AI worker completes with private file', any(item.get('type') == 'done' for item in events(wire)))
    history = api('/api/admin/article/ai/messages/export?id=' + str(article_id))
    check('AI conversation persists', 'Explicit actor response' in json.dumps(history))
    check('private file remains account scoped', (runtime / 'conf/ai-input-files/1').is_dir())
    _, cover = request('/api/admin/article/ai?tool=cover&input=cover&id=' + str(article_id),
        {'title': 'Cover', 'markdown': 'Persistent boundary content', 'digest': 'Package validation'})
    check('cover generation uses explicit skill factory', '/admin/attached/tmp/users/1/ai/' in cover)
    api('/api/admin/user/updatePreferences?section=assistant', {'assistant': {'knowledgeScope': 'own_all'}})
    Provider.queue.append({'type': 'function_call', 'id': 'fc_1', 'call_id': 'call_1', 'name': 'create_article',
        'arguments': json.dumps({'title': 'Created by explicit tool', 'typeId': 1, 'status': 'draft', 'content': '<p>Tool content</p>'}),
        'status': 'completed'})
    _, pending = request('/api/admin/article/ai', {'input': 'Create a draft with confirmation', 'articleId': int(article_id)})
    view = next(item['run'] for item in events(pending) if item.get('type') == 'approval-required')
    check('write tool requires confirmation', view['approval']['tool'] == 'create_article')
    _, resumed = request('/api/admin/article/ai/approval', {'articleId': int(article_id), 'runId': view['runId'],
        'approvalId': view['approval']['id'], 'decision': 'approve'})
    check('confirmed tool resumes and completes', any(item.get('type') == 'done' for item in events(resumed)))
    with sqlite3.connect(runtime / 'conf/zrlog.db') as db:
        check('tool write belongs to actor', db.execute("select userId from log where title='Created by explicit tool'").fetchone() == (1,))
        audit = db.execute("select value from website where name='admin_audit_log'").fetchone()[0]
        check('explicit audit actor persisted', any(entry.get('actorUserId') == 1 for entry in json.loads(audit)))
    stop()
    cookies.clear()
    start('restart')
    login()
    api('/api/admin/system')
    with sqlite3.connect(runtime / 'conf/zrlog.db') as db:
        check('article survives restart', db.execute('select title from log where logId=?', (article_id,)).fetchone() == (article_body['title'],))
    history = api('/api/admin/article/ai/messages/export?id=' + str(article_id))
    check('AI history survives restart', 'Explicit actor response' in json.dumps(history))
    check('dashboard survives restart', request('/admin/index')[0] == 200)
    report['status'] = 'passed'
except Exception as error:
    report['status'] = 'failed'
    report['failure'] = type(error).__name__ + ': ' + str(error)
    raise
finally:
    stop()
    provider.shutdown()
    report['services_stopped'] = True
    if report.get('status') == 'passed':
        shutil.rmtree(runtime)
        report['runtime_removed'] = True
    (evidence / (kind + '-consumer.json')).write_text(json.dumps(report, indent=2) + '\n')
    print(json.dumps({'kind': kind, 'status': report['status'], 'checks': len(report['checks']), 'runtime': str(runtime)}), flush=True)
