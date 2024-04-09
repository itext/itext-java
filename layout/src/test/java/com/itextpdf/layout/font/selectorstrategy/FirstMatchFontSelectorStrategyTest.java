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
package com.itextpdf.layout.font.selectorstrategy;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.selectorstrategy.FirstMatchFontSelectorStrategy.FirstMathFontSelectorStrategyFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FirstMatchFontSelectorStrategyTest extends ExtendedITextTest {
    @Test
    public void twoDiacriticsInRowTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSansAndTNR(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0301\u0302 abc");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("L with accent: ", result.get(0).getFirst().toString());
        Assert.assertEquals("\u004f\u0301\u0302 abc", result.get(1).getFirst().toString());
    }

    @Test
    public void oneDiacriticTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSansAndTNR(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("L with accent: ", result.get(0).getFirst().toString());
        Assert.assertEquals("\u004f\u0302 abc", result.get(1).getFirst().toString());
        Assert.assertNotEquals(result.get(0).getSecond(), result.get(1).getSecond());
    }

    @Test
    public void oneDiacriticWithUnsupportedFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithTNR(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("L with accent: \u004f", result.get(0).getFirst().toString());
        Assert.assertEquals("", result.get(1).getFirst().toString());
        Assert.assertEquals(" abc", result.get(2).getFirst().toString());
        Assert.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
        Assert.assertEquals(result.get(0).getSecond(), result.get(1).getSecond());
    }

    @Test
    public void oneDiacriticWithOneSupportedFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithFreeSans(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "L with accent: \u004f\u0302 abc");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("L with accent: \u004f\u0302 abc", result.get(0).getFirst().toString());
    }

    @Test
    public void surrogatePairsTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithOldItalic(new FirstMathFontSelectorStrategyFactory());

        // this text contains three successive surrogate pairs
        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(
                "text \uD800\uDF10\uD800\uDF00\uD800\uDF11 text");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("text ", result.get(0).getFirst().toString());
        Assert.assertEquals("\uD800\uDF10\uD800\uDF00\uD800\uDF11 ", result.get(1).getFirst().toString());
        Assert.assertEquals("text", result.get(2).getFirst().toString());
        Assert.assertEquals(result.get(0).getSecond(), result.get(2).getSecond());
    }

    @Test
    public void simpleThreeFontTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithLimitedThreeFonts(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines("abcdefxyz");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("abcdefxyz", result.get(0).getFirst().toString());
    }

    @Test
    public void threeFontWithSpacesTest() {
        IFontSelectorStrategy strategy = FontSelectorTestsUtil.createStrategyWithLimitedThreeFonts(new FirstMathFontSelectorStrategyFactory());

        final List<Tuple2<GlyphLine, PdfFont>> result = strategy.getGlyphLines(" axadefa ");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(" axadefa ", result.get(0).getFirst().toString());
    }
}
