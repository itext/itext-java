package com.itextpdf.layout;

import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.splitting.ISplitCharacters;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A generic abstract element that fits in a PDF layout object hierarchy.
 * A superclass of all {@link IElement layout object} implementations.
 * 
 * @param <Type> this type
 */
public abstract class ElementPropertyContainer<Type extends ElementPropertyContainer> implements IPropertyContainer<Type> {

    protected Map<Property, Object> properties = new EnumMap<>(Property.class);

    @Override
    public <T extends Type> T setProperty(Property property, Object value) {
        properties.put(property, value);
        return (T) this;
    }

    @Override
    public boolean hasProperty(Property property) {
        return hasOwnProperty(property);
    }

    @Override
    public boolean hasOwnProperty(Property property) {
        return properties.containsKey(property);
    }

    @Override
    public void deleteOwnProperty(Property property) {
        properties.remove(property);
    }

    @Override
    public <T> T getProperty(Property property) {
        return getOwnProperty(property);
    }

    @Override
    public <T> T getOwnProperty(Property property) {
        return (T) properties.get(property);
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case MARGIN_TOP:
            case MARGIN_RIGHT:
            case MARGIN_BOTTOM:
            case MARGIN_LEFT:
            case PADDING_TOP:
            case PADDING_RIGHT:
            case PADDING_BOTTOM:
            case PADDING_LEFT:
                return (T) Float.valueOf(0);
            case POSITION:
                return (T)Integer.valueOf(LayoutPosition.STATIC);
            case FORCED_PLACEMENT:
                return (T) Boolean.FALSE;
            default:
                return null;
        }
    }

    /**
     * Gets the width property of the Element.
     * 
     * @return the width of the element, with a value and a measurement unit.
     * @see Property.UnitValue
     */
    public Property.UnitValue getWidth() {
        return getProperty(Property.WIDTH);
    }

    /**
     * Sets the width property of the Element, measured in points.
     * 
     * @param width a value measured in points.
     * @return this Element.
     */
    public Type setWidth(float width) {
        return setProperty(Property.WIDTH, Property.UnitValue.createPointValue(width));
    }

    /**
     * Sets the width property of the Element, measured in percentage.
     * 
     * @param widthPercent a value measured in percentage.
     * @return this Element.
     */
    public Type setWidthPercent(float widthPercent) {
        return setProperty(Property.WIDTH, Property.UnitValue.createPercentValue(widthPercent));
    }

    /**
     * Sets the width property of the Element with a {@link com.itextpdf.layout.Property.UnitValue}.
     * 
     * @param width a {@link com.itextpdf.layout.Property.UnitValue} object
     * @return this Element.
     */
    public Type setWidth(Property.UnitValue width) {
        return setProperty(Property.WIDTH, width);
    }
    
    /**
     * Gets the height property of the Element.
     * 
     * @return the height of the element, as a floating point value.
     */
    public Float getHeight() {
        return getProperty(Property.HEIGHT);
    }

    /**
     * Sets the height property of the Element.
     * 
     * @param height a floating point value for the new height
     * @return this Element.
     */
    public Type setHeight(float height) {
        return setProperty(Property.HEIGHT, height);
    }

    /**
     * Sets values for a relative repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to 
     * {@link LayoutPosition#RELATIVE relative}.
     * 
     * The default implementation in {@link AbstractRenderer} treats
     * <code>left</code> and <code>top</code> as the most important values. Only
     * if <code>left == 0</code> will <code>right</code> be used for the
     * calculation; ditto for top vs. bottom.
     * 
     * @param left movement to the left
     * @param top movement upwards on the page
     * @param right movement to the right
     * @param bottom movement downwards on the page
     * @return this Element.
     * @see LayoutPosition#RELATIVE
     */
    public Type setRelativePosition(float left, float top, float right, float bottom) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.RELATIVE).
                setProperty(Property.LEFT, left).
                setProperty(Property.RIGHT, right).
                setProperty(Property.TOP, top).
                setProperty(Property.BOTTOM, bottom);
    }

    /**
     * Sets values for a absolute repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to 
     * {@link LayoutPosition#FIXED fixed}.
     * 
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @param width a floating point value measured in points.
     * @return this Element.
     */
    public Type setFixedPosition(float x, float y, float width) {
        return setFixedPosition(x, y, Property.UnitValue.createPointValue(width));
    }

    /**
     * Sets values for a absolute repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to 
     * {@link LayoutPosition#FIXED fixed}.
     * 
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @param width a {@link com.itextpdf.layout.Property.UnitValue}
     * @return this Element.
     */
    public Type setFixedPosition(float x, float y, Property.UnitValue width) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.FIXED).
                setProperty(Property.X, x).
                setProperty(Property.Y, y).
                setProperty(Property.WIDTH, width);
    }

    /**
     * Sets values for a absolute repositioning of the Element, on a specific
     * page. Also has as a side effect that the Element's {@link
     * Property#POSITION} is changed to {@link LayoutPosition#FIXED fixed}.
     * 
     * @param pageNumber the page where the element must be positioned
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @param width a floating point value measured in points.
     * @return this Element.
     */
    public Type setFixedPosition(int pageNumber, float x, float y, float width) {
        return (Type) setFixedPosition(x, y, width).
                setProperty(Property.PAGE_NUMBER, pageNumber);
    }

    /**
     * Sets values for a absolute repositioning of the Element, on a specific
     * page. Also has as a side effect that the Element's {@link
     * Property#POSITION} is changed to {@link LayoutPosition#FIXED fixed}.
     * 
     * @param pageNumber the page where the element must be positioned
     * @param x horizontal position on the page
     * @param y vertical position on the page
     * @param width a floating point value measured in points.
     * @return this Element.
     */
    public Type setFixedPosition(int pageNumber, float x, float y, Property.UnitValue width) {
        return (Type) setFixedPosition(x, y, width).
                setProperty(Property.PAGE_NUMBER, pageNumber);
    }

//    public Type setAbsolutePosition(float x, float y) {
//        return (Type) setProperty(Property.POSITION, LayoutPosition.ABSOLUTE).
//            setProperty(Property.X, x).
//            setProperty(Property.Y, y);
//    }

    /**
     * Sets the horizontal alignment of this Element.
     * 
     * @param horizontalAlignment an enum value of type {@link com.itextpdf.layout.Property.HorizontalAlignment}
     * @return this Element.
     */
    public Type setHorizontalAlignment(Property.HorizontalAlignment horizontalAlignment) {
        return setProperty(Property.HORIZONTAL_ALIGNMENT, horizontalAlignment);
    }

    /**
     * Sets the font of this Element.
     * 
     * @param font a {@link PdfFont font program}
     * @return this Element.
     */
    public Type setFont(PdfFont font) {
        return setProperty(Property.FONT, font);
    }

    /**
     * Sets the font color of this Element.
     * 
     * @param fontColor a {@link Color} for the text in this Element.
     * @return this Element.
     */
    public Type setFontColor(Color fontColor) {
        return setProperty(Property.FONT_COLOR, fontColor);
    }

    /**
     * Sets the font size of this Element.
     * 
     * @param fontSize a floating point value
     * @return this Element.
     */
    public Type setFontSize(float fontSize) {
        return setProperty(Property.FONT_SIZE, fontSize);
    }

    /**
     * Sets the font size of this Element.
     * 
     * @param alignment an enum value of type {@link com.itextpdf.layout.Property.TextAlignment}
     * @return this Element.
     */
    public Type setTextAlignment(Property.TextAlignment alignment) {
        return setProperty(Property.TEXT_ALIGNMENT, alignment);
    }

    /**
     * Defines a custom spacing distance between all characters of a textual element.
     * The character-spacing parameter is added to the glyph’s horizontal or vertical displacement (depending on the writing mode).
     * 
     * @param charSpacing a floating point value
     * @return this Element.
     */
    public Type setCharacterSpacing(float charSpacing) {
        return setProperty(Property.CHARACTER_SPACING, charSpacing);
    }

    /**
     * Defines a custom spacing distance between words of a textual element.
     * This value works exactly like the character spacing, but only kicks in at word boundaries.
     * 
     * @param wordSpacing a floating point value
     * @return this Element.
     */
    public Type setWordSpacing(float wordSpacing) {
        return setProperty(Property.WORD_SPACING, wordSpacing);
    }

    /**
     * Enable or disable kerning.
     * Some fonts may specify kern pairs, i.e. pair of glyphs, between which the amount of horizontal space is adjusted.
     * This adjustment is typically negative, e.g. in "AV" pair the glyphs will typically be moved closer to each other.
     * 
     * @param fontKerning an enum value as a boolean wrapper specifying whether or not to apply kerning
     * @return this Element.
     */
    public Type setFontKerning(Property.FontKerning fontKerning) {
        return setProperty(Property.FONT_KERNING, fontKerning);
    }

    /**
     * Specifies a background color for the Element.
     * 
     * @param backgroundColor the background color
     * @return this Element.
     */
    public Type setBackgroundColor(Color backgroundColor) {
        return setBackgroundColor(backgroundColor, 0, 0, 0, 0);
    }

    /**
     * Specifies a background color for the Element, and extra space that
     * must be counted as part of the background and therefore colored.
     * 
     * @param backgroundColor the background color
     * @param extraLeft extra coloring to the left side
     * @param extraTop extra coloring at the top
     * @param extraRight extra coloring to the right side
     * @param extraBottom extra coloring at the bottom
     * @return this Element.
     */
    public Type setBackgroundColor(Color backgroundColor, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
        return setProperty(Property.BACKGROUND, new Property.Background(backgroundColor, extraLeft, extraTop, extraRight, extraBottom));
    }

    /**
     * Sets a border for all four edges of this Element with customizable color, width, pattern type.
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public Type setBorder(Border border) {
        return setProperty(Property.BORDER, border);
    }

    /**
     * Sets a border for the upper limit of this Element with customizable color, width, pattern type.
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public Type setBorderTop(Border border) {
        return setProperty(Property.BORDER_TOP, border);
    }

    /**
     * Sets a border for the right limit of this Element with customizable color, width, pattern type.
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public Type setBorderRight(Border border) {
        return setProperty(Property.BORDER_RIGHT, border);
    }

    /**
     * Sets a border for the bottom limit of this Element with customizable color, width, pattern type.
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public Type setBorderBottom(Border border) {
        return setProperty(Property.BORDER_BOTTOM, border);
    }

    /**
     * Sets a border for the left limit of this Element with customizable color, width, pattern type.
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public Type setBorderLeft(Border border) {
        return setProperty(Property.BORDER_LEFT, border);
    }

    /**
     * Sets a rule for splitting strings when they don't fit into one line.
     * The default implementation is {@link com.itextpdf.layout.splitting.DefaultSplitCharacters}
     * @param splitCharacters an implementation of {@link ISplitCharacters}
     * @return this Element.
     */
    public Type setSplitCharacters(ISplitCharacters splitCharacters) {
        return setProperty(Property.SPLIT_CHARACTERS, splitCharacters);
    }

    /**
     * Gets a rule for splitting strings when they don't fit into one line.
     * @return the current string splitting rule, an implementation of {@link ISplitCharacters}
     */
    public ISplitCharacters getSplitCharacters() {
        return getProperty(Property.SPLIT_CHARACTERS);
    }

    /**
     * Gets the text rendering mode, a variable that determines whether showing
     * text causes glyph outlines to be stroked, filled, used as a clipping
     * boundary, or some combination of the three.
     * @return the current text rendering mode
     * @see com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode
     */
    public Integer getTextRenderingMode() {
        return getProperty(Property.TEXT_RENDERING_MODE);
    }

    /**
     * Sets the text rendering mode, a variable that determines whether showing
     * text causes glyph outlines to be stroked, filled, used as a clipping
     * boundary, or some combination of the three.
     * @param textRenderingMode an <code>int</code> value
     * @return this Element.
     * @see com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode
     */
    public Type setTextRenderingMode(int textRenderingMode) {
        return setProperty(Property.TEXT_RENDERING_MODE, textRenderingMode);
    }

    /**
     * Gets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     * 
     * @return the current stroke color
     */
    public Color getStrokeColor() {
        return getProperty(Property.STROKE_COLOR);
    }

    /**
     * Sets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     * 
     * @param strokeColor a new stroke color
     * @return this Element.
     */
    public Type setStrokeColor(Color strokeColor) {
        return setProperty(Property.STROKE_COLOR, strokeColor);
    }

    /**
     * Gets the stroke width for the current element.
     * The stroke width is the width of the outlines or edges of a shape.
     * 
     * @return the current stroke width
     */
    public Float getStrokeWidth() {
        return getProperty(Property.STROKE_WIDTH);
    }

    /**
     * Sets the stroke width for the current element.
     * The stroke width is the width of the outlines or edges of a shape.
     * 
     * @param strokeWidth a new stroke width
     * @return this Element.
     */
    public Type setStrokeWidth(float strokeWidth) {
        return setProperty(Property.STROKE_WIDTH, strokeWidth);
    }

    /**
     * Switch on the simulation of bold style for a font.
     * Be aware that using correct bold font is highly preferred over this option.
     *
     * @return this element
     */
    public Type setBold() {
        return setProperty(Property.BOLD_SIMULATION, true);
    }

    /**
     * Switch on the simulation of italic style for a font.
     * Be aware that using correct italic (oblique) font is highly preferred over this option.
     *
     * @return this element
     */
    public Type setItalic() {
        return setProperty(Property.ITALIC_SIMULATION, true);
    }

    /**
     * Sets default line-through attributes for text.
     * See {@link #setUnderline(Color, float, float, float, float, int)} for more fine tuning.
     *
     * @return this element
     */
    public Type setLineThrough() {
        // 7/24 is the average between default browser behavior(1/4) and iText5 behavior(1/3)
        return setUnderline(null, .75f, 0, 0, 7/24f, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets default underline attributes for text.
     * See other overloads for more fine tuning.
     *
     * @return this element
     */
    public Type setUnderline() {
        return setUnderline(null, .75f, 0, 0, -1/8f, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets an horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically and has always the text width.
     * Multiple call to this method will produce multiple lines.
     *
     * @param thickness
     *            the absolute thickness of the line
     * @param yPosition
     *            the absolute y position relative to the baseline
     * @return this element
     */
    public Type setUnderline(float thickness, float yPosition) {
        return setUnderline(null, thickness, 0, yPosition, 0, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets an horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically due to position parameter.
     * Multiple call to this method will produce multiple lines.
     *
     * The thickness of the line will be {@code thickness + thicknessMul * fontSize}.
     * The position of the line will be {@code baseLine + yPosition + yPositionMul * fontSize}.
     *
     * @param color
     *            the color of the line or <CODE>null</CODE> to follow the
     *            text color
     * @param thickness
     *            the absolute thickness of the line
     * @param thicknessMul
     *            the thickness multiplication factor with the font size
     * @param yPosition
     *            the absolute y position relative to the baseline
     * @param yPositionMul
     *            the position multiplication factor with the font size
     * @param lineCapStyle
     *            the end line cap style. Allowed values are enumerated in
     *            {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle}
     * @return this element
     */
    public Type setUnderline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        Property.Underline newUnderline = new Property.Underline(color, thickness, thicknessMul, yPosition, yPositionMul, lineCapStyle);
        Object currentProperty = getProperty(Property.UNDERLINE);
        if (currentProperty instanceof List) {
            ((List) currentProperty).add(newUnderline);
        } else if (currentProperty instanceof Property.Underline) {
            setProperty(Property.UNDERLINE, Arrays.asList((Property.Underline)currentProperty, newUnderline));
        } else {
            setProperty(Property.UNDERLINE, newUnderline);
        }
        return (Type) this;
    }

    /**
     * This attribute specifies the base direction of directionally neutral text
     * (i.e., text that doesn't have inherent directionality as defined in Unicode)
     * in an element's content and attribute values.
     * @param baseDirection base direction
     * @return this element
     */
    public Type setBaseDirection(Property.BaseDirection baseDirection) {
        return setProperty(Property.BASE_DIRECTION, baseDirection);
    }

    /**
     * Sets a custom hyphenation configuration which will hyphenate words automatically accordingly to the
     * language and country.
     * @param hyphenationConfig
     * @return this element
     */
    public Type setHyphenation(HyphenationConfig hyphenationConfig) {
        return setProperty(Property.HYPHENATION, hyphenationConfig);
    }

    /**
     * Sets the writing system for this text element.
     * 
     * @param script a new script type
     * @return this Element.
     */
    public Type setFontScript(Character.UnicodeScript script) {
        return setProperty(Property.FONT_SCRIPT, script);
    }

}
