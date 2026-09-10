#!/usr/bin/env python3
"""Line-limit check that skips Gradle/npm caches (Git Bash find hangs on them)."""
from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
STATIC_LIMIT = 300
LOGIC_LIMIT = 150
SKIP_DIRS = {
    ".git",
    ".gradle",
    ".venv",
    "node_modules",
    "build",
    "dist",
    "target",
    ".cxx",
    "__pycache__",
}


def iter_files(root: Path):
    if not root.is_dir():
        return
    for child in root.iterdir():
        if child.is_dir():
            if child.name in SKIP_DIRS:
                continue
            yield from iter_files(child)
        elif child.is_file():
            yield child


def line_count(path: Path) -> int:
    return sum(1 for _ in path.open(encoding="utf-8", errors="replace"))


def is_ui_kt(path: Path) -> bool:
    rel = path.as_posix()
    if not rel.endswith(".kt"):
        return False
    if "/ui/GoldenPath" in rel:
        return True
    return path.parent.parent.name == "ui"


def is_static(path: Path) -> bool:
    name = path.name
    rel = path.as_posix()
    if name.endswith((".tsx", ".jsx", ".vue")) or "_view." in name:
        return True
    if path.suffix == ".ts" and "/examples/web/src/components/" in rel:
        return True
    if is_ui_kt(path):
        return True
    if path.suffix == ".json" and ("/locales/" in rel or "/src/locales/" in rel):
        return True
    if name == "strings.xml" and "/res/values" in rel:
        return True
    return False


def is_logic(path: Path) -> bool:
    if path.suffix not in {".ts", ".py", ".kt"}:
        return False
    if path.name.endswith((".test.ts", ".test.py", ".test.kt", ".spec.ts", ".spec.py")):
        return False
    rel = path.as_posix()
    if "/examples/web/src/components/" in rel:
        return False
    if "/examples/web/src/main.ts" in rel:
        return False
    if is_ui_kt(path):
        return False
    return "examples/" in rel.replace("\\", "/")


def main() -> int:
    errors = 0
    print(f"Checking static data file limits (max {STATIC_LIMIT} lines)...")
    examples = ROOT / "examples"
    for path in iter_files(examples):
        if path.name in {"package.json", "package-lock.json", "tsconfig.json", ".lighthouserc.json"}:
            continue
        if is_static(path) and line_count(path) > STATIC_LIMIT:
            print(f"FAIL [static-data] {path}: {line_count(path)} lines (max {STATIC_LIMIT})")
            errors += 1
    print(f"Checking scripts/lib logic file limits (max {LOGIC_LIMIT} lines)...")
    lib = ROOT / "scripts" / "lib"
    if lib.is_dir():
        for path in lib.glob("*.py"):
            n = line_count(path)
            if n > LOGIC_LIMIT:
                print(f"FAIL [logic] {path}: {n} lines (max {LOGIC_LIMIT})")
                errors += 1
    print(f"Checking pure logic file limits (max {LOGIC_LIMIT} lines)...")
    for path in iter_files(examples):
        if is_logic(path) and line_count(path) > LOGIC_LIMIT:
            print(f"FAIL [logic] {path}: {line_count(path)} lines (max {LOGIC_LIMIT})")
            errors += 1
    if errors:
        print(f"{errors} file(s) exceed line limits")
        return 1
    print("All file line limits OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
