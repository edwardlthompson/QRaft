#!/usr/bin/env bash
# Fail if any tracked file exceeds size budget (matches pre-commit 500KB gate)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"

MAX_KB=500
export MAX_KB
"$PY" - <<'PY'
import os, subprocess, sys
from pathlib import Path

max_bytes = int(os.environ["MAX_KB"]) * 1024
files = subprocess.check_output(["git", "ls-files"], text=True).splitlines()
errors = 0
reported = 0
for rel in files:
    path = Path(rel)
    if not path.is_file():
        continue
    size = path.stat().st_size
    if size > max_bytes:
        print(f"LARGE TRACKED FILE: {rel} ({size // 1024} KB > {os.environ['MAX_KB']} KB)")
        errors += 1
        reported += 1
        if reported >= 20:
            print("... truncated (max 20)")
            break
if errors:
    print(f"{errors} tracked file(s) exceed {os.environ['MAX_KB']} KB")
    sys.exit(1)
print("Large tracked file check passed")
PY
