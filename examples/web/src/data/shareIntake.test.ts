import { describe, expect, it } from "vitest";
import { parseShare } from "./shareIntake";

describe("parseShare", () => {
  it("maps https to url", () => {
    const d = parseShare("https://example.com");
    expect(d?.kind).toBe("url");
    expect(d?.primary).toBe("https://example.com");
  });

  it("maps vcard", () => {
    const d = parseShare("BEGIN:VCARD\nVERSION:3.0\nFN:Ada\nTEL:1\nEND:VCARD");
    expect(d?.kind).toBe("vcard");
    expect(d?.primary).toBe("Ada");
  });

  it("returns null for empty", () => {
    expect(parseShare("")).toBeNull();
    expect(parseShare(null)).toBeNull();
  });

  it("truncates oversized text", () => {
    const big = "x".repeat(5000);
    const d = parseShare(big);
    expect(d?.primary.length).toBe(4096);
  });
});
