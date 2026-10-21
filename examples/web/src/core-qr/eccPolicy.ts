import type { ErrorCorrectionLevel, QrSurface } from "./types";

/** Mirrors Android EccPolicy. */
export function chooseEcc(surface: QrSurface, hasOverlay: boolean): ErrorCorrectionLevel {
  if (hasOverlay) return "H";
  if (surface === "WALLPAPER") return "H";
  return "M";
}

export function forceHighEcc(hasOverlay: boolean): boolean {
  return hasOverlay;
}
