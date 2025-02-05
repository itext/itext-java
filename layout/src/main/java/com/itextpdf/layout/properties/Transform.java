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

import com.itextpdf.kernel.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store and process multiple {@code transform} css property before drawing.
 */
public class Transform {
    private List<SingleTransform> multipleTransform;

    /**
     * Creates a new {@link Transform} instance.
     *
     * @param length the amount of {@link SingleTransform} instances that this {@link Transform} instant shall contain and be able to process
     */
    public Transform(int length) {
        multipleTransform = new ArrayList<SingleTransform>(length);
    }

    /**
     * Adds a {@link SingleTransform} in a list of single transforms to process later.
     *
     * @param singleTransform a {@link SingleTransform} instance
     */
    public void addSingleTransform(SingleTransform singleTransform) {
        multipleTransform.add(singleTransform);
    }

    private List<SingleTransform> getMultipleTransform() {
        return multipleTransform;
    }

    /**
     * Converts the {@link Transform} instance, i.e. the list of {@link SingleTransform} instances,
     * to the equivalent {@link AffineTransform} instance relatively to the available area,
     * including resolving of percent values to point values.
     *
     * @param t      a {@link Transform} instance to convert
     * @param width  the width of available area, the point value of which is equivalent to 100% for percentage resolving
     * @param height the height of available area, the point value of which is equivalent to 100% for percentage resolving
     * @return resulting affine transformation instance, accumulated from {@link Transform}
     */
    public static AffineTransform getAffineTransform(Transform t, float width, float height) {
        List<SingleTransform> multipleTransform = t.getMultipleTransform();
        AffineTransform affineTransform = new AffineTransform();
        for (int k = multipleTransform.size() - 1; k >= 0; k--) {
            SingleTransform transform = multipleTransform.get(k);
            float[] floats = new float[6];
            for (int i = 0; i < 4; i++)
                floats[i] = transform.getFloats()[i];
            for (int i = 4; i < 6; i++)
                floats[i] = transform.getUnitValues()[i - 4].getUnitType() == UnitValue.POINT ?
                        transform.getUnitValues()[i - 4].getValue() : transform.getUnitValues()[i - 4].getValue() / 100 * (i == 4 ? width : height);
            affineTransform.preConcatenate(new AffineTransform(floats));
        }
        return affineTransform;
    }

    /**
     * This class is used to store one {@code transform} function.
     */
    public static class SingleTransform {
        private float a, b, c, d;
        private UnitValue tx, ty;

        /**
         * Creates a default {@link SingleTransform} instance equivalent to no transform.
         */
        public SingleTransform() {
            this.a = 1;
            this.b = 0;
            this.c = 0;
            this.d = 1;
            this.tx = new UnitValue(UnitValue.POINT, 0);
            this.ty = new UnitValue(UnitValue.POINT, 0);
        }

        /**
         * Creates a {@link SingleTransform} instance.
         *
         * @param a  horizontal scaling
         * @param b  vertical skewing
         * @param c  horizontal skewing
         * @param d  vertical scaling
         * @param tx horizontal translation
         * @param ty vertical translation
         */
        public SingleTransform(float a, float b, float c, float d, UnitValue tx, UnitValue ty) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.tx = tx;
            this.ty = ty;
        }

        /**
         * Gets an array of values corresponding to transformation, i.e. scaling and skewing.
         *
         * @return an array of floats
         */
        public float[] getFloats() {
            return new float[]{a, b, c, d};
        }

        /**
         * Gets an array of values corresponding to translation.
         *
         * @return an array of {@link UnitValue}-s
         */
        public UnitValue[] getUnitValues() {
            return new UnitValue[]{tx, ty};
        }
    }
}
