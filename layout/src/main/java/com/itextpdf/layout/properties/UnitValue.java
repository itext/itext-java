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

import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * A specialized class that holds a value and the unit it is measured in.
 */
public class UnitValue {
    public static final int POINT = 1;
    public static final int PERCENT = 2;

    protected int unitType;
    protected float value;

    /**
     * Creates a UnitValue object with a specified type and value.
     * @param unitType either {@link UnitValue#POINT} or a {@link UnitValue#PERCENT}
     * @param value the value to be stored.
     */
    public UnitValue(int unitType, float value) {
        this.unitType = unitType;
        assert !Float.isNaN(value);
        this.value = value;
    }

    /**
     * Creates a copy of UnitValue object.
     *
     * @param unitValue the value to be stored
     */
    public UnitValue(UnitValue unitValue) {
        this(unitValue.unitType, unitValue.value);
    }

    /**
     * Creates a UnitValue POINT object with a specified value.
     * @param value the value to be stored.
     * @return a new {@link UnitValue#POINT} {@link UnitValue}
     */
    public static UnitValue createPointValue(float value) {
        return new UnitValue(POINT, value);
    }

    /**
     * Creates a UnitValue PERCENT object with a specified value.
     * @param value the value to be stored.
     * @return a new {@link UnitValue#PERCENT} {@link UnitValue}
     */
    public static UnitValue createPercentValue(float value) {
        return new UnitValue(PERCENT, value);
    }

    /**
     * Creates an array of UnitValue PERCENT objects with specified values.
     *
     * @param values the values to be stored.
     * @return a new normalized (Σ=100%) array of {@link UnitValue#PERCENT} {@link UnitValue}.
     */
    public static UnitValue[] createPercentArray(float[] values) {
        UnitValue[] resultArray = new UnitValue[values.length];
        float sum = 0;
        for (float val : values) sum += val;
        for (int i = 0; i < values.length; i++) {
            resultArray[i] = UnitValue.createPercentValue(100 * values[i] / sum);
        }
        return resultArray;
    }

    /**
     * Creates an array of UnitValue PERCENT objects with equal values.
     *
     * @param size of the resulted array.
     * @return a array of equal {@link UnitValue#PERCENT} {@link UnitValue}.
     */
    public static UnitValue[] createPercentArray(int size) {
        UnitValue[] resultArray = new UnitValue[size];
        for (int i = 0; i < size; i++) {
            resultArray[i] = UnitValue.createPercentValue(100f / size);
        }
        return resultArray;
    }

    /**
     * Creates an array of UnitValue POINT objects with specified values.
     * @param values the values to be stored.
     * @return a new array of {@link UnitValue#POINT} {@link UnitValue}
     */
    public static UnitValue[] createPointArray(float[] values) {
        UnitValue[] resultArray = new UnitValue[values.length];
        for (int i = 0; i < values.length; i++) {
            resultArray[i] = UnitValue.createPointValue(values[i]);
        }
        return resultArray;
    }

    /**
     * Returns the unit this value is stored in, either points (pt) or percent(%)
     * @return either 1 for {@link UnitValue#POINT} or 2 for {@link UnitValue#PERCENT}
     */
    public int getUnitType() {
        return unitType;
    }

    /**
     * Sets the unit this value is stored in, either points (pt) or percent(%)
     * @param unitType either {@link UnitValue#POINT} or {@link UnitValue#PERCENT}
     */
    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

    /**
     * Gets the measured value stored in this object
     * @return the value, as a <code>float</code>
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the measured value stored in this object
     * @param value a <code>float</code>
     */
    public void setValue(float value) {
        assert !Float.isNaN(value);
        this.value = value;
    }

    /**
     * Returns whether or not the value is stored in points (pt)
     * @return <code>true</code> if stored in points
     */
    public boolean isPointValue() {
        return unitType == POINT;
    }

    /**
     * Returns whether or not the value is stored in percent (%)
     * @return <code>true</code> if stored in percent
     */
    public boolean isPercentValue() {
        return unitType == PERCENT;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        UnitValue other = (UnitValue) obj;
        return Integer.compare(unitType, other.unitType) == 0 && Float.compare(value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.unitType;
        hash = 71 * hash + Float.floatToIntBits(this.value);
        return hash;
    }

    @Override
    public String toString() {
        return MessageFormatUtil.format((unitType == PERCENT ? "{0}%" : "{0}pt"), value);
    }
}
