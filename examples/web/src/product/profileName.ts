import type { PayloadKind } from "../core-qr/payload";
import { t } from "../i18n";

const KINDS: PayloadKind[] = ["url", "text", "wifi", "vcard", "email", "sms", "phone", "crypto"];

/** Default profile name for a payload kind (url → Website). */
export function suggestedProfileName(kind: PayloadKind): string {
  if (kind === "url") return t("editor.new_name");
  return t(`kind.${kind}`);
}

/** True when name is empty or still a kind suggestion (safe to auto-replace). */
export function isSuggestedProfileName(name: string): boolean {
  const trimmed = name.trim();
  if (!trimmed) return true;
  return KINDS.some((k) => suggestedProfileName(k) === trimmed);
}
