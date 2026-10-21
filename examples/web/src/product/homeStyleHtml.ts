import { t } from "../i18n";
import type { CenterMark, ModuleShape, QrStyle } from "../render";
import { EDITOR_COLOR_THEMES } from "../render/style";
import { STYLE_PRESETS } from "../render/stylePresets";
import { escapeAttr } from "./dom";

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

const SHAPES: ModuleShape[] = ["SQUARE", "ROUNDED", "DOT", "DIAMOND", "PILL_H", "PILL_V", "BLOB"];

/** Look / style controls — used in the Style column on expanded Home. */
export function homeStyleHtml(style: QrStyle): string {
  const hasLogo = style.logoImagePath.trim() !== "";
  const diameter = Math.round(style.logoCutout.diameterFraction * 100);
  return `
    <h2 class="qr-section-title">${t("editor.section.look")}</h2>
    <div class="qr-preset-strip" role="group" aria-label="${t("style.presets")}">
      ${STYLE_PRESETS.map(
        (p) =>
          `<button type="button" class="qr-preset-chip" data-preset="${p.id}" title="${t(p.labelKey)}">${t(p.labelKey)}</button>`,
      ).join("")}
    </div>
    <label>${t("style.theme")}
      <select data-field="theme">${EDITOR_COLOR_THEMES.map((th) => `<option value="${th}" ${style.colorTheme === th ? "selected" : ""}>${th}</option>`).join("")}</select>
    </label>
    <label>${t("style.module")}
      <select data-field="module">${SHAPES.map((s) => `<option value="${s}" ${style.moduleShape === s ? "selected" : ""}>${s}</option>`).join("")}</select>
    </label>
    <label>${t("style.center")}
      <select data-field="centerMark" title="${t("style.center")}">${CENTER_MARKS.map((m) => `<option value="${m}" ${style.centerMark === m ? "selected" : ""}>${t(`style.center.${m.toLowerCase()}`)}</option>`).join("")}</select>
    </label>
    <label>${t("style.quiet_zone")}
      <input type="number" data-field="quietZone" min="4" max="16" step="1" value="${style.quietZoneModules}" />
    </label>
    <label>${t("style.logo_size")}
      <input type="range" data-field="logoDiameter" min="10" max="28" step="1" value="${diameter}" />
      <span class="qr-hint" data-logo-size-label>${diameter}%</span>
    </label>
    <div class="qr-logo-actions">
      <button type="button" class="qr-btn-secondary" data-act="logo-pick" title="${t("style.logo_image")}">${t("style.logo_image")}</button>
      ${hasLogo ? `<button type="button" class="qr-btn-secondary" data-act="logo-clear" title="${t("style.logo_image_clear")}">${t("style.logo_image_clear")}</button>` : ""}
    </div>
    <input type="file" accept="image/*" data-logo-import hidden />
    <label>${t("style.caption")}
      <input data-field="caption" value="${escapeAttr(style.caption.text)}" />
    </label>
  `;
}
