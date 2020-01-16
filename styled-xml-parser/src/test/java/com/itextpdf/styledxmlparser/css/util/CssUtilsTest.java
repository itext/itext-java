/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CssUtilsTest extends ExtendedITextTest {

    public static float EPS = 0.0001f;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void parseAbsoluteLengthFromNAN() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "Definitely not a number"));

        String value = "Definitely not a number";
        CssUtils.parseAbsoluteLength(value);
    }

    @Test
    public void parseAbsoluteLengthFromNull() {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "null"));

        String value = null;
        CssUtils.parseAbsoluteLength(value);
    }

    @Test
    public void parseAbsoluteLengthFrom10px() {
        String value = "10px";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.PX);
        float expected = 7.5f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10cm() {
        String value = "10cm";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.CM);
        float expected = 283.46457f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10in() {
        String value = "10in";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.IN);
        float expected = 720.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10pt() {
        String value = "10pt";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.PT);
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAboluteLengthExponential01() {
        String value = "1e2pt";
        float actual = CssUtils.parseAbsoluteLength(value);
        float expected = 1e2f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAboluteLengthExponential02() {
        String value = "1e2px";
        float actual = CssUtils.parseAbsoluteLength(value);
        float expected = 1e2f * 0.75f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 1)})
    public void parseAbsoluteLengthFromUnknownType() {
        String value = "10pateekes";
        float actual = CssUtils.parseAbsoluteLength(value, "pateekes");
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void validateMetricValue() {
        Assert.assertTrue(CssUtils.isMetricValue("1px"));
        Assert.assertTrue(CssUtils.isMetricValue("1in"));
        Assert.assertTrue(CssUtils.isMetricValue("1cm"));
        Assert.assertTrue(CssUtils.isMetricValue("1mm"));
        Assert.assertTrue(CssUtils.isMetricValue("1pc"));
        Assert.assertFalse(CssUtils.isMetricValue("1em"));
        Assert.assertFalse(CssUtils.isMetricValue("1rem"));
        Assert.assertFalse(CssUtils.isMetricValue("1ex"));
        Assert.assertTrue(CssUtils.isMetricValue("1pt"));
        Assert.assertFalse(CssUtils.isMetricValue("1inch"));
        Assert.assertFalse(CssUtils.isMetricValue("+1m"));
    }

    @Test
    public void validateNumericValue() {
        Assert.assertTrue(CssUtils.isNumericValue("1"));
        Assert.assertTrue(CssUtils.isNumericValue("12"));
        Assert.assertTrue(CssUtils.isNumericValue("1.2"));
        Assert.assertTrue(CssUtils.isNumericValue(".12"));
        Assert.assertFalse(CssUtils.isNumericValue("12f"));
        Assert.assertFalse(CssUtils.isNumericValue("f1.2"));
        Assert.assertFalse(CssUtils.isNumericValue(".12f"));
    }

    @Test
    public void parseLength() {
        Assert.assertEquals(9, CssUtils.parseAbsoluteLength("12"), 0);
        Assert.assertEquals(576, CssUtils.parseAbsoluteLength("8inch"), 0);
        Assert.assertEquals(576, CssUtils.parseAbsoluteLength("8", CommonCssConstants.IN), 0);
    }

    @Test
    public void normalizeProperty() {
        Assert.assertEquals("part1 part2", CssUtils.normalizeCssProperty("   part1   part2  "));
        Assert.assertEquals("\" the next quote is ESCAPED \\\\\\\" still  IN string \"", CssUtils.normalizeCssProperty("\" the next quote is ESCAPED \\\\\\\" still  IN string \""));
        Assert.assertEquals("\" the next quote is NOT ESCAPED \\\\\" not in the string", CssUtils.normalizeCssProperty("\" the next quote is NOT ESCAPED \\\\\" NOT in   THE string"));
        Assert.assertEquals("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\"", CssUtils.normalizeCssProperty("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\""));
        Assert.assertEquals("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'", CssUtils.normalizeCssProperty("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'"));
        Assert.assertEquals("\" ( BLA \" attr(href)\" BLA )  \"", CssUtils.normalizeCssProperty("\" ( BLA \"      AttR( Href  )\" BLA )  \""));
        Assert.assertEquals("\" (  \"attr(href) \"  )  \"", CssUtils.normalizeCssProperty("\" (  \"aTTr( hREf  )   \"  )  \""));
        Assert.assertEquals("rgba(255,255,255,0.2)", CssUtils.normalizeCssProperty("rgba(  255,  255 ,  255 ,0.2   )"));
    }

    @Test
    public void normalizeUrlTest() {
        Assert.assertEquals("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)",
                CssUtils.normalizeCssProperty("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)"));
        Assert.assertEquals("url(\"quoted  Url\")", CssUtils.normalizeCssProperty("  url(  \"quoted  Url\")"));
        Assert.assertEquals("url('quoted  Url')", CssUtils.normalizeCssProperty("  url(  'quoted  Url')"));
        Assert.assertEquals("url(haveEscapedEndBracket\\))", CssUtils.normalizeCssProperty("url(  haveEscapedEndBracket\\) )"));
    }

    @Test
    public void parseUnicodeRangeTest() {
        Assert.assertEquals("[(0; 1048575)]", CssUtils.parseUnicodeRange("U+?????").toString());
        Assert.assertEquals("[(38; 38)]", CssUtils.parseUnicodeRange("U+26").toString());
        Assert.assertEquals("[(0; 127)]", CssUtils.parseUnicodeRange(" U+0-7F").toString());
        Assert.assertEquals("[(37; 255)]", CssUtils.parseUnicodeRange("U+0025-00FF").toString());
        Assert.assertEquals("[(1024; 1279)]", CssUtils.parseUnicodeRange("U+4??").toString());
        Assert.assertEquals("[(262224; 327519)]", CssUtils.parseUnicodeRange("U+4??5?").toString());
        Assert.assertEquals("[(37; 255), (1024; 1279)]", CssUtils.parseUnicodeRange("U+0025-00FF, U+4??").toString());

        Assert.assertNull(CssUtils.parseUnicodeRange("U+??????")); // more than 5 question marks are not allowed
        Assert.assertNull(CssUtils.parseUnicodeRange("UU+7-10")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7?-9?")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7-")); // wrong syntax
    }

    @Test
    public void parseAbsoluteFontSizeTest() {
        Assert.assertEquals(75, CssUtils.parseAbsoluteFontSize("100", CommonCssConstants.PX), EPS);
        Assert.assertEquals(75, CssUtils.parseAbsoluteFontSize("100px"), EPS);
        Assert.assertEquals(12, CssUtils.parseAbsoluteFontSize(CommonCssConstants.MEDIUM), EPS);
        Assert.assertEquals(0, CssUtils.parseAbsoluteFontSize("", ""), EPS);
    }

    @Test
    public void parseRelativeFontSizeTest() {
        Assert.assertEquals(120, CssUtils.parseRelativeFontSize("10em", 12), EPS);
        Assert.assertEquals(12.5f, CssUtils.parseRelativeFontSize(CommonCssConstants.SMALLER, 15), EPS);
    }


    @Test
    public void parseAbsoluteLengthTest() {
        Assert.assertEquals(75, CssUtils.parseAbsoluteLength("100", CommonCssConstants.PX), EPS);
        Assert.assertEquals(75, CssUtils.parseAbsoluteLength("100px"), EPS);
    }

    @Test
    public void parseInvalidFloat() {
        String value = "invalidFloat";
        try {
            Assert.assertNull(CssUtils.parseFloat(value));
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void parseAbsoluteLength12cmTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix.
        Assert.assertEquals(340.15747f, CssUtils.parseAbsoluteLength("12cm"), 0f);
    }


    @Test
    public void parseAbsoluteLength12qTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix
        Assert.assertEquals(8.503937f, CssUtils.parseAbsoluteLength("12q"), 0f);
    }

    @Test
    public void testIsAngleCorrectValues() {
       Assert.assertTrue(CssUtils.isAngleValue("10deg"));
       Assert.assertTrue(CssUtils.isAngleValue("-20grad"));
       Assert.assertTrue(CssUtils.isAngleValue("30.5rad"));
       Assert.assertTrue(CssUtils.isAngleValue("0rad"));
    }

    @Test
    public void testIsAngleNullValue() {
        Assert.assertFalse(CssUtils.isAngleValue(null));
    }

    @Test
    public void testIsAngleIncorrectValues() {
        Assert.assertFalse(CssUtils.isAngleValue("deg"));
        Assert.assertFalse(CssUtils.isAngleValue("-20,6grad"));
        Assert.assertFalse(CssUtils.isAngleValue("0"));
        Assert.assertFalse(CssUtils.isAngleValue("10in"));
        Assert.assertFalse(CssUtils.isAngleValue("10px"));
    }
}
