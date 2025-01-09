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
package com.itextpdf.layout.properties;

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
    private BackgroundBox backgroundClip = BackgroundBox.BORDER_BOX;

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
     * Creates a background with a specified color, opacity and clip value.
     *
     * @param color   the background color
     * @param opacity the opacity of the background color; a float between 0 and 1, where 1 stands for fully opaque
     *                color and 0 - for fully transparent
     * @param clip    the value to clip the background color
     */
    public Background(Color color, float opacity, BackgroundBox clip) {
        this(color, opacity);
        this.backgroundClip = clip;
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

    /**
     * Gets background clip value.
     *
     * @return background clip value
     */
    public BackgroundBox getBackgroundClip() {
        return backgroundClip;
    }
}
