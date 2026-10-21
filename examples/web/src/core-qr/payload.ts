/** Typed payloads — same encodeText contracts as Android QrPayload. */

export type WifiSecurity = "NOPASS" | "WEP" | "WPA";

export type QrPayload =
  | { kind: "url"; url: string }
  | { kind: "text"; text: string }
  | { kind: "wifi"; ssid: string; password?: string; security?: WifiSecurity; hidden?: boolean }
  | { kind: "vcard"; fullName: string; phone?: string; email?: string; org?: string; url?: string }
  | { kind: "email"; address: string; subject?: string; body?: string }
  | { kind: "sms"; number: string; body?: string }
  | { kind: "phone"; number: string }
  | { kind: "crypto"; address: string; scheme?: string };

function escapeWifi(value: string): string {
  return value
    .replace(/\\/g, "\\\\")
    .replace(/;/g, "\\;")
    .replace(/,/g, "\\,")
    .replace(/:/g, "\\:")
    .replace(/"/g, '\\"');
}

function encodeQuery(value: string): string {
  return encodeURIComponent(value).replace(/%20/g, "+");
}

export function encodePayloadText(payload: QrPayload): string {
  switch (payload.kind) {
    case "url":
      return payload.url.trim();
    case "text":
      return payload.text;
    case "wifi": {
      const auth =
        payload.security === "NOPASS" ? "nopass" : payload.security === "WEP" ? "WEP" : "WPA";
      const hide = payload.hidden ? "H:true;" : "";
      return `WIFI:T:${auth};S:${escapeWifi(payload.ssid)};P:${escapeWifi(payload.password ?? "")};${hide};`;
    }
    case "vcard": {
      const lines = ["BEGIN:VCARD", "VERSION:3.0", `FN:${payload.fullName}`];
      if (payload.org?.trim()) lines.push(`ORG:${payload.org}`);
      if (payload.phone?.trim()) lines.push(`TEL:${payload.phone}`);
      if (payload.email?.trim()) lines.push(`EMAIL:${payload.email}`);
      if (payload.url?.trim()) lines.push(`URL:${payload.url}`);
      lines.push("END:VCARD");
      return lines.join("\n");
    }
    case "email": {
      const address = payload.address.trim();
      if (!payload.subject?.trim() && !payload.body?.trim()) return `mailto:${address}`;
      const q: string[] = [];
      if (payload.subject?.trim()) q.push(`subject=${encodeQuery(payload.subject)}`);
      if (payload.body?.trim()) q.push(`body=${encodeQuery(payload.body)}`);
      return `mailto:${address}?${q.join("&")}`;
    }
    case "sms":
      return payload.body?.trim()
        ? `sms:${payload.number.trim()}?body=${encodeQuery(payload.body)}`
        : `sms:${payload.number.trim()}`;
    case "phone":
      return `tel:${payload.number.trim()}`;
    case "crypto": {
      const a = payload.address.trim();
      return payload.scheme?.trim() ? `${payload.scheme.trim()}:${a}` : a;
    }
  }
}

export type PayloadKind = "url" | "text" | "wifi" | "vcard" | "email" | "sms" | "phone" | "crypto";

export const DEFAULT_PRIMARY: Record<PayloadKind, string> = {
  url: "https://example.com",
  text: "Hello from QRaft",
  wifi: "QRaft",
  vcard: "Ada Lovelace",
  email: "hello@example.com",
  sms: "+15555550100",
  phone: "+15555550100",
  crypto: "",
};

export function draftToPayload(
  kind: PayloadKind,
  primary: string,
  secondary = "",
  tertiary = "",
  extras: {
    org?: string;
    url?: string;
    security?: WifiSecurity;
    hidden?: boolean;
    scheme?: string;
  } = {},
): QrPayload {
  const p = primary.trim() || DEFAULT_PRIMARY[kind];
  switch (kind) {
    case "url":
      return { kind: "url", url: p };
    case "text":
      return { kind: "text", text: primary || DEFAULT_PRIMARY.text };
    case "wifi":
      return {
        kind: "wifi",
        ssid: p,
        password: secondary,
        security: extras.security ?? "WPA",
        hidden: extras.hidden ?? false,
      };
    case "vcard":
      return {
        kind: "vcard",
        fullName: p,
        phone: secondary,
        email: tertiary,
        org: extras.org ?? "",
        url: extras.url ?? "",
      };
    case "email":
      return { kind: "email", address: p, subject: secondary, body: tertiary };
    case "sms":
      return { kind: "sms", number: p, body: secondary };
    case "phone":
      return { kind: "phone", number: p };
    case "crypto":
      return { kind: "crypto", address: primary.trim(), scheme: extras.scheme ?? "" };
  }
}
