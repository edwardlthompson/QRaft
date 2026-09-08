"""Fail when README hero/stack/owner badges drift from repo truth."""
from __future__ import annotations

import json
from pathlib import Path

OWNER_COLORS = {
    "AGENT": "2ea043",
    "HUMAN": "0969da",
    "ADB": "bf8700",
    "AUTO": "656d76",
}
STACK_COLORS = {
    "web": "646cff",
    "python": "3776AB",
    "android": "3DDC84",
    "node": "339933",
}


def _bootstrap(root: Path) -> dict:
    path = root / "bootstrap.config.json"
    if not path.is_file():
        return {}
    return json.loads(path.read_text(encoding="utf-8"))


def _product(root: Path) -> dict:
    path = root / "branding" / "product.json"
    if not path.is_file():
        return {}
    return json.loads(path.read_text(encoding="utf-8"))


def _active_stacks(root: Path, bootstrap: dict, product: dict) -> list[str]:
    stacks = product.get("stacks")
    if isinstance(stacks, list) and stacks:
        return [str(s) for s in stacks]
    stack = str(bootstrap.get("stack") or "multi")
    if stack == "multi":
        return list(STACK_COLORS)
    if stack in STACK_COLORS:
        return [stack]
    return []


def check_repo(root: Path) -> list[str]:
    readme = (root / "README.md").read_text(encoding="utf-8")
    bootstrap = _bootstrap(root)
    product = _product(root)
    product_mode = product.get("mode") == "product"
    license_id = str(bootstrap.get("license") or "MIT")
    errors: list[str] = []

    if not product_mode:
        version = (root / ".template-version").read_text(encoding="utf-8").strip()
        if f"badge/template-{version}" not in readme:
            errors.append(f"hero template badge must be template-{version}")

    if license_id == "Apache-2.0":
        if "badge/license-Apache--2.0" not in readme and "badge/license-Apache-2.0" not in readme:
            errors.append("hero license badge must be Apache-2.0")
    else:
        if "badge/license-MIT" not in readme:
            errors.append("hero license badge must be MIT")

    if "FOSS-no_tracking" not in readme and "FOSS-offline" not in readme:
        errors.append("hero FOSS badge must say no_tracking")
    if "actions/workflows/ci.yml" not in readme and "/ci.yml" not in readme:
        errors.append("CI badge must link ci.yml")

    for label, color in OWNER_COLORS.items():
        needle = f"badge/{label}-"
        if needle not in readme or color not in readme:
            # Product READMEs may omit owner legend; require only in template mode.
            if not product_mode:
                errors.append(f"owner badge {label} / {color} missing")

    for stack in _active_stacks(root, bootstrap, product):
        color = STACK_COLORS.get(stack)
        if not color:
            continue
        if f"badge/{stack}-stack-{color}" not in readme:
            # Allow product primary color override for the single active stack badge.
            if product_mode and f"badge/{stack}-stack-" in readme:
                continue
            errors.append(f"stack badge {stack} must use {color}")
    return errors


def main() -> int:
    errors = check_repo(Path.cwd())
    if errors:
        print("README badge accuracy check failed:")
        for item in errors:
            print(f"  {item}")
        return 1
    print("README badge accuracy check passed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
