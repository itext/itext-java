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
 * See ISO 18004:2006, 6.4.1, Tables 2 and 3. This enum encapsulates the various modes in which
 * data can be encoded to bits in the QR code standard.
 */
final class Mode {

    // Not really a mode...
    public static final Mode TERMINATOR = new Mode(new int[]{0, 0, 0}, 0x00, "TERMINATOR");
    public static final Mode NUMERIC = new Mode(new int[]{10, 12, 14}, 0x01, "NUMERIC");
    public static final Mode ALPHANUMERIC = new Mode(new int[]{9, 11, 13}, 0x02, "ALPHANUMERIC");

    // Not supported
    public static final Mode STRUCTURED_APPEND = new Mode(new int[]{0, 0, 0}, 0x03, "STRUCTURED_APPEND");
    public static final Mode BYTE = new Mode(new int[]{8, 16, 16}, 0x04, "BYTE");

    // character counts don't apply
    public static final Mode ECI = new Mode(null, 0x07, "ECI");
    public static final Mode KANJI = new Mode(new int[]{8, 10, 12}, 0x08, "KANJI");
    public static final Mode FNC1_FIRST_POSITION = new Mode(null, 0x05, "FNC1_FIRST_POSITION");
    public static final Mode FNC1_SECOND_POSITION = new Mode(null, 0x09, "FNC1_SECOND_POSITION");

    private final int[] characterCountBitsForVersions;
    private final int bits;
    private final String name;

    private Mode(int[] characterCountBitsForVersions, int bits, String name) {
        this.characterCountBitsForVersions = characterCountBitsForVersions;
        this.bits = bits;
        this.name = name;
    }

    /**
     * @param bits four bits encoding a QR Code data mode
     * @return {@link Mode} encoded by these bits
     * @throws IllegalArgumentException if bits do not correspond to a known mode
     */
    public static Mode forBits(int bits) {
        switch (bits) {
            case 0x0:
                return TERMINATOR;
            case 0x1:
                return NUMERIC;
            case 0x2:
                return ALPHANUMERIC;
            case 0x3:
                return STRUCTURED_APPEND;
            case 0x4:
                return BYTE;
            case 0x5:
                return FNC1_FIRST_POSITION;
            case 0x7:
                return ECI;
            case 0x8:
                return KANJI;
            case 0x9:
                return FNC1_SECOND_POSITION;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * @param version version in question
     * @return number of bits used, in this QR Code symbol {@link Version}, to encode the
     *         count of characters that will follow encoded in this {@link Mode}
     */
    public int getCharacterCountBits(Version version) {
        if (characterCountBitsForVersions == null) {
            throw new IllegalArgumentException("Character count doesn't apply to this mode");
        }
        int number = version.getVersionNumber();
        int offset;
        if (number <= 9) {
            offset = 0;
        } else if (number <= 26) {
            offset = 1;
        } else {
            offset = 2;
        }
        return characterCountBitsForVersions[offset];
    }

    /**
     * @return the bits of the mode
     */
    public int getBits() {
        return bits;
    }

    /**
     * @return the name of the mode.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the name of the mode.
     */
    public String toString() {
        return name;
    }

}
