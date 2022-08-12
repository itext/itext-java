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
package com.itextpdf.svg.css;

import com.itextpdf.svg.css.SvgStrokeParameterConverter.PdfLineDashParameters;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgStrokeParameterConverterUnitTest extends ExtendedITextTest {

    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    SvgLogMessageConstant.PERCENTAGE_VALUES_IN_STROKE_DASHARRAY_AND_STROKE_DASHOFFSET_ARE_NOT_SUPPORTED)})
    @Test
    public void testStrokeDashArrayPercentsAreNotSupported() {
        Assert.assertNull(SvgStrokeParameterConverter.convertStrokeDashParameters("5,3%", null));
    }

    @Test
    public void testStrokeDashArrayOddNumberOfValues() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt", null);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getDashPhase(), 0);
        Assert.assertArrayEquals(new float[] {5, 5}, result.getDashArray(), 1e-5f);
    }

    @Test
    public void testEmptyStrokeDashArray() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("", null);
        Assert.assertNull(result);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    SvgLogMessageConstant.PERCENTAGE_VALUES_IN_STROKE_DASHARRAY_AND_STROKE_DASHOFFSET_ARE_NOT_SUPPORTED)})
    @Test
    public void testStrokeDashOffsetPercentsAreNotSupported() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "10%");
        Assert.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 0), result);
    }

    @Test
    public void testEmptyStrokeDashOffset() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "");
        Assert.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 0), result);
    }

    @Test
    public void testStrokeDashOffset() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashParameters("5pt,3pt", "10");
        Assert.assertEquals(new PdfLineDashParameters(new float[]{5, 3}, 7.5f), result);
    }
}
