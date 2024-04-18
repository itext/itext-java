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
package com.itextpdf.io.util;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextUtilTest extends ExtendedITextTest {

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

    @Test
    public void isLetterPositiveTest() {
        Glyph glyph = new Glyph(0, 0, 'a');
        Assert.assertTrue(TextUtil.isLetterOrDigit(glyph));
    }

    @Test
    public void isDigitPositiveTest() {
        Glyph glyph = new Glyph(0, 0, '8');
        Assert.assertTrue(TextUtil.isLetterOrDigit(glyph));
    }

    @Test
    public void isLetterOrDigitNegativeTest() {
        Glyph glyph = new Glyph(0, 0, '-');
        Assert.assertFalse(TextUtil.isLetterOrDigit(glyph));
    }

    @Test
    public void isMarkPositiveTest() {
        // TAI THAM SIGN KHUEN TONE-3
        Glyph glyph = new Glyph(0, 0, 0x1A77);
        Assert.assertTrue(TextUtil.isMark(glyph));
    }

    @Test
    public void isMarkNegativeTest() {
        Glyph glyph = new Glyph(0, 0, '-');
        Assert.assertFalse(TextUtil.isMark(glyph));
    }

    @Test
    public void isDiacriticTest() {
        Assert.assertTrue(TextUtil.isDiacritic("\u0303".charAt(0)));
        Assert.assertFalse(TextUtil.isDiacritic("\u006b".charAt(0)));
    }

    private void helper(boolean expected, int currentCRPosition, Glyph...glyphs) {
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(glyphs));
        Assert.assertTrue(expected == TextUtil.isCarriageReturnFollowedByLineFeed(glyphLine, currentCRPosition));
    }
}
