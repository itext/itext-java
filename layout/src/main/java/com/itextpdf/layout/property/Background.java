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
import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized class holding configurable properties related to an {@link
 * com.itextpdf.layout.element.IElement}'s background. This class is meant to be used as the value for the
 * {@link Property#BACKGROUND} key in an {@link IPropertyContainer}. Allows
 * to define a background color, and positive or negative changes to the
 * location of the edges of the background coloring.
 */
public class Background {
    protected TransparentColor transparentColor;
    protected float extraLeft;
    protected float extraRight;
    protected float extraTop;
    protected float extraBottom;

    /**
     * Creates a background with a specified color.
     * @param color the background color
     */
    public Background(Color color) {
        this(color, 1f, 0, 0, 0, 0);
    }

    /**
     * Creates a background with a specified color and opacity.
     * @param color the background color
     * @param opacity the opacity of the background color; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public Background(Color color, float opacity) {
        this(color, opacity, 0, 0, 0, 0);
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
    public Background(Color color, float extraLeft, float extraTop, float extraRight, float extraBottom) {
        this(color, 1f, extraLeft, extraTop, extraRight, extraBottom);
    }
    
    /**
     * Creates a background with a specified color, and extra space that
     * must be counted as part of the background and therefore colored.
     * These values are allowed to be negative.
     * @param color the background color
     * @param opacity the opacity of the background color; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     * @param extraLeft extra coloring to the left side
     * @param extraTop extra coloring at the top
     * @param extraRight extra coloring to the right side
     * @param extraBottom extra coloring at the bottom
     */
    public Background(Color color, float opacity, float extraLeft, float extraTop, float extraRight, float extraBottom) {
        this.transparentColor = new TransparentColor(color, opacity);
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
        return transparentColor.getColor();
    }

    /**
     * Gets the opacity of the background.
     * @return a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public float getOpacity() {
        return transparentColor.getOpacity();
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
