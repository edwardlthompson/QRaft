import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import {
  type BeforeInstallPromptEvent,
  canInstallPwa,
  initPwaInstall,
  onInstallAvailability,
  promptPwaInstall,
  resetPwaInstallForTests,
} from "./pwaInstall";

function fireBeforeInstall(): BeforeInstallPromptEvent {
  const ev = new Event("beforeinstallprompt", { cancelable: true }) as BeforeInstallPromptEvent;
  ev.prompt = vi.fn(async () => undefined);
  ev.userChoice = Promise.resolve({ outcome: "accepted" as const });
  window.dispatchEvent(ev);
  return ev;
}

describe("pwaInstall", () => {
  beforeEach(() => {
    resetPwaInstallForTests();
    initPwaInstall();
  });

  afterEach(() => {
    resetPwaInstallForTests();
  });

  it("captures beforeinstallprompt and reports availability", async () => {
    const seen: boolean[] = [];
    const unsub = onInstallAvailability((v) => seen.push(v));
    expect(canInstallPwa()).toBe(false);
    fireBeforeInstall();
    expect(canInstallPwa()).toBe(true);
    expect(seen.at(-1)).toBe(true);
    const outcome = await promptPwaInstall();
    expect(outcome).toBe("accepted");
    expect(canInstallPwa()).toBe(false);
    unsub();
  });

  it("returns unavailable when no deferred prompt", async () => {
    expect(await promptPwaInstall()).toBe("unavailable");
  });
});
