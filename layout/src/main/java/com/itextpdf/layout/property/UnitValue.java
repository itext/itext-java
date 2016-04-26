package com.itextpdf.layout.property;

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
        this.value = value;
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

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isPointValue() {
        return unitType == POINT;
    }

    public boolean isPercentValue() {
        return unitType == PERCENT;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnitValue)) {
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
}
