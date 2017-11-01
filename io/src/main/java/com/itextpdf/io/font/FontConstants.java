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
package com.itextpdf.io.font;

import java.util.HashSet;
import java.util.Set;

/**
 * Font constants for {@link FontProgramFactory} and PdfFontFactory.
 */
public class FontConstants {

    //-Font styles------------------------------------------------------------------------------------------------------


    //-Default fonts----------------------------------------------------------------------------------------------------
    //TODO return and deprecate!

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

}
