import { type ProfileSort, type QrProfile, searchProfiles, sortProfiles } from "../data";
import { t } from "../i18n";
import { escapeAttr, escapeHtml } from "./dom";

const KIND_TAGS = new Set(["url", "text", "wifi", "vcard", "email", "sms", "phone", "crypto"]);

function kindLabel(p: QrProfile): string {
  const tag = p.tags[0];
  return tag && KIND_TAGS.has(tag) ? t(`kind.${tag}`) : t("kind.text");
}

export function profilesHtml(
  profiles: QrProfile[],
  query: string,
  sort: ProfileSort,
  selectedId: string | null,
  checkedIds: ReadonlySet<string>,
): string {
  const list = sortProfiles(searchProfiles(profiles, query), sort);
  const selected = list.find((p) => p.id === selectedId) ?? null;
  const checkedCount = list.filter((p) => checkedIds.has(p.id)).length;
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
                `<li class="qr-profile-row">
                  <label class="qr-check"><input type="checkbox" data-batch="${p.id}" ${checkedIds.has(p.id) ? "checked" : ""} title="${t("profiles.batch_select")}" /></label>
                  <button type="button" data-profile="${p.id}" class="${p.id === selectedId ? "is-active" : ""}" title="${escapeAttr(p.name)}">
                    <span class="qr-profile-thumb" data-thumb="${p.id}"></span>
                    <span class="qr-profile-meta">
                      <span class="qr-profile-name">${escapeHtml(p.name)}</span>
                      <span class="qr-kind-chip">${escapeHtml(kindLabel(p))}</span>
                    </span>
                  </button>
                </li>`,
            )
            .join("")}
        </ul>
        <div class="qr-actions qr-batch-actions">
          <button type="button" class="qr-btn-secondary" data-act="batch-pdf" ${checkedCount === 0 ? "disabled" : ""} title="${t("profiles.batch_pdf")}">${t("profiles.batch_pdf")}${checkedCount ? ` (${checkedCount})` : ""}</button>
        </div>
      </section>
      <section class="qr-pane qr-pane-detail">
        ${
          selected
            ? `<h2>${escapeHtml(selected.name)}</h2>
               <p class="qr-mono">${escapeHtml(selected.payloadText.slice(0, 120))}</p>
               <div data-preview></div>
               <div class="qr-actions">
                 <button type="button" data-act="load" title="${t("profiles.load")}">${t("profiles.load")}</button>
                 <button type="button" class="qr-btn-secondary" data-act="delete" title="${t("profiles.delete")}">${t("profiles.delete")}</button>
               </div>`
            : `<p>${list.length === 0 ? t("profiles.empty") : t("profiles.select_hint")}</p>`
        }
      </section>
    `;
}
