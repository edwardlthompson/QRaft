import type { EditorDraft } from "./draftStore";
import { DEFAULT_DRAFT } from "./draftStore";

export const MAX_SHARE_CHARS = 4096;

/** Port of Android ShareIntake → EditorDraft. */
export function parseShare(
  text: string | null | undefined,
  mime = "text/plain",
): EditorDraft | null {
  const raw = (text ?? "").trim();
  if (!raw) return null;
  const clipped = raw.length > MAX_SHARE_CHARS ? raw.slice(0, MAX_SHARE_CHARS) : raw;
  const lower = mime.toLowerCase();
  if (lower.includes("vcard") || clipped.toUpperCase().startsWith("BEGIN:VCARD")) {
    return vcardDraft(clipped);
  }
  const line = clipped.split(/\r?\n/)[0] ?? "";
  if (/^https?:\/\//i.test(line)) return { ...DEFAULT_DRAFT, kind: "url", primary: line };
  if (/^mailto:/i.test(line)) {
    return {
      ...DEFAULT_DRAFT,
      kind: "email",
      primary: line.replace(/^mailto:/i, "").split("?")[0] ?? "",
    };
  }
  if (/^tel:/i.test(line)) {
    return { ...DEFAULT_DRAFT, kind: "phone", primary: line.replace(/^tel:/i, "") };
  }
  if (/^sms:/i.test(line)) {
    return {
      ...DEFAULT_DRAFT,
      kind: "sms",
      primary: line.replace(/^sms:/i, "").split("?")[0] ?? "",
    };
  }
  if (/^WIFI:/i.test(line)) return { ...DEFAULT_DRAFT, kind: "wifi", primary: clipped };
  if (/^geo:/i.test(line)) return { ...DEFAULT_DRAFT, kind: "text", primary: line };
  return { ...DEFAULT_DRAFT, kind: "text", primary: clipped };
}

function vcardDraft(body: string): EditorDraft {
  const field = (name: string): string => {
    const prefix = `${name}:`;
    const hit = body.split(/\r?\n/).find((l) => l.toUpperCase().startsWith(prefix.toUpperCase()));
    return hit ? hit.slice(prefix.length).trim() : "";
  };
  const name = field("FN") || field("N").replace(/;/g, " ").trim() || "Contact";
  return {
    ...DEFAULT_DRAFT,
    kind: "vcard",
    primary: name,
    secondary: field("TEL"),
    tertiary: field("EMAIL"),
    org: field("ORG"),
    url: field("URL"),
  };
}

export function shareTargetFromParams(params: URLSearchParams): EditorDraft | null {
  const url = params.get("url");
  const text = params.get("text");
  const title = params.get("title");
  return parseShare(url || text || title);
}
