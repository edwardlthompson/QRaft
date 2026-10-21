import { applyColorTheme, DEFAULT_STYLE, type QrStyle } from "./style";

export type StylePreset = { id: string; labelKey: string; style: QrStyle };

/** Curated offline packs — mirrors Android StylePresets (OLED pack → Invert). */
export const STYLE_PRESETS: StylePreset[] = [
  { id: "classic", labelKey: "style.preset.classic", style: { ...DEFAULT_STYLE } },
  {
    id: "ink",
    labelKey: "style.preset.ink",
    style: {
      ...DEFAULT_STYLE,
      moduleShape: "ROUNDED",
      finderShape: "ROUNDED",
      finderPupil: "ROUNDED",
      foregroundArgb: 0xff111111,
      eyeColorArgb: 0xff111111,
      backgroundArgb: 0xfffafafa,
      frame: "THIN",
      colorTheme: "CLASSIC",
    },
  },
  {
    id: "ocean",
    labelKey: "style.preset.ocean",
    style: applyColorTheme("BLUE", {
      ...DEFAULT_STYLE,
      moduleShape: "DOT",
      finderShape: "CIRCLE",
    }),
  },
  {
    id: "forest",
    labelKey: "style.preset.forest",
    style: applyColorTheme("FOREST", {
      ...DEFAULT_STYLE,
      moduleShape: "ROUNDED",
      frame: "ROUNDED",
    }),
  },
  {
    id: "invert",
    labelKey: "style.preset.invert",
    style: applyColorTheme("INVERT", { ...DEFAULT_STYLE }),
  },
  {
    id: "sunset",
    labelKey: "style.preset.sunset",
    style: applyColorTheme("SUNSET", {
      ...DEFAULT_STYLE,
      moduleShape: "ROUNDED",
      finderShape: "ROUNDED",
    }),
  },
];

export function stylePresetById(id: string): StylePreset | undefined {
  return STYLE_PRESETS.find((p) => p.id === id);
}

/** Apply a preset while keeping caption and custom logo. */
export function applyStylePreset(id: string, current: QrStyle): QrStyle | null {
  const pack = stylePresetById(id);
  if (!pack) return null;
  const keepLogo = current.logoImagePath.trim() !== "";
  return {
    ...pack.style,
    caption: current.caption,
    logoImagePath: current.logoImagePath,
    logoCutout: {
      ...pack.style.logoCutout,
      diameterFraction: keepLogo
        ? current.logoCutout.diameterFraction
        : pack.style.logoCutout.diameterFraction,
      enabled: pack.style.logoCutout.enabled || keepLogo,
    },
  };
}
