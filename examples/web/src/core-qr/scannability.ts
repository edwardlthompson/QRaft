import type { ErrorCorrectionLevel, QrMatrix } from "./types";

export const MIN_QUIET_ZONE_MODULES = 4;
export const MIN_CONTRAST_RATIO = 3.0;

export type ScannabilityWarning =
  | "LOW_CONTRAST"
  | "LOGO_TOO_LARGE"
  | "QUIET_ZONE_TOO_SMALL"
  | "OVERLAY_NEEDS_HIGH_ECC";

export type ScannabilityReport = { warnings: ScannabilityWarning[] };

function relativeLuminance(argb: number): number {
  const channel = (c: number): number => {
    const s = c / 255;
    return s <= 0.03928 ? s / 12.92 : ((s + 0.055) / 1.055) ** 2.4;
  };
  const r = channel((argb >>> 16) & 0xff);
  const g = channel((argb >>> 8) & 0xff);
  const b = channel(argb & 0xff);
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

export function contrastRatio(foregroundArgb: number, backgroundArgb: number): number {
  const l1 = relativeLuminance(foregroundArgb);
  const l2 = relativeLuminance(backgroundArgb);
  const lighter = Math.max(l1, l2);
  const darker = Math.min(l1, l2);
  return (lighter + 0.05) / (darker + 0.05);
}

export function maxLogoCoverage(ecc: ErrorCorrectionLevel): number {
  switch (ecc) {
    case "H":
      return 0.2;
    case "Q":
      return 0.12;
    case "M":
      return 0.08;
    case "L":
      return 0.05;
  }
}

export function analyzeScannability(opts: {
  matrix: QrMatrix;
  foregroundArgb?: number;
  backgroundArgb?: number;
  quietZoneModules?: number;
  logoCoverage?: number;
  hasOverlay?: boolean;
}): ScannabilityReport {
  const fg = opts.foregroundArgb ?? 0xff000000;
  const bg = opts.backgroundArgb ?? 0xffffffff;
  const qz = opts.quietZoneModules ?? MIN_QUIET_ZONE_MODULES;
  const logo = opts.logoCoverage ?? 0;
  const warnings: ScannabilityWarning[] = [];
  if (contrastRatio(fg, bg) < MIN_CONTRAST_RATIO) warnings.push("LOW_CONTRAST");
  if (qz < MIN_QUIET_ZONE_MODULES) warnings.push("QUIET_ZONE_TOO_SMALL");
  if (logo > maxLogoCoverage(opts.matrix.errorCorrection)) warnings.push("LOGO_TOO_LARGE");
  if (opts.hasOverlay && opts.matrix.errorCorrection !== "H")
    warnings.push("OVERLAY_NEEDS_HIGH_ECC");
  return { warnings };
}
