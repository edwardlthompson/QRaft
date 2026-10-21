import { DEFAULT_PRIMARY, type PayloadKind, type WifiSecurity } from "../core-qr/payload";
import { DEFAULT_DRAFT } from "../data/draftStore";
import { applyColorTheme, type CenterMark, type ColorTheme, type ModuleShape } from "../render";
import type { HomeHooks } from "./homeBind";
import { isSuggestedProfileName, suggestedProfileName } from "./profileName";

export function onHomeField(ev: Event, hooks: HomeHooks): void {
  const el = ev.target as HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;
  const field = el.dataset.field;
  if (!field) return;
  const { draft, style, saveName } = hooks.get();
  if (field === "kind") {
    const kind = el.value as PayloadKind;
    const nextName = isSuggestedProfileName(saveName) ? suggestedProfileName(kind) : saveName;
    hooks.set({
      draft: {
        ...DEFAULT_DRAFT,
        kind,
        primary: DEFAULT_PRIMARY[kind],
        version: draft.version,
      },
      saveName: nextName,
    });
    hooks.persist();
    hooks.render();
    return;
  }
  if (field === "primary") hooks.set({ draft: { ...draft, primary: el.value } });
  else if (field === "secondary") hooks.set({ draft: { ...draft, secondary: el.value } });
  else if (field === "tertiary") hooks.set({ draft: { ...draft, tertiary: el.value } });
  else if (field === "org") hooks.set({ draft: { ...draft, org: el.value } });
  else if (field === "url") hooks.set({ draft: { ...draft, url: el.value } });
  else if (field === "security")
    hooks.set({ draft: { ...draft, security: el.value as WifiSecurity } });
  else if (field === "hidden")
    hooks.set({ draft: { ...draft, hidden: (el as HTMLInputElement).checked } });
  else if (field === "scheme") hooks.set({ draft: { ...draft, scheme: el.value } });
  else if (field === "saveName") hooks.set({ saveName: el.value });
  else if (field === "theme") hooks.set({ style: applyColorTheme(el.value as ColorTheme, style) });
  else if (field === "module")
    hooks.set({ style: { ...style, moduleShape: el.value as ModuleShape } });
  else if (field === "centerMark") {
    const mark = el.value as CenterMark;
    hooks.set({
      style: {
        ...style,
        centerMark: mark,
        logoCutout: {
          ...style.logoCutout,
          enabled: mark !== "NONE" || style.logoImagePath.trim() !== "",
        },
      },
    });
  } else if (field === "quietZone") {
    const n = Math.min(16, Math.max(4, Number(el.value) || 4));
    hooks.set({ style: { ...style, quietZoneModules: n } });
  } else if (field === "logoDiameter") {
    const pct = Math.min(28, Math.max(10, Number(el.value) || 20));
    const label = el.closest(".qr-controls-style")?.querySelector("[data-logo-size-label]");
    if (label) label.textContent = `${pct}%`;
    hooks.set({
      style: {
        ...style,
        logoCutout: { ...style.logoCutout, diameterFraction: pct / 100, enabled: true },
      },
    });
  } else if (field === "caption") hooks.set({ style: { ...style, caption: { text: el.value } } });
  hooks.persist();
  hooks.refreshPreview();
}
