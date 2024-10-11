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
package com.itextpdf.kernel.pdf.function;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfType0Order1FunctionTest extends AbstractPdfType0FunctionTest {

    private static final int LINEAR_INTERPOLATION_ORDER = 1;

    public PdfType0Order1FunctionTest() {
        super(LINEAR_INTERPOLATION_ORDER);
    }

    @Test
    public void testLinerFunctionsWithEncoding() {
        testLinearFunctionsWithEncoding();
    }

    @Test
    public void testPolynomials() {
        testPolynomials(new double[] {0.2, 0.14, 0.06});
    }

    @Test
    public void testPolynomialsWithEncoding() {
        testPolynomialsWithEncoding(new double[] {0.2, 0.14, 0.06});
    }

    @Test
    public void testPolynomialsDim2() {
        testPolynomialsDim2(new double[] {0.01 + DELTA, DELTA});
    }

    @Test
    public void testPolynomialsDim2WithEncoding() {
        testPolynomialsDim2WithEncoding(new double[] {0.01 + DELTA, DELTA});
    }

    @Test
    public void testSinus() {
        testSinus(3.2e-3);
    }

    @Test
    public void testExponent() {
        testExponent(0.05);
    }

    @Test
    public void testLogarithm() {
        testLogarithm(0.06);
    }

    @Test
    public void testGeneralInterpolation() {
        testGeneralInterpolation(0.015);
    }
}
