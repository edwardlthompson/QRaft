import { encodePayloadText, type QrPayload } from "./payload";
import type { ErrorCorrectionLevel, QrMatrix } from "./types";
import { qrcodegen } from "./vendor/qrcodegen.js";

function toNayuki(ecl: ErrorCorrectionLevel): qrcodegen.QrCode.Ecc {
  switch (ecl) {
    case "L":
      return qrcodegen.QrCode.Ecc.LOW;
    case "M":
      return qrcodegen.QrCode.Ecc.MEDIUM;
    case "Q":
      return qrcodegen.QrCode.Ecc.QUARTILE;
    case "H":
      return qrcodegen.QrCode.Ecc.HIGH;
  }
}

function fromNayuki(ecl: qrcodegen.QrCode.Ecc): ErrorCorrectionLevel {
  if (ecl === qrcodegen.QrCode.Ecc.LOW) return "L";
  if (ecl === qrcodegen.QrCode.Ecc.MEDIUM) return "M";
  if (ecl === qrcodegen.QrCode.Ecc.QUARTILE) return "Q";
  return "H";
}

export class QrEncodeError extends Error {
  constructor(message: string, cause?: unknown) {
    super(message);
    this.name = "QrEncodeError";
    if (cause !== undefined) (this as Error & { cause?: unknown }).cause = cause;
  }
}

export function encodeText(
  text: string,
  errorCorrection: ErrorCorrectionLevel = "M",
  opts: { minVersion?: number; maxVersion?: number; mask?: number; boostEcl?: boolean } = {},
): QrMatrix {
  if (!text) throw new QrEncodeError("text must not be empty");
  const minVersion = opts.minVersion ?? 1;
  const maxVersion = opts.maxVersion ?? 40;
  const mask = opts.mask ?? -1;
  const boostEcl = opts.boostEcl ?? true;
  try {
    const segs = qrcodegen.QrSegment.makeSegments(text);
    const qr = qrcodegen.QrCode.encodeSegments(
      segs,
      toNayuki(errorCorrection),
      minVersion,
      maxVersion,
      mask,
      boostEcl,
    );
    const size = qr.size;
    const modules = Array.from({ length: size * size }, (_, i) =>
      qr.getModule(i % size, Math.floor(i / size)),
    );
    return {
      version: qr.version,
      size,
      errorCorrection: fromNayuki(qr.errorCorrectionLevel),
      modules,
    };
  } catch (e) {
    throw new QrEncodeError(`Payload does not fit in QR versions ${minVersion}..${maxVersion}`, e);
  }
}

export function encodePayload(
  payload: QrPayload,
  errorCorrection: ErrorCorrectionLevel = "M",
  forceHighEcc = false,
): QrMatrix {
  const ecl = forceHighEcc ? "H" : errorCorrection;
  const text = encodePayloadText(payload);
  if (!text) throw new QrEncodeError("payload encodes to empty text");
  return encodeText(text, ecl, { boostEcl: !forceHighEcc });
}
