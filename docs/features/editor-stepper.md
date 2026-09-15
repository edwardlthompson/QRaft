# Feature: editor-stepper

> Guided Home create flow. Four steps (Content, Look, Place, Share) with Next/Previous and jump chips. Live preview stays. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Home is a stepper, not one long form. Compact 120dp preview on Home; tap toggles enlarge (clamped). Gallery and Wallpaper keep 240dp
- ✅ Next/Previous above the product tab bar; chips jump; system Back goes Previous except on Content
- ✅ Frequent types URL / Wi-Fi / Text / Contact; More auto-opens for other kinds. Look Advanced auto-opens for non-default advanced style
- ✅ Edit on Home, New, and Duplicate reset to Content
- ✅ Accessibility: step chips expose selected state; Next/Previous 48dp; passphrase-style secrets are not involved
- ✅ i18n: `editor_step_*` plus updated `tour_home` in en/es/de

## Smoke scenario

1. _Given_ Home shows Content and a compact preview
2. _When_ the user taps Next through Look to Place and saves
3. _Then_ the card is in Gallery; system Back from Look returns to Content; Gallery Edit on Home opens Content

## Container map

| Layer | Path |
|-------|------|
| Logic | `EditorSteps.kt` |
| View | `EditorScreen.kt`, step bodies, `StylePrimary` / `StyleAdvanced` |
| Tests | `EditorStepsTest` |
| Wiring | `ProductPages` holds `editorStep`; `MainActivity` unchanged |

## Tests

- Automated: yes — `EditorStepsTest` (next, previous, consumeBack, showMoreTypes, isAdvanced)
- Coverage: pure step machine; preview size default is the Gallery/Wallpaper path

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist). Device smoke is `[ADB]`.
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Public API (locked)

```kotlin
enum class EditorStep { Content, Look, Place, Share }
object EditorSteps {
  val FREQUENT: List<PayloadKind>
  fun next(step: EditorStep): EditorStep
  fun previous(step: EditorStep): EditorStep
  fun consumeBack(step: EditorStep): EditorStep?
  fun showMoreTypes(kind: PayloadKind): Boolean
  fun isAdvanced(style: QrStyle): Boolean
  const val PREVIEW_COMPACT_DP = 120f
  const val PREVIEW_FULL_DP = 240f
}
```

## Critique

| Issue | Resolution |
|-------|------------|
| Empty payload | Next allowed; Save uses `canSave` |
| Network timeout | N/A |
| Process death | `rememberSaveable` step; Gallery edit resets Content |
| Unhandled exceptions | Preview `runCatching` unchanged |
| Shared preview | `sizeDp` default full; Home passes compact |

## Notes

- Follow-up: persist last step in DataStore (safe to skip; Content is default).
- After AGENT: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`
