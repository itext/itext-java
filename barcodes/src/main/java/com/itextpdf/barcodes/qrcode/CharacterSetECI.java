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

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a Character Set ECI, according to "Extended Channel Interpretations" 5.3.1.1
 * of ISO 18004.
 *
 * @author Sean Owen
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
