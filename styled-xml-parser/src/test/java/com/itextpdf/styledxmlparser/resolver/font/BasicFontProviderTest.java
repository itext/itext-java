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
package com.itextpdf.styledxmlparser.resolver.font;

import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@Tag("UnitTest")
public class BasicFontProviderTest {

    @Test
    public void basicTest() {
        FontProvider fontProvider = new BasicFontProvider();
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Helvetica"),
                                                                 new FontCharacteristics(), null);
        FontInfo selectedFont = fontSelector.bestMatch();
        Assertions.assertEquals("Helvetica", selectedFont.getFontName());
    }

    @Test
    public void basicTest2() {
        FontProvider fontProvider = new BasicFontProvider(true, true, false);
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Symbol"),
                new FontCharacteristics(), null);
        FontInfo selectedFont = fontSelector.bestMatch();
        Assertions.assertEquals("Symbol", selectedFont.getFontName());
    }

    @Test
    public void basicTest3() {
        FontProvider fontProvider = new BasicFontProvider(new FontSet(), StandardFontFamilies.TIMES);
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Times"),
                new FontCharacteristics(), null);
        Assertions.assertThrows(Exception.class, () -> fontSelector.bestMatch());
    }

    @Test
    public void shippedFontTest() {
        FontProvider fontProvider = new TestFontProvider();
        //Not checking shipped fonts since that's an empty file in this test,
        //so instead just trying to get "Courier" font to check that nothing breaks
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Courier"),
                new FontCharacteristics(), null);
        FontInfo selectedFont = fontSelector.bestMatch();
        Assertions.assertEquals("Courier", selectedFont.getFontName());
    }

    @Test
    public void systemFontTest() {
        FontProvider fontProvider = new BasicFontProvider(true, true, true);
        //Not checking system fonts since based on a system running those can be different,
        //so instead just trying to get "Courier" font to check that nothing breaks
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Courier"),
                new FontCharacteristics(), null);
        FontInfo selectedFont = fontSelector.bestMatch();
        Assertions.assertEquals("Courier", selectedFont.getFontName());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.ERROR_LOADING_FONT))
    public void invalidShippedFontTest() {
        FontProvider fontProvider = new InvalidTestFontProvider();
        FontSelector fontSelector = fontProvider.getFontSelector(Collections.singletonList("Courier"),
                new FontCharacteristics(), null);
        FontInfo selectedFont = fontSelector.bestMatch();
        Assertions.assertEquals("Courier", selectedFont.getFontName());
    }

    private static class TestFontProvider extends BasicFontProvider {
        public TestFontProvider() {
            super();
        }

        @Override
        protected void initShippedFontsResourcePath() {
            //This file is empty since real fonts are pretty large files, we're just checking that addShippedFonts is
            //getting called and doesn't throw log message
            shippedFontResourcePath = "com/itextpdf/styledxmlparser/resolver/font/";
            shippedFontNames = new ArrayList<>();
            shippedFontNames.add("test.ttf");
        }
    }

    private static class InvalidTestFontProvider extends BasicFontProvider {
        public InvalidTestFontProvider() {
            super();
        }

        @Override
        protected void initShippedFontsResourcePath() {
            //This file is empty since real fonts are pretty large files, we're just checking that addShippedFonts is
            //getting called and doesn't throw log message
            shippedFontResourcePath = "com/itextpdf/styledxmlparser/resolver/font/";
            shippedFontNames = new ArrayList<>();
            shippedFontNames.add("noSuchFile.ttf");
        }
    }
}
