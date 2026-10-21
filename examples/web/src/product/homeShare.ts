import { t } from "../i18n";
import { canvasToPngBlob, renderMatrixToCanvas } from "../render";
import { ensureStyleLogos } from "../render/ensureLogos";
import { encodeDraft, payloadTextOf } from "./encodeDraft";
import type { HomeHooks } from "./homeBind";

/** Copy PNG / Web Share for the current draft. */
export function bindHomeShare(root: HTMLElement, hooks: HomeHooks): void {
  const shareBtn = root.querySelector<HTMLButtonElement>("[data-act=share]");
  if (shareBtn && typeof navigator.share === "function") {
    shareBtn.hidden = false;
  }

  async function makePng(): Promise<{ blob: Blob; canvas: HTMLCanvasElement }> {
    const { draft, style } = hooks.get();
    const hint = payloadTextOf(draft);
    await ensureStyleLogos(style, hint);
    const canvas = renderMatrixToCanvas(encodeDraft(draft, style), style, 1024, undefined, hint);
    return { blob: await canvasToPngBlob(canvas), canvas };
  }

  root.querySelector("[data-act=copy-png]")?.addEventListener("click", async () => {
    try {
      const { blob } = await makePng();
      if (!navigator.clipboard?.write) {
        hooks.setStatus(t("editor.copy_png_unsupported"));
        return;
      }
      await navigator.clipboard.write([new ClipboardItem({ "image/png": blob })]);
      hooks.setStatus(t("editor.copied_png"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });

  shareBtn?.addEventListener("click", async () => {
    try {
      const { blob } = await makePng();
      const file = new File([blob], "qraft.png", { type: "image/png" });
      if (!navigator.canShare?.({ files: [file] }) && !navigator.share) {
        hooks.setStatus(t("editor.share_unsupported"));
        return;
      }
      await navigator.share({
        files: [file],
        title: "QRaft",
        text: payloadTextOf(hooks.get().draft),
      });
      hooks.setStatus(t("editor.shared"));
    } catch (err) {
      if ((err as Error)?.name === "AbortError") return;
      hooks.setStatus(t("editor.encode_error"));
    }
  });
}
