#!/usr/bin/env bash
# Enforce file line limits: 300 for static data (UI + i18n), 150 for pure logic
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"
exec "$PY" "$ROOT/scripts/check-file-limits.py"
