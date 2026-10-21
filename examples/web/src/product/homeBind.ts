import { type QrProfile, upsertProfile } from "../data";
import { DEFAULT_DRAFT, type EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import { DEFAULT_STYLE, type QrStyle } from "../render";
import { encodeStyleJson } from "../render/styleJson";
import { applyStylePreset } from "../render/stylePresets";
import { payloadTextOf } from "./encodeDraft";
import { bindHomeExports } from "./homeExport";
import { onHomeField } from "./homeFields";
import { bindLogoControls } from "./homeLogoBind";
import { suggestedProfileName } from "./profileName";

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
    el.addEventListener("change", (ev) => onHomeField(ev, hooks));
    el.addEventListener("input", (ev) => onHomeField(ev, hooks));
  });

  root.querySelector("[data-act=save]")?.addEventListener("click", () => {
    const { draft, style, saveName, profiles } = hooks.get();
    hooks.set({
      profiles: upsertProfile(profiles, {
        name: saveName || suggestedProfileName(draft.kind),
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
  bindLogoControls(root, hooks);

  root.querySelectorAll("[data-preset]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = (btn as HTMLElement).dataset.preset;
      if (!id) return;
      const { style } = hooks.get();
      const next = applyStylePreset(id, style);
      if (!next) return;
      hooks.set({ style: next });
      hooks.persist();
      hooks.render();
    });
  });

  root.querySelector("[data-act=print]")?.addEventListener("click", () => window.print());
  root.querySelector("[data-act=new]")?.addEventListener("click", () => {
    if (!window.confirm(t("editor.new_confirm"))) return;
    hooks.set({
      draft: { ...DEFAULT_DRAFT },
      style: { ...DEFAULT_STYLE },
      saveName: suggestedProfileName("url"),
    });
    hooks.persist();
    hooks.render();
  });
}
