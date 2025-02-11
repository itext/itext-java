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
package com.itextpdf.kernel.geom;

import java.util.Arrays;

/**
 * Keeps all the values of a 3 by 3 matrix and allows you to
 * do some math with matrices.
 */
public class Matrix {

    /** the row=1, col=1 position ('a') in the matrix. */
    public static final int I11 = 0;
    /** the row=1, col=2 position ('b') in the matrix. */
    public static final int I12 = 1;
    /** the row=1, col=3 position (always 0 for 2-D) in the matrix. */
    public static final int I13 = 2;
    /** the row=2, col=1 position ('c') in the matrix. */
    public static final int I21 = 3;
    /** the row=2, col=2 position ('d') in the matrix. */
    public static final int I22 = 4;
    /** the row=2, col=3 position (always 0 for 2-D) in the matrix. */
    public static final int I23 = 5;
    /** the row=3, col=1 ('e', or X translation) position in the matrix. */
    public static final int I31 = 6;
    /** the row=3, col=2 ('f', or Y translation) position in the matrix. */
    public static final int I32 = 7;
    /** the row=3, col=3 position (always 1 for 2-D) in the matrix. */
    public static final int I33 = 8;

    /**
     * The values inside the matrix (the identity matrix by default).
     *
     * <p>
     * For reference, the indeces are as follows:
     * <br>I11 I12 I13
     * <br>I21 I22 I23
     * <br>I31 I32 I33
     */
    private final float[] vals = new float[]{
            1,0,0,
            0,1,0,
            0,0,1
    };

    /**
     * constructs a new Matrix with identity.
     */
    public Matrix() {
    }

    /**
     * Constructs a matrix that represents translation.
     *
     * @param tx x-axis translation
     * @param ty y-axis translation
     */
    public Matrix(float tx, float ty) {
        vals[I31] = tx;
        vals[I32] = ty;
    }

    /**
     * Creates a Matrix with 9 specified entries.
     *
     * @param e11 element at position (1,1)
     * @param e12 element at position (1,2)
     * @param e13 element at position (1,3)
     * @param e21 element at position (2,1)
     * @param e22 element at position (2,2)
     * @param e23 element at position (2,3)
     * @param e31 element at position (3,1)
     * @param e32 element at position (3,2)
     * @param e33 element at position (3,3)
     */
    public Matrix(float e11, float e12, float e13, float e21, float e22, float e23, float e31, float e32, float e33){
        vals[I11] = e11;
        vals[I12] = e12;
        vals[I13] = e13;
        vals[I21] = e21;
        vals[I22] = e22;
        vals[I23] = e23;
        vals[I31] = e31;
        vals[I32] = e32;
        vals[I33] = e33;
    }

    /**
     * Creates a Matrix with 6 specified entries.
     * The third column will always be [0 0 1]
     * (row, column)
     *
     * @param a element at (1,1)
     * @param b element at (1,2)
     * @param c element at (2,1)
     * @param d element at (2,2)
     * @param e element at (3,1)
     * @param f element at (3,2)
     */
    public Matrix(float a, float b, float c, float d, float e, float f){
        vals[I11] = a;
        vals[I12] = b;
        vals[I13] = 0;
        vals[I21] = c;
        vals[I22] = d;
        vals[I23] = 0;
        vals[I31] = e;
        vals[I32] = f;
        vals[I33] = 1;
    }

    /**
     * Gets a specific value inside the matrix.
     *
     * <p>
     * For reference, the indeces are as follows:
     * <br>I11 I12 I13
     * <br>I21 I22 I23
     * <br>I31 I32 I33
     *
     * @param index an array index corresponding with a value inside the matrix
     * @return the value at that specific position.
     */
    public float get(int index){
        return vals[index];
    }

    /**
     * multiplies this matrix by 'b' and returns the result.
     * See http://en.wikipedia.org/wiki/Matrix_multiplication
     *
     * @param by The matrix to multiply by
     * @return the resulting matrix
     */
    public Matrix multiply(Matrix by){
        Matrix rslt = new Matrix();

        float[] a = vals;
        float[] b = by.vals;
        float[] c = rslt.vals;

        c[I11] = a[I11]*b[I11] + a[I12]*b[I21] + a[I13]*b[I31];
        c[I12] = a[I11]*b[I12] + a[I12]*b[I22] + a[I13]*b[I32];
        c[I13] = a[I11]*b[I13] + a[I12]*b[I23] + a[I13]*b[I33];
        c[I21] = a[I21]*b[I11] + a[I22]*b[I21] + a[I23]*b[I31];
        c[I22] = a[I21]*b[I12] + a[I22]*b[I22] + a[I23]*b[I32];
        c[I23] = a[I21]*b[I13] + a[I22]*b[I23] + a[I23]*b[I33];
        c[I31] = a[I31]*b[I11] + a[I32]*b[I21] + a[I33]*b[I31];
        c[I32] = a[I31]*b[I12] + a[I32]*b[I22] + a[I33]*b[I32];
        c[I33] = a[I31]*b[I13] + a[I32]*b[I23] + a[I33]*b[I33];

        return rslt;
    }

    /**
     * Adds a matrix from this matrix and returns the results.
     *
     * @param arg the matrix to subtract from this matrix
     * @return a Matrix object
     */
    public Matrix add(Matrix arg){
        Matrix rslt = new Matrix();

        float[] a = vals;
        float[] b = arg.vals;
        float[] c = rslt.vals;

        c[I11] = a[I11]+b[I11];
        c[I12] = a[I12]+b[I12];
        c[I13] = a[I13]+b[I13];
        c[I21] = a[I21]+b[I21];
        c[I22] = a[I22]+b[I22];
        c[I23] = a[I23]+b[I23];
        c[I31] = a[I31]+b[I31];
        c[I32] = a[I32]+b[I32];
        c[I33] = a[I33]+b[I33];


        return rslt;
    }

    /**
     * Subtracts a matrix from this matrix and returns the results.
     *
     * @param arg the matrix to subtract from this matrix
     * @return a Matrix object
     */
    public Matrix subtract(Matrix arg){
        Matrix rslt = new Matrix();

        float[] a = vals;
        float[] b = arg.vals;
        float[] c = rslt.vals;

        c[I11] = a[I11]-b[I11];
        c[I12] = a[I12]-b[I12];
        c[I13] = a[I13]-b[I13];
        c[I21] = a[I21]-b[I21];
        c[I22] = a[I22]-b[I22];
        c[I23] = a[I23]-b[I23];
        c[I31] = a[I31]-b[I31];
        c[I32] = a[I32]-b[I32];
        c[I33] = a[I33]-b[I33];

        return rslt;
    }

    /**
     * Computes the determinant of the matrix.
     *
     * @return the determinant of the matrix
     */
    public float getDeterminant(){
        // ref http://en.wikipedia.org/wiki/Determinant
        // note that in PDF, I13 and I23 are always 0 and I33 is always 1
        // so this could be simplified/faster
        return    vals[I11] * vals[I22] * vals[I33]
                + vals[I12] * vals[I23] * vals[I31]
                + vals[I13] * vals[I21] * vals[I32]
                - vals[I11] * vals[I23] * vals[I32]
                - vals[I12] * vals[I21] * vals[I33]
                - vals[I13] * vals[I22] * vals[I31];
    }

    /**
     * Checks equality of matrices.
     *
     * @param obj the other Matrix that needs to be compared with this matrix.
     * @return true if both matrices are equal
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix))
            return false;

        return Arrays.equals(vals, ((Matrix) obj).vals);
    }

    /**
     * Generates a hash code for this object.
     *
     * @return	the hash code of this object
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(vals);
    }

    /**
     * Generates a String representation of the matrix.
     *
     * @return	the values, delimited with tabs and newlines.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return  vals[I11] + "\t" + vals[I12] + "\t" + vals[I13] + "\n" +
                vals[I21] + "\t" + vals[I22] + "\t" + vals[I23] + "\n" +
                vals[I31] + "\t" + vals[I32] + "\t" + vals[I33];
    }
}
