import { t } from "../i18n";

/** Append install / wallpaper / share tips to a settings panel element. */
export function appendSettingsTips(panel: HTMLElement): void {
  const tips = document.createElement("section");
  tips.className = "qr-tips";
  tips.innerHTML = `<h3>${t("tips.heading")}</h3><p>${t("tips.install")}</p><p>${t("tips.wallpaper")}</p><p>${t("tips.share")}</p>`;
  panel.appendChild(tips);
}
