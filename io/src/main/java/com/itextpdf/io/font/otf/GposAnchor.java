/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.font.otf;

/**
 * A GPOS table uses anchor points to position one glyph with respect to another: two glyphs each define an anchor
 * point, and the text-processing client attaches the glyphs by aligning their corresponding anchor points.
 *
 * <p>
 * For more information see <a href="https://learn.microsoft.com/en-us/typography/opentype/spec/gpos#anchor-table">Anchor table</a>
 */
public class GposAnchor {
    private int xCoordinate;
    private int yCoordinate;

    /**
     * Creates a GPOS Anchor object with zero x and y coordinates.
     */
    public GposAnchor() {
    }

    /**
     * Creates a Gpos Anchor object based on a given Gpos Anchor object.
     *
     * @param other other Gpos Anchor object
     */
    public GposAnchor(GposAnchor other) {
        this.xCoordinate = other.xCoordinate;
        this.yCoordinate = other.yCoordinate;
    }

    /**
     * Retrieves the X coordinate of the Gpos Anchor.
     *
     * @return X coordinate
     */
    public int getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the x coordinate of the Gpos Anchor.
     *
     * @param xCoordinate X coordinate
     */
    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * Retrieves the Y coordinate of the Gpos Anchor.
     *
     * @return Y coordinate
     */
    public int getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the Y coordinate of the Gpos Anchor.
     *
     * @param yCoordinate Y coordinate
     */
    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
