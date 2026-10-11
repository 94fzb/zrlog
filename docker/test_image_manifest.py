import importlib.util
import unittest
from pathlib import Path

spec = importlib.util.spec_from_file_location("image_manifest", Path(__file__).with_name("write-image-manifest.py"))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class ImageManifestTest(unittest.TestCase):
    def setUp(self):
        self.native = dict(version="4.0.1", buildId="abcdef1", sourceCommit="abcdef1" + "0" * 33,
                           releaseDate="2026-10-11 13:00:00Z", type="release")
        self.hub = dict(repository="zrlog/zrlog", tag="4.0.1", digest="sha256:" + "1" * 64,
                        platforms=["linux/amd64", "linux/arm64"], version="4.0.1", sourceCommit=self.native["sourceCommit"])
        self.mirror = dict(self.hub, repository="registry.cn-chengdu.aliyuncs.com/hibegin/zrlog", digest="sha256:" + "2" * 64)

    def test_preserves_per_registry_digest_and_shared_build_identity(self):
        result = module.generate(self.native, [self.hub, self.mirror], "release")
        self.assertEqual(result["packageType"], "docker-image")
        self.assertEqual(result["buildId"], self.native["buildId"])
        self.assertEqual({image["repository"]: image["digest"] for image in result["images"]}, {
            "docker.io/zrlog/zrlog": self.hub["digest"], self.mirror["repository"]: self.mirror["digest"]})

    def test_does_not_publish_a_different_build_or_guessed_digest(self):
        for change in (dict(sourceCommit="b" * 40), dict(version="4.0.2"), dict(digest="latest"), dict(platforms=[])):
            with self.subTest(change=change), self.assertRaises(ValueError):
                module.generate(self.native, [dict(self.hub, **change)], "release")

    def test_preserves_dependency_preview_build_ids_and_timezone(self):
        native = dict(self.native, buildId="adminweb-1234567", releaseDate="2026-10-11 21:00:00+08:00")
        result = module.generate(native, [self.hub], "preview")
        self.assertEqual(result["buildId"], "adminweb-1234567")
        self.assertEqual(result["releaseDate"], native["releaseDate"])
        with self.assertRaises(ValueError):
            module.generate(native, [self.hub], "release")

    def test_aliases_and_default_arm64_variant_cannot_create_ambiguous_targets(self):
        with self.assertRaises(ValueError):
            module.generate(self.native, [self.hub, dict(self.hub, repository="docker.io/zrlog/zrlog")], "release")
        with self.assertRaises(ValueError):
            module.generate(self.native, [dict(self.hub, platforms=["linux/arm64", "linux/arm64/v8"])], "release")


class PublicationTest(unittest.TestCase):
    setUp = ImageManifestTest.setUp
    def test_immutable_metadata_precedes_channel_and_rejects_stale_publish(self):
        import io
        import hashlib
        publisher_spec = importlib.util.spec_from_file_location("publisher", Path(__file__).with_name("publish-image-manifest.py"))
        publisher = importlib.util.module_from_spec(publisher_spec)
        publisher_spec.loader.exec_module(publisher)
        class Missing(Exception):
            response = {"Error": {"Code": "NoSuchKey"}}
        class Store:
            data = {}
            writes = []
            def get_object(self, Bucket, Key):
                if Key not in self.data: raise Missing()
                body = self.data[Key]
                return {"Body": io.BytesIO(body), "ETag": hashlib.sha256(body).hexdigest()}
            def put_object(self, Bucket, Key, Body, **kwargs):
                if kwargs.get("IfNoneMatch") == "*" and Key in self.data: raise ValueError("precondition")
                if "IfMatch" in kwargs and kwargs["IfMatch"] != hashlib.sha256(self.data[Key]).hexdigest(): raise ValueError("precondition")
                self.data[Key] = Body
                self.writes.append(Key)
        store = Store()
        manifest = module.generate(self.native, [self.hub, self.mirror], "release")
        result = publisher.publish(store, "fixture", manifest)
        self.assertEqual(store.writes, [result["immutableKey"], result["channelKey"]])
        publisher.publish(store, "fixture", manifest)
        self.assertEqual(len(store.writes), 2)
        old = dict(manifest, releaseDate="2026-10-10 12:00:00Z")
        with self.assertRaises(ValueError): publisher.publish(store, "fixture", old)
        changed = module.generate(self.native, [dict(self.hub, digest="sha256:" + "3" * 64)], "release")
        with self.assertRaises(ValueError): publisher.publish(store, "fixture", changed)
        self.assertEqual(len(store.writes), 2)


if __name__ == "__main__":
    unittest.main()
