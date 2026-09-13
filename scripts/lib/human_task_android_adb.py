"""Low-level adb/gradle helpers for Android HUMAN/ADB automation."""
from __future__ import annotations

import os
import shutil
import subprocess
from pathlib import Path

from human_task_core import run_cmd

GRADLE_UNIT = (
    ":app:testDebugUnitTest",
    ":wallpaper:test",
    ":widget:test",
    ":core-qr:test",
    ":render:test",
    ":data:test",
)


def adb_bin() -> str:
    adb = os.environ.get("ADB", "adb")
    if os.name == "nt" and not shutil.which(adb):
        win = os.environ.get("LOCALAPPDATA", "")
        if win:
            candidate = Path(win) / "Android/Sdk/platform-tools/adb.exe"
            if candidate.is_file():
                return str(candidate)
    return adb


def adb_device_serials(_root: Path | None = None) -> list[str]:
    try:
        out = subprocess.run(
            [adb_bin(), "devices"], capture_output=True, text=True, check=False
        )
    except FileNotFoundError:
        return []
    if out.returncode != 0:
        return []
    serials: list[str] = []
    for line in out.stdout.splitlines()[1:]:
        parts = line.split()
        if len(parts) >= 2 and parts[1] == "device":
            serials.append(parts[0])
    return serials


def adb_authorized(root: Path) -> bool:
    return bool(adb_device_serials(root))


def pin_serial(root: Path) -> str | None:
    pinned = os.environ.get("ANDROID_SERIAL", "").strip()
    serials = adb_device_serials(root)
    if pinned and pinned in serials:
        return pinned
    return serials[0] if serials else None


def gradle(root: Path) -> Path | None:
    path = root / "examples/android/gradlew"
    return path if path.is_file() else None


def run_gradle(root: Path, *tasks: str, serial: str | None = None) -> tuple[int, str]:
    g = gradle(root)
    if g is None:
        return 1, "examples/android/gradlew missing"
    if serial:
        os.environ["ANDROID_SERIAL"] = serial
    return run_cmd(
        root, ["bash", str(g), *tasks, "--no-daemon"], cwd=root / "examples/android"
    )
