/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FontToUnicodeTest extends ExtendedITextTest {
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/FontToUnicodeTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    // TODO DEVSIX-3634. In the output now we don't expect the \u2F46 unicode range.
    // TODO DEVSIX-3634. SUBSTITUTE "Assert.assertEquals("\u2F46"..." to "Assert.assertEquals("\u65E0"..." after the fix
    public void severalUnicodesWithinOneGlyphTest() throws IOException {
        String outFileName = destinationFolder + "severalUnicodesWithinOneGlyphTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf",
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

        PdfDocument resultantPdfAsFile = new PdfDocument(new PdfReader(outFileName));
        String actualUnicode = PdfTextExtractor.getTextFromPage(resultantPdfAsFile.getFirstPage());

        Assert.assertEquals("\u2F46", actualUnicode);
    }
}
