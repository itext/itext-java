/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.barcodes.qrcode;

/**
 * See ISO 18004:2006, 6.5.1. This enum encapsulates the four error correction levels
 * defined by the QR code standard.
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

    /**
     * Gets the ordinal value.
     *
     * @return the ordinal
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * Gets the bits.
     *
     * @return the bits
     */
    public int getBits() {
        return bits;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
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
