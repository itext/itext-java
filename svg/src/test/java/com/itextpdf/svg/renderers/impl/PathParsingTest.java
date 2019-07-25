/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.util.Collection;

@Category(UnitTest.class)
public class PathParsingTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void pathParsingNoDOperatorTest() {
        // Path objects must have a d attribute
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.STROKE, "black");
        path.parsePathOperations();
    }

    @Test
    public void pathParsingOperatorEmptyTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertTrue(ops.isEmpty());
    }

    @Test
    public void pathParsingOperatorOnlySpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "  ");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertTrue(ops.isEmpty());
    }

    @Test
    public void pathParsingOperatorBadOperatorTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "b 1 1");
        path.parsePathOperations();
    }

    @Test
    public void pathParsingOperatorLaterBadOperatorTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 100 l 50 50 x");
        path.parsePathOperations();
    }

    @Test
    public void pathParsingOperatorStartWithSpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "  \t\n m 200 100 l 50 50");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(2, ops.size());
    }

    @Test
    public void pathParsingOperatorEndWithSpacesTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 100 l 50 50  m 200 100 l 50 50  \t\n ");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(4, ops.size());
    }

    @Test
    public void pathParsingNoOperatorSpacesNoExceptionTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m200,100L50,50L200,100");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(3, ops.size());
    }

    @Test
    public void pathParsingLoseCommasTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m200,100L50,50L200,100");
        Collection<String> ops = path.parsePathOperations();
        for (String op : ops) {
            Assert.assertFalse(op.contains(","));
        }
    }

    @Test
    public void pathParsingBadOperatorArgsNoExceptionTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m 200 l m");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(3, ops.size());
    }

    @Test
    public void pathParsingHandlesDecPointsTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m2.35.96");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(1, ops.size());
        Assert.assertTrue(ops.contains("m 2.35 .96"));
    }

    @Test
    public void pathParsingHandlesMinusTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, "m40-50");
        Collection<String> ops = path.parsePathOperations();
        Assert.assertEquals(1, ops.size());
        Assert.assertTrue(ops.contains("m 40 -50"));
    }

    @Test
    public void decimalPointParsingTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96";

        String expected = "2.35 .96";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingSpaceTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25 .25";

        String expected = "2.35 .96 3.25 .25";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingTabTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25\t.25";

        String expected = "2.35 .96 3.25\t.25";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void decimalPointParsingMinusTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "2.35.96 3.25-.25";

        String expected = "2.35 .96 3.25 -.25";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void negativeAfterPositiveTest() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "40-50";

        String expected = "40 -50";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void exponentInNumberTest01() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String input = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401e-14 393.3333333333333 -2.842170943040401e-14";

        String expected = "C 268.88888888888886 67.97916666666663e+10 331.1111111111111 -2.842170943040401e-14 393.3333333333333 -2.842170943040401e-14";
        String actual = path.separateDecimalPoints(input);
        Assert.assertEquals(expected, actual);
    }

}
