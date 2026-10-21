import { loadDraft, loadProfiles, type ProfileSort, saveDraft } from "../data";
import type { EditorDraft } from "../data/draftStore";
import { shareTargetFromParams } from "../data/shareIntake";
import { t } from "../i18n";
import type { GpRoute } from "../nav";
import type { QrStyle } from "../render";
import { decodeStyleJson } from "../render/styleJson";
import { bindHome } from "./homeBind";
import { homeHtml } from "./homeHtml";
import { refreshProductPreview } from "./previewRefresh";
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
  let checkedIds = new Set<string>();
  let wallW = 3840;
  let wallH = 2160;
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
  const refreshPreview = () =>
    refreshProductPreview(root, route, () => ({ draft, style, wallW, wallH, margin }));

  function render(): void {
    const view = route === "profiles" || route === "wallpaper" ? route : "home";
    root.innerHTML = `
      <nav class="qr-tabs" aria-label="${t("nav.primary")}">
        <button type="button" data-tab="home" class="${view === "home" ? "is-active" : ""}" title="${t("nav.home")}">${t("nav.home")}</button>
        <button type="button" data-tab="profiles" class="${view === "profiles" ? "is-active" : ""}" title="${t("nav.profiles")}">${t("nav.profiles")}</button>
        <button type="button" data-tab="wallpaper" class="${view === "wallpaper" ? "is-active" : ""}" title="${t("nav.wallpaper")}">${t("nav.wallpaper")}</button>
      </nav>
      <div class="qr-adaptive" data-view="${view}">
        ${view === "home" ? homeHtml(draft, style, saveName) : ""}
        ${view === "profiles" ? profilesHtml(profiles, query, sort, selectedId, checkedIds) : ""}
        ${view === "wallpaper" ? wallpaperHtml(wallW, wallH, margin) : ""}
      </div>
      ${status ? `<p class="qr-status" role="status">${status}</p>` : ""}
    `;
    root.querySelectorAll("[data-tab]").forEach((btn) => {
      btn.addEventListener("click", () =>
        callbacks.onRoute((btn as HTMLElement).dataset.tab as GpRoute),
      );
    });
    bindView(view);
    refreshPreview();
  }

  function bindView(view: string): void {
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
        getCheckedIds: () => checkedIds,
        setCheckedIds: (ids) => {
          checkedIds = ids;
        },
        setDraftStyle: (d, s) => {
          draft = d;
          style = s;
        },
        persist,
        setStatus,
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
  }

  render();
  return () => root.remove();
}
