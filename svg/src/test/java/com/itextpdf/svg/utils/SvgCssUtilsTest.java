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
package com.itextpdf.svg.utils;

import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgCssUtilsTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void commaSplitValueTest() {
        String input = "a,b,c,d";
        List<String> expected = new ArrayList<>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        expected.add("d");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void leadingAndTrailingWhiteSpaceTest() {
        String input = "          -140.465,-116.438 -163.725,-103.028 -259.805,-47.618         ";

        List<String> expected = new ArrayList<>();
        expected.add("-140.465");
        expected.add("-116.438");
        expected.add("-163.725");
        expected.add("-103.028");
        expected.add("-259.805");
        expected.add("-47.618");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void whitespaceSplitValueTest() {
        String input = "1 2 3 4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void newLineSplitValueTest() {
        String input = "1\n2\n3\n4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tabSplitValueTest() {
        String input = "1\t2\t3\t4";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("3");
        expected.add("4");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mixedCommaWhitespaceSplitValueTest() {
        String input = "1,2 a,b";
        List<String> expected = new ArrayList<>();
        expected.add("1");
        expected.add("2");
        expected.add("a");
        expected.add("b");

        List<String> actual = SvgCssUtils.splitValueList(input);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullSplitValueTest() {
        List<String> actual = SvgCssUtils.splitValueList(null);

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void emptySplitValueTest() {
        List<String> actual = SvgCssUtils.splitValueList("");

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void normalConvertPtsToPxTest() {
        float[] input = new float[] { -1f, 0f, 1f };
        float[] expected = new float[] {-0.75f, 0f, 0.75f};

        for (int i = 0; i < input.length; i++) {
            float actual = SvgCssUtils.convertPtsToPx(input[i]);
            Assert.assertEquals(expected[i], actual, 0f);
        }
    }

    @Test
    public void convertFloatMaximumToPdfTest() {
        float expected = 2.5521175E38f;
        float actual = SvgCssUtils.convertPtsToPx(Float.MAX_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void convertFloatToStringTest() {
        String expected = "0.5";
        String actual = SvgCssUtils.convertFloatToString(0.5f);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void convertLongerFloatToStringTest() {
        String expected = "0.1234567";
        String actual = SvgCssUtils.convertFloatToString(0.1234567f);

        Assert.assertEquals(expected, actual);
    }

    @Ignore("TODO: Check autoport for failing float comparisons. Blocked by RND-882\n")
    @Test
    public void convertFloatMinimumToPdfTest() {
        float expected = 1.4E-45f;
        float actual = SvgCssUtils.convertPtsToPx(Float.MIN_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }
}
