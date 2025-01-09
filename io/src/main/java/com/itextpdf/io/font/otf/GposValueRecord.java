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
package com.itextpdf.io.font.otf;

public class GposValueRecord {
    private int xPlacement;
    private int yPlacement;
    private int xAdvance;
    private int yAdvance;

    /**
     * Retrieves the X placement of the Gpos value record.
     *
     * @return X placement
     */
    public int getXPlacement() {
        return xPlacement;
    }

    /**
     * Sets the X placement of the Gpos value record.
     *
     * @param xPlacement X placement
     */
    public void setXPlacement(int xPlacement) {
        this.xPlacement = xPlacement;
    }

    /**
     * Retrieves the Y placement of the Gpos value record.
     *
     * @return Y placement
     */
    public int getYPlacement() {
        return yPlacement;
    }

    /**
     * Sets the Y placement of the Gpos value record.
     *
     * @param yPlacement Y placement
     */
    public void setYPlacement(int yPlacement) {
        this.yPlacement = yPlacement;
    }

    /**
     * Retrieves the X advance of the Gpos value record.
     *
     * @return x advance
     */
    public int getXAdvance() {
        return xAdvance;
    }

    /**
     * Sets the X advance of the Gpos value record.
     *
     * @param xAdvance X advance
     */
    public void setXAdvance(int xAdvance) {
        this.xAdvance = xAdvance;
    }

    /**
     * Retrieves the Y advance of the Gpos value record.
     *
     * @return Y advance
     */
    public int getYAdvance() {
        return yAdvance;
    }

    /**
     * Sets the Y advance of the Gpos value record.
     *
     * @param yAdvance Y advance
     */
    public void setYAdvance(int yAdvance) {
        this.yAdvance = yAdvance;
    }
}
