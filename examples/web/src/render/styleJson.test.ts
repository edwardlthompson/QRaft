import { describe, expect, it } from "vitest";
import { decodeExportJson, encodeExportJson } from "./exportJson";
import { DEFAULT_STYLE } from "./style";
import { decodeStyleJson, encodeStyleJson } from "./styleJson";

describe("QrStyleJson parity", () => {
  it("round-trips default style", () => {
    const json = encodeStyleJson(DEFAULT_STYLE);
    const back = decodeStyleJson(json);
    expect(back.moduleShape).toBe("SQUARE");
    expect(back.quietZoneModules).toBe(4);
    expect(back.colorTheme).toBe("CLASSIC");
  });

  it("decodes Android-shaped style object", () => {
    const android = {
      moduleShape: "DOT",
      finderShape: "CIRCLE",
      finderPupil: "RING",
      foregroundArgb: -16777216,
      eyeColorArgb: -16776961,
      backgroundArgb: -1,
      quietZoneModules: 6,
      logoEnabled: true,
      logoDiameterFraction: 0.18,
      colorTheme: "FOREST",
      gradientEnabled: false,
      gradientStartArgb: 0,
      gradientEndArgb: 0,
      centerMark: "LINK",
      caption: "Hi",
      imageBackgroundPath: "",
      frame: "THIN",
      cornerBadge: true,
    };
    const style = decodeStyleJson(JSON.stringify(android));
    expect(style.moduleShape).toBe("DOT");
    expect(style.logoCutout.enabled).toBe(true);
    expect(style.centerMark).toBe("LINK");
    expect(style.frame).toBe("THIN");
    expect(style.cornerBadge).toBe(true);
  });
});

describe("QrExportDocument", () => {
  it("round-trips export document", () => {
    const json = encodeExportJson({
      version: 1,
      payloadText: "https://example.com",
      style: DEFAULT_STYLE,
    });
    const doc = decodeExportJson(json);
    expect(doc?.payloadText).toBe("https://example.com");
    expect(doc?.style.moduleShape).toBe("SQUARE");
  });

  it("rejects blank payload", () => {
    expect(decodeExportJson('{"version":1,"payloadText":""}')).toBeNull();
  });
});
