package com.itextpdf.barcodes.qrcode;

/**
 * These are a set of hints that you may pass to Writers to specify their behavior.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class EncodeHintType {

    /**
     * Specifies what degree of error correction to use, for example in QR Codes (type Integer).
     */
    public static final EncodeHintType ERROR_CORRECTION = new EncodeHintType();

    /**
     * Specifies what character encoding to use where applicable (type String)
     */
    public static final EncodeHintType CHARACTER_SET = new EncodeHintType();

    private EncodeHintType() {
    }

}
