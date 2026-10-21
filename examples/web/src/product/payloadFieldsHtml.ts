import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import { escapeAttr, escapeHtml } from "./dom";

function textField(field: string, label: string, value: string, multiline = false): string {
  if (multiline) {
    return `<label>${label}
      <textarea data-field="${field}" rows="3">${escapeHtml(value)}</textarea>
    </label>`;
  }
  return `<label>${label}
    <input data-field="${field}" value="${escapeAttr(value)}" />
  </label>`;
}

/** Kind-specific payload inputs — mirrors Android PayloadFields / PayloadExtraFields. */
export function payloadFieldsHtml(draft: EditorDraft): string {
  switch (draft.kind) {
    case "url":
      return textField("primary", t("home.url.label"), draft.primary);
    case "text":
      return textField("primary", t("editor.field.text"), draft.primary, true);
    case "wifi":
      return [
        textField("primary", t("editor.field.ssid"), draft.primary),
        textField("secondary", t("editor.field.password"), draft.secondary),
        `<label>${t("editor.field.wifi_security")}
          <select data-field="security">
            <option value="WPA" ${draft.security === "WPA" ? "selected" : ""}>WPA</option>
            <option value="WEP" ${draft.security === "WEP" ? "selected" : ""}>WEP</option>
            <option value="NOPASS" ${draft.security === "NOPASS" ? "selected" : ""}>NOPASS</option>
          </select>
        </label>`,
        `<label class="qr-check"><input type="checkbox" data-field="hidden" ${draft.hidden ? "checked" : ""}/> ${t("editor.wifi.hidden")}</label>`,
      ].join("");
    case "vcard":
      return [
        textField("primary", t("editor.field.name"), draft.primary),
        textField("secondary", t("editor.field.phone"), draft.secondary),
        textField("tertiary", t("editor.field.email"), draft.tertiary),
        textField("org", t("editor.field.org"), draft.org),
        textField("url", t("editor.field.url"), draft.url),
      ].join("");
    case "email":
      return [
        textField("primary", t("editor.field.email"), draft.primary),
        textField("secondary", t("editor.field.subject"), draft.secondary),
        textField("tertiary", t("editor.field.body"), draft.tertiary, true),
      ].join("");
    case "sms":
      return [
        textField("primary", t("editor.field.number"), draft.primary),
        textField("secondary", t("editor.field.body"), draft.secondary, true),
      ].join("");
    case "phone":
      return textField("primary", t("editor.field.number"), draft.primary);
    case "crypto":
      return [
        `<label>${t("editor.field.scheme")}
          <select data-field="scheme">
            <option value="" ${draft.scheme === "" ? "selected" : ""}>${t("crypto.scheme.none")}</option>
            <option value="bitcoin" ${draft.scheme === "bitcoin" ? "selected" : ""}>${t("crypto.scheme.bitcoin")}</option>
            <option value="ethereum" ${draft.scheme === "ethereum" ? "selected" : ""}>${t("crypto.scheme.ethereum")}</option>
            <option value="litecoin" ${draft.scheme === "litecoin" ? "selected" : ""}>${t("crypto.scheme.litecoin")}</option>
            <option value="dogecoin" ${draft.scheme === "dogecoin" ? "selected" : ""}>${t("crypto.scheme.dogecoin")}</option>
            <option value="monero" ${draft.scheme === "monero" ? "selected" : ""}>${t("crypto.scheme.monero")}</option>
          </select>
        </label>`,
        textField("primary", t("editor.field.address"), draft.primary),
      ].join("");
    default: {
      const _exhaustive: never = draft.kind;
      void _exhaustive;
      return textField("primary", t("editor.field.text"), draft.primary);
    }
  }
}
