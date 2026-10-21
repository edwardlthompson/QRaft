import { assetUrl } from "../assetUrl";
import { centerMarkIconFile, resolveCenterMark } from "./centerMarkResolve";
import { loadLogoImage } from "./logoCache";
import type { QrStyle } from "./style";

/** Preload custom logo and/or center-mark SVG so canvas can paint them. */
export async function ensureStyleLogos(style: QrStyle, payloadHint = ""): Promise<void> {
  const jobs: Promise<unknown>[] = [];
  const custom = style.logoImagePath.trim();
  if (custom) jobs.push(loadLogoImage(custom).catch(() => undefined));
  const mark = resolveCenterMark(style.centerMark, payloadHint);
  const file = centerMarkIconFile(mark);
  if (!custom && file) {
    jobs.push(loadLogoImage(assetUrl(`center-icons/${file}`)).catch(() => undefined));
  }
  if (jobs.length) await Promise.all(jobs);
}
