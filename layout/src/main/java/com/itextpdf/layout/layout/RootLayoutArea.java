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
package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;

/**
 * Represents the root layout area.
 */
public class RootLayoutArea extends LayoutArea implements Cloneable {
    /**
     * Indicates whether the area already has some placed content or not.
     */
    protected boolean emptyArea = true;

    /**
     * Creates the root layout area.
     *
     * @param pageNumber the value number of page
     * @param bBox the bounding box
     */
    public RootLayoutArea(int pageNumber, Rectangle bBox) {
        super(pageNumber, bBox);
    }

    /**
     * Indicates whether the area already has some placed content or not.
     *
     * @return whether the area is empty or not
     */
    public boolean isEmptyArea() {
        return emptyArea;
    }

    /**
     * Defines whether the area already has some placed content or not.
     *
     * @param emptyArea indicates whether the area already has some placed content or not.
     */
    public void setEmptyArea(boolean emptyArea) {
        this.emptyArea = emptyArea;
    }

    /**
     * Creates a "deep copy" of this RootLayoutArea, meaning the object returned by this method will be independent
     * of the object being cloned.
     * Note that although the return type of this method is {@link LayoutArea},
     * the actual type of the returned object is {@link RootLayoutArea}.
     *
     * @return the copied RootLayoutArea.
     */
    @Override
    public LayoutArea clone() {
        RootLayoutArea area = (RootLayoutArea) super.clone();
        area.setEmptyArea(emptyArea);
        return area;
    }
}
