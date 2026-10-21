import type { PayloadKind, WifiSecurity } from "../core-qr/payload";
import { DEFAULT_STYLE, type QrStyle } from "../render/style";
import { decodeStyleJson, encodeStyleJson } from "../render/styleJson";

export type EditorDraft = {
  kind: PayloadKind;
  primary: string;
  secondary: string;
  tertiary: string;
  org: string;
  url: string;
  security: WifiSecurity;
  hidden: boolean;
  scheme: string;
  version: number;
};

export const DEFAULT_DRAFT: EditorDraft = {
  kind: "url",
  primary: "https://example.com",
  secondary: "",
  tertiary: "",
  org: "",
  url: "",
  security: "WPA",
  hidden: false,
  scheme: "",
  version: 1,
};

const KEY = "qraft-draft-v1";

export type PersistedDraft = {
  draft: EditorDraft;
  styleJson: string;
  saveName: string;
  updatedAt: number;
};

export function loadDraft(): PersistedDraft {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) {
      return {
        draft: { ...DEFAULT_DRAFT },
        styleJson: encodeStyleJson(DEFAULT_STYLE),
        saveName: "Website",
        updatedAt: Date.now(),
      };
    }
    return JSON.parse(raw) as PersistedDraft;
  } catch {
    return {
      draft: { ...DEFAULT_DRAFT },
      styleJson: encodeStyleJson(DEFAULT_STYLE),
      saveName: "Website",
      updatedAt: Date.now(),
    };
  }
}

export function saveDraft(draft: EditorDraft, style: QrStyle, saveName: string): void {
  const payload: PersistedDraft = {
    draft: { ...draft, version: draft.version + 1 },
    styleJson: encodeStyleJson(style),
    saveName,
    updatedAt: Date.now(),
  };
  localStorage.setItem(KEY, JSON.stringify(payload));
}

export function styleFromPersisted(p: PersistedDraft): QrStyle {
  return decodeStyleJson(p.styleJson);
}
