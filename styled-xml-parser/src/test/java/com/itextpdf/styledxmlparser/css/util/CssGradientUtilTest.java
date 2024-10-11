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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.test.ExtendedITextTest;

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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradientWithHexColorsTest() {
        String gradientValue = "linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradientWithRgbFunctionsTest() {
        String gradientValue = "linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithNamesTest() {
        String gradientValue = "  repeating-linear-gradient(red, green, blue) \t ";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithHexColorsAndUpperCaseTest() {
        String gradientValue = "rePEATing-linear-grADIENt(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradientWithRgbFunctionsTest() {
        String gradientValue = "repeating-linear-gradient(  rgb(255, 0, 0), rgb(0, 127, 0), rgb(0, 0,   255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d, GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentSidesRightTest() {
        String gradientValue = "linear-gradient(to right, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentSidesBottomTest() {
        String gradientValue = "linear-gradient(to bottom, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentSidesTopTest() {
        String gradientValue = "linear-gradient(to top, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToLeftTopTest() {
        String gradientValue = "linear-gradient(to left top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToTopLeftTest() {
        String gradientValue = "linear-gradient(to top left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToLeftBottomTest() {
        String gradientValue = "linear-gradient(to left bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToBottomLeftTest() {
        String gradientValue = "linear-gradient(to bottom left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToRightTopTest() {
        String gradientValue = "linear-gradient(to right top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToTopRightTest() {
        String gradientValue = "linear-gradient(to top right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToRightBottomTest() {
        String gradientValue = "linear-gradient(to right bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentCornersToBottomRightTest() {
        String gradientValue = "linear-gradient(to bottom right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentDegPositiveTest() {
        String gradientValue = "linear-gradient(41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentDegNegativeTest() {
        String gradientValue = "linear-gradient(-41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentDegZeroTest() {
        String gradientValue = "linear-gradient(0deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*0/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentRadPositiveTest() {
        String gradientValue = "linear-gradient(0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentRadNegativeTest() {
        String gradientValue = "linear-gradient(-0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentRadZeroTest() {
        String gradientValue = "linear-gradient(0rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentGradPositiveTest() {
        String gradientValue = "linear-gradient(41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) -Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentGradNegativeTest() {
        String gradientValue = "linear-gradient(-41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDifferentGradZeroTest() {
        String gradientValue = "linear-gradient(0grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*0/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesRightTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesBottomTest() {
        String gradientValue = "repeating-linear-gradient(to bottom, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentSidesTopTest() {
        String gradientValue = "repeating-linear-gradient(to top, orange -20pt, red 0%, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToLeftTopTest() {
        String gradientValue = "repeating-linear-gradient(to left top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToTopLeftTest() {
        String gradientValue = "repeating-linear-gradient(to top left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToLeftBottomTest() {
        String gradientValue = "repeating-linear-gradient(to left bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToBottomLeftTest() {
        String gradientValue = "repeating-linear-gradient(to bottom left, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToRightTopTest() {
        String gradientValue = "repeating-linear-gradient(to right top, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToTopRightTest() {
        String gradientValue = "repeating-linear-gradient(to top right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToRightBottomTest() {
        String gradientValue = "repeating-linear-gradient(to right bottom, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentCornersToBottomRightTest() {
        String gradientValue = "repeating-linear-gradient(to bottom right, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegPositiveTest() {
        String gradientValue = "repeating-linear-gradient(41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-41deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*41/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentDegZeroTest() {
        String gradientValue = "repeating-linear-gradient(0deg, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, Math.PI*0/180,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadPositiveTest() {
        String gradientValue = "repeating-linear-gradient(0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, -0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-0.5rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0.5,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentRadZeroTest() {
        String gradientValue = "repeating-linear-gradient(0rad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, 0,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradPositiveTest() {
        String gradientValue = "repeating-linear-gradient(41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) -Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradNegativeTest() {
        String gradientValue = "repeating-linear-gradient(-41grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*41/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDifferentGradZeroTest() {
        String gradientValue = "repeating-linear-gradient(0grad, orange -20pt, red 0%, green, blue 100%, orange 120%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -20f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1.2f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, true, (float) Math.PI*0/200,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffColorHexTest() {
        String gradientValue = "linear-gradient(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffColorRGBTest() {
        String gradientValue = "linear-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffColorRGBaTest() {
        String gradientValue = "linear-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopTest() {
        String gradientValue = "linear-gradient(to left top, red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopRGBTest() {
        String gradientValue = "linear-gradient(to left top, red, rgb(0, 127, 0), blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradComplexArgsLeftTopRGBOffsetsHintsTest() {
        String gradientValue = "linear-gradient(to left top, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0.8f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorNameTest() {
        String gradientValue = "repeating-linear-gradient(red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorHexTest() {
        String gradientValue = "repeating-linear-gradient(#ff0000, #008000, #0000ff)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorRGBTest() {
        String gradientValue = "repeating-linear-gradient(rgb(255, 0, 0), rgb(0, 128, 0), rgb(0, 0, 255))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffColorRGBaTest() {
        String gradientValue = "repeating-linear-gradient(rgba(255, 0, 0, 1),  rgba(0, 128, 0, 1), rgba(0, 0, 255, 1))";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_BOTTOM, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red, green, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopRGBTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red, rgb(0, 127, 0), blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0f, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradComplexArgsLeftTopRGBOffsetsHintsTest() {
        String gradientValue = "repeating-linear-gradient(to left top, red 10% 20%, 30%, rgb(0, 127, 0) 80%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.2f, OffsetType.RELATIVE)
                .setHint(0.3f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 127f/255f, 0f}, 0.8f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_TOP_LEFT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteCMTest() {
        String gradientValue = "linear-gradient(to right, orange 3cm, red 3cm, green 9cm, blue 9cm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "linear-gradient(to right, orange 3mm, red 3mm, green 9mm, blue 9mm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "linear-gradient(to right, orange 30Q, red 30Q, green 90Q, blue 90Q)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "linear-gradient(to right, orange 1in, red 1in, green 3in, blue 3in)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 3*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3*72f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "linear-gradient(to right, orange 10pc, red 10pc, green 30pc, blue 30pc)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 30*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "linear-gradient(to right, orange 100pt, red 100pt, green 300pt, blue 300pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "linear-gradient(to right, orange 100px, red 100px, green 300px, blue 300px)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300*0.75f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "linear-gradient(to right, orange 3em, red 3em, green 9em, blue 9em)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "linear-gradient(to right, orange 3rem, red 3rem, green 9rem, blue 9rem)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "linear-gradient(to right, orange 3ex, red 3ex, green 9ex, blue 9ex)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*6f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9 / 2.54) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteMMTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3mm, red 3mm, green 9mm, blue 9mm)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((3f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((9f / 25.4) * 72), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteQTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 30Q, red 30Q, green 90Q, blue 90Q)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, (float) ((30f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, (float) ((90f/2.54)*72/40), OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsoluteInTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 1in, red 1in, green 3in, blue 3in)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 1*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 3*72f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 3*72f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePcTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10pc, red 10pc, green 30pc, blue 30pc)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 10*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 30*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 30*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePtTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100pt, red 100pt, green 300pt, blue 300pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsAbsolutePxTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100px, red 100px, green 300px, blue 300px)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 100*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 300*0.75f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 300*0.75f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedEmTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3em, red 3em, green 9em, blue 9em)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedRemTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3rem, red 3rem, green 9rem, blue 9rem)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 24, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*12f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*12f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatLinearGradDiffMetricsFontRelatedExTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 3ex, red 3ex, green 9ex, blue 9ex)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 3*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 9*6f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 9*6f, OffsetType.ABSOLUTE));


        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
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

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetStartEndOutTest() {
        String gradientValue = "linear-gradient(to right, orange -100pt, red 150pt, green 200pt, blue 750pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -100, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "linear-gradient(to right, orange, red -20%, green 120%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 1.2f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "linear-gradient(to right, orange, red 300pt, green, blue 80%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "linear-gradient(to right, orange, red 10%, lime, 80%, blue 80.5%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "linear-gradient(to right, orange 10%, red, lime, 40%, blue, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetSmallHintTest() {
        String gradientValue = "linear-gradient(to right, orange, 1%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetNegativeHintTest() {
        String gradientValue = "linear-gradient(to right, orange, -100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffOffsetHintTest() {
        String gradientValue = "linear-gradient(to right, orange, 100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void linearGradDiffMultipleOffsetsTest() {
        String gradientValue = "linear-gradient(to right, orange 10%, blue 60% 70%, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.PAD, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetStartEndInsideTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 100pt, red 150pt, green 200pt, blue 250pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 100, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 250, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetStartEndOutTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange -100pt, red 150pt, green 200pt, blue 750pt)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, -100, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 150, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 200, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 750, OffsetType.ABSOLUTE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoStartEndMiddleElementsOutRangeTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red -20%, green 120%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, -0.2f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 1.2f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoBetweenAbsoluteRelativeTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red 300pt, green, blue 80%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 300f, OffsetType.ABSOLUTE));
        colorStops.add(new GradientColorStop(new float[]{0f, 128f/255f, 0f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.8f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetAutoBetweenRelativeHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, red 10%, lime, 80%, blue 80.5%, black)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.8f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.805f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 1, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetHintBetweenAutosTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10%, red, lime, 40%, blue, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{1f, 0f, 0f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f,1f, 0f}, 0, OffsetType.AUTO)
                .setHint(0.4f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0, OffsetType.AUTO));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetSmallHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, 1%, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(0.01f, HintOffsetType.RELATIVE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetNegativeHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, -100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(-100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffOffsetHintTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange, 100pt, blue)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0, OffsetType.RELATIVE)
                .setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 1f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    @Test
    public void repeatingLinearGradDiffMultipleOffsetsTest() {
        String gradientValue = "repeating-linear-gradient(to right, orange 10%, blue 60% 70%, black 90%)";

        Assertions.assertTrue(CssGradientUtil.isCssLinearGradientValue(gradientValue));

        StrategyBasedLinearGradientBuilder gradientBuilder = CssGradientUtil.parseCssLinearGradient(gradientValue, 12, 12);
        Assertions.assertNotNull(gradientBuilder);

        List<GradientColorStop> colorStops = new ArrayList<>();
        colorStops.add(new GradientColorStop(new float[]{1f, 165f/255f, 0f}, 0.1f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.6f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 1f}, 0.7f, OffsetType.RELATIVE));
        colorStops.add(new GradientColorStop(new float[]{0f, 0f, 0f}, 0.9f, OffsetType.RELATIVE));

        assertStrategyBasedBuilderEquals(gradientBuilder, false, 0d,
                GradientStrategy.TO_RIGHT, GradientSpreadMethod.REPEAT, colorStops);
    }

    private void assertStrategyBasedBuilderEquals(AbstractLinearGradientBuilder gradientBuilder,
            boolean isCentralRotationStrategy, double rotateVectorAngle,
            GradientStrategy gradientStrategy, GradientSpreadMethod spreadMethod,
            List<GradientColorStop> stops) {
        Assertions.assertTrue(gradientBuilder instanceof StrategyBasedLinearGradientBuilder);

        StrategyBasedLinearGradientBuilder builder = (StrategyBasedLinearGradientBuilder) gradientBuilder;
        Assertions.assertEquals(isCentralRotationStrategy, builder.isCentralRotationAngleStrategy());
        Assertions.assertEquals(rotateVectorAngle, builder.getRotateVectorAngle(), 1e-10);
        Assertions.assertEquals(gradientStrategy, builder.getGradientStrategy());
        Assertions.assertEquals(spreadMethod, builder.getSpreadMethod());

        List<GradientColorStop> actualStops = builder.getColorStops();
        Assertions.assertEquals(stops.size(), actualStops.size());
        for (int i = 0; i < stops.size(); ++i) {
            Assertions.assertEquals(stops.get(i), actualStops.get(i));
        }
    }
}
