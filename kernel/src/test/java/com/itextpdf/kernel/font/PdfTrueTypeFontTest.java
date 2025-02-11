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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfTrueTypeFontTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/font/PdfTrueTypeFontTest/";

    @Test
    public void testReadingPdfTrueTypeFontWithType1StandardFontProgram() throws IOException {
        // We deliberately use an existing PDF in this test and not simplify the test to create the
        // PDF object structure on the fly to be able to easily inspect the PDF with other processors
        String filePath = SOURCE_FOLDER + "trueTypeFontWithStandardFontProgram.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filePath));

        PdfDictionary fontDict = pdfDocument.getPage(1).getResources().getResource(PdfName.Font).getAsDictionary(new PdfName("F1"));
        PdfFont pdfFont = PdfFontFactory.createFont(fontDict);

        Assertions.assertEquals(542, pdfFont.getFontProgram().getAvgWidth());
        Assertions.assertEquals(556, pdfFont.getGlyph('a').getWidth());
    }

    @Test
    public void isBuiltInTest() {
        PdfFont font = PdfFontFactory.createFont(createTrueTypeFontDictionaryWithStandardHelveticaFont());
        Assertions.assertTrue(font instanceof PdfTrueTypeFont);
        Assertions.assertTrue(((PdfTrueTypeFont) font).isBuiltInFont());
    }

    @Test
    public void isNotBuiltInTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                SOURCE_FOLDER + "NotoSans-Regular.ttf",
                PdfEncodings.WINANSI);
        Assertions.assertTrue(font instanceof PdfTrueTypeFont);
        Assertions.assertFalse(((PdfTrueTypeFont) font).isBuiltInFont());
    }

    private static PdfDictionary createTrueTypeFontDictionaryWithStandardHelveticaFont() {
        PdfDictionary fontDictionary = new PdfDictionary();
        fontDictionary.put(PdfName.Type, PdfName.Font);
        fontDictionary.put(PdfName.Subtype, PdfName.TrueType);
        fontDictionary.put(PdfName.Encoding, PdfName.WinAnsiEncoding);
        fontDictionary.put(PdfName.BaseFont, new PdfName(StandardFonts.HELVETICA));
        return fontDictionary;
    }

}
