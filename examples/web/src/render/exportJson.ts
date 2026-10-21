import { DEFAULT_STYLE, type QrStyle } from "./style";
import { decodeStyleJson, encodeStyleJson } from "./styleJson";

export type QrExportDocument = {
  version: number;
  payloadText: string;
  style: QrStyle;
};

export function encodeExportJson(doc: QrExportDocument): string {
  if (!doc.payloadText.trim()) throw new Error("payloadText must not be blank");
  return JSON.stringify({
    version: doc.version,
    payloadText: doc.payloadText,
    style: JSON.parse(encodeStyleJson(doc.style)),
  });
}

export function decodeExportJson(json: string): QrExportDocument | null {
  if (!json.trim()) return null;
  try {
    const obj = JSON.parse(json) as Record<string, unknown>;
    const payload = typeof obj.payloadText === "string" ? obj.payloadText : "";
    if (!payload.trim()) return null;
    const styleRaw = obj.style;
    const style =
      styleRaw && typeof styleRaw === "object"
        ? decodeStyleJson(JSON.stringify(styleRaw))
        : { ...DEFAULT_STYLE };
    return {
      version: typeof obj.version === "number" ? obj.version : 1,
      payloadText: payload,
      style,
    };
  } catch {
    return null;
  }
}
