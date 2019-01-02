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
