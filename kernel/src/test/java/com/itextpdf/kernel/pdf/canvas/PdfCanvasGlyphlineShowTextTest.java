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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfCanvasGlyphlineShowTextTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasGlyphlineShowTextTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasGlyphlineShowTextTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void notoSerifWithInvalidXYPlacementAnchorDeltaTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "notoSerifWithInvalidXYPlacementAnchorDeltaTest.pdf";
        String cmpPdf = sourceFolder + "cmp_notoSerifWithInvalidXYPlacementAnchorDeltaTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdf));
        PdfPage page = pdfDoc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSerif-Regular_v1.7.ttf", PdfEncodings.IDENTITY_H);

        // ゙B̸̭̼ͣ̎̇
        List<Glyph> glyphs = Arrays.asList(
                font.getGlyph((int) '\u0042'),
                applyGlyphParameters('\u0363', -1, 327, 178, font),
                applyGlyphParameters('\u030e', -1, 10, 298, font),
                applyGlyphParameters('\u0307', -1, 0,224, font),
                applyGlyphParameters('\u032d', -3, 11, 620, font),
                applyGlyphParameters('\u033c', -1 , -1, -220, font),
                font.getGlyph((int) '\u0338')
        );
        GlyphLine glyphLine = new GlyphLine(glyphs);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 800)
                .setFontAndSize(font, 12)
                .showText(glyphLine)
                .endText()
                .restoreState();

        canvas.release();

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    private Glyph applyGlyphParameters(char glyphUni, int anchorDelta, int xPlacement, int yPlacement, PdfFont font) {
        Glyph glyph = font.getGlyph((int) glyphUni);

        glyph.setAnchorDelta((short) anchorDelta);
        glyph.setXPlacement((short) xPlacement);
        glyph.setYPlacement((short) yPlacement);

        return glyph;
    }
}
