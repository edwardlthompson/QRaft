import { assetUrl } from "./assetUrl";
import { t } from "./i18n";

export function shellHeaderHtml(canInstall: boolean | undefined): string {
  const logoSrc = assetUrl("logo.svg");
  const install = canInstall
    ? `<button type="button" class="gp-install-btn" data-install-open
            aria-label="${t("app.install")}" title="${t("app.install")}">⬇</button>`
    : "";
  return `
    <main class="qr-main">
      <div class="gp-header">
        <div class="gp-brand">
          <img class="gp-logo" src="${logoSrc}" width="40" height="40" alt="${t("app.logo.alt")}" />
          <h1 class="gp-title">${t("app.title")}</h1>
        </div>
        <div class="gp-header-actions">
          ${install}
          <button type="button" class="gp-settings-btn" data-settings-open
            aria-label="${t("settings.open")}" title="${t("settings.open")}">⚙</button>
          <button type="button" class="gp-about-btn" data-about-open
            aria-label="${t("about.open")}" title="${t("about.open")}">i</button>
        </div>
      </div>
      <div data-product-mount class="qr-product-mount"></div>
      <div data-panel-mount></div>
    </main>
  `;
}
