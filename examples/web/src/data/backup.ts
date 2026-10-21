import type { QrProfile } from "./profiles";

const enc = new TextEncoder();
const dec = new TextDecoder();

async function deriveKey(passphrase: string, salt: Uint8Array): Promise<CryptoKey> {
  const base = await crypto.subtle.importKey("raw", enc.encode(passphrase), "PBKDF2", false, [
    "deriveKey",
  ]);
  const saltBuf = salt.buffer.slice(
    salt.byteOffset,
    salt.byteOffset + salt.byteLength,
  ) as ArrayBuffer;
  return crypto.subtle.deriveKey(
    { name: "PBKDF2", salt: saltBuf, iterations: 100_000, hash: "SHA-256" },
    base,
    { name: "AES-GCM", length: 256 },
    false,
    ["encrypt", "decrypt"],
  );
}

/** Passphrase-wrapped gallery backup (AES-GCM), portable JSON envelope. */
export async function encryptBackup(profiles: QrProfile[], passphrase: string): Promise<string> {
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const iv = crypto.getRandomValues(new Uint8Array(12));
  const key = await deriveKey(passphrase, salt);
  const plain = enc.encode(JSON.stringify(profiles));
  const cipher = new Uint8Array(await crypto.subtle.encrypt({ name: "AES-GCM", iv }, key, plain));
  return JSON.stringify({
    v: 1,
    alg: "AES-GCM",
    salt: btoa(String.fromCharCode(...salt)),
    iv: btoa(String.fromCharCode(...iv)),
    data: btoa(String.fromCharCode(...cipher)),
  });
}

export async function decryptBackup(envelope: string, passphrase: string): Promise<QrProfile[]> {
  const obj = JSON.parse(envelope) as { salt: string; iv: string; data: string };
  const salt = Uint8Array.from(atob(obj.salt), (c) => c.charCodeAt(0));
  const iv = Uint8Array.from(atob(obj.iv), (c) => c.charCodeAt(0));
  const data = Uint8Array.from(atob(obj.data), (c) => c.charCodeAt(0));
  const key = await deriveKey(passphrase, salt);
  const plain = await crypto.subtle.decrypt({ name: "AES-GCM", iv }, key, data);
  return JSON.parse(dec.decode(plain)) as QrProfile[];
}

export function encodePlainBackup(profiles: QrProfile[]): string {
  return JSON.stringify({ version: 1, profiles });
}

export function decodePlainBackup(json: string): QrProfile[] | null {
  try {
    const o = JSON.parse(json) as { profiles?: QrProfile[] } | QrProfile[];
    if (Array.isArray(o)) return o;
    if (o && Array.isArray(o.profiles)) return o.profiles;
    return null;
  } catch {
    return null;
  }
}
