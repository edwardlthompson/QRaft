/** Chrome/Edge `beforeinstallprompt` → in-app Install control. */

export type BeforeInstallPromptEvent = Event & {
  prompt: () => Promise<void>;
  userChoice: Promise<{ outcome: "accepted" | "dismissed" }>;
};

type Listener = (available: boolean) => void;

let deferred: BeforeInstallPromptEvent | null = null;
let hooked = false;
const listeners = new Set<Listener>();

function notify(available: boolean): void {
  for (const cb of listeners) cb(available);
}

/** Call once at bootstrap, before first paint when possible. */
export function initPwaInstall(): void {
  if (hooked) return;
  hooked = true;
  window.addEventListener("beforeinstallprompt", (ev) => {
    ev.preventDefault();
    deferred = ev as BeforeInstallPromptEvent;
    notify(true);
  });
  window.addEventListener("appinstalled", () => {
    deferred = null;
    notify(false);
  });
}

/** Test helper — clears deferred prompt state. */
export function resetPwaInstallForTests(): void {
  deferred = null;
}

export function canInstallPwa(): boolean {
  return deferred !== null;
}

export function onInstallAvailability(cb: Listener): () => void {
  listeners.add(cb);
  if (deferred) cb(true);
  return () => {
    listeners.delete(cb);
  };
}

export async function promptPwaInstall(): Promise<"accepted" | "dismissed" | "unavailable"> {
  if (!deferred) return "unavailable";
  const ev = deferred;
  deferred = null;
  notify(false);
  await ev.prompt();
  const { outcome } = await ev.userChoice;
  return outcome;
}
