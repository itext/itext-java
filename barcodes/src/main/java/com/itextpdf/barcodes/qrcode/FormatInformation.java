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
 * Encapsulates a QR Code's format information, including the data mask used and
 * error correction level.
 *
 * @author Sean Owen
 * @see ErrorCorrectionLevel
 */
final class FormatInformation {

    private static final int FORMAT_INFO_MASK_QR = 0x5412;

    /**
     * See ISO 18004:2006, Annex C, Table C.1
     */
    private static final int[][] FORMAT_INFO_DECODE_LOOKUP = {
            {0x5412, 0x00},
            {0x5125, 0x01},
            {0x5E7C, 0x02},
            {0x5B4B, 0x03},
            {0x45F9, 0x04},
            {0x40CE, 0x05},
            {0x4F97, 0x06},
            {0x4AA0, 0x07},
            {0x77C4, 0x08},
            {0x72F3, 0x09},
            {0x7DAA, 0x0A},
            {0x789D, 0x0B},
            {0x662F, 0x0C},
            {0x6318, 0x0D},
            {0x6C41, 0x0E},
            {0x6976, 0x0F},
            {0x1689, 0x10},
            {0x13BE, 0x11},
            {0x1CE7, 0x12},
            {0x19D0, 0x13},
            {0x0762, 0x14},
            {0x0255, 0x15},
            {0x0D0C, 0x16},
            {0x083B, 0x17},
            {0x355F, 0x18},
            {0x3068, 0x19},
            {0x3F31, 0x1A},
            {0x3A06, 0x1B},
            {0x24B4, 0x1C},
            {0x2183, 0x1D},
            {0x2EDA, 0x1E},
            {0x2BED, 0x1F},
    };

    /**
     * Offset i holds the number of 1 bits in the binary representation of i
     */
    private static final int[] BITS_SET_IN_HALF_BYTE =
            {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};

    private final ErrorCorrectionLevel errorCorrectionLevel;
    private final byte dataMask;

    private FormatInformation(int formatInfo) {
        // Bits 3,4
        errorCorrectionLevel = ErrorCorrectionLevel.forBits((formatInfo >> 3) & 0x03);
        // Bottom 3 bits
        dataMask = (byte) (formatInfo & 0x07);
    }

    static int numBitsDiffering(int a, int b) {
        a ^= b; // a now has a 1 bit exactly where its bit differs with b's
        // Count bits set quickly with a series of lookups:
        return BITS_SET_IN_HALF_BYTE[a & 0x0F] +
                BITS_SET_IN_HALF_BYTE[(a >>> 4 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 8 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 12 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 16 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 20 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 24 & 0x0F)] +
                BITS_SET_IN_HALF_BYTE[(a >>> 28 & 0x0F)];
    }

    /**
     * @param maskedFormatInfo1 format info indicator, with mask still applied
     * @param maskedFormatInfo2 second copy of same info; both are checked at the same time
     *  to establish best match
     * @return information about the format it specifies, or <code>null</code>
     *  if doesn't seem to match any known pattern
     */
    static FormatInformation decodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
        FormatInformation formatInfo = doDecodeFormatInformation(maskedFormatInfo1, maskedFormatInfo2);
        if (formatInfo != null) {
            return formatInfo;
        }
        // Should return null, but, some QR codes apparently
        // do not mask this info. Try again by actually masking the pattern
        // first
        return doDecodeFormatInformation(maskedFormatInfo1 ^ FORMAT_INFO_MASK_QR,
                maskedFormatInfo2 ^ FORMAT_INFO_MASK_QR);
    }

    private static FormatInformation doDecodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
        // Find the int in FORMAT_INFO_DECODE_LOOKUP with fewest bits differing
        int bestDifference = Integer.MAX_VALUE;
        int bestFormatInfo = 0;
        for (int i = 0; i < FORMAT_INFO_DECODE_LOOKUP.length; i++) {
            int[] decodeInfo = FORMAT_INFO_DECODE_LOOKUP[i];
            int targetInfo = decodeInfo[0];
            if (targetInfo == maskedFormatInfo1 || targetInfo == maskedFormatInfo2) {
                // Found an exact match
                return new FormatInformation(decodeInfo[1]);
            }
            int bitsDifference = numBitsDiffering(maskedFormatInfo1, targetInfo);
            if (bitsDifference < bestDifference) {
                bestFormatInfo = decodeInfo[1];
                bestDifference = bitsDifference;
            }
            if (maskedFormatInfo1 != maskedFormatInfo2) {
                // also try the other option
                bitsDifference = numBitsDiffering(maskedFormatInfo2, targetInfo);
                if (bitsDifference < bestDifference) {
                    bestFormatInfo = decodeInfo[1];
                    bestDifference = bitsDifference;
                }
            }
        }
        // Hamming distance of the 32 masked codes is 7, by construction, so <= 3 bits
        // differing means we found a match
        if (bestDifference <= 3) {
            return new FormatInformation(bestFormatInfo);
        }
        return null;
    }

    ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    /**
     * @return The datamask in byte-format
     */
    byte getDataMask() {
        return dataMask;
    }

    /**
     * @return the hashcode of the QR-code format information
     */
    public int hashCode() {
        return (errorCorrectionLevel.ordinal() << 3) | (int) dataMask;
    }

    /**
     * Compares the Format Information of this and o
     * @param o object to compare to
     * @return True if o is a FormatInformationObject and the error-correction level and the datamask are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (!(o instanceof FormatInformation)) {
            return false;
        }
        FormatInformation other = (FormatInformation) o;
        return this.errorCorrectionLevel == other.errorCorrectionLevel &&
                this.dataMask == other.dataMask;
    }

}

