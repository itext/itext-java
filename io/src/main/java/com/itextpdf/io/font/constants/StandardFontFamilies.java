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
 * Class containing families for {@link StandardFonts}.
 * This class was made for com.itextpdf.io.font.FontRegisterProvider.
 */
public final class StandardFontFamilies {

    private StandardFontFamilies() {
    }

    /**
     * Font family for {@link StandardFonts#COURIER}, {@link StandardFonts#COURIER_BOLD},
     * {@link StandardFonts#COURIER_OBLIQUE} and {@link StandardFonts#COURIER_BOLDOBLIQUE}.
     */
    public static final String COURIER = "Courier";

    /**
     * Font family for {@link StandardFonts#HELVETICA}, {@link StandardFonts#HELVETICA_BOLD},
     * {@link StandardFonts#HELVETICA_OBLIQUE} and {@link StandardFonts#HELVETICA_BOLDOBLIQUE}.
     */
    public static final String HELVETICA = "Helvetica";

    /**
     * Font family for {@link StandardFonts#SYMBOL}.
     */
    public static final String SYMBOL = "Symbol";

    /**
     * Font family for {@link StandardFonts#ZAPFDINGBATS}.
     */
    public static final String ZAPFDINGBATS = "ZapfDingbats";

    /**
     * Font family for {@link StandardFonts#TIMES_ROMAN}, {@link StandardFonts#TIMES_BOLD},
     * {@link StandardFonts#TIMES_ITALIC} and {@link StandardFonts#TIMES_BOLDITALIC}.
     */
    public static final String TIMES = "Times";
}
