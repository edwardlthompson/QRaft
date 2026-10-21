/** Mirrors Android `:core-qr` ErrorCorrectionLevel / QrMatrix. */

export type ErrorCorrectionLevel = "L" | "M" | "Q" | "H";

export type QrSurface = "EDITOR" | "WIDGET" | "WALLPAPER" | "EXPORT";

export type QrMatrix = {
  version: number;
  size: number;
  errorCorrection: ErrorCorrectionLevel;
  /** Row-major dark modules. */
  modules: boolean[];
};

export function moduleAt(matrix: QrMatrix, x: number, y: number): boolean {
  return matrix.modules[y * matrix.size + x] ?? false;
}
