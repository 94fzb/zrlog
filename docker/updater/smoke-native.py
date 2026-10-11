#!/usr/bin/env python3
"""Native HTTP, JSON/reflection and restart persistence smoke; does not require/mutate Docker."""
import argparse
import json
import os
from pathlib import Path
import socket
import subprocess
import tempfile
import time
import urllib.error
import urllib.request


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("binary", type=Path)
    args = parser.parse_args()
    binary = args.binary.resolve()
    with tempfile.TemporaryDirectory(prefix="zrlog-updater-native-") as tmp:
        root = Path(tmp)
        state = root / "state"
        state.mkdir()
        (root / "docker-compose.yml").write_text("services: {}\n")
        (root / "docker-compose.updater.yml").write_text("services: {}\n")
        (root / ".env").write_text("ZRLOG_IMAGE=zrlog/zrlog:local\n")
        task_id = "native-smoke-task-1234"
        (state / "state.json").write_text(json.dumps({
            "schemaVersion": 1, "settings": {"repository": "", "channel": "release"},
            "jobs": [{"id": task_id, "phase": "replacing", "version": "4.0.0", "buildId": "1234567"}],
        }))
        with socket.socket() as listener:
            listener.bind(("127.0.0.1", 0))
            port = listener.getsockname()[1]
        token = "native_smoke_token_1234567890_abcdef"
        env = dict(os.environ, UPDATER_TOKEN=token, UPDATER_PROJECT="native-smoke",
                   UPDATER_DEPLOYMENT_DIR=str(root), UPDATER_STATE_DIR=str(state),
                   UPDATER_BIND="127.0.0.1", UPDATER_PORT=str(port))

        def request(path, auth=True):
            headers = {"Authorization": "Bearer " + token} if auth else {}
            opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
            return opener.open(urllib.request.Request(f"http://127.0.0.1:{port}" + path,
                                                     headers=headers), timeout=2)

        for attempt in range(2):
            process = subprocess.Popen([str(binary)], env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
            try:
                deadline = time.monotonic() + 20
                while True:
                    try:
                        with request("/health", False) as response:
                            assert json.load(response) == {"status": "ok"}
                        break
                    except (OSError, urllib.error.URLError):
                        if process.poll() is not None or time.monotonic() >= deadline:
                            raise RuntimeError("Native HTTP server did not start")
                        time.sleep(0.1)
                subprocess.run([str(binary), "--healthcheck"], env=env, check=True, timeout=10)
                try:
                    request("/v1/upgrades/" + task_id, False)
                    raise AssertionError("Missing token accepted")
                except urllib.error.HTTPError as error:
                    assert error.code == 401
                with request("/v1/upgrades/" + task_id) as response:
                    job = json.load(response)
                    assert job["id"] == task_id and job["phase"] == "interrupted"
                    assert job["recoveryRequired"] is True
                    assert response.headers["Cache-Control"] == "no-store"
            finally:
                process.terminate()
                output, _ = process.communicate(timeout=15)
                if process.returncode not in (0, -15, 143):
                    raise RuntimeError(output.decode())
        print(f"Native HTTP/auth/JSON/restart smoke passed; binary {binary.stat().st_size} bytes")


if __name__ == "__main__":
    main()
