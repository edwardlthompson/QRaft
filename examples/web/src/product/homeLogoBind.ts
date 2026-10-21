import { t } from "../i18n";
import { fileToLogoDataUrl } from "../render/logoCache";
import type { HomeHooks } from "./homeBind";

export function bindLogoControls(root: HTMLElement, hooks: HomeHooks): void {
  const input = root.querySelector<HTMLInputElement>("[data-logo-import]");
  root.querySelector("[data-act=logo-pick]")?.addEventListener("click", () => input?.click());
  root.querySelector("[data-act=logo-clear]")?.addEventListener("click", () => {
    if (!window.confirm(t("style.logo_clear_confirm"))) return;
    const { style } = hooks.get();
    hooks.set({
      style: {
        ...style,
        logoImagePath: "",
        logoCutout: {
          ...style.logoCutout,
          enabled: style.centerMark !== "NONE",
        },
      },
    });
    hooks.persist();
    hooks.render();
  });
  input?.addEventListener("change", () => {
    const file = input.files?.[0];
    input.value = "";
    if (!file) return;
    void fileToLogoDataUrl(file)
      .then((dataUrl) => {
        const { style } = hooks.get();
        hooks.set({
          style: {
            ...style,
            logoImagePath: dataUrl,
            logoCutout: { ...style.logoCutout, enabled: true },
            centerMark: style.centerMark === "NONE" ? "CUTOUT" : style.centerMark,
          },
        });
        hooks.persist();
        hooks.render();
      })
      .catch(() => hooks.setStatus(t("style.logo_image_failed")));
  });
}
