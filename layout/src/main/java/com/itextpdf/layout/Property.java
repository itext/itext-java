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
package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;

/**
 * An enum of property names that are used for graphical properties of layout
 * elements. The {@link IPropertyContainer} performs the same function as an
 * {@link EnumMap}, with the values of {@link Property} as its potential keys.
 */
public enum Property {

    ACTION,
    AREA_BREAK_TYPE,
    AUTO_SCALE,
    AUTO_SCALE_HEIGHT,
    AUTO_SCALE_WIDTH,
    BACKGROUND,
    BASE_DIRECTION(true),
    BOLD_SIMULATION(true),
    BORDER,
    BORDER_BOTTOM,
    BORDER_LEFT,
    BORDER_RIGHT,
    BORDER_TOP,
    BOTTOM,
    CHARACTER_SPACING(true),
    COLSPAN,
    DESTINATION,
    FIRST_LINE_INDENT(true),
    FLUSH_ON_DRAW,
    FONT(true),
    FONT_COLOR(true),
    FONT_KERNING(true),
    FONT_SCRIPT(true),
    FONT_SIZE(true),
    FULL,
    FORCED_PLACEMENT(true),
    HEIGHT,
    HORIZONTAL_ALIGNMENT,
    /**
     * Value of 1 is equivalent to no scaling
     **/
    HORIZONTAL_SCALING,
    HYPHENATION(true),
    ITALIC_SIMULATION(true),
    KEEP_TOGETHER(true),
    LEADING,
    LEFT,
    LINE_DRAWER,
    LIST_START,
    LIST_SYMBOL(true),
    LIST_SYMBOL_ALIGNMENT,
    LIST_SYMBOL_INDENT,
    LIST_SYMBOL_PRE_TEXT(true),
    LIST_SYMBOL_POST_TEXT(true),
    MARGIN_BOTTOM,
    MARGIN_LEFT,
    MARGIN_RIGHT,
    MARGIN_TOP,
    PADDING_BOTTOM,
    PADDING_LEFT,
    PADDING_RIGHT,
    PADDING_TOP,
    PAGE_NUMBER,
    POSITION,
    REVERSED,
    RIGHT,
    ROTATION_ANGLE,
    ROTATION_INITIAL_HEIGHT,
    ROTATION_INITIAL_WIDTH,
    ROTATION_POINT_X,
    ROTATION_POINT_Y,
    ROWSPAN,
    SPACING_RATIO(true),
    SPLIT_CHARACTERS(true),
    STROKE_COLOR(true),
    STROKE_WIDTH(true),
    SKEW,
    TAB_ANCHOR,
    TAB_DEFAULT,
    TAB_LEADER,
    TAB_STOPS,
    TEXT_ALIGNMENT(true),
    /**
     * Use values from .
     */
    TEXT_RENDERING_MODE(true),
    TEXT_RISE(true),
    TOP,
    UNDERLINE(true),
    /**
     * Value of 1 is equivalent to no scaling
     **/
    VERTICAL_ALIGNMENT,
    VERTICAL_SCALING,
    WIDTH,
    WORD_SPACING(true),
    X,
    Y;


    private boolean inherited;

    Property() {
        this.inherited = false;
    }

    Property(boolean inherited) {
        this.inherited = inherited;
    }

    /**
     * Some properties must be passed to {@link IPropertyContainer} objects that
     * are lower in the document's hierarchy. Most inherited properties are
     * related to textual operations.
     * 
     * @return whether or not this type of property is inheritable.
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * A specialized enum containing potential property values for {@link
     * Property#HORIZONTAL_ALIGNMENT}.
     */
    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * A specialized enum containing potential property values for {@link
     * Property#VERTICAL_ALIGNMENT}.
     */
    public enum VerticalAlignment {
        TOP,
        MIDDLE,
        BOTTOM
    }

    /**
     * A specialized enum containing potential property values for {@link
     * Property#TEXT_ALIGNMENT}.
     */
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFIED,
        JUSTIFIED_ALL
    }

    /**
     * A specialized enum containing alignment properties for list symbols.
     * {@link ListSymbolAlignment#LEFT} means that the items will be aligned as follows:
     * 9.  Item 9
     * 10. Item 10
     *
     * Whereas {@link ListSymbolAlignment#RIGHT} means the items will be aligned as follows:
     *  9. Item 9
     * 10. Item 10
     */
    public enum ListSymbolAlignment {
        RIGHT,
        LEFT
    }

    /**
     * A specialized class holding configurable properties related to an {@link
     * Element}'s background. This class is meant to be used as the value for the
     * {@link Property#BACKGROUND} key in an {@link IPropertyContainer}. Allows
     * to define a background color, and positive or negative changes to the
     * location of the edges of the background coloring.
     */
    public static class Background {
        protected Color color;
        protected float extraLeft;
        protected float extraRight;
        protected float extraTop;
        protected float extraBottom;

        /**
         * Creates a background with a specified color.
         * @param color the background color
         */
        public Background(Color color) {
            this(color, 0, 0, 0, 0);
        }

        /**
         * Creates a background with a specified color, and extra space that
         * must be counted as part of the background and therefore colored.
         * These values are allowed to be negative.
         * @param color the background color
         * @param extraLeft extra coloring to the left side
         * @param extraTop extra coloring at the top
         * @param extraRight extra coloring to the right side
         * @param extraBottom extra coloring at the bottom
         */
        public Background(Color color, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
            this.color = color;
            this.extraLeft = extraLeft;
            this.extraRight = extraRight;
            this.extraTop = extraTop;
            this.extraBottom = extraBottom;
        }

        /**
         * Gets the background's color.
         * @return a {@link Color} of any supported kind
         */
        public Color getColor() {
            return color;
        }

        /**
         * Gets the extra space that must be filled to the left of the Element.
         * @return a float value
         */
        public float getExtraLeft() {
            return extraLeft;
        }

        /**
         * Gets the extra space that must be filled to the right of the Element.
         * @return a float value
         */
        public float getExtraRight() {
            return extraRight;
        }

        /**
         * Gets the extra space that must be filled at the top of the Element.
         * @return a float value
         */
        public float getExtraTop() {
            return extraTop;
        }

        /**
         * Gets the extra space that must be filled at the bottom of the Element.
         * @return a float value
         */
        public float getExtraBottom() {
            return extraBottom;
        }
    }

    /**
     * A specialized class that specifies the leading, "the vertical distance between
     * the baselines of adjacent lines of text" (ISO-32000-1, section 9.3.5).
     * Allows to use either an absolute (constant) leading value, or one
     * determined by font size. Pronounce as 'ledding' (cfr. Led Zeppelin).
     * 
     * This class is meant to be used as the value for the
     * {@link Property#LEADING} key in an {@link IPropertyContainer}.
     */
    public static class Leading {
        /**
         * A leading type independent of font size.
         */
        public static final int FIXED = 1;
        
        /**
         * A leading type related to the font size and the resulting bounding box.
         */
        public static final int MULTIPLIED = 2;

        protected int type;
        protected float value;

        /**
         * Creates a Leading object.
         * 
         * @param type a constant type that defines the calculation of actual
         * leading distance. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
         * @param value to be used as a basis for the leading calculation.
         */
        public Leading(int type, float value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Gets the calculation type of the Leading object.
         * 
         * @return the calculation type. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
         */
        public int getType() {
            return type;
        }

        /**
         * Gets the value to be used as the basis for the leading calculation.
         * @return a calculation value
         */
        public float getValue() {
            return value;
        }
    }

    public static class Underline {
        protected Color color;
        protected float thickness;
        protected float thicknessMul;
        protected float yPosition;
        protected float yPositionMul;
        protected int lineCapStyle;
        
        public Underline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
            this.color = color;
            this.thickness = thickness;
            this.thicknessMul = thicknessMul;
            this.yPosition = yPosition;
            this.yPositionMul = yPositionMul;
            this.lineCapStyle = lineCapStyle;
        }

        public Color getColor() {
            return color;
        }

        public float getThickness(float fontSize) {
            return thickness + thicknessMul * fontSize;
        }

        public float getYPosition(float fontSize) {
            return yPosition + yPositionMul * fontSize;
        }

        public float getYPositionMul() {
            return yPositionMul;
        }
    }

    /**
     * A specialized class that holds a value and the unit it is measured in.
     */
    public static class UnitValue {
        public static final int POINT = 1;
        public static final int PERCENT = 2;

        protected int unitType;
        protected float value;

        /**
         * Creates a UnitValue object with a specified type and value.
         * @param unitType either {@link UnitValue#POINT} or a {@link UnitValue#PERCENT}
         * @param value the value to be stored.
         */
        public UnitValue(int unitType, float value) {
            this.unitType = unitType;
            this.value = value;
        }

        /**
         * Creates a UnitValue POINT object with a specified value.
         * @param value the value to be stored.
         * @return a new {@link UnitValue#POINT} {@link UnitValue}
         */
        public static UnitValue createPointValue(float value) {
            return new UnitValue(POINT, value);
        }

        /**
         * Creates a UnitValue PERCENT object with a specified value.
         * @param value the value to be stored.
         * @return a new {@link UnitValue#PERCENT} {@link UnitValue}
         */
        public static UnitValue createPercentValue(float value) {
            return new UnitValue(PERCENT, value);
        }

        public int getUnitType() {
            return unitType;
        }

        public void setUnitType(int unitType) {
            this.unitType = unitType;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public boolean isPointValue() {
            return unitType == POINT;
        }

        public boolean isPercentValue() {
            return unitType == PERCENT;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UnitValue)) {
                return false;
            }
            UnitValue other = (UnitValue) obj;
            return Integer.compare(unitType, other.unitType) == 0 && Float.compare(value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.unitType;
            hash = 71 * hash + Float.floatToIntBits(this.value);
            return hash;
        }
    }

    /**
     * A specialized enum holding the possible values for a list {@link
     * com.itextpdf.layout.element.List}'s entry prefix. This class is meant to
     * be used as the value for the {@link Property#LIST_SYMBOL} key in an
     * {@link IPropertyContainer}.
     */
    public enum ListNumberingType {
        DECIMAL,
        ROMAN_LOWER,
        ROMAN_UPPER,
        ENGLISH_LOWER,
        ENGLISH_UPPER,
        GREEK_LOWER,
        GREEK_UPPER,
        /** Zapfdingbats font characters in range [172; 181] */
        ZAPF_DINGBATS_1,
        /** Zapfdingbats font characters in range [182; 191] */
        ZAPF_DINGBATS_2,
        /** Zapfdingbats font characters in range [192; 201] */
        ZAPF_DINGBATS_3,
        /** Zapfdingbats font characters in range [202; 221] */
        ZAPF_DINGBATS_4
    }

    /**
     * A specialized enum holding the possible values for a {@link
     * com.itextpdf.layout.element.List List}'s entry prefix. This class is meant
     * to be used as the value for the {@link Property#LIST_SYMBOL} key in an
     * {@link IPropertyContainer}.
     */
    public enum TabAlignment {
        LEFT,
        RIGHT,
        CENTER,
        ANCHOR
    }

    /**
     * A specialized enum holding the possible values for a text {@link
     * Element}'s kerning property. This class is meant to
     * be used as the value for the {@link Property#FONT_KERNING} key in an
     * {@link IPropertyContainer}.
     */
    public enum FontKerning {
        YES,
        NO
    }

    /**
     * A specialized enum holding the possible values for a text {@link
     * Element}'s base direction. This class is meant to
     * be used as the value for the {@link Property#BASE_DIRECTION} key in an
     * {@link IPropertyContainer}.
     */
    public enum BaseDirection {
        NO_BIDI,
        DEFAULT_BIDI,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public enum AreaBreakType {
        NEW_AREA,
        NEW_PAGE,
        LAST_PAGE
    }

}
