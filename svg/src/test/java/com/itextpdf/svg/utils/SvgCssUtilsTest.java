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
package com.itextpdf.svg.utils;

import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgCssUtilsTest extends ExtendedITextTest {

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
        float[] expected = new float[] {-1.3333334f, 0f, 1.3333334f};

        for (int i = 0; i < input.length; i++) {
            float actual = SvgCssUtils.convertPtsToPx(input[i]);
            Assert.assertEquals(expected[i], actual, 0f);
        }
    }

    @Test
    public void convertFloatMaximumToPdfTest() {
        float expected = Float.POSITIVE_INFINITY;
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

    @Test
    public void convertFloatMinimumToPdfTest() {
        float expected = 1.4E-45f;
        float actual = SvgCssUtils.convertPtsToPx(Float.MIN_VALUE);

        Assert.assertEquals(expected, actual, 0f);
    }

    @Test
    public void compareIsStyleSheetLinkResult() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        boolean expected = CssUtils.isStyleSheetLink(elementNode);
        boolean actual = SvgCssUtils.isStyleSheetLink(elementNode);

        Assert.assertEquals(actual, expected);
    }
}
