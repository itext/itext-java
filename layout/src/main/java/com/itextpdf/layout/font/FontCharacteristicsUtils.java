/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontWeight;

final class FontCharacteristicsUtils {

    static FontWeight calculateFontWeight(short fw) {
        switch (fw) {
            case 100:
                return FontWeight.THIN;
            case 200:
                return FontWeight.EXTRA_LIGHT;
            case 300:
                return FontWeight.LIGHT;
            case 400:
                return FontWeight.NORMAL;
            case 500:
                return FontWeight.MEDIUM;
            case 600:
                return FontWeight.SEMI_BOLD;
            case 700:
                return FontWeight.BOLD;
            case 800:
                return FontWeight.EXTRA_BOLD;
            case 900:
                return FontWeight.BLACK;
            default:
                return FontWeight.NORMAL;
        }
    }

    static short calculateFontWeightNumber(FontWeight fw) {
        switch (fw) {
            case THIN:
                return 100;
            case EXTRA_LIGHT:
                return 200;
            case LIGHT:
                return 300;
            case NORMAL:
                return 400;
            case MEDIUM:
                return 500;
            case SEMI_BOLD:
                return 600;
            case BOLD:
                return 700;
            case EXTRA_BOLD:
                return 800;
            case BLACK:
                return 900;
            default:
                return 400;
        }
    }

    static short normalizeFontWeight(short fw) {
        fw = (short) ((fw/100)*100);
        if (fw < 100) return 100;
        if (fw > 900) return 900;
        return fw;
    }

    static short parseFontWeight(String fw) {
        if (fw == null || fw.length() == 0) {
            return -1;
        }
        fw = fw.trim().toLowerCase();
        switch (fw) {
            case "bold":
                return 700;
            case "normal":
                return 400;
            default:
                try {
                    return normalizeFontWeight((short) Integer.parseInt(fw));
                } catch (NumberFormatException ignored) {
                    return -1;
                }
        }
    }
}
