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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;

/**
* A property corresponding to the css vertical-align property and used to
* set vertical alignment on inline blocks, it specifies the  type of alignment
* and where needed a numerical value to complete it.
*/
public class InlineVerticalAlignment {

    private InlineVerticalAlignmentType type;
    private float value;


    /**
     * Creates a default InlineVerticalAlignment, it gets the type {@link InlineVerticalAlignmentType#BASELINE}.
     */
    public InlineVerticalAlignment () {
        type = InlineVerticalAlignmentType.BASELINE;
    }

    /**
     * Creates an InlineVerticalAlignment with a specified type.
     *
     * @param type {@link InlineVerticalAlignmentType}
     */
    public InlineVerticalAlignment (InlineVerticalAlignmentType type) {
        this.type = type;
    }

    /**
     * Creates an InlineVerticalAlignment with a specified type and a value.
     * This will throw a {@link PdfException} when used with a type that does not require a value.
     *
     * @param type {@link InlineVerticalAlignmentType}
     * @param value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */
    public InlineVerticalAlignment (InlineVerticalAlignmentType type, float value) {
        if (!(type == InlineVerticalAlignmentType.FRACTION || type == InlineVerticalAlignmentType.FIXED)) {
            throw new PdfException(LayoutExceptionMessageConstant.INLINE_VERTICAL_ALIGNMENT_DOESN_T_NEED_A_VALUE)
                    .setMessageParams(type);

        }
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the type of InlineVerticalAlignment.
     *
     * @return the type {@link InlineVerticalAlignmentType}
     */
    public InlineVerticalAlignmentType getType() {
        return type;
    }

    /**
     * Sets the type {@link InlineVerticalAlignmentType}.
     *
     * @param type {@link InlineVerticalAlignmentType}
     */
    public void setType(InlineVerticalAlignmentType type) {
        this.type = type;
    }

    /**
     * Gets the value.
     *
     * @return  value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */

    public float getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value In the case of {@link InlineVerticalAlignmentType#FIXED} a lenth in pts,
     *              in case of {@link InlineVerticalAlignmentType#FRACTION} a multiplier value.
     */
    public void setValue(float value) {
        this.value = value;
    }
}
