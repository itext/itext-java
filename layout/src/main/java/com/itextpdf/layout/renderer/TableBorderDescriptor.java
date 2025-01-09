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
package com.itextpdf.layout.renderer;

class TableBorderDescriptor {
    private int borderIndex;
    private float mainCoordinateStart;
    private float crossCoordinate;
    private float[] mainCoordinateWidths;

    /**
     * Creates a table border descriptor which will be used while drawing the described border.
     *
     * @param borderIndex the index of the described border
     * @param mainCoordinateStart the border's start main-axis coordinate
     * @param crossCoordinate fixed cross-axis coordinate of the whole border
     * @param mainCoordinateWidths the sizes (widths or heights) of rows or columns depending on the type of main axis
     */
    public TableBorderDescriptor(int borderIndex, float mainCoordinateStart, float crossCoordinate,
            float[] mainCoordinateWidths) {
        this.borderIndex = borderIndex;
        this.mainCoordinateStart = mainCoordinateStart;
        this.crossCoordinate = crossCoordinate;
        this.mainCoordinateWidths = mainCoordinateWidths;
    }

    public int getBorderIndex() {
        return borderIndex;
    }

    public float getMainCoordinateStart() {
        return mainCoordinateStart;
    }

    public float getCrossCoordinate() {
        return crossCoordinate;
    }

    public float[] getMainCoordinateWidths() {
        return mainCoordinateWidths;
    }
}
