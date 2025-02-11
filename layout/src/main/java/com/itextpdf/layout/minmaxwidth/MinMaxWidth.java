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
package com.itextpdf.layout.minmaxwidth;

public class MinMaxWidth {


    private float childrenMinWidth;
    private float childrenMaxWidth;
    private float additionalWidth;

    public MinMaxWidth() {
        this(0);
    }

    public MinMaxWidth(float additionalWidth) {
        this(0, 0, additionalWidth);
    }

    public MinMaxWidth(float childrenMinWidth, float childrenMaxWidth, float additionalWidth) {
        this.childrenMinWidth = childrenMinWidth;
        this.childrenMaxWidth = childrenMaxWidth;
        this.additionalWidth = additionalWidth;
    }

    public float getChildrenMinWidth() {
        return childrenMinWidth;
    }

    public void setChildrenMinWidth(float childrenMinWidth) {
        this.childrenMinWidth = childrenMinWidth;
    }

    public float getChildrenMaxWidth() {
        return childrenMaxWidth;
    }

    public void setChildrenMaxWidth(float childrenMaxWidth) {
        this.childrenMaxWidth = childrenMaxWidth;
    }

    public float getAdditionalWidth() {
        return additionalWidth;
    }

    public void setAdditionalWidth(float additionalWidth) {
        this.additionalWidth = additionalWidth;
    }

    public float getMaxWidth() {
        return Math.min(childrenMaxWidth + additionalWidth, MinMaxWidthUtils.getInfWidth());
    }

    public float getMinWidth() {
        return Math.min(childrenMinWidth + additionalWidth, getMaxWidth());
    }

    @Override
    public String toString() {
        return "min=" + (childrenMinWidth + additionalWidth) +
                ", max=" + (childrenMaxWidth + additionalWidth);
    }
}
