import { DEFAULT_PRIMARY, type PayloadKind, type WifiSecurity } from "../core-qr/payload";
import { type QrProfile, upsertProfile } from "../data";
import { DEFAULT_DRAFT, type EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import {
  applyColorTheme,
  type ColorTheme,
  DEFAULT_STYLE,
  type ModuleShape,
  type QrStyle,
} from "../render";
import { encodeStyleJson } from "../render/styleJson";
import { payloadTextOf } from "./encodeDraft";
import { bindHomeExports } from "./homeExport";

export type HomeState = {
  draft: EditorDraft;
  style: QrStyle;
  saveName: string;
  profiles: QrProfile[];
};

export type HomeHooks = {
  get: () => HomeState;
  set: (patch: Partial<HomeState>) => void;
  persist: () => void;
  setStatus: (msg: string) => void;
  refreshPreview: () => void;
  render: () => void;
};

export function bindHome(root: HTMLElement, hooks: HomeHooks): void {
  root.querySelectorAll("[data-field]").forEach((el) => {
    el.addEventListener("change", (ev) => onField(ev, hooks));
    el.addEventListener("input", (ev) => onField(ev, hooks));
  });

  root.querySelector("[data-act=save]")?.addEventListener("click", () => {
    const { draft, style, saveName, profiles } = hooks.get();
    hooks.set({
      profiles: upsertProfile(profiles, {
        name: saveName || "Website",
        payloadText: payloadTextOf(draft),
        tags: [draft.kind],
        styleJson: encodeStyleJson(style),
        sensitive: draft.kind === "wifi",
      }),
    });
    hooks.persist();
    hooks.setStatus(t("editor.saved"));
  });

  bindHomeExports(root, hooks);

  root.querySelector("[data-act=print]")?.addEventListener("click", () => window.print());
  root.querySelector("[data-act=new]")?.addEventListener("click", () => {
    hooks.set({ draft: { ...DEFAULT_DRAFT }, style: { ...DEFAULT_STYLE }, saveName: "Website" });
    hooks.persist();
    hooks.render();
  });
}

function onField(ev: Event, hooks: HomeHooks): void {
  const el = ev.target as HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;
  const field = el.dataset.field;
  if (!field) return;
  const { draft, style } = hooks.get();
  if (field === "kind") {
    const kind = el.value as PayloadKind;
    hooks.set({
      draft: {
        ...DEFAULT_DRAFT,
        kind,
        primary: DEFAULT_PRIMARY[kind],
        version: draft.version,
      },
    });
    hooks.persist();
    hooks.render(); // rebuild kind-specific fields
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
  else if (field === "caption") hooks.set({ style: { ...style, caption: { text: el.value } } });
  else if (field === "oled") {
    hooks.set({
      style: applyColorTheme((el as HTMLInputElement).checked ? "OLED" : "CLASSIC", style),
    });
  }
  hooks.persist();
  hooks.refreshPreview();
}
