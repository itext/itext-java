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

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a Character Set ECI, according to "Extended Channel Interpretations" 5.3.1.1
 * of ISO 18004.
 */
final class CharacterSetECI {

    private static Map<String,CharacterSetECI> NAME_TO_ECI;

    private static void initialize() {
        Map<String,CharacterSetECI> n = new HashMap<>(29);
        addCharacterSet(0, "Cp437", n);
        addCharacterSet(1, new String[] {"ISO8859_1", "ISO-8859-1"}, n);
        addCharacterSet(2, "Cp437", n);
        addCharacterSet(3, new String[] {"ISO8859_1", "ISO-8859-1"}, n);
        addCharacterSet(4, new String[] {"ISO8859_2", "ISO-8859-2"}, n);
        addCharacterSet(5, new String[] {"ISO8859_3", "ISO-8859-3"}, n);
        addCharacterSet(6, new String[] {"ISO8859_4", "ISO-8859-4"}, n);
        addCharacterSet(7, new String[] {"ISO8859_5", "ISO-8859-5"}, n);
        addCharacterSet(8, new String[] {"ISO8859_6", "ISO-8859-6"}, n);
        addCharacterSet(9, new String[] {"ISO8859_7", "ISO-8859-7"}, n);
        addCharacterSet(10, new String[] {"ISO8859_8", "ISO-8859-8"}, n);
        addCharacterSet(11, new String[] {"ISO8859_9", "ISO-8859-9"}, n);
        addCharacterSet(12, new String[] {"ISO8859_10", "ISO-8859-10"}, n);
        addCharacterSet(13, new String[] {"ISO8859_11", "ISO-8859-11"}, n);
        addCharacterSet(15, new String[] {"ISO8859_13", "ISO-8859-13"}, n);
        addCharacterSet(16, new String[] {"ISO8859_14", "ISO-8859-14"}, n);
        addCharacterSet(17, new String[] {"ISO8859_15", "ISO-8859-15"}, n);
        addCharacterSet(18, new String[] {"ISO8859_16", "ISO-8859-16"}, n);
        addCharacterSet(20, new String[] {"SJIS", "Shift_JIS"}, n);
        NAME_TO_ECI = n;
    }

    private final String encodingName;
    private final int value;

    private CharacterSetECI(int value, String encodingName) {
        this.encodingName = encodingName;
        this.value = value;
    }

    /**
     * @return name of the encoding.
     */
    public String getEncodingName() {
        return encodingName;
    }

    /**
     * @return the value of the encoding.
     */
    public int getValue() {
        return value;
    }

    private static void addCharacterSet(int value, String encodingName, Map<String,CharacterSetECI> n) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingName);
        n.put(encodingName, eci);
    }

    private static void addCharacterSet(int value, String[] encodingNames, Map<String,CharacterSetECI> n) {
        CharacterSetECI eci = new CharacterSetECI(value, encodingNames[0]);
        for (int i = 0; i < encodingNames.length; i++) {
            n.put(encodingNames[i], eci);
        }
    }

    /**
     * @param name character set ECI encoding name
     * @return {@link CharacterSetECI} representing ECI for character encoding, or null if it is legal
     * but unsupported
     */
    public static CharacterSetECI getCharacterSetECIByName(String name) {
        if (NAME_TO_ECI == null) {
            initialize();
        }
        return NAME_TO_ECI.get(name);
    }

}
