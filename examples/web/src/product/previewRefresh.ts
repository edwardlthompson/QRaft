import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { GpRoute } from "../nav";
import { composeWallpaperPng, type QrStyle, renderMatrixToCanvas } from "../render";
import { ensureStyleLogos } from "../render/ensureLogos";
import { encodeDraft, payloadTextOf } from "./encodeDraft";

export type PreviewState = {
  draft: EditorDraft;
  style: QrStyle;
  wallW: number;
  wallH: number;
  margin: number;
};

export function refreshProductPreview(
  root: HTMLElement,
  route: GpRoute,
  get: () => PreviewState,
): void {
  if (!root.querySelector("[data-preview]")) return;
  const { draft, style, wallW, wallH, margin } = get();
  const hint = payloadTextOf(draft);
  void ensureStyleLogos(style, hint).then(() => {
    if (!root.isConnected) return;
    const pane = root.querySelector<HTMLElement>("[data-preview]");
    if (!pane) return;
    pane.innerHTML = "";
    try {
      const matrix = encodeDraft(draft, style);
      const canvas =
        route === "wallpaper"
          ? composeWallpaperPng(matrix, style, wallW, wallH, margin, hint)
          : renderMatrixToCanvas(matrix, style, 280, undefined, hint);
      canvas.className = "qr-preview-canvas";
      canvas.setAttribute("data-testid", "qr-preview");
      pane.appendChild(canvas);
    } catch {
      pane.textContent = t("editor.encode_error");
    }
  });
}
