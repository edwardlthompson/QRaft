import { assetUrl } from "../assetUrl";
import { centerMarkIconFile, resolveCenterMark } from "./centerMarkResolve";
import { getCachedLogo, loadLogoImage } from "./logoCache";
import type { QrStyle } from "./style";

/** Resolve the center inlay as a data URL for canvas/SVG exports. */
export async function logoDataUrlForStyle(
  style: QrStyle,
  payloadHint = "",
): Promise<string | null> {
  const custom = style.logoImagePath.trim();
  if (custom.startsWith("data:")) return custom;
  if (custom) {
    await loadLogoImage(custom).catch(() => undefined);
    return imageToDataUrl(getCachedLogo(custom));
  }
  const mark = resolveCenterMark(style.centerMark, payloadHint);
  const file = centerMarkIconFile(mark);
  if (!file) return null;
  const src = assetUrl(`center-icons/${file}`);
  await loadLogoImage(src).catch(() => undefined);
  return imageToDataUrl(getCachedLogo(src));
}

function imageToDataUrl(img: HTMLImageElement | undefined): string | null {
  if (!img?.complete || img.naturalWidth <= 0) return null;
  const c = document.createElement("canvas");
  c.width = img.naturalWidth;
  c.height = img.naturalHeight;
  const ctx = c.getContext("2d");
  if (!ctx) return null;
  ctx.drawImage(img, 0, 0);
  return c.toDataURL("image/png");
}
