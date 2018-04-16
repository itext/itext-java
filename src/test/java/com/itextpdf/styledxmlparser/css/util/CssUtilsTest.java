/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.styledxmlparser.css.CssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CssUtilsTest {

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
        float actual = CssUtils.parseAbsoluteLength(value, CssConstants.PX);
        float expected = 7.5f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10cm() {
        String value = "10cm";
        float actual = CssUtils.parseAbsoluteLength(value, CssConstants.CM);
        float expected = 283.46457f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10in() {
        String value = "10in";
        float actual = CssUtils.parseAbsoluteLength(value, CssConstants.IN);
        float expected = 720.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10pt() {
        String value = "10pt";
        float actual = CssUtils.parseAbsoluteLength(value, CssConstants.PT);
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFromUnknownType() {
        String value = "10pateekes";
        float actual = CssUtils.parseAbsoluteLength(value, "pateekes");
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }
}