import type { LaunchPrompt } from "./about/runAppUpdates";
import type { DonationConfig } from "./about/types";
import { current, type FeedbackKind, type GpRoute, type NavState } from "./nav";
import { shellHeaderHtml } from "./shellHeader";
import { mountShellRoute } from "./shellPanels";

let dialogCleanup: (() => void) | undefined;
let productCleanup: (() => void) | undefined;

export type AppShellState = {
  nav: NavState;
  feedbackPrefill?: string;
  updateStatus: string;
  donations: DonationConfig;
  launchPrompt: LaunchPrompt | null;
  releaseRepo?: string;
  canInstall?: boolean;
};

export type AppShellCallbacks = {
  onState: (next: Partial<AppShellState>) => void;
  onPushRoute: (route: GpRoute, kind?: FeedbackKind) => void;
  onPop: () => void;
  onApplyUpdate?: () => void;
  onDonate?: () => void;
  onInstall?: () => void;
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

  root.innerHTML = shellHeaderHtml(state.canInstall);
  root
    .querySelector("[data-about-open]")
    ?.addEventListener("click", () => toggleOrPush(route, "about", callbacks));
  root
    .querySelector("[data-settings-open]")
    ?.addEventListener("click", () => toggleOrPush(route, "settings", callbacks));
  root
    .querySelector("[data-install-open]")
    ?.addEventListener("click", () => callbacks.onInstall?.());

  const productMount = root.querySelector<HTMLElement>("[data-product-mount]");
  const mount = root.querySelector("[data-panel-mount]");
  if (!mount) return;
  mountShellRoute(
    root,
    mount,
    productMount,
    route,
    state,
    callbacks,
    (fn) => {
      dialogCleanup = fn;
    },
    (fn) => {
      productCleanup = fn;
    },
  );
}

function toggleOrPush(route: GpRoute, target: GpRoute, callbacks: AppShellCallbacks): void {
  if (route === target) callbacks.onPop();
  else callbacks.onPushRoute(target);
}
