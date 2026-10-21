import { qrContentRect } from "../core-qr/safeZone";
import type { QrMatrix } from "../core-qr/types";
import { renderMatrixToCanvas } from "./canvasRenderer";
import { argbToCss, type QrStyle } from "./style";

/** Blit a square QR into a wallpaper-sized canvas using safe-zone math. */
export function composeWallpaperPng(
  matrix: QrMatrix,
  style: QrStyle,
  width: number,
  height: number,
  marginFraction: number,
  payloadHint = "",
): HTMLCanvasElement {
  const canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext("2d");
  if (!ctx) return canvas;
  ctx.fillStyle = argbToCss(style.backgroundArgb);
  ctx.fillRect(0, 0, width, height);

  const rect = qrContentRect(width, height, marginFraction);
  const qrCanvas = renderMatrixToCanvas(
    matrix,
    { ...style, caption: { text: "" } },
    rect.width,
    undefined,
    payloadHint,
  );
  ctx.drawImage(qrCanvas, rect.left, rect.top, rect.width, rect.height);

  if (style.caption.text.trim()) {
    ctx.fillStyle = argbToCss(style.foregroundArgb);
    ctx.font = `${Math.max(14, Math.floor(height * 0.02))}px sans-serif`;
    ctx.textAlign = "center";
    ctx.fillText(style.caption.text, width / 2, Math.min(height - 24, rect.top + rect.height + 32));
  }
  return canvas;
}
