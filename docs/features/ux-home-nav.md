# Feature: ux-home-nav

> One Home navigator. Audit kept chips + footer Next for that sequence; this row spends the cut. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Home has **either** step chips **or** Previous/Next in the footer, not both
- ✅ Footer still has one filled primary (Next / Save to gallery) + More
- ✅ Jumping steps remains possible (chips **or** a compact step label), so Look is not skippable only by accident
- ✅ Accessibility: remaining control is 48dp; step name is announced
- ✅ i18n: tour_home still matches the chrome that remains (en/es/de)

## Smoke scenario

1. _Given_ Home Content
2. _When_ the user goes to Look
3. _Then_ there is one obvious Next, not chips plus a second Next

## Container map

| Layer | Path |
|-------|------|
| Logic | `EditorSteps` next/previous unchanged |
| View | `EditorScreen.kt`, `EditorStepFooter.kt` |
| Tests | `EditorStepsTest`; chip row composed xor footer Previous |
| Wiring | none |

## Tests

- Automated: yes — only one of chip row / footer Previous is shown
- Coverage: `next(Look)=Place`; system Back still Previous

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Pick chips vs footer | Keep footer Next (one CTA) and drop chips; step name in the footer label |
| Look skippable | Next still lands on Look before Save |
| Network | N/A |
| Unhandled | `rememberSaveable` step enum unchanged |
