import { describe, expect, it } from "vitest";
import { DEFAULT_STYLE } from "./style";
import { applyStylePreset, stylePresetById } from "./stylePresets";

describe("stylePresets", () => {
  it("resolves curated packs", () => {
    expect(stylePresetById("classic")?.style.colorTheme).toBe("CLASSIC");
    expect(stylePresetById("forest")?.style.colorTheme).toBe("FOREST");
    expect(stylePresetById("missing")).toBeUndefined();
  });

  it("keeps caption and custom logo when applying", () => {
    const current = {
      ...DEFAULT_STYLE,
      caption: { text: "Hello" },
      logoImagePath: "data:image/png;base64,xx",
      logoCutout: { enabled: true, diameterFraction: 0.25 },
    };
    const next = applyStylePreset("ocean", current);
    expect(next?.caption.text).toBe("Hello");
    expect(next?.logoImagePath).toBe(current.logoImagePath);
    expect(next?.logoCutout.diameterFraction).toBe(0.25);
    expect(next?.logoCutout.enabled).toBe(true);
    expect(next?.colorTheme).toBe("BLUE");
  });
});
