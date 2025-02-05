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
package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;

/**
 * Utility class that contains common checks used in both the  PDFA and PDFUA module for fonts.
 */
public final class FontCheckUtil {

    private FontCheckUtil(){
        // Empty constructor
    }

    /**
     * Checks the text by the passed checker and the font.
     *
     * @param text the text to check
     * @param font the font to check
     * @param checker the checker which checks the text according to the font
     *
     * @return {@code -1} if no character passes the check, or index of the first symbol which passes the check
     */
    public static int checkGlyphsOfText(String text, PdfFont font, CharacterChecker checker) {
        for (int i = 0; i < text.length(); ++i) {
            int ch;
            if (TextUtil.isSurrogatePair(text, i)) {
                ch = TextUtil.convertToUtf32(text, i);
                i++;
            } else {
                ch = text.charAt(i);
            }
            if (checker.check(ch, font)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Character checker which performs check of passed symbol against the font.
     */
    public static interface CharacterChecker {
        /**
         * Checks passed symbol against the font
         *
         * @param ch character to check
         * @param font font to check
         *
         * @return {@code true} if check passes, otherwise {@code false}
         */
        boolean check(int ch, PdfFont font);
    }
}
