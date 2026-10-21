/**
 * Minimal single-page PDF embedding a JPEG image of the QR.
 */
export async function canvasToPdfBlob(
  canvas: HTMLCanvasElement,
  pageWidth = 612,
  pageHeight = 792,
): Promise<Blob> {
  const jpeg = await new Promise<Blob>((resolve, reject) => {
    canvas.toBlob(
      (b) => (b ? resolve(b) : reject(new Error("JPEG encode failed"))),
      "image/jpeg",
      0.92,
    );
  });
  const imgBytes = new Uint8Array(await jpeg.arrayBuffer());
  const imgW = Math.min(400, canvas.width);
  const imgH = Math.min(400, canvas.height);
  const x = Math.floor((pageWidth - imgW) / 2);
  const y = Math.floor((pageHeight - imgH) / 2);

  const enc = new TextEncoder();
  const chunks: Uint8Array[] = [];
  const offsets: number[] = [];
  let pos = 0;

  const push = (u8: Uint8Array) => {
    chunks.push(u8);
    pos += u8.length;
  };
  const pushStr = (s: string) => push(enc.encode(s));

  pushStr("%PDF-1.4\n");

  const objStarts: number[] = [];
  const beginObj = (n: number) => {
    objStarts[n] = pos;
    pushStr(`${n} 0 obj\n`);
  };
  const endObj = () => pushStr("\nendobj\n");

  beginObj(1);
  pushStr("<< /Type /Catalog /Pages 2 0 R >>");
  endObj();

  beginObj(2);
  pushStr("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
  endObj();

  beginObj(3);
  pushStr(
    `<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${pageWidth} ${pageHeight}] /Contents 4 0 R /Resources << /XObject << /Im0 5 0 R >> >> >>`,
  );
  endObj();

  const content = `q\n${imgW} 0 0 ${imgH} ${x} ${y} cm\n/Im0 Do\nQ\n`;
  beginObj(4);
  pushStr(`<< /Length ${content.length} >>\nstream\n${content}endstream`);
  endObj();

  beginObj(5);
  pushStr(
    `<< /Type /XObject /Subtype /Image /Width ${canvas.width} /Height ${canvas.height} /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length ${imgBytes.length} >>\nstream\n`,
  );
  push(imgBytes);
  pushStr("\nendstream");
  endObj();

  const xrefPos = pos;
  pushStr(`xref\n0 6\n0000000000 65535 f \n`);
  for (let i = 1; i <= 5; i++) {
    pushStr(`${String(objStarts[i] ?? 0).padStart(10, "0")} 00000 n \n`);
  }
  pushStr(`trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n${xrefPos}\n%%EOF\n`);

  void offsets;
  const total = chunks.reduce((n, c) => n + c.length, 0);
  const out = new Uint8Array(total);
  let o = 0;
  for (const c of chunks) {
    out.set(c, o);
    o += c.length;
  }
  return new Blob([out], { type: "application/pdf" });
}
