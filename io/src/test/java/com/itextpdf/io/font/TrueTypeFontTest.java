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
package com.itextpdf.io.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class TrueTypeFontTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/sharedFontsResourceFiles/";

    @Test
    public void notoSansJpCmapTest() throws IOException, InterruptedException {
        // 信
        char jpChar = '\u4FE1';

        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "NotoSansJP-Regular_charsetDataFormat0.otf");
        Glyph glyph = fontProgram.getGlyph(jpChar);

        Assertions.assertArrayEquals(new char[] {jpChar}, glyph.getUnicodeChars());
        Assertions.assertEquals(20449, glyph.getUnicode());
        Assertions.assertEquals(10195, glyph.getCode());
    }

    @Test
    public void notoSansScCmapTest() throws IOException {
        // 易
        char chChar = '\u6613';

        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "NotoSansSC-Regular.otf");
        Glyph glyph = fontProgram.getGlyph(chChar);

        Assertions.assertArrayEquals(new char[] {chChar}, glyph.getUnicodeChars());
        Assertions.assertEquals(26131, glyph.getUnicode());
        Assertions.assertEquals(20292, glyph.getCode());
    }

    @Test
    public void notoSansTcCmapTest() throws IOException {
        // 易
        char chChar = '\u6613';

        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "NotoSansTC-Regular.otf");
        Glyph glyph = fontProgram.getGlyph(chChar);

        Assertions.assertArrayEquals(new char[] {chChar}, glyph.getUnicodeChars());
        Assertions.assertEquals(26131, glyph.getUnicode());
        Assertions.assertEquals(20292, glyph.getCode());
    }

    @Test
    public void notoSansScMapGlyphsCidsToGidsTest() throws IOException {
        // 易
        char chChar = '\u6613';
        int charCidInFont = 20292;
        int charGidInFont = 14890;

        TrueTypeFont trueTypeFontProgram = (TrueTypeFont) FontProgramFactory.createFont(SOURCE_FOLDER + "NotoSansSC-Regular.otf");

        HashSet<Integer> glyphs = new HashSet<>(Collections.singletonList(charCidInFont));
        Set<Integer> actualResult = trueTypeFontProgram.mapGlyphsCidsToGids(glyphs);

        Assertions.assertEquals(1, actualResult.size());
        Assertions.assertTrue(actualResult.contains(charGidInFont));
    }

    @Test
    public void cmapPlatform0PlatEnc3Format4Test() throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "glyphs.ttf");
        checkCmapTableEntry(fontProgram, 'f', 2);
        checkCmapTableEntry(fontProgram, 'i', 3);
    }

    @Test
    public void cmapPlatform0PlatEnc3Format6Test() throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "glyphs-fmt-6.ttf");
        checkCmapTableEntry(fontProgram, 'f', 2);
        checkCmapTableEntry(fontProgram, 'i', 3);
    }

    @Test
    public void checkSxHeightTtfTest() throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(SOURCE_FOLDER + "glyphs-fmt-6.ttf");
        FontMetrics metrics = fontProgram.getFontMetrics();
        int xHeight = metrics.getXHeight();
        Assertions.assertEquals(536, xHeight);
    }

    @Test
    public void containsCmapTest() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(SOURCE_FOLDER + "glyphs-fmt-6.ttf");
        Assertions.assertEquals(1, fontProgram.getNumberOfCmaps());
        Assertions.assertTrue(fontProgram.isCmapPresent(0, 3));
        Assertions.assertFalse(fontProgram.isCmapPresent(1, 0));
    }

    private void checkCmapTableEntry(FontProgram fontProgram, char uniChar, int expectedGlyphId) {

        Glyph glyph = fontProgram.getGlyph(uniChar);

        Assertions.assertEquals(expectedGlyphId, glyph.getCode());
        Assertions.assertArrayEquals(new char[]{uniChar}, glyph.getUnicodeChars());
    }
}
