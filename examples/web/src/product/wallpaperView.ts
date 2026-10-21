import type { EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import { canvasToPngBlob, composeWallpaperPng, type QrStyle } from "../render";
import { ensureStyleLogos } from "../render/ensureLogos";
import { downloadBlob } from "./dom";
import { encodeDraft, payloadTextOf } from "./encodeDraft";

export function wallpaperHtml(wallW: number, wallH: number, margin: number): string {
  return `
      <section class="qr-pane qr-pane-preview" data-preview aria-label="${t("wallpaper.preview")}"></section>
      <section class="qr-pane qr-pane-controls">
        <label>${t("wallpaper.width")} <input type="number" data-field="wallW" value="${wallW}" min="320" max="7680"/></label>
        <label>${t("wallpaper.height")} <input type="number" data-field="wallH" value="${wallH}" min="320" max="7680"/></label>
        <label>${t("wallpaper.margin")} <input type="number" data-field="margin" value="${margin}" min="0" max="0.2" step="0.01"/></label>
        <div class="qr-actions">
          <button type="button" data-act="preset-phone" title="${t("wallpaper.preset_phone")}">${t("wallpaper.preset_phone")}</button>
          <button type="button" data-act="preset-desktop" title="${t("wallpaper.preset_desktop")}">${t("wallpaper.preset_desktop")}</button>
          <button type="button" data-act="download" title="${t("wallpaper.download")}">${t("wallpaper.download")}</button>
        </div>
        <p class="qr-hint">${t("tips.wallpaper")}</p>
      </section>
    `;
}

export type WallpaperHooks = {
  getSize: () => { wallW: number; wallH: number; margin: number };
  setSize: (wallW: number, wallH: number, margin: number) => void;
  getDraftStyle: () => { draft: EditorDraft; style: QrStyle };
  setStatus: (msg: string) => void;
  refreshPreview: () => void;
  render: () => void;
};

export function bindWallpaper(root: HTMLElement, hooks: WallpaperHooks): void {
  root.querySelectorAll("[data-field]").forEach((el) => {
    el.addEventListener("change", (ev) => {
      const tEl = ev.target as HTMLInputElement;
      const cur = hooks.getSize();
      let { wallW, wallH, margin } = cur;
      if (tEl.dataset.field === "wallW") wallW = Number(tEl.value) || wallW;
      if (tEl.dataset.field === "wallH") wallH = Number(tEl.value) || wallH;
      if (tEl.dataset.field === "margin") margin = Number(tEl.value) || margin;
      hooks.setSize(wallW, wallH, margin);
      hooks.refreshPreview();
    });
  });
  root.querySelector("[data-act=preset-phone]")?.addEventListener("click", () => {
    hooks.setSize(1080, 1920, hooks.getSize().margin);
    hooks.render();
  });
  root.querySelector("[data-act=preset-desktop]")?.addEventListener("click", () => {
    hooks.setSize(1920, 1080, hooks.getSize().margin);
    hooks.render();
  });
  root.querySelector("[data-act=download]")?.addEventListener("click", async () => {
    try {
      const { draft, style } = hooks.getDraftStyle();
      const { wallW, wallH, margin } = hooks.getSize();
      const hint = payloadTextOf(draft);
      await ensureStyleLogos(style, hint);
      const canvas = composeWallpaperPng(
        encodeDraft(draft, style),
        style,
        wallW,
        wallH,
        margin,
        hint,
      );
      downloadBlob(await canvasToPngBlob(canvas), `qraft-wallpaper-${wallW}x${wallH}.png`);
      hooks.setStatus(t("wallpaper.downloaded"));
    } catch {
      hooks.setStatus(t("editor.encode_error"));
    }
  });
}
