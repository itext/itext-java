/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.layout;

import com.itextpdf.commons.actions.sequence.AbstractIdentifiableElement;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.FontKerning;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.splitting.ISplitCharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A generic abstract element that fits in a PDF layout object hierarchy.
 * A superclass of all {@link com.itextpdf.layout.element.IElement layout object} implementations.
 *
 * @param <T> this type
 */
public abstract class ElementPropertyContainer<T extends IPropertyContainer> extends AbstractIdentifiableElement
        implements IPropertyContainer {

    protected Map<Integer, Object> properties = new HashMap<>();

    @Override
    public void setProperty(int property, Object value) {
        properties.put(property, value);
    }

    @Override
    public boolean hasProperty(int property) {
        return hasOwnProperty(property);
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return properties.containsKey(property);
    }

    @Override
    public void deleteOwnProperty(int property) {
        properties.remove(property);
    }

    @Override
    public <T1> T1 getProperty(int property) {
        return (T1) this.<T1>getOwnProperty(property);
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) properties.<T1>get(property);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.MARGIN_TOP:
            case Property.MARGIN_RIGHT:
            case Property.MARGIN_BOTTOM:
            case Property.MARGIN_LEFT:
            case Property.PADDING_TOP:
            case Property.PADDING_RIGHT:
            case Property.PADDING_BOTTOM:
            case Property.PADDING_LEFT:
                return (T1) (Object) UnitValue.createPointValue(0f);
            default:
                return (T1) (Object) null;
        }
    }

    /**
     * Sets values for a relative repositioning of the Element. Also has as a
     * side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#RELATIVE relative}.
     *
     * The default implementation in {@link com.itextpdf.layout.renderer.AbstractRenderer} treats
     * <code>left</code> and <code>top</code> as the most important values. Only
     * if <code>left == 0</code> will <code>right</code> be used for the
     * calculation; ditto for top vs. bottom.
     *
     * @param left   movement to the left
     * @param top    movement upwards on the page
     * @param right  movement to the right
     * @param bottom movement downwards on the page
     * @return this Element.
     * @see LayoutPosition#RELATIVE
     */
    public T setRelativePosition(float left, float top, float right, float bottom) {
        setProperty(Property.POSITION, LayoutPosition.RELATIVE);
        setProperty(Property.LEFT, left);
        setProperty(Property.RIGHT, right);
        setProperty(Property.TOP, top);
        setProperty(Property.BOTTOM, bottom);
        return (T) (Object) this;
    }

    /**
     * Sets values for a absolute repositioning of the Element.
     * The coordinates specified correspond to the
     * bottom-left corner of the element and it grows upwards.
     * Also has as a side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#FIXED fixed}.
     *
     * @param left   horizontal position of the bottom-left corner on the page
     * @param bottom vertical position of the bottom-left corner on the page
     * @param width  a floating point value measured in points.
     * @return this Element.
     */
    public T setFixedPosition(float left, float bottom, float width) {
        setFixedPosition(left, bottom, UnitValue.createPointValue(width));
        return (T) (Object) this;
    }

    /**
     * Sets values for an absolute repositioning of the Element.
     * The coordinates specified correspond to the
     * bottom-left corner of the element, and it grows upwards.
     * Also has as a side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#FIXED fixed}.
     *
     * @param left   horizontal position of the bottom-left corner on the page
     * @param bottom vertical position of the bottom-left corner on the page
     * @param width  a {@link UnitValue}
     * @return this Element.
     */
    public T setFixedPosition(float left, float bottom, UnitValue width) {
        setProperty(Property.POSITION, LayoutPosition.FIXED);
        setProperty(Property.LEFT, left);
        setProperty(Property.BOTTOM, bottom);
        setProperty(Property.WIDTH, width);
        return (T) (Object) this;
    }

    /**
     * Sets values for a absolute repositioning of the Element.
     * The coordinates specified correspond to the
     * bottom-left corner of the element and it grows upwards.
     * Also has as a side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#FIXED fixed}.
     *
     * @param pageNumber the page where the element must be positioned
     * @param left       horizontal position of the bottom-left corner on the page
     * @param bottom     vertical position of the bottom-left corner on the page
     * @param width      a floating point value measured in points.
     * @return this Element.
     */
    public T setFixedPosition(int pageNumber, float left, float bottom, float width) {
        setFixedPosition(left, bottom, width);
        setProperty(Property.PAGE_NUMBER, pageNumber);
        return (T) (Object) this;
    }

    /**
     * Sets values for a absolute repositioning of the Element.
     * The coordinates specified correspond to the
     * bottom-left corner of the element and it grows upwards.
     * Also has as a side effect that the Element's {@link Property#POSITION} is changed to
     * {@link LayoutPosition#FIXED fixed}.
     *
     * @param pageNumber the page where the element must be positioned
     * @param left       horizontal position of the bottom-left corner on the page
     * @param bottom     vertical position of the bottom-left corner on the page
     * @param width      a floating point value measured in points.
     * @return this Element.
     */
    public T setFixedPosition(int pageNumber, float left, float bottom, UnitValue width) {
        setFixedPosition(left, bottom, width);
        setProperty(Property.PAGE_NUMBER, pageNumber);
        return (T) (Object) this;
    }

    /**
     * Sets the horizontal alignment of this Element.
     *
     * @param horizontalAlignment an enum value of type {@link HorizontalAlignment}
     * @return this Element.
     */
    public T setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        setProperty(Property.HORIZONTAL_ALIGNMENT, horizontalAlignment);
        return (T) (Object) this;
    }

    /**
     * Sets the font of this Element.
     * <p>
     * This property overrides the value set by {@link #setFontFamily}. Font is set either via exact {@link PdfFont}
     * instance or via font-family name that should correspond to the font in {@link FontProvider}, but not both.
     * @param font a {@link PdfFont font}
     * @return this Element.
     */
    public T setFont(PdfFont font) {
        setProperty(Property.FONT, font);
        return (T) (Object) this;
    }

    /**
     * Sets the preferable font families for this Element.
     * Note that {@link com.itextpdf.layout.font.FontProvider} shall be set as well.
     * See {@link RootElement#setFontProvider(FontProvider)}
     * <p>
     * This property overrides the value set by {@link #setFont(PdfFont)}. Font is set either via exact {@link PdfFont}
     * instance or via font-family name that should correspond to the font in {@link FontProvider}, but not both.
     * <p>
     * All {@link String} that are passed as argument are directly handled as a collection of font family names,
     * without any pre-processing. Every font family name is treated as a preferable font-family to be used
     * inside the element. The {@code fontFamilyNames} argument is interpreted as as an ordered list,
     * where every next font-family should be used if font for the previous one was not found or doesn't contain required glyphs.
     * @see com.itextpdf.io.font.constants.StandardFontFamilies
     * @param fontFamilyNames defines an ordered list of preferable font families for this Element.
     * @return this Element.
     */
    public T setFontFamily(String... fontFamilyNames) {
        setProperty(Property.FONT, fontFamilyNames);
        return (T) (Object) this;
    }

    /**
     * Sets the preferable font families for this Element.
     * Note that {@link com.itextpdf.layout.font.FontProvider} shall be set as well.
     * See {@link RootElement#setFontProvider(FontProvider)}
     * <p>
     * This property overrides the value set by {@link #setFont(PdfFont)}. Font is set either via exact {@link PdfFont}
     * instance or via font-family name that should correspond to the font in {@link FontProvider}, but not both.
     * <p>
     * All {@link String} that are passed as argument are directly handled as a collection of font family names,
     * without any pre-processing. Every font family name is treated as a preferable font-family to be used
     * inside the element. The {@code fontFamilyNames} argument is interpreted as as an ordered list,
     * where every next font-family should be used if font for the previous one was not found or doesn't contain required glyphs.
     * @see com.itextpdf.io.font.constants.StandardFontFamilies
     * @param fontFamilyNames defines an ordered list of preferable font families for this Element.
     * @return this Element.
     */
    public T setFontFamily(List<String> fontFamilyNames) {
        return this.setFontFamily(fontFamilyNames.toArray(new String[fontFamilyNames.size()]));
    }

    /**
     * Sets the font color of this Element.
     *
     * @param fontColor a {@link Color} for the text in this Element.
     * @return this Element.
     */
    public T setFontColor(Color fontColor) {
        return setFontColor(fontColor, 1f);
    }

    /**
     * Sets the font color of this Element and the opacity of the text.
     *
     * @param fontColor a {@link Color} for the text in this Element.
     * @param opacity   an opacity for the text in this Element; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent.
     * @return this Element.
     */
    public T setFontColor(Color fontColor, float opacity) {
        setProperty(Property.FONT_COLOR, fontColor != null ? new TransparentColor(fontColor, opacity) : null);
        return (T) (Object) this;
    }
    
    public T setFontColor(TransparentColor transparentColor) {
        setProperty(Property.FONT_COLOR, transparentColor);
        return (T) (Object) this;
    }

    /**
     * Sets the font size of this Element, measured in points.
     *
     * @param fontSize a floating point value
     * @return this Element.
     */
    public T setFontSize(float fontSize) {
        UnitValue fontSizeAsUV = UnitValue.createPointValue(fontSize);
        setProperty(Property.FONT_SIZE, fontSizeAsUV);
        return (T) (Object) this;
    }

    /**
     * Sets the text alignment of this Element.
     *
     * @param alignment an enum value of type {@link TextAlignment}
     * @return this Element.
     */
    public T setTextAlignment(TextAlignment alignment) {
        setProperty(Property.TEXT_ALIGNMENT, alignment);
        return (T) (Object) this;
    }

    /**
     * Defines a custom spacing distance between all characters of a textual element.
     * The character-spacing parameter is added to the glyph's horizontal or vertical displacement (depending on the writing mode).
     *
     * @param charSpacing a floating point value
     * @return this Element.
     */
    public T setCharacterSpacing(float charSpacing) {
        setProperty(Property.CHARACTER_SPACING, charSpacing);
        return (T) (Object) this;
    }

    /**
     * Defines a custom spacing distance between words of a textual element.
     * This value works exactly like the character spacing, but only kicks in at word boundaries.
     *
     * @param wordSpacing a floating point value
     * @return this Element.
     */
    public T setWordSpacing(float wordSpacing) {
        setProperty(Property.WORD_SPACING, wordSpacing);
        return (T) (Object) this;
    }

    /**
     * Enable or disable kerning.
     * Some fonts may specify kern pairs, i.e. pair of glyphs, between which the amount of horizontal space is adjusted.
     * This adjustment is typically negative, e.g. in "AV" pair the glyphs will typically be moved closer to each other.
     *
     * @param fontKerning an enum value as a boolean wrapper specifying whether or not to apply kerning
     * @return this Element.
     */
    public T setFontKerning(FontKerning fontKerning) {
        setProperty(Property.FONT_KERNING, fontKerning);
        return (T) (Object) this;
    }

    /**
     * Specifies a background color for the Element.
     *
     * @param backgroundColor the background color
     * @return this Element.
     */
    public T setBackgroundColor(Color backgroundColor) {
        return setBackgroundColor(backgroundColor, 1f);
    }

    /**
     * Specifies a background color for the Element.
     *
     * @param backgroundColor the background color
     * @param opacity         the background color opacity; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent.
     * @return this Element.
     */
    public T setBackgroundColor(Color backgroundColor, float opacity) {
        return setBackgroundColor(backgroundColor, opacity, 0, 0, 0, 0);
    }

    /**
     * Specifies a background color for the Element, and extra space that
     * must be counted as part of the background and therefore colored.
     *
     * @param backgroundColor the background color
     * @param extraLeft       extra coloring to the left side
     * @param extraTop        extra coloring at the top
     * @param extraRight      extra coloring to the right side
     * @param extraBottom     extra coloring at the bottom
     * @return this Element.
     */
    public T setBackgroundColor(Color backgroundColor, float extraLeft, float extraTop, float extraRight, float extraBottom) {
        return setBackgroundColor(backgroundColor, 1f, extraLeft, extraTop, extraRight, extraBottom);
    }

    /**
     * Specifies a background color for the Element, and extra space that
     * must be counted as part of the background and therefore colored.
     *
     * @param backgroundColor the background color
     * @param opacity         the background color opacity; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     * @param extraLeft       extra coloring to the left side
     * @param extraTop        extra coloring at the top
     * @param extraRight      extra coloring to the right side
     * @param extraBottom     extra coloring at the bottom
     * @return this Element.
     */
    public T setBackgroundColor(Color backgroundColor, float opacity, float extraLeft, float extraTop, float extraRight, float extraBottom) {
        setProperty(Property.BACKGROUND, backgroundColor != null ? new Background(backgroundColor, opacity, extraLeft, extraTop, extraRight, extraBottom) : null);
        return (T) (Object) this;
    }

    /**
     * Specifies a background image for the Element.
     *
     * @param image {@link BackgroundImage}
     * @return this Element.
     */
    public T setBackgroundImage(BackgroundImage image) {
        final List<BackgroundImage> backgroundImages = new ArrayList<>();
        backgroundImages.add(image);
        setProperty(Property.BACKGROUND_IMAGE, backgroundImages);
        return (T) (Object) this;
    }

    /**
     * Specifies a list of background images for the Element.
     *
     * @param imagesList List of {@link BackgroundImage}
     * @return this Element.
     */
    public T setBackgroundImage(List<BackgroundImage> imagesList) {
        setProperty(Property.BACKGROUND_IMAGE, imagesList);
        return (T) (Object) this;
    }

    /**
     * Sets a border for all four edges of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorder(Border border) {
        setProperty(Property.BORDER_TOP, border);
        setProperty(Property.BORDER_RIGHT, border);
        setProperty(Property.BORDER_BOTTOM, border);
        setProperty(Property.BORDER_LEFT, border);
        return (T) (Object) this;
    }

    /**
     * Sets a border for the upper limit of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorderTop(Border border) {
        setProperty(Property.BORDER_TOP, border);
        return (T) (Object) this;
    }

    /**
     * Sets a border for the right limit of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorderRight(Border border) {
        setProperty(Property.BORDER_RIGHT, border);
        return (T) (Object) this;
    }

    /**
     * Sets a border for the bottom limit of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorderBottom(Border border) {
        setProperty(Property.BORDER_BOTTOM, border);
        return (T) (Object) this;
    }

    /**
     * Sets a border for the left limit of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorderLeft(Border border) {
        setProperty(Property.BORDER_LEFT, border);
        return (T) (Object) this;
    }

    /**
     * Sets a border radius for all four edges of this Element.
     *
     * @param borderRadius a customized {@link BorderRadius}
     * @return this Element.
     */
    public T setBorderRadius(BorderRadius borderRadius) {
        setProperty(Property.BORDER_BOTTOM_LEFT_RADIUS, borderRadius);
        setProperty(Property.BORDER_BOTTOM_RIGHT_RADIUS, borderRadius);
        setProperty(Property.BORDER_TOP_LEFT_RADIUS, borderRadius);
        setProperty(Property.BORDER_TOP_RIGHT_RADIUS, borderRadius);
        return (T) (Object) this;
    }

    /**
     * Sets a border radius for the bottom left corner of this Element.
     *
     * @param borderRadius a customized {@link BorderRadius}
     * @return this Element.
     */
    public T setBorderBottomLeftRadius(BorderRadius borderRadius) {
        setProperty(Property.BORDER_BOTTOM_LEFT_RADIUS, borderRadius);
        return (T) (Object) this;
    }

    /**
     * Sets a border radius for the bottom right corner of this Element.
     *
     * @param borderRadius a customized {@link BorderRadius}
     * @return this Element.
     */
    public T setBorderBottomRightRadius(BorderRadius borderRadius) {
        setProperty(Property.BORDER_BOTTOM_RIGHT_RADIUS, borderRadius);
        return (T) (Object) this;
    }

    /**
     * Sets a border radius for the top left corner of this Element.
     *
     * @param borderRadius a customized {@link BorderRadius}
     * @return this Element.
     */
    public T setBorderTopLeftRadius(BorderRadius borderRadius) {
        setProperty(Property.BORDER_TOP_LEFT_RADIUS, borderRadius);
        return (T) (Object) this;
    }

    /**
     * Sets a border radius for the top right corner of this Element.
     *
     * @param borderRadius a customized {@link BorderRadius}
     * @return this Element.
     */
    public T setBorderTopRightRadius(BorderRadius borderRadius) {
        setProperty(Property.BORDER_TOP_RIGHT_RADIUS, borderRadius);
        return (T) (Object) this;
    }

    /**
     * Sets a rule for splitting strings when they don't fit into one line.
     * The default implementation is {@link com.itextpdf.layout.splitting.DefaultSplitCharacters}
     *
     * @param splitCharacters an implementation of {@link ISplitCharacters}
     * @return this Element.
     */
    public T setSplitCharacters(ISplitCharacters splitCharacters) {
        setProperty(Property.SPLIT_CHARACTERS, splitCharacters);
        return (T) (Object) this;
    }

    /**
     * Gets a rule for splitting strings when they don't fit into one line.
     *
     * @return the current string splitting rule, an implementation of {@link ISplitCharacters}
     */
    public ISplitCharacters getSplitCharacters() {
        return this.<ISplitCharacters>getProperty(Property.SPLIT_CHARACTERS);
    }

    /**
     * Gets the text rendering mode, a variable that determines whether showing
     * text causes glyph outlines to be stroked, filled, used as a clipping
     * boundary, or some combination of the three.
     *
     * @return the current text rendering mode
     * @see com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode
     */
    public Integer getTextRenderingMode() {
        return this.<Integer>getProperty(Property.TEXT_RENDERING_MODE);
    }

    /**
     * Sets the text rendering mode, a variable that determines whether showing
     * text causes glyph outlines to be stroked, filled, used as a clipping
     * boundary, or some combination of the three.
     *
     * @param textRenderingMode an <code>int</code> value
     * @return this Element.
     * @see com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode
     */
    public T setTextRenderingMode(int textRenderingMode) {
        setProperty(Property.TEXT_RENDERING_MODE, textRenderingMode);
        return (T) (Object) this;
    }

    /**
     * Gets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @return the current stroke color
     *
     * @deprecated in favour of {@link #getTransparentStrokeColor()} which should be renamed to {@code getStrokeColor}
     * after this method will be removed
     */
    @Deprecated
    public Color getStrokeColor() {
        return this.<TransparentColor>getProperty(Property.STROKE_COLOR).getColor();
    }

    /**
     * Gets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @return the current stroke color
     */
    public TransparentColor getTransparentStrokeColor() {
        return this.<TransparentColor>getProperty(Property.STROKE_COLOR);
    }

    /**
     * Sets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @param strokeColor a new stroke color
     *
     * @return this element
     */
    public T setStrokeColor(Color strokeColor) {
        return setStrokeColor(strokeColor, 1f);
    }

    /**
     * Sets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @param strokeColor a {@link Color} for the stroke
     * @param opacity an opacity for the stroke color; a float between 0 and 1, where 1 stands for fully opaque color
     *                and 0 - for fully transparent
     *
     * @return this element
     */
    public T setStrokeColor(Color strokeColor, float opacity) {
        setProperty(Property.STROKE_COLOR, strokeColor != null ? new TransparentColor(strokeColor, opacity) : null);
        return (T) (Object) this;
    }

    /**
     * Sets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @param transparentColor a new stroke color with transparency
     *
     * @return this element
     */
    public T setStrokeColor(TransparentColor transparentColor) {
        setProperty(Property.STROKE_COLOR, transparentColor);
        return (T) (Object) this;
    }

    /**
     * Sets the stroke dash pattern for the current text. Dash pattern is an array of the form [ dashArray dashPhase ],
     * where {@code dashArray} is a float array that specifies the length of the alternating dashes and gaps,
     * {@code dashPhase} is a float that specifies the distance into the dash pattern to start the dash.
     *
     * @param dashArray float array that specifies the length of the alternating dashes and gaps,
     *                  use {@code null} for solid line
     * @param dashPhase float that specifies the distance into the dash pattern to start the dash,
     *                  use 0 in case offset isn't needed
     *
     * @return this element
     */
    public T setDashPattern(float[] dashArray, float dashPhase) {
        List<Float> dashPattern = new ArrayList<>();
        if (dashArray != null) {
            for (float fl : dashArray) {
                dashPattern.add(fl);
            }
        }
        dashPattern.add(dashPhase);
        setProperty(Property.STROKE_DASH_PATTERN, dashPattern);
        return (T) (Object) this;
    }

    /**
     * Gets the stroke width for the current element.
     * The stroke width is the width of the outlines or edges of a shape.
     *
     * @return the current stroke width
     */
    public Float getStrokeWidth() {
        return this.<Float>getProperty(Property.STROKE_WIDTH);
    }

    /**
     * Sets the stroke width for the current element.
     * The stroke width is the width of the outlines or edges of a shape.
     *
     * @param strokeWidth a new stroke width
     * @return this Element.
     */
    public T setStrokeWidth(float strokeWidth) {
        setProperty(Property.STROKE_WIDTH, strokeWidth);
        return (T) (Object) this;
    }

    /**
     * Simulates bold style for a font.
     * Be aware that using correct bold font is highly preferred over this option.
     *
     * @return this element
     */
    public T simulateBold() {
        setProperty(Property.BOLD_SIMULATION, true);
        return (T) (Object) this;
    }

    /**
     * Simulates italic style for a font.
     * Be aware that using correct italic (oblique) font is highly preferred over this option.
     *
     * @return this element
     */
    public T simulateItalic() {
        setProperty(Property.ITALIC_SIMULATION, true);
        return (T) (Object) this;
    }

    /**
     * Sets default line-through attributes for text.
     * See {@link #setUnderline(Color, float, float, float, float, int)} for more fine tuning.
     *
     * @return this element
     */
    public T setLineThrough() {
        // 7/24 is the average between default browser behavior(1/4) and iText5 behavior(1/3)
        return setUnderline(null, .75f, 0, 0, 7 / 24f, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets default underline attributes for text.
     * See other overloads for more fine tuning.
     *
     * @return this element
     */
    public T setUnderline() {
        return setUnderline(null, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets an horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically and has always the text width.
     * Multiple call to this method will produce multiple lines.
     *
     * @param thickness the absolute thickness of the line
     * @param yPosition the absolute y position relative to the baseline
     * @return this element
     */
    public T setUnderline(float thickness, float yPosition) {
        return setUnderline(null, thickness, 0, yPosition, 0, PdfCanvasConstants.LineCapStyle.BUTT);
    }

    /**
     * Sets an horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically due to position parameter.
     * Multiple call to this method will produce multiple lines.
     * <p>
     * The thickness of the line will be {@code thickness + thicknessMul * fontSize}.
     * The position of the line will be {@code baseLine + yPosition + yPositionMul * fontSize}.
     *
     * @param color        the color of the line or <CODE>null</CODE> to follow the
     *                     text color
     * @param thickness    the absolute thickness of the line
     * @param thicknessMul the thickness multiplication factor with the font size
     * @param yPosition    the absolute y position relative to the baseline
     * @param yPositionMul the position multiplication factor with the font size
     * @param lineCapStyle the end line cap style. Allowed values are enumerated in
     *                     {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle}
     * @return this element
     */
    public T setUnderline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        return setUnderline(color, 1f, thickness, thicknessMul, yPosition, yPositionMul, lineCapStyle);
    }

    /**
     * Sets horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically due to position parameter.
     * Multiple call to this method will produce multiple lines.
     *
     * <p>
     * The thickness of the line will be {@code thickness + thicknessMul * fontSize}.
     * The position of the line will be {@code baseLine + yPosition + yPositionMul * fontSize}.
     *
     * @param color        the color of the line or <CODE>null</CODE> to follow the
     *                     text color
     * @param opacity      the opacity of the line; a float between 0 and 1, where 1 stands for fully opaque color and
     *                     0 - for fully transparent
     * @param thickness    the absolute thickness of the line
     * @param thicknessMul the thickness multiplication factor with the font size
     * @param yPosition    the absolute y position relative to the baseline
     * @param yPositionMul the position multiplication factor with the font size
     * @param lineCapStyle the end line cap style. Allowed values are enumerated in
     *                     {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle}
     *
     * @return this element
     */
    public T setUnderline(Color color, float opacity, float thickness, float thicknessMul, float yPosition,
                          float yPositionMul, int lineCapStyle) {
        return setUnderline(new Underline(color, opacity, thickness, thicknessMul, yPosition,
                yPositionMul, lineCapStyle));
    }

    /**
     * Sets horizontal line that can be an underline, overline or a strikethrough.
     * Actually, the line can be anywhere vertically due to position parameter.
     * Multiple call to this method will produce multiple lines.
     *
     * @param underline {@link Underline} to set
     *
     * @return this element
     */
    public T setUnderline(Underline underline) {
        Object currentProperty = this.<Object>getProperty(Property.UNDERLINE);
        if (currentProperty instanceof List) {
            ((List) currentProperty).add(underline);
        } else if (currentProperty instanceof Underline) {
            List<Underline> mergedUnderlines = new ArrayList<>();
            mergedUnderlines.add((Underline) currentProperty);
            mergedUnderlines.add(underline);
            setProperty(Property.UNDERLINE, mergedUnderlines);
        } else {
            setProperty(Property.UNDERLINE, underline);
        }
        return (T) (Object) this;
    }

    /**
     * This attribute specifies the base direction of directionally neutral text
     * (i.e., text that doesn't have inherent directionality as defined in Unicode)
     * in an element's content and attribute values.
     *
     * @param baseDirection base direction
     * @return this element
     */
    public T setBaseDirection(BaseDirection baseDirection) {
        setProperty(Property.BASE_DIRECTION, baseDirection);
        return (T) (Object) this;
    }

    /**
     * Sets a custom hyphenation configuration which will hyphenate words automatically accordingly to the
     * language and country.
     *
     * @param hyphenationConfig The hyphenation configuration
     * @return this element
     */
    public T setHyphenation(HyphenationConfig hyphenationConfig) {
        setProperty(Property.HYPHENATION, hyphenationConfig);
        return (T) (Object) this;
    }

    /**
     * Sets the writing system for this text element.
     *
     * @param script a new script type
     * @return this Element.
     */
    public T setFontScript(Character.UnicodeScript script) {
        setProperty(Property.FONT_SCRIPT, script);
        return (T) (Object) this;
    }

    /**
     * Sets a destination name that will be created when this element is drawn to content.
     *
     * @param destination the destination name to be created
     * @return this Element.
     */
    public T setDestination(String destination) {
        Set<Object> existingDestinations = this.<Set<Object>>getProperty(Property.DESTINATION);
        if (existingDestinations == null) {
            existingDestinations = new HashSet<>();
        }
        existingDestinations.add(destination);
        setProperty(Property.DESTINATION, existingDestinations);
        return (T) (Object) this;
    }

    /**
     * Sets an opacity of the given element. It will affect element content, borders and background. Note, that it will also
     * affect all element children, as they are the content of the given element.
     *
     * @param opacity a float between 0 and 1, where 1 stands for fully opaque element and 0 - for fully transparent
     * @return this Element.
     */
    public T setOpacity(Float opacity) {
        setProperty(Property.OPACITY, opacity);
        return (T) (Object) this;
    }
}
