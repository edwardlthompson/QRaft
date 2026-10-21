/** Type declarations for Nayuki QR-Code-generator (MIT). */
export namespace qrcodegen {
  export class QrCode {
    static encodeText(text: string, ecl: QrCode.Ecc): QrCode;
    static encodeSegments(
      segs: ReadonlyArray<QrSegment>,
      ecl: QrCode.Ecc,
      minVersion?: number,
      maxVersion?: number,
      mask?: number,
      boostEcl?: boolean,
    ): QrCode;
    readonly version: number;
    readonly size: number;
    readonly errorCorrectionLevel: QrCode.Ecc;
    getModule(x: number, y: number): boolean;
  }
  export namespace QrCode {
    export class Ecc {
      static readonly LOW: Ecc;
      static readonly MEDIUM: Ecc;
      static readonly QUARTILE: Ecc;
      static readonly HIGH: Ecc;
      readonly ordinal: number;
    }
  }
  export class QrSegment {
    static makeSegments(text: string): QrSegment[];
  }
}
