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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Collection;

@Tag("UnitTest")
public class PathParsingTest extends ExtendedITextTest {

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
