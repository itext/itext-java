/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.barcodes.dmcode;

public  class DmParams {
    /**
     * Creates a DM code parameter block
     * @param height total height
     * @param width total width
     * @param heightSection height of a single section
     * @param widthSection width of a single section
     * @param dataSize size of the data
     * @param dataBlock size of a data-block
     * @param errorBlock size of a error-correction block
     */
    public DmParams(int height, int width, int heightSection, int widthSection, int dataSize, int dataBlock, int errorBlock) {
        this.height = height;
        this.width = width;
        this.heightSection = heightSection;
        this.widthSection = widthSection;
        this.dataSize = dataSize;
        this.dataBlock = dataBlock;
        this.errorBlock = errorBlock;
    }

    public int height;
    public int width;
    public int heightSection;
    public int widthSection;
    public int dataSize;
    public int dataBlock;
    public int errorBlock;
}
