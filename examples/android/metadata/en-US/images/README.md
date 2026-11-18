# F-Droid / Fastlane images

Canonical store listing assets live at repository-root
`fastlane/metadata/android/en-US/images/` (what F-Droid scrapes).

| File | Size (typical) |
|------|----------------|
| `icon.png` | 512×512 |
| `featureGraphic.png` | 1024×500 |
| `phoneScreenshots/*.png` | 16:9 or device frames |

This nested `examples/android/metadata/` tree keeps the Triple-T text files
in sync. Phone screenshots are only under root Fastlane.

Dummy, placeholder, sample, or tiny (≤8px) PNGs fail `scripts/check-fdroid-screenshots.sh`.
