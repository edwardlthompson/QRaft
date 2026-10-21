import { t } from "../i18n";

/** Share / export / import actions — fourth column on expanded Home. */
export function homeShareHtml(): string {
  return `
    <h3 class="qr-section-title">${t("editor.section.share")}</h3>
    <div class="qr-actions qr-actions-primary qr-actions-stack">
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
    <div class="qr-actions qr-actions-secondary qr-actions-stack">
      <button type="button" class="qr-btn-secondary" data-act="json" title="${t("editor.export_json")}">${t("editor.export_json")}</button>
      <button type="button" class="qr-btn-secondary" data-act="import" title="${t("editor.import_json")}">${t("editor.import_json")}</button>
    </div>
    <input type="file" accept="application/json,.json" data-import hidden />
    <p class="qr-hint">${t("tips.install")}</p>
  `;
}
