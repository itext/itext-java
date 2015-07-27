package com.itextpdf.barcodes.qrcode;

/**
 * <p>See ISO 18004:2006, 6.5.1. This enum encapsulates the four error correction levels
 * defined by the QR code standard.</p>
 *
 * @author Sean Owen
 */
public final class ErrorCorrectionLevel {

    /**
     * L = ~7% correction
     */
    public static final ErrorCorrectionLevel L = new ErrorCorrectionLevel(0, 0x01, "L");
    /**
     * M = ~15% correction
     */
    public static final ErrorCorrectionLevel M = new ErrorCorrectionLevel(1, 0x00, "M");
    /**
     * Q = ~25% correction
     */
    public static final ErrorCorrectionLevel Q = new ErrorCorrectionLevel(2, 0x03, "Q");
    /**
     * H = ~30% correction
     */
    public static final ErrorCorrectionLevel H = new ErrorCorrectionLevel(3, 0x02, "H");

    private static final ErrorCorrectionLevel[] FOR_BITS = {M, L, H, Q};

    private final int ordinal;
    private final int bits;
    private final String name;

    private ErrorCorrectionLevel(int ordinal, int bits, String name) {
        this.ordinal = ordinal;
        this.bits = bits;
        this.name = name;
    }

    public int ordinal() {
        return ordinal;
    }

    public int getBits() {
        return bits;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    /**
     * @param bits int containing the two bits encoding a QR Code's error correction level
     * @return {@link ErrorCorrectionLevel} representing the encoded error correction level
     */
    public static ErrorCorrectionLevel forBits(int bits) {
        if (bits < 0 || bits >= FOR_BITS.length) {
            throw new IllegalArgumentException();
        }
        return FOR_BITS[bits];
    }


}
