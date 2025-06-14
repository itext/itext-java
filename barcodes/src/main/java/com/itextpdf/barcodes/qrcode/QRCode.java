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

/**
 * A QR code (short for "quick-response code") is a type of two-dimensional matrix barcode, invented in 1994, by
 * Japanese company Denso Wave for labelling automobile parts.[1][2] A barcode is a machine-readable optical image that
 * contains information specific to the labelled item. In practice, QR codes contain data for a locator, an identifier,
 * and web tracking. To efficiently store data, QR codes use four standardized modes of encoding (i) numeric, (ii)
 * alphanumeric, (iii) byte or binary, and (iv) kanji.[3]
 */
final class QRCode {

    public static final int NUM_MASK_PATTERNS = 8;

    private Mode mode;
    private ErrorCorrectionLevel ecLevel;
    private int version;
    private int matrixWidth;
    private int maskPattern;
    private int numTotalBytes;
    private int numDataBytes;
    private int numECBytes;
    private int numRSBlocks;
    private ByteMatrix matrix;

    /**
     * Create a QR-code object with unitialized parameters
     */
    public QRCode() {
        mode = null;
        ecLevel = null;
        version = -1;
        matrixWidth = -1;
        maskPattern = -1;
        numTotalBytes = -1;
        numDataBytes = -1;
        numECBytes = -1;
        numRSBlocks = -1;
        matrix = null;
    }
    /**
     * Mode used by the QR code to encode data into bits.
     * Possible values: TERMINATOR, NUMERIC, ALPHANUMERIC, STRUCTURED_APPEND, BYTE, ECI, KANJI, FNC1_FIRST_POSITION, FNC2_SECOND_POSITION
     * @return Mode of the QR Code.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Possible error correction level values ranked from lowest error correction capability to highest: L, M, Q, H
     * @return Error correction level of the QR Code.
     */
    public ErrorCorrectionLevel getECLevel() {
        return ecLevel;
    }

    /**
     * Together with error correction level, the version determines the information capacity of the QR code. Higher version numbers correspond with higher capacity. Ranges from 1 to 40.
     * @return Version of the QR Code.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return ByteMatrix width of the QR Code.
     */
    public int getMatrixWidth() {
        return matrixWidth;
    }

    /**
     * @return Mask pattern of the QR Code.
     */
    public int getMaskPattern() {
        return maskPattern;
    }

    /**
     * @return Number of total bytes in the QR Code.
     */
    public int getNumTotalBytes() {
        return numTotalBytes;
    }

    /**
     * @return Number of data bytes in the QR Code.
     */
    public int getNumDataBytes() {
        return numDataBytes;
    }

    /**
     * @return Number of error correction bytes in the QR Code.
     */
    public int getNumECBytes() {
        return numECBytes;
    }

    /**
     * @return Number of Reedsolomon blocks in the QR Code.
     */
    public int getNumRSBlocks() {
        return numRSBlocks;
    }

    /**
     * @return ByteMatrix data of the QR Code.
     */
    public ByteMatrix getMatrix() {
        return matrix;
    }

    /**
     * Retrieve the value of the module (cell) pointed by "x" and "y" in the matrix of the QR Code.
     * 1 represents a black cell, and 0 represents a white cell.
     * @param x width coordinate
     * @param y height coordinate
     * @return 1 for a black cell, 0 for a white cell
     */
    public int at(int x, int y) {
        // The value must be zero or one.
        int value = matrix.get(x, y);
        if (!(value == 0 || value == 1)) {
            // this is really like an assert... not sure what better exception to use?
            throw new RuntimeException("Bad value");
        }
        return value;
    }

    /**
     * Check the validity of all member variables
     * @return true if all variables are valid, false otherwise
     */
    public boolean isValid() {
        return
                // First check if all version are not uninitialized.
                mode != null &&
                        ecLevel != null &&
                        version != -1 &&
                        matrixWidth != -1 &&
                        maskPattern != -1 &&
                        numTotalBytes != -1 &&
                        numDataBytes != -1 &&
                        numECBytes != -1 &&
                        numRSBlocks != -1 &&
                        // Then check them in other ways..
                        isValidMaskPattern(maskPattern) &&
                        numTotalBytes == numDataBytes + numECBytes &&
                        // ByteMatrix stuff.
                        matrix != null &&
                        matrixWidth == matrix.getWidth() &&
                        // Must be square.
                        // See 7.3.1 of JISX0510:2004 (p.5).
                        matrix.getWidth() == matrix.getHeight();
    }

    /**
     * Prints all parameters
     * @return string containing all parameters
     */
    public String toString() {
        StringBuffer result = new StringBuffer(200);
        result.append("<<\n");
        result.append(" mode: ");
        result.append(mode);
        result.append("\n ecLevel: ");
        result.append(ecLevel);
        result.append("\n version: ");
        result.append(version);
        result.append("\n matrixWidth: ");
        result.append(matrixWidth);
        result.append("\n maskPattern: ");
        result.append(maskPattern);
        result.append("\n numTotalBytes: ");
        result.append(numTotalBytes);
        result.append("\n numDataBytes: ");
        result.append(numDataBytes);
        result.append("\n numECBytes: ");
        result.append(numECBytes);
        result.append("\n numRSBlocks: ");
        result.append(numRSBlocks);
        if (matrix == null) {
            result.append("\n matrix: null\n");
        } else {
            result.append("\n matrix:\n");
            result.append(matrix.toString());
        }
        result.append(">>\n");
        return result.toString();
    }

    /**
     * Set the data encoding mode of the QR code
     * Possible modes: TERMINATOR, NUMERIC, ALPHANUMERIC, STRUCTURED_APPEND, BYTE, ECI, KANJI, FNC1_FIRST_POSITION, FNC2_SECOND_POSITION
     * @param value new data encoding mode
     */
    public void setMode(Mode value) {
        mode = value;
    }

    /**
     * Set the error correction level of th QR code.
     * Possible error correction level values ranked from lowest error correction capability to highest: L, M, Q, H
     * @param value new error correction level
     */
    public void setECLevel(ErrorCorrectionLevel value) {
        ecLevel = value;
    }

    /**
     * Set the version of the QR code.
     * Together with error correction level, the version determines the information capacity of the QR code. Higher version numbers correspond with higher capacity.
     * Range: 1 to 40.
     * @param value the new version of the QR code
     */
    public void setVersion(int value) {
        version = value;
    }

    /**
     * Sets the width of the byte matrix
     * @param value the new width of the matrix
     */
    public void setMatrixWidth(int value) {
        matrixWidth = value;
    }

    /**
     * Set the masking pattern
     * @param value new masking pattern of the QR code
     */
    public void setMaskPattern(int value) {
        maskPattern = value;
    }

    /**
     * Set the number of total bytes
     * @param value new number of total bytes
     */
    public void setNumTotalBytes(int value) {
        numTotalBytes = value;
    }

    /**
     * Set the number of data bytes
     * @param value new number of data bytes
     */
    public void setNumDataBytes(int value) {
        numDataBytes = value;
    }

    /**
     * Set the number of error correction blocks
     * @param value new number of error correction blocks
     */
    public void setNumECBytes(int value) {
        numECBytes = value;
    }

    /**
     * Set the number of Reed-Solomon blocks
     * @param value new number of Reed-Solomon blocks
     */
    public void setNumRSBlocks(int value) {
        numRSBlocks = value;
    }

    /**
     * Set the byte-matrix
     * @param value the new byte-matrix
     */
    public void setMatrix(ByteMatrix value) {
        matrix = value;
    }

    /**
     * Check if "mask_pattern" is valid.
     * @param maskPattern masking pattern to check
     * @return true if the pattern is valid, false otherwise
     */
    public static boolean isValidMaskPattern(int maskPattern) {
        return maskPattern >= 0 && maskPattern < NUM_MASK_PATTERNS;
    }

}

