import { moduleAt, type QrMatrix } from "../core-qr/types";
import { drawCenterPlate } from "./centerPlate";
import { argbToCss, type QrStyle } from "./style";

/** Square raster of modules (+ quiet zone). Finder styling simplified to eye color. */
export function renderMatrixToCanvas(
  matrix: QrMatrix,
  style: QrStyle,
  pixelSize: number,
  canvas?: HTMLCanvasElement,
  payloadHint = "",
): HTMLCanvasElement {
  const qz = Math.max(0, style.quietZoneModules);
  const modules = matrix.size + qz * 2;
  const cell = Math.max(1, Math.floor(pixelSize / modules));
  const side = cell * modules;
  const el = canvas ?? document.createElement("canvas");
  el.width = side;
  el.height = side;
  const ctx = el.getContext("2d");
  if (!ctx) return el;

  ctx.fillStyle = argbToCss(style.backgroundArgb);
  ctx.fillRect(0, 0, side, side);

  if (style.gradient.enabled) {
    const g = ctx.createLinearGradient(0, 0, side, side);
    g.addColorStop(0, argbToCss(style.gradient.startArgb));
    g.addColorStop(1, argbToCss(style.gradient.endArgb));
    ctx.fillStyle = g;
  } else {
    ctx.fillStyle = argbToCss(style.foregroundArgb);
  }

  const inFinder = (x: number, y: number): boolean => {
    const n = matrix.size;
    const near = (ox: number, oy: number) => x >= ox && x < ox + 7 && y >= oy && y < oy + 7;
    return near(0, 0) || near(n - 7, 0) || near(0, n - 7);
  };

  for (let y = 0; y < matrix.size; y++) {
    for (let x = 0; x < matrix.size; x++) {
      if (!moduleAt(matrix, x, y)) continue;
      const px = (x + qz) * cell;
      const py = (y + qz) * cell;
      if (inFinder(x, y)) ctx.fillStyle = argbToCss(style.eyeColorArgb);
      else if (style.gradient.enabled) {
        /* keep gradient fill */
      } else ctx.fillStyle = argbToCss(style.foregroundArgb);

      drawModule(ctx, style, px, py, cell);
    }
  }

  drawCenterPlate(ctx, style, side, payloadHint);

  if (style.caption.text.trim()) {
    const band = Math.max(24, Math.floor(side * 0.08));
    const out = document.createElement("canvas");
    out.width = side;
    out.height = side + band;
    const octx = out.getContext("2d");
    if (octx) {
      octx.fillStyle = argbToCss(style.backgroundArgb);
      octx.fillRect(0, 0, out.width, out.height);
      octx.drawImage(el, 0, 0);
      octx.fillStyle = argbToCss(style.foregroundArgb);
      octx.font = `${Math.floor(band * 0.45)}px sans-serif`;
      octx.textAlign = "center";
      octx.textBaseline = "middle";
      octx.fillText(style.caption.text, side / 2, side + band / 2, side - 8);
      return out;
    }
  }
  return el;
}

function drawModule(
  ctx: CanvasRenderingContext2D,
  style: QrStyle,
  px: number,
  py: number,
  cell: number,
): void {
  const pad = cell * 0.08;
  switch (style.moduleShape) {
    case "DOT":
      ctx.beginPath();
      ctx.arc(px + cell / 2, py + cell / 2, cell / 2 - pad, 0, Math.PI * 2);
      ctx.fill();
      break;
    case "ROUNDED":
      roundRect(ctx, px + pad, py + pad, cell - pad * 2, cell - pad * 2, cell * 0.25);
      break;
    case "DIAMOND":
      ctx.beginPath();
      ctx.moveTo(px + cell / 2, py + pad);
      ctx.lineTo(px + cell - pad, py + cell / 2);
      ctx.lineTo(px + cell / 2, py + cell - pad);
      ctx.lineTo(px + pad, py + cell / 2);
      ctx.closePath();
      ctx.fill();
      break;
    default:
      ctx.fillRect(px, py, cell, cell);
  }
}

function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  w: number,
  h: number,
  r: number,
): void {
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.arcTo(x + w, y, x + w, y + h, r);
  ctx.arcTo(x + w, y + h, x, y + h, r);
  ctx.arcTo(x, y + h, x, y, r);
  ctx.arcTo(x, y, x + w, y, r);
  ctx.closePath();
  ctx.fill();
}

export function canvasToPngBlob(canvas: HTMLCanvasElement): Promise<Blob> {
  return new Promise((resolve, reject) => {
    canvas.toBlob((b) => (b ? resolve(b) : reject(new Error("PNG encode failed"))), "image/png");
  });
}
