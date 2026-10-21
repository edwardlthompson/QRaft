import { APP_VERSION } from "./about/aboutSession";
import { createLaunchPromptDialog } from "./about/launchPrompt";
import type { LaunchPrompt } from "./about/runAppUpdates";
import type { DonationConfig } from "./about/types";
import { assetUrl } from "./assetUrl";
import { createAboutPanel } from "./components/AboutPanel";
import { createFeedbackPanel } from "./components/FeedbackPanel";
import { createSettingsPanel } from "./components/SettingsPanel";
import { t } from "./i18n";
import { applyPanelScroll, current, type FeedbackKind, type GpRoute, type NavState } from "./nav";
import { PRODUCT_ROUTES } from "./nav/types";
import { bindPanelDialog } from "./panelDialog";
import { mountProductWorkspace } from "./product/ProductWorkspace";
import { appendSettingsTips } from "./product/settingsTips";

let dialogCleanup: (() => void) | undefined;
let productCleanup: (() => void) | undefined;

export type AppShellState = {
  nav: NavState;
  feedbackPrefill?: string;
  updateStatus: string;
  donations: DonationConfig;
  launchPrompt: LaunchPrompt | null;
  releaseRepo?: string;
};

export type AppShellCallbacks = {
  onState: (next: Partial<AppShellState>) => void;
  onPushRoute: (route: GpRoute, kind?: FeedbackKind) => void;
  onPop: () => void;
  onApplyUpdate?: () => void;
  onDonate?: () => void;
  onLaunchPrompt?: (accepted: boolean) => void;
  canApplyUpdate?: boolean;
};

export function createAppShell(
  root: HTMLElement,
  state: AppShellState,
  callbacks: AppShellCallbacks,
): void {
  const route = current(state.nav);

  productCleanup?.();
  productCleanup = undefined;
  dialogCleanup?.();
  dialogCleanup = undefined;

  const logoSrc = assetUrl("logo.svg");
  root.innerHTML = `
    <main class="qr-main">
      <div class="gp-header">
        <div class="gp-brand">
          <img class="gp-logo" src="${logoSrc}" width="40" height="40" alt="${t("app.logo.alt")}" />
          <h1 class="gp-title">${t("app.title")}</h1>
        </div>
        <div class="gp-header-actions">
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

  root
    .querySelector("[data-about-open]")
    ?.addEventListener("click", () => toggleOrPush(route, "about", callbacks));
  root
    .querySelector("[data-settings-open]")
    ?.addEventListener("click", () => toggleOrPush(route, "settings", callbacks));

  const productMount = root.querySelector<HTMLElement>("[data-product-mount]");
  const mount = root.querySelector("[data-panel-mount]");
  if (!mount) return;

  if (state.nav.promptOpen && state.launchPrompt) {
    mount.appendChild(
      createLaunchPromptDialog(state.launchPrompt, (accepted) =>
        callbacks.onLaunchPrompt?.(accepted),
      ),
    );
    return;
  }

  if (route === "feedback") {
    const panel = createFeedbackPanel(state.nav.feedbackKind ?? "bug", {
      onClose: callbacks.onPop,
      releaseRepo: state.releaseRepo ?? "",
      description: state.feedbackPrefill,
    });
    mount.appendChild(panel);
    dialogCleanup = bindPanelDialog(panel, callbacks.onPop);
    applyPanelScroll(root, state.nav);
    return;
  }

  if (route === "settings") {
    const panel = createSettingsPanel({
      onClose: callbacks.onPop,
      onOpenAbout: () => callbacks.onPushRoute("about"),
    });
    appendSettingsTips(panel);
    mount.appendChild(panel);
    dialogCleanup = bindPanelDialog(panel, callbacks.onPop);
    applyPanelScroll(root, state.nav);
    return;
  }

  if (route === "about") {
    mount.appendChild(
      createAboutPanel(
        {
          version: APP_VERSION,
          updateStatus: state.updateStatus,
          donations: state.donations,
          canApplyUpdate: callbacks.canApplyUpdate,
        },
        callbacks.onPop,
        callbacks.onApplyUpdate,
        () => callbacks.onPushRoute("feedback", "bug"),
        () => callbacks.onPushRoute("feedback", "feature"),
      ),
    );
    dialogCleanup = bindPanelDialog(mount.lastElementChild as HTMLElement, callbacks.onPop);
    applyPanelScroll(root, state.nav);
    return;
  }

  if (productMount && (PRODUCT_ROUTES as readonly string[]).includes(route)) {
    productCleanup = mountProductWorkspace(productMount, route, {
      onRoute: (r) => callbacks.onPushRoute(r),
    });
  }
}

function toggleOrPush(route: GpRoute, target: GpRoute, callbacks: AppShellCallbacks): void {
  if (route === target) callbacks.onPop();
  else callbacks.onPushRoute(target);
}
