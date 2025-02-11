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

import com.itextpdf.barcodes.exceptions.WriterException;

import java.util.Map;

/**
 * This object renders a QR Code as a ByteMatrix 2D array of greyscale values.
 */
public final class QRCodeWriter {

    private static final int QUIET_ZONE_SIZE = 4;

    /**
     * Encode a string into a QR code with dimensions width x height, using error-correction level L and the smallest version for which he contents fit into the QR-code?
     * @param contents String to encode into the QR code
     * @param width width of the QR-code
     * @param height height of the QR-code
     * @return 2D Greyscale map containing the visual representation of the QR-code, stored as a Bytematrix
     * @throws WriterException exception is thrown in case an error occurs when encoding a barcode using the Writer framework
     */
    public ByteMatrix encode(String contents, int width, int height)
            throws WriterException {

        return encode(contents, width, height, null);
    }

    /**
     * Encode a string into a QR code with dimensions width x height. Hints contains suggestions for error-correction level and version.
     * The default error-correction level is L, the default version is the smallest version for which the contents will fit into the QR-code.
     * @param contents String to encode into the QR code
     * @param width width of the QR-code
     * @param height height of the QR-code
     * @param hints Map containing suggestions for error-correction level and version
     * @return 2D Greyscale map containing the visual representation of the QR-code, stored as a Bytematrix
     * @throws WriterException exception is thrown in case an error occurs when encoding a barcode using the Writer framework
     */
    public ByteMatrix encode(String contents, int width, int height,
                             Map<EncodeHintType,Object> hints) throws WriterException {

        if (contents == null || contents.length() == 0) {
            throw new IllegalArgumentException("Found empty contents");
        }

        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
                    height);
        }

        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        if (hints != null) {
            ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);


            if (requestedECLevel != null) {
                errorCorrectionLevel = requestedECLevel;
            }
        }

        QRCode code = new QRCode();
        Encoder.encode(contents, errorCorrectionLevel, hints, code);
        return renderResult(code, width, height);
    }


    // Note that the input matrix uses 0 == white, 1 == black, while the output matrix uses
    // 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
    private static ByteMatrix renderResult(QRCode code, int width, int height) {
        ByteMatrix input = code.getMatrix();
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + (QUIET_ZONE_SIZE << 1);
        int qrHeight = inputHeight + (QUIET_ZONE_SIZE << 1);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
        // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
        // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
        // handle all the padding from 100x100 (the actual QR) up to 200x160.
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

        ByteMatrix output = new ByteMatrix(outputWidth, outputHeight);
        byte[][] outputArray = output.getArray();

        // We could be tricky and use the first row in each set of multiple as the temporary storage,
        // instead of allocating this separate array.
        byte[] row = new byte[outputWidth];

        // 1. Write the white lines at the top
        for (int y = 0; y < topPadding; y++) {
            setRowColor(outputArray[y], (byte) 255);
        }

        // 2. Expand the QR image to the multiple
        byte[][] inputArray = input.getArray();
        for (int y = 0; y < inputHeight; y++) {
            // a. Write the white pixels at the left of each row
            for (int x = 0; x < leftPadding; x++) {
                row[x] = (byte) 255;
            }

            // b. Write the contents of this row of the barcode
            int offset = leftPadding;
            for (int x = 0; x < inputWidth; x++) {
                byte value = (inputArray[y][x] == 1) ? (byte) 0 : (byte) 255;
                for (int z = 0; z < multiple; z++) {
                    row[offset + z] = value;
                }
                offset += multiple;
            }

            // c. Write the white pixels at the right of each row
            offset = leftPadding + (inputWidth * multiple);
            for (int x = offset; x < outputWidth; x++) {
                row[x] = (byte) 255;
            }

            // d. Write the completed row multiple times
            offset = topPadding + (y * multiple);
            for (int z = 0; z < multiple; z++) {
                System.arraycopy(row, 0, outputArray[offset + z], 0, outputWidth);
            }
        }

        // 3. Write the white lines at the bottom
        int offset = topPadding + (inputHeight * multiple);
        for (int y = offset; y < outputHeight; y++) {
            setRowColor(outputArray[y], (byte) 255);
        }

        return output;
    }

    private static void setRowColor(byte[] row, byte value) {
        for (int x = 0; x < row.length; x++) {
            row[x] = value;
        }
    }

}

