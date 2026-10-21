import type { PayloadKind } from "../core-qr/payload";
import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { QrStyle } from "../render";
import { escapeAttr } from "./dom";
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
        </div>
        <div class="qr-actions qr-actions-primary">
          <button type="button" data-act="save" title="${t("editor.save")}">${t("editor.save")}</button>
          <div class="qr-export-wrap">
            <button type="button" data-act="export-toggle" title="${t("editor.export")}" aria-haspopup="menu" aria-expanded="false">${t("editor.export")}</button>
            <div class="qr-export-pop" data-export-pop hidden role="menu">
              <button type="button" role="menuitem" data-act="png">${t("editor.export_png")}</button>
              <button type="button" role="menuitem" data-act="svg">${t("editor.export_svg")}</button>
              <button type="button" role="menuitem" data-act="pdf">${t("editor.export_pdf")}</button>
            </div>
          </div>
          <button type="button" class="qr-btn-secondary" data-act="copy-png" title="${t("editor.copy_png")}">${t("editor.copy_png")}</button>
          <button type="button" class="qr-btn-secondary" data-act="share" hidden title="${t("editor.share")}">${t("editor.share")}</button>
          <button type="button" data-act="print" title="${t("editor.print")}">${t("editor.print")}</button>
          <button type="button" data-act="new" title="${t("editor.new")}">${t("editor.new")}</button>
        </div>
        <div class="qr-actions qr-actions-secondary">
          <button type="button" class="qr-btn-secondary" data-act="json" title="${t("editor.export_json")}">${t("editor.export_json")}</button>
          <button type="button" class="qr-btn-secondary" data-act="import" title="${t("editor.import_json")}">${t("editor.import_json")}</button>
        </div>
        <input type="file" accept="application/json,.json" data-import hidden />
        <p class="qr-hint">${t("tips.install")}</p>
      </section>
    `;
}
