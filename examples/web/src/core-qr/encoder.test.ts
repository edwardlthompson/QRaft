import { describe, expect, it } from "vitest";
import { chooseEcc } from "./eccPolicy";
import { encodePayload, encodeText } from "./encoder";
import { encodePayloadText } from "./payload";
import { qrContentRect, safeRectangle } from "./safeZone";

describe("encodePayloadText", () => {
  it("encodes wifi with hidden flag", () => {
    expect(
      encodePayloadText({
        kind: "wifi",
        ssid: "QRaft",
        password: "secret",
        security: "WPA",
        hidden: true,
      }),
    ).toBe("WIFI:T:WPA;S:QRaft;P:secret;H:true;;");
  });

  it("encodes vcard fields", () => {
    const t = encodePayloadText({
      kind: "vcard",
      fullName: "Ada Lovelace",
      phone: "+1",
      email: "a@b.c",
      org: "Analytical",
      url: "https://example.com",
    });
    expect(t).toContain("BEGIN:VCARD");
    expect(t).toContain("FN:Ada Lovelace");
    expect(t).toContain("ORG:Analytical");
  });
});

describe("encodeText", () => {
  it("produces a square matrix for a URL", () => {
    const m = encodeText("https://example.com", "M", { boostEcl: false });
    expect(m.size).toBeGreaterThanOrEqual(21);
    expect(m.modules.length).toBe(m.size * m.size);
    expect(m.errorCorrection).toBe("M");
  });

  it("forces high ECC for overlays", () => {
    const m = encodePayload({ kind: "url", url: "https://example.com" }, "M", true);
    expect(m.errorCorrection).toBe("H");
  });
});

describe("eccPolicy", () => {
  it("uses H for wallpaper and overlays", () => {
    expect(chooseEcc("WALLPAPER", false)).toBe("H");
    expect(chooseEcc("EDITOR", true)).toBe("H");
    expect(chooseEcc("EDITOR", false)).toBe("M");
  });
});

describe("safeZone", () => {
  it("matches Android fixture proportions", () => {
    const safe = safeRectangle(1000, 2000, 0.1);
    expect(safe.width).toBe(900);
    expect(safe.height).toBe(1800);
    const qr = qrContentRect(1080, 2400, 0.1);
    expect(qr.width).toBe(qr.height);
  });
});
