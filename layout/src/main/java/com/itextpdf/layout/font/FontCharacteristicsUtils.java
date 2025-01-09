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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.constants.FontWeights;

final class FontCharacteristicsUtils {

    static short normalizeFontWeight(short fw) {
        fw = (short) ((fw/100)*100);
        if (fw < FontWeights.THIN) return FontWeights.THIN;
        if (fw > FontWeights.BLACK) return FontWeights.BLACK;
        return fw;
    }

    static short parseFontWeight(String fw) {
        if (fw == null || fw.length() == 0) {
            return -1;
        }
        fw = fw.trim().toLowerCase();
        switch (fw) {
            case "bold":
                return FontWeights.BOLD;
            case "normal":
                return FontWeights.NORMAL;
            default:
                try {
                    return normalizeFontWeight((short) Integer.parseInt(fw));
                } catch (NumberFormatException ignored) {
                    return -1;
                }
        }
    }
}
