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
package com.itextpdf.io.util;

/**
 * A helper data class, which aggregates true/false result of ImageMagick comparing
 * as well as the number of different pixels.
 */
public final class ImageMagickCompareResult {

    private final boolean result;
    private final long diffPixels;

    /**
     * Creates an instance that contains ImageMagick comparing result information.
     *
     * @param result     true, if the compared images are equal.
     * @param diffPixels number of different pixels.
     */
    public ImageMagickCompareResult(boolean result, long diffPixels) {
        this.result = result;
        this.diffPixels = diffPixels;
    }

    /**
     * Returns image compare boolean value.
     *
     * @return true if the compared images are equal.
     */
    public boolean isComparingResultSuccessful() {
        return result;
    }

    /**
     * Getter for a different pixels count.
     *
     * @return Returns a a different pixels count.
     */
    public long getDiffPixels() {
        return diffPixels;
    }
}
