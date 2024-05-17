/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.IPropertyContainer;

/**
 * An enum of property names that are used for graphical properties of layout
 * elements. The {@link IPropertyContainer} performs the same function as an
 * {@link java.util.Map}, with the values of {@link Property} as its potential keys.
 */
public final class Property {

    public static final int ACTION = 1;
    public static final int ALIGN_CONTENT = 130;
    public static final int ALIGN_ITEMS = 134;
    public static final int ALIGN_SELF = 129;

    // This property is needed for form field appearance with right-to-left text. By setting true we avoid writing
    // /ActualText and /ReversedChars to form field appearance streams because this resulted in Acrobat showing
    // an empty appearance in such cases.
    public static final int APPEARANCE_STREAM_LAYOUT = 82;

    public static final int AREA_BREAK_TYPE = 2;
    public static final int AUTO_SCALE = 3;
    public static final int AUTO_SCALE_HEIGHT = 4;
    public static final int AUTO_SCALE_WIDTH = 5;
    public static final int BACKGROUND = 6;
    public static final int BACKGROUND_IMAGE = 90;
    public static final int BASE_DIRECTION = 7;
    public static final int BOLD_SIMULATION = 8;
    public static final int BORDER = 9;
    public static final int BORDER_BOTTOM = 10;
    public static final int BORDER_BOTTOM_LEFT_RADIUS = 113;
    public static final int BORDER_BOTTOM_RIGHT_RADIUS = 112;
    public static final int BORDER_COLLAPSE = 114;
    public static final int BORDER_LEFT = 11;
    public static final int BORDER_RADIUS = 101;
    public static final int BORDER_RIGHT = 12;
    public static final int BORDER_TOP = 13;
    public static final int BORDER_TOP_LEFT_RADIUS = 110;
    public static final int BORDER_TOP_RIGHT_RADIUS = 111;
    public static final int BOTTOM = 14;
    public static final int BOX_SIZING = 105;
    public static final int CAPTION_SIDE = 119;
    public static final int CHARACTER_SPACING = 15;
    public static final int CLEAR = 100;
    public static final int COLLAPSING_MARGINS = 89;
    public static final int COLSPAN = 16;
    public static final int COLUMN_COUNT = 138;
    public static final int COLUMN_WIDTH = 142;
    public static final int COLUMN_GAP = 143;
    public static final int COLUMN_GAP_BORDER = 144;
    /**
     * Can be either destination name (id) as String or
     * a Tuple2(String, PdfDictionary) where String is destination name (id) and PdfDictionary is a dictionary of
     * goto PdfAction. This second variant allow to create structure destination in tagged pdf.
     */
    public static final int DESTINATION = 17;
    public static final int FILL_AVAILABLE_AREA = 86;
    public static final int FILL_AVAILABLE_AREA_ON_SPLIT = 87;
    public static final int FIRST_LINE_INDENT = 18;
    public static final int FLEX_BASIS = 131;
    public static final int FLEX_GROW = 132;
    public static final int FLEX_SHRINK = 127;
    public static final int FLEX_WRAP = 128;
    public static final int FLEX_DIRECTION = 139;

    public static final int FLOAT = 99;
    public static final int FLUSH_ON_DRAW = 19;

    /**
     * Font family as String or PdfFont shall be set.
     *
     * @see com.itextpdf.io.font.constants.StandardFontFamilies
     */
    public static final int FONT = 20;
    public static final int FONT_COLOR = 21;
    public static final int FONT_KERNING = 22;
    /**
     * String value. 'normal'|'italic'|'oblique'
     * Note, this property will be applied only if {@link #FONT} has String[] value.
     */
    public static final int FONT_STYLE = 94;
    /**
     * String value. 'normal'|'bold'|number
     * Note, this property will be applied only if {@link #FONT} has String[] value.
     */
    public static final int FONT_WEIGHT = 95;
    public static final int FONT_SCRIPT = 23;
    /**
     * Shall be instance of {@link com.itextpdf.layout.font.FontProvider}
     */
    public static final int FONT_PROVIDER = 91;
    /**
     * Shall be instance of {@link com.itextpdf.layout.font.FontSet}.
     */
    public static final int FONT_SET = 98;
    public static final int FONT_SIZE = 24;
    public static final int FORCED_PLACEMENT = 26;
    public static final int FULL = 25;
    public static final int GRID_COLUMN_END = 147;
    public static final int GRID_COLUMN_START = 148;
    public static final int GRID_ROW_END = 149;
    public static final int GRID_ROW_START = 150;
    public static final int GRID_TEMPLATE_COLUMNS = 145;
    public static final int GRID_TEMPLATE_ROWS = 146;
    public static final int GRID_AUTO_ROWS = 151;
    public static final int GRID_AUTO_COLUMNS = 152;
    public static final int HEIGHT = 27;
    public static final int HORIZONTAL_ALIGNMENT = 28;
    public static final int HORIZONTAL_BORDER_SPACING = 115;
    /**
     * Value of 1 is equivalent to no scaling
     **/
    public static final int HORIZONTAL_SCALING = 29;
    public static final int HYPHENATION = 30;
    public static final int ID = 126;
    public static final int IGNORE_FOOTER = 96;
    public static final int IGNORE_HEADER = 97;
    public static final int ITALIC_SIMULATION = 31;
    public static final int JUSTIFY_CONTENT = 133;
    public static final int KEEP_TOGETHER = 32;
    public static final int KEEP_WITH_NEXT = 81;
    public static final int LEADING = 33;
    public static final int LEFT = 34;
    public static final int LINE_DRAWER = 35;
    public static final int LINE_HEIGHT = 124;
    public static final int LINK_ANNOTATION = 88;
    public static final int LIST_START = 36;
    public static final int LIST_SYMBOL = 37;
    public static final int LIST_SYMBOL_ALIGNMENT = 38;
    public static final int LIST_SYMBOL_INDENT = 39;
    public static final int LIST_SYMBOL_ORDINAL_VALUE = 120;
    public static final int LIST_SYMBOL_PRE_TEXT = 41;
    public static final int LIST_SYMBOL_POSITION = 83;
    public static final int LIST_SYMBOL_POST_TEXT = 42;
    public static final int LIST_SYMBOLS_INITIALIZED = 40;
    public static final int MARGIN_BOTTOM = 43;
    public static final int MARGIN_LEFT = 44;
    public static final int MARGIN_RIGHT = 45;
    public static final int MARGIN_TOP = 46;
    public static final int MAX_HEIGHT = 84;
    public static final int MAX_WIDTH = 79;
    public static final int META_INFO = 135;
    public static final int MIN_HEIGHT = 85;
    public static final int MIN_WIDTH = 80;
    public static final int NO_SOFT_WRAP_INLINE = 118;

    public static final int OBJECT_FIT = 125;
    public static final int OPACITY = 92;
    public static final int ORPHANS_CONTROL = 121;
    public static final int OUTLINE = 106;
    public static final int OUTLINE_OFFSET = 107;
    public static final int OVERFLOW_WRAP = 102;
    public static final int OVERFLOW_X = 103;
    public static final int OVERFLOW_Y = 104;
    public static final int PADDING_BOTTOM = 47;
    public static final int PADDING_LEFT = 48;
    public static final int PADDING_RIGHT = 49;
    public static final int PADDING_TOP = 50;
    public static final int PAGE_NUMBER = 51;
    public static final int POSITION = 52;
    public static final int RENDERING_MODE = 123;
    public static final int RIGHT = 54;
    public static final int ROTATION_ANGLE = 55;
    public static final int ROTATION_INITIAL_HEIGHT = 56;
    public static final int ROTATION_INITIAL_WIDTH = 57;
    public static final int ROTATION_POINT_X = 58;
    public static final int ROTATION_POINT_Y = 59;
    public static final int ROWSPAN = 60;
    public static final int ROW_GAP = 153;
    public static final int SPACING_RATIO = 61;
    public static final int SPLIT_CHARACTERS = 62;
    public static final int STROKE_COLOR = 63;
    public static final int STROKE_WIDTH = 64;
    public static final int SKEW = 65;
    public static final int TABLE_LAYOUT = 93;
    public static final int TAB_ANCHOR = 66;
    public static final int TAB_DEFAULT = 67;
    public static final int TAB_LEADER = 68;
    public static final int TAB_STOPS = 69;
    public static final int TAGGING_HELPER = 108;
    public static final int TAGGING_HINT_KEY = 109;
    public static final int TEXT_ALIGNMENT = 70;
    /**
     * Use values from {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode}.
     */
    public static final int TEXT_RENDERING_MODE = 71;
    public static final int TEXT_RISE = 72;
    public static final int TOP = 73;
    public static final int TRANSFORM = 53;
    public static final int TYPOGRAPHY_CONFIG = 117;
    public static final int UNDERLINE = 74;
    public static final int VERTICAL_ALIGNMENT = 75;
    public static final int VERTICAL_BORDER_SPACING = 116;
    public static final int INLINE_VERTICAL_ALIGNMENT = 136;

    /**
     * Value of 1 is equivalent to no scaling
     **/
    public static final int VERTICAL_SCALING = 76;
    public static final int WIDOWS_CONTROL = 122;
    public static final int WIDTH = 77;
    public static final int WORD_SPACING = 78;
    public static final int ADD_MARKED_CONTENT_TEXT = 137;
    public static final int TREAT_AS_CONTINUOUS_CONTAINER = 140;
    public static final int TREAT_AS_CONTINUOUS_CONTAINER_RESULT = 141;

    /**
     * Some properties must be passed to {@link IPropertyContainer} objects that
     * are lower in the document's hierarchy. Most inherited properties are
     * related to textual operations. Indicates whether or not this type of property is inheritable.
     */
    private static final boolean[] INHERITED_PROPERTIES;
    private static final int MAX_INHERITED_PROPERTY_ID = 153;

    static {
        INHERITED_PROPERTIES = new boolean[MAX_INHERITED_PROPERTY_ID + 1];

        INHERITED_PROPERTIES[Property.APPEARANCE_STREAM_LAYOUT] = true;
        INHERITED_PROPERTIES[Property.BASE_DIRECTION] = true;
        INHERITED_PROPERTIES[Property.BOLD_SIMULATION] = true;
        INHERITED_PROPERTIES[Property.CAPTION_SIDE] = true;
        INHERITED_PROPERTIES[Property.CHARACTER_SPACING] = true;
        INHERITED_PROPERTIES[Property.COLLAPSING_MARGINS] = true;
        INHERITED_PROPERTIES[Property.FIRST_LINE_INDENT] = true;
        INHERITED_PROPERTIES[Property.FONT] = true;
        INHERITED_PROPERTIES[Property.FONT_COLOR] = true;
        INHERITED_PROPERTIES[Property.FONT_KERNING] = true;
        INHERITED_PROPERTIES[Property.FONT_PROVIDER] = true;
        INHERITED_PROPERTIES[Property.FONT_SET] = true;
        INHERITED_PROPERTIES[Property.FONT_SCRIPT] = true;
        INHERITED_PROPERTIES[Property.FONT_SIZE] = true;
        INHERITED_PROPERTIES[Property.FONT_STYLE] = true;
        INHERITED_PROPERTIES[Property.FONT_WEIGHT] = true;
        INHERITED_PROPERTIES[Property.FORCED_PLACEMENT] = true;
        INHERITED_PROPERTIES[Property.HYPHENATION] = true;
        INHERITED_PROPERTIES[Property.ITALIC_SIMULATION] = true;
        INHERITED_PROPERTIES[Property.KEEP_TOGETHER] = true;
        INHERITED_PROPERTIES[Property.LEADING] = true;
        INHERITED_PROPERTIES[Property.NO_SOFT_WRAP_INLINE] = true;
        INHERITED_PROPERTIES[Property.ORPHANS_CONTROL] = true;
        INHERITED_PROPERTIES[Property.SPACING_RATIO] = true;
        INHERITED_PROPERTIES[Property.SPLIT_CHARACTERS] = true;
        INHERITED_PROPERTIES[Property.STROKE_COLOR] = true;
        INHERITED_PROPERTIES[Property.STROKE_WIDTH] = true;
        INHERITED_PROPERTIES[Property.TEXT_ALIGNMENT] = true;
        INHERITED_PROPERTIES[Property.TEXT_RENDERING_MODE] = true;
        INHERITED_PROPERTIES[Property.TEXT_RISE] = true;
        INHERITED_PROPERTIES[Property.UNDERLINE] = true;
        INHERITED_PROPERTIES[Property.WIDOWS_CONTROL] = true;
        INHERITED_PROPERTIES[Property.WORD_SPACING] = true;
        INHERITED_PROPERTIES[Property.TAGGING_HELPER] = true;
        INHERITED_PROPERTIES[Property.TYPOGRAPHY_CONFIG] = true;
        INHERITED_PROPERTIES[Property.RENDERING_MODE] = true;
        INHERITED_PROPERTIES[Property.LINE_HEIGHT] = true;
        INHERITED_PROPERTIES[Property.OVERFLOW_WRAP] = true;
        INHERITED_PROPERTIES[Property.META_INFO] = true;
        INHERITED_PROPERTIES[Property.ADD_MARKED_CONTENT_TEXT] = true;
        INHERITED_PROPERTIES[Property.TREAT_AS_CONTINUOUS_CONTAINER] = true;
    }

    private Property() {
    }

    /**
     * This method checks whether a Property, in order to be picked up by the
     * rendering engine, must be defined on the current element or renderer
     * (<code>return false</code>), or may be defined in one of its parent
     * elements or renderers (<code>return true</code>).
     *
     * @param property the ID, defined in this class, of the property to check
     * @return whether the property type is inheritable
     */
    public static boolean isPropertyInherited(int property) {
        return property >= 0 && property <= MAX_INHERITED_PROPERTY_ID && INHERITED_PROPERTIES[property];
    }
}
