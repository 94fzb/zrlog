"""Run with python3 .github/actions/process-artifact/test_process_artifact.py.

Exercises the real Bash client and curl against a local HTTP service only.
"""
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile
import threading
import unittest
from collections import Counter
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


SCRIPT = Path(__file__).with_name("process-artifact.sh")
SOURCE = b"uncompressed-native-binary\x00\xff"
RESULT = b"compressed-native-binary\x00\xfe"
GATEWAY_ERROR = b"<html><h1>504 Gateway Time-out</h1></html>\n"


class ProcessArtifactTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.directory = Path(self.temporary.name)
        self.artifact = self.directory / "native binary"
        self.artifact.write_bytes(SOURCE)
        self.response_directory = self.directory / "responses"
        self.response_directory.mkdir()
        self.attempts = Counter()
        self.fail_once = set()
        self.fail_always = set()
        self.invalid_json = False
        self.bad_checksum = False
        self.requests = []
        test = self

        class Handler(BaseHTTPRequestHandler):
            def handle_request(self):
                size = int(self.headers.get("Content-Length", "0"))
                body = self.rfile.read(size)
                key = (self.command, self.path)
                test.requests.append((key, dict(self.headers), body))
                test.attempts[key] += 1
                if key in test.fail_always or (key in test.fail_once and test.attempts[key] == 1):
                    self.respond(504, GATEWAY_ERROR)
                elif key == ("POST", "/artifact/api/v1/uploads"):
                    if test.invalid_json:
                        self.respond(200, b"<html>unexpected response</html>")
                    else:
                        self.respond_json({"id": "test-job", "status": "PENDING"})
                elif key == ("GET", "/artifact/api/v1/jobs/detail?id=test-job"):
                    self.respond_json({
                        "id": "test-job", "status": "SUCCEEDED",
                        "downloadPath": "/api/v1/jobs/result?id=test-job",
                        "sha256": "0" * 64 if test.bad_checksum else hashlib.sha256(RESULT).hexdigest(),
                        "sizeBytes": len(RESULT),
                    })
                elif key == ("GET", "/artifact/api/v1/jobs/result?id=test-job"):
                    self.respond(200, RESULT)
                elif key == ("DELETE", "/artifact/api/v1/jobs/result?id=test-job"):
                    self.respond_json({"deleted": True})
                else:
                    self.respond(404, b"Unknown route")

            def respond(self, status, body):
                self.send_response(status)
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)

            def respond_json(self, data):
                self.respond(200, json.dumps({"success": True, "data": data}).encode())

            def log_message(self, *_):
                pass

            do_GET = handle_request
            do_POST = handle_request
            do_DELETE = handle_request

        self.server = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
        self.thread = threading.Thread(target=self.server.serve_forever, daemon=True)
        self.thread.start()
        self.addCleanup(self.close_server)

    def close_server(self):
        self.server.shutdown()
        self.server.server_close()
        self.thread.join()

    def run_client(self):
        env = os.environ.copy()
        # Do not let developer proxy settings affect this local fixture.
        env.update({
            "ARTIFACT_SERVICE_URL": f"http://127.0.0.1:{self.server.server_port}/artifact/",
            "ARTIFACT_SERVICE_TOKEN": "test-token",
            "ARTIFACT_POLL_INTERVAL_SECONDS": "1",
            "ARTIFACT_PROCESS_TIMEOUT_SECONDS": "30",
            "TMPDIR": str(self.response_directory),
            "NO_PROXY": "127.0.0.1", "no_proxy": "127.0.0.1",
        })
        result = subprocess.run(
            ["bash", str(SCRIPT), str(self.artifact), "zrlog", "4.0.0-SNAPSHOT", "Linux-amd64"],
            env=env, capture_output=True, text=True, timeout=45,
        )
        self.assertEqual([], list(self.response_directory.iterdir()))
        self.assertEqual([], list(self.directory.glob(".processed-artifact.*")))
        return result

    def test_retries_gateway_errors_without_corrupting_json_or_download(self):
        self.fail_once = {
            ("POST", "/artifact/api/v1/uploads"),
            ("GET", "/artifact/api/v1/jobs/detail?id=test-job"),
            ("GET", "/artifact/api/v1/jobs/result?id=test-job"),
            ("DELETE", "/artifact/api/v1/jobs/result?id=test-job"),
        }
        result = self.run_client()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(RESULT, self.artifact.read_bytes())
        self.assertTrue(os.access(self.artifact, os.X_OK))
        self.assertNotIn("parse error", result.stderr)
        self.assertNotIn("Warning:", result.stderr)
        for key in self.fail_once:
            self.assertEqual(2, self.attempts[key])
        for (method, _), headers, body in self.requests:
            self.assertEqual("Bearer test-token", headers["Authorization"])
            if method == "POST":
                self.assertEqual(SOURCE, body)
                self.assertEqual("zrlog", headers["X-Artifact-Name"])
                self.assertEqual("4.0.0-SNAPSHOT", headers["X-Artifact-Version"])
                self.assertEqual("Linux-amd64", headers["X-Artifact-Architecture"])

    def test_exhausted_upload_retries_fail_and_preserve_original(self):
        self.fail_always = {("POST", "/artifact/api/v1/uploads")}
        result = self.run_client()
        self.assertNotEqual(0, result.returncode)
        self.assertIn("Unable to upload artifact for processing", result.stderr)
        self.assertEqual(1, result.stderr.count("<html>"))
        self.assertEqual(SOURCE, self.artifact.read_bytes())
        self.assertEqual({("POST", "/artifact/api/v1/uploads"): 4}, self.attempts)

    def test_bad_checksum_preserves_original_and_does_not_acknowledge_result(self):
        self.bad_checksum = True
        result = self.run_client()
        self.assertNotEqual(0, result.returncode)
        self.assertIn("Processed artifact checksum mismatch", result.stderr)
        self.assertEqual(SOURCE, self.artifact.read_bytes())
        self.assertEqual(0, self.attempts[("DELETE", "/artifact/api/v1/jobs/result?id=test-job")])

    def test_invalid_success_response_fails_and_preserves_original(self):
        self.invalid_json = True
        result = self.run_client()
        self.assertNotEqual(0, result.returncode)
        self.assertIn("Artifact upload response is invalid", result.stderr)
        self.assertEqual(SOURCE, self.artifact.read_bytes())

    def test_cleanup_failure_remains_nonfatal_after_verified_replacement(self):
        self.fail_always = {("DELETE", "/artifact/api/v1/jobs/result?id=test-job")}
        result = self.run_client()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertIn("Warning: unable to remove temporary artifact result", result.stderr)
        self.assertEqual(RESULT, self.artifact.read_bytes())


if __name__ == "__main__":
    unittest.main()
