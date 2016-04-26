/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import java.util.HashSet;
import java.util.Set;

public class FontConstants {

    static final Set<String> BUILTIN_FONTS_14 = new HashSet<>();

    static {
        BUILTIN_FONTS_14.add(FontConstants.COURIER);
        BUILTIN_FONTS_14.add(FontConstants.COURIER_BOLD);
        BUILTIN_FONTS_14.add(FontConstants.COURIER_BOLDOBLIQUE);
        BUILTIN_FONTS_14.add(FontConstants.COURIER_OBLIQUE);
        BUILTIN_FONTS_14.add(FontConstants.HELVETICA);
        BUILTIN_FONTS_14.add(FontConstants.HELVETICA_BOLD);
        BUILTIN_FONTS_14.add(FontConstants.HELVETICA_BOLDOBLIQUE);
        BUILTIN_FONTS_14.add(FontConstants.HELVETICA_OBLIQUE);
        BUILTIN_FONTS_14.add(FontConstants.SYMBOL);
        BUILTIN_FONTS_14.add(FontConstants.TIMES_ROMAN);
        BUILTIN_FONTS_14.add(FontConstants.TIMES_BOLD);
        BUILTIN_FONTS_14.add(FontConstants.TIMES_BOLDITALIC);
        BUILTIN_FONTS_14.add(FontConstants.TIMES_ITALIC);
        BUILTIN_FONTS_14.add(FontConstants.ZAPFDINGBATS);
    }

    /**
     * The path to the font resources.
     */
    public static final String RESOURCE_PATH = "com/itextpdf/io/font/";

    //-Font styles------------------------------------------------------------------------------------------------------
    public static final int UNDEFINED = -1;
    /**
     * this is a possible style.
     */
    public static final int NORMAL = 0;
    /**
     * this is a possible style.
     */
    public static final int BOLD = 1;
    /**
     * this is a possible style.
     */
    public static final int ITALIC = 2;
    /**
     * this is a possible style.
     */
    public static final int UNDERLINE = 4;
    /**
     * this is a possible style.
     */
    public static final int STRIKETHRU = 8;
    /**
     * this is a possible style.
     */
    public static final int BOLDITALIC = BOLD | ITALIC;

    //-Font types-------------------------------------------------------------------------------------------------------

    /**
     * Type 1 PostScript font.
     */
    public static final int TYPE_1_FONT = 1;
    /**
     * Compact Font Format PostScript font.
     */
    public static final int TYPE_1_COMPACT_FONT = 2;
    /**
     * TrueType or OpenType with TrueType outlines font.
     */
    public static final int TRUE_TYPE_FONT = 3;
    /**
     * CIDFont Type0 (Type1 outlines).
     */
    public static final int CID_FONT_TYPE_0_FONT = 4;
    /**
     * CIDFont Type2 (TrueType outlines).
     */
    public static final int CID_FONT_TYPE_2_FONT = 5;
    /**
     * OpenType with Type1 outlines.
     */
    public static final int OPEN_TYPE_FONT = 6;

    //-Default fonts----------------------------------------------------------------------------------------------------

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

    public static final String TIMES = "Times";

    //-Font Descriptor--------------------------------------------------------------------------------------------------

    /**
     * The maximum height above the baseline reached by glyphs in this
     * font, excluding the height of glyphs for accented characters.
     */
    public static final int ASCENT = 1;
    /**
     * The y coordinate of the top of flat capital letters, measured from
     * the baseline.
     */
    public static final int CAPHEIGHT = 2;
    /**
     * The maximum depth below the baseline reached by glyphs in this
     * font. The value is a negative number.
     */
    public static final int DESCENT = 3;
    /**
     * The angle, expressed in degrees counterclockwise from the vertical,
     * of the dominant vertical strokes of the font. The value is
     * negative for fonts that slope to the right, as almost all italic fonts do.
     */
    public static final int ITALICANGLE = 4;
    /**
     * The lower left x glyph coordinate.
     */
    public static final int BBOXLLX = 5;
    /**
     * The lower left y glyph coordinate.
     */
    public static final int BBOXLLY = 6;
    /**
     * The upper right x glyph coordinate.
     */
    public static final int BBOXURX = 7;
    /**
     * The upper right y glyph coordinate.
     */
    public static final int BBOXURY = 8;
    /**
     * AWT Font property.
     */
    public static final int AWT_ASCENT = 9;
    /**
     * AWT Font property.
     */
    public static final int AWT_DESCENT = 10;
    /**
     * AWT Font property.
     */
    public static final int AWT_LEADING = 11;
    /**
     * AWT Font property.
     */
    public static final int AWT_MAXADVANCE = 12;
    /**
     * The underline position. Usually a negative value.
     */
    public static final int UNDERLINE_POSITION = 13;
    /**
     * The underline thickness.
     */
    public static final int UNDERLINE_THICKNESS = 14;
    /**
     * The strikethrough position.
     */
    public static final int STRIKETHROUGH_POSITION = 15;
    /**
     * The strikethrough thickness.
     */
    public static final int STRIKETHROUGH_THICKNESS = 16;
    /**
     * The recommended vertical size for subscripts for this font.
     */
    public static final int SUBSCRIPT_SIZE = 17;
    /**
     * The recommended vertical offset from the baseline for subscripts for this font. Usually a negative value.
     */
    public static final int SUBSCRIPT_OFFSET = 18;
    /**
     * The recommended vertical size for superscripts for this font.
     */
    public static final int SUPERSCRIPT_SIZE = 19;
    /**
     * The recommended vertical offset from the baseline for superscripts for this font.
     */
    public static final int SUPERSCRIPT_OFFSET = 20;
    /**
     * The weight class of the font, as defined by the font author.
     */
    public static final int WEIGHT_CLASS = 21;
    /**
     * The width class of the font, as defined by the font author.
     */
    public static final int WIDTH_CLASS = 22;
    /**
     * The entry of PDF FontDescriptor dictionary.
     * (Optional; PDF 1.5; strongly recommended for Type 3 fonts in Tagged PDF documents)
     * The weight (thickness) component of the fully-qualified font name or font specifier.
     * A value larger than 500 indicates bold font-weight.
     */
    public static final int FONT_WEIGHT = 23;

    //-Internal constants-----------------------------------------------------------------------------------------------

    /**
     * A not defined character in a custom PDF encoding.
     */
    public static final String notdef = ".notdef";

    public static final double[] DefaultFontMatrix = {0.001, 0, 0, 0.001, 0, 0};

    //-TrueType constants-----------------------------------------------------------------------------------------------

    protected static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    //-True Type Code pages---------------------------------------------------------------------------------------------

    /**
     * The code pages possible for a True Type font.
     */
    protected static final String[] CODE_PAGES = {
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

}
