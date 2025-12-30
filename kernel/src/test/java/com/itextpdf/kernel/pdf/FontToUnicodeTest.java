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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class FontToUnicodeTest extends ExtendedITextTest {
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/kernel/fonts/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/kernel/pdf/FontToUnicodeTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }
    
    @Test
    public void severalUnicodesWithinOneGlyphTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "severalUnicodesWithinOneGlyphTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
        PdfFont font = PdfFontFactory.createFont(FONTS_FOLDER + "NotoSansCJKjp-Bold.otf",
                PdfEncodings.IDENTITY_H);

        List<Glyph> glyphs = Collections.singletonList(font.getGlyph((int) '\u65E0'));
        GlyphLine glyphLine = new GlyphLine(glyphs);

        PdfCanvas canvas2 = new PdfCanvas(pdfDocument.addNewPage());
        canvas2
                .saveState()
                .beginText()
                .moveText(36, 800)
                .setFontAndSize(font, 12)
                .showText(glyphLine)
                .endText()
                .restoreState();

        pdfDocument.close();

        PdfDocument resultantPdfAsFile = new PdfDocument(CompareTool.createOutputReader(outFileName));
        String actualUnicode = PdfTextExtractor.getTextFromPage(resultantPdfAsFile.getFirstPage());

        Assertions.assertEquals("\u65E0", actualUnicode);
    }
}
