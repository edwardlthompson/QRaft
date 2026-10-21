import { deleteProfile, type ProfileSort, type QrProfile } from "../data";
import { DEFAULT_DRAFT, type EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { GpRoute } from "../nav";
import { type QrStyle, renderMatrixToCanvas } from "../render";
import { canvasesToPdfBlob } from "../render/batchPdfExporter";
import { ensureStyleLogos } from "../render/ensureLogos";
import { decodeStyleJson } from "../render/styleJson";
import { downloadBlob } from "./dom";
import { encodeDraft } from "./encodeDraft";

export type ProfilesHooks = {
  getProfiles: () => QrProfile[];
  setProfiles: (p: QrProfile[]) => void;
  getQuery: () => string;
  setQuery: (q: string) => void;
  getSort: () => ProfileSort;
  setSort: (s: ProfileSort) => void;
  getSelectedId: () => string | null;
  setSelectedId: (id: string | null) => void;
  getCheckedIds: () => Set<string>;
  setCheckedIds: (ids: Set<string>) => void;
  setDraftStyle: (draft: EditorDraft, style: QrStyle) => void;
  persist: () => void;
  setStatus: (msg: string) => void;
  onRoute: (route: GpRoute) => void;
  render: () => void;
};

async function fillThumbs(root: HTMLElement, profiles: QrProfile[]): Promise<void> {
  const nodes = [...root.querySelectorAll<HTMLElement>("[data-thumb]")];
  for (const el of nodes) {
    const id = el.dataset.thumb;
    const p = profiles.find((x) => x.id === id);
    if (!p || el.querySelector("canvas")) continue;
    try {
      const style = decodeStyleJson(p.styleJson);
      const draft: EditorDraft = { ...DEFAULT_DRAFT, kind: "text", primary: p.payloadText };
      await ensureStyleLogos(style, p.payloadText);
      const canvas = renderMatrixToCanvas(
        encodeDraft(draft, style),
        style,
        96,
        undefined,
        p.payloadText,
      );
      el.replaceChildren(canvas);
    } catch {
      /* leave empty thumb */
    }
  }
}

export function bindProfiles(root: HTMLElement, hooks: ProfilesHooks): void {
  root.querySelector("[data-field=query]")?.addEventListener("input", (ev) => {
    hooks.setQuery((ev.target as HTMLInputElement).value);
    hooks.render();
  });
  root.querySelector("[data-field=sort]")?.addEventListener("change", (ev) => {
    hooks.setSort((ev.target as HTMLSelectElement).value as ProfileSort);
    hooks.render();
  });
  root.querySelectorAll("[data-batch]").forEach((box) => {
    box.addEventListener("change", (ev) => {
      const id = (box as HTMLElement).dataset.batch;
      if (!id) return;
      const next = new Set(hooks.getCheckedIds());
      if ((ev.target as HTMLInputElement).checked) next.add(id);
      else next.delete(id);
      hooks.setCheckedIds(next);
      hooks.render();
    });
  });
  root.querySelectorAll("[data-profile]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = (btn as HTMLElement).dataset.profile ?? null;
      hooks.setSelectedId(id);
      const p = hooks.getProfiles().find((x) => x.id === id);
      if (p) {
        hooks.setDraftStyle(
          { ...DEFAULT_DRAFT, kind: "text", primary: p.payloadText },
          decodeStyleJson(p.styleJson),
        );
      }
      hooks.render();
    });
  });
  root.querySelector("[data-act=load]")?.addEventListener("click", () => {
    hooks.persist();
    hooks.onRoute("home");
  });
  root.querySelector("[data-act=delete]")?.addEventListener("click", () => {
    const id = hooks.getSelectedId();
    if (!id) return;
    hooks.setProfiles(deleteProfile(hooks.getProfiles(), id));
    const checked = new Set(hooks.getCheckedIds());
    checked.delete(id);
    hooks.setCheckedIds(checked);
    hooks.setSelectedId(null);
    hooks.render();
  });
  root.querySelector("[data-act=batch-pdf]")?.addEventListener("click", () => {
    void (async () => {
      const ids = hooks.getCheckedIds();
      const selected = hooks.getProfiles().filter((p) => ids.has(p.id));
      if (selected.length === 0) return;
      try {
        const canvases: HTMLCanvasElement[] = [];
        for (const p of selected) {
          const style = decodeStyleJson(p.styleJson);
          const draft: EditorDraft = { ...DEFAULT_DRAFT, kind: "text", primary: p.payloadText };
          await ensureStyleLogos(style, p.payloadText);
          canvases.push(
            renderMatrixToCanvas(encodeDraft(draft, style), style, 512, undefined, p.payloadText),
          );
        }
        downloadBlob(await canvasesToPdfBlob(canvases), "qraft-batch.pdf");
        hooks.setStatus(t("profiles.batch_exported"));
      } catch {
        hooks.setStatus(t("editor.encode_error"));
      }
    })();
  });
  void fillThumbs(root, hooks.getProfiles());
}
