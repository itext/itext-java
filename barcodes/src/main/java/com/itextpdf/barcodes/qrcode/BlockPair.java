package com.itextpdf.barcodes.qrcode;

final class BlockPair {

    private final ByteArray dataBytes;
    private final ByteArray errorCorrectionBytes;

    BlockPair(ByteArray data, ByteArray errorCorrection) {
        dataBytes = data;
        errorCorrectionBytes = errorCorrection;
    }

    public ByteArray getDataBytes() {
        return dataBytes;
    }

    public ByteArray getErrorCorrectionBytes() {
        return errorCorrectionBytes;
    }

}
