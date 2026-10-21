import {
  deleteProfile,
  type ProfileSort,
  type QrProfile,
  searchProfiles,
  sortProfiles,
} from "../data";
import { DEFAULT_DRAFT, type EditorDraft } from "../data/draftStore";
import { t } from "../i18n";
import type { GpRoute } from "../nav";
import type { QrStyle } from "../render";
import { decodeStyleJson } from "../render/styleJson";
import { escapeAttr, escapeHtml } from "./dom";

export function profilesHtml(
  profiles: QrProfile[],
  query: string,
  sort: ProfileSort,
  selectedId: string | null,
): string {
  const list = sortProfiles(searchProfiles(profiles, query), sort);
  const selected = list.find((p) => p.id === selectedId) ?? null;
  return `
      <section class="qr-pane qr-pane-list">
        <input data-field="query" placeholder="${t("profiles.search")}" value="${escapeAttr(query)}" />
        <select data-field="sort">
          <option value="NEWEST" ${sort === "NEWEST" ? "selected" : ""}>${t("profiles.sort_newest")}</option>
          <option value="NAME" ${sort === "NAME" ? "selected" : ""}>${t("profiles.sort_name")}</option>
        </select>
        <ul class="qr-profile-list">
          ${list
            .map(
              (p) =>
                `<li><button type="button" data-profile="${p.id}" class="${p.id === selectedId ? "is-active" : ""}" title="${escapeAttr(p.name)}">${escapeHtml(p.name)}</button></li>`,
            )
            .join("")}
        </ul>
      </section>
      <section class="qr-pane qr-pane-detail">
        ${
          selected
            ? `<h2>${escapeHtml(selected.name)}</h2>
               <p class="qr-mono">${escapeHtml(selected.payloadText.slice(0, 120))}</p>
               <div data-preview></div>
               <div class="qr-actions">
                 <button type="button" data-act="load" title="${t("profiles.load")}">${t("profiles.load")}</button>
                 <button type="button" data-act="delete" title="${t("profiles.delete")}">${t("profiles.delete")}</button>
               </div>`
            : `<p>${t("profiles.select_hint")}</p>`
        }
      </section>
    `;
}

export type ProfilesHooks = {
  getProfiles: () => QrProfile[];
  setProfiles: (p: QrProfile[]) => void;
  getQuery: () => string;
  setQuery: (q: string) => void;
  getSort: () => ProfileSort;
  setSort: (s: ProfileSort) => void;
  getSelectedId: () => string | null;
  setSelectedId: (id: string | null) => void;
  setDraftStyle: (draft: EditorDraft, style: QrStyle) => void;
  persist: () => void;
  onRoute: (route: GpRoute) => void;
  render: () => void;
};

export function bindProfiles(root: HTMLElement, hooks: ProfilesHooks): void {
  root.querySelector("[data-field=query]")?.addEventListener("input", (ev) => {
    hooks.setQuery((ev.target as HTMLInputElement).value);
    hooks.render();
  });
  root.querySelector("[data-field=sort]")?.addEventListener("change", (ev) => {
    hooks.setSort((ev.target as HTMLSelectElement).value as ProfileSort);
    hooks.render();
  });
  root.querySelectorAll("[data-profile]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = (btn as HTMLElement).dataset.profile ?? null;
      hooks.setSelectedId(id);
      const p = hooks.getProfiles().find((x) => x.id === id);
      if (p) {
        hooks.setDraftStyle(
          { ...DEFAULT_DRAFT, kind: "text", primary: p.payloadText },
          decodeStyleJson(p.styleJson),
        );
      }
      hooks.render();
    });
  });
  root.querySelector("[data-act=load]")?.addEventListener("click", () => {
    hooks.persist();
    hooks.onRoute("home");
  });
  root.querySelector("[data-act=delete]")?.addEventListener("click", () => {
    const id = hooks.getSelectedId();
    if (!id) return;
    hooks.setProfiles(deleteProfile(hooks.getProfiles(), id));
    hooks.setSelectedId(null);
    hooks.render();
  });
}
