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
import { logoDataUrlForStyle } from "../render/logoDataUrl";
import { downloadBlob } from "./dom";
import { encodeDraft, payloadTextOf } from "./encodeDraft";
import type { HomeHooks } from "./homeBind";

const exportAbort = new WeakMap<HTMLElement, AbortController>();

export function bindHomeExports(root: HTMLElement, hooks: HomeHooks): void {
  exportAbort.get(root)?.abort();
  const ac = new AbortController();
  exportAbort.set(root, ac);
  const { signal } = ac;

  const pop = root.querySelector<HTMLElement>("[data-export-pop]");
  const toggle = root.querySelector<HTMLButtonElement>("[data-act=export-toggle]");
  const wrap = root.querySelector<HTMLElement>(".qr-export-wrap");

  const setOpen = (open: boolean) => {
    if (!pop || !toggle) return;
    pop.hidden = !open;
    toggle.setAttribute("aria-expanded", open ? "true" : "false");
  };

  toggle?.addEventListener(
    "click",
    (ev) => {
      ev.stopPropagation();
      setOpen(!!pop?.hidden);
    },
    { signal },
  );

  document.addEventListener(
    "click",
    (ev) => {
      if (!wrap?.contains(ev.target as Node)) setOpen(false);
    },
    { signal },
  );
  document.addEventListener(
    "keydown",
    (ev) => {
      if (ev.key === "Escape") setOpen(false);
    },
    { signal },
  );

  const runExport = async (kind: "png" | "svg" | "pdf") => {
    setOpen(false);
    try {
      const { draft, style } = hooks.get();
      const hint = payloadTextOf(draft);
      await ensureStyleLogos(style, hint);
      const matrix = encodeDraft(draft, style);
      if (kind === "svg") {
        const logo = await logoDataUrlForStyle(style, hint);
        downloadBlob(
          new Blob([matrixToSvg(matrix, style, 512, hint, logo)], { type: "image/svg+xml" }),
          "qraft.svg",
        );
      } else {
        const size = kind === "png" ? 1024 : 512;
        const canvas = renderMatrixToCanvas(matrix, style, size, undefined, hint);
        const blob = kind === "png" ? await canvasToPngBlob(canvas) : await canvasToPdfBlob(canvas);
        downloadBlob(blob, kind === "png" ? "qraft.png" : "qraft.pdf");
      }
      hooks.setStatus(t("editor.exported"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  };

  root.querySelector("[data-act=png]")?.addEventListener("click", () => void runExport("png"));
  root.querySelector("[data-act=svg]")?.addEventListener("click", () => void runExport("svg"));
  root.querySelector("[data-act=pdf]")?.addEventListener("click", () => void runExport("pdf"));

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
