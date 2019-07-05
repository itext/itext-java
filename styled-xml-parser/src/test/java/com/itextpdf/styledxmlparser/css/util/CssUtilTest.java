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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class CssUtilTest extends ExtendedITextTest {

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
        assertEquals(true, CssUtils.isMetricValue("1px"));
        assertEquals(true, CssUtils.isMetricValue("1in"));
        assertEquals(true, CssUtils.isMetricValue("1cm"));
        assertEquals(true, CssUtils.isMetricValue("1mm"));
        assertEquals(true, CssUtils.isMetricValue("1pc"));
        assertEquals(false, CssUtils.isMetricValue("1em"));
        assertEquals(false, CssUtils.isMetricValue("1rem"));
        assertEquals(false, CssUtils.isMetricValue("1ex"));
        assertEquals(true, CssUtils.isMetricValue("1pt"));
        assertEquals(false, CssUtils.isMetricValue("1inch"));
        assertEquals(false, CssUtils.isMetricValue("+1m"));
    }

    @Test
    public void validateNumericValue() {
        assertEquals(true, CssUtils.isNumericValue("1"));
        assertEquals(true, CssUtils.isNumericValue("12"));
        assertEquals(true, CssUtils.isNumericValue("1.2"));
        assertEquals(true, CssUtils.isNumericValue(".12"));
        assertEquals(false, CssUtils.isNumericValue("12f"));
        assertEquals(false, CssUtils.isNumericValue("f1.2"));
        assertEquals(false, CssUtils.isNumericValue(".12f"));
    }

    @Test
    public void parseLength() {
        assertEquals(9, CssUtils.parseAbsoluteLength("12"), 0);
        assertEquals(576, CssUtils.parseAbsoluteLength("8inch"), 0);
        assertEquals(576, CssUtils.parseAbsoluteLength("8", CommonCssConstants.IN), 0);
    }

    @Test
    public void normalizeProperty() {
        assertEquals("part1 part2", CssUtils.normalizeCssProperty("   part1   part2  "));
        assertEquals("\" the next quote is ESCAPED \\\\\\\" still  IN string \"", CssUtils.normalizeCssProperty("\" the next quote is ESCAPED \\\\\\\" still  IN string \""));
        assertEquals("\" the next quote is NOT ESCAPED \\\\\" not in the string", CssUtils.normalizeCssProperty("\" the next quote is NOT ESCAPED \\\\\" NOT in   THE string"));
        assertEquals("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\"", CssUtils.normalizeCssProperty("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\""));
        assertEquals("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'", CssUtils.normalizeCssProperty("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'"));
        assertEquals("\" ( BLA \" attr(href)\" BLA )  \"", CssUtils.normalizeCssProperty("\" ( BLA \"      AttR( Href  )\" BLA )  \""));
        assertEquals("\" (  \"attr(href) \"  )  \"", CssUtils.normalizeCssProperty("\" (  \"aTTr( hREf  )   \"  )  \""));
        assertEquals("rgba(255,255,255,0.2)", CssUtils.normalizeCssProperty("rgba(  255,  255 ,  255 ,0.2   )"));
    }

    @Test
    public void normalizeUrlTest() {
        assertEquals("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)",
                CssUtils.normalizeCssProperty("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)"));
        assertEquals("url(\"quoted  Url\")", CssUtils.normalizeCssProperty("  url(  \"quoted  Url\")"));
        assertEquals("url('quoted  Url')", CssUtils.normalizeCssProperty("  url(  'quoted  Url')"));
        assertEquals("url(haveEscapedEndBracket\\))", CssUtils.normalizeCssProperty("url(  haveEscapedEndBracket\\) )"));
    }

    @Test
    public void parseUnicodeRangeTest() {
        assertEquals("[(0; 1048575)]", CssUtils.parseUnicodeRange("U+?????").toString());
        assertEquals("[(38; 38)]", CssUtils.parseUnicodeRange("U+26").toString());
        assertEquals("[(0; 127)]", CssUtils.parseUnicodeRange(" U+0-7F").toString());
        assertEquals("[(37; 255)]", CssUtils.parseUnicodeRange("U+0025-00FF").toString());
        assertEquals("[(1024; 1279)]", CssUtils.parseUnicodeRange("U+4??").toString());
        assertEquals("[(262224; 327519)]", CssUtils.parseUnicodeRange("U+4??5?").toString());
        assertEquals("[(37; 255), (1024; 1279)]", CssUtils.parseUnicodeRange("U+0025-00FF, U+4??").toString());

        assertNull(CssUtils.parseUnicodeRange("U+??????")); // more than 5 question marks are not allowed
        assertNull(CssUtils.parseUnicodeRange("UU+7-10")); // wrong syntax
        assertNull(CssUtils.parseUnicodeRange("U+7?-9?")); // wrong syntax
        assertNull(CssUtils.parseUnicodeRange("U+7-")); // wrong syntax
    }

    @Test
    public void parseAbsoluteFontSizeTest() {
        assertEquals(75, CssUtils.parseAbsoluteFontSize("100", CommonCssConstants.PX), EPS);
        assertEquals(75, CssUtils.parseAbsoluteFontSize("100px"), EPS);
        assertEquals(12, CssUtils.parseAbsoluteFontSize(CommonCssConstants.MEDIUM), EPS);
        assertEquals(0, CssUtils.parseAbsoluteFontSize("", ""), EPS);
    }

    @Test
    public void parseRelativeFontSizeTest() {
        assertEquals(120, CssUtils.parseRelativeFontSize("10em", 12), EPS);
        assertEquals(12.5f, CssUtils.parseRelativeFontSize(CommonCssConstants.SMALLER, 15), EPS);
    }


    @Test
    public void parseAbsoluteLengthTest() {
        assertEquals(75, CssUtils.parseAbsoluteLength("100", CommonCssConstants.PX), EPS);
        assertEquals(75, CssUtils.parseAbsoluteLength("100px"), EPS);
    }

    @Test
    public void parseInvalidFloat() {
        String value = "invalidFloat";
        try {
            assertNull(CssUtils.parseFloat(value));
        } catch (Exception e){
            Assert.fail();
        }
    }
}
