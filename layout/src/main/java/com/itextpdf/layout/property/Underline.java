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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import static com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.*;

/**
 * A POJO that describes the underline of a layout element.
 * 
 * This class is to be used as a property for an element or renderer,
 * as the value for {@link com.itextpdf.layout.property.Property#UNDERLINE}
 */
public class Underline {
    protected TransparentColor transparentColor;
    protected float thickness;
    protected float thicknessMul;
    protected float yPosition;
    protected float yPositionMul;
    protected int lineCapStyle = PdfCanvasConstants.LineCapStyle.BUTT;

    /**
     * Creates an Underline. Both the thickness and vertical positioning under
     * the text element's base line can be set to a fixed value, or a variable
     * one depending on the element's font size.
     * If you want a fixed-width thickness, set <code>thicknessMul</code> to 0;
     * if you want a thickness solely dependent on the font size, set
     * <code>thickness</code> to 0.
     * Mutatis mutandis for the y-position.
     * 
     * @param color the {@link Color} of the underline
     * @param thickness  a float defining the minimum thickness in points of the underline
     * @param thicknessMul  a float defining the font size dependent component of the thickness of the underline
     * @param yPosition a float defining the default absolute vertical distance in points from the text's base line
     * @param yPositionMul  a float defining the font size dependent component of the vertical positioning of the underline
     * @param lineCapStyle the way the underline finishes at its edges. {@link LineCapStyle}
     */
    public Underline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        this(color, 1f, thickness, thicknessMul, yPosition, yPositionMul, lineCapStyle);
    }

    /**
     * Creates an Underline. Both the thickness and vertical positioning under
     * the text element's base line can be set to a fixed value, or a variable
     * one depending on the element's font size.
     * If you want a fixed-width thickness, set <code>thicknessMul</code> to 0;
     * if you want a thickness solely dependent on the font size, set
     * <code>thickness</code> to 0.
     * Mutatis mutandis for the y-position.
     * 
     * @param color the {@link Color} of the underline
     * @param opacity  a float defining the opacity of the underline; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     * @param thickness  a float defining the minimum thickness in points of the underline
     * @param thicknessMul  a float defining the font size dependent component of the thickness of the underline
     * @param yPosition a float defining the default absolute vertical distance in points from the text's base line
     * @param yPositionMul  a float defining the font size dependent component of the vertical positioning of the underline
     * @param lineCapStyle the way the underline finishes at its edges. {@link LineCapStyle}
     */
    public Underline(Color color, float opacity, float thickness, float thicknessMul, float yPosition, float yPositionMul, int lineCapStyle) {
        this.transparentColor = new TransparentColor(color, opacity);
        this.thickness = thickness;
        this.thicknessMul = thicknessMul;
        this.yPosition = yPosition;
        this.yPositionMul = yPositionMul;
        this.lineCapStyle = lineCapStyle;
    }

    /**
     * Gets the color of the underline.
     * @return a {@link Color}
     */
    public Color getColor() {
        return transparentColor.getColor();
    }

    /**
     * Gets the opacity of the underline color.
     * @return a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public float getOpacity() {
        return transparentColor.getOpacity();
    }

    /**
     * Gets the total thickness of the underline (fixed + variable part).
     * @param fontSize the font size for which to calculate the variable thickness
     * @return the total thickness, as a <code>float</code>, in points
     */
    public float getThickness(float fontSize) {
        return thickness + thicknessMul * fontSize;
    }

    /**
     * Gets the vertical position of the underline (fixed + variable part).
     * @param fontSize the font size for which to calculate the variable position
     * @return the y-position, as a <code>float</code>, in points
     */
    public float getYPosition(float fontSize) {
        return yPosition + yPositionMul * fontSize;
    }

    /**
     * Gets the multiplier for the vertical positioning of the text underline.
     * @return the Y-position multiplier, as a <code>float</code>
     */
    public float getYPositionMul() {
        return yPositionMul;
    }
    
    /**
     * Gets the {@link LineCapStyle} of the text underline.
     * @return the line cap style, as an <code>int</code> referring to
     * the values of {@link LineCapStyle}
     */
    public int getLineCapStyle() {
        return lineCapStyle;
    }
}
