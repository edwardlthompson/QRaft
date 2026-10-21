import type { AppShellState } from "./AppShell";
import { APP_VERSION, assetPrefixOf, loadAppUpdateConfig } from "./about/aboutSession";
import { decideLaunchPrompt, type LaunchPrompt } from "./about/runAppUpdates";

type NavCtl = { openPrompt: () => AppShellState["nav"] };

/** Daily launch prompt (donate / update) after restart guard. */
export async function maybeOpenLaunchPrompt(
  navCtl: NavCtl,
  setState: (patch: Partial<AppShellState>) => void,
): Promise<void> {
  const config = await loadAppUpdateConfig();
  const prompt: LaunchPrompt | null = await decideLaunchPrompt({
    currentVersion: APP_VERSION,
    kind: "exe",
    prefix: assetPrefixOf(config),
    releaseRepo: config?.release_repo ?? "",
    userAgent: `GoldenPath/${APP_VERSION}`,
  });
  setState({ releaseRepo: config?.release_repo ?? "" });
  if (!prompt) return;
  setState({ launchPrompt: prompt, nav: navCtl.openPrompt() });
}
