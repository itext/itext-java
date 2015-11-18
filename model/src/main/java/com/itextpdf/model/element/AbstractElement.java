package com.itextpdf.model.element;

import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.hyphenation.HyphenationConfig;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.splitting.ISplitCharacters;

import java.util.*;
import java.util.List;

public abstract class AbstractElement<Type extends AbstractElement> implements IElement<Type> {

    protected IRenderer nextRenderer;
    protected Map<Property, Object> properties = new EnumMap<>(Property.class);
    protected List<IElement> childElements = new ArrayList<>();

    @Override
    public void setNextRenderer(IRenderer renderer) {
        this.nextRenderer = renderer;
    }

    @Override
    public IRenderer createRendererSubTree() {
        IRenderer rendererRoot = makeRenderer();
        for (IElement child : childElements) {
            rendererRoot.addChild(child.createRendererSubTree());
        }
        return rendererRoot;
    }

    @Override
    public <T extends Type> T setProperty(Property property, Object value) {
        properties.put(property, value);
        return (T) this;
    }

    @Override
    public boolean hasProperty(Property property) {
        return properties.containsKey(property);
    }

    @Override
    public void deleteProperty(Property property) {
        properties.remove(property);
    }

    @Override
    public <T> T getProperty(Property property) {
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
            default:
                return null;
        }
    }

    public Property.UnitValue getWidth() {
        return getProperty(Property.WIDTH);
    }

    public Type setWidth(float width) {
        return setProperty(Property.WIDTH, Property.UnitValue.createPointValue(width));
    }

    public Type setWidthPercent(float widthPercent) {
        return setProperty(Property.WIDTH, Property.UnitValue.createPercentValue(widthPercent));
    }

    public Type setWidth(Property.UnitValue width) {
        return setProperty(Property.WIDTH, width);
    }

    public Float getHeight() {
        return getProperty(Property.HEIGHT);
    }

    public Type setHeight(float height) {
        return setProperty(Property.HEIGHT, height);
    }

    public Type setRelativePosition(float left, float top, float right, float bottom) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.RELATIVE).
            setProperty(Property.LEFT, left).
            setProperty(Property.RIGHT, right).
            setProperty(Property.TOP, top).
            setProperty(Property.BOTTOM, bottom);
    }

    public Type setFixedPosition(float x, float y, float width) {
        return setFixedPosition(x, y, Property.UnitValue.createPointValue(width));
    }

    public Type setFixedPosition(float x, float y, Property.UnitValue width) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.FIXED).
            setProperty(Property.X, x).
            setProperty(Property.Y, y).
            setProperty(Property.WIDTH, width);
    }

    public Type setFixedPosition(int pageNumber, float x, float y, float width) {
        return (Type) setFixedPosition(x, y, width).
               setProperty(Property.PAGE_NUMBER, pageNumber);
    }

    public Type setFixedPosition(int pageNumber, float x, float y, Property.UnitValue width) {
        return (Type) setFixedPosition(x, y, width).
                setProperty(Property.PAGE_NUMBER, pageNumber);
    }

//    public Type setAbsolutePosition(float x, float y) {
//        return (Type) setProperty(Property.POSITION, LayoutPosition.ABSOLUTE).
//            setProperty(Property.X, x).
//            setProperty(Property.Y, y);
//    }

    public Type setFont(PdfFont font) {
        return setProperty(Property.FONT, font);
    }

    public Type setFontColor(Color fontColor) {
        return setProperty(Property.FONT_COLOR, fontColor);
    }

    public Type setFontSize(float fontSize) {
        return setProperty(Property.FONT_SIZE, fontSize);
    }

    public Type setTextAlignment(Property.TextAlignment alignment) {
        return setProperty(Property.TEXT_ALIGNMENT, alignment);
    }

    public Type setCharacterSpacing(float charSpacing) {
        return setProperty(Property.CHARACTER_SPACING, charSpacing);
    }

    /**
     * The word-spacing parameter is added to the glyph’s horizontal or vertical displacement (depending on the writing mode).
     */
    public Type setWordSpacing(float wordSpacing) {
        return setProperty(Property.WORD_SPACING, wordSpacing);
    }

    /**
     * Enable or disable kerning.
     * Some fonts may specify kern pairs, i.e. pair of glyphs, between which the amount of horizontal space is adjusted.
     * This adjustment is typically negative, e.g. in "AV" pair the glyphs will typically be moved closer to each other.
     */
    public Type setFontKerning(Property.FontKerning fontKerning) {
        return setProperty(Property.FONT_KERNING, fontKerning);
    }

    public Type setBackgroundColor(Color backgroundColor) {
        return setBackgroundColor(backgroundColor, 0, 0, 0, 0);
    }

    public Type setBackgroundColor(Color backgroundColor, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
        return setProperty(Property.BACKGROUND, new Property.Background(backgroundColor, extraLeft, extraTop, extraRight, extraBottom));
    }

    public Type setBorder(Border border) {
        return setProperty(Property.BORDER, border);
    }

    public Type setBorderTop(Border border) {
        return setProperty(Property.BORDER_TOP, border);
    }

    public Type setBorderRight(Border border) {
        return setProperty(Property.BORDER_RIGHT, border);
    }

    public Type setBorderBottom(Border border) {
        return setProperty(Property.BORDER_BOTTOM, border);
    }

    public Type setBorderLeft(Border border) {
        return setProperty(Property.BORDER_LEFT, border);
    }

    /**
     * Sets a rule for splitting strings when they don't fit into one line.
     * The default implementation is {@link com.itextpdf.model.splitting.DefaultSplitCharacters}
     */
    public Type setSplitCharacters(ISplitCharacters splitCharacters) {
        return setProperty(Property.SPLIT_CHARACTERS, splitCharacters);
    }

    /**
     * Gets a rule for splitting strings when they don't fit into one line.
     */
    public ISplitCharacters getSplitCharacters() {
        return getProperty(Property.SPLIT_CHARACTERS);
    }

    public Integer getTextRenderingMode() {
        return getProperty(Property.TEXT_RENDERING_MODE);
    }

    public Type setTextRenderingMode(int textRenderingMode) {
        return setProperty(Property.TEXT_RENDERING_MODE, textRenderingMode);
    }

    public Color getStrokeColor() {
        return getProperty(Property.STROKE_COLOR);
    }

    public Type setStrokeColor(Color strokeColor) {
        return setProperty(Property.STROKE_COLOR, strokeColor);
    }

    public Float getStrokeWidth() {
        return getProperty(Property.STROKE_WIDTH);
    }

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
     *            {@link com.itextpdf.canvas.PdfCanvasConstants.LineCapStyle}
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
     * @return this element
     */
    public Type setHyphenation(HyphenationConfig hyphenationConfig) {
        return setProperty(Property.HYPHENATION, hyphenationConfig);
    }

    public Type setFontScript(Character.UnicodeScript script) {
        return setProperty(Property.FONT_SCRIPT, script);
    }

}
