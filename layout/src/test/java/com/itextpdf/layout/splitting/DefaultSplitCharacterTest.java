package com.itextpdf.layout.splitting;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.test.annotations.type.UnitTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSplitCharacterTest {
    static List<Glyph> glyphs = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '2'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '5'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '7'));
        glyphs.add(new Glyph(1, '-'));

        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '7'));

        glyphs.add(new Glyph(1, '5'));
        glyphs.add(new Glyph(1, '-'));
        glyphs.add(new Glyph(1, '-'));
    }

    @Test
    public void beginCharacterTest() {
        Assert.assertFalse(isPsplitCharacter(0));
    }

    @Test
    public void middleCharacterTest() {
        Assert.assertTrue(isPsplitCharacter(4));
    }

    @Test
    public void lastCharacterTest() {
        Assert.assertTrue(isPsplitCharacter(8));
    }

    @Test
    public void firstMiddleCharacterTest() {
        Assert.assertTrue(isPsplitCharacter(9));
    }

    @Test
    public void lastMiddleCharacterTest() {
        Assert.assertTrue(isPsplitCharacter(14));
    }

    private static boolean isPsplitCharacter(int glyphPos) {
        GlyphLine text = new GlyphLine(glyphs);
        return new DefaultSplitCharacters().isSplitCharacter(text, glyphPos);
    }
}
