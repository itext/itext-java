/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.kernel.colors.gradients.StrategyBasedRadialGradientBuilder;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssGradientUtilTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        String gradientValue = null;

        Assertions.assertFalse(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Assertions.assertNull(CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12));
    }

    @Test
    public void webkitExtensionLinearGradientTest() {
        String gradientValue = "-webkit-linear-gradient(red, green, blue)";

        Assertions.assertFalse(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Assertions.assertNull(CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12));
    }

    @Test
    public void linearGradientWithNamesTest() {
        String gradientValue = "  linear-gradient(red, green, blue) \t ";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradientWithHexColorsTest() {
        String gradientValue = "linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradientWithRgbFunctionsTest() {
        String gradientValue = "linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradientWithNamesTest() {
        String gradientValue = "  repeating-linear-gradient(red, green, blue) \t ";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradientWithHexColorsAndUpperCaseTest() {
        String gradientValue = "rePEATing-linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradientWithRgbFunctionsTest() {
        String gradientValue = "repeating-linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void emptyParsedArguments1Test() {
        String gradientValue = "linear-gradient()";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, "linear-gradient()"),
                e.getMessage());
    }

    @Test
    public void emptyParsedArguments2Test() {
        String gradientValue = "linear-gradient( , )";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, "linear-gradient( , )"),
                e.getMessage());
    }

    @Test
    public void emptyCommaSeparatedLinearGradientArgumentsTest() {
        String[] invalidGradientValues = {
                "linear-gradient(, red, blue)",
                "linear-gradient(red,,blue)",
                "linear-gradient(red, blue,)"
        };

        for (String gradientValue : invalidGradientValues) {
            Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                    () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12));
            Assertions.assertEquals(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, gradientValue), e.getMessage());
        }
    }

    @Test
    public void emptyCommaSeparatedRadialGradientArgumentsTest() {
        String[] invalidGradientValues = {
                "radial-gradient(, red, blue)",
                "radial-gradient(red,,blue)",
                "radial-gradient(red, blue,)"
        };

        for (String gradientValue : invalidGradientValues) {
            Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                    () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12));
            Assertions.assertEquals(MessageFormatUtil.format(
                    StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, gradientValue), e.getMessage());
        }
    }

    @Test
    public void invalidFirstArgumentTest() {
        String gradientValue = "linear-gradient(not-angle-or-color, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "not-angle-or-color"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest0() {
        String gradientValue = "linear-gradient(to , orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "to"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest1() {
        String gradientValue = "linear-gradient(to, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "to"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest2() {
        String gradientValue = "linear-gradient(to left left, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to left left"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest3() {
        String gradientValue = "linear-gradient(to bottom top, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to bottom top"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest4() {
        String gradientValue = "linear-gradient(to left right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to left right"),
                e.getMessage());
    }

    @Test
    public void invalidToSideTest5() {
        String gradientValue = "linear-gradient(to top right right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING, "to top right right"),
                e.getMessage());
    }

    @Test
    public void invalidColorWithThreeOffsetsValueTest() {
        String gradientValue = "linear-gradient(red, orange 20pt 30pt 100pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20pt 30pt 100pt"),
                e.getMessage());
    }

    @Test
    public void invalidColorOffsetValueTest() {
        String gradientValue = "linear-gradient(red, orange 20, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20"),
                e.getMessage());
    }

    @Test
    public void invalidMultipleHintsInARowValueTest() {
        String gradientValue = "linear-gradient(red, orange, 20%, 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "30%"),
                e.getMessage());
    }

    @Test
    public void invalidMultipleHintsInARowWithoutCommaValueTest() {
        String gradientValue = "linear-gradient(red, orange, 20% 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "20% 30%"),
                e.getMessage());
    }

    @Test
    public void invalidFirstElementIsAHintValueTest() {
        String gradientValue = "linear-gradient(5%, red, orange, 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "5%"),
                e.getMessage());
    }

    @Test
    public void invalidLastElementIsAHintValueTest() {
        String gradientValue = "linear-gradient(red, orange, 30%, green 200pt, blue 250pt, 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "120%"),
                e.getMessage());
    }

    @Test
    public void linearGradDifferentSidesLeftTest() {
        String gradientValue = "linear-gradient(to left, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentSidesRightTest() {
        String gradientValue = "linear-gradient(to right, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentSidesBottomTest() {
        String gradientValue = "linear-gradient(to bottom, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentSidesTopTest() {
        String gradientValue = "linear-gradient(to top, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToLeftTopTest() {
        String gradientValue = "linear-gradient(to left top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToTopLeftTest() {
        String gradientValue = "linear-gradient(to top left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToLeftBottomTest() {
        String gradientValue = "linear-gradient(to left bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToBottomLeftTest() {
        String gradientValue = "linear-gradient(to bottom left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToRightTopTest() {
        String gradientValue = "linear-gradient(to right top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToTopRightTest() {
        String gradientValue = "linear-gradient(to top right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToRightBottomTest() {
        String gradientValue = "linear-gradient(to right bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentCornersToBottomRightTest() {
        String gradientValue = "linear-gradient(to bottom right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentDegPositiveTest() {
        String gradientValue = "linear-gradient(41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentDegNegativeTest() {
        String gradientValue = "linear-gradient(-41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentDegZeroTest() {
        String gradientValue = "linear-gradient(0deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*0/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentRadPositiveTest() {
        String gradientValue = "linear-gradient(0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentRadNegativeTest() {
        String gradientValue = "linear-gradient(-0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentRadZeroTest() {
        String gradientValue = "linear-gradient(0rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentGradPositiveTest() {
        String gradientValue = "linear-gradient(41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) -Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentGradNegativeTest() {
        String gradientValue = "linear-gradient(-41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDifferentGradZeroTest() {
        String gradientValue = "linear-gradient(0grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*0/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDifferentTurnPositiveTest() {
        String gradientValue = "linear-gradient(0.17turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "0.17turn"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDifferentTurnNegativeTest() {
        String gradientValue = "linear-gradient(-0.17turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "-0.17turn"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDifferentTurnZeroTest() {
        String gradientValue = "linear-gradient(0turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "0turn"),
                e.getMessage());
    }

    @Test
    public void repeatingLinearGradDifferentSidesLeftTest() {
        String gradientValue = "repeating-linear-gradient(to left, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesRightTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesBottomTest() {
        String gradientValue = "repeating-linear-gradient(to bottom, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesTopTest() {
        String gradientValue = "repeating-linear-gradient(to top, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToLeftTopTest() {
        String gradientValue = "repeating-linear-gradient(to left top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToTopLeftTest() {
        String gradientValue = "repeating-linear-gradient(to top left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToLeftBottomTest() {
        String gradientValue = "repeating-linear-gradient(to left bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToBottomLeftTest() {
        String gradientValue = "repeating-linear-gradient(to bottom left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToRightTopTest() {
        String gradientValue = "repeating-linear-gradient(to right top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToTopRightTest() {
        String gradientValue = "repeating-linear-gradient(to top right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToRightBottomTest() {
        String gradientValue = "repeating-linear-gradient(to right bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToBottomRightTest() {
        String gradientValue = "repeating-linear-gradient(to bottom right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegPositiveTest() {
        String gradientValue = "repeating-linear-gradient(41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegZeroTest() {
        String gradientValue = "repeating-linear-gradient(0deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*0/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadPositiveTest() {
        String gradientValue = "repeating-linear-gradient(0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadZeroTest() {
        String gradientValue = "repeating-linear-gradient(0rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradPositiveTest() {
        String gradientValue = "repeating-linear-gradient(41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) -Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradZeroTest() {
        String gradientValue = "repeating-linear-gradient(0grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*0/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatingLinearGradDifferentTurnPositiveTest() {
        String gradientValue = "repeating-linear-gradient(0.17turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "0.17turn"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatingLinearGradDifferentTurnNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-0.17turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "-0.17turn"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3595. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatingLinearGradDifferentTurnZeroTest() {
        String gradientValue = "repeating-linear-gradient(0turn, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "0turn"),
                e.getMessage());
    }

    @Test
    public void linearGradDiffColorNameTest() {
        String gradientValue = "linear-gradient(red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffColorHexTest() {
        String gradientValue = "linear-gradient(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffColorRGBTest() {
        String gradientValue = "linear-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffColorRGBaTest() {
        String gradientValue = "linear-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopTest() {
        String gradientValue = "linear-gradient(to left top, red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopRGBTest() {
        String gradientValue = "linear-gradient(to left top, red, rgb(0, 127, 0), blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopRGBOffsetsHintsTest() {
        String gradientValue = "linear-gradient(to left top, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorNameTest() {
        String gradientValue = "repeating-linear-gradient(red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorHexTest() {
        String gradientValue = "repeating-linear-gradient(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorRGBTest() {
        String gradientValue = "repeating-linear-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorRGBaTest() {
        String gradientValue = "repeating-linear-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopRGBTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red, rgb(0, 127, 0), blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopRGBOffsetsHintsTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteCMTest() {
        String gradientValue = "linear-gradient(to right, orange 3cm, red 3cm, green 9cm, blue 9cm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "linear-gradient(to right, orange 3mm, red 3mm, green 9mm, blue 9mm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "linear-gradient(to right, orange 30Q, red 30Q, green 90Q, blue 90Q)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "linear-gradient(to right, orange 1in, red 1in, green 3in, blue 3in)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 3*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3*72f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "linear-gradient(to right, orange 10pc, red 10pc, green 30pc, blue 30pc)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 30*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "linear-gradient(to right, orange 100pt, red 100pt, green 300pt, blue 300pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "linear-gradient(to right, orange 100px, red 100px, green 300px, blue 300px)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300*0.75f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "linear-gradient(to right, orange 3em, red 3em, green 9em, blue 9em)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "linear-gradient(to right, orange 3rem, red 3rem, green 9rem, blue 9rem)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "linear-gradient(to right, orange 3ex, red 3ex, green 9ex, blue 9ex)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*6f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDiffMetricsFontRelatedChTest() {
        String gradientValue = "linear-gradient(to right, orange 3ch, red 3ch, green 9ch, blue 9ch)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3ch"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDiffMetricsFontRelatedVhTest() {
        String gradientValue = "linear-gradient(to right, orange 3vh, red 3vh, green 9vh, blue 9vh)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vh"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDiffMetricsViewPortVwTest() {
        String gradientValue = "linear-gradient(to right, orange 3vw, red 3vw, green 9vw, blue 9vw)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vw"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDiffMetricsViewPortVminTest() {
        String gradientValue = "linear-gradient(to right, orange 3vmin, red 3vmin, green 9vmin, blue 9vmin)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vmin"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void linearGradDiffMetricsViewPortVmaxTest() {
        String gradientValue = "linear-gradient(to right, orange 3vmax, red 3vmax, green 9vmax, blue 9vmax)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vmax"),
                e.getMessage());
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteCMTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3cm, red 3cm, green 9cm, blue 9cm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3mm, red 3mm, green 9mm, blue 9mm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 30Q, red 30Q, green 90Q, blue 90Q)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 1in, red 1in, green 3in, blue 3in)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 3*72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3*72f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10pc, red 10pc, green 30pc, blue 30pc)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 30*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100pt, red 100pt, green 300pt, blue 300pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100px, red 100px, green 300px, blue 300px)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300*0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300*0.75f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3em, red 3em, green 9em, blue 9em)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3rem, red 3rem, green 9rem, blue 9rem)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3ex, red 3ex, green 9ex, blue 9ex)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*6f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatLinearGradDiffMetricsFontRelatedChTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3ch, red 3ch, green 9ch, blue 9ch)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3ch"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatLinearGradDiffMetricsFontRelatedVhTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3vh, red 3vh, green 9vh, blue 9vh)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vh"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatLinearGradDiffMetricsViewPortVwTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3vw, red 3vw, green 9vw, blue 9vw)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vw"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatLinearGradDiffMetricsViewPortVminTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3vmin, red 3vmin, green 9vmin, blue 9vmin)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vmin"),
                e.getMessage());
    }

    @Test
    // TODO: DEVSIX-3596. Remove Exception expectation after fix and update the logic of the test similar to the already existed tests logic
    public void repeatLinearGradDiffMetricsViewPortVmaxTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3vmax, red 3vmax, green 9vmax, blue 9vmax)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 3vmax"),
                e.getMessage());
    }

    @Test
    public void linearGradDiffOffsetStartEndInsideTest() {
        String gradientValue = "linear-gradient(to right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetStartEndOutTest() {
        String gradientValue = "linear-gradient(to right, orange -100pt, red 150pt, green 200pt, blue 750pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "linear-gradient(to right, orange, red -20%, green 120%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 1.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "linear-gradient(to right, orange, red 300pt, green, blue 80%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "linear-gradient(to right, orange, red 10%, lime, 80%, blue 80.5%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "linear-gradient(to right, orange 10%, red, lime, 40%, blue, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetSmallHintTest() {
        String gradientValue = "linear-gradient(to right, orange, 1%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetNegativeHintTest() {
        String gradientValue = "linear-gradient(to right, orange, -100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffOffsetHintTest() {
        String gradientValue = "linear-gradient(to right, orange, 100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void linearGradDiffMultipleOffsetsTest() {
        String gradientValue = "linear-gradient(to right, orange 10%, blue 60% 70%, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetStartEndInsideTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetStartEndOutTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange -100pt, red 150pt, green 200pt, blue 750pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red -20%, green 120%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 1.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red 300pt, green, blue 80%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red 10%, lime, 80%, blue 80.5%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10%, red, lime, 40%, blue, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetSmallHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, 1%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetNegativeHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, -100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, 100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingLinearGradDiffMultipleOffsetsTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10%, blue 60% 70%, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    private void assertStrategyBasedBuilderEquals(AbstractLinearGradientBuilder gradientBuilder,
            boolean expectedIsCentralRotationStrategy, double expectedRotateVectorAngle,
            GradientStrategy expectedGradientStrategy, GradientSpreadMethod expectedSpreadMethod,
            List<GradientColorStop> expectedColorStops) {
        Assertions.assertTrue(gradientBuilder instanceof StrategyBasedLinearGradientBuilder);

        StrategyBasedLinearGradientBuilder builder = (StrategyBasedLinearGradientBuilder) gradientBuilder;
        Assertions.assertEquals(expectedIsCentralRotationStrategy, builder.isCentralRotationAngleStrategy());
        Assertions.assertEquals(expectedRotateVectorAngle, builder.getRotateVectorAngle(), 1e-10);
        Assertions.assertEquals(expectedGradientStrategy, builder.getGradientStrategy());
        Assertions.assertEquals(expectedSpreadMethod, builder.getSpread());

        List<GradientColorStop> actualStops = builder.getStopColors();
        Assertions.assertEquals(expectedColorStops.size(), actualStops.size());
        for (int i = 0; i < expectedColorStops.size(); ++i) {
            Assertions.assertEquals(expectedColorStops.get(i), actualStops.get(i));
        }
    }

    @Test
    public void isCssRadialGradientValueTest() {
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue(null));
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue(""));
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue("linear-gradient(red, green)"));
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue("-webkit-radial-gradient(red, green)"));
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue("radial-gradient"));
        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue("radial-gradient(red, green"));

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue("radial-gradient(red, green)"));
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue("repeating-radial-gradient(red, green)"));
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue("  radial-gradient(circle, red, blue) \t "));
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue("RADIAL-GRADIENT(red, blue)"));

        Assertions.assertTrue(CssGradientUtil.isCssGradientValue("linear-gradient(red, green)"));
        Assertions.assertTrue(CssGradientUtil.isCssGradientValue("radial-gradient(red, green)"));
        Assertions.assertTrue(CssGradientUtil.isCssGradientValue("repeating-linear-gradient(red, green)"));
        Assertions.assertTrue(CssGradientUtil.isCssGradientValue("repeating-radial-gradient(red, green)"));
        Assertions.assertFalse(CssGradientUtil.isCssGradientValue("url(image.png)"));
    }

    @Test
    public void radialGradientBasicTest() {
        String gradientValue = "radial-gradient(red, green, blue)";
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));

        StrategyBasedRadialGradientBuilder builder = CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(builder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(builder, true, false,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0.5d, true, false, 0.5d, true,
                0d, false, 0d, false,
                GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradientTest() {
        String gradientValue = "repeating-radial-gradient(red, green, blue)";
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));

        StrategyBasedRadialGradientBuilder builder = CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(builder);

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(builder, true, false,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0.5d, true, false, 0.5d, true,
                0d, false, 0d, false,
                GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void radialGradientCircleTest() {
        String gradientValue = "radial-gradient(circle, red, blue)";
        StrategyBasedRadialGradientBuilder builder = CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isFromCenterStrategy());
        Assertions.assertTrue(builder.isCircular());
        Assertions.assertEquals(StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER, builder.getGradientStrategy());
    }

    @Test
    public void radialGradientEllipseTest() {
        String gradientValue = "radial-gradient(ellipse, red, blue)";
        StrategyBasedRadialGradientBuilder builder = CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isFromCenterStrategy());
        Assertions.assertFalse(builder.isCircular());
        Assertions.assertEquals(StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER, builder.getGradientStrategy());
    }

    @Test
    public void radialGradientExtentKeywordsTest() {
        String[] keywords = {"closest-side", "closest-corner", "farthest-side", "farthest-corner"};
        StrategyBasedRadialGradientBuilder.GradientStrategy[] expectedStrategies = {
                StrategyBasedRadialGradientBuilder.GradientStrategy.CLOSEST_SIDE,
                StrategyBasedRadialGradientBuilder.GradientStrategy.CLOSEST_CORNER,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_SIDE,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER
        };

        for (int i = 0; i < keywords.length; i++) {
            StrategyBasedRadialGradientBuilder builder =
                    CssGradientUtil.parseCssRadialGradient("radial-gradient(" + keywords[i] + ", red, blue)", 24, 12);
            Assertions.assertNotNull(builder);
            Assertions.assertTrue(builder.isFromCenterStrategy());
            Assertions.assertFalse(builder.isCircular());
            Assertions.assertEquals(expectedStrategies[i], builder.getGradientStrategy());

            builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(circle " + keywords[i] + ", red, blue)", 24, 12);
            Assertions.assertNotNull(builder);
            Assertions.assertTrue(builder.isFromCenterStrategy());
            Assertions.assertTrue(builder.isCircular());
            Assertions.assertEquals(expectedStrategies[i], builder.getGradientStrategy());

            builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(" + keywords[i] + " circle, red, blue)", 24, 12);
            Assertions.assertNotNull(builder);
            Assertions.assertTrue(builder.isFromCenterStrategy());
            Assertions.assertTrue(builder.isCircular());
            Assertions.assertEquals(expectedStrategies[i], builder.getGradientStrategy());

            builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(ellipse " + keywords[i] + ", red, blue)", 24, 12);
            Assertions.assertNotNull(builder);
            Assertions.assertTrue(builder.isFromCenterStrategy());
            Assertions.assertFalse(builder.isCircular());
            Assertions.assertEquals(expectedStrategies[i], builder.getGradientStrategy());
        }
    }

    @Test
    public void radialGradientExplicitSizeTest() {
        // Single length -> circle (100px = 75pt)
        StrategyBasedRadialGradientBuilder builder =
                CssGradientUtil.parseCssRadialGradient("radial-gradient(100px, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isFromCenterStrategy());
        Assertions.assertEquals(75.0, builder.getXRadius(), 1e-6);
        Assertions.assertFalse(builder.isXRadiusRelative());
        Assertions.assertEquals(75.0, builder.getYRadius(), 1e-6);
        Assertions.assertFalse(builder.isYRadiusRelative());

        // Circle + length
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(circle 50pt, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isFromCenterStrategy());
        Assertions.assertEquals(50.0, builder.getXRadius(), 1e-6);
        Assertions.assertEquals(50.0, builder.getYRadius(), 1e-6);

        // Two lengths (100px 50px = 75pt 37.5pt)
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(100px 50px, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isFromCenterStrategy());
        Assertions.assertEquals(75.0, builder.getXRadius(), 1e-6);
        Assertions.assertFalse(builder.isXRadiusRelative());
        Assertions.assertEquals(37.5, builder.getYRadius(), 1e-6);
        Assertions.assertFalse(builder.isYRadiusRelative());

        // Two percentages
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(50% 60%, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isFromCenterStrategy());
        Assertions.assertEquals(0.5, builder.getXRadius(), 1e-6);
        Assertions.assertTrue(builder.isXRadiusRelative());
        Assertions.assertEquals(0.6, builder.getYRadius(), 1e-6);
        Assertions.assertTrue(builder.isYRadiusRelative());

        // Ellipse + mixed
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(ellipse 50% 100pt, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isFromCenterStrategy());
        Assertions.assertEquals(0.5, builder.getXRadius(), 1e-6);
        Assertions.assertTrue(builder.isXRadiusRelative());
        Assertions.assertEquals(100.0, builder.getYRadius(), 1e-6);
        Assertions.assertFalse(builder.isYRadiusRelative());
    }

    @Test
    public void radialGradientPositionTest() {
        // at center
        StrategyBasedRadialGradientBuilder builder =
                CssGradientUtil.parseCssRadialGradient("radial-gradient(at center, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isLeftToRight());
        Assertions.assertEquals(0.5, builder.getXOffset(), 1e-6);
        Assertions.assertTrue(builder.isXOffsetRelative());
        Assertions.assertFalse(builder.isBottomToTop());
        Assertions.assertEquals(0.5, builder.getYOffset(), 1e-6);
        Assertions.assertTrue(builder.isYOffsetRelative());

        // at top left
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(at top left, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isLeftToRight());
        Assertions.assertEquals(0.0, builder.getXOffset(), 1e-6);
        Assertions.assertFalse(builder.isBottomToTop());
        Assertions.assertEquals(0.0, builder.getYOffset(), 1e-6);

        // at 20% 30%
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(at 20% 30%, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isLeftToRight());
        Assertions.assertEquals(0.2, builder.getXOffset(), 1e-6);
        Assertions.assertTrue(builder.isXOffsetRelative());
        Assertions.assertFalse(builder.isBottomToTop());
        Assertions.assertEquals(0.3, builder.getYOffset(), 1e-6);
        Assertions.assertTrue(builder.isYOffsetRelative());

        // at right 10px bottom 20px (10px = 7.5pt, 20px = 15pt)
        builder = CssGradientUtil.parseCssRadialGradient("radial-gradient(at right 10px bottom 20px, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertFalse(builder.isLeftToRight());
        Assertions.assertEquals(7.5, builder.getXOffset(), 1e-6);
        Assertions.assertFalse(builder.isXOffsetRelative());
        Assertions.assertTrue(builder.isBottomToTop());
        Assertions.assertEquals(15.0, builder.getYOffset(), 1e-6);
        Assertions.assertFalse(builder.isYOffsetRelative());

        // shape + extent + position
        builder = CssGradientUtil.parseCssRadialGradient(
                "radial-gradient(circle closest-side at 20% 30%, red, blue)", 24, 12);
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder.isCircular());
        Assertions.assertEquals(StrategyBasedRadialGradientBuilder.GradientStrategy.CLOSEST_SIDE, builder.getGradientStrategy());
        Assertions.assertTrue(builder.isLeftToRight());
        Assertions.assertEquals(0.2, builder.getXOffset(), 1e-6);
        Assertions.assertFalse(builder.isBottomToTop());
        Assertions.assertEquals(0.3, builder.getYOffset(), 1e-6);
    }

    @Test
    public void radialGradientInvalidArgumentsTest() {
        String[] invalidGradients = {
                "radial-gradient()",
                "radial-gradient( , )",
                "radial-gradient(circle)",
                "radial-gradient(not-shape-or-color, red, blue)",
                "radial-gradient(circle ellipse, red, blue)",
                "radial-gradient(closest-side farthest-corner, red, blue)",
                "radial-gradient(circle 100px 200px, red, blue)",
                "radial-gradient(ellipse 100px, red, blue)",
                "radial-gradient(closest-side 100px, red, blue)",
                "radial-gradient(at, red, blue)",
                "radial-gradient(circle at center at top, red, blue)",
                "radial-gradient(-50px, red, blue)"
        };

        for (String invalidGradient : invalidGradients) {
            Assertions.assertThrows(StyledXMLParserException.class,
                    () -> CssGradientUtil.parseCssRadialGradient(invalidGradient, 24, 12),
                    "Expected exception for: " + invalidGradient);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION)
    })
    public void radialGradientInvalidPositionTest() {
        Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient("radial-gradient(at left right, red, blue)", 24, 12));
    }

    @Test
    public void parseCssGradientTest() {
        Assertions.assertNotNull(CssGradientUtil.parseCssGradient("linear-gradient(red, blue)", 24, 12));
        Assertions.assertNotNull(CssGradientUtil.parseCssGradient("radial-gradient(circle, red, blue)", 24, 12));
        Assertions.assertNull(CssGradientUtil.parseCssGradient("none", 24, 12));
        Assertions.assertNull(CssGradientUtil.parseCssGradient("url(image.png)", 24, 12));
    }

    @Test
    public void nullRadialGradientValueTest() {
        String gradientValue = null;

        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Assertions.assertNull(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12));
    }

    @Test
    public void webkitExtensionRadialGradientTest() {
        String gradientValue = "-webkit-radial-gradient(red, green, blue)";

        Assertions.assertFalse(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Assertions.assertNull(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12));
    }

    @Test
    public void radialGradientWithNamesTest() {
        String gradientValue = "  radial-gradient(red, green, blue) \t ";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradientWithHexColorsTest() {
        String gradientValue = "radial-grADIENt(#ff0000, #008000, #0000ff)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradientWithRgbFunctionsTest() {
        String gradientValue = "radial-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradientWithNamesTest() {
        String gradientValue = "  repeating-radial-gradient(red, green, blue) \t ";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradientWithHexColorsAndUpperCaseTest() {
        String gradientValue = "rePEATing-radIAL-grADIENt(#ff0000, #008000, #0000ff)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradientWithRgbFunctionsTest() {
        String gradientValue = "repeating-radial-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void emptyRadialGradientArguments1Test() {
        String gradientValue = "radial-gradient()";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, "radial-gradient()"),
                e.getMessage());
    }

    @Test
    public void emptyRadialGradientArguments2Test() {
        String gradientValue = "radial-gradient( , )";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, "radial-gradient( , )"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientFirstArgumentTest() {
        String gradientValue = "radial-gradient(not-shape-or-color, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST, "not-shape-or-color"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientColorWithThreeOffsetsValueTest() {
        String gradientValue = "radial-gradient(red, orange 20pt 30pt 100pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20pt 30pt 100pt"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientColorOffsetValueTest() {
        String gradientValue = "radial-gradient(red, orange 20, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "orange 20"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientMultipleHintsInARowValueTest() {
        String gradientValue = "radial-gradient(red, orange, 20%, 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "30%"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientMultipleHintsInARowWithoutCommaValueTest() {
        String gradientValue = "radial-gradient(red, orange, 20% 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "20% 30%"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientFirstElementIsAHintValueTest() {
        String gradientValue = "radial-gradient(circle, 5%, red, orange, 30%, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "5%"),
                e.getMessage());
    }

    @Test
    public void invalidRadialGradientLastElementIsAHintValueTest() {
        String gradientValue = "radial-gradient(circle, red, orange, 30%, green 200pt, blue 250pt, 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, "120%"),
                e.getMessage());
    }

    @Test
    public void radialGradDiffColorNameTest() {
        String gradientValue = "radial-gradient(red, green, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffColorHexTest() {
        String gradientValue = "radial-gradient(#ff0000, #008000, #0000ff)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffColorRGBTest() {
        String gradientValue = "radial-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffColorRGBaTest() {
        String gradientValue = "radial-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradComplexArgsTopLeftTest() {
        String gradientValue = "radial-gradient(at top left, red, green, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradComplexArgsTopLeftRGBTest() {
        String gradientValue = "radial-gradient(at top left, red, rgb(0, 127, 0), blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradComplexArgsTopLeftRGBOffsetsHintsTest() {
        String gradientValue = "radial-gradient(at top left, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffColorNameTest() {
        String gradientValue = "repeating-radial-gradient(red, green, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffColorHexTest() {
        String gradientValue = "repeating-radial-gradient(#ff0000, #008000, #0000ff)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffColorRGBTest() {
        String gradientValue = "repeating-radial-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffColorRGBaTest() {
        String gradientValue = "repeating-radial-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradComplexArgsTopLeftTest() {
        String gradientValue = "repeating-radial-gradient(at top left, red, green, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradComplexArgsTopLeftRGBTest() {
        String gradientValue = "repeating-radial-gradient(at top left, red, rgb(0, 127, 0), blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}, 0f, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradComplexArgsTopLeftRGBOffsetsHintsTest() {
        String gradientValue = "repeating-radial-gradient(at top left, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 127f / 255f, 0f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedRadialBuilderEquals(CssGradientUtil.parseCssRadialGradient(gradientValue, 24, 12),
                true, false, StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0d, true, false, 0d, true,
                0d, false, 0d, false, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsoluteCMTest() {
        String gradientValue = "radial-gradient(orange 3cm, red 3cm, green 9cm, blue 9cm)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "radial-gradient(orange 3mm, red 3mm, green 9mm, blue 9mm)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "radial-gradient(orange 30Q, red 30Q, green 90Q, blue 90Q)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((30f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((90f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "radial-gradient(orange 1in, red 1in, green 3in, blue 3in)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 1 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 3 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3 * 72f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "radial-gradient(orange 10pc, red 10pc, green 30pc, blue 30pc)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 10 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 30 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "radial-gradient(orange 100pt, red 100pt, green 300pt, blue 300pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "radial-gradient(orange 100px, red 100px, green 300px, blue 300px)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 300 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300 * 0.75f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "radial-gradient(orange 3em, red 3em, green 9em, blue 9em)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "radial-gradient(orange 3rem, red 3rem, green 9rem, blue 9rem)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "radial-gradient(orange 3ex, red 3ex, green 9ex, blue 9ex)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 6f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMetricsFontRelatedChTest() {
        assertInvalidRadialGradientMetric("radial-gradient(orange 3ch, red 3ch, green 9ch, blue 9ch)", 24, 12,
                "orange 3ch");
    }

    @Test
    public void radialGradDiffMetricsFontRelatedVhTest() {
        assertInvalidRadialGradientMetric("radial-gradient(orange 3vh, red 3vh, green 9vh, blue 9vh)", 12, 12,
                "orange 3vh");
    }

    @Test
    public void radialGradDiffMetricsViewPortVwTest() {
        assertInvalidRadialGradientMetric("radial-gradient(orange 3vw, red 3vw, green 9vw, blue 9vw)", 12, 12,
                "orange 3vw");
    }

    @Test
    public void radialGradDiffMetricsViewPortVminTest() {
        assertInvalidRadialGradientMetric("radial-gradient(orange 3vmin, red 3vmin, green 9vmin, blue 9vmin)", 12, 12,
                "orange 3vmin");
    }

    @Test
    public void radialGradDiffMetricsViewPortVmaxTest() {
        assertInvalidRadialGradientMetric("radial-gradient(orange 3vmax, red 3vmax, green 9vmax, blue 9vmax)", 12, 12,
                "orange 3vmax");
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsoluteCMTest() {
        String gradientValue = "repeating-radial-gradient(orange 3cm, red 3cm, green 9cm, blue 9cm)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "repeating-radial-gradient(orange 3mm, red 3mm, green 9mm, blue 9mm)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "repeating-radial-gradient(orange 30Q, red 30Q, green 90Q, blue 90Q)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, (float) ((30f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, (float) ((90f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f / 2.54) * 72 / 40), OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "repeating-radial-gradient(orange 1in, red 1in, green 3in, blue 3in)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 1 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 3 * 72f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3 * 72f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "repeating-radial-gradient(orange 10pc, red 10pc, green 30pc, blue 30pc)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 10 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 30 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "repeating-radial-gradient(orange 100pt, red 100pt, green 300pt, blue 300pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "repeating-radial-gradient(orange 100px, red 100px, green 300px, blue 300px)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 300 * 0.75f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300 * 0.75f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "repeating-radial-gradient(orange 3em, red 3em, green 9em, blue 9em)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "repeating-radial-gradient(orange 3rem, red 3rem, green 9rem, blue 9rem)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 12f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 12f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 24, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "repeating-radial-gradient(orange 3ex, red 3ex, green 9ex, blue 9ex)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 3 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 9 * 6f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9 * 6f, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatRadialGradDiffMetricsFontRelatedChTest() {
        assertInvalidRadialGradientMetric("repeating-radial-gradient(orange 3ch, red 3ch, green 9ch, blue 9ch)", 12, 12,
                "orange 3ch");
    }

    @Test
    public void repeatRadialGradDiffMetricsFontRelatedVhTest() {
        assertInvalidRadialGradientMetric("repeating-radial-gradient(orange 3vh, red 3vh, green 9vh, blue 9vh)", 12, 12,
                "orange 3vh");
    }

    @Test
    public void repeatRadialGradDiffMetricsViewPortVwTest() {
        assertInvalidRadialGradientMetric("repeating-radial-gradient(orange 3vw, red 3vw, green 9vw, blue 9vw)", 12, 12,
                "orange 3vw");
    }

    @Test
    public void repeatRadialGradDiffMetricsViewPortVminTest() {
        assertInvalidRadialGradientMetric("repeating-radial-gradient(orange 3vmin, red 3vmin, green 9vmin, blue 9vmin)", 12, 12,
                "orange 3vmin");
    }

    @Test
    public void repeatRadialGradDiffMetricsViewPortVmaxTest() {
        assertInvalidRadialGradientMetric("repeating-radial-gradient(orange 3vmax, red 3vmax, green 9vmax, blue 9vmax)", 12, 12,
                "orange 3vmax");
    }

    @Test
    public void radialGradDiffOffsetStartEndInsideTest() {
        String gradientValue = "radial-gradient(orange 100pt, red 150pt, green 200pt, blue 250pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetStartEndOutTest() {
        String gradientValue = "radial-gradient(orange -100pt, red 150pt, green 200pt, blue 750pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, -100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "radial-gradient(orange, red -20%, green 120%, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 1.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "radial-gradient(orange, red 300pt, green, blue 80%, black)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "radial-gradient(orange, red 10%, lime, 80%, blue 80.5%, black)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "radial-gradient(orange 10%, red, lime, 40%, blue, black 90%)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetSmallHintTest() {
        String gradientValue = "radial-gradient(orange, 1%, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetNegativeHintTest() {
        String gradientValue = "radial-gradient(orange, -100pt, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffOffsetHintTest() {
        String gradientValue = "radial-gradient(orange, 100pt, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void radialGradDiffMultipleOffsetsTest() {
        String gradientValue = "radial-gradient(orange 10%, blue 60% 70%, black 90%)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.PAD, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetStartEndInsideTest() {
        String gradientValue = "repeating-radial-gradient(orange 100pt, red 150pt, green 200pt, blue 250pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetStartEndOutTest() {
        String gradientValue = "repeating-radial-gradient(orange -100pt, red 150pt, green 200pt, blue 750pt)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, -100, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 200, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "repeating-radial-gradient(orange, red -20%, green 120%, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 1.2f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "repeating-radial-gradient(orange, red 300pt, green, blue 80%, black)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 128f / 255f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "repeating-radial-gradient(orange, red 10%, lime, 80%, blue 80.5%, black)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "repeating-radial-gradient(orange 10%, red, lime, 40%, blue, black 90%)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetSmallHintTest() {
        String gradientValue = "repeating-radial-gradient(orange, 1%, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetNegativeHintTest() {
        String gradientValue = "repeating-radial-gradient(orange, -100pt, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffOffsetHintTest() {
        String gradientValue = "repeating-radial-gradient(orange, 100pt, blue)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    @Test
    public void repeatingRadialGradDiffMultipleOffsetsTest() {
        String gradientValue = "repeating-radial-gradient(orange 10%, blue 60% 70%, black 90%)";
        List<GradientColorStop> expectedColorStops = new ArrayList<>();
        expectedColorStops.add(new GradientColorStop(new float[]{1f, 165f / 255f, 0f}, 0.1f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        expectedColorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));
        assertDefaultRadialGradient(gradientValue, 12, 12, GradientSpreadMethod.REPEAT, expectedColorStops);
    }

    private void assertInvalidRadialGradientMetric(String gradientValue, float emValue, float remValue,
            String expectedInvalidColorStop) {
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssGradientUtil.parseCssRadialGradient(gradientValue, emValue, remValue));
        Assertions.assertEquals(MessageFormatUtil.format(
                        StyledXMLParserException.INVALID_GRADIENT_COLOR_STOP_VALUE, expectedInvalidColorStop),
                e.getMessage());
    }

    private void assertDefaultRadialGradient(String gradientValue, float emValue, float remValue,
            GradientSpreadMethod expectedSpreadMethod, List<GradientColorStop> expectedColorStops) {
        Assertions.assertTrue(CssGradientUtil.isCssRadialGradientValue(gradientValue));
        StrategyBasedRadialGradientBuilder builder = CssGradientUtil.parseCssRadialGradient(
                gradientValue, emValue, remValue);
        assertStrategyBasedRadialBuilderEquals(builder, true, false,
                StrategyBasedRadialGradientBuilder.GradientStrategy.FARTHEST_CORNER,
                true, 0.5d, true, false, 0.5d, true,
                0d, false, 0d, false, expectedSpreadMethod, expectedColorStops);
    }

    private void assertStrategyBasedRadialBuilderEquals(StrategyBasedRadialGradientBuilder builder,
            boolean expectedIsFromCenterStrategy, boolean expectedIsCircular,
            StrategyBasedRadialGradientBuilder.GradientStrategy expectedGradientStrategy,
            boolean expectedLeftToRight, double expectedXOffset, boolean expectedXOffsetRelative,
            boolean expectedBottomToTop, double expectedYOffset, boolean expectedYOffsetRelative,
            double expectedXRadius, boolean expectedXRadiusRelative,
            double expectedYRadius, boolean expectedYRadiusRelative,
            GradientSpreadMethod expectedSpreadMethod,
            List<GradientColorStop> expectedColorStops) {
        Assertions.assertNotNull(builder);
        Assertions.assertEquals(expectedIsFromCenterStrategy, builder.isFromCenterStrategy());
        if (expectedIsFromCenterStrategy) {
            Assertions.assertEquals(expectedIsCircular, builder.isCircular());
            Assertions.assertEquals(expectedGradientStrategy, builder.getGradientStrategy());
        } else {
            Assertions.assertEquals(expectedXRadius, builder.getXRadius(), 1e-6);
            Assertions.assertEquals(expectedXRadiusRelative, builder.isXRadiusRelative());
            Assertions.assertEquals(expectedYRadius, builder.getYRadius(), 1e-6);
            Assertions.assertEquals(expectedYRadiusRelative, builder.isYRadiusRelative());
        }
        Assertions.assertEquals(expectedLeftToRight, builder.isLeftToRight());
        Assertions.assertEquals(expectedXOffset, builder.getXOffset(), 1e-6);
        Assertions.assertEquals(expectedXOffsetRelative, builder.isXOffsetRelative());
        Assertions.assertEquals(expectedBottomToTop, builder.isBottomToTop());
        Assertions.assertEquals(expectedYOffset, builder.getYOffset(), 1e-6);
        Assertions.assertEquals(expectedYOffsetRelative, builder.isYOffsetRelative());
        Assertions.assertEquals(expectedSpreadMethod, builder.getSpread());

        List<GradientColorStop> actualStops = builder.getStopColors();
        Assertions.assertEquals(expectedColorStops.size(), actualStops.size());
        for (int i = 0; i < expectedColorStops.size(); ++i) {
            Assertions.assertEquals(expectedColorStops.get(i), actualStops.get(i));
        }
    }
}
