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
package com.itextpdf.io.font.constants;

/**
 * The code pages possible for a True Type font.
 */
public final class TrueTypeCodePages {

    private TrueTypeCodePages() {
    }

    private static final String[] CODE_PAGES = {
            "1252 Latin 1",
            "1250 Latin 2: Eastern Europe",
            "1251 Cyrillic",
            "1253 Greek",
            "1254 Turkish",
            "1255 Hebrew",
            "1256 Arabic",
            "1257 Windows Baltic",
            "1258 Vietnamese",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "874 Thai",
            "932 JIS/Japan",
            "936 Chinese: Simplified chars--PRC and Singapore",
            "949 Korean Wansung",
            "950 Chinese: Traditional chars--Taiwan and Hong Kong",
            "1361 Korean Johab",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "Macintosh Character Set (US Roman)",
            "OEM Character Set",
            "Symbol Character Set",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "869 IBM Greek",
            "866 MS-DOS Russian",
            "865 MS-DOS Nordic",
            "864 Arabic",
            "863 MS-DOS Canadian French",
            "862 Hebrew",
            "861 MS-DOS Icelandic",
            "860 MS-DOS Portuguese",
            "857 IBM Turkish",
            "855 IBM Cyrillic; primarily Russian",
            "852 Latin 2",
            "775 MS-DOS Baltic",
            "737 Greek; former 437 G",
            "708 Arabic; ASMO 708",
            "850 WE/Latin 1",
            "437 US"
    };

    /**
     * Gets code page description based on ulCodePageRange bit settings (OS/2 table).
     * See https://www.microsoft.com/typography/unicode/ulcp.htm for more details.
     * @param bit index from ulCodePageRange bit settings (OS/2 table). From 0 to 63.
     * @return code bage description.
     */
    public static String get(int bit) {
        assert bit >= 0 && bit < 64;
        return CODE_PAGES[bit];
    }
}
