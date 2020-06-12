/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CssGradientUtilTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    // TODO: DEVSIX-4105
    //  1. add tests from background-image-angles-linear-gradient.html
    //  2. add tests from background-image-metrics-linear-gradient.html
    //  3. add tests from background-image-offsets-linear-gradient.html
    //  4. for points 1-3 the same tests for repeating gradient

    @Test
    public void nullValueTest() {
        String gradientValue = null;

        Assert.assertFalse(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Assert.assertNull(CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12));
    }

    @Test
    public void webkitExtensionLinearGradientTest() {
        String gradientValue = "-webkit-linear-gradient(red, green, blue)";

        Assert.assertFalse(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Assert.assertNull(CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12));
    }

    @Test
    public void linearGradientWithNamesTest() {
        String gradientValue = "  linear-gradient(red, green, blue) \t ";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradientWithHexColorsTest() {
        String gradientValue = "linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradientWithRgbFunctionsTest() {
        String gradientValue = "linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithNamesTest() {
        String gradientValue = "  repeating-linear-gradient(red, green, blue) \t ";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithHexColorsTest() {
        String gradientValue = "repeating-linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithRgbFunctionsTest() {
        String gradientValue = "repeating-linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assert.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void invalidFirstArgumentTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "not-angle-or-color"));

        String gradientValue = "linear-gradient(not-angle-or-color, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest0() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "to"));

        String gradientValue = "linear-gradient(to , orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest1() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "to"));

        String gradientValue = "linear-gradient(to, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest2() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to left left"));

        String gradientValue = "linear-gradient(to left left, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest3() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to bottom top"));

        String gradientValue = "linear-gradient(to bottom top, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest4() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to left right"));

        String gradientValue = "linear-gradient(to left right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidToSideTest5() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to top right right"));

        String gradientValue = "linear-gradient(to top right right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidColorWithThreeOffsetsValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20pt 30pt 100pt"));

        String gradientValue = "linear-gradient(red, orange 20pt 30pt 100pt, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidColorOffsetValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20"));

        String gradientValue = "linear-gradient(red, orange 20, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidMultipleHintsInARowValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "30%"));

        String gradientValue = "linear-gradient(red, orange, 20%, 30%, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidMultipleHintsInARowWithoutCommaValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "20% 30%"));

        String gradientValue = "linear-gradient(red, orange, 20% 30%, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidFirstElementIsAHintValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "5%"));

        String gradientValue = "linear-gradient(5%, red, orange, 30%, green 200pt, blue 250pt)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    @Test
    public void invalidLastElementIsAHintValueTest() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "120%"));

        String gradientValue = "linear-gradient(red, orange, 30%, green 200pt, blue 250pt, 120%)";

        Assert.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
    }

    private void assertStrategyBasedBuilderEquals(AbstractLinearGradientBuilder gradientBuilder,
            boolean isCentralRotationStrategy, double rotateVectorAngle,
            GradientStrategy gradientStrategy, GradientSpreadMethod spreadMethod,
            List<GradientColorStop> stops) {
        Assert.assertTrue(gradientBuilder instanceof StrategyBasedLinearGradientBuilder);

        StrategyBasedLinearGradientBuilder builder = (StrategyBasedLinearGradientBuilder) gradientBuilder;
        Assert.assertEquals(isCentralRotationStrategy, builder.isCentralRotationAngleStrategy());
        Assert.assertEquals(rotateVectorAngle, builder.getRotateVectorAngle(), 1e-10);
        Assert.assertEquals(gradientStrategy, builder.getGradientStrategy());
        Assert.assertEquals(spreadMethod, builder.getSpreadMethod());

        List<GradientColorStop> actualStops = builder.getColorStops();
        Assert.assertEquals(stops.size(), actualStops.size());
        for (int i = 0; i < stops.size(); ++i) {
            Assert.assertEquals(stops.get(i), actualStops.get(i));
        }
    }
}
