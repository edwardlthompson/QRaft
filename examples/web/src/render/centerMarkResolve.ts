import type { CenterMark } from "./style";

/** Maps AUTO (and payload prefixes) to a drawable center mark — Android CenterMarkResolve. */
export function resolveCenterMark(mark: CenterMark, payload = ""): CenterMark {
  if (mark !== "AUTO") return mark;
  const t = payload.trim();
  if (t.toUpperCase().startsWith("WIFI:")) return "WIFI";
  if (t.toUpperCase().startsWith("BEGIN:VCARD")) return "PERSON";
  if (t.toUpperCase().startsWith("MECARD:")) return "PERSON";
  if (t.toLowerCase().startsWith("mailto:")) return "EMAIL";
  if (t.toLowerCase().startsWith("smsto:") || t.toLowerCase().startsWith("sms:")) return "SMS";
  if (t.toLowerCase().startsWith("tel:") || t.toLowerCase().startsWith("facetime")) return "PHONE";
  if (t.toLowerCase().startsWith("geo:")) return "GLOBE";
  if (t.toLowerCase().startsWith("bitcoin:") || t.toLowerCase().startsWith("ethereum:"))
    return "CRYPTO";
  if (t.toLowerCase().includes("github.com")) return "GITHUB";
  if (t.toLowerCase().includes("mastodon")) return "MASTODON";
  if (t.toLowerCase().startsWith("http://") || t.toLowerCase().startsWith("https://"))
    return "LINK";
  if (!t) return "NONE";
  return "GLOBE";
}

export function centerMarkIconFile(mark: CenterMark): string | null {
  switch (mark) {
    case "WIFI":
      return "wifi.svg";
    case "PERSON":
      return "person.svg";
    case "EMAIL":
      return "email.svg";
    case "PHONE":
      return "phone.svg";
    case "SMS":
      return "sms.svg";
    case "CRYPTO":
      return "crypto.svg";
    case "GITHUB":
      return "code.svg";
    case "MASTODON":
    case "MATRIX":
      return "share.svg";
    case "GLOBE":
      return "globe.svg";
    case "LINK":
      return "link.svg";
    default:
      return null;
  }
}
