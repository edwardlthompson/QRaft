"""Root Fastlane metadata is F-Droid-scrape ready."""
from __future__ import annotations

import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
FASTLANE = ROOT / "fastlane" / "metadata" / "android" / "en-US"
GRADLE = ROOT / "examples" / "android" / "app" / "build.gradle.kts"
STUB = "Golden Path FOSS Android stub"


class FdroidMetadataTests(unittest.TestCase):
    def test_root_fastlane_listing_files(self) -> None:
        for name in ("title.txt", "short_description.txt", "full_description.txt"):
            path = FASTLANE / name
            self.assertTrue(path.is_file(), path)
            self.assertTrue(path.read_text(encoding="utf-8").strip(), path)

    def test_short_description_is_not_placeholder(self) -> None:
        short = (FASTLANE / "short_description.txt").read_text(encoding="utf-8").strip()
        self.assertNotIn(STUB, short)
        self.assertLessEqual(len(short), 80)
        self.assertIn("QR", short)

    def test_changelog_matches_gradle_version_code(self) -> None:
        gradle = GRADLE.read_text(encoding="utf-8")
        code = None
        for line in gradle.splitlines():
            if "versionCode" in line and "=" in line:
                code = line.split("=")[1].strip().split()[0]
                break
        self.assertIsNotNone(code)
        changelog = FASTLANE / "changelogs" / f"{code}.txt"
        self.assertTrue(changelog.is_file(), changelog)
        self.assertTrue(changelog.read_text(encoding="utf-8").strip())

    def test_release_please_bumps_gradle_version_name(self) -> None:
        cfg = json.loads((ROOT / "release-please-config.json").read_text(encoding="utf-8"))
        extra = cfg["packages"]["."]["extra-files"]
        gradle_extra = "examples/android/app/build.gradle.kts"
        self.assertTrue(
            any(
                (isinstance(item, dict) and item.get("path") == gradle_extra)
                or item == gradle_extra
                for item in extra
            ),
        )

    def test_release_signing_survives_fdroid_strip(self) -> None:
        text = GRADLE.read_text(encoding="utf-8")
        self.assertIn('maybeCreate("release")', text)
        self.assertIn('findByName("release")', text)
        self.assertNotIn('getByName("release")', text)
        self.assertIn("x-release-please-version", text)

    def test_nested_fastlane_is_pointer(self) -> None:
        nested = ROOT / "examples" / "android" / "fastlane" / "README.md"
        self.assertTrue(nested.is_file())
        self.assertIn("fastlane/metadata/android/", nested.read_text(encoding="utf-8"))
        stub = (
            ROOT
            / "examples"
            / "android"
            / "fastlane"
            / "metadata"
            / "android"
            / "en-US"
            / "short_description.txt"
        )
        self.assertFalse(stub.exists(), "placeholder nested Fastlane listing must be gone")

    def test_icon_and_phone_screenshots(self) -> None:
        icon = FASTLANE / "images" / "icon.png"
        self.assertTrue(icon.is_file(), icon)
        shots = list((FASTLANE / "images" / "phoneScreenshots").glob("*.png"))
        self.assertGreaterEqual(len(shots), 1, "need real phoneScreenshots PNGs")


if __name__ == "__main__":
    unittest.main()
