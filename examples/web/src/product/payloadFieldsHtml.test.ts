import { describe, expect, it } from "vitest";
import { DEFAULT_DRAFT } from "../data/draftStore";
import { payloadFieldsHtml } from "./payloadFieldsHtml";

describe("payloadFieldsHtml", () => {
  it("shows URL label for url kind", () => {
    const html = payloadFieldsHtml({ ...DEFAULT_DRAFT, kind: "url" });
    expect(html).toContain("Website URL");
    expect(html).not.toContain('data-field="secondary"');
  });

  it("shows Wi-Fi security and hidden for wifi kind", () => {
    const html = payloadFieldsHtml({ ...DEFAULT_DRAFT, kind: "wifi", primary: "Home" });
    expect(html).toContain("Network name");
    expect(html).toContain('data-field="security"');
    expect(html).toContain('data-field="hidden"');
    expect(html).toContain("Hidden network");
  });

  it("shows org and website for vcard kind", () => {
    const html = payloadFieldsHtml({ ...DEFAULT_DRAFT, kind: "vcard", primary: "Ada" });
    expect(html).toContain('data-field="org"');
    expect(html).toContain('data-field="url"');
    expect(html).toContain("Organization");
  });

  it("shows crypto scheme select", () => {
    const html = payloadFieldsHtml({ ...DEFAULT_DRAFT, kind: "crypto", scheme: "bitcoin" });
    expect(html).toContain('data-field="scheme"');
    expect(html).toContain("Bitcoin");
    expect(html).toContain('value="bitcoin" selected');
  });

  it("uses textarea for text body", () => {
    const html = payloadFieldsHtml({ ...DEFAULT_DRAFT, kind: "text", primary: "hi <b>" });
    expect(html).toContain("<textarea");
    expect(html).toContain("hi &lt;b&gt;");
  });
});
