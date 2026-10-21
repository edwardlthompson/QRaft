import { type AppShellState, createAppShell } from "./AppShell";
import { APP_VERSION, handleRestartGuard } from "./about/aboutSession";
import { applyPwaUpdate } from "./about/applyUpdate";
import { loadDonations, primaryDonateUrl } from "./about/donations";
import { markUpdateChecked, markVersionSeen } from "./about/updatePrefs";
import { assetUrl } from "./assetUrl";
import { maybeOpenLaunchPrompt } from "./bootstrapLaunch";
import { installCrashHandler } from "./crash-capture/installHandler";
import { readPendingCrash } from "./crash-capture/pendingCrash";
import { getSaveCrashes } from "./feedback/saveCrashes";
import { t } from "./i18n";
import {
  applyFeedbackIntents,
  createHistoryNav,
  feedbackPrefillOf,
  loadNav,
  syncHistoryToNav,
} from "./nav";
import {
  canInstallPwa,
  initPwaInstall,
  onInstallAvailability,
  promptPwaInstall,
} from "./pwaInstall";
import { shareTargetDescription } from "./share-target";
import { initTheme, subscribeThemeChange } from "./theme";

let popAbort: AbortController | undefined;

export function bootstrapApp(appRoot: HTMLDivElement): void {
  popAbort?.abort();
  popAbort = new AbortController();
  initTheme();
  initPwaInstall();
  installCrashHandler(() => getSaveCrashes());
  const shared = shareTargetDescription(new URLSearchParams(window.location.search));
  const intent = applyFeedbackIntents(loadNav(), {
    crash: Boolean(readPendingCrash()),
    share: shared,
  });
  syncHistoryToNav(history, intent.nav);
  const navCtl = createHistoryNav(history, intent.nav);

  let state: AppShellState = {
    nav: intent.nav,
    feedbackPrefill: intent.prefill,
    updateStatus: t("about.update.current"),
    donations: { enabled: false, message: "", links: [] },
    launchPrompt: null,
    canInstall: canInstallPwa(),
  };

  const patchState = (patch: Partial<AppShellState>) => {
    state = { ...state, ...patch };
    render();
  };

  function applyNav(next: typeof intent.nav): void {
    state = {
      ...state,
      nav: next,
      launchPrompt: next.promptOpen ? state.launchPrompt : null,
      feedbackPrefill: feedbackPrefillOf(next, state.feedbackPrefill),
    };
    render();
  }

  function render(): void {
    createAppShell(appRoot, state, {
      onState: patchState,
      onPushRoute: (route, kind) => {
        const next = navCtl.pushRoute(route, kind);
        if (next) applyNav(next);
      },
      onPop: () => navCtl.popFromUi(),
      onApplyUpdate: () => {
        void handleApplyUpdate();
      },
      onDonate: openDonate,
      onInstall: () => {
        void promptPwaInstall().then(() => patchState({ canInstall: canInstallPwa() }));
      },
      onLaunchPrompt: handleLaunchPrompt,
      canApplyUpdate: false,
    });
  }

  onInstallAvailability((available) => {
    if (state.canInstall !== available) patchState({ canInstall: available });
  });

  async function handleApplyUpdate(): Promise<void> {
    if (!("serviceWorker" in navigator)) return;
    const registration = await navigator.serviceWorker.getRegistration();
    if (!registration) return;
    if (await applyPwaUpdate(registration)) {
      patchState({ updateStatus: t("about.update.restarting") });
    }
  }

  function openDonate(): void {
    window.open(primaryDonateUrl(state.donations), "_blank", "noopener,noreferrer");
  }

  function handleLaunchPrompt(accepted: boolean): void {
    const prompt = state.launchPrompt;
    if (!prompt) return;
    if (prompt.kind === "donate") {
      markVersionSeen(APP_VERSION);
      if (accepted) openDonate();
    } else {
      markUpdateChecked(Date.now(), prompt.version);
      if (accepted) window.open(prompt.url, "_blank", "noopener,noreferrer");
    }
    navCtl.popFromUi();
  }

  subscribeThemeChange(() => render());
  window.addEventListener("popstate", () => applyNav(navCtl.onPopState(appRoot).nav), {
    signal: popAbort.signal,
  });

  render();
  void loadDonations().then((d) => patchState({ donations: d }));
  if (!handleRestartGuard()) {
    void maybeOpenLaunchPrompt(navCtl, patchState);
  }

  window.addEventListener("online", render);
  window.addEventListener("offline", render);

  if ("serviceWorker" in navigator) {
    window.addEventListener("load", () => {
      navigator.serviceWorker.register(assetUrl("sw.js")).catch(() => {});
    });
  }
}
