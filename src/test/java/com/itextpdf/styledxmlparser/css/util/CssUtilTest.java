/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class CssUtilTest extends ExtendedITextTest {

    @Test
    public void validateMetricValue() {
        assertEquals(true, CssUtils.isMetricValue("1px"));
        assertEquals(true, CssUtils.isMetricValue("1in"));
        assertEquals(true, CssUtils.isMetricValue("1cm"));
        assertEquals(true, CssUtils.isMetricValue("1mm"));
        assertEquals(true, CssUtils.isMetricValue("1pc"));
        assertEquals(false, CssUtils.isMetricValue("1em"));
        assertEquals(false, CssUtils.isMetricValue("1rem"));
        assertEquals(false,CssUtils.isMetricValue("1ex"));
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
}
