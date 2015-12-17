package com.itextpdf.model;

import com.itextpdf.core.color.Color;

public enum Property {

    ACTION,
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
    FONT(true),
    FONT_COLOR(true),
    FONT_KERNING(true),
    FONT_SCRIPT(true),
    FONT_SIZE(true),
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
    LIST_SYMBOL,
    LIST_SYMBOL_INDENT,
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
    TAB_ANCHOR,
    TAB_DEFAULT,
    TAB_LEADER,
    TAB_STOPS,
    TEXT_ALIGNMENT(true),
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

    public boolean isInherited() {
        return inherited;
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAlignment {
        TOP,
        MIDDLE,
        BOTTOM
    }

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFIED,
        JUSTIFIED_ALL
    }

    public static final class TextRenderingMode {
        private TextRenderingMode() {}

        public static final int TEXT_RENDERING_MODE_FILL = 0;
        public static final int TEXT_RENDERING_MODE_STROKE = 1;
        public static final int TEXT_RENDERING_MODE_FILL_STROKE = 2;
        public static final int TEXT_RENDERING_MODE_INVISIBLE = 3;
        public static final int TEXT_RENDERING_MODE_FILL_CLIP = 4;
        public static final int TEXT_RENDERING_MODE_STROKE_CLIP = 5;
        public static final int TEXT_RENDERING_MODE_FILL_STROKE_CLIP = 6;
        public static final int TEXT_RENDERING_MODE_CLIP = 7;
    }

    public static class Background {
        protected Color color;
        protected float extraLeft;
        protected float extraRight;
        protected float extraTop;
        protected float extraBottom;

        public Background(Color color) {
            this(color, 0, 0, 0, 0);
        }

        public Background(Color color, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
            this.color = color;
            this.extraLeft = extraLeft;
            this.extraRight = extraRight;
            this.extraTop = extraTop;
            this.extraBottom = extraBottom;
        }

        public Color getColor() {
            return color;
        }

        public float getExtraLeft() {
            return extraLeft;
        }

        public float getExtraRight() {
            return extraRight;
        }

        public float getExtraTop() {
            return extraTop;
        }

        public float getExtraBottom() {
            return extraBottom;
        }
    }

    public static class Leading {
        public static final int FIXED = 1;
        public static final int MULTIPLIED = 2;

        protected int type;
        protected float value;

        public Leading(int type, float value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return type;
        }

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

        public float getyPositionMul() {
            return yPositionMul;
        }
    }

    public static class UnitValue {
        public static final int POINT = 1;
        public static final int PERCENT = 2;

        protected int unitType;
        protected float value;

        public UnitValue(int unitType, float value) {
            this.unitType = unitType;
            this.value = value;
        }

        public static UnitValue createPointValue(float value) {
            return new UnitValue(POINT, value);
        }

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
    }

    public enum ListNumberingType {
        DECIMAL,
        ROMAN_LOWER,
        ROMAN_UPPER,
        ENGLISH_LOWER,
        ENGLISH_UPPER,
        GREEK_LOWER,
        GREEK_UPPER,
        // Zapfdingbats font characters in range [172; 181]
        ZAPF_DINGBATS_1,
        // Zapfdingbats font characters in range [182; 191]
        ZAPF_DINGBATS_2,
        // Zapfdingbats font characters in range [192; 201]
        ZAPF_DINGBATS_3,
        // Zapfdingbats font characters in range [202; 221]
        ZAPF_DINGBATS_4
    }

    public enum TabAlignment {
        LEFT,
        RIGHT,
        CENTER,
        ANCHOR
    }

    // TODO boolean?
    public enum FontKerning {
        YES,
        NO
    }

    public enum BaseDirection {
        NO_BIDI,
        DEFAULT_BIDI,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

}
