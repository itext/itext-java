package com.itextpdf.io.font.otf;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;

@Category(UnitTest.class)
public class GlyphLineTest {

    @Test
    public void testEquals() {
        Glyph glyph = new Glyph(200, 200, 200);
        GlyphLine.ActualText actualText = new GlyphLine.ActualText("-");

        GlyphLine one = new GlyphLine(new ArrayList<Glyph>(Arrays.asList(glyph)), new ArrayList<GlyphLine.ActualText>(Arrays.asList(actualText)), 0, 1);
        GlyphLine two = new GlyphLine(new ArrayList<Glyph>(Arrays.asList(glyph)), new ArrayList<GlyphLine.ActualText>(Arrays.asList(actualText)), 0, 1);

        one.add(glyph);
        two.add(glyph);

        one.end++;
        two.end++;

        Assert.assertTrue(one.equals(two));
    }

}
