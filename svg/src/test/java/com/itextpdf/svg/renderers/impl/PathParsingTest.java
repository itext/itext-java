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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Collection;

@Tag("UnitTest")
public class PathParsingTest extends ExtendedITextTest {

    @Test
    public void compactArcFlagsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 10 0 0020 0");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 10 0 0 0 20 0"),
                path.parsePathOperations());
        Assertions.assertEquals(2, path.getShapes().size());
    }

    @Test
    public void compactArcFlagCombinationsTest() {
        for (String flags : Arrays.asList("00", "01", "10", "11")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "A10 20 30 " + flags + "40 50");
            Assertions.assertEquals(Arrays.asList("A 10 20 30 " + flags.charAt(0) + " "
                    + flags.charAt(1) + " 40 50"), path.parsePathOperations());
        }
    }

    @Test
    public void compactArcRepeatedArgumentsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 01-30.5.6 10 20 0 10.5-.6");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 -30.5 .6 10 20 0 1 0 .5 -.6"),
                path.parsePathOperations());
        Assertions.assertEquals(3, path.getShapes().size());
    }

    @Test
    public void compactArcExponentsAndSignsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "A1e+1,2E-1,-3e1,01+4e-1 -5E+1");
        Assertions.assertEquals(Arrays.asList("A 1e+1 2E-1 -3e1 0 1 +4e-1 -5E+1"),
                path.parsePathOperations());
    }

    @Test
    public void separatedArcFlagsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a 10,20,30,0,1,40,50");
        Assertions.assertEquals(Arrays.asList("a 10 20 30 0 1 40 50"), path.parsePathOperations());
    }

    @Test
    public void incompleteArcArgumentsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 01");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1"), path.parsePathOperations());
        Assertions.assertEquals(1, path.getShapes().size());
    }

    @Test
    public void malformedArcArgumentTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 01--30 40");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 - -30 40"), path.parsePathOperations());
    }

    @Test
    public void malformedArcTailStopsRenderingTest() {
        for (String tail : Arrays.asList(".", "-", "e")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 01 30 40 " + tail + "l10 10");
            Collection<IPathShape> shapes = path.getShapes();
            Assertions.assertEquals(2, shapes.size());
            IPathShape arc = shapes.toArray(new IPathShape[0])[1];
            Assertions.assertEquals(30, arc.getEndingPoint().getX());
            Assertions.assertEquals(40, arc.getEndingPoint().getY());
        }
    }

    @Test
    public void percentageArcRadiusTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a50% 50% 0 0 1 10 10l20 20");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 50% 50% 0 0 1 10 10", "l 20 20"),
                path.parsePathOperations());
    }

    @Test
    public void malformedArcExponentTest() {
        for (String exponent : Arrays.asList("30e", "30e+", "30E", "30E-")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 01 " + exponent + " 40");
            Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 " + exponent + " 40"),
                    path.parsePathOperations());
        }
    }

    @Test
    public void decimalArcFlagsTest() {
        String[][] cases = {
                {"1.0 1", "1 .0 1"}, {"0.5 1", "0 .5 1"}, {".5 1", ".5 1"}, {"1 .5", "1 .5"}};
        for (String[] testCase : cases) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 " + testCase[0] + " 30 40l5 6");
            Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 " + testCase[1] + " 30 40", "l 5 6"),
                    path.parsePathOperations());
        }
    }

    @Test
    public void malformedArcExponentSuffixUsesSharedSplitterTest() {
        for (String suffix : Arrays.asList("e", "E", "e.", "E.")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 0130 40" + suffix + "l5 6");
            Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 30 40"
                    + suffix, "l 5 6"),
                    path.parsePathOperations());
        }
    }

    @Test
    public void incompleteSignedArcExponentUsesSharedSplitterTest() {
        for (String suffix : Arrays.asList("e+", "E-")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 0130 40" + suffix + "l5 6");
            Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 30 40" + suffix, "l 5 6"),
                    path.parsePathOperations());
        }
    }

    @Test
    public void outOfRangeArcFlagsTest() {
        for (String flag : Arrays.asList("2", "-1", "+1")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 " + flag + " 1 30 40");
            Assertions.assertEquals(1, path.getShapes().size());
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 1 " + flag + " 30 40");
            Assertions.assertEquals(1, path.getShapes().size());
        }
    }

    @Test
    public void outOfRangeArcFlagsStopRenderingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 0 1 30 40 10 20 0 -1 0 50 60l5 6Z");
        Assertions.assertEquals(2, path.getShapes().size());
    }

    @Test
    public void incompleteArcGroupStopsRenderingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 0130 40 10 20 0l5 6");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 30 40 10 20 0", "l 5 6"),
                path.parsePathOperations());
        Assertions.assertEquals(2, path.getShapes().size());
    }

    @Test
    public void arcEndPointWithExtraNumberStopsRenderingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M 50 200 a -100 -50 15 1 1 250.7 50.8.9999 Z");
        IPathShape[] shapes = path.getShapes().toArray(new IPathShape[0]);
        Assertions.assertEquals(2, shapes.length);
        Assertions.assertEquals(250.8, shapes[1].getEndingPoint().getY(), 1e-6);
    }

    @Test
    public void arcWithoutArgumentsStopsRenderingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a l5 6");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a", "l 5 6"), path.parsePathOperations());
        Assertions.assertEquals(1, path.getShapes().size());
    }

    @Test
    public void signedArcRadiiAndRotationTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a-10+20-45 1 0 30 40");
        Assertions.assertEquals(Arrays.asList("a -10 +20 -45 1 0 30 40"), path.parsePathOperations());
    }

    @Test
    public void arcNumberProductionTest() {
        // fractional-constant ::= (digit* "." digit+) | digit+, exponent ::= ("e" | "E") sign? digit+
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a.5 5 5.5 0 0 1e5 1E-5 1 1e+5 0 1 1 0.6.5");
        Assertions.assertEquals(Arrays.asList("a .5 5 5.5 0 0 1e5 1E-5 1 1e+5 0 1 1 0.6 .5"),
                path.parsePathOperations());
    }

    @Test
    public void arcNumberIsNotSplitToCompleteArgumentTest() {
        // "3040" is a single number, so the arc lacks its y coordinate
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a10 20 0 0 1 3040");
        Assertions.assertEquals(Arrays.asList("a 10 20 0 0 1 3040"), path.parsePathOperations());
    }

    @Test
    public void arcCommaWspTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a10 ,20, 0\t,\n0\r1\f30 , 40,10 20 0 0 1 30 40");
        Assertions.assertEquals(Arrays.asList("a 10 20 0 0 1 30 40 10 20 0 0 1 30 40"),
                path.parsePathOperations());
    }

    @Test
    public void arcCommasUseSharedSplitterTest() {
        String[][] cases = {
                {"a10,,20 0 0 1 30 40", "a 10 20 0 0 1 30 40"},
                {"a,10 20 0 0 1 30 40", "a 10 20 0 0 1 30 40"},
                {"a10 20 0 0 1 30 40,", "a 10 20 0 0 1 30 40"},
                {"a10 20 0 0 1 30 40,,10 20 0 0 1 30 40", "a 10 20 0 0 1 30 40 10 20 0 0 1 30 40"}};
        for (String[] testCase : cases) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0" + testCase[0] + "l5 6");
            Assertions.assertEquals(Arrays.asList("M 0 0", testCase[1], "l 5 6"), path.parsePathOperations());
        }
    }

    @Test
    public void compactArcFlagsAcrossTokensTest() {
        for (String arguments : Arrays.asList("004.547", "0 04.547", "00 4.547", "0 0 4.547")) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0a2.274 2.274 0 " + arguments + " 0");
            Assertions.assertEquals(Arrays.asList("M 0 0", "a 2.274 2.274 0 0 0 4.547 0"),
                    path.parsePathOperations(), arguments);
        }
    }

    @Test
    public void arcWithIncompleteTrailingDecimalTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a10 20 0 0 1 30.l5 6");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a 10 20 0 0 1 30.", "l 5 6"), path.parsePathOperations());
    }

    @Test
    public void longIncompleteArcNumberTest() {
        String digits = new String(new char[50000]).replace('\0', '1');
        String[][] cases = {
                {"a" + digits, "a " + digits},
                {"a10 20 0 0 1 30 40 " + digits, "a 10 20 0 0 1 30 40 " + digits}};
        for (String[] testCase : cases) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, "M0 0" + testCase[0] + "l5 6");
            Assertions.assertEquals(Arrays.asList("M 0 0", testCase[1], "l 5 6"), path.parsePathOperations());
        }
    }

    @Test
    public void separateArcFlagsWithRepeatedSpacesTest() {
        String[][] cases = {
                {"a  10 20 0  0040 50", "a 10 20 0 0 0 40 50"},
                {"a 10 20 0  0  040 50", "a 10 20 0 0 0 40 50"},
                {"a 10 20 0  00  40 50", "a 10 20 0 0 0 40 50"},
                {"a 10 20 0  0  0  40 50", "a 10 20 0 0 0 40 50"},
                {"a 10 20 0 0040 50  10 20 0 1  160 70", "a 10 20 0 0 0 40 50 10 20 0 1 1 60 70"}};
        for (String[] testCase : cases) {
            Assertions.assertEquals(testCase[1], PathSvgNodeRenderer.separateArcFlags(testCase[0]));
        }
    }

    @Test
    public void longWhitespaceRunInArcTest() {
        String spaces = new String(new char[50000]).replace('\0', ' ');
        String[][] cases = {
                {"a 1" + spaces + "x", "a 1 x"},
                {"a 10 20 0 0 1 30 40" + spaces + "x", "a 10 20 0 0 1 30 40 x"}};
        for (String[] testCase : cases) {
            Assertions.assertEquals(testCase[1], PathSvgNodeRenderer.separateArcFlags(testCase[0]));
        }
        Assertions.assertEquals("a 10 20 0 0 1 30 40", PathSvgNodeRenderer.separateArcFlags(
                "a 10" + spaces + "20 0 0 1 30" + spaces + "40"));
    }

    @Test
    public void separateArcFlagsEmptyAndIncompleteInputTest() {
        String[][] cases = {
                {"", ""},
                {"   ", ""},
                {"a", "a"},
                {"A  ", "A"},
                {"a 10", "a 10"},
                {"a 10 20 0", "a 10 20 0"},
                {"a 10 20 0 0", "a 10 20 0 0"},
                {"a 10 20 0 01", "a 10 20 0 0 1"}};
        for (String[] testCase : cases) {
            Assertions.assertEquals(testCase[1], PathSvgNodeRenderer.separateArcFlags(testCase[0]));
        }
    }

    @Test
    public void truncatedArcPathParsingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M0 0a");
        Assertions.assertEquals(Arrays.asList("M 0 0", "a"), path.parsePathOperations());
    }

    @Test
    public void compactArcFlagsAfterCommaTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "a10,20,0,01,30,40");
        Assertions.assertEquals(Arrays.asList("a 10 20 0 0 1 30 40"), path.parsePathOperations());
    }

    @Test
    public void compactArcFollowingRelativeLineTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "M10 10a10 20 0 0130 40l5 6");
        IPathShape[] shapes = path.getShapes().toArray(new IPathShape[0]);
        Assertions.assertEquals(3, shapes.length);
        Assertions.assertEquals(45, shapes[2].getEndingPoint().getX());
        Assertions.assertEquals(56, shapes[2].getEndingPoint().getY());
    }

    @Test
    public void pathParsingOperatorEmptyTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertTrue(ops.isEmpty());
    }

    @Test
    public void pathParsingOperatorDefaultValueTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttributesAndStyles(new HashMap<>());
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertTrue(ops.isEmpty());
    }

    @Test
    public void pathParsingOperatorOnlySpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "  ");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertTrue(ops.isEmpty());
    }

    @Test
    public void pathParsingOperatorBadOperatorTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "b 1 1");

        Assertions.assertThrows(SvgProcessingException.class, () -> path.parsePathOperations());
    }

    @Test
    public void pathParsingOperatorLaterBadOperatorTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 100 l 50 50 x");

        Assertions.assertThrows(SvgProcessingException.class, () -> path.parsePathOperations());
    }

    @Test
    public void pathParsingOperatorStartWithSpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "  \t\n m 200 100 l 50 50");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(2, ops.size());
    }

    @Test
    public void pathParsingOperatorEndWithSpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 100 l 50 50  m 200 100 l 50 50  \t\n ");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(4, ops.size());
    }

    @Test
    public void pathParsingNoOperatorSpacesNoExceptionTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m200,100L50,50L200,100");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(3, ops.size());
    }

    @Test
    public void pathParsingLoseCommasTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m200,100L50,50L200,100");
        Collection<String> ops = path.parsePathOperations();
        for (String op : ops) {
            Assertions.assertFalse(op.contains(","));
        }
    }

    @Test
    public void pathParsingBadOperatorArgsNoExceptionTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 l m");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(3, ops.size());
    }

    @Test
    public void pathParsingHandlesDecPointsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m2.35.96");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(1, ops.size());
        Assertions.assertTrue(ops.contains("m 2.35 .96"));
    }

    @Test
    public void pathParsingHandlesMinusTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m40-50");
        Collection<String> ops = path.parsePathOperations();
        Assertions.assertEquals(1, ops.size());
        Assertions.assertTrue(ops.contains("m 40 -50"));
    }

    @Test
    public void decimalPointParsingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96";

        String expected = "2.35 .96";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingSpaceTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25 .25";

        String expected = "2.35 .96 3.25 .25";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingTabTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25\t.25";

        String expected = "2.35 .96 3.25\t.25";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingMinusTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25-.25";

        String expected = "2.35 .96 3.25 -.25";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void negativeAfterPositiveTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "40-50";

        String expected = "40 -50";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void plusSignNumberBoundariesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String[][] cases = {
                {"10+20", "10 +20"},
                {"1.5+.6", "1.5 +.6"},
                {"+1+2", "+1 +2"},
                {"1+.5.6+7", "1 +.5 .6 +7"},
                {"+1e+3\t-2E-4", "+1e+3\t-2E-4"}};
        for (String[] testCase : cases) {
            Assertions.assertEquals(testCase[1], path.separateDecimalPoints(testCase[0]), testCase[0]);
        }
    }

    @Test
    public void exponentBoundaryHandlingIsUnchangedTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        for (String input : Arrays.asList("1e5-3", "1E-5+3", "1e+5-3", "1e5.6", "1e-3.4")) {
            Assertions.assertEquals(input, path.separateDecimalPoints(input));
        }
    }

    @Test
    public void compactNonArcCommandsTest() {
        String[][] cases = {
                {"M10+20", "M 10 +20"},
                {"L10+3", "L 10 +3"},
                {"H1+3", "H 1 +3"},
                {"V1+.6", "V 1 +.6"},
                {"C1-4+5-6.5.6+7", "C 1 -4 +5 -6.5 .6 +7"},
                {"S1+2-3+4", "S 1 +2 -3 +4"},
                {"Q1.5.5+3-4", "Q 1.5 .5 +3 -4"},
                {"T1+3", "T 1 +3"}};
        for (String[] testCase : cases) {
            PathSvgNodeRenderer path = new PathSvgNodeRenderer();
            path.setAttribute(SvgConstants.Attributes.D, testCase[0]);
            Assertions.assertEquals(Collections.singletonList(testCase[1]), path.parsePathOperations(), testCase[0]);
        }
    }

    @Test
    public void exponentInNumberTest01() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401e-14 393.3333333333333 -2.842170943040401e-14";

        String expected = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401e-14 393.3333333333333 -2.842170943040401e-14";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void exponentInNumberTest02() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401E-14 393.3333333333333 -2.842170943040401E-14";

        String expected = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401E-14 393.3333333333333 -2.842170943040401E-14";
        String actual = path.separateDecimalPoints(input);
        Assertions.assertEquals(expected, actual);
    }
}
