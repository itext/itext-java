/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.geom;

import java.util.Arrays;

/**
 * Represents a vector (i.e. a point in space).  This class is completely
 * unrelated to the {@link java.util.Vector} class in the standard JRE.
 * <br><br>
 * For many PDF related operations, the z coordinate is specified as 1
 * This is to support the coordinate transformation calculations.  If it
 * helps, just think of all PDF drawing operations as occurring in a single plane
 * with z=1.
 */
public class Vector {
    /**
     * index of the X coordinate
     */
    public static final int I1 = 0;
    /**
     * index of the Y coordinate
     */
    public static final int I2 = 1;
    /**
     * index of the Z coordinate
     */
    public static final int I3 = 2;

    /**
     * the values inside the vector
     */
    private final float[] vals = new float[] {0, 0, 0};

    /**
     * Creates a new Vector
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public Vector(float x, float y, float z) {
        vals[I1] = x;
        vals[I2] = y;
        vals[I3] = z;
    }

    /**
     * Gets the value from a coordinate of the vector
     *
     * @param index the index of the value to get (I1, I2 or I3)
     * @return a coordinate value
     */
    public float get(int index) {
        return vals[index];
    }

    /**
     * Computes the cross product of this vector and the specified matrix
     *
     * @param by the matrix to cross this vector with
     * @return the result of the cross product
     */
    public Vector cross(Matrix by) {
        float x = vals[I1] * by.get(Matrix.I11) + vals[I2] * by.get(Matrix.I21) + vals[I3] * by.get(Matrix.I31);
        float y = vals[I1] * by.get(Matrix.I12) + vals[I2] * by.get(Matrix.I22) + vals[I3] * by.get(Matrix.I32);
        float z = vals[I1] * by.get(Matrix.I13) + vals[I2] * by.get(Matrix.I23) + vals[I3] * by.get(Matrix.I33);

        return new Vector(x, y, z);
    }

    /**
     * Computes the difference between this vector and the specified vector
     *
     * @param v the vector to subtract from this one
     * @return the results of the subtraction
     */
    public Vector subtract(Vector v) {
        float x = vals[I1] - v.vals[I1];
        float y = vals[I2] - v.vals[I2];
        float z = vals[I3] - v.vals[I3];

        return new Vector(x, y, z);
    }

    /**
     * Computes the cross product of this vector and the specified vector
     *
     * @param with the vector to cross this vector with
     * @return the cross product
     */
    public Vector cross(Vector with) {
        float x = vals[I2] * with.vals[I3] - vals[I3] * with.vals[I2];
        float y = vals[I3] * with.vals[I1] - vals[I1] * with.vals[I3];
        float z = vals[I1] * with.vals[I2] - vals[I2] * with.vals[I1];

        return new Vector(x, y, z);
    }

    /**
     * Normalizes the vector (i.e. returns the unit vector in the same orientation as this vector)
     *
     * @return the unit vector
     */
    public Vector normalize() {
        float l = this.length();
        float x = vals[I1] / l;
        float y = vals[I2] / l;
        float z = vals[I3] / l;
        return new Vector(x, y, z);
    }

    /**
     * Multiplies the vector by a scalar
     *
     * @param by the scalar to multiply by
     * @return the result of the scalar multiplication
     */
    public Vector multiply(float by) {
        float x = vals[I1] * by;
        float y = vals[I2] * by;
        float z = vals[I3] * by;
        return new Vector(x, y, z);
    }

    /**
     * Computes the dot product of this vector with the specified vector
     *
     * @param with the vector to dot product this vector with
     * @return the dot product
     */
    public float dot(Vector with) {
        return vals[I1] * with.vals[I1] + vals[I2] * with.vals[I2] + vals[I3] * with.vals[I3];
    }

    /**
     * Computes the length of this vector
     * <br>
     * <b>Note:</b> If you are working with raw vectors from PDF, be careful -
     * the Z axis will generally be set to 1.  If you want to compute the
     * length of a vector, subtract it from the origin first (this will set
     * the Z axis to 0).
     * <br>
     * For example:
     * <code>aVector.subtract(originVector).length();</code>
     *
     * @return the length of this vector
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * Computes the length squared of this vector.
     *
     * The square of the length is less expensive to compute, and is often
     * useful without taking the square root.
     * <br><br>
     * <b>Note:</b> See the important note under {@link Vector#length()}
     *
     * @return the square of the length of the vector
     */
    public float lengthSquared() {
        return vals[I1] * vals[I1] + vals[I2] * vals[I2] + vals[I3] * vals[I3];
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return vals[I1] + "," + vals[I2] + "," + vals[I3];
    }

    /**
     * Calculates the hashcode using the values.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(vals);
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector other = (Vector) obj;
        if (!Arrays.equals(vals, other.vals)) {
            return false;
        }
        return true;
    }

}
