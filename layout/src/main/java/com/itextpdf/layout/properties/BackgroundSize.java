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

/**
 * Class to hold background-size property.
 */
public class BackgroundSize {

    /**
     * Width size for this image. If {@link UnitValue} is in percent, then width depends on the area of the element.
     */
    private UnitValue backgroundWidthSize;
    /**
     * Height size for this image. If {@link UnitValue} is in percent, then height depends on the area of the element.
     */
    private UnitValue backgroundHeightSize;
    /**
     * Image covers the entire area and its size may be more than the area.
     */
    private boolean cover;
    /**
     * Image hsd a maximum size but not larger than the area.
     */
    private boolean contain;

    /**
     * Creates a new {@link BackgroundSize} instance.
     * The "cover" and "contain" properties are not set.
     */
    public BackgroundSize() {
        cover = false;
        contain = false;
    }

    /**
     * Clears all current properties and sets new width and height values. One of the parameters
     * can be null. Note that in this case null property will be scaled so that it becomes
     * proportionally equal with the non-null value. If both parameters are set to null, then
     * the default image size will be used.
     *
     * @param width a {@link UnitValue} object
     * @param height a {@link UnitValue} object
     */
    public void setBackgroundSizeToValues(UnitValue width, UnitValue height) {
        // See also BackgroundSizeCalculationUtil#calculateBackgroundImageSize
        clear();
        this.backgroundWidthSize = width;
        this.backgroundHeightSize = height;
    }

    /**
     * Clears all size values and sets the "contain" property {@code true}.
     *
     * @see BackgroundSize#contain
     */
    public void setBackgroundSizeToContain() {
        clear();
        contain = true;
    }

    /**
     * Clears all size values and sets the "cover" property {@code true}.
     *
     * @see BackgroundSize#cover
     */
    public void setBackgroundSizeToCover() {
        clear();
        cover = true;
    }

    /**
     * Gets the background width property of the image.
     *
     * @return the {@link UnitValue} width for this image.
     * @see BackgroundSize#backgroundWidthSize
     */
    public UnitValue getBackgroundWidthSize() {
        return backgroundWidthSize;
    }

    /**
     * Gets the background height property of the image.
     *
     * @return the {@link UnitValue} height for this image.
     * @see BackgroundSize#backgroundHeightSize
     */
    public UnitValue getBackgroundHeightSize() {
        return backgroundHeightSize;
    }

    /**
     * Returns is size has specific property.
     *
     * @return {@code true} if size set to "contain" or "cover", otherwise false.
     */
    public boolean isSpecificSize() {
        return contain || cover;
    }

    /**
     * Returns value of the "contain" property.
     *
     * @return {@code true} if property "contain" is set to the size, otherwise false.
     * @see BackgroundSize#contain
     */
    public boolean isContain() {
        return contain;
    }

    /**
     * Returns value of the "cover" property.
     *
     * @return {@code true} if property "cover" is set to the size, otherwise false.
     * @see BackgroundSize#cover
     */
    public boolean isCover() {
        return cover;
    }

    private void clear() {
        contain = false;
        cover = false;
        backgroundWidthSize = null;
        backgroundHeightSize = null;
    }
}
