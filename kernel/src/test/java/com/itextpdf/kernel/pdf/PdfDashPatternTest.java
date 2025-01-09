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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfDashPatternTest extends ExtendedITextTest {

    @Test
    public void constructorNoParamTest() {
        PdfDashPattern dashPattern = new PdfDashPattern();
        Assertions.assertEquals(-1, dashPattern.getDash(), 0.0001);
        Assertions.assertEquals(-1, dashPattern.getGap(), 0.0001);
        Assertions.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorOneParamTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10);
        Assertions.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assertions.assertEquals(-1, dashPattern.getGap(), 0.0001);
        Assertions.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorTwoParamsTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10, 20);
        Assertions.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assertions.assertEquals(20, dashPattern.getGap(), 0.0001);
        Assertions.assertEquals(-1, dashPattern.getPhase(), 0.0001);
    }

    @Test
    public void constructorThreeParamsTest() {
        PdfDashPattern dashPattern = new PdfDashPattern(10, 20, 30);
        Assertions.assertEquals(10, dashPattern.getDash(), 0.0001);
        Assertions.assertEquals(20, dashPattern.getGap(), 0.0001);
        Assertions.assertEquals(30, dashPattern.getPhase(), 0.0001);
    }
}
