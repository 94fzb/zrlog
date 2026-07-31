from __future__ import annotations

import importlib.util
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch


MODULE_PATH = Path(__file__).with_name("generate_trust_metadata.py")
SPEC = importlib.util.spec_from_file_location("generate_trust_metadata", MODULE_PATH)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC.loader is not None
SPEC.loader.exec_module(MODULE)


class GenerateTrustMetadataTest(unittest.TestCase):

    def test_release_artifacts_excludes_aliases_and_sidecars(self) -> None:
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            expected = directory / "zrlog-3.9.0-abcdef0-release-Linux-amd64.zip"
            expected.write_bytes(b"release")
            (directory / "zrlog.zip").write_bytes(b"alias")
            (directory / (expected.name + ".sha256")).write_text("old", encoding="utf-8")

            self.assertEqual([expected], MODULE.release_artifacts(directory))

    def test_generate_records_artifact_and_build_evidence(self) -> None:
        with tempfile.TemporaryDirectory() as temporary:
            artifact = Path(temporary) / "zrlog-3.9.0-abcdef0-release.zip"
            artifact.write_bytes(b"release")

            def fake_run(command: list[str]) -> str:
                if command[0] == "syft":
                    Path(command[-1].split("=", 1)[1]).write_text(
                        '{"bomFormat":"CycloneDX"}\n', encoding="utf-8"
                    )
                elif command[0] == "cosign":
                    Path(command[4]).write_text('{"verificationMaterial":{}}\n', encoding="utf-8")
                return ""

            with patch.object(MODULE, "run", side_effect=fake_run):
                generated = MODULE.generate(
                    artifact,
                    source_commit="a" * 40,
                    source_repository="https://github.com/94fzb/zrlog",
                    workflow_url="https://github.com/94fzb/zrlog/actions/runs/1",
                    build_timestamp="2026-07-31T00:00:00+00:00",
                )

            self.assertEqual(4, len(generated))
            provenance = json.loads(
                artifact.with_name(artifact.name + ".provenance.json").read_text(encoding="utf-8")
            )
            self.assertEqual(7, provenance["artifact"]["size"])
            self.assertEqual("a" * 40, provenance["source"]["commit"])
            self.assertEqual("CycloneDX JSON", provenance["evidence"]["sbom"]["format"])


if __name__ == "__main__":
    unittest.main()
