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
package com.itextpdf.svg.utils;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.TextLeafSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgTextUtilTest extends ExtendedITextTest {

    public static float EPS = 0.0001f;

    //Trim leading tests
    @Test
    public void trimLeadingTest() {
        String toTrim = "\t \t   to trim  \t";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to trim  \t";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingEmptyTest() {
        String toTrim = "";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingNoLeadingTest() {
        String toTrim = "to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to Test  ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingSingleWhiteSpaceTest() {
        String toTrim = " to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "to Test  ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingNonBreakingSpaceTest() {
        String toTrim = "\u00A0to Test  ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "\u00A0to Test  ";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingOnlyWhitespaceTest() {
        String toTrim = "\t\t\t   \t\t\t";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimLeadingLineBreakTest() {
        String toTrim = " \n Test ";

        String actual = SvgTextUtil.trimLeadingWhitespace(toTrim);
        String expected = "\n Test ";
        Assert.assertEquals(expected, actual);
    }

    //Trim trailing tests
    @Test
    public void trimTrailingTest() {
        String toTrim = "\t \t   to trim  \t";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "\t \t   to trim";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingEmptyTest() {
        String toTrim = "";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingNoTrailingTest() {
        String toTrim = "   to Test";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "   to Test";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingSingleWhiteSpaceTest() {
        String toTrim = " to Test ";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to Test";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingNonBreakingSpaceTest() {
        String toTrim = " to Test\u00A0";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to Test\u00A0";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingOnlyWhitespaceTest() {
        String toTrim = "\t\t\t   \t\t\t";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingLineBreakTest() {
        String toTrim = " to trim \n";

        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = " to trim \n";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimNullLeadingTest() {
        String expected = "";

        String actual = SvgTextUtil.trimLeadingWhitespace(null);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimNullTrailingTest() {
        String expected = "";

        String actual = SvgTextUtil.trimTrailingWhitespace(null);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void trimTrailingOfStringWithLength1Test() {
        String toTrim = "A";
        String actual = SvgTextUtil.trimTrailingWhitespace(toTrim);
        String expected = "A";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void processWhiteSpaceBreakLine() {
        //Create tree
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();

        TextLeafSvgNodeRenderer textBefore = new TextLeafSvgNodeRenderer();
        textBefore.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "            text\n" +
                        "            ");
        root.addChild(textBefore);

        TextSvgBranchRenderer span = new TextSvgBranchRenderer();
        TextLeafSvgNodeRenderer textInSpan = new TextLeafSvgNodeRenderer();
        textInSpan.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "                tspan text\n" +
                        "            ");
        span.addChild(textInSpan);
        root.addChild(span);

        TextLeafSvgNodeRenderer textAfter = new TextLeafSvgNodeRenderer();
        textAfter.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "            after text\n" +
                        "        ");
        root.addChild(textAfter);

        //Run
        SvgTextUtil.processWhiteSpace(root, true);
        root.getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT);
        //Create result array
        String[] actual = new String[]{
                root.getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT),
                ((TextSvgBranchRenderer) root.getChildren().get(1)).getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT),
                root.getChildren().get(2).getAttribute(SvgConstants.Attributes.TEXT_CONTENT)
        };
        //Create expected
        String[] expected = new String[]{"text", " tspan text", " after text"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void processWhiteSpaceAbsPositionChange() {
        //Create tree
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();

        TextLeafSvgNodeRenderer textBefore = new TextLeafSvgNodeRenderer();
        textBefore.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "            text\n" +
                        "            ");
        root.addChild(textBefore);

        TextSvgBranchRenderer span = new TextSvgBranchRenderer();
        span.setAttribute(SvgConstants.Attributes.X, "10");
        span.setAttribute(SvgConstants.Attributes.Y, "20");
        TextLeafSvgNodeRenderer textInSpan = new TextLeafSvgNodeRenderer();
        textInSpan.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "                tspan text\n" +
                        "            ");
        span.addChild(textInSpan);
        root.addChild(span);

        TextLeafSvgNodeRenderer textAfter = new TextLeafSvgNodeRenderer();
        textAfter.setAttribute(SvgConstants.Attributes.TEXT_CONTENT,
                "\n" +
                        "            after text\n" +
                        "        ");
        root.addChild(textAfter);

        //Run
        SvgTextUtil.processWhiteSpace(root, true);
        root.getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT);
        //Create result array
        String[] actual = new String[]{
                root.getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT),
                ((TextSvgBranchRenderer) root.getChildren().get(1)).getChildren().get(0).getAttribute(SvgConstants.Attributes.TEXT_CONTENT),
                root.getChildren().get(2).getAttribute(SvgConstants.Attributes.TEXT_CONTENT)
        };
        //Create expected
        String[] expected = new String[]{"text", "tspan text", " after text"};//No preceding whitespace on the second element
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void processFontSizeInEM() {
        float expected = 120;

        // Create a renderer
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();
        root.setAttribute(SvgConstants.Attributes.FONT_SIZE, "12em");

        //Run
        float actual = SvgTextUtil.resolveFontSize(root, 10);

        Assert.assertEquals(expected, actual, EPS);
    }

    @Test
    public void processFontSizeInPX() {
        float expected = 24;

        // Create a renderer
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();
        root.setAttribute(SvgConstants.Attributes.FONT_SIZE, "32px");

        //Run
        float actual = SvgTextUtil.resolveFontSize(root, 10);

        Assert.assertEquals(expected, actual, EPS);
    }

    @Test
    public void processFontSizeInPT() {
        float expected = 24;

        // Create a renderer
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();
        root.setAttribute(SvgConstants.Attributes.FONT_SIZE, "24pt");

        //Run
        float actual = SvgTextUtil.resolveFontSize(root, 10);

        Assert.assertEquals(expected, actual, EPS);
    }

    @Test
    public void processKeywordedFontSize() {
        float expected = 24;

        // Create a renderer
        TextSvgBranchRenderer root = new TextSvgBranchRenderer();
        root.setAttribute(SvgConstants.Attributes.FONT_SIZE, "xx-large");

        //Run
        // Parent's font-size doesn't impact the result in this test
        float actual = SvgTextUtil.resolveFontSize(root, 10);

        Assert.assertEquals(expected, actual, EPS);
    }

    @Test
    public void testFilterReferenceValueMarkerReference() {
        Assert.assertEquals("MarkerCircle", SvgTextUtil.filterReferenceValue("url(#MarkerCircle)"));
    }

    @Test
    public void testFilterReferenceValueMarkerFullEntry() {
        Assert.assertEquals("marker-end: MarkerArrow;",
                SvgTextUtil.filterReferenceValue("marker-end: url(#MarkerArrow);"));
    }

    @Test
    public void testFilterReferenceValueSimpleReference() {
        Assert.assertEquals("figure11",
                SvgTextUtil.filterReferenceValue("#figure11"));
    }

    @Test
    public void testFilterReferenceValueNoFilter() {
        Assert.assertEquals("circle",
                SvgTextUtil.filterReferenceValue("circle"));
    }

    @Test
    public void testFilterReferenceValueEmptyString() {
        Assert.assertEquals("",
                SvgTextUtil.filterReferenceValue(""));
    }

    @Test
    public void testFilterReferenceValueNumberString() {
        Assert.assertEquals("16554245",
                SvgTextUtil.filterReferenceValue("16554245"));
    }

    @Test
    public void testFilterReferenceValueFilteredValues() {
        Assert.assertEquals("",
                SvgTextUtil.filterReferenceValue("))url(####)"));
    }
}
