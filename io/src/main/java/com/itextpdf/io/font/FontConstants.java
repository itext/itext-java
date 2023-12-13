/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.io.font;

/**
 * Font constants for {@link FontProgramFactory} and PdfFontFactory.
 * @deprecated Use constants from com.itextpdf.io.font.constants.
 */
@Deprecated
public class FontConstants {

    //-Font styles------------------------------------------------------------------------------------------------------
    /**
     * Undefined font style.
     *
     * @deprecated use {@link com.itextpdf.io.font.constants.FontStyles#UNDEFINED} instead.
     */
    @Deprecated
    public static final int UNDEFINED = -1;

    /**
     * Normal font style.
     * @deprecated use {@link com.itextpdf.io.font.constants.FontStyles#NORMAL} instead.
     */
    @Deprecated
    public static final int NORMAL = 0;

    /**
     * Bold font style.
     * @deprecated use {@link com.itextpdf.io.font.constants.FontStyles#BOLD} instead.
     */
    @Deprecated
    public static final int BOLD = 1;

    /**
     * Italic font style.
     * @deprecated use {@link com.itextpdf.io.font.constants.FontStyles#ITALIC} instead.
     */
    @Deprecated
    public static final int ITALIC = 2;
    /**
     * Bold-Italic font style.
     * @deprecated use {@link com.itextpdf.io.font.constants.FontStyles#BOLDITALIC} instead.
     */
    @Deprecated
    public static final int BOLDITALIC = BOLD | ITALIC;


    //-Default fonts----------------------------------------------------------------------------------------------------

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#COURIER} instead.
     */
    @Deprecated
    public static final String COURIER = "Courier";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#COURIER_BOLD} instead.
     */
    @Deprecated
    public static final String COURIER_BOLD = "Courier-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#COURIER_OBLIQUE} instead.
     */
    @Deprecated
    public static final String COURIER_OBLIQUE = "Courier-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#COURIER_BOLDOBLIQUE} instead.
     */
    @Deprecated
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#HELVETICA} instead.
     */
    @Deprecated
    public static final String HELVETICA = "Helvetica";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#HELVETICA_BOLD} instead.
     */
    @Deprecated
    public static final String HELVETICA_BOLD = "Helvetica-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#HELVETICA_OBLIQUE} instead.
     */
    @Deprecated
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#HELVETICA_BOLDOBLIQUE} instead.
     */
    @Deprecated
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#SYMBOL} instead.
     */
    @Deprecated
    public static final String SYMBOL = "Symbol";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#TIMES_ROMAN} instead.
     */
    @Deprecated
    public static final String TIMES_ROMAN = "Times-Roman";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#TIMES_BOLD} instead.
     */
    @Deprecated
    public static final String TIMES_BOLD = "Times-Bold";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#TIMES_ITALIC} instead.
     */
    @Deprecated
    public static final String TIMES_ITALIC = "Times-Italic";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#TIMES_BOLDITALIC} instead.
     */
    @Deprecated
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";

    /**
     * This is a possible value of a base 14 type 1 font
     * @deprecated use {@link com.itextpdf.io.font.constants.StandardFonts#ZAPFDINGBATS} instead.
     */
    @Deprecated
    public static final String ZAPFDINGBATS = "ZapfDingbats";
}
