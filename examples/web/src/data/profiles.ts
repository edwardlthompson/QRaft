export type QrProfile = {
  id: string;
  name: string;
  payloadText: string;
  tags: string[];
  styleJson: string;
  sensitive: boolean;
  updatedAt: number;
};

const STORAGE_KEY = "qraft-profiles-v1";

function uid(): string {
  return `p_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 8)}`;
}

export function seedProfiles(): QrProfile[] {
  const now = Date.now();
  return [
    {
      id: uid(),
      name: "Personal",
      payloadText: "https://example.com",
      tags: ["personal"],
      styleJson: "{}",
      sensitive: false,
      updatedAt: now,
    },
    {
      id: uid(),
      name: "Work",
      payloadText: "https://example.com/work",
      tags: ["work"],
      styleJson: "{}",
      sensitive: false,
      updatedAt: now - 1,
    },
    {
      id: uid(),
      name: "Guest",
      payloadText: "WIFI:T:WPA;S:Guest;P:;;",
      tags: ["wifi", "guest"],
      styleJson: "{}",
      sensitive: true,
      updatedAt: now - 2,
    },
  ];
}

export function loadProfiles(): QrProfile[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      const seeded = seedProfiles();
      saveProfiles(seeded);
      return seeded;
    }
    const parsed = JSON.parse(raw) as QrProfile[];
    return Array.isArray(parsed) ? parsed : seedProfiles();
  } catch {
    return seedProfiles();
  }
}

export function saveProfiles(profiles: QrProfile[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(profiles));
}

export function upsertProfile(
  profiles: QrProfile[],
  input: Omit<QrProfile, "id" | "updatedAt"> & { id?: string },
): QrProfile[] {
  const id = input.id ?? uid();
  const next: QrProfile = { ...input, id, updatedAt: Date.now() };
  const idx = profiles.findIndex((p) => p.id === id);
  if (idx >= 0) {
    const copy = profiles.slice();
    copy[idx] = next;
    saveProfiles(copy);
    return copy;
  }
  const copy = [next, ...profiles];
  saveProfiles(copy);
  return copy;
}

export function deleteProfile(profiles: QrProfile[], id: string): QrProfile[] {
  const copy = profiles.filter((p) => p.id !== id);
  saveProfiles(copy);
  return copy;
}

export function searchProfiles(profiles: QrProfile[], query: string): QrProfile[] {
  const q = query.trim().toLowerCase();
  if (!q) return profiles;
  return profiles.filter(
    (p) =>
      p.name.toLowerCase().includes(q) ||
      p.payloadText.toLowerCase().includes(q) ||
      p.tags.some((t) => t.toLowerCase().includes(q)),
  );
}

export type ProfileSort = "NAME" | "NEWEST";

export function sortProfiles(profiles: QrProfile[], sort: ProfileSort): QrProfile[] {
  const copy = profiles.slice();
  if (sort === "NAME") copy.sort((a, b) => a.name.localeCompare(b.name));
  else copy.sort((a, b) => b.updatedAt - a.updatedAt);
  return copy;
}
