/** Port of Android WallpaperSafeZone — pure math, no DOM. */

export const DEFAULT_MARGIN_FRACTION = 0.1;
export const MAX_USER_MARGIN = 0.2;

export type RectPx = {
  left: number;
  top: number;
  width: number;
  height: number;
};

export function clampUserMargin(fraction: number): number {
  return Math.min(MAX_USER_MARGIN, Math.max(0, fraction));
}

export function safeRectangle(
  totalWidthPx: number,
  totalHeightPx: number,
  marginFraction = DEFAULT_MARGIN_FRACTION,
): RectPx {
  if (totalWidthPx <= 0 || totalHeightPx <= 0) throw new Error("dimensions must be positive");
  if (marginFraction < 0 || marginFraction > 0.4)
    throw new Error("marginFraction must be in 0..0.4");
  const widthSafe = Math.max(1, Math.floor(totalWidthPx * (1 - marginFraction)));
  const heightSafe = Math.max(1, Math.floor(totalHeightPx * (1 - marginFraction)));
  return {
    left: Math.floor((totalWidthPx - widthSafe) / 2),
    top: Math.floor((totalHeightPx - heightSafe) / 2),
    width: widthSafe,
    height: heightSafe,
  };
}

function inscribedSquare(rect: RectPx): RectPx {
  const side = Math.min(rect.width, rect.height);
  const cx = rect.left + Math.floor(rect.width / 2);
  const cy = rect.top + Math.floor(rect.height / 2);
  return {
    left: cx - Math.floor(side / 2),
    top: cy - Math.floor(side / 2),
    width: side,
    height: side,
  };
}

export function qrContentRect(
  totalWidthPx: number,
  totalHeightPx: number,
  marginFraction = DEFAULT_MARGIN_FRACTION,
): RectPx {
  return inscribedSquare(
    safeRectangle(totalWidthPx, totalHeightPx, clampUserMargin(marginFraction)),
  );
}
