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