#!/usr/bin/env python3
"""Exercise the packaging script's cleanup after a successful or failed agent process."""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


REPOSITORY = Path(__file__).resolve().parents[2]


class NativeAgentCleanupTest(unittest.TestCase):
    def check_agent_exit(self, exit_code):
        with tempfile.TemporaryDirectory(prefix="zrlog agent cleanup ") as directory:
            root = Path(directory)
            for name in ["bin", "shell/native", "shell/lib"]:
                (root / name).mkdir(parents=True)
            shutil.copy2(REPOSITORY / "shell/native/package-native.sh", root / "shell/native")
            shutil.copy2(REPOSITORY / "shell/lib/updater-modules.sh", root / "shell/lib")
            (root / "bin/add-build-info.sh").write_text("#!/usr/bin/env bash\nexit 0\n")
            maven = root / "mvnw"
            maven.write_text('''#!/usr/bin/env python3
import os
from pathlib import Path
import sys

runtime = Path("package/target/native-agent-runtime")
if "exec:exec@java-agent" in sys.argv:
    runtime.mkdir(parents=True)
    log = (runtime / "held-open.log").open("w")
    log.write("tracing process owns this handle until it exits")
    log.flush()
    (runtime / "conf").mkdir()
    (runtime / "conf/late-plugin-write").write_text("background writer completed")
    sys.exit(int(os.environ["TEST_AGENT_EXIT_CODE"]))
if "clean" not in sys.argv:
    if runtime.exists():
        sys.exit(99)
    Path("native-compilation-started").touch()
    Path("package/target/zrlog").write_text("native binary fixture")
''')
            maven.chmod(0o755)
            result = subprocess.run(["bash", "-e", "shell/native/package-native.sh", "dev"],
                                    cwd=root, env={**os.environ, "TEST_AGENT_EXIT_CODE": str(exit_code)},
                                    stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, timeout=30)
            self.assertEqual(exit_code, result.returncode, result.stdout)
            self.assertFalse((root / "package/target/native-agent-runtime").exists())
            self.assertEqual(exit_code == 0, (root / "native-compilation-started").exists())

    def test_success_cleans_before_native_compilation(self):
        self.check_agent_exit(0)

    def test_failure_cleans_and_preserves_agent_exit_code(self):
        self.check_agent_exit(42)


if __name__ == "__main__":
    unittest.main()
