import { moduleAt, type QrMatrix } from "../core-qr/types";
import { resolveCenterMark } from "./centerMarkResolve";
import { argbToCss, type QrStyle } from "./style";

export function matrixToSvg(
  matrix: QrMatrix,
  style: QrStyle,
  viewBox = 256,
  payloadHint = "",
  logoDataUrl: string | null = null,
): string {
  const qz = Math.max(0, style.quietZoneModules);
  const modules = matrix.size + qz * 2;
  const cell = viewBox / modules;
  const fg = argbToCss(style.foregroundArgb);
  const bg = argbToCss(style.backgroundArgb);
  const parts: string[] = [
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${viewBox} ${viewBox}" width="${viewBox}" height="${viewBox}">`,
    `<rect width="100%" height="100%" fill="${bg}"/>`,
  ];
  for (let y = 0; y < matrix.size; y++) {
    for (let x = 0; x < matrix.size; x++) {
      if (!moduleAt(matrix, x, y)) continue;
      const px = (x + qz) * cell;
      const py = (y + qz) * cell;
      if (style.moduleShape === "DOT") {
        parts.push(
          `<circle cx="${px + cell / 2}" cy="${py + cell / 2}" r="${cell / 2 - cell * 0.08}" fill="${fg}"/>`,
        );
      } else if (style.moduleShape === "DIAMOND") {
        const cx = px + cell / 2;
        const cy = py + cell / 2;
        const h = cell / 2 - cell * 0.08;
        parts.push(
          `<polygon points="${cx},${cy - h} ${cx + h},${cy} ${cx},${cy + h} ${cx - h},${cy}" fill="${fg}"/>`,
        );
      } else {
        parts.push(`<rect x="${px}" y="${py}" width="${cell}" height="${cell}" fill="${fg}"/>`);
      }
    }
  }
  appendCenterPlate(parts, style, viewBox, payloadHint, logoDataUrl);
  if (style.caption.text.trim()) {
    parts.push(
      `<text x="${viewBox / 2}" y="${viewBox - 4}" text-anchor="middle" font-size="${Math.max(8, viewBox * 0.04)}" fill="${fg}">${escapeXml(style.caption.text)}</text>`,
    );
  }
  parts.push("</svg>");
  return parts.join("");
}

function appendCenterPlate(
  parts: string[],
  style: QrStyle,
  viewBox: number,
  payloadHint: string,
  logoDataUrl: string | null,
): void {
  const mark = resolveCenterMark(style.centerMark, payloadHint);
  const custom = style.logoImagePath.trim();
  if (!style.logoCutout.enabled && mark === "NONE" && !custom) return;
  const frac = Math.min(0.28, Math.max(0.1, style.logoCutout.diameterFraction || 0.2));
  const d = viewBox * frac;
  const cx = viewBox / 2;
  const cy = viewBox / 2;
  const r = d / 2;
  parts.push(`<circle cx="${cx}" cy="${cy}" r="${r}" fill="${argbToCss(0xffffffff)}"/>`);
  if (!logoDataUrl) return;
  const pad = d * 0.12;
  const inlay = d - pad * 2;
  parts.push(
    `<image href="${escapeXml(logoDataUrl)}" x="${cx - inlay / 2}" y="${cy - inlay / 2}" width="${inlay}" height="${inlay}" preserveAspectRatio="xMidYMid meet"/>`,
  );
}

function escapeXml(s: string): string {
  return s
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}
