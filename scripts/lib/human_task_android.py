"""ADB / Android HUMAN automation handlers."""
from __future__ import annotations

import os
from pathlib import Path

from human_task_android_adb import adb_authorized, gradle, pin_serial, run_gradle
from human_task_android_product import automate_adb_product_device
from human_task_core import AttemptResult, run_cmd

__all__ = [
    "adb_authorized",
    "automate_adb_instrumented",
    "automate_adb_product_device",
    "automate_android_sdk_smoke",
    "automate_fdroid_dry_run",
]


def automate_adb_instrumented(root: Path, _cfg: dict) -> AttemptResult:
    serial = pin_serial(root)
    if serial:
        code, tail = run_gradle(root, "connectedDebugAndroidTest", serial=serial)
        if code == 0:
            return AttemptResult(0, "connectedDebugAndroidTest", "passed", False)
        return AttemptResult(1, "connectedDebugAndroidTest", tail or f"exit {code}", True)
    g = gradle(root)
    if g is not None:
        run_cmd(root, ["bash", str(g), "test", "--no-daemon"], cwd=root / "examples/android")
    return AttemptResult(1, "adb-unavailable", "no_authorized_device", True)


def automate_fdroid_dry_run(root: Path, _cfg: dict) -> AttemptResult:
    script = root / "scripts/fdroid-device-dry-run.sh"
    if not script.is_file():
        return AttemptResult(1, "fdroid-dry-run", "fdroid-device-dry-run.sh missing", True)
    if not adb_authorized(root):
        return AttemptResult(1, "fdroid-dry-run", "no_authorized_device", True)
    code, tail = run_cmd(root, ["bash", str(script)])
    if code == 0:
        return AttemptResult(0, "fdroid-dry-run", "F-Droid device dry-run passed", False)
    return AttemptResult(1, "fdroid-dry-run", tail or f"exit {code}", True)


def automate_android_sdk_smoke(root: Path, _cfg: dict) -> AttemptResult:
    g = gradle(root)
    if g is not None:
        code, tail = run_cmd(root, ["bash", str(g), "test"], cwd=root / "examples/android")
        if code != 0:
            return AttemptResult(1, "gradle-test", tail or f"exit {code}", True)
    if adb_authorized(root):
        adb = os.environ.get("ADB", "adb")
        code, _ = run_cmd(root, [adb, "shell", "getprop", "ro.build.version.sdk"])
        if code == 0:
            return AttemptResult(0, "adb-getprop", "Gradle tests + adb getprop smoke", False)
    if g is not None:
        return AttemptResult(1, "adb-unavailable", "no_authorized_device after unit tests", True)
    return AttemptResult(1, "android-sdk", "No Android example tree", True)
