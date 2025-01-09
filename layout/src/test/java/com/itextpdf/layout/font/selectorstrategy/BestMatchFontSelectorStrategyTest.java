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
package com.itextpdf.layout.font.selectorstrategy;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.selectorstrategy.BestMatchFontSelectorStrategy.BestMatchFontSelectorStrategyFactory;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BestMatchFontSelectorStrategyTest extends ExtendedITextTest {
    @Test
    public void twoDiacriticsInRowTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSansAndTNR(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0301\u0302 abc");
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("L with accent: ", result.get(0).getFirst().toString());
        Assertions.assertEquals("\u004f\u0301\u0302 ", result.get(1).getFirst().toString());
        Assertions.assertEquals("abc", result.get(2).getFirst().toString());
        // Diacritics and symbol were separated, but the font is the same
        Assertions.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
    }

    @Test
    public void oneDiacriticTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSansAndTNR(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("L with accent: ", result.get(0).getFirst().toString());
        Assertions.assertEquals("\u004f\u0302 ", result.get(1).getFirst().toString());
        Assertions.assertEquals("abc", result.get(2).getFirst().toString());
        Assertions.assertNotEquals(result.get(0).getSecond(), result.get(1).getSecond());
    }

    @Test
    public void oneDiacriticWithUnsupportedFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithTNR(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("L with accent: \u004f", result.get(0).getFirst().toString());
        Assertions.assertEquals("", result.get(1).getFirst().toString());
        Assertions.assertEquals(" abc", result.get(2).getFirst().toString());
        Assertions.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
        Assertions.assertEquals(result.get(0).getSecond(), result.get(1).getSecond());
    }

    @Test
    public void diacriticFontDoesnotContainPreviousSymbolTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithNotoSans(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "Ми\u0301ръ (mírə)");
        Assertions.assertEquals(6, result.size());
        Assertions.assertEquals("Ми", result.get(0).getFirst().toString());
        Assertions.assertEquals("\u0301", result.get(1).getFirst().toString());
        Assertions.assertEquals("ръ (", result.get(2).getFirst().toString());
        Assertions.assertEquals("mír", result.get(3).getFirst().toString());
        Assertions.assertEquals("ə", result.get(4).getFirst().toString());
        Assertions.assertEquals(")", result.get(5).getFirst().toString());
        Assertions.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
        Assertions.assertEquals(result.get(2).getSecond(), result.get(3).getSecond());
    }


    @Test
    public void oneDiacriticWithOneSupportedFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSans(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("L with accent: \u004f\u0302 abc", result.get(0).getFirst().toString());
    }

    @Test
    public void surrogatePairsTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithOldItalic(new BestMatchFontSelectorStrategyFactory());

        // this text contains three successive surrogate pairs
        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "text \uD800\uDF10\uD800\uDF00\uD800\uDF11 text");
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("text ", result.get(0).getFirst().toString());
        Assertions.assertEquals("\uD800\uDF10\uD800\uDF00\uD800\uDF11 ", result.get(1).getFirst().toString());
        Assertions.assertEquals("text", result.get(2).getFirst().toString());
        Assertions.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
    }

    @Test
    public void simpleThreeFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithLimitedThreeFonts(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines("abcdefxyz");
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("abc", result.get(0).getFirst().toString());
        Assertions.assertEquals("def", result.get(1).getFirst().toString());
        Assertions.assertEquals("xyz", result.get(2).getFirst().toString());
    }

    @Test
    public void threeFontWithSpacesTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithLimitedThreeFonts(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(" axadefa ");
        Assertions.assertEquals(5, result.size());
        Assertions.assertEquals(" a", result.get(0).getFirst().toString());
        Assertions.assertEquals("x", result.get(1).getFirst().toString());
        Assertions.assertEquals("a", result.get(2).getFirst().toString());
        Assertions.assertEquals("def", result.get(3).getFirst().toString());
        Assertions.assertEquals("a ", result.get(4).getFirst().toString());
    }

    @Test
    public void windowsLineEndingsTest() {
        IFontSelectorStrategy strategy =
                FontSelectorTestsUtil.createStrategyWithFreeSans(new BestMatchFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines("Hello\r\n   World!\r\n ");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Hello\r\n   World!\r\n ", result.get(0).getFirst().toString());
    }
}
