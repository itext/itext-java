/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.kernel.geom.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sequence of 2D transformations to be applied during rendering.
 *
 * <p>
 * This class can be used to compose translations, scaling, skewing, rotations,
 * and lightweight 2D approximations of 3D rotations. It is used both for CSS-derived
 * transforms and for programmatically defined layout transformations.
 */
public class Transform {
    private List<SingleTransform> multipleTransform;

    /**
     * Creates a new {@link Transform} instance.
     */
    public Transform() {
        multipleTransform = new ArrayList<>();
    }

    /**
     * Creates a new {@link Transform} instance.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param a horizontal scaling
     * @param b vertical skewing
     * @param c horizontal skewing
     * @param d vertical scaling
     * @param e horizontal translation
     * @param f vertical translation
     */
    public Transform(float a, float b, float c, float d, UnitValue e, UnitValue f) {
        this();
        multipleTransform.add(new SingleTransform(a, b, c, d, e, f));
    }

    /**
     * Creates a new {@link Transform} instance.
     *
     * @param length the amount of {@link SingleTransform} instances that this {@link Transform} instant shall contain and be able to process
     *
     * @deprecated in favor of {@link #Transform()}
     */
    @Deprecated
    public Transform(int length) {
        multipleTransform = new ArrayList<>(length);
    }

    /**
     * Adds a {@link SingleTransform} in a list of single transforms to process later.
     *
     * @param singleTransform a {@link SingleTransform} instance
     *
     * @deprecated in favor of {@link #addTransform}
     */
    @Deprecated
    public void addSingleTransform(SingleTransform singleTransform) {
        multipleTransform.add(singleTransform);
    }

    /**
     * Adds a {@link SingleTransform} in a list of single transforms to process later.
     *
     * @param singleTransform a {@link SingleTransform} instance
     *
     * @return this {@link Transform} instance
     */
    public Transform addTransform(SingleTransform singleTransform) {
        multipleTransform.add(singleTransform);

        return this;
    }

    /**
     * Appends a translation transform by the given point-unit offsets.
     *
     * @param x the horizontal translation in points
     * @param y the vertical translation in points
     *
     * @return this {@link Transform} instance
     */
    public Transform translate(float x, float y) {
        multipleTransform.add(new SingleTransform(1, 0, 0, 1,
                UnitValue.createPointValue(x), UnitValue.createPointValue(y)));

        return this;
    }

    /**
     * Appends a translation transform by the given {@link UnitValue} offsets.
     *
     * <p>
     * Both point and percentage values are supported; percentages are resolved
     * relative to the width (for {@code x}) and the height (for {@code y}) of
     * the available area at render time.
     *
     * @param x the horizontal translation as a {@link UnitValue}
     * @param y the vertical translation as a {@link UnitValue}
     *
     * @return this {@link Transform} instance
     */
    public Transform translate(UnitValue x, UnitValue y) {
        multipleTransform.add(new SingleTransform(1, 0, 0, 1, x, y));

        return this;
    }

    /**
     * Appends a horizontal scaling transform.
     *
     * <p>
     * The element is stretched or compressed along the X axis by the given factor.
     * A value of {@code 1} leaves the width unchanged, values greater than {@code 1}
     * widen the element, and values between {@code 0} and {@code 1} narrow it.
     *
     * <p>
     * Passing {@code 0} collapses the element to zero width, making it invisible.
     * Passing a negative value flips the element horizontally and scales it by the
     * absolute value of the factor.
     *
     * @param scX the horizontal scale factor
     *
     * @return this {@link Transform} instance
     */
    public Transform scaleX(float scX) {
        multipleTransform.add(new SingleTransform(scX, 0, 0, 1,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends a vertical scaling transform.
     *
     * <p>
     * The element is stretched or compressed along the Y axis by the given factor.
     * A value of {@code 1} leaves the height unchanged, values greater than {@code 1}
     * increase the height, and values between {@code 0} and {@code 1} reduce it.
     *
     * <p>
     * Passing {@code 0} collapses the element to zero height, making it invisible.
     * Passing a negative value flips the element vertically and scales it by the
     * absolute value of the factor.
     *
     * @param scY the vertical scale factor
     *
     * @return this {@link Transform} instance
     */
    public Transform scaleY(float scY) {
        multipleTransform.add(new SingleTransform(1, 0, 0, scY,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends a horizontal skewing (shearing) transform.
     *
     * <p>
     * Vertical lines are tilted by the angle given in radians, shifting X coordinates
     * proportionally to their Y position. Equivalent to the CSS {@code skewX()} function.
     *
     * @param skewAngleX the skew angle, in radians
     *
     * @return this {@link Transform} instance
     */
    public Transform skewX(float skewAngleX) {
        multipleTransform.add(new SingleTransform(1, 0, (float) Math.tan(skewAngleX), 1,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends a vertical skewing (shearing) transform.
     *
     * <p>
     * Horizontal lines are tilted by the angle given in radians, shifting Y coordinates
     * proportionally to their X position. Equivalent to the CSS {@code skewY()} function.
     *
     * @param skewAngleY the skew angle, in radians
     *
     * @return this {@link Transform} instance
     */
    public Transform skewY(float skewAngleY) {
        multipleTransform.add(new SingleTransform(1, (float) Math.tan(skewAngleY), 0, 1,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends an orthographic 2D approximation of a 3D rotation around the X axis.
     *
     * <p>
     * This simulation applies vertical foreshortening only, equivalent to scaling the Y axis
     * by {@code cos(angle)}. Perspective distortion is not applied.
     *
     * @param angle the rotation angle around the X axis, in radians
     *
     * @return this {@link Transform} instance
     */
    public Transform simulateRotateX(float angle) {
        multipleTransform.add(new SingleTransform(1, 0, 0, (float) Math.cos(angle),
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends an orthographic 2D approximation of a 3D rotation around the Y axis.
     *
     * <p>
     * This simulation applies horizontal foreshortening only, equivalent to scaling the X axis
     * by {@code cos(angle)}. Perspective distortion is not applied.
     *
     * @param angle the rotation angle around the Y axis, in radians
     *
     * @return this {@link Transform} instance
     */
    public Transform simulateRotateY(float angle) {
        multipleTransform.add(new SingleTransform((float) Math.cos(angle), 0, 0, 1,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends a counter-clockwise rotation transform.
     *
     * <p>
     * The rotation maps to the affine matrix {@code [cos θ, sin θ, -sin θ, cos θ, 0, 0]},
     * where θ is the supplied angle in radians. A positive angle rotates
     * counter-clockwise in the PDF coordinate system (Y-axis pointing upward).
     *
     * <p>
     * Note: the renderer applies every {@link Transform} centered on the element's occupied area,
     * so this effectively rotates around the <b>center of the element</b>, not around the
     * element origin (0, 0).
     *
     * <p>
     * To rotate around a point offset from the element's center use
     * {@link #rotate(float, float, float)}.
     *
     * @param angle the counter-clockwise rotation angle, in radians
     *
     * @return this {@link Transform} instance
     */
    public Transform rotate(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        multipleTransform.add(new SingleTransform(cos, sin, -sin, cos,
                UnitValue.createPointValue(0), UnitValue.createPointValue(0)));

        return this;
    }

    /**
     * Appends a counter-clockwise rotation transform around a point offset from the
     * element's center by {@code (cx, cy)}.
     *
     * <p>
     * Because the renderer already centers the coordinate system on the element's occupied area
     * before applying the transform, {@code (cx, cy)} are interpreted as offsets from that
     * center. Passing {@code (0, 0)} is equivalent to calling {@link #rotate(float)}.
     *
     * <p>
     * A positive angle rotates counter-clockwise in the PDF coordinate system
     * (Y-axis pointing upward).
     *
     * @param angle the counter-clockwise rotation angle, in radians
     * @param cx    horizontal offset from the element's center to the pivot point, in points
     * @param cy    vertical offset from the element's center to the pivot point, in points
     *
     * @return this {@link Transform} instance
     */
    public Transform rotate(float angle, float cx, float cy) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float e = cx * (1 - cos) + cy * sin;
        float f = cy * (1 - cos) - cx * sin;
        multipleTransform.add(new SingleTransform(cos, sin, -sin, cos,
                UnitValue.createPointValue(e), UnitValue.createPointValue(f)));

        return this;
    }

    /**
     * Appends a counter-clockwise rotation transform around a pivot point specified as
     * {@link UnitValue} offsets from the element's center.
     *
     * <p>
     * Both point and percentage {@link UnitValue}s are supported. Percentages for {@code cx} are
     * resolved relative to the element's width and percentages for {@code cy} relative to its
     * height at render time, consistent with how {@link #translate(UnitValue, UnitValue)} works.
     *
     * <p>
     * Passing {@code UnitValue.createPointValue(0)} for both {@code cx} and {@code cy} is
     * equivalent to calling {@link #rotate(float)}.
     * A positive angle rotates counter-clockwise in the PDF coordinate system
     * (Y-axis pointing upward).
     *
     * @param angle the counter-clockwise rotation angle, in radians
     * @param cx    horizontal offset from the element's center to the pivot point
     * @param cy    vertical offset from the element's center to the pivot point
     *
     * @return this {@link Transform} instance
     */
    public Transform rotate(float angle, UnitValue cx, UnitValue cy) {
        UnitValue negCx = new UnitValue(cx.getUnitType(), -cx.getValue());
        UnitValue negCy = new UnitValue(cy.getUnitType(), -cy.getValue());

        // The transform list is applied in reverse, so we append in reverse of conceptual order:
        // conceptual: translate(-cx, -cy) -> rotate -> translate(cx, cy)
        // append:     translate(cx, cy)   -> rotate -> translate(-cx, -cy)
        translate(cx, cy);
        rotate(angle);
        translate(negCx, negCy);

        return this;
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
        private final float a;
        private final float b;
        private final float c;
        private final float d;
        private final UnitValue e;
        private final UnitValue f;

        /**
         * Creates a default {@link SingleTransform} instance equivalent to no transform.
         */
        public SingleTransform() {
            this.a = 1;
            this.b = 0;
            this.c = 0;
            this.d = 1;
            this.e = new UnitValue(UnitValue.POINT, 0);
            this.f = new UnitValue(UnitValue.POINT, 0);
        }

        /**
         * Creates a {@link SingleTransform} instance.
         *
         * <p>
         * Detailed explanation of {@code [a b c d e f]} parameters of transformation
         * matrix can be found in {@link Matrix} documentation.
         *
         * @param a horizontal scaling
         * @param b vertical skewing
         * @param c horizontal skewing
         * @param d vertical scaling
         * @param e horizontal translation
         * @param f vertical translation
         */
        public SingleTransform(float a, float b, float c, float d, UnitValue e, UnitValue f) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
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
            return new UnitValue[]{e, f};
        }
    }
}
