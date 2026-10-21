import type { AppShellCallbacks, AppShellState } from "./AppShell";
import { APP_VERSION } from "./about/aboutSession";
import { createLaunchPromptDialog } from "./about/launchPrompt";
import { createAboutPanel } from "./components/AboutPanel";
import { createFeedbackPanel } from "./components/FeedbackPanel";
import { createSettingsPanel } from "./components/SettingsPanel";
import { applyPanelScroll, type GpRoute } from "./nav";
import { PRODUCT_ROUTES } from "./nav/types";
import { bindPanelDialog } from "./panelDialog";
import { mountProductWorkspace } from "./product/ProductWorkspace";
import { appendSettingsTips } from "./product/settingsTips";

export function mountShellRoute(
  root: HTMLElement,
  mount: Element,
  productMount: HTMLElement | null,
  route: GpRoute,
  state: AppShellState,
  callbacks: AppShellCallbacks,
  setDialogCleanup: (fn: (() => void) | undefined) => void,
  setProductCleanup: (fn: (() => void) | undefined) => void,
): void {
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
    setDialogCleanup(bindPanelDialog(panel, callbacks.onPop));
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
    setDialogCleanup(bindPanelDialog(panel, callbacks.onPop));
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
    setDialogCleanup(bindPanelDialog(mount.lastElementChild as HTMLElement, callbacks.onPop));
    applyPanelScroll(root, state.nav);
    return;
  }
  if (productMount && (PRODUCT_ROUTES as readonly string[]).includes(route)) {
    setProductCleanup(
      mountProductWorkspace(productMount, route, {
        onRoute: (r) => callbacks.onPushRoute(r),
      }),
    );
  }
}
