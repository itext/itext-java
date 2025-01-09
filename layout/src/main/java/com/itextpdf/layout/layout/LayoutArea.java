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

import com.itextpdf.io.util.HashCode;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * Represents the area for content {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LayoutArea implements Cloneable {

    /**
     * The number of page on which the area is located.
     */
    protected int pageNumber;
    /**
     * The area's bounding box
     */
    protected Rectangle bBox;

    /**
     * Creates the area for content {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
     *
     * @param pageNumber the number of page on which the area is located.
     * @param bBox the area's bounding box
     */
    public LayoutArea(int pageNumber, Rectangle bBox) {
        this.pageNumber = pageNumber;
        this.bBox = bBox;
    }

    /**
     * Gets the number of page on which the area is located.
     *
     * @return page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Gets the {@link Rectangle box} which bounds the area.
     *
     * @return the bounding box
     */
    public Rectangle getBBox() {
        return bBox;
    }

    /**
     * Sets the {@link Rectangle box} which bounds the area.
     *
     * @param bbox the area's bounding box
     */
    public void setBBox(Rectangle bbox) {
        this.bBox = bbox;
    }

    /**
     * Creates a "deep copy" of this LayoutArea, meaning the object returned by this method will be independent
     * of the object being cloned.
     *
     * @return the copied LayoutArea.
     */
    @Override
    public LayoutArea clone() {
        try {
            LayoutArea clone = (LayoutArea) super.clone();
            // super.clone performs a "shallow-copy", therefore it's needed to copy non-primitive fields manually.
            clone.bBox = bBox.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // should never happen since Cloneable is implemented
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        LayoutArea that = (LayoutArea) obj;
        return pageNumber == that.pageNumber && bBox.equalsWithEpsilon(that.bBox);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCode hashCode = new HashCode();
        hashCode.append(pageNumber).
                append(bBox.hashCode());
        return hashCode.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MessageFormatUtil.format("{0}, page {1}", bBox.toString(), pageNumber);
    }
}
