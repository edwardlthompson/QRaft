import { DEFAULT_DRAFT } from "../data/draftStore";
import { t } from "../i18n";
import {
  canvasToPdfBlob,
  canvasToPngBlob,
  decodeExportJson,
  encodeExportJson,
  matrixToSvg,
  renderMatrixToCanvas,
} from "../render";
import { ensureStyleLogos } from "../render/ensureLogos";
import { downloadBlob } from "./dom";
import { encodeDraft, payloadTextOf } from "./encodeDraft";
import type { HomeHooks } from "./homeBind";

export function bindHomeExports(root: HTMLElement, hooks: HomeHooks): void {
  root.querySelector("[data-act=png]")?.addEventListener("click", async () => {
    try {
      const { draft, style } = hooks.get();
      const hint = payloadTextOf(draft);
      await ensureStyleLogos(style, hint);
      const canvas = renderMatrixToCanvas(encodeDraft(draft, style), style, 1024, undefined, hint);
      downloadBlob(await canvasToPngBlob(canvas), "qraft.png");
      hooks.setStatus(t("editor.exported"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });

  root.querySelector("[data-act=svg]")?.addEventListener("click", () => {
    try {
      const { draft, style } = hooks.get();
      downloadBlob(
        new Blob([matrixToSvg(encodeDraft(draft, style), style, 512)], { type: "image/svg+xml" }),
        "qraft.svg",
      );
      hooks.setStatus(t("editor.exported"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });

  root.querySelector("[data-act=pdf]")?.addEventListener("click", async () => {
    try {
      const { draft, style } = hooks.get();
      const hint = payloadTextOf(draft);
      await ensureStyleLogos(style, hint);
      const canvas = renderMatrixToCanvas(encodeDraft(draft, style), style, 512, undefined, hint);
      downloadBlob(await canvasToPdfBlob(canvas), "qraft.pdf");
      hooks.setStatus(t("editor.exported"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });

  root.querySelector("[data-act=json]")?.addEventListener("click", async () => {
    try {
      const { draft, style } = hooks.get();
      await navigator.clipboard.writeText(
        encodeExportJson({ version: 1, payloadText: payloadTextOf(draft), style }),
      );
      hooks.setStatus(t("editor.copied_json"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });

  root.querySelector("[data-act=import]")?.addEventListener("click", () => {
    root.querySelector<HTMLInputElement>("[data-import]")?.click();
  });

  root.querySelector("[data-import]")?.addEventListener("change", async (ev) => {
    const file = (ev.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const doc = decodeExportJson(await file.text());
    if (!doc) {
      hooks.setStatus(t("editor.import_failed"));
      return;
    }
    hooks.set({
      draft: { ...DEFAULT_DRAFT, kind: "text", primary: doc.payloadText },
      style: doc.style,
    });
    hooks.persist();
    hooks.setStatus(t("editor.imported"));
  });
}
