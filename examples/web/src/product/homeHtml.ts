import type { PayloadKind } from "../core-qr/payload";
import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { QrStyle } from "../render";
import { escapeAttr } from "./dom";
import { homeShareHtml } from "./homeShareHtml";
import { homeStyleHtml } from "./homeStyleHtml";
import { payloadFieldsHtml } from "./payloadFieldsHtml";

export function homeHtml(draft: EditorDraft, style: QrStyle, saveName: string): string {
  const kinds: PayloadKind[] = ["url", "text", "wifi", "vcard", "email", "sms", "phone", "crypto"];
  return `
      <section class="qr-pane qr-pane-preview" data-preview aria-label="${t("editor.preview")}"></section>
      <section class="qr-pane qr-pane-controls">
        <div class="qr-controls-grid">
          <div class="qr-controls-col qr-controls-payload">
            <h3 class="qr-section-title">${t("editor.section.payload")}</h3>
            <label>${t("editor.kind")}
              <select data-field="kind" title="${t("editor.kind")}">${kinds.map((k) => `<option value="${k}" ${draft.kind === k ? "selected" : ""}>${t(`kind.${k}`)}</option>`).join("")}</select>
            </label>
            <p class="qr-hint qr-kind-hint">${t(`kind.hint.${draft.kind}`)}</p>
            <div class="qr-payload-fields" data-payload-fields>
              ${payloadFieldsHtml(draft)}
            </div>
            <label>${t("editor.save_name")}
              <input data-field="saveName" value="${escapeAttr(saveName)}" />
            </label>
          </div>
          <div class="qr-controls-col qr-controls-style">
            ${homeStyleHtml(style)}
          </div>
          <div class="qr-controls-col qr-controls-share">
            ${homeShareHtml()}
          </div>
        </div>
      </section>
    `;
}
