import { describe, expect, it } from "vitest";
import { DEFAULT_DRAFT } from "../data/draftStore";
import { DEFAULT_STYLE } from "../render";
import { homeHtml } from "./homeHtml";
import { homeShareHtml } from "./homeShareHtml";

describe("homeShareHtml", () => {
  it("exposes export and import actions", () => {
    const html = homeShareHtml();
    expect(html).toContain('data-act="save"');
    expect(html).toContain('data-act="export-toggle"');
    expect(html).toContain('data-act="import"');
  });
});

describe("homeHtml columns", () => {
  it("places share controls in a dedicated column", () => {
    const html = homeHtml(DEFAULT_DRAFT, DEFAULT_STYLE, "Website");
    expect(html).toContain("qr-controls-share");
    expect(html).toContain("qr-controls-payload");
    expect(html).toContain("qr-controls-style");
  });
});
