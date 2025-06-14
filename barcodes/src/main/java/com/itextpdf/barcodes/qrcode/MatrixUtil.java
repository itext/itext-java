/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itextpdf.barcodes.qrcode;

import com.itextpdf.barcodes.exceptions.WriterException;

final class MatrixUtil {

    private MatrixUtil() {
    }

    private static final int[][] POSITION_DETECTION_PATTERN = {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1},
    };

    private static final int[][] HORIZONTAL_SEPARATION_PATTERN = {
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    private static final int[][] VERTICAL_SEPARATION_PATTERN = {
            {0}, {0}, {0}, {0}, {0}, {0}, {0},
    };

    private static final int[][] POSITION_ADJUSTMENT_PATTERN = {
            {1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1},
            {1, 0, 1, 0, 1},
            {1, 0, 0, 0, 1},
            {1, 1, 1, 1, 1},
    };

    // From Appendix E. Table 1, JIS0510X:2004 (p 71). The table was double-checked by komatsu.
    private static final int[][] POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE = {

            // Version 1
            {-1, -1, -1, -1, -1, -1, -1},

            // Version 2
            {6, 18, -1, -1, -1, -1, -1},

            // Version 3
            {6, 22, -1, -1, -1, -1, -1},

            // Version 4
            {6, 26, -1, -1, -1, -1, -1},

            // Version 5
            {6, 30, -1, -1, -1, -1, -1},

            // Version 6
            {6, 34, -1, -1, -1, -1, -1},

            // Version 7
            {6, 22, 38, -1, -1, -1, -1},

            // Version 8
            {6, 24, 42, -1, -1, -1, -1},

            // Version 9
            {6, 26, 46, -1, -1, -1, -1},

            // Version 10
            {6, 28, 50, -1, -1, -1, -1},

            // Version 11
            {6, 30, 54, -1, -1, -1, -1},

            // Version 12
            {6, 32, 58, -1, -1, -1, -1},

            // Version 13
            {6, 34, 62, -1, -1, -1, -1},

            // Version 14
            {6, 26, 46, 66, -1, -1, -1},

            // Version 15
            {6, 26, 48, 70, -1, -1, -1},

            // Version 16
            {6, 26, 50, 74, -1, -1, -1},

            // Version 17
            {6, 30, 54, 78, -1, -1, -1},

            // Version 18
            {6, 30, 56, 82, -1, -1, -1},

            // Version 19
            {6, 30, 58, 86, -1, -1, -1},

            // Version 20
            {6, 34, 62, 90, -1, -1, -1},

            // Version 21
            {6, 28, 50, 72, 94, -1, -1},

            // Version 22
            {6, 26, 50, 74, 98, -1, -1},

            // Version 23
            {6, 30, 54, 78, 102, -1, -1},

            // Version 24
            {6, 28, 54, 80, 106, -1, -1},

            // Version 25
            {6, 32, 58, 84, 110, -1, -1},

            // Version 26
            {6, 30, 58, 86, 114, -1, -1},

            // Version 27
            {6, 34, 62, 90, 118, -1, -1},

            // Version 28
            {6, 26, 50, 74, 98, 122, -1},

            // Version 29
            {6, 30, 54, 78, 102, 126, -1},

            // Version 30
            {6, 26, 52, 78, 104, 130, -1},

            // Version 31
            {6, 30, 56, 82, 108, 134, -1},

            // Version 32
            {6, 34, 60, 86, 112, 138, -1},

            // Version 33
            {6, 30, 58, 86, 114, 142, -1},

            // Version 34
            {6, 34, 62, 90, 118, 146, -1},

            // Version 35
            {6, 30, 54, 78, 102, 126, 150},

            // Version 36
            {6, 24, 50, 76, 102, 128, 154},

            // Version 37
            {6, 28, 54, 80, 106, 132, 158},

            // Version 38
            {6, 32, 58, 84, 110, 136, 162},

            // Version 39
            {6, 26, 54, 82, 110, 138, 166},

            // Version 40
            {6, 30, 58, 86, 114, 142, 170},
    };

    // Type info cells at the left top corner.
    private static final int[][] TYPE_INFO_COORDINATES = {
            {8, 0},
            {8, 1},
            {8, 2},
            {8, 3},
            {8, 4},
            {8, 5},
            {8, 7},
            {8, 8},
            {7, 8},
            {5, 8},
            {4, 8},
            {3, 8},
            {2, 8},
            {1, 8},
            {0, 8},
    };

    // From Appendix D in JISX0510:2004 (p. 67)
    // 1 1111 0010 0101
    private static final int VERSION_INFO_POLY = 0x1f25;

    // From Appendix C in JISX0510:2004 (p.65).
    private static final int TYPE_INFO_POLY = 0x537;
    private static final int TYPE_INFO_MASK_PATTERN = 0x5412;

    // Set all cells to -1.  -1 means that the cell is empty (not set yet).
    //
    // JAVAPORT: We shouldn't need to do this at all. The code should be rewritten to begin encoding
    // with the ByteMatrix initialized all to zero.
    public static void clearMatrix(ByteMatrix matrix) {
        matrix.clear((byte) 0xff);
    }

    /**
     * Build 2D matrix of QR Code from "dataBits" with "ecLevel", "version" and "getMaskPattern". On
     * success, store the result in "matrix".
     *
     * @param dataBits    BitVector containing the databits
     * @param ecLevel     Error correction level of the QR code (L,M,Q,H)
     * @param version     Version of the QR code, [1 .. 40]
     * @param maskPattern masking pattern
     * @param matrix      Bytematrix in which the output will be stored
     */
    public static void buildMatrix(BitVector dataBits, ErrorCorrectionLevel ecLevel, int version,
                                   int maskPattern, ByteMatrix matrix) throws WriterException {
        clearMatrix(matrix);
        embedBasicPatterns(version, matrix);
        // Type information appear with any version.
        embedTypeInfo(ecLevel, maskPattern, matrix);
        // Version info appear if version >= 7.
        maybeEmbedVersionInfo(version, matrix);
        // Data should be embedded at end.
        embedDataBits(dataBits, maskPattern, matrix);
    }

    /**
     * Embed basic patterns. On success, modify the matrix.
     * The basic patterns are:
     * - Position detection patterns
     * - Timing patterns
     * - Dark dot at the left bottom corner
     * - Position adjustment patterns, if need be
     *
     * @param version Version of the QR code, [1 .. 40]
     * @param matrix  Bytematrix in which the output will be stored
     */
    public static void embedBasicPatterns(int version, ByteMatrix matrix) throws WriterException {
        // Let's get started with embedding big squares at corners.
        embedPositionDetectionPatternsAndSeparators(matrix);
        // Then, embed the dark dot at the left bottom corner.
        embedDarkDotAtLeftBottomCorner(matrix);

        // Position adjustment patterns appear if version >= 2.
        maybeEmbedPositionAdjustmentPatterns(version, matrix);
        // Timing patterns should be embedded after position adj. patterns.
        embedTimingPatterns(matrix);
    }

    /**
     * Embed type information into the matrix
     *
     * @param ecLevel     The error correction level (L,M,Q,H)
     * @param maskPattern the masking pattern
     * @param matrix      Bytematrix in which the output will be stored
     */
    public static void embedTypeInfo(ErrorCorrectionLevel ecLevel, int maskPattern, ByteMatrix matrix)
            throws WriterException {
        BitVector typeInfoBits = new BitVector();
        makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);

        for (int i = 0; i < typeInfoBits.size(); ++i) {
            // Place bits in LSB to MSB order.  LSB (least significant bit) is the last value in
            // "typeInfoBits".
            int bit = typeInfoBits.at(typeInfoBits.size() - 1 - i);

            // Type info bits at the left top corner. See 8.9 of JISX0510:2004 (p.46).
            int x1 = TYPE_INFO_COORDINATES[i][0];
            int y1 = TYPE_INFO_COORDINATES[i][1];
            matrix.set(x1, y1, bit);

            if (i < 8) {
                // Right top corner.
                int x2 = matrix.getWidth() - i - 1;
                int y2 = 8;
                matrix.set(x2, y2, bit);
            } else {
                // Left bottom corner.
                int x2 = 8;
                int y2 = matrix.getHeight() - 7 + (i - 8);
                matrix.set(x2, y2, bit);
            }
        }
    }

    //
    //

    /**
     * Embed version information if need be.
     * For version &lt; 7, version info is not necessary
     * On success, the matrix is modified
     * See 8.10 of JISX0510:2004 (p.47) for how to embed version information.
     * @param version QR code version
     * @param matrix Byte matrix representing the QR code
     * @throws WriterException
     */
    public static void maybeEmbedVersionInfo(int version, ByteMatrix matrix) throws WriterException {

        // Version info is necessary if version >= 7.
        if (version < 7) {

            // Don't need version info.
            return;
        }
        BitVector versionInfoBits = new BitVector();
        makeVersionInfoBits(version, versionInfoBits);

        // It will decrease from 17 to 0.
        int bitIndex = 6 * 3 - 1;
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 3; ++j) {

                // Place bits in LSB (least significant bit) to MSB order.
                int bit = versionInfoBits.at(bitIndex);
                bitIndex--;

                // Left bottom corner.
                matrix.set(i, matrix.getHeight() - 11 + j, bit);

                // Right bottom corner.
                matrix.set(matrix.getHeight() - 11 + j, i, bit);
            }
        }
    }

    /**
     * Embed "dataBits" using "getMaskPattern". On success, the matrix is modified
     * For debugging purposes, it skips masking process if "getMaskPattern" is -1.
     * See 8.7 of JISX0510:2004 (p.38) for how to embed data bits.
     * @param dataBits data bits to embed in the QR code
     * @param maskPattern masking pattern to apply to the data bits
     * @param matrix Byte matrix representing the QR code
     * @throws WriterException
     */
    public static void embedDataBits(BitVector dataBits, int maskPattern, ByteMatrix matrix)
            throws WriterException {
        int bitIndex = 0;
        int direction = -1;

        // Start from the right bottom cell.
        int x = matrix.getWidth() - 1;
        int y = matrix.getHeight() - 1;
        while (x > 0) {

            // Skip the vertical timing pattern.
            if (x == 6) {
                x -= 1;
            }
            while (y >= 0 && y < matrix.getHeight()) {
                for (int i = 0; i < 2; ++i) {
                    int xx = x - i;

                    // Skip the cell if it's not empty.
                    if (!isEmpty(matrix.get(xx, y))) {
                        continue;
                    }
                    int bit;
                    if (bitIndex < dataBits.size()) {
                        bit = dataBits.at(bitIndex);
                        ++bitIndex;
                    } else {

                        // Padding bit. If there is no bit left, we'll fill the left cells with 0, as described
                        // in 8.4.9 of JISX0510:2004 (p. 24).
                        bit = 0;
                    }

                    // Skip masking if mask_pattern is -1.
                    if (maskPattern != -1) {
                        if (MaskUtil.getDataMaskBit(maskPattern, xx, y)) {
                            bit ^= 0x1;
                        }
                    }
                    matrix.set(xx, y, bit);
                }
                y += direction;
            }

            // Reverse the direction.
            direction = -direction;
            y += direction;

            // Move to the left.
            x -= 2;
        }

        // All bits should be consumed.
        if (bitIndex != dataBits.size()) {
            throw new WriterException("Not all bits consumed: " + bitIndex + '/' + dataBits.size());
        }
    }

    /**
     * Return the position of the most significant bit set (to one) in the "value".
     * The most significant bit is position 32. If there is no bit set, return 0. Examples:
     * - findMSBSet(0) => 0
     * - findMSBSet(1) => 1
     * - findMSBSet(255) => 8
     * @param value bitstring as integer
     * @return the position of the most significant bit set to 1 in the bit-representation of value
     */
    public static int findMSBSet(int value) {
        int numDigits = 0;
        while (value != 0) {
            value >>>= 1;
            ++numDigits;
        }
        return numDigits;
    }

    /**
     * Calculate BCH (Bose-Chaudhuri-Hocquenghem) code for "value" using polynomial "poly". The BCH
     * code is used for encoding type information and version information.
     * Example: Calculation of version information of 7.
     * f(x) is created from 7.
     * - 7 = 000111 in 6 bits
     * - f(x) = x^2 + x^2 + x^1
     * g(x) is given by the standard (p. 67)
     * - g(x) = x^12 + x^11 + x^10 + x^9 + x^8 + x^5 + x^2 + 1
     * Multiply f(x) by x^(18 - 6)
     * - f'(x) = f(x) * x^(18 - 6)
     * - f'(x) = x^14 + x^13 + x^12
     * Calculate the remainder of f'(x) / g(x)
     * x^2
     * __________________________________________________
     * g(x) )x^14 + x^13 + x^12
     * x^14 + x^13 + x^12 + x^11 + x^10 + x^7 + x^4 + x^2
     * --------------------------------------------------
     * x^11 + x^10 + x^7 + x^4 + x^2
     * <p>
     * The remainder is x^11 + x^10 + x^7 + x^4 + x^2
     * Encode it in binary: 110010010100
     * The return value is 0xc94 (1100 1001 0100)
     * <p>
     * Since all coefficients in the polynomials are 1 or 0, we can do the calculation by bit
     * operations. We don't care if cofficients are positive or negative.
     * @param value the bitstring to calculate the BCH Code from
     * @param poly the polynomial in GF[2^n] to use
     */
    public static int calculateBCHCode(int value, int poly) {
        // If poly is "1 1111 0010 0101" (version info poly), msbSetInPoly is 13. We'll subtract 1
        // from 13 to make it 12.
        int msbSetInPoly = findMSBSet(poly);
        value <<= msbSetInPoly - 1;
        // Do the division business using exclusive-or operations.
        while (findMSBSet(value) >= msbSetInPoly) {
            value ^= poly << (findMSBSet(value) - msbSetInPoly);
        }
        // Now the "value" is the remainder (i.e. the BCH code)
        return value;
    }

    /**
     * Make bit vector of type information. On success, store the result in "bits".
     * Encode error correction level and mask pattern. See 8.9 of JISX0510:2004 (p.45) for details.
     * @param ecLevel error correction level of the QR code
     * @param maskPattern masking pattern to use
     * @param bits Vactor of bits to contain the result
     * @throws WriterException
     */
    public static void makeTypeInfoBits(ErrorCorrectionLevel ecLevel, int maskPattern, BitVector bits)
            throws WriterException {
        if (!QRCode.isValidMaskPattern(maskPattern)) {
            throw new WriterException("Invalid mask pattern");
        }
        int typeInfo = (ecLevel.getBits() << 3) | maskPattern;
        bits.appendBits(typeInfo, 5);

        int bchCode = calculateBCHCode(typeInfo, TYPE_INFO_POLY);
        bits.appendBits(bchCode, 10);

        BitVector maskBits = new BitVector();
        maskBits.appendBits(TYPE_INFO_MASK_PATTERN, 15);
        bits.xor(maskBits);

        // Just in case.
        if (bits.size() != 15) {
            throw new WriterException("should not happen but we got: " + bits.size());
        }
    }

    //
    //

    /**
     * Make bit vector of version information. On success, store the result in "bits".
     * See 8.10 of JISX0510:2004 (p.45) for details.
     * @param version Version of the QR-code
     * @param bits Vector of bits to contain the result
     * @throws WriterException
     */
    public static void makeVersionInfoBits(int version, BitVector bits) throws WriterException {
        bits.appendBits(version, 6);
        int bchCode = calculateBCHCode(version, VERSION_INFO_POLY);
        bits.appendBits(bchCode, 12);

        // Just in case.
        if (bits.size() != 18) {
            throw new WriterException("should not happen but we got: " + bits.size());
        }
    }

    // Check if "value" is empty.
    private static boolean isEmpty(byte value) {
        return value == (byte) 0xff;
    }

    // Check if "value" is valid.
    private static boolean isValidValue(byte value) {

        // Empty.
        return (value == (byte) 0xff ||

                // Light (white).
                value == 0 ||

                // Dark (black).
                value == 1);
    }

    private static void embedTimingPatterns(ByteMatrix matrix) throws WriterException {

        // -8 is for skipping position detection patterns (size 7), and two horizontal/vertical
        // separation patterns (size 1). Thus, 8 = 7 + 1.
        for (int i = 8; i < matrix.getWidth() - 8; ++i) {
            int bit = (i + 1) % 2;

            // Horizontal line.
            if (!isValidValue(matrix.get(i, 6))) {
                throw new WriterException();
            }
            if (isEmpty(matrix.get(i, 6))) {
                matrix.set(i, 6, bit);
            }

            // Vertical line.
            if (!isValidValue(matrix.get(6, i))) {
                throw new WriterException();
            }
            if (isEmpty(matrix.get(6, i))) {
                matrix.set(6, i, bit);
            }
        }
    }

    // Embed the lonely dark dot at left bottom corner. JISX0510:2004 (p.46)
    private static void embedDarkDotAtLeftBottomCorner(ByteMatrix matrix) throws WriterException {
        if (matrix.get(8, matrix.getHeight() - 8) == 0) {
            throw new WriterException();
        }
        matrix.set(8, matrix.getHeight() - 8, 1);
    }

    private static void embedHorizontalSeparationPattern(int xStart, int yStart,
                                                         ByteMatrix matrix) throws WriterException {
        // We know the width and height.
        if (HORIZONTAL_SEPARATION_PATTERN[0].length != 8 || HORIZONTAL_SEPARATION_PATTERN.length != 1) {
            throw new WriterException("Bad horizontal separation pattern");
        }
        for (int x = 0; x < 8; ++x) {
            if (!isEmpty(matrix.get(xStart + x, yStart))) {
                throw new WriterException();
            }
            matrix.set(xStart + x, yStart, HORIZONTAL_SEPARATION_PATTERN[0][x]);
        }
    }

    private static void embedVerticalSeparationPattern(int xStart, int yStart,
                                                       ByteMatrix matrix) throws WriterException {
        // We know the width and height.
        if (VERTICAL_SEPARATION_PATTERN[0].length != 1 || VERTICAL_SEPARATION_PATTERN.length != 7) {
            throw new WriterException("Bad vertical separation pattern");
        }
        for (int y = 0; y < 7; ++y) {
            if (!isEmpty(matrix.get(xStart, yStart + y))) {
                throw new WriterException();
            }
            matrix.set(xStart, yStart + y, VERTICAL_SEPARATION_PATTERN[y][0]);
        }
    }

    // Note that we cannot unify the function with embedPositionDetectionPattern() despite they are
    // almost identical, since we cannot write a function that takes 2D arrays in different sizes in
    // C/C++. We should live with the fact.
    private static void embedPositionAdjustmentPattern(int xStart, int yStart,
                                                       ByteMatrix matrix) throws WriterException {
        // We know the width and height.
        if (POSITION_ADJUSTMENT_PATTERN[0].length != 5 || POSITION_ADJUSTMENT_PATTERN.length != 5) {
            throw new WriterException("Bad position adjustment");
        }
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                if (!isEmpty(matrix.get(xStart + x, yStart + y))) {
                    throw new WriterException();
                }
                matrix.set(xStart + x, yStart + y, POSITION_ADJUSTMENT_PATTERN[y][x]);
            }
        }
    }

    private static void embedPositionDetectionPattern(int xStart, int yStart,
                                                      ByteMatrix matrix) throws WriterException {
        // We know the width and height.
        if (POSITION_DETECTION_PATTERN[0].length != 7 || POSITION_DETECTION_PATTERN.length != 7) {
            throw new WriterException("Bad position detection pattern");
        }
        for (int y = 0; y < 7; ++y) {
            for (int x = 0; x < 7; ++x) {
                if (!isEmpty(matrix.get(xStart + x, yStart + y))) {
                    throw new WriterException();
                }
                matrix.set(xStart + x, yStart + y, POSITION_DETECTION_PATTERN[y][x]);
            }
        }
    }

    // Embed position detection patterns and surrounding vertical/horizontal separators.
    private static void embedPositionDetectionPatternsAndSeparators(ByteMatrix matrix) throws WriterException {
        // Embed three big squares at corners.
        int pdpWidth = POSITION_DETECTION_PATTERN[0].length;
        // Left top corner.
        embedPositionDetectionPattern(0, 0, matrix);
        // Right top corner.
        embedPositionDetectionPattern(matrix.getWidth() - pdpWidth, 0, matrix);
        // Left bottom corner.
        embedPositionDetectionPattern(0, matrix.getWidth() - pdpWidth, matrix);

        // Embed horizontal separation patterns around the squares.
        int hspWidth = HORIZONTAL_SEPARATION_PATTERN[0].length;
        // Left top corner.
        embedHorizontalSeparationPattern(0, hspWidth - 1, matrix);
        // Right top corner.
        embedHorizontalSeparationPattern(matrix.getWidth() - hspWidth,
                hspWidth - 1, matrix);
        // Left bottom corner.
        embedHorizontalSeparationPattern(0, matrix.getWidth() - hspWidth, matrix);

        // Embed vertical separation patterns around the squares.
        int vspSize = VERTICAL_SEPARATION_PATTERN.length;
        // Left top corner.
        embedVerticalSeparationPattern(vspSize, 0, matrix);
        // Right top corner.
        embedVerticalSeparationPattern(matrix.getHeight() - vspSize - 1, 0, matrix);
        // Left bottom corner.
        embedVerticalSeparationPattern(vspSize, matrix.getHeight() - vspSize,
                matrix);
    }

    // Embed position adjustment patterns if need be.
    private static void maybeEmbedPositionAdjustmentPatterns(int version, ByteMatrix matrix)
            throws WriterException {

        // The patterns appear if version >= 2
        if (version < 2) {
            return;
        }
        int index = version - 1;
        int[] coordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index];
        int numCoordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index].length;
        for (int i = 0; i < numCoordinates; ++i) {
            for (int j = 0; j < numCoordinates; ++j) {
                int y = coordinates[i];
                int x = coordinates[j];
                if (x == -1 || y == -1) {
                    continue;
                }
                // If the cell is unset, we embed the position adjustment pattern here.
                if (isEmpty(matrix.get(x, y))) {
                    // -2 is necessary since the x/y coordinates point to the center of the pattern, not the
                    // left top corner.
                    embedPositionAdjustmentPattern(x - 2, y - 2, matrix);
                }
            }
        }
    }

}
