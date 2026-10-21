import { assetUrl } from "../assetUrl";
import { centerMarkIconFile, resolveCenterMark } from "./centerMarkResolve";
import { getCachedLogo } from "./logoCache";
import { argbToCss, type QrStyle } from "./style";

/** Light center plate + custom logo or preset mark icon. */
export function drawCenterPlate(
  ctx: CanvasRenderingContext2D,
  style: QrStyle,
  side: number,
  payloadHint: string,
): void {
  const customLogo = style.logoImagePath.trim();
  const mark = resolveCenterMark(style.centerMark, payloadHint);
  if (!style.logoCutout.enabled && mark === "NONE" && !customLogo) return;
  const frac = Math.min(0.28, Math.max(0.1, style.logoCutout.diameterFraction || 0.2));
  const d = side * frac;
  const cx = side / 2;
  const cy = side / 2;
  // Opaque plate (Android CenterMarkIcons.WHITE) so the inlay stays scannable.
  ctx.fillStyle = argbToCss(0xffffffff);
  ctx.beginPath();
  ctx.arc(cx, cy, d / 2, 0, Math.PI * 2);
  ctx.fill();
  const pad = d * 0.12;
  const inlay = d - pad * 2;
  const file = centerMarkIconFile(mark);
  const logoSrc = customLogo || (file ? assetUrl(`center-icons/${file}`) : "");
  const img = logoSrc ? getCachedLogo(logoSrc) : undefined;
  if (img) ctx.drawImage(img, cx - inlay / 2, cy - inlay / 2, inlay, inlay);
}
