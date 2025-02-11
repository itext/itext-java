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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfType0FontIntegrationTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfType0FontIntegrationTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfType0FontIntegrationTest/";

    private static final String[] CHINESE = new String[] {
            "[以下、x。，料子法個資人用要供c及業B之括b對立該如 此蒐主N(需形並經擔予前關取揭手交其處,管:",
            "同政持易任；行符何司股認意受求與為X稱理務服府或集使表步)情係a必f提循地東合商代風益限列「得保於團作露進已品不就事遵險維建F公機一",
            "目有僱包院律的]"
    };
    private static final String JAPANESE =
            "5た うぞせツそぇBぁデぢつっず信えいすてナおドぅだトヅでぉミ(:テかちぜ)じぃあづ";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void notoSansJpFontTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "notoSansJpFontTest.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_notoSansJpFontTest.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont jpFont = PdfFontFactory.createFont(FONTS_FOLDER + "NotoSansJP-Regular.otf");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(jpFont, 8)
                .showText(jpFont.createGlyphLine(JAPANESE))
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, DESTINATION_FOLDER));
    }

    @Test
    public void notoSansScFontTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "notoSansScFontTest.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_notoSansScFontTest.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont jpFont = PdfFontFactory.createFont(FONTS_FOLDER + "NotoSansSC-Regular.otf");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .setFontAndSize(jpFont, 8)
                .moveText(36, 700);
        for (String s : CHINESE) {
            canvas.showText(jpFont.createGlyphLine(s))
                    .moveText(0, -16);
        }
        canvas.endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, DESTINATION_FOLDER));
    }

    @Test
    public void notoSansTcFontTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "notoSansTcFontTest.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_notoSansTcFontTest.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont jpFont = PdfFontFactory.createFont(FONTS_FOLDER + "NotoSansTC-Regular.otf");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .setFontAndSize(jpFont, 8)
                .moveText(36, 700);
        for (String s : CHINESE) {
            canvas.showText(jpFont.createGlyphLine(s))
                    .moveText(0, -16);
        }
        canvas.endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, DESTINATION_FOLDER));
    }

    @Test
    public void cmapPlatform0PlatEnc3Format4FontTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "cmapPlatform0PlatEnc3Format4FontTest.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_cmapPlatform0PlatEnc3Format4FontTest.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(FONTS_FOLDER + "glyphs.ttf");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .setFontAndSize(font, 20)
                .moveText(36, 700)
                .showText("===fff===iii===ﬁ")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, DESTINATION_FOLDER));
    }

    @Test
    public void cmapPlatform0PlatEnc3Format6FontTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "cmapPlatform0PlatEnc3Format6FontTest.pdf";
        String cmpFilename = SOURCE_FOLDER + "cmp_cmapPlatform0PlatEnc3Format6FontTest.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(FONTS_FOLDER + "glyphs-fmt-6.ttf");

        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .setFontAndSize(font, 20)
                .moveText(36, 700)
                .showText("===fff===iii===")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(filename, cmpFilename, DESTINATION_FOLDER));
    }
}
