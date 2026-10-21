import type { DonationConfig } from "./types";

export const VENMO_DONATE_URL = "https://venmo.com/code?user_id=1857304970395648420";

export const DONATE_LABEL = "Donate via Venmo";

const DEFAULT_MESSAGE = "If this project helps you, consider supporting development.";

/** Keep Venmo available and show donate links in About. */
export function withQuietDonate(config: DonationConfig): DonationConfig {
  const hasVenmo = config.links.some((link) => link.url === VENMO_DONATE_URL);
  const links = hasVenmo
    ? config.links.map((link) =>
        link.url === VENMO_DONATE_URL ? { ...link, label: DONATE_LABEL } : link,
      )
    : [...config.links, { label: DONATE_LABEL, url: VENMO_DONATE_URL }];
  // Drop template placeholders so About only lists real destinations.
  const real = links.filter((l) => !/YOUR_[A-Z0-9_]+/i.test(l.url));
  return {
    enabled: true,
    message: config.message.trim() || DEFAULT_MESSAGE,
    links: real.length > 0 ? real : [{ label: DONATE_LABEL, url: VENMO_DONATE_URL }],
  };
}
