const cache = new Map<string, HTMLImageElement>();

export function getCachedLogo(src: string): HTMLImageElement | undefined {
  const img = cache.get(src);
  return img?.complete && img.naturalWidth > 0 ? img : undefined;
}

/** Load (or return cached) image; rejects on error. */
export function loadLogoImage(src: string): Promise<HTMLImageElement> {
  const hit = getCachedLogo(src);
  if (hit) return Promise.resolve(hit);
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.decoding = "async";
    img.onload = () => {
      cache.set(src, img);
      resolve(img);
    };
    img.onerror = () => reject(new Error("logo load failed"));
    img.src = src;
    cache.set(src, img);
  });
}

/** Shrink an image file to a PNG data URL (max edge 256). */
export function fileToLogoDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error("read failed"));
    reader.onload = () => {
      const raw = String(reader.result ?? "");
      const img = new Image();
      img.onload = () => {
        const max = 256;
        const scale = Math.min(1, max / Math.max(img.width, img.height, 1));
        const w = Math.max(1, Math.round(img.width * scale));
        const h = Math.max(1, Math.round(img.height * scale));
        const c = document.createElement("canvas");
        c.width = w;
        c.height = h;
        const ctx = c.getContext("2d");
        if (!ctx) {
          reject(new Error("canvas"));
          return;
        }
        ctx.drawImage(img, 0, 0, w, h);
        const dataUrl = c.toDataURL("image/png");
        cache.set(dataUrl, img);
        resolve(dataUrl);
      };
      img.onerror = () => reject(new Error("decode failed"));
      img.src = raw;
    };
    reader.readAsDataURL(file);
  });
}
