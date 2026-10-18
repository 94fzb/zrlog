#!/usr/bin/env python3
"""Verify the compiled agent against a disposable existing site and inherited configuration."""
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile


def run(command, root, environment, expected=0):
    result = subprocess.run(command, cwd=root, env=environment, text=True,
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=60)
    if result.returncode != expected:
        raise AssertionError(f"Expected exit {expected}, got {result.returncode}\n{result.stdout}")
    return result.stdout


def main():
    if len(sys.argv) != 3:
        raise SystemExit("Usage: test-native-agent.py <java> <runtime-classpath-file>")
    repository = Path(__file__).resolve().parents[2]
    java = Path(sys.argv[1]).resolve()
    classpath = os.pathsep.join([str(repository / "package/target/classes"),
                                 Path(sys.argv[2]).read_text().strip()])
    environment = os.environ.copy()
    for name in ["DB_PROPERTIES", "DB_PROPERTIES_FILE_NAME", "INSTALL_LOCK_FILE_NAME",
                 "ZRLOG_PID_FILE", "ZRLOG_HTTP_PORT_FILE", "JAVA_TOOL_OPTIONS", "JDK_JAVA_OPTIONS"]:
        environment.pop(name, None)
    with tempfile.TemporaryDirectory(prefix="zrlog-agent-contract-") as directory:
        root = Path(directory)
        conf = root / "conf"
        conf.mkdir()
        (conf / "db.properties").write_text("driverClass=existing.database.must.not.be.loaded\njdbcUrl=jdbc:existing:protected\n")
        (conf / "install.lock").write_text("existing-installation\n")
        before = {path: path.read_bytes() for path in conf.iterdir()}
        environment["ZRLOG_HOME"] = str(root)
        environment["ZRLOG_PID_FILE"] = str(root / "existing.pid")
        environment["ZRLOG_HTTP_PORT_FILE"] = str(root / "existing.port")
        options = [f"-Dsws.{name}.path={root / name}" for name in ["conf", "static", "cache", "temp", "log"]]
        options.append(f"-Dsws.root.path={root}")
        fixture = repository / "shell/native/tests/com/zrlog/web/GraalvmAgentRuntimeContract.java"
        run([str(java.with_name("javac")), "-cp", classpath, "-d", str(root), str(fixture)], root, environment)
        run([str(java), *options, "-cp", str(root) + os.pathsep + classpath,
             "com.zrlog.web.GraalvmAgentRuntimeContract", str(root)], root, environment)
        command = [str(java), *options, "-cp", classpath, "com.zrlog.web.GraalvmAgentApplication", "--contextPath=/agent"]
        output = run(command, root, environment)
        assert "Loaded web modules: [admin," in output and "install, blog]" in output, output
        installed = re.search(r"Native agent installed disposable H2 site at (.+)", output)
        assert installed and not Path(installed.group(1).strip()).exists(), output
        for name, value in [("DB_PROPERTIES", "driverClass=existing.database.must.not.be.loaded"),
                            ("DB_PROPERTIES_FILE_NAME", str(conf / "db.properties")),
                            ("INSTALL_LOCK_FILE_NAME", str(conf / "install.lock"))]:
            output = run(command, root, {**environment, name: value}, expected=1)
            assert f"requires an isolated {name}" in output, output
            assert "Native agent installed" not in output, output
        assert before == {path: path.read_bytes() for path in conf.iterdir()}, "Existing configuration changed"
        assert not (root / "existing.pid").exists() and not (root / "existing.port").exists(), "Existing runtime paths used"
    print("Native agent: real H2 install, fresh databases, module startup, cleanup and existing-site isolation passed")


if __name__ == "__main__":
    main()
