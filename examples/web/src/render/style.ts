import { MIN_QUIET_ZONE_MODULES } from "../core-qr/scannability";

export type ModuleShape = "SQUARE" | "ROUNDED" | "DOT" | "DIAMOND" | "PILL_H" | "PILL_V" | "BLOB";
export type FinderShape = "SQUARE" | "ROUNDED" | "CIRCLE" | "HEX" | "RING";
export type QrFrame = "NONE" | "THIN" | "BOLD" | "ROUNDED";
export type ColorTheme = "CLASSIC" | "OLED" | "INVERT" | "BLUE" | "FOREST" | "SUNSET";
export type CenterMark =
  | "NONE"
  | "CUTOUT"
  | "LINK"
  | "WIFI"
  | "PERSON"
  | "EMAIL"
  | "PHONE"
  | "SMS"
  | "CRYPTO"
  | "GITHUB"
  | "MASTODON"
  | "MATRIX"
  | "GLOBE"
  | "AUTO";

export type LogoCutout = { enabled: boolean; diameterFraction: number };
export type GradientSpec = { enabled: boolean; startArgb: number; endArgb: number };
export type CaptionSpec = { text: string };

export type QrStyle = {
  moduleShape: ModuleShape;
  finderShape: FinderShape;
  finderPupil: FinderShape;
  foregroundArgb: number;
  eyeColorArgb: number;
  backgroundArgb: number;
  quietZoneModules: number;
  logoCutout: LogoCutout;
  colorTheme: ColorTheme;
  gradient: GradientSpec;
  centerMark: CenterMark;
  caption: CaptionSpec;
  imageBackgroundPath: string;
  frame: QrFrame;
  cornerBadge: boolean;
};

export const DEFAULT_STYLE: QrStyle = {
  moduleShape: "SQUARE",
  finderShape: "SQUARE",
  finderPupil: "SQUARE",
  foregroundArgb: 0xff000000,
  eyeColorArgb: 0xff000000,
  backgroundArgb: 0xffffffff,
  quietZoneModules: MIN_QUIET_ZONE_MODULES,
  logoCutout: { enabled: false, diameterFraction: 0.2 },
  colorTheme: "CLASSIC",
  gradient: { enabled: false, startArgb: 0xff000000, endArgb: 0xff1565c0 },
  centerMark: "NONE",
  caption: { text: "" },
  imageBackgroundPath: "",
  frame: "NONE",
  cornerBadge: false,
};

export function styleHasOverlay(style: QrStyle): boolean {
  return style.logoCutout.enabled || style.centerMark !== "NONE" || style.cornerBadge;
}

const THEME_COLORS: Record<ColorTheme, [number, number, number]> = {
  CLASSIC: [0xff000000, 0xffffffff, 0xff000000],
  OLED: [0xffffffff, 0xff000000, 0xffffffff],
  INVERT: [0xffffffff, 0xff000000, 0xffffffff],
  BLUE: [0xff0d47a1, 0xffffffff, 0xff0d47a1],
  FOREST: [0xff1b5e20, 0xfff1f8e9, 0xff1b5e20],
  SUNSET: [0xffbf360c, 0xfffff3e0, 0xffe65100],
};

export function applyColorTheme(theme: ColorTheme, style: QrStyle): QrStyle {
  const [fg, bg, eye] = THEME_COLORS[theme];
  return { ...style, colorTheme: theme, foregroundArgb: fg, backgroundArgb: bg, eyeColorArgb: eye };
}

export function argbToCss(argb: number): string {
  const a = ((argb >>> 24) & 0xff) / 255;
  const r = (argb >>> 16) & 0xff;
  const g = (argb >>> 8) & 0xff;
  const b = argb & 0xff;
  return a >= 1 ? `rgb(${r},${g},${b})` : `rgba(${r},${g},${b},${a.toFixed(3)})`;
}
