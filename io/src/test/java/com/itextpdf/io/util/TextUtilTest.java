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
package com.itextpdf.io.util;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextUtilTest {

    private Glyph carriageReturn;
    private Glyph lineFeed;

    @Before
    public void before() {
        this.carriageReturn = new Glyph(0, 0, '\r');
        this.lineFeed = new Glyph(0, 0, '\n');
    }

    @Test
    public void carriageReturnFollowedByLineFeedTest() {
        helper(true, 0,
                carriageReturn, lineFeed);
    }

    @Test
    public void carriageReturnFollowedByCarriageReturnAndThenLineFeedTest() {
        helper(false, 0,
                carriageReturn, carriageReturn, lineFeed);
    }

    @Test
    public void carriageReturnPrecededByCarriageReturnAndFollowedByLineFeedTest() {
        helper(true, 1,
                carriageReturn, carriageReturn, lineFeed);
    }

    @Test
    public void carriageReturnFollowedByNothingTest() {
        helper(false, 0,
                carriageReturn);
    }

    @Test
    public void carriageReturnPrecededByLineFeedTest() {
        helper(false, 0,
                lineFeed, carriageReturn);
    }

    @Test
    public void carriageReturnPrecededByTextFollowedByLineFeedTest() {
        helper(true, 1,
                new Glyph(0,0, 'a'), carriageReturn, lineFeed);
    }

    private void helper(boolean expected, int currentCRPosition, Glyph...glyphs) {
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(glyphs));
        Assert.assertTrue(expected == TextUtil.isCarriageReturnFollowedByLineFeed(glyphLine, currentCRPosition));
    }
}
