/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
