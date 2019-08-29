/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.barcodes.qrcode;

/**
 * See ISO 18004:2006, 6.5.1. This enum encapsulates the four error correction levels
 * defined by the QR code standard.
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

    /**
     * Gets the ordinal value.
     *
     * @return the ordinal
     */
    public int ordinal() {
        return ordinal;
    }

    public int getBits() {
        return bits;
    }

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
