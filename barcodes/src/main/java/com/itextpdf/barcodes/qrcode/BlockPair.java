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
 * Helper class that groups a block of databytes with its corresponding block of error correction block
 */
final class BlockPair {

    private final ByteArray dataBytes;
    private final ByteArray errorCorrectionBytes;

    BlockPair(ByteArray data, ByteArray errorCorrection) {
        dataBytes = data;
        errorCorrectionBytes = errorCorrection;
    }

    /**
     * @return data block of the pair
     */
    public ByteArray getDataBytes() {
        return dataBytes;
    }

    /**
     * @return error correction block of the pair
     */
    public ByteArray getErrorCorrectionBytes() {
        return errorCorrectionBytes;
    }

}
