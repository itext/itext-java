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
package com.itextpdf.barcodes.qrcode;

/**
 * This class contains utility methods for performing mathematical operations over
 * the Galois Field GF(256). Operations use a given primitive polynomial in calculations.
 * <p>
 * Throughout this package, elements of GF(256) are represented as an <code>int</code>
 * for convenience and speed (but at the cost of memory).
 * Only the bottom 8 bits are really used.
 */
final class GF256 {

    // x^8 + x^4 + x^3 + x^2 + 1
    public static final GF256 QR_CODE_FIELD = new GF256(0x011D);

    // x^8 + x^5 + x^3 + x^2 + 1
    public static final GF256 DATA_MATRIX_FIELD = new GF256(0x012D);

    private final int[] expTable;
    private final int[] logTable;
    private final GF256Poly zero;
    private final GF256Poly one;

    /**
     * Create a representation of GF(256) using the given primitive polynomial.
     *
     * @param primitive irreducible polynomial whose coefficients are represented by
     *  the bits of an int, where the least-significant bit represents the constant
     *  coefficient
     */
    private GF256(int primitive) {
        expTable = new int[256];
        logTable = new int[256];
        int x = 1;
        for (int i = 0; i < 256; i++) {
            expTable[i] = x;

            // x = x * 2; we're assuming the generator alpha is 2
            x <<= 1;
            if (x >= 0x100) {
                x ^= primitive;
            }
        }
        for (int i = 0; i < 255; i++) {
            logTable[expTable[i]] = i;
        }

        // logTable[0] == 0 but this should never be used
        zero = new GF256Poly(this, new int[]{0});
        one = new GF256Poly(this, new int[]{1});
    }

    GF256Poly getZero() {
        return zero;
    }

    GF256Poly getOne() {
        return one;
    }

    /**
     * @return the monomial representing coefficient * x^degree
     */
    GF256Poly buildMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return zero;
        }
        int[] coefficients = new int[degree + 1];
        coefficients[0] = coefficient;
        return new GF256Poly(this, coefficients);
    }

    /**
     * Implements both addition and subtraction -- they are the same in GF(256).
     *
     * @return sum/difference of a and b
     */
    static int addOrSubtract(int a, int b) {
        return a ^ b;
    }

    /**
     * @return 2 to the power of a in GF(256)
     */
    int exp(int a) {
        return expTable[a];
    }

    /**
     * @return base 2 log of a in GF(256)
     */
    int log(int a) {
        if (a == 0) {
            throw new IllegalArgumentException();
        }
        return logTable[a];
    }

    /**
     * @return multiplicative inverse of a
     */
    int inverse(int a) {
        if (a == 0) {
            throw new ArithmeticException();
        }
        return expTable[255 - logTable[a]];
    }

    /**
     * @param a
     * @param b
     * @return product of a and b in GF(256)
     */
    int multiply(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        if (a == 1) {
            return b;
        }
        if (b == 1) {
            return a;
        }
        return expTable[(logTable[a] + logTable[b]) % 255];
    }

}
