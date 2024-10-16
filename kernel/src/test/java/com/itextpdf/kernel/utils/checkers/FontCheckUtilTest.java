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
package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.utils.checkers.FontCheckUtil.CharacterChecker;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


@Tag("UnitTest")
public class FontCheckUtilTest extends ExtendedITextTest {
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";

    @Test
    public void checkFontAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Assertions.assertEquals(-1, FontCheckUtil.checkGlyphsOfText("123", font, new CharacterChecker() {
            @Override
            public boolean check(int ch, PdfFont fontToCheck) {
                return !fontToCheck.containsGlyph(ch);
            }
        }));
    }


    @Test
    public void checkFontNotAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Assertions.assertEquals(2, FontCheckUtil.checkGlyphsOfText("hi⫊", font, new CharacterChecker() {
            @Override
            public boolean check(int ch, PdfFont fontToCheck) {
                return !fontToCheck.containsGlyph(ch);
            }
        }));
    }

    @Test
    public void checkUnicodeMappingNotAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FontProgramFactory.createType1Font(FONTS_FOLDER + "cmr10.afm", FONTS_FOLDER + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.FORCE_EMBEDDED);
        int index  = FontCheckUtil.checkGlyphsOfText("h i", font, new CharacterChecker() {
            @Override
            public boolean check(int ch, PdfFont fontToCheck) {
                if (fontToCheck.containsGlyph(ch)) {
                    return !fontToCheck.getGlyph(ch).hasValidUnicode();
                } else {
                    return true;
                }
            }
        });
        Assertions.assertEquals(1, index);
    }

    @Test
    public void checkUnicodeMappingAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FontProgramFactory.createType1Font(FONTS_FOLDER + "cmr10.afm", FONTS_FOLDER + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.FORCE_EMBEDDED);
        int index  = FontCheckUtil.checkGlyphsOfText("hi", font, new CharacterChecker() {
            @Override
            public boolean check(int ch, PdfFont fontToCheck) {
                if (fontToCheck.containsGlyph(ch)) {
                    return !fontToCheck.getGlyph(ch).hasValidUnicode();
                } else {
                    return true;
                }
            }
        });
        Assertions.assertEquals(-1, index);
    }
}