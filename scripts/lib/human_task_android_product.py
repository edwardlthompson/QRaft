"""QRaft product ADB checks: unit tests, instrumented smoke, install, launch."""
from __future__ import annotations

from pathlib import Path

from human_task_android_adb import (
    GRADLE_UNIT,
    adb_bin,
    gradle,
    pin_serial,
    run_gradle,
)
from human_task_core import AttemptResult, run_cmd

SMOKE_CLASS = "org.qraft.app.MainActivitySmokeTest"
LAUNCH = "org.qraft.app/.MainActivity"


def automate_adb_product_device(root: Path, _cfg: dict) -> AttemptResult:
    """Accept device leftovers via unit + smoke + install (peer camera proxied)."""
    if gradle(root) is None:
        return AttemptResult(1, "adb-product", "examples/android/gradlew missing", True)
    code, tail = run_gradle(root, *GRADLE_UNIT)
    if code != 0:
        return AttemptResult(1, "adb-product-unit", tail or f"unit exit {code}", True)
    serial = pin_serial(root)
    if not serial:
        return AttemptResult(1, "adb-unavailable", "no device after unit tests", True)
    full_ok = run_gradle(root, "connectedDebugAndroidTest", serial=serial)[0] == 0
    if not full_ok:
        smoke = (
            "connectedDebugAndroidTest",
            f"-Pandroid.testInstrumentationRunnerArguments.class={SMOKE_CLASS}",
        )
        code2, tail2 = run_gradle(root, *smoke, serial=serial)
        if code2 != 0:
            return AttemptResult(1, "connectedDebugAndroidTest", (tail2 or "")[-400:], True)
    code, tail = run_gradle(root, ":app:installDebug", serial=serial)
    if code != 0:
        return AttemptResult(1, "installDebug", tail or f"exit {code}", True)
    launch = run_cmd(root, [adb_bin(), "-s", serial, "shell", "am", "start", "-n", LAUNCH])
    if launch[0] != 0:
        return AttemptResult(1, "am-start", launch[1] or "am start failed", True)
    if full_ok:
        return AttemptResult(0, "adb-product-device", "unit + full instrumented + install + launch", False)
    return AttemptResult(
        0,
        "adb-product-smoke",
        "unit + MainActivitySmokeTest + install + launch "
        "(full UI on API 34 Monday cron; peer camera scan proxied)",
        False,
    )
