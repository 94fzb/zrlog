import copy
import importlib.util
import hashlib
import io
import json
from pathlib import Path
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location("sources", Path(__file__).with_name("publish-update-sources.py"))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class UpdateSourcesTest(unittest.TestCase):
    def setUp(self):
        self.commit = "a" * 40
        self.name = "zrlog-4.0.0-aaaaaaa-release.zip"
        self.manifest = dict(version="4.0.0", sourceCommit=self.commit, buildId="aaaaaaa",
                             zipDownloadUrl="https://dl.zrlog.com/release/" + self.name,
                             zipFileSize=123, zipSha256="b" * 64)
        self.release = dict(draft=False, prerelease=False, tag_name="v4.0.0", assets=[dict(
            name=self.name, state="uploaded", size=123,
            browser_download_url="https://github.com/94fzb/zrlog/releases/download/v4.0.0/" + self.name)])

    def test_only_actual_verified_sources_are_advertised_with_shared_checksum(self):
        verified = []
        result = module.enrich(self.manifest, self.release, self.commit, lambda *args: verified.append(args))
        self.assertEqual([entry["id"] for entry in result["zipSources"]], ["dl", "github"])
        self.assertEqual(len(verified), 2)
        self.assertEqual(verified[0][1:], verified[1][1:])
        self.assertEqual(result["zipSha256"], self.manifest["zipSha256"])
        self.assertNotIn("zipSources", self.manifest)

    def test_rejects_drafts_missing_assets_wrong_source_and_corrupt_downloads(self):
        for change in [dict(draft=True), dict(prerelease=True), dict(tag_name="v4.0.1"), dict(assets=[])]:
            with self.subTest(change=change), self.assertRaises(ValueError):
                module.enrich(self.manifest, dict(self.release, **change), self.commit, lambda *args: None)
        with self.assertRaises(ValueError):
            module.enrich(self.manifest, self.release, "c" * 40, lambda *args: None)
        release = copy.deepcopy(self.release)
        release["assets"][0]["browser_download_url"] = "https://example.com/guessed.zip"
        with self.assertRaises(ValueError):
            module.enrich(self.manifest, release, self.commit, lambda *args: None)
        def corrupt(*args):
            raise ValueError("Checksum mismatch")
        with self.assertRaises(ValueError):
            module.enrich(self.manifest, self.release, self.commit, corrupt)

    def test_publication_checks_all_sources_before_conditional_channel_updates(self):
        class Missing(Exception):
            response = {"Error": {"Code": "NoSuchKey"}}
        class Store:
            def __init__(self, data):
                self.data = {key: json.dumps(value).encode() for key, value in data.items()}
                self.writes = []
                self.conflict = False
            def get_paginator(self, _): return self
            def paginate(self, **_):
                return [{"Contents": [{"Key": key} for key in self.data if key.startswith("release/last.")]}]
            def get_object(self, Bucket, Key):
                if Key not in self.data: raise Missing()
                body = self.data[Key]
                return {"Body": io.BytesIO(body), "ETag": hashlib.sha256(body).hexdigest()}
            def put_object(self, Bucket, Key, Body, **options):
                if options.get("IfNoneMatch") == "*" and Key in self.data: raise ValueError("precondition")
                if "IfMatch" in options and (self.conflict or options["IfMatch"] != hashlib.sha256(self.data[Key]).hexdigest()):
                    raise ValueError("precondition")
                self.writes.append(Key)
                self.data[Key] = Body
        key = "release/last.version.json"
        store = Store({key: self.manifest,
                       # Skip legacy updater entries even if their filename matches an application asset.
                       "release/last.updater.Linux-amd64.version.json": dict(self.manifest, packageType="updater"),
                       "release/last.other.version.json": dict(self.manifest, sourceCommit="c" * 40)})
        original_enrich = module.enrich
        def verified(*args): return original_enrich(*args, verifier=lambda *download: None)
        with patch.object(module, "enrich", side_effect=verified):
            self.assertEqual(module.publish(store, "test", self.release, self.commit), [key])
            self.assertEqual(store.writes, ["release/package-sources/4.0.0-aaaaaaa/last.version.json", key])
            # A concurrent new channel value must not be overwritten.
            store.conflict = True
            with self.assertRaisesRegex(ValueError, "precondition"):
                module.publish(store, "test", self.release, self.commit)
        store = Store({key: self.manifest, "release/last.invalid.version.json": self.manifest})
        with patch.object(module, "enrich", side_effect=[verified(self.manifest, self.release, self.commit), ValueError("corrupt second source")]):
            with self.assertRaisesRegex(ValueError, "corrupt second source"):
                module.publish(store, "test", self.release, self.commit)
        self.assertEqual(store.writes, [])


if __name__ == "__main__":
    unittest.main()
