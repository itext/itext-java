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
}
