import type { PayloadKind } from "../core-qr/payload";
import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { CenterMark, ModuleShape, QrStyle } from "../render";
import { EDITOR_COLOR_THEMES } from "../render/style";
import { escapeAttr } from "./dom";
import { payloadFieldsHtml } from "./payloadFieldsHtml";

const CENTER_MARKS: CenterMark[] = [
  "AUTO",
  "NONE",
  "CUTOUT",
  "LINK",
  "WIFI",
  "PERSON",
  "EMAIL",
  "PHONE",
  "SMS",
  "CRYPTO",
  "GITHUB",
  "MASTODON",
  "MATRIX",
  "GLOBE",
];

export function homeHtml(draft: EditorDraft, style: QrStyle, saveName: string): string {
  const kinds: PayloadKind[] = ["url", "text", "wifi", "vcard", "email", "sms", "phone", "crypto"];
  const shapes: ModuleShape[] = ["SQUARE", "ROUNDED", "DOT", "DIAMOND", "PILL_H", "PILL_V", "BLOB"];
  const hasLogo = style.logoImagePath.trim() !== "";
  return `
      <section class="qr-pane qr-pane-preview" data-preview aria-label="${t("editor.preview")}"></section>
      <section class="qr-pane qr-pane-controls">
        <label>${t("editor.kind")}
          <select data-field="kind" title="${t("editor.kind")}">${kinds.map((k) => `<option value="${k}" ${draft.kind === k ? "selected" : ""}>${t(`kind.${k}`)}</option>`).join("")}</select>
        </label>
        <div class="qr-payload-fields" data-payload-fields>
          ${payloadFieldsHtml(draft)}
        </div>
        <label>${t("editor.save_name")}
          <input data-field="saveName" value="${escapeAttr(saveName)}" />
        </label>
        <label>${t("style.theme")}
          <select data-field="theme">${EDITOR_COLOR_THEMES.map((th) => `<option value="${th}" ${style.colorTheme === th ? "selected" : ""}>${th}</option>`).join("")}</select>
        </label>
        <label>${t("style.module")}
          <select data-field="module">${shapes.map((s) => `<option value="${s}" ${style.moduleShape === s ? "selected" : ""}>${s}</option>`).join("")}</select>
        </label>
        <label>${t("style.center")}
          <select data-field="centerMark" title="${t("style.center")}">${CENTER_MARKS.map((m) => `<option value="${m}" ${style.centerMark === m ? "selected" : ""}>${t(`style.center.${m.toLowerCase()}`)}</option>`).join("")}</select>
        </label>
        <div class="qr-logo-actions">
          <button type="button" class="qr-btn-secondary" data-act="logo-pick" title="${t("style.logo_image")}">${t("style.logo_image")}</button>
          ${hasLogo ? `<button type="button" class="qr-btn-secondary" data-act="logo-clear" title="${t("style.logo_image_clear")}">${t("style.logo_image_clear")}</button>` : ""}
        </div>
        <input type="file" accept="image/*" data-logo-import hidden />
        <label>${t("style.caption")}
          <input data-field="caption" value="${escapeAttr(style.caption.text)}" />
        </label>
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
