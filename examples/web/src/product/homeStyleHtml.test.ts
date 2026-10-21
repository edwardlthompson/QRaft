import { describe, expect, it } from "vitest";
import { DEFAULT_STYLE } from "../render";
import { homeStyleHtml } from "./homeStyleHtml";

describe("homeStyleHtml", () => {
  it("exposes presets, quiet zone, and logo size", () => {
    const html = homeStyleHtml(DEFAULT_STYLE);
    expect(html).toContain("data-preset=");
    expect(html).toContain('data-field="quietZone"');
    expect(html).toContain('data-field="logoDiameter"');
  });
});
