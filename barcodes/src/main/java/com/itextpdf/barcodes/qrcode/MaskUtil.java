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
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
final class MaskUtil {

    private MaskUtil() {
    }

    /**
     * Apply mask penalty rule 1 and return the penalty. Find repetitive cells with the same color and
     * give penalty to them. Example: 00000 or 11111.
     * @param matrix ByteMatrix to apply the penalty rule to
     * @return the rule 1 penalty
     */
    public static int applyMaskPenaltyRule1(ByteMatrix matrix) {
        return applyMaskPenaltyRule1Internal(matrix, true) + applyMaskPenaltyRule1Internal(matrix, false);
    }



    /**
     * Apply mask penalty rule 2 and return the penalty. Find 2x2 blocks with the same color and give
     * penalty to them.
     * @param matrix ByteMatrix to apply the penalty rule to
     * @return the rule 2 penalty
     */
    public static int applyMaskPenaltyRule2(ByteMatrix matrix) {
        int penalty = 0;
        byte[][] array = matrix.getArray();
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                int value = array[y][x];
                if (value == array[y][x + 1] && value == array[y + 1][x] && value == array[y + 1][x + 1]) {
                    penalty += 3;
                }
            }
        }
        return penalty;
    }

    /**
     * Apply mask penalty rule 3 and return the penalty. Find consecutive cells of 00001011101 or
     * 10111010000, and give penalty to them.  If we find patterns like 000010111010000, we give
     * penalties twice (i.e. 40 * 2).
     * @param matrix ByteMatrix to apply the penalty rule to
     * @return the rule 3 penalty
     */
    public static int applyMaskPenaltyRule3(ByteMatrix matrix) {
        int penalty = 0;
        byte[][] array = matrix.getArray();
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // Tried to simplify following conditions but failed.
                if (x + 6 < width &&
                        array[y][x] == 1 &&
                        array[y][x +  1] == 0 &&
                        array[y][x +  2] == 1 &&
                        array[y][x +  3] == 1 &&
                        array[y][x +  4] == 1 &&
                        array[y][x +  5] == 0 &&
                        array[y][x +  6] == 1 &&
                        ((x + 10 < width &&
                                array[y][x +  7] == 0 &&
                                array[y][x +  8] == 0 &&
                                array[y][x +  9] == 0 &&
                                array[y][x + 10] == 0) ||
                                (x - 4 >= 0 &&
                                        array[y][x -  1] == 0 &&
                                        array[y][x -  2] == 0 &&
                                        array[y][x -  3] == 0 &&
                                        array[y][x -  4] == 0))) {
                    penalty += 40;
                }
                if (y + 6 < height &&
                        array[y][x] == 1  &&
                        array[y +  1][x] == 0  &&
                        array[y +  2][x] == 1  &&
                        array[y +  3][x] == 1  &&
                        array[y +  4][x] == 1  &&
                        array[y +  5][x] == 0  &&
                        array[y +  6][x] == 1 &&
                        ((y + 10 < height &&
                                array[y +  7][x] == 0 &&
                                array[y +  8][x] == 0 &&
                                array[y +  9][x] == 0 &&
                                array[y + 10][x] == 0) ||
                                (y - 4 >= 0 &&
                                        array[y -  1][x] == 0 &&
                                        array[y -  2][x] == 0 &&
                                        array[y -  3][x] == 0 &&
                                        array[y -  4][x] == 0))) {
                    penalty += 40;
                }
            }
        }
        return penalty;
    }



    /**
     * Apply mask penalty rule 4 and return the penalty. Calculate the ratio of dark cells and give
     * penalty if the ratio is far from 50%. It gives 10 penalty for 5% distance. Examples:
     * -   0% => 100
     * -  40% =>  20
     * -  45% =>  10
     * -  50% =>   0
     * -  55% =>  10
     * -  55% =>  20
     * - 100% => 100
     * @param matrix Bytematrix to apply the rule to
     * @return the rule 4 penalty
     */
    public static int applyMaskPenaltyRule4(ByteMatrix matrix) {
        int numDarkCells = 0;
        byte[][] array = matrix.getArray();
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (array[y][x] == 1) {
                    numDarkCells += 1;
                }
            }
        }
        int numTotalCells = matrix.getHeight() * matrix.getWidth();
        double darkRatio = (double) numDarkCells / numTotalCells;
        return Math.abs((int) (darkRatio * 100 - 50)) / 5 * 10;
    }



    /**
     * Return the mask bit for "getMaskPattern" at "x" and "y". See 8.8 of JISX0510:2004 for mask
     * pattern conditions.
     * @param maskPattern masking pattern to use
     * @param x width coordiante
     * @param y height-coordinate
     * @return the mask bit at that position
     */
    public static boolean getDataMaskBit(int maskPattern, int x, int y) {
        if (!QRCode.isValidMaskPattern(maskPattern)) {
            throw new IllegalArgumentException("Invalid mask pattern");
        }
        int intermediate, temp;
        switch (maskPattern) {
            case 0:
                intermediate = (y + x) & 0x1;
                break;
            case 1:
                intermediate = y & 0x1;
                break;
            case 2:
                intermediate = x % 3;
                break;
            case 3:
                intermediate = (y + x) % 3;
                break;
            case 4:
                intermediate = ((y >>> 1) + (x / 3)) & 0x1;
                break;
            case 5:
                temp = y * x;
                intermediate = (temp & 0x1) + (temp % 3);
                break;
            case 6:
                temp = y * x;
                intermediate = (((temp & 0x1) + (temp % 3)) & 0x1);
                break;
            case 7:
                temp = y * x;
                intermediate = (((temp % 3) + ((y + x) & 0x1)) & 0x1);
                break;
            default:
                throw new IllegalArgumentException("Invalid mask pattern: " + maskPattern);
        }
        return intermediate == 0;
    }

    // Helper function for applyMaskPenaltyRule1. We need this for doing this calculation in both
    // vertical and horizontal orders respectively.
    private static int applyMaskPenaltyRule1Internal(ByteMatrix matrix, boolean isHorizontal) {
        int penalty = 0;
        int numSameBitCells = 0;
        int prevBit = -1;
        // Horizontal mode:
        //   for (int i = 0; i < matrix.height(); ++i) {
        //     for (int j = 0; j < matrix.width(); ++j) {
        //       int bit = matrix.get(i, j);
        // Vertical mode:
        //   for (int i = 0; i < matrix.width(); ++i) {
        //     for (int j = 0; j < matrix.height(); ++j) {
        //       int bit = matrix.get(j, i);
        int iLimit = isHorizontal ? matrix.getHeight() : matrix.getWidth();
        int jLimit = isHorizontal ? matrix.getWidth() : matrix.getHeight();
        byte[][] array = matrix.getArray();
        for (int i = 0; i < iLimit; ++i) {
            for (int j = 0; j < jLimit; ++j) {
                int bit = isHorizontal ? array[i][j] : array[j][i];
                if (bit == prevBit) {
                    numSameBitCells += 1;
                    // Found five repetitive cells with the same color (bit).
                    // We'll give penalty of 3.
                    if (numSameBitCells == 5) {
                        penalty += 3;
                    } else if (numSameBitCells > 5) {
                        // After five repetitive cells, we'll add the penalty one
                        // by one.
                        penalty += 1;
                    }
                } else {
                    numSameBitCells = 1;  // Include the cell itself.
                    prevBit = bit;
                }
            }
            numSameBitCells = 0;  // Clear at each row/column.
        }
        return penalty;
    }

}
