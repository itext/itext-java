/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GlyphLineTest extends ExtendedITextTest {

    public static final String FREESANS_FONT_PATH = "./src/test/resources/com/itextpdf/io/font/otf/FreeSans.ttf";

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

        one.setEnd(one.getEnd() +1);
        two.setEnd(two.getEnd() +1);

        Assertions.assertTrue(one.equals(two));
    }

    @Test
    public void testOtherLinesAddition() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("Viva France!", font));

        GlyphLine childLine1 = new GlyphLine(constructGlyphListFromString(" Liberte", font));
        containerLine.add(childLine1);
        Assertions.assertEquals(12, containerLine.getEnd());
        containerLine.setEnd(20);
        GlyphLine childLine2 = new GlyphLine(constructGlyphListFromString(" Egalite", font));
        containerLine.add(childLine2);
        Assertions.assertEquals(20, containerLine.getEnd());
        containerLine.setStart(10);
        GlyphLine childLine3 = new GlyphLine(constructGlyphListFromString(" Fraternite", font));
        containerLine.add(childLine3);
        Assertions.assertEquals(10, containerLine.getStart());
        containerLine.setStart(0);
        containerLine.add(constructGlyphListFromString("!", font).get(0));
        containerLine.setEnd(40);
        Assertions.assertEquals(40, containerLine.glyphs.size());
    }

    @Test
    public void testAdditionWithActualText() throws IOException {
        TrueTypeFont font = initializeFont();

        List<Glyph> glyphs = constructGlyphListFromString("Viva France!", font);

        GlyphLine containerLine = new GlyphLine(glyphs);
        Assertions.assertNull(containerLine.actualText);

        containerLine.setActualText(0, 1, "TEST");
        Assertions.assertNotNull(containerLine.actualText);
        Assertions.assertEquals(12, containerLine.actualText.size());
        Assertions.assertEquals("TEST", containerLine.actualText.get(0).getValue());

        containerLine.add(new GlyphLine(glyphs));
        Assertions.assertEquals(24, containerLine.actualText.size());
        for (int i = 13; i < 24; i++) {
            Assertions.assertNull(containerLine.actualText.get(i));
        }
    }

    @Test
    public void testOtherLinesWithActualTextAddition() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.setEnd(16);
        for (int i = 0; i < 9; i++) {
            Assertions.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assertions.assertEquals("Viva", containerLine.actualText.get(i).getValue());
        }
        Assertions.assertEquals("France---Viva", containerLine.toString());
    }

    @Test
    public void testOtherLinesWithActualTextAddition02() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine containerLine = new GlyphLine(constructGlyphListFromString("France", font));
        containerLine.setActualText(1, 5, "id");

        GlyphLine childLine = new GlyphLine(constructGlyphListFromString("---Liberte", font));
        childLine.setActualText(3, 10, "Viva");

        containerLine.add(childLine);
        containerLine.setEnd(16);
        Assertions.assertNull(containerLine.actualText.get(0));
        for (int i = 1; i < 5; i++) {
            Assertions.assertEquals("id", containerLine.actualText.get(i).getValue());
        }
        for (int i = 5; i < 9; i++) {
            Assertions.assertNull(containerLine.actualText.get(i));
        }
        for (int i = 9; i < 16; i++) {
            Assertions.assertEquals("Viva", containerLine.actualText.get(i).getValue());
        }
        Assertions.assertEquals("Fide---Viva", containerLine.toString());
    }

    @Test
    public void testContentReplacingWithNullActualText() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine lineToBeReplaced = new GlyphLine(constructGlyphListFromString("Byelorussia", font));
        lineToBeReplaced.setActualText(1, 2, "e");

        GlyphLine lineToBeCopied = new GlyphLine(constructGlyphListFromString("Belarus", font));
        lineToBeReplaced.replaceContent(lineToBeCopied);

        // Test that no exception has been thrown. Also check the content.
        Assertions.assertEquals("Belarus", lineToBeReplaced.toString());
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany01() throws IOException {
        String expectedActualTextForFirstGlyph = "0";
        String expectedActualTextForSecondGlyph = "A";

        TrueTypeFont font = initializeFont();

        // no actual text for the second glyph is set - it should be created during substitution
        GlyphLine line = new GlyphLine(constructGlyphListFromString("AA", font));
        line.setActualText(0, 1, expectedActualTextForFirstGlyph);
        line.setIdx(1);

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assertions.assertNotNull(line.actualText);
        Assertions.assertEquals(3, line.actualText.size());
        Assertions.assertSame(line.actualText.get(1), line.actualText.get(2));
        Assertions.assertEquals(expectedActualTextForSecondGlyph, line.actualText.get(1).getValue());
        // check that it hasn't been corrupted
        Assertions.assertEquals(expectedActualTextForFirstGlyph, line.actualText.get(0).getValue());
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany02() throws IOException {
        String expectedActualTextForFirstGlyph = "A";

        TrueTypeFont font = initializeFont();

        GlyphLine line = new GlyphLine(constructGlyphListFromString("A", font));
        line.setActualText(0, 1, expectedActualTextForFirstGlyph);

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assertions.assertNotNull(line.actualText);
        Assertions.assertEquals(2, line.actualText.size());
        Assertions.assertSame(line.actualText.get(0), line.actualText.get(1));
        Assertions.assertEquals(expectedActualTextForFirstGlyph, line.actualText.get(0).getValue());
    }

    @Test
    public void testActualTextForSubstitutedGlyphProcessingInSubstituteOneToMany03() throws IOException {
        TrueTypeFont font = initializeFont();

        // no actual text is set
        GlyphLine line = new GlyphLine(constructGlyphListFromString("A", font));

        line.substituteOneToMany(font.getGsubTable(), new int[] {39, 40});

        Assertions.assertNull(line.actualText);
    }

    @Test
    public void defaultConstructorTest() {
        GlyphLine glyphLine = new GlyphLine();
        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(0, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
    }

    @Test
    public void otherConstructorTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine otherLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        GlyphLine glyphLine = new GlyphLine(otherLine);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(16, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("A test otherLine", glyphLine.toString());
    }

    @Test
    public void startEndConstructorTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine otherLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        GlyphLine glyphLine = new GlyphLine(otherLine, 2, 16);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(14, glyphLine.getEnd());
        Assertions.assertEquals(-2, glyphLine.getIdx());
        Assertions.assertEquals("test otherLine", glyphLine.toString());
    }

    @Test
    public void startEndAndActualTextTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("XXX otherLine", font));
        glyphLine.setActualText(0, 3, "txt");

        GlyphLine other = new GlyphLine(glyphLine, 0, 13);

        Assertions.assertEquals("txt otherLine", other.toString());
    }

    @Test
    public void copyGlyphLineTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        GlyphLine copyLine = glyphLine.copy(2, 6);

        Assertions.assertEquals(0, copyLine.getStart());
        Assertions.assertEquals(4, copyLine.getEnd());
        Assertions.assertEquals(0, copyLine.getIdx());
        Assertions.assertEquals("test", copyLine.toString());
    }

    @Test
    public void copyWithActualTextGlyphLineTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("XXX otherLine", font));
        glyphLine.setActualText(0, 3, "txt");
        GlyphLine copyLine = glyphLine.copy(0, 3);

        Assertions.assertEquals(0, copyLine.getStart());
        Assertions.assertEquals(3, copyLine.getEnd());
        Assertions.assertEquals(0, copyLine.getIdx());
        Assertions.assertEquals("txt", copyLine.toString());
    }

    @Test
    public void addIndexedGlyphLineTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        Glyph glyph = new Glyph(200, 200, 200);
        glyphLine.add(0, glyph);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(16, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("ÈA test otherLin", glyphLine.toString());
    }

    @Test
    public void addIndexedGlyphLineActualTextTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("XXX otherLine", font));
        glyphLine.setActualText(0, 3, "txt");
        Glyph glyph = new Glyph(200, 200, 200);
        glyphLine.add(0, glyph);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(13, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("Ètxt otherLin", glyphLine.toString());
    }

    @Test
    public void replaceGlyphInLineTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        Glyph glyph = new Glyph(200, 200, 200);
        glyphLine.set(0, glyph);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(16, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("È test otherLine", glyphLine.toString());
    }

    @Test
    public void replaceGlyphLineNoActualTextTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));

        GlyphLine replaceLine = new GlyphLine(constructGlyphListFromString("different text", font));
        replaceLine.setActualText(0, 14, "different text");
        glyphLine.replaceContent(replaceLine);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(14, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("different text", glyphLine.toString());
    }

    @Test
    public void replaceGlyphLineWithActualTextTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        glyphLine.setActualText(0, 14, "A test otherLine");

        GlyphLine replaceLine = new GlyphLine(constructGlyphListFromString("different text", font));
        replaceLine.setActualText(0, 14, "different text");
        glyphLine.replaceContent(replaceLine);

        Assertions.assertEquals(0, glyphLine.getStart());
        Assertions.assertEquals(14, glyphLine.getEnd());
        Assertions.assertEquals(0, glyphLine.getIdx());
        Assertions.assertEquals("different text", glyphLine.toString());
    }

    @Test
    public void nullEqualsTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        boolean equals = glyphLine.equals(null);
        Assertions.assertFalse(equals);
    }

    @Test
    public void equalsItselfTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine glyphLine = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        boolean equals = glyphLine.equals(glyphLine);
        Assertions.assertTrue(equals);
    }


    @Test
    public void equalGlyphLinesTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine first = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        first.setActualText(0, 14, "A test otherLine");
        GlyphLine second = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        second.setActualText(0, 14, "A test otherLine");
        boolean equals = first.equals(second);
        Assertions.assertTrue(equals);
    }

    @Test
    public void diffStartEndEqualsTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine first = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        GlyphLine second = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        second.setEnd(3);
        second.setStart(1);
        boolean equals = first.equals(second);
        Assertions.assertFalse(equals);
    }

    @Test
    public void diffActualTextEqualsTest() throws IOException {
        TrueTypeFont font = initializeFont();

        GlyphLine first = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        first.setActualText(0, 3, "txt");
        GlyphLine second = new GlyphLine(constructGlyphListFromString("A test otherLine", font));
        boolean equals = first.equals(second);
        Assertions.assertFalse(equals);
    }

    private TrueTypeFont initializeFont() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(FREESANS_FONT_PATH));
        return new TrueTypeFont(ttf);
    }
}

