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
package com.itextpdf.svg.css;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.css.SvgStrokeParameterConverter.PdfLineDashParameters;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SvgStrokeParameterConverterUnitTest extends ExtendedITextTest {

    @Test
    public void testStrokeDashArrayPercents() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("10pt,3%", null,
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{10, 30}, 0), result);
    }

    @Test
    public void testStrokeDashArrayOddNumberOfValues() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt", null,
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{5, 5}, 0), result);
    }

    @Test
    public void testEmptyStrokeDashArray() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("", null,
                12f, createTextSvgContext());
        Assertions.assertNull(result);
    }

    @Test
    public void testStrokeDashOffsetPercents() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "10%",
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 100), result);
    }

    @Test
    public void testEmptyStrokeDashOffset() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "",
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 0), result);
    }

    @Test
    public void testStrokeDashOffset() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "10",
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 7.5f), result);
    }

    @Test
    public void testStrokeEm() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("1em,2em", "0.5em",
                8f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{8, 16}, 4), result);
    }

    @Test
    public void testStrokeRem() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("1rem,2rem", "0.5rem",
                12f, createTextSvgContext());
        Assertions.assertEquals(new PdfLineDashParameters(new float[]{12, 24}, 6), result);
    }

    private SvgDrawContext createTextSvgContext() {
        SvgDrawContext context = new SvgDrawContext(null, null);
        context.addViewPort(new Rectangle(1000, 1000));
        return context;
    }
}
