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
package com.itextpdf.io.font.otf;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GlyphLineTest extends ExtendedITextTest {

    private static List<Glyph> constructGlyphListFromString(String text, TrueTypeFont font) {
        List<Glyph> glyphList = new ArrayList<>();
        char[] chars = text.toCharArray();
        for (char letter : chars) {
            glyphList.add(font.getGlyph(letter));
        }
        return glyphList;
    }

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

    @Test
    public void testOtherLinesAddition() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("Viva France!", font));

        GlyphLine childLine1 = new GlyphLine(constructGlyphListFromString(" Liberte", font));
        containerLine.add(childLine1);
        Assert.assertEquals(containerLine.end, 12);
        containerLine.end = 20;
        GlyphLine childLine2 = new GlyphLine(constructGlyphListFromString(" Egalite", font));
        containerLine.add(childLine2);
        Assert.assertEquals(containerLine.end, 20);
        containerLine.start = 10;
        GlyphLine childLine3 = new GlyphLine(constructGlyphListFromString(" Fraternite", font));
        containerLine.add(childLine3);
        Assert.assertEquals(containerLine.start, 10);
        containerLine.start = 0;
        containerLine.add(constructGlyphListFromString("!", font).get(0));
        containerLine.end = 40;
        Assert.assertEquals(containerLine.glyphs.size(), 40);
    }

    @Test
    public void testAdditionWithActualText() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        List<Glyph> glyphs = constructGlyphListFromString("Viva France!", font);

        GlyphLine containerLine = new GlyphLine(glyphs);
        Assert.assertNull(containerLine.actualText);

        containerLine.setActualText(0, 1, "TEST");
        Assert.assertNotNull(containerLine.actualText);
        Assert.assertEquals(12, containerLine.actualText.size());
        Assert.assertEquals("TEST", containerLine.actualText.get(0).value);

        containerLine.add(new GlyphLine(glyphs));
        Assert.assertEquals(24, containerLine.actualText.size());
        for (int i = 13; i < 24; i++) {
            Assert.assertNull(containerLine.actualText.get(i));
        }
    }

    @Test
    public void testOtherLinesWithActualTextAddition() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.end = 16;
        for (int i = 0; i < 9; i++) {
            Assert.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assert.assertEquals("Viva", containerLine.actualText.get(i).value);
        }
        Assert.assertEquals("France---Viva", containerLine.toString());
    }

    @Test
    public void testOtherLinesWithActualTextAddition02() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));
        containerLine.setActualText(1, 5, "id");

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.end = 16;
        Assert.assertNull(containerLine.actualText.get(0));
        for (int i = 1; i < 5; i++) {
            Assert.assertEquals("id", containerLine.actualText.get(i).value);
        }
        for (int i = 5; i < 9; i++) {
            Assert.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assert.assertEquals("Viva", containerLine.actualText.get(i).value);
        }
        Assert.assertEquals("Fide---Viva", containerLine.toString());
    }

    @Test
    public void testContentReplacingWithNullActualText() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine lineToBeReplaced = new GlyphLine(constructGlyphListFromString("Byelorussia", font));
        lineToBeReplaced.setActualText(1, 2, "e");

        GlyphLine lineToBeCopied = new GlyphLine(constructGlyphListFromString("Belarus", font));
        lineToBeReplaced.replaceContent(lineToBeCopied);

        // Test that no exception has been thrown. Also check the content.
        Assert.assertEquals("Belarus", lineToBeReplaced.toString());
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany01() throws IOException {
        String expectedActualTextForFirstGlyph = "0";
        String expectedActualTextForSecondGlyph = "A";

        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        // no actual text for the second glyph is set - it should be created during substitution
        GlyphLine line = new GlyphLine(constructGlyphListFromString("AA", font));
        line.setActualText(0, 1, expectedActualTextForFirstGlyph);
        line.idx = 1;

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assert.assertNotNull(line.actualText);
        Assert.assertEquals(3, line.actualText.size());
        Assert.assertSame(line.actualText.get(1), line.actualText.get(2));
        Assert.assertEquals(expectedActualTextForSecondGlyph, line.actualText.get(1).value);
        // check that it hasn't been corrupted
        Assert.assertEquals(expectedActualTextForFirstGlyph, line.actualText.get(0).value);
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany02() throws IOException {
        String expectedActualTextForFirstGlyph = "A";

        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        GlyphLine line = new GlyphLine(constructGlyphListFromString("A", font));
        line.setActualText(0, 1, expectedActualTextForFirstGlyph);

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assert.assertNotNull(line.actualText);
        Assert.assertEquals(2, line.actualText.size());
        Assert.assertSame(line.actualText.get(0), line.actualText.get(1));
        Assert.assertEquals(expectedActualTextForFirstGlyph, line.actualText.get(0).value);
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany03() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile("./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf"));
        TrueTypeFont font = new TrueTypeFont(ttf);

        // no actual text is set
        GlyphLine line = new GlyphLine(constructGlyphListFromString("A", font));

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assert.assertNull(line.actualText);
    }
}

