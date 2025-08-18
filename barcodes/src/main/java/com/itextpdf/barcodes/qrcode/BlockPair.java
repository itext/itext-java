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
