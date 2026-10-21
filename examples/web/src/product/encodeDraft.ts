import { chooseEcc, draftToPayload, encodePayload, encodePayloadText } from "../core-qr";
import type { EditorDraft } from "../data/draftStore";
import { type QrStyle, styleHasOverlay } from "../render";

export function payloadTextOf(draft: EditorDraft): string {
  return encodePayloadText(
    draftToPayload(draft.kind, draft.primary, draft.secondary, draft.tertiary, {
      org: draft.org,
      url: draft.url,
      security: draft.security,
      hidden: draft.hidden,
      scheme: draft.scheme,
    }),
  );
}

export function encodeDraft(draft: EditorDraft, style: QrStyle) {
  const payload = draftToPayload(draft.kind, draft.primary, draft.secondary, draft.tertiary, {
    org: draft.org,
    url: draft.url,
    security: draft.security,
    hidden: draft.hidden,
    scheme: draft.scheme,
  });
  const overlay = styleHasOverlay(style);
  return encodePayload(payload, chooseEcc("EDITOR", overlay), overlay);
}
