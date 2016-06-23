/*

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
package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

/**
 * An enum of property names that are used for graphical properties of layout
 * elements. The {@link IPropertyContainer} performs the same function as an
 * {@link java.util.Map}, with the values of {@link Property} as its potential keys.
 */
public final class Property {

    private Property() {
    }

    public static final int ACTION = 1;
    public static final int AREA_BREAK_TYPE = 2;
    public static final int AUTO_SCALE = 3;
    public static final int AUTO_SCALE_HEIGHT = 4;
    public static final int AUTO_SCALE_WIDTH = 5;
    public static final int BACKGROUND = 6;
    public static final int BASE_DIRECTION = 7;
    public static final int BOLD_SIMULATION = 8;
    public static final int BORDER = 9;
    public static final int BORDER_BOTTOM = 10;
    public static final int BORDER_LEFT = 11;
    public static final int BORDER_RIGHT = 12;
    public static final int BORDER_TOP = 13;
    public static final int BOTTOM = 14;
    public static final int CHARACTER_SPACING = 15;
    public static final int COLSPAN = 16;
    public static final int DESTINATION = 17;
    public static final int FIRST_LINE_INDENT = 18;
    public static final int FLUSH_ON_DRAW = 19;
    public static final int FONT = 20;
    public static final int FONT_COLOR = 21;
    public static final int FONT_KERNING = 22;
    public static final int FONT_SCRIPT = 23;
    public static final int FONT_SIZE = 24;
    public static final int FULL = 25;
    public static final int FORCED_PLACEMENT = 26;
    public static final int HEIGHT = 27;
    public static final int HORIZONTAL_ALIGNMENT = 28;
    /**
     * Value of 1 is equivalent to no scaling
     **/
    public static final int HORIZONTAL_SCALING = 29;
    public static final int HYPHENATION = 30;
    public static final int ITALIC_SIMULATION = 31;
    public static final int KEEP_TOGETHER = 32;
    public static final int KEEP_WITH_NEXT = 81;
    public static final int LEADING = 33;
    public static final int LEFT = 34;
    public static final int LINE_DRAWER = 35;
    public static final int LIST_START = 36;
    public static final int LIST_SYMBOL = 37;
    public static final int LIST_SYMBOL_ALIGNMENT = 38;
    public static final int LIST_SYMBOL_INDENT = 39;
    public static final int LIST_SYMBOLS_INITIALIZED = 40;
    public static final int LIST_SYMBOL_PRE_TEXT = 41;
    public static final int LIST_SYMBOL_POST_TEXT = 42;
    public static final int MARGIN_BOTTOM = 43;
    public static final int MARGIN_LEFT = 44;
    public static final int MARGIN_RIGHT = 45;
    public static final int MARGIN_TOP = 46;
    public static final int PADDING_BOTTOM = 47;
    public static final int PADDING_LEFT = 48;
    public static final int PADDING_RIGHT = 49;
    public static final int PADDING_TOP = 50;
    public static final int PAGE_NUMBER = 51;
    public static final int POSITION = 52;
    public static final int REVERSED = 53;
    public static final int RIGHT = 54;
    public static final int ROTATION_ANGLE = 55;
    public static final int ROTATION_INITIAL_HEIGHT = 56;
    public static final int ROTATION_INITIAL_WIDTH = 57;
    public static final int ROTATION_POINT_X = 58;
    public static final int ROTATION_POINT_Y = 59;
    public static final int ROWSPAN = 60;
    public static final int SPACING_RATIO = 61;
    public static final int SPLIT_CHARACTERS = 62;
    public static final int STROKE_COLOR = 63;
    public static final int STROKE_WIDTH = 64;
    public static final int SKEW = 65;
    public static final int TAB_ANCHOR = 66;
    public static final int TAB_DEFAULT = 67;
    public static final int TAB_LEADER = 68;
    public static final int TAB_STOPS = 69;
    public static final int TEXT_ALIGNMENT = 70;
    /**
     * Use values from .
     */
    public static final int TEXT_RENDERING_MODE = 71;
    public static final int TEXT_RISE = 72;
    public static final int TOP = 73;
    public static final int UNDERLINE = 74;
    /**
     * Value of 1 is equivalent to no scaling
     **/
    public static final int VERTICAL_ALIGNMENT = 75;
    public static final int VERTICAL_SCALING = 76;
    public static final int WIDTH = 77;
    public static final int WORD_SPACING = 78;
    public static final int X = 79;
    public static final int Y = 80;

    /**
     * Some properties must be passed to {@link IPropertyContainer} objects that
     * are lower in the document's hierarchy. Most inherited properties are
     * related to textual operations. Indicates whether or not this type of property is inheritable.
     */
    private static final boolean[] INHERITED_PROPERTIES;
    private static final int MAX_INHERITED_PROPERTY_ID = 80;

    static {
        INHERITED_PROPERTIES = new boolean[MAX_INHERITED_PROPERTY_ID + 1];

        INHERITED_PROPERTIES[Property.BASE_DIRECTION] = true;
        INHERITED_PROPERTIES[Property.BOLD_SIMULATION] = true;
        INHERITED_PROPERTIES[Property.CHARACTER_SPACING] = true;
        INHERITED_PROPERTIES[Property.FIRST_LINE_INDENT] = true;
        INHERITED_PROPERTIES[Property.FONT] = true;
        INHERITED_PROPERTIES[Property.FONT_COLOR] = true;
        INHERITED_PROPERTIES[Property.FONT_KERNING] = true;
        INHERITED_PROPERTIES[Property.FONT_SCRIPT] = true;
        INHERITED_PROPERTIES[Property.FONT_SIZE] = true;
        INHERITED_PROPERTIES[Property.FORCED_PLACEMENT] = true;
        INHERITED_PROPERTIES[Property.HYPHENATION] = true;
        INHERITED_PROPERTIES[Property.ITALIC_SIMULATION] = true;
        INHERITED_PROPERTIES[Property.KEEP_TOGETHER] = true;
        INHERITED_PROPERTIES[Property.LIST_SYMBOL] = true;
        INHERITED_PROPERTIES[Property.LIST_SYMBOL_PRE_TEXT] = true;
        INHERITED_PROPERTIES[Property.LIST_SYMBOL_POST_TEXT] = true;
        INHERITED_PROPERTIES[Property.SPACING_RATIO] = true;
        INHERITED_PROPERTIES[Property.SPLIT_CHARACTERS] = true;
        INHERITED_PROPERTIES[Property.STROKE_COLOR] = true;
        INHERITED_PROPERTIES[Property.STROKE_WIDTH] = true;
        INHERITED_PROPERTIES[Property.TEXT_ALIGNMENT] = true;
        INHERITED_PROPERTIES[Property.TEXT_RENDERING_MODE] = true;
        INHERITED_PROPERTIES[Property.TEXT_RISE] = true;
        INHERITED_PROPERTIES[Property.UNDERLINE] = true;
        INHERITED_PROPERTIES[Property.WORD_SPACING] = true;
    }

    public static boolean isPropertyInherited(int property) {
        return property >= 0 && property < MAX_INHERITED_PROPERTY_ID && INHERITED_PROPERTIES[property];
    }
}
