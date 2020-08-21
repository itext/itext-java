/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.font;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Type3FontTest extends ExtendedITextTest {

    @Test
    public void addGlyphTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);

        Assert.assertEquals(1, font.getNumberOfGlyphs());
    }

    @Test
    public void addGlyphsWithDifferentUnicodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);
        font.addGlyph(2, 2, 600, null, null);

        Assert.assertEquals(2, font.getNumberOfGlyphs());
        Assert.assertEquals(1, font.getGlyphByCode(1).getUnicode());
        Assert.assertEquals(2, font.getGlyphByCode(2).getUnicode());
    }

    @Test
    public void addGlyphsWithDifferentCodesTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(2, -1, 700, null, null);

        Assert.assertEquals(2, font.getNumberOfGlyphs());
        Assert.assertEquals(600, font.getGlyphByCode(1).getWidth());
        Assert.assertEquals(700, font.getGlyphByCode(2).getWidth());
    }

    @Test
    public void replaceGlyphsWithSameUnicodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 1, 600, null, null);
        font.addGlyph(2, 1, 600, null, null);

        Assert.assertEquals(1, font.getNumberOfGlyphs());
        Assert.assertEquals(2, font.getGlyph(1).getCode());
    }

    @Test
    public void replaceGlyphWithSameCodeTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(1, -1, 700, null, null);

        Assert.assertEquals(1, font.getNumberOfGlyphs());
        Assert.assertEquals(700, font.getGlyphByCode(1).getWidth());
    }

    @Test
    public void notAddGlyphWithSameCodeEmptyUnicodeFirstTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, -1, 600, null, null);
        font.addGlyph(1, 100, 600, null, null);

        Assert.assertEquals(1, font.getNumberOfGlyphs());
        Assert.assertEquals(1, font.getGlyph(100).getCode());
        Assert.assertEquals(100, font.getGlyphByCode(1).getUnicode());
    }

    @Test
    public void replaceGlyphWithSameCodeEmptyUnicodeLastTest() {
        Type3Font font = new Type3Font(false);

        font.addGlyph(1, 100, 600, null, null);
        font.addGlyph(1, -1, 600, null, null);

        Assert.assertNull(font.getGlyph(-1));
        Assert.assertNull(font.getGlyph(100));
        Assert.assertEquals(1, font.getNumberOfGlyphs());
        Assert.assertEquals(-1, font.getGlyphByCode(1).getUnicode());
    }
}
