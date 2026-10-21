import type { PayloadKind } from "../core-qr/payload";
import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { ColorTheme, ModuleShape, QrStyle } from "../render";
import { escapeAttr } from "./dom";

export function homeHtml(draft: EditorDraft, style: QrStyle, saveName: string): string {
  const kinds: PayloadKind[] = ["url", "text", "wifi", "vcard", "email", "sms", "phone", "crypto"];
  const themes: ColorTheme[] = ["CLASSIC", "OLED", "INVERT", "BLUE", "FOREST", "SUNSET"];
  const shapes: ModuleShape[] = ["SQUARE", "ROUNDED", "DOT", "DIAMOND", "PILL_H", "PILL_V", "BLOB"];
  return `
      <section class="qr-pane qr-pane-preview" data-preview aria-label="${t("editor.preview")}"></section>
      <section class="qr-pane qr-pane-controls">
        <label>${t("editor.kind")}
          <select data-field="kind">${kinds.map((k) => `<option value="${k}" ${draft.kind === k ? "selected" : ""}>${t(`kind.${k}`)}</option>`).join("")}</select>
        </label>
        <label>${t("editor.primary")}
          <input data-field="primary" value="${escapeAttr(draft.primary)}" />
        </label>
        <label>${t("editor.secondary")}
          <input data-field="secondary" value="${escapeAttr(draft.secondary)}" />
        </label>
        <label>${t("editor.tertiary")}
          <input data-field="tertiary" value="${escapeAttr(draft.tertiary)}" />
        </label>
        <label>${t("editor.save_name")}
          <input data-field="saveName" value="${escapeAttr(saveName)}" />
        </label>
        <label>${t("style.theme")}
          <select data-field="theme">${themes.map((th) => `<option value="${th}" ${style.colorTheme === th ? "selected" : ""}>${th}</option>`).join("")}</select>
        </label>
        <label>${t("style.module")}
          <select data-field="module">${shapes.map((s) => `<option value="${s}" ${style.moduleShape === s ? "selected" : ""}>${s}</option>`).join("")}</select>
        </label>
        <label>${t("style.caption")}
          <input data-field="caption" value="${escapeAttr(style.caption.text)}" />
        </label>
        <label class="qr-check"><input type="checkbox" data-field="oled" ${style.colorTheme === "OLED" ? "checked" : ""}/> ${t("style.oled")}</label>
        <div class="qr-actions">
          <button type="button" data-act="save">${t("editor.save")}</button>
          <button type="button" data-act="png">${t("editor.export_png")}</button>
          <button type="button" data-act="svg">${t("editor.export_svg")}</button>
          <button type="button" data-act="pdf">${t("editor.export_pdf")}</button>
          <button type="button" data-act="json">${t("editor.export_json")}</button>
          <button type="button" data-act="import">${t("editor.import_json")}</button>
          <button type="button" data-act="print">${t("editor.print")}</button>
          <button type="button" data-act="new">${t("editor.new")}</button>
        </div>
        <input type="file" accept="application/json,.json" data-import hidden />
        <p class="qr-hint">${t("tips.install")}</p>
      </section>
    `;
}
