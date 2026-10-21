import { describe, expect, it } from "vitest";
import type { QrProfile } from "../data";
import { profilesHtml } from "./profilesHtml";

const sample: QrProfile = {
  id: "p1",
  name: "Site",
  payloadText: "https://example.com",
  tags: ["url"],
  styleJson: "{}",
  sensitive: false,
  updatedAt: 1,
};

describe("profilesHtml", () => {
  it("renders thumbnails, kind chip, and batch export control", () => {
    const html = profilesHtml([sample], "", "NEWEST", null, new Set());
    expect(html).toContain('data-thumb="p1"');
    expect(html).toContain("URL");
    expect(html).toContain('data-act="batch-pdf"');
    expect(html).toContain("disabled");
  });

  it("enables batch when checked", () => {
    const html = profilesHtml([sample], "", "NEWEST", "p1", new Set(["p1"]));
    expect(html).toContain("checked");
    expect(html).not.toMatch(/data-act="batch-pdf"[^>]*disabled/);
  });
});
