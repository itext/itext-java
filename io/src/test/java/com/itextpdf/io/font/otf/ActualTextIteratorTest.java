package com.itextpdf.io.font.otf;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

@Category(UnitTest.class)
public class ActualTextIteratorTest {

    @Test
    public void testActualTestParts() {
        Glyph glyph = new Glyph(200, 200, '\u002d');
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(glyph));
        glyphLine.setActualText(0, 1, "\u002d");
        ActualTextIterator actualTextIterator = new ActualTextIterator(glyphLine);
        GlyphLine.GlyphLinePart part = actualTextIterator.next();
        // When actual text is the same as the result by text extraction, we should omit redundant actual text in the content stream
        Assert.assertNull(part.actualText);
    }

}
