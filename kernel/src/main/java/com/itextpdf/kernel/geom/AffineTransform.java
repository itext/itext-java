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
package com.itextpdf.kernel.geom;

import java.util.Objects;

/**
 * The {@link AffineTransform} class represents an affine transformation,
 * which is a combination of linear transformations such as translation,
 * scaling, rotation, and shearing which allows preservation of the straightness of lines.
 */
public class AffineTransform implements Cloneable {


    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_IDENTITY = 0;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_TRANSLATION = 1;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_UNIFORM_SCALE = 2;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_GENERAL_SCALE = 4;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_QUADRANT_ROTATION = 8;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_GENERAL_ROTATION = 16;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_GENERAL_TRANSFORM = 32;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_FLIP = 64;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_MASK_SCALE = TYPE_UNIFORM_SCALE | TYPE_GENERAL_SCALE;

    /**
     * The type of affine transformation. See {@link AffineTransform#getType()}.
     */
    public static final int TYPE_MASK_ROTATION = TYPE_QUADRANT_ROTATION | TYPE_GENERAL_ROTATION;

    /**
     * The <code>TYPE_UNKNOWN</code> is an initial type value.
     */
    static final int TYPE_UNKNOWN = -1;

    /**
     * The min value equivalent to zero. If absolute value less then ZERO it considered as zero.
     */
    static final double ZERO = 1E-10;

    /**
     * The values of transformation matrix
     */
    double m00;
    double m10;
    double m01;
    double m11;
    double m02;
    double m12;

    /**
     * The transformation <code>type</code>
     */
    int type;

    /**
     * Create an empty {@link  AffineTransform} instance.
     * The default type is for the transformation is {@code  TYPE_IDENTITY}
     */
    public AffineTransform() {
        type = TYPE_IDENTITY;
        m00 = m11 = 1;
        m10 = m01 = m02 = m12 = 0;
    }

    /**
     * Will create a new {@link AffineTransform} instance with the values provided from the original
     * {@link AffineTransform} instance.
     *
     * @param t The AffineTransform class to be used.
     */
    public AffineTransform(AffineTransform t) {
        this.type = t.type;
        this.m00 = t.m00;
        this.m10 = t.m10;
        this.m01 = t.m01;
        this.m11 = t.m11;
        this.m02 = t.m02;
        this.m12 = t.m12;
    }

    /**
     * Create an {@link AffineTransform} instance with the values provided.
     * The default type is for the transformation is {@code  TYPE_UNKNOWN}
     *
     * @param m00 The value of the first row and first column of the matrix.
     * @param m10 The value of the second row and first column of the matrix.
     * @param m01 The value of the first row and second column of the matrix.
     * @param m11 The value of the second row and second column of the matrix.
     * @param m02 The value of the first row and third column of the matrix.
     * @param m12 The value of the second row and third column of the matrix.
     */
    public AffineTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    /**
     * Create an {@link AffineTransform} instance with the values provided.
     * The default type is for the transformation is {@code  TYPE_UNKNOWN}
     *
     * @param matrix The array of values to be used for the transformation matrix.
     */
    public AffineTransform(float[] matrix) {
        this.type = TYPE_UNKNOWN;
        m00 = matrix[0];
        m10 = matrix[1];
        m01 = matrix[2];
        m11 = matrix[3];
        if (matrix.length > 4) {
            m02 = matrix[4];
            m12 = matrix[5];
        }
    }

    /**
     * Create an {@link AffineTransform} instance with the values provided.
     * The default type is for the transformation is {@code  TYPE_UNKNOWN}
     *
     * @param matrix The array of values to be used for the transformation matrix.
     */
    public AffineTransform(double[] matrix) {
        this.type = TYPE_UNKNOWN;
        m00 = matrix[0];
        m10 = matrix[1];
        m01 = matrix[2];
        m11 = matrix[3];
        if (matrix.length > 4) {
            m02 = matrix[4];
            m12 = matrix[5];
        }
    }

    /**
     * Method returns type of affine transformation.
     * <p>
     * Transform matrix is
     * m00 m01 m02
     * m10 m11 m12
     * <p>
     * According analytic geometry new basis vectors are (m00, m01) and (m10, m11),
     * translation vector is (m02, m12). Original basis vectors are (1, 0) and (0, 1).
     * Type transformations classification:
     * <ul>
     * <li>{@link AffineTransform#TYPE_IDENTITY} - new basis equals original one and zero translation
     * <li>{@link AffineTransform#TYPE_TRANSLATION} - translation vector isn't zero
     * <li>{@link AffineTransform#TYPE_UNIFORM_SCALE} - vectors length of new basis equals
     * <li>{@link AffineTransform#TYPE_GENERAL_SCALE} - vectors length of new basis doesn't equal
     * <li>{@link AffineTransform#TYPE_FLIP} - new basis vector orientation differ from original one
     * <li>{@link AffineTransform#TYPE_QUADRANT_ROTATION} - new basis is rotated by 90, 180, 270, or 360 degrees
     * <li>{@link AffineTransform#TYPE_GENERAL_ROTATION} - new basis is rotated by arbitrary angle
     * <li>{@link AffineTransform#TYPE_GENERAL_TRANSFORM} - transformation can't be inversed
     * </ul>
     *
     * @return the type of this AffineTransform
     */
    public int getType() {
        if (this.type != TYPE_UNKNOWN) {
            return this.type;
        }

        int type = 0;

        if (m00 * m01 + m10 * m11 != 0.0) {
            type |= TYPE_GENERAL_TRANSFORM;
            return type;
        }

        if (m02 != 0.0 || m12 != 0.0) {
            type |= TYPE_TRANSLATION;
        } else if (m00 == 1.0 && m11 == 1.0 && m01 == 0.0 && m10 == 0.0) {
            type = TYPE_IDENTITY;
            return type;
        }

        if (m00 * m11 - m01 * m10 < 0.0) {
            type |= TYPE_FLIP;
        }

        double dx = m00 * m00 + m10 * m10;
        double dy = m01 * m01 + m11 * m11;
        if (dx != dy) {
            type |= TYPE_GENERAL_SCALE;
        } else if (dx != 1.0) {
            type |= TYPE_UNIFORM_SCALE;
        }

        if ((m00 == 0.0 && m11 == 0.0) ||
                (m10 == 0.0 && m01 == 0.0 && (m00 < 0.0 || m11 < 0.0))) {
            type |= TYPE_QUADRANT_ROTATION;
        } else if (m01 != 0.0 || m10 != 0.0) {
            type |= TYPE_GENERAL_ROTATION;
        }

        return type;
    }

    /**
     * Gets the scale factor of the x-axis.
     *
     * @return the scale factor of the x-axis.
     */
    public double getScaleX() {
        return m00;
    }

    /**
     * Gets the scale factor of the y-axis.
     *
     * @return the scale factor of the y-axis.
     */
    public double getScaleY() {
        return m11;
    }

    /**
     * Gets the shear factor of the x-axis.
     *
     * @return the shear factor of the x-axis.
     */
    public double getShearX() {
        return m01;
    }

    /**
     * Gets the shear factor of the y-axis.
     *
     * @return the shear factor of the y-axis.
     */
    public double getShearY() {
        return m10;
    }

    /**
     * Gets translation factor of the x-axis.
     *
     * @return the translation factor of the x-axis.
     */
    public double getTranslateX() {
        return m02;
    }

    /**
     * Gets translation factor of the y-axis.
     *
     * @return the translation factor of the y-axis.
     */
    public double getTranslateY() {
        return m12;
    }

    /**
     * Gets whether this {@link AffineTransform} is an identity transformation.
     *
     * @return {@code true} if this {@link AffineTransform} is an identity transformation, {@code false} otherwise.
     */
    public boolean isIdentity() {
        return getType() == TYPE_IDENTITY;
    }

    /**
     * Fills the matrix parameter with the values of this {@link AffineTransform} instance.
     *
     * @param matrix the array to be filled with the values of this {@link AffineTransform} instance.
     */
    public void getMatrix(float[] matrix) {
        matrix[0] = (float) m00;
        matrix[1] = (float) m10;
        matrix[2] = (float) m01;
        matrix[3] = (float) m11;
        if (matrix.length > 4) {
            matrix[4] = (float) m02;
            matrix[5] = (float) m12;
        }
    }

    /**
     * Fills the matrix parameter with the values of this {@link AffineTransform} instance.
     *
     * @param matrix the array to be filled with the values of this {@link AffineTransform} instance.
     */
    public void getMatrix(double[] matrix) {
        matrix[0] = m00;
        matrix[1] = m10;
        matrix[2] = m01;
        matrix[3] = m11;
        if (matrix.length > 4) {
            matrix[4] = m02;
            matrix[5] = m12;
        }
    }

    /**
     * Gets the determinant of the matrix representation of this {@link AffineTransform}.
     *
     * @return the determinant of the matrix representation of this {@link AffineTransform}.
     */
    public double getDeterminant() {
        return m00 * m11 - m01 * m10;
    }

    /**
     * Sets the values of this {@link AffineTransform} instance to the values provided.
     * The type of the transformation is set to {@code TYPE_UNKNOWN}.
     *
     * @param m00 The value of the first row and first column of the matrix.
     * @param m10 The value of the second row and first column of the matrix.
     * @param m01 The value of the first row and second column of the matrix.
     * @param m11 The value of the second row and second column of the matrix.
     * @param m02 The value of the first row and third column of the matrix.
     * @param m12 The value of the second row and third column of the matrix.
     */
    public void setTransform(float m00, float m10, float m01, float m11, float m02, float m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    /**
     * Sets the values of this {@link AffineTransform} instance to the values provided.
     * The type of the transformation is set to {@code TYPE_UNKNOWN}.
     *
     * @param m00 The value of the first row and first column of the matrix.
     * @param m10 The value of the second row and first column of the matrix.
     * @param m01 The value of the first row and second column of the matrix.
     * @param m11 The value of the second row and second column of the matrix.
     * @param m02 The value of the first row and third column of the matrix.
     * @param m12 The value of the second row and third column of the matrix.
     */
    public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.type = TYPE_UNKNOWN;
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    /**
     * Sets the values of this {@link AffineTransform} instance to the values provided.
     *
     * @param t The {@link AffineTransform} instance to be used.
     */
    public void setTransform(AffineTransform t) {
        type = t.type;
        setTransform(t.m00, t.m10, t.m01, t.m11, t.m02, t.m12);
    }

    /**
     * Sets this {@link AffineTransform} to the identity transformation.
     */
    public void setToIdentity() {
        type = TYPE_IDENTITY;
        m00 = m11 = 1;
        m10 = m01 = m02 = m12 = 0;
    }

    /**
     * Sets this {@link AffineTransform} to represent a translation transformation.
     *
     * @param mx The value of the translation on the x-axis.
     * @param my The value of the translation on the y-axis.
     */
    public void setToTranslation(double mx, double my) {
        m00 = m11 = 1;
        m01 = m10 = 0;
        m02 = mx;
        m12 = my;
        if (mx == 0 && my == 0) {
            type = TYPE_IDENTITY;
        } else {
            type = TYPE_TRANSLATION;
        }
    }

    /**
     * Sets this {@link AffineTransform} to represent a scale transformation.
     *
     * @param scx The value of the scale factor on the x-axis.
     * @param scy The value of the scale factor on the y-axis.
     */
    public void setToScale(double scx, double scy) {
        m00 = scx;
        m11 = scy;
        m10 = m01 = m02 = m12 = 0;
        if (scx != 1.0 || scy != 1) {
            type = TYPE_UNKNOWN;
        } else {
            type = TYPE_IDENTITY;
        }
    }

    /**
     * Sets this {@link AffineTransform} to represent a shear transformation.
     *
     * @param shx The value of the shear factor on the x-axis.
     * @param shy The value of the shear factor on the y-axis.
     */
    public void setToShear(double shx, double shy) {
        m00 = m11 = 1;
        m02 = m12 = 0;
        m01 = shx;
        m10 = shy;
        if (shx != 0.0 || shy != 0.0) {
            type = TYPE_UNKNOWN;
        } else {
            type = TYPE_IDENTITY;
        }
    }

    /**
     * Set this affine transformation to represent a rotation over the passed angle
     *
     * @param angle angle to rotate over in radians
     */
    public void setToRotation(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        if (Math.abs(cos) < ZERO) {
            cos = 0.0;
            sin = sin > 0.0 ? 1.0 : -1.0;
        } else if (Math.abs(sin) < ZERO) {
            sin = 0.0;
            cos = cos > 0.0 ? 1.0 : -1.0;
        }
        m00 = m11 = (float) cos;
        m01 = (float) -sin;
        m10 = (float) sin;
        m02 = m12 = 0;
        type = TYPE_UNKNOWN;
    }

    /**
     * Set this affine transformation to represent a rotation over the passed angle,
     * using the passed point as the center of rotation
     *
     * @param angle angle to rotate over in radians
     * @param px    x-coordinate of center of rotation
     * @param py    y-coordinate of center of rotation
     */
    public void setToRotation(double angle, double px, double py) {
        setToRotation(angle);
        m02 = px * (1 - m00) + py * m10;
        m12 = py * (1 - m00) - px * m10;
        type = TYPE_UNKNOWN;
    }


    /**
     * Get a new {@link  AffineTransform} instance representing a translation over the passed values
     *
     * @param mx x-coordinate of translation
     * @param my y-coordinate of translation
     * @return {@link AffineTransform} representing the translation
     */
    public static AffineTransform getTranslateInstance(double mx, double my) {
        AffineTransform t = new AffineTransform();
        t.setToTranslation(mx, my);
        return t;
    }

    /**
     * Get a new {@link  AffineTransform} instance representing a scale over the passed values
     *
     * @param scx scale factor on the x-axis
     * @param scY scale factor on the y-axis
     * @return {@link AffineTransform} representing the scale
     */
    public static AffineTransform getScaleInstance(double scx, double scY) {
        AffineTransform t = new AffineTransform();
        t.setToScale(scx, scY);
        return t;
    }

    /**
     * Get a new {@link  AffineTransform} instance representing a shear over the passed values
     *
     * @param shx shear factor on the x-axis
     * @param shy shear factor on the y-axis
     * @return {@link AffineTransform} representing the shear
     */
    public static AffineTransform getShearInstance(double shx, double shy) {
        AffineTransform m = new AffineTransform();
        m.setToShear(shx, shy);
        return m;
    }

    /**
     * Get an affine transformation representing a counter-clockwise rotation over the passed angle
     *
     * @param angle angle in radians to rotate over
     * @return {@link AffineTransform} representing the rotation
     */
    public static AffineTransform getRotateInstance(double angle) {
        AffineTransform t = new AffineTransform();
        t.setToRotation(angle);
        return t;
    }

    /**
     * Get an affine transformation representing a counter-clockwise rotation over the passed angle,
     * using the passed point as the center of rotation
     *
     * @param angle angle in radians to rotate over
     * @param x     x-coordinate of center of rotation
     * @param y     y-coordinate of center of rotation
     * @return {@link AffineTransform} representing the rotation
     */
    public static AffineTransform getRotateInstance(double angle, double x, double y) {
        AffineTransform t = new AffineTransform();
        t.setToRotation(angle, x, y);
        return t;
    }

    public void translate(double mx, double my) {
        concatenate(AffineTransform.getTranslateInstance(mx, my));
    }

    public void scale(double scx, double scy) {
        concatenate(AffineTransform.getScaleInstance(scx, scy));
    }

    public void shear(double shx, double shy) {
        concatenate(AffineTransform.getShearInstance(shx, shy));
    }

    /**
     * Add a counter-clockwise rotation to this transformation
     *
     * @param angle angle in radians to rotate over
     */
    public void rotate(double angle) {
        concatenate(AffineTransform.getRotateInstance(angle));
    }

    /**
     * Add a counter-clockwise rotation to this transformation,
     * using the passed point as the center of rotation
     *
     * @param angle angle in radians to rotate over
     * @param px    x-coordinate of center of rotation
     * @param py    y-coordinate of center of rotation
     */
    public void rotate(double angle, double px, double py) {
        concatenate(AffineTransform.getRotateInstance(angle, px, py));
    }

    /**
     * Multiply matrix of two AffineTransform objects
     *
     * @param t1 - the AffineTransform object is a multiplicand.
     * @param t2 - the AffineTransform object is a multiplier.
     * @return an AffineTransform object that is a result of t1 multiplied by matrix t2.
     */
    AffineTransform multiply(AffineTransform t1, AffineTransform t2) {
        return new AffineTransform(
                t1.m00 * t2.m00 + t1.m10 * t2.m01,
                t1.m00 * t2.m10 + t1.m10 * t2.m11,
                t1.m01 * t2.m00 + t1.m11 * t2.m01,
                t1.m01 * t2.m10 + t1.m11 * t2.m11,
                t1.m02 * t2.m00 + t1.m12 * t2.m01 + t2.m02,
                t1.m02 * t2.m10 + t1.m12 * t2.m11 + t2.m12);
    }

    /**
     * Multiply matrix of two AffineTransform objects
     *
     * @param t - the AffineTransform object is a multiplier.
     */
    public void concatenate(AffineTransform t) {
        setTransform(multiply(t, this));
    }

    /**
     * Multiply matrix of two AffineTransform objects
     *
     * @param t - the AffineTransform object is a multiplicand.
     */
    public void preConcatenate(AffineTransform t) {
        setTransform(multiply(this, t));
    }

    /**
     * Creates a new {@link AffineTransform} object that is invert of this {@link AffineTransform} object.
     *
     * @return a new {@link AffineTransform} object that is invert of this {@link AffineTransform} object.
     * @throws NoninvertibleTransformException if this {@link AffineTransform} object cannot be inverted.
     */
    public AffineTransform createInverse() throws NoninvertibleTransformException {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
            // awt.204=Determinant is zero
            //$NON-NLS-1$
            throw new NoninvertibleTransformException(NoninvertibleTransformException.DETERMINANT_IS_ZERO_CANNOT_INVERT_TRANSFORMATION);
        }
        return new AffineTransform(
                m11 / det,
                -m10 / det,
                -m01 / det,
                m00 / det,
                (m01 * m12 - m11 * m02) / det,
                (m10 * m02 - m00 * m12) / det
        );
    }

    /**
     * Transform the point according to the values of this {@link AffineTransform} object.
     *
     * @param src The point to be transformed.
     * @param dst The point that will hold the result of the transformation.
     * @return The point that holds the result of the transformation.
     */
    public Point transform(Point src, Point dst) {
        if (dst == null) {
            dst = new Point();
        }

        double x = src.getX();
        double y = src.getY();

        dst.setLocation(x * m00 + y * m01 + m02, x * m10 + y * m11 + m12);
        return dst;
    }

    /**
     * Transform the array of points according to the values of this {@link AffineTransform} object.
     *
     * @param src    The array of points to be transformed.
     * @param srcOff The offset of the first point in the array.
     * @param dst    The array of points that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void transform(Point[] src, int srcOff, Point[] dst, int dstOff, int length) {
        while (--length >= 0) {
            Point srcPoint = src[srcOff++];
            double x = srcPoint.getX();
            double y = srcPoint.getY();
            Point dstPoint = dst[dstOff];
            if (dstPoint == null) {
                dstPoint = new Point();
            }
            dstPoint.setLocation(x * m00 + y * m01 + m02, x * m10 + y * m11 + m12);
            dst[dstOff++] = dstPoint;
        }
    }

    /**
     * Transform the array of points according to the values of this {@link AffineTransform} object.
     *
     * @param src    The array of points to be transformed.
     * @param srcOff The offset of the first point in the array.
     * @param dst    The array of points that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void transform(double[] src, int srcOff, double[] dst, int dstOff, int length) {
        int step = 2;
        if (src == dst && srcOff < dstOff && dstOff < srcOff + length * 2) {
            srcOff = srcOff + length * 2 - 2;
            dstOff = dstOff + length * 2 - 2;
            step = -2;
        }
        while (--length >= 0) {
            double x = src[srcOff + 0];
            double y = src[srcOff + 1];
            dst[dstOff + 0] = x * m00 + y * m01 + m02;
            dst[dstOff + 1] = x * m10 + y * m11 + m12;
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Transform the array of points according to the values of this {@link AffineTransform} object.
     *
     * @param src    The array of points to be transformed.
     * @param srcOff The offset of the first point in the array.
     * @param dst    The array of points that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void transform(float[] src, int srcOff, float[] dst, int dstOff, int length) {
        int step = 2;
        if (src == dst && srcOff < dstOff && dstOff < srcOff + length * 2) {
            srcOff = srcOff + length * 2 - 2;
            dstOff = dstOff + length * 2 - 2;
            step = -2;
        }
        while (--length >= 0) {
            float x = src[srcOff + 0];
            float y = src[srcOff + 1];
            dst[dstOff + 0] = (float) (x * m00 + y * m01 + m02);
            dst[dstOff + 1] = (float) (x * m10 + y * m11 + m12);
            srcOff += step;
            dstOff += step;
        }
    }

    /**
     * Transform the array of points according to the values of this {@link AffineTransform} object.
     *
     * @param src    The array of points to be transformed.
     * @param srcOff The offset of the first point in the array.
     * @param dst    The array of points that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void transform(float[] src, int srcOff, double[] dst, int dstOff, int length) {
        while (--length >= 0) {
            float x = src[srcOff++];
            float y = src[srcOff++];
            dst[dstOff++] = x * m00 + y * m01 + m02;
            dst[dstOff++] = x * m10 + y * m11 + m12;
        }
    }

    /**
     * Transform the array of points according to the values of this {@link AffineTransform} object.
     *
     * @param src    The array of points to be transformed.
     * @param srcOff The offset of the first point in the array.
     * @param dst    The array of points that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void transform(double[] src, int srcOff, float[] dst, int dstOff, int length) {
        while (--length >= 0) {
            double x = src[srcOff++];
            double y = src[srcOff++];
            dst[dstOff++] = (float) (x * m00 + y * m01 + m02);
            dst[dstOff++] = (float) (x * m10 + y * m11 + m12);
        }
    }

    /**
     * Performs  the transformation on the source point and stores the result in the destination point.
     *
     * @param src The source point to be transformed.
     * @param dst The destination point that will hold the result of the transformation.
     * @return The modified destination point.
     */
    public Point deltaTransform(Point src, Point dst) {
        if (dst == null) {
            dst = new Point();
        }

        double x = src.getX();
        double y = src.getY();

        dst.setLocation(x * m00 + y * m01, x * m10 + y * m11);
        return dst;
    }

    /**
     * Performs the delta transformation on the source array of points and stores the result in
     * the destination array of points.
     *
     * @param src    The source array of data to be transformed.
     * @param srcOff The offset of the first point in the source array.
     * @param dst    The destination array of data that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     */
    public void deltaTransform(double[] src, int srcOff, double[] dst, int dstOff, int length) {
        while (--length >= 0) {
            double x = src[srcOff++];
            double y = src[srcOff++];
            dst[dstOff++] = x * m00 + y * m01;
            dst[dstOff++] = x * m10 + y * m11;
        }
    }

    /**
     * Performs the inverse transformation on the source point and stores the result in the destination point.
     *
     * @param src The source point to be transformed.
     * @param dst The destination point that will hold the result of the transformation.
     * @return The modified destination point.
     * @throws NoninvertibleTransformException if the matrix cannot be inverted.
     */
    public Point inverseTransform(Point src, Point dst) throws NoninvertibleTransformException {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
            // awt.204=Determinant is zero
            //$NON-NLS-1$
            throw new NoninvertibleTransformException(NoninvertibleTransformException.DETERMINANT_IS_ZERO_CANNOT_INVERT_TRANSFORMATION);
        }

        if (dst == null) {
            dst = new Point();
        }

        double x = src.getX() - m02;
        double y = src.getY() - m12;

        dst.setLocation((x * m11 - y * m01) / det, (y * m00 - x * m10) / det);
        return dst;
    }

    /**
     * Performs the inverse transformation on the source array of points and stores the result
     * in the destination array of points.
     *
     * @param src    The source array of data to be transformed.
     * @param srcOff The offset of the first point in the source array.
     * @param dst    The destination array of data that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     * @throws NoninvertibleTransformException if the matrix cannot be inverted.
     */
    public void inverseTransform(double[] src, int srcOff, double[] dst, int dstOff, int length)
            throws NoninvertibleTransformException {
        double det = getDeterminant();
        if (Math.abs(det) < ZERO) {
            // awt.204=Determinant is zero
            //$NON-NLS-1$
            throw new NoninvertibleTransformException(NoninvertibleTransformException.DETERMINANT_IS_ZERO_CANNOT_INVERT_TRANSFORMATION);
        }

        while (--length >= 0) {
            double x = src[srcOff++] - m02;
            double y = src[srcOff++] - m12;
            dst[dstOff++] = (x * m11 - y * m01) / det;
            dst[dstOff++] = (y * m00 - x * m10) / det;
        }
    }

    /**
     * Performs the inverse transformation on the source array of points and stores the result
     * in the destination array of points.
     *
     * @param src    The source array of data to be transformed.
     * @param srcOff The offset of the first point in the source array.
     * @param dst    The destination array of data that will hold the result of the transformation.
     * @param dstOff The offset of the first point in the destination array.
     * @param length The number of points to be transformed.
     * @throws NoninvertibleTransformException if the matrix cannot be inverted.
     */
    public void inverseTransform(float[] src, int srcOff, float[] dst, int dstOff, int length)
            throws NoninvertibleTransformException {
        float det = (float) getDeterminant();
        if (Math.abs(det) < ZERO) {
            // awt.204=Determinant is zero
            //$NON-NLS-1$
            throw new NoninvertibleTransformException(NoninvertibleTransformException.DETERMINANT_IS_ZERO_CANNOT_INVERT_TRANSFORMATION);
        }

        while (--length >= 0) {
            float x = (float) (src[srcOff++] - m02);
            float y = (float) (src[srcOff++] - m12);
            dst[dstOff++] = (float) ((x * m11 - y * m01) / det);
            dst[dstOff++] = (float) ((y * m00 - x * m10) / det);
        }
    }

    /**
     * Creates a "deep copy" of this AffineTransform, meaning the object returned by this method will be independent
     * of the object being cloned.
     *
     * @return the copied AffineTransform.
     */
    @Override
    public AffineTransform clone() throws CloneNotSupportedException {
        // super.clone is safe to return since all of the AffineTransform's fields are primitive.
        return (AffineTransform) super.clone();

    }

    /**
     * Compares this AffineTransform with the specified Object.
     * If the object is the same as this AffineTransform, this method returns true.
     * Otherwise, this method checks if the Object is an instance of AffineTransform and if the values of the two
     * AffineTransforms are equal.
     *
     * @param o The object to compare this AffineTransform with.
     * @return {@code true} if the object is the same as this AffineTransform, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AffineTransform that = (AffineTransform) o;

        return Double.compare(that.m00, m00) == 0 &&
                Double.compare(that.m10, m10) == 0 &&
                Double.compare(that.m01, m01) == 0 &&
                Double.compare(that.m11, m11) == 0 &&
                Double.compare(that.m02, m02) == 0 &&
                Double.compare(that.m12, m12) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(m00, m10, m01, m11, m02, m12);
    }
}
