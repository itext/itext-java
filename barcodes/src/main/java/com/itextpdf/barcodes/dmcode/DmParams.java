/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

/**
 * Class that contains the parameters for a DM code.
 * It contains all the information needed to create one data matrix entry
 */
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

    private final int height;

    private final int width;

    private final int heightSection;
    private final int widthSection;
    private final int dataSize;
    private final int dataBlock;
    private final int errorBlock;

    /**
     * Retrieves the height of DmParams object.
     *
     * @return total height value
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the width of DmParams object.
     *
     * @return total width value
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of a single section.
     *
     * @return total height value
     */
    public int getHeightSection() {
        return heightSection;
    }

    /**
     * Retrieves the width of a single section.
     *
     * @return total width value
     */
    public int getWidthSection() {
        return widthSection;
    }

    /**
     * Retrieves the size of the data.
     *
     * @return data size value
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * Retrieves the size of the data block.
     *
     * @return data block size value
     */
    public int getDataBlock() {
        return dataBlock;
    }

    /**
     * Retrieves the size of the error block.
     *
     * @return error block size value
     */
    public int getErrorBlock() {
        return errorBlock;
    }
}
