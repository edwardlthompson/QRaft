import { describe, expect, it } from "vitest";
import { isSuggestedProfileName, suggestedProfileName } from "./profileName";

describe("profileName", () => {
  it("suggests Website for url and kind labels otherwise", () => {
    expect(suggestedProfileName("url")).toBe("Website");
    expect(suggestedProfileName("wifi")).toBe("Wi-Fi");
    expect(suggestedProfileName("vcard")).toBe("vCard");
  });

  it("treats prior suggestions as replaceable", () => {
    expect(isSuggestedProfileName("")).toBe(true);
    expect(isSuggestedProfileName("Website")).toBe(true);
    expect(isSuggestedProfileName("Wi-Fi")).toBe(true);
    expect(isSuggestedProfileName("My custom name")).toBe(false);
  });
});
