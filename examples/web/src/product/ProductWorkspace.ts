import { loadDraft, loadProfiles, type ProfileSort, saveDraft } from "../data";
import type { EditorDraft } from "../data/draftStore";
import { shareTargetFromParams } from "../data/shareIntake";
import { t } from "../i18n";
import type { GpRoute } from "../nav";
import { composeWallpaperPng, type QrStyle, renderMatrixToCanvas } from "../render";
import { decodeStyleJson } from "../render/styleJson";
import { encodeDraft } from "./encodeDraft";
import { bindHome } from "./homeBind";
import { homeHtml } from "./homeHtml";
import { bindProfiles, profilesHtml } from "./profilesView";
import { bindWallpaper, wallpaperHtml } from "./wallpaperView";

export type ProductCallbacks = { onRoute: (route: GpRoute) => void };

export function mountProductWorkspace(
  host: HTMLElement,
  route: GpRoute,
  callbacks: ProductCallbacks,
): () => void {
  const persisted = loadDraft();
  let draft: EditorDraft = { ...persisted.draft };
  let style: QrStyle = decodeStyleJson(persisted.styleJson);
  let saveName = persisted.saveName;
  let profiles = loadProfiles();
  let query = "";
  let sort: ProfileSort = "NEWEST";
  let selectedId: string | null = null;
  let wallW = 1080;
  let wallH = 1920;
  let margin = 0.1;
  let status = "";

  const shared = shareTargetFromParams(new URLSearchParams(window.location.search));
  if (shared) {
    draft = shared;
    history.replaceState(null, "", window.location.pathname + window.location.hash);
  }

  const root = document.createElement("div");
  root.className = "qr-workspace";
  host.appendChild(root);

  const persist = () => saveDraft(draft, style, saveName);
  const setStatus = (msg: string) => {
    status = msg;
    render();
  };

  function refreshPreview(): void {
    const preview = root.querySelector<HTMLElement>("[data-preview]");
    if (!preview) return;
    preview.innerHTML = "";
    try {
      const matrix = encodeDraft(draft, style);
      const canvas =
        route === "wallpaper"
          ? composeWallpaperPng(matrix, style, wallW, wallH, margin)
          : renderMatrixToCanvas(matrix, style, 280);
      canvas.className = "qr-preview-canvas";
      canvas.setAttribute("data-testid", "qr-preview");
      preview.appendChild(canvas);
    } catch {
      preview.textContent = t("editor.encode_error");
    }
  }

  function render(): void {
    const view = route === "profiles" || route === "wallpaper" ? route : "home";
    root.innerHTML = `
      <nav class="qr-tabs" aria-label="${t("nav.primary")}">
        <button type="button" data-tab="home" class="${view === "home" ? "is-active" : ""}">${t("nav.home")}</button>
        <button type="button" data-tab="profiles" class="${view === "profiles" ? "is-active" : ""}">${t("nav.profiles")}</button>
        <button type="button" data-tab="wallpaper" class="${view === "wallpaper" ? "is-active" : ""}">${t("nav.wallpaper")}</button>
      </nav>
      <div class="qr-adaptive" data-view="${view}">
        ${view === "home" ? homeHtml(draft, style, saveName) : ""}
        ${view === "profiles" ? profilesHtml(profiles, query, sort, selectedId) : ""}
        ${view === "wallpaper" ? wallpaperHtml(wallW, wallH, margin) : ""}
      </div>
      ${status ? `<p class="qr-status" role="status">${status}</p>` : ""}
    `;
    root.querySelectorAll("[data-tab]").forEach((btn) => {
      btn.addEventListener("click", () =>
        callbacks.onRoute((btn as HTMLElement).dataset.tab as GpRoute),
      );
    });
    if (view === "home") {
      bindHome(root, {
        get: () => ({ draft, style, saveName, profiles }),
        set: (patch) => {
          if (patch.draft) draft = patch.draft;
          if (patch.style) style = patch.style;
          if (patch.saveName !== undefined) saveName = patch.saveName;
          if (patch.profiles) profiles = patch.profiles;
        },
        persist,
        setStatus,
        refreshPreview,
        render,
      });
    }
    if (view === "profiles") {
      bindProfiles(root, {
        getProfiles: () => profiles,
        setProfiles: (p) => {
          profiles = p;
        },
        getQuery: () => query,
        setQuery: (q) => {
          query = q;
        },
        getSort: () => sort,
        setSort: (s) => {
          sort = s;
        },
        getSelectedId: () => selectedId,
        setSelectedId: (id) => {
          selectedId = id;
        },
        setDraftStyle: (d, s) => {
          draft = d;
          style = s;
        },
        persist,
        onRoute: callbacks.onRoute,
        render,
      });
    }
    if (view === "wallpaper") {
      bindWallpaper(root, {
        getSize: () => ({ wallW, wallH, margin }),
        setSize: (w, h, m) => {
          wallW = w;
          wallH = h;
          margin = m;
        },
        getDraftStyle: () => ({ draft, style }),
        setStatus,
        refreshPreview,
        render,
      });
    }
    refreshPreview();
  }

  render();
  return () => root.remove();
}
