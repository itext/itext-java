/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.barcodes.qrcode;

/**
 * <p>Represents a polynomial whose coefficients are elements of GF(256).
 * Instances of this class are immutable.</p>
 *
 * <p>Much credit is due to William Rucklidge since portions of this code are an indirect
 * port of his C++ Reed-Solomon implementation.</p>
 *
 * @author Sean Owen
 */
final class GF256Poly {

    private final GF256 field;
    private final int[] coefficients;

    /**
     * @param field the {@link GF256} instance representing the field to use
     * to perform computations
     * @param coefficients coefficients as ints representing elements of GF(256), arranged
     * from most significant (highest-power term) coefficient to least significant
     * @throws IllegalArgumentException if argument is null or empty,
     * or if leading coefficient is 0 and this is not a
     * constant polynomial (that is, it is not the monomial "0")
     */
    GF256Poly(GF256 field, int[] coefficients) {
        if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException();
        }
        this.field = field;
        int coefficientsLength = coefficients.length;
        if (coefficientsLength > 1 && coefficients[0] == 0) {
            // Leading term must be non-zero for anything except the constant polynomial "0"
            int firstNonZero = 1;
            while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
                firstNonZero++;
            }
            if (firstNonZero == coefficientsLength) {
                this.coefficients = field.getZero().coefficients;
            } else {
                this.coefficients = new int[coefficientsLength - firstNonZero];
                System.arraycopy(coefficients,
                        firstNonZero,
                        this.coefficients,
                        0,
                        this.coefficients.length);
            }
        } else {
            this.coefficients = coefficients;
        }
    }

    int[] getCoefficients() {
        return coefficients;
    }

    /**
     * @return degree of this polynomial
     */
    int getDegree() {
        return coefficients.length - 1;
    }

    /**
     * @return true iff this polynomial is the monomial "0"
     */
    boolean isZero() {
        return coefficients[0] == 0;
    }

    /**
     * @return coefficient of x^degree term in this polynomial
     */
    int getCoefficient(int degree) {
        return coefficients[coefficients.length - 1 - degree];
    }

    /**
     * @return evaluation of this polynomial at a given point
     */
    int evaluateAt(int a) {
        if (a == 0) {
            // Just return the x^0 coefficient
            return getCoefficient(0);
        }
        int size = coefficients.length;
        if (a == 1) {
            // Just the sum of the coefficients
            int result = 0;
            for (int i = 0; i < size; i++) {
                result = GF256.addOrSubtract(result, coefficients[i]);
            }
            return result;
        }
        int result = coefficients[0];
        for (int i = 1; i < size; i++) {
            result = GF256.addOrSubtract(field.multiply(a, result), coefficients[i]);
        }
        return result;
    }

    /**
     * GF addition or subtraction (they are identical for a GF(2^n)
     * @param other the other GF-poly
     * @return new GF256Poly obtained by summing this GF and other
     */

    GF256Poly addOrSubtract(GF256Poly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GF256Polys do not have same GF256 field");
        }
        if (isZero()) {
            return other;
        }
        if (other.isZero()) {
            return this;
        }

        int[] smallerCoefficients = this.coefficients;
        int[] largerCoefficients = other.coefficients;
        if (smallerCoefficients.length > largerCoefficients.length) {
            int[] temp = smallerCoefficients;
            smallerCoefficients = largerCoefficients;
            largerCoefficients = temp;
        }
        int[] sumDiff = new int[largerCoefficients.length];
        int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
        // Copy high-order terms only found in higher-degree polynomial's coefficients
        System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);

        for (int i = lengthDiff; i < largerCoefficients.length; i++) {
            sumDiff[i] = GF256.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
        }

        return new GF256Poly(field, sumDiff);
    }

    /**
     * GF multiplication
     * @param other the other GF-poly
     * @return new GF-poly obtained by multiplying this  with other
     */
    GF256Poly multiply(GF256Poly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GF256Polys do not have same GF256 field");
        }
        if (isZero() || other.isZero()) {
            return field.getZero();
        }
        int[] aCoefficients = this.coefficients;
        int aLength = aCoefficients.length;
        int[] bCoefficients = other.coefficients;
        int bLength = bCoefficients.length;
        int[] product = new int[aLength + bLength - 1];
        for (int i = 0; i < aLength; i++) {
            int aCoeff = aCoefficients[i];
            for (int j = 0; j < bLength; j++) {
                product[i + j] = GF256.addOrSubtract(product[i + j],
                        field.multiply(aCoeff, bCoefficients[j]));
            }
        }
        return new GF256Poly(field, product);
    }

    /**
     * GF scalar multiplication
     * @param scalar scalar
     * @return new GF-poly obtained by multiplying every element of this with the scalar.
     */
    GF256Poly multiply(int scalar) {
        if (scalar == 0) {
            return field.getZero();
        }
        if (scalar == 1) {
            return this;
        }
        int size = coefficients.length;
        int[] product = new int[size];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], scalar);
        }
        return new GF256Poly(field, product);
    }

    GF256Poly multiplyByMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return field.getZero();
        }
        int size = coefficients.length;
        int[] product = new int[size + degree];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], coefficient);
        }
        return new GF256Poly(field, product);
    }

    GF256Poly[] divide(GF256Poly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GF256Polys do not have same GF256 field");
        }
        if (other.isZero()) {
            throw new IllegalArgumentException("Divide by 0");
        }

        GF256Poly quotient = field.getZero();
        GF256Poly remainder = this;

        int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
        int inverseDenominatorLeadingTerm = field.inverse(denominatorLeadingTerm);

        while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
            int degreeDifference = remainder.getDegree() - other.getDegree();
            int scale = field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            GF256Poly term = other.multiplyByMonomial(degreeDifference, scale);
            GF256Poly iterationQuotient = field.buildMonomial(degreeDifference, scale);
            quotient = quotient.addOrSubtract(iterationQuotient);
            remainder = remainder.addOrSubtract(term);
        }

        return new GF256Poly[] { quotient, remainder };
    }

    /**
     * @return String representation of the Galois Field polynomial.
     */
    public String toString() {
        StringBuffer result = new StringBuffer(8 * getDegree());
        for (int degree = getDegree(); degree >= 0; degree--) {
            int coefficient = getCoefficient(degree);
            if (coefficient != 0) {
                if (coefficient < 0) {
                    result.append(" - ");
                    coefficient = -coefficient;
                } else {
                    if (result.length() > 0) {
                        result.append(" + ");
                    }
                }
                if (degree == 0 || coefficient != 1) {
                    int alphaPower = field.log(coefficient);
                    if (alphaPower == 0) {
                        result.append('1');
                    } else if (alphaPower == 1) {
                        result.append('a');
                    } else {
                        result.append("a^");
                        result.append(alphaPower);
                    }
                }
                if (degree != 0) {
                    if (degree == 1) {
                        result.append('x');
                    } else {
                        result.append("x^");
                        result.append(degree);
                    }
                }
            }
        }
        return result.toString();
    }

}
