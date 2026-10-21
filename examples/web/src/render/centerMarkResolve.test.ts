import { describe, expect, it } from "vitest";
import { centerMarkIconFile, resolveCenterMark } from "./centerMarkResolve";

describe("resolveCenterMark", () => {
  it("passes through non-AUTO marks", () => {
    expect(resolveCenterMark("WIFI")).toBe("WIFI");
  });

  it("maps AUTO from payload prefixes", () => {
    expect(resolveCenterMark("AUTO", "WIFI:S:x;;")).toBe("WIFI");
    expect(resolveCenterMark("AUTO", "https://example.com")).toBe("LINK");
    expect(resolveCenterMark("AUTO", "mailto:a@b.c")).toBe("EMAIL");
  });

  it("maps icon files for drawable marks", () => {
    expect(centerMarkIconFile("LINK")).toBe("link.svg");
    expect(centerMarkIconFile("GITHUB")).toBe("code.svg");
    expect(centerMarkIconFile("NONE")).toBeNull();
  });
});
