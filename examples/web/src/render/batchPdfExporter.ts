/**
 * Multi-page PDF: one JPEG-embedded QR per page (Letter 612×792).
 */
export async function canvasesToPdfBlob(
  canvases: HTMLCanvasElement[],
  pageWidth = 612,
  pageHeight = 792,
): Promise<Blob> {
  if (canvases.length === 0) throw new Error("No canvases");
  const pages = await Promise.all(
    canvases.map(
      (canvas) =>
        new Promise<{ bytes: Uint8Array; w: number; h: number }>((resolve, reject) => {
          canvas.toBlob(
            async (b) => {
              if (!b) {
                reject(new Error("JPEG encode failed"));
                return;
              }
              resolve({
                bytes: new Uint8Array(await b.arrayBuffer()),
                w: canvas.width,
                h: canvas.height,
              });
            },
            "image/jpeg",
            0.92,
          );
        }),
    ),
  );

  const enc = new TextEncoder();
  const chunks: Uint8Array[] = [];
  let pos = 0;
  const push = (u8: Uint8Array) => {
    chunks.push(u8);
    pos += u8.length;
  };
  const pushStr = (s: string) => push(enc.encode(s));
  const objStarts: number[] = [];
  const beginObj = (n: number) => {
    objStarts[n] = pos;
    pushStr(`${n} 0 obj\n`);
  };
  const endObj = () => pushStr("\nendobj\n");

  pushStr("%PDF-1.4\n");
  beginObj(1);
  pushStr("<< /Type /Catalog /Pages 2 0 R >>");
  endObj();

  const pageCount = pages.length;
  const pageObjNums = pages.map((_, i) => 3 + i * 3);
  beginObj(2);
  pushStr(
    `<< /Type /Pages /Kids [${pageObjNums.map((n) => `${n} 0 R`).join(" ")}] /Count ${pageCount} >>`,
  );
  endObj();

  for (let i = 0; i < pageCount; i++) {
    const pageObj = 3 + i * 3;
    const contentObj = pageObj + 1;
    const imageObj = pageObj + 2;
    const { bytes, w, h } = pages[i];
    const imgW = Math.min(400, w);
    const imgH = Math.min(400, h);
    const x = Math.floor((pageWidth - imgW) / 2);
    const y = Math.floor((pageHeight - imgH) / 2);
    const content = `q\n${imgW} 0 0 ${imgH} ${x} ${y} cm\n/Im0 Do\nQ\n`;

    beginObj(pageObj);
    pushStr(
      `<< /Type /Page /Parent 2 0 R /MediaBox [0 0 ${pageWidth} ${pageHeight}] /Contents ${contentObj} 0 R /Resources << /XObject << /Im0 ${imageObj} 0 R >> >> >>`,
    );
    endObj();

    beginObj(contentObj);
    pushStr(`<< /Length ${content.length} >>\nstream\n${content}endstream`);
    endObj();

    beginObj(imageObj);
    pushStr(
      `<< /Type /XObject /Subtype /Image /Width ${w} /Height ${h} /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length ${bytes.length} >>\nstream\n`,
    );
    push(bytes);
    pushStr("\nendstream");
    endObj();
  }

  const lastObj = 2 + pageCount * 3;
  const xrefPos = pos;
  pushStr(`xref\n0 ${lastObj + 1}\n0000000000 65535 f \n`);
  for (let i = 1; i <= lastObj; i++) {
    pushStr(`${String(objStarts[i] ?? 0).padStart(10, "0")} 00000 n \n`);
  }
  pushStr(`trailer\n<< /Size ${lastObj + 1} /Root 1 0 R >>\nstartxref\n${xrefPos}\n%%EOF\n`);

  const total = chunks.reduce((n, c) => n + c.length, 0);
  const out = new Uint8Array(total);
  let o = 0;
  for (const c of chunks) {
    out.set(c, o);
    o += c.length;
  }
  return new Blob([out], { type: "application/pdf" });
}
