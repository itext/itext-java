/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.colors.gradients;

import java.util.Arrays;
import java.util.Objects;

/**
 * The gradient stop color structure representing the stop color configuration.
 * The stop color consists of:
 * - {@code float[]} rgb color array. Values should be in [0, 1] range. All values outside of
 * this range would be adjusted to the nearest corner of the range.
 * - {@code double} offset and {@link OffsetType} offset type specifies the coordinate of
 * the stop color on the targeting gradient coordinates vector
 * - {@code double} hint offset and {@link HintOffsetType} hint offset type specifies the color
 * transition mid point offset between the current color and the next color
 */
public class GradientColorStop {
    private final float[] rgb;
    private final float opacity;

    private OffsetType offsetType;
    private double offset;
    private double hintOffset = 0d;
    private HintOffsetType hintOffsetType = HintOffsetType.NONE;

    /**
     * Constructor of stop color with with specified rgb color and default ({@link OffsetType#AUTO})
     * offset
     *
     * @param rgb the color value
     */
    public GradientColorStop(float[] rgb) {
        this(rgb, 1f, 0d, OffsetType.AUTO);
    }

    /**
     * Constructor of stop color with with specified rgb color and offset
     *
     * @param rgb        the color value
     * @param offset     the offset value. Makes sense only if the {@code offsetType} is not {@link OffsetType#AUTO}
     * @param offsetType the offset's type
     */
    public GradientColorStop(float[] rgb, double offset, OffsetType offsetType) {
        this(rgb, 1f, offset, offsetType);
    }

    /**
     * Constructor that creates the stop with the same color as the another stop and new offset
     *
     * @param gradientColorStop the gradient stop color from which the color value would be copied
     * @param offset            the new offset. Makes sense only if the {@code offsetType} is not {@link OffsetType#AUTO}
     * @param offsetType        the new offset's type
     */
    public GradientColorStop(GradientColorStop gradientColorStop, double offset, OffsetType offsetType) {
        this(gradientColorStop.getRgbArray(), gradientColorStop.getOpacity(), offset, offsetType);
    }

    private GradientColorStop(float[] rgb, float opacity, double offset, OffsetType offsetType) {
        this.rgb = copyRgbArray(rgb);

        this.opacity = normalize(opacity);

        setOffset(offset, offsetType);
    }

    /**
     * Get the stop color rgb value
     *
     * @return the copy of stop's rgb value
     */
    public float[] getRgbArray() {
        return copyRgbArray(this.rgb);
    }

    // TODO: DEVSIX-4136 make public with opacity logic implementation
    /**
     * Get the stop color opacity value
     *
     * @return the stop color opacity value
     */
    private float getOpacity() {
        return this.opacity;
    }

    /**
     * Get the offset type
     *
     * @return the offset type
     */
    public OffsetType getOffsetType() {
        return offsetType;
    }

    /**
     * Get the offset value
     *
     * @return the offset value
     */
    public double getOffset() {
        return this.offset;
    }

    /**
     * Get the hint offset value
     *
     * @return the hint offset value
     */
    public double getHintOffset() {
        return hintOffset;
    }

    /**
     * Get the hint offset type
     *
     * @return the hint offset type
     */
    public HintOffsetType getHintOffsetType() {
        return hintOffsetType;
    }

    /**
     * Set the offset specified by its value and type
     *
     * @param offset     the offset's value to be set. Makes sense only if the {@code offsetType}
     *                   is not {@link OffsetType#AUTO}
     * @param offsetType the offset's type to be set
     * @return the current {@link GradientColorStop} instance
     */
    public GradientColorStop setOffset(double offset, OffsetType offsetType) {
        this.offsetType = offsetType != null ? offsetType : OffsetType.AUTO;
        this.offset = this.offsetType != OffsetType.AUTO ? offset : 0d;
        return this;
    }

    /**
     * Set the color hint specified by its value and type ({@link GradientColorStop more details}).
     *
     * @param hintOffset     the hint offset's value to be set. Makes sense only
     *                       if the {@code hintOffsetType} is not {@link HintOffsetType#NONE}
     * @param hintOffsetType the hint offset's type to be set
     * @return the current {@link GradientColorStop} instance
     */
    public GradientColorStop setHint(double hintOffset, HintOffsetType hintOffsetType) {
        this.hintOffsetType = hintOffsetType != null ? hintOffsetType : HintOffsetType.NONE;
        this.hintOffset = this.hintOffsetType != HintOffsetType.NONE ? hintOffset : 0d;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GradientColorStop that = (GradientColorStop) o;
        return Float.compare(that.opacity, opacity) == 0 &&
                Double.compare(that.offset, offset) == 0 &&
                Double.compare(that.hintOffset, hintOffset) == 0 &&
                Arrays.equals(rgb, that.rgb) &&
                offsetType == that.offsetType &&
                hintOffsetType == that.hintOffsetType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(opacity, offset, hintOffset);
        result = 31 * result + offsetType.hashCode();
        result = 31 * result + hintOffsetType.hashCode();
        result = 31 * result + Arrays.hashCode(rgb);
        return result;
    }

    private static float normalize(float toNormalize) {
        return toNormalize > 1f ? 1f : toNormalize > 0f ? toNormalize : 0f;
    }

    private static float[] copyRgbArray(float[] toCopy) {
        if (toCopy == null || toCopy.length < 3) {
            return new float[] {0f, 0f, 0f};
        }
        return new float[] {normalize(toCopy[0]), normalize(toCopy[1]), normalize(toCopy[2])};
    }

    /**
     * Represents the possible offset type
     */
    public enum OffsetType {
        /**
         * The absolute offset value from the target coordinates vector's start
         */
        ABSOLUTE,
        /**
         * The automatic offset evaluation. The offset value should be evaluated automatically
         * based on the whole stop colors list specified for the gradient. The general auto offset
         * logic should be the next:
         * - find the previous and the next specified offset or hint offset values
         * - the sublist of sequent auto offsets should spread evenly between the found values
         */
        AUTO,
        /**
         * The relative offset value to the target coordinates vector. The {@code 0} value means
         * the target vector start, the {@code 1} value means the target vector end.
         */
        RELATIVE
    }

    /**
     * Represents the possible hint offset type
     */
    public enum HintOffsetType {
        /**
         * The absolute hint offset value on the target gradient value
         */
        ABSOLUTE_ON_GRADIENT,
        /**
         * The relative hint offset value to the target coordinates vector. The {@code 0} value
         * means the target vector start, the {@code 1} value means the target vector end.
         */
        RELATIVE_ON_GRADIENT,
        /**
         * The relative hint offset value to the interval between the current gradient stop color
         * and the next one.
         */
        RELATIVE_BETWEEN_COLORS,
        /**
         * None hint offset specified
         */
        NONE
    }
}
