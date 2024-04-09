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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.RangeBuilder;

import java.util.ArrayList;
import java.util.List;

public class FontSelectorTestsUtil {
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    public static IFontSelectorStrategy createStrategyWithFreeSansAndTNR(IFontSelectorStrategyFactory factory) {
        FontSet fs = new FontSet();
        fs.addFont(StandardFonts.TIMES_ROMAN);
        fs.addFont(FONTS_FOLDER + "FreeSans.ttf");
        final FontProvider fontProvider = new FontProvider(fs);
        fontProvider.setFontSelectorStrategyFactory(factory);
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("random");
        return fontProvider.createFontSelectorStrategy(fontFamilies, new FontCharacteristics(), null);
    }

    public static IFontSelectorStrategy createStrategyWithTNR(IFontSelectorStrategyFactory factory) {
        FontSet fs = new FontSet();
        fs.addFont(StandardFonts.TIMES_ROMAN);
        final FontProvider fontProvider = new FontProvider(fs);
        fontProvider.setFontSelectorStrategyFactory(factory);
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("random");
        return fontProvider.createFontSelectorStrategy(fontFamilies, new FontCharacteristics(), null);
    }

    public static IFontSelectorStrategy createStrategyWithFreeSans(IFontSelectorStrategyFactory factory) {
        FontSet fs = new FontSet();
        fs.addFont(FONTS_FOLDER + "FreeSans.ttf");
        final FontProvider fontProvider = new FontProvider(fs);
        fontProvider.setFontSelectorStrategyFactory(factory);
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("random");
        return fontProvider.createFontSelectorStrategy(fontFamilies, new FontCharacteristics(), null);
    }

    public static IFontSelectorStrategy createStrategyWithLimitedThreeFonts(IFontSelectorStrategyFactory factory) {
        final FontProvider fontProvider = new FontProvider();

        // 'a', 'b' and 'c' are in that interval
        fontProvider.getFontSet().addFont(FONTS_FOLDER + "NotoSansCJKjp-Bold.otf", null, "FontAlias", new RangeBuilder(97, 99).create());
        // 'd', 'e' and 'f' are in that interval
        fontProvider.getFontSet().addFont(FONTS_FOLDER + "FreeSans.ttf", null, "FontAlias", new RangeBuilder(100, 102).create());
        // 'x', 'y' and 'z' are in that interval
        fontProvider.getFontSet().addFont(FONTS_FOLDER + "Puritan2.otf", null, "FontAlias", new RangeBuilder(120, 122).create());

        fontProvider.setFontSelectorStrategyFactory(factory);
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("random");
        return fontProvider.createFontSelectorStrategy(fontFamilies, new FontCharacteristics(), null);
    }

    public static IFontSelectorStrategy createStrategyWithOldItalic(IFontSelectorStrategyFactory factory) {
        final FontProvider fontProvider = new FontProvider();

        fontProvider.getFontSet().addFont(FONTS_FOLDER + "NotoSansOldItalic-Regular.ttf", null, "FontAlias");
        fontProvider.getFontSet().addFont(FONTS_FOLDER + "FreeSans.ttf", null, "FontAlias");

        fontProvider.setFontSelectorStrategyFactory(factory);
        List<String> fontFamilies = new ArrayList<>();
        fontFamilies.add("random");
        return fontProvider.createFontSelectorStrategy(fontFamilies, new FontCharacteristics(), null);
    }
}
