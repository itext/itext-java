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
package com.itextpdf.layout.properties.grid;

import com.itextpdf.layout.properties.UnitValue;

/**
 * Represents fit content function template value.
 */
public class FitContentValue extends FunctionValue {
    private LengthValue length;

    /**
     * Create fit content function value based on provided {@link LengthValue} instance.
     *
     * @param length max size value
     */
    public FitContentValue(LengthValue length) {
        super(ValueType.FIT_CONTENT);
        this.length = length;
    }

    /**
     * Create fit content function value based on provided {@link UnitValue} instance.
     *
     * @param length max size value
     */
    public FitContentValue(UnitValue length) {
        super(ValueType.FIT_CONTENT);
        if (length != null) {
            if (length.isPointValue()) {
                this.length = new PointValue(length.getValue());
            } else if (length.isPercentValue()) {
                this.length = new PercentValue(length.getValue());
            }
        }
    }

    /**
     * Get underlying {@link LengthValue} which represents max size on a grid for this value.
     *
     * @return underlying {@link LengthValue} value
     */
    public LengthValue getLength() {
        return length;
    }

    /**
     * Gets the maximum size which the value can take on passed space.
     *
     * @param space the space for which fit-content size will be calculated
     *
     * @return the maximum size of the value on passed space
     */
    public float getMaxSizeForSpace(float space) {
        if (length.getType() == GridValue.ValueType.POINT) {
            return length.getValue();
        } else {
            return length.getValue() / 100 * space;
        }
    }
}
