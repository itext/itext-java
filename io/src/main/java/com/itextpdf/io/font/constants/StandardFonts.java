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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class StandardFonts {

    private StandardFonts() {
    }

    private static final Set<String> BUILTIN_FONTS;

    static {
        // HashSet is required in order to autoport correctly in .Net
        HashSet<String> tempSet = new HashSet<>();
        tempSet.add(StandardFonts.COURIER);
        tempSet.add(StandardFonts.COURIER_BOLD);
        tempSet.add(StandardFonts.COURIER_BOLDOBLIQUE);
        tempSet.add(StandardFonts.COURIER_OBLIQUE);
        tempSet.add(StandardFonts.HELVETICA);
        tempSet.add(StandardFonts.HELVETICA_BOLD);
        tempSet.add(StandardFonts.HELVETICA_BOLDOBLIQUE);
        tempSet.add(StandardFonts.HELVETICA_OBLIQUE);
        tempSet.add(StandardFonts.SYMBOL);
        tempSet.add(StandardFonts.TIMES_ROMAN);
        tempSet.add(StandardFonts.TIMES_BOLD);
        tempSet.add(StandardFonts.TIMES_BOLDITALIC);
        tempSet.add(StandardFonts.TIMES_ITALIC);
        tempSet.add(StandardFonts.ZAPFDINGBATS);
        BUILTIN_FONTS = Collections.unmodifiableSet(tempSet);
    }

    public static boolean isStandardFont(String fontName) {
        return BUILTIN_FONTS.contains(fontName);
    }


    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER = "Courier";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_BOLD = "Courier-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_OBLIQUE = "Courier-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA = "Helvetica";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_BOLD = "Helvetica-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String SYMBOL = "Symbol";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_ROMAN = "Times-Roman";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_BOLD = "Times-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_ITALIC = "Times-Italic";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";

    /**
     * This is a possible value of a base 14 type 1 font
     */
    public static final String ZAPFDINGBATS = "ZapfDingbats";
}
