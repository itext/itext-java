/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfType1FontTest extends ExtendedITextTest {
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/kernel/fonts/";

    @Test
    public void standardFontWithoutFontDescriptorTest() {
        PdfFont font = PdfFontFactory.createFont(createType1FontDictionary(StandardFonts.HELVETICA));
        Assertions.assertTrue(font instanceof PdfType1Font);
        Assertions.assertTrue(((PdfType1Font) font).isBuiltInFont());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FONT_DICTIONARY_WITH_NO_FONT_DESCRIPTOR),
            @LogMessage(messageTemplate = IoLogMessageConstant.FONT_DICTIONARY_WITH_NO_WIDTHS)
    })
    public void afmPathAsBaseFontIsNotOpenedTest() {
        String afmPath = FONTS_FOLDER + "cmr10.afm";
        PdfFont font = PdfFontFactory.createFont(createType1FontDictionary(afmPath));
        Assertions.assertTrue(font.getFontProgram() instanceof DocType1Font);
        Assertions.assertEquals(afmPath, font.getFontProgram().getFontNames().getFontName());
    }

    private static PdfDictionary createType1FontDictionary(String baseFont) {
        PdfDictionary fontDictionary = new PdfDictionary();
        fontDictionary.put(PdfName.Type, PdfName.Font);
        fontDictionary.put(PdfName.Subtype, PdfName.Type1);
        fontDictionary.put(PdfName.BaseFont, new PdfName(baseFont));
        return fontDictionary;
    }
}
