/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfType0Order3FunctionTest extends AbstractPdfType0FunctionTest {

    private static final int CUBIC_INTERPOLATION_ORDER = 3;

    public PdfType0Order3FunctionTest() {
        super(CUBIC_INTERPOLATION_ORDER);
    }

    @Test
    public void testPolynomials() {
        testPolynomials(new double[] {0.1, 0.07, 0.03});
    }

    @Test
    public void testPolynomialsWithEncoding() {
        testPolynomialsWithEncoding(new double[] {0.1, 0.07, 0.03});
    }

    @Test
    public void testPolynomialsDim2() {
        testPolynomialsDim2(new double[] {0.007, DELTA});
    }

    @Test
    public void testPolynomialsDim2WithEncoding() {
        testPolynomialsDim2WithEncoding(new double[] {0.007, DELTA});
    }

    @Test
    public void testSinus() {
        testSinus(1.6e-6);
    }

    @Test
    public void testExponent() {
        testExponent(0.03);
    }

    @Test
    public void testLogarithm() {
        testLogarithm(0.035);
    }

    @Test
    public void testGeneralInterpolation() {
        testGeneralInterpolation(0.01);
    }
}
