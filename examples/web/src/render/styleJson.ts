import { DEFAULT_STYLE, type QrStyle } from "./style";

function enumOr<T extends string>(raw: unknown, allowed: readonly T[], fallback: T): T {
  return typeof raw === "string" && (allowed as readonly string[]).includes(raw)
    ? (raw as T)
    : fallback;
}

/** Round-trip compatible with Android QrStyleJson. */
export function encodeStyleJson(style: QrStyle): string {
  return JSON.stringify({
    moduleShape: style.moduleShape,
    finderShape: style.finderShape,
    finderPupil: style.finderPupil,
    foregroundArgb: style.foregroundArgb,
    eyeColorArgb: style.eyeColorArgb,
    backgroundArgb: style.backgroundArgb,
    quietZoneModules: style.quietZoneModules,
    logoEnabled: style.logoCutout.enabled,
    logoDiameterFraction: style.logoCutout.diameterFraction,
    colorTheme: style.colorTheme,
    gradientEnabled: style.gradient.enabled,
    gradientStartArgb: style.gradient.startArgb,
    gradientEndArgb: style.gradient.endArgb,
    centerMark: style.centerMark,
    caption: style.caption.text,
    imageBackgroundPath: style.imageBackgroundPath,
    frame: style.frame,
    cornerBadge: style.cornerBadge,
  });
}

export function decodeStyleJson(json: string): QrStyle {
  if (!json || json === "{}") return { ...DEFAULT_STYLE };
  try {
    const o = JSON.parse(json) as Record<string, unknown>;
    return {
      moduleShape: enumOr(
        o.moduleShape,
        ["SQUARE", "ROUNDED", "DOT", "DIAMOND", "PILL_H", "PILL_V", "BLOB"] as const,
        "SQUARE",
      ),
      finderShape: enumOr(
        o.finderShape,
        ["SQUARE", "ROUNDED", "CIRCLE", "HEX", "RING"] as const,
        "SQUARE",
      ),
      finderPupil: enumOr(
        o.finderPupil,
        ["SQUARE", "ROUNDED", "CIRCLE", "HEX", "RING"] as const,
        "SQUARE",
      ),
      foregroundArgb:
        typeof o.foregroundArgb === "number" ? o.foregroundArgb : DEFAULT_STYLE.foregroundArgb,
      eyeColorArgb:
        typeof o.eyeColorArgb === "number" ? o.eyeColorArgb : DEFAULT_STYLE.eyeColorArgb,
      backgroundArgb:
        typeof o.backgroundArgb === "number" ? o.backgroundArgb : DEFAULT_STYLE.backgroundArgb,
      quietZoneModules:
        typeof o.quietZoneModules === "number"
          ? o.quietZoneModules
          : DEFAULT_STYLE.quietZoneModules,
      logoCutout: {
        enabled: Boolean(o.logoEnabled),
        diameterFraction: typeof o.logoDiameterFraction === "number" ? o.logoDiameterFraction : 0.2,
      },
      colorTheme: enumOr(
        o.colorTheme,
        ["CLASSIC", "OLED", "INVERT", "BLUE", "FOREST", "SUNSET"] as const,
        "CLASSIC",
      ),
      gradient: {
        enabled: Boolean(o.gradientEnabled),
        startArgb:
          typeof o.gradientStartArgb === "number"
            ? o.gradientStartArgb
            : DEFAULT_STYLE.gradient.startArgb,
        endArgb:
          typeof o.gradientEndArgb === "number"
            ? o.gradientEndArgb
            : DEFAULT_STYLE.gradient.endArgb,
      },
      centerMark: enumOr(
        o.centerMark,
        [
          "NONE",
          "CUTOUT",
          "LINK",
          "WIFI",
          "PERSON",
          "EMAIL",
          "PHONE",
          "SMS",
          "CRYPTO",
          "GITHUB",
          "MASTODON",
          "MATRIX",
          "GLOBE",
          "AUTO",
        ] as const,
        "NONE",
      ),
      caption: { text: typeof o.caption === "string" ? o.caption : "" },
      imageBackgroundPath: typeof o.imageBackgroundPath === "string" ? o.imageBackgroundPath : "",
      frame: enumOr(o.frame, ["NONE", "THIN", "BOLD", "ROUNDED"] as const, "NONE"),
      cornerBadge: Boolean(o.cornerBadge),
    };
  } catch {
    return { ...DEFAULT_STYLE };
  }
}
