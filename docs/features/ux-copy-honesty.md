# Feature: ux-copy-honesty

> Tour tells the truth, About hides inset debug on release, clipboard snack is not “JSON”, Scan hint is once, ECC only on Look+overlay. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: tour does not say Next is above the tabs; release About has no inset line; copy snackbar is “Copied to clipboard”
- ✅ Scan hint appears once; empty payload copy is “Enter a URL or text first”
- ✅ Accessibility: About debug remains for debug builds (TalkBack can still hear inset when debugging)
- ✅ i18n: rewritten keys in en/es/de

## Smoke scenario

1. _Given_ a first-run install
2. _When_ the tour opens
3. _Then_ Home copy mentions Next or the step names, not “above the tabs”

## Container map

| Layer | Path |
|-------|------|
| Logic | `CopyHonesty.kt`, `EditorSteps.showEcc` |
| View | `AboutScreen.kt`, `ScanScreen.kt`, `EditorPreview.kt` |
| Tests | `CopyHonestyTest`, `EditorStepsTest` |
| Wiring | `BuildConfig.DEBUG` on About |

## Tests

- Automated: yes — `CopyHonestyTest`, `EditorSteps.showEcc`
- Coverage: About debug false on release; scan status hidden when equal to hint; ECC only Look+overlay

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty | `editor_payload_empty` still used; wording only |
| Network | N/A |
| Unhandled | N/A |
