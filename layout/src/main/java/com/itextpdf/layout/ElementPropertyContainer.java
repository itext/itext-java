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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.TransparentColor;
import com.itextpdf.layout.property.Underline;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.splitting.ISplitCharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic abstract element that fits in a PDF layout object hierarchy.
 * A superclass of all {@link com.itextpdf.layout.element.IElement layout object} implementations.
 *
 * @param <T> this type
 */
public abstract class ElementPropertyContainer<T extends IPropertyContainer> implements IPropertyContainer {

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
     * Sets values for a absolute repositioning of the Element.
     * The coordinates specified correspond to the
     * bottom-left corner of the element and it grows upwards.
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
     * Sets the font family of this Element. Note that {@link com.itextpdf.layout.font.FontProvider} shall be set as well.
     * See {@link RootElement#setFontProvider(FontProvider)}
     *
     * @see com.itextpdf.io.font.constants.StandardFontFamilies
     * @param font a font name to fetch from {@link com.itextpdf.layout.font.FontProvider}
     * @return this Element.
     * @deprecated This method will be removed in 7.2. Use {@link #setFontFamily(String...)} instead.
     */
    @Deprecated
    public T setFont(String font) {
        setProperty(Property.FONT, font);
        return (T) (Object) this;
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
     * Sets a border for all four edges of this Element with customizable color, width, pattern type.
     *
     * @param border a customized {@link Border}
     * @return this Element.
     */
    public T setBorder(Border border) {
        setProperty(Property.BORDER, border);
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
        setProperty(Property.BORDER_RADIUS, borderRadius);
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
     */
    public Color getStrokeColor() {
        return this.<Color>getProperty(Property.STROKE_COLOR);
    }

    /**
     * Sets the stroke color for the current element.
     * The stroke color is the color of the outlines or edges of a shape.
     *
     * @param strokeColor a new stroke color
     * @return this Element.
     */
    public T setStrokeColor(Color strokeColor) {
        setProperty(Property.STROKE_COLOR, strokeColor);
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
     * Switch on the simulation of bold style for a font.
     * Be aware that using correct bold font is highly preferred over this option.
     *
     * @return this element
     */
    public T setBold() {
        setProperty(Property.BOLD_SIMULATION, true);
        return (T) (Object) this;
    }

    /**
     * Switch on the simulation of italic style for a font.
     * Be aware that using correct italic (oblique) font is highly preferred over this option.
     *
     * @return this element
     */
    public T setItalic() {
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
     * Sets an horizontal line that can be an underline or a strikethrough.
     * Actually, the line can be anywhere vertically due to position parameter.
     * Multiple call to this method will produce multiple lines.
     * <p>
     * The thickness of the line will be {@code thickness + thicknessMul * fontSize}.
     * The position of the line will be {@code baseLine + yPosition + yPositionMul * fontSize}.
     *
     * @param color        the color of the line or <CODE>null</CODE> to follow the
     *                     text color
     * @param opacity      the opacity of the line; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     * @param thickness    the absolute thickness of the line
     * @param thicknessMul the thickness multiplication factor with the font size
     * @param yPosition    the absolute y position relative to the baseline
     * @param yPositionMul the position multiplication factor with the font size
     * @param lineCapStyle the end line cap style. Allowed values are enumerated in
     *                     {@link com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle}
     * @return this element
     */
    public T setUnderline(Color color, float opacity, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        Underline newUnderline = new Underline(color, opacity, thickness, thicknessMul, yPosition, yPositionMul, lineCapStyle);
        Object currentProperty = this.<Object>getProperty(Property.UNDERLINE);
        if (currentProperty instanceof List) {
            ((List) currentProperty).add(newUnderline);
        } else if (currentProperty instanceof Underline) {
            List<Underline> mergedUnderlines = new ArrayList<>();
            mergedUnderlines.add((Underline) currentProperty);
            mergedUnderlines.add(newUnderline);
            setProperty(Property.UNDERLINE, mergedUnderlines);
        } else {
            setProperty(Property.UNDERLINE, newUnderline);
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
        setProperty(Property.DESTINATION, destination);
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
