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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class EncodingTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/EncodingTest/";
    public static final String outputFolder = "./target/test/com/itextpdf/kernel/pdf/EncodingTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(outputFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(outputFolder);
    }

    @Test
    public void surrogatePairTest() throws IOException, InterruptedException {
        String fileName = "surrogatePairTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "DejaVuSans.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 750).
                setFontAndSize(font, 72).
                showText("\uD835\uDD59\uD835\uDD56\uD835\uDD5D\uD835\uDD5D\uD835\uDD60\uD83D\uDE09\uD835\uDD68" +
                        "\uD835\uDD60\uD835\uDD63\uD835\uDD5D\uD835\uDD55").
                endText().
                restoreState();
        canvas.release();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void customSimpleEncodingTimesRomanTest() throws IOException, InterruptedException {
        String fileName = "customSimpleEncodingTimesRomanTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "# simple 1 0020 041c 0456 0440 044a 0050 0065 0061 0063",
                EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                        // Міръ Peace
                        showText("\u041C\u0456\u0440\u044A Peace").
                endText().
                restoreState();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void customFullEncodingTimesRomanTest() throws IOException, InterruptedException {
        String fileName = "customFullEncodingTimesRomanTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN,
                "# full 'A' Aring 0041 'E' Egrave 0045 32 space 0020");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 806).
                setFontAndSize(font, 12).
                showText("A E").
                endText().
                restoreState();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInStandardFontTest() throws IOException, InterruptedException {
        String fileName = "notdefInStandardFontTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA,
                "# full 'A' Aring 0041 'E' abc11 0045 32 space 0020");
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36).
                showText("A E").
                endText().
                restoreState();

        font = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI);
        canvas.
                saveState().
                beginText().
                moveText(36, 756).
                setFontAndSize(font, 36).
                showText("\u0188").
                endText().
                restoreState();

        doc.close();


        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInTrueTypeFontTest() throws IOException, InterruptedException {
        String fileName = "notdefInTrueTypeFontTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "# simple 32 0020 00C5 1987", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36).
                showText("\u00C5 \u1987").
                endText().
                restoreState();
        font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        canvas.
                saveState().
                beginText().
                moveText(36, 756).
                setFontAndSize(font, 36).
                showText("\u1987").
                endText().
                restoreState();
        doc.close();


        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInType0Test() throws IOException, InterruptedException {
        String fileName = "notdefInType0Test.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36).
                showText("\u00C5 \u1987").
                endText().
                restoreState();

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolDefaultFontTest() throws IOException, InterruptedException {
        String fileName = "symbolDefaultFontTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.SYMBOL);
        fillSymbolDefaultPage(font, doc.addNewPage());
        //WinAnsi encoding doesn't support special symbols
        font = PdfFontFactory.createFont(StandardFonts.SYMBOL, PdfEncodings.WINANSI);
        fillSymbolDefaultPage(font, doc.addNewPage());
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    private void fillSymbolDefaultPage(PdfFont font, PdfPage page) {
        PdfCanvas canvas = new PdfCanvas(page);
        StringBuilder builder = new StringBuilder();
        for (int i = 32; i <= 100; i++) {
            builder.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 12).
                moveText(36, 806).
                showText(builder.toString()).
                endText().
                restoreState();
        builder = new StringBuilder();
        for (int i = 101; i <= 190; i++) {
            builder.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 12).
                moveText(36, 786).
                showText(builder.toString()).
                endText();
        builder = new StringBuilder();
        for (int i = 191; i <= 254; i++) {
            builder.append((char) i);
        }
        canvas.
                beginText().
                moveText(36, 766).
                showText(builder.toString()).
                endText().
                restoreState();
    }

    @Test
    public void symbolTrueTypeFontWinAnsiTest() throws IOException, InterruptedException {
        String fileName = "symbolTrueTypeFontWinAnsiTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "Symbols1.ttf", PdfEncodings.WINANSI,
                EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        StringBuilder str = new StringBuilder();
        for (int i = 32; i <= 65; i++) {
            str.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36).
                showText(str.toString()).
                endText();

        str = new StringBuilder();
        for (int i = 65; i <= 190; i++) {
            str.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                moveText(36, 756).
                setFontAndSize(font, 36).
                showText(str.toString()).
                endText();
        str = new StringBuilder();
        for (int i = 191; i <= 254; i++) {
            str.append((char) i);
        }
        canvas.
                beginText().
                moveText(36, 726).
                setFontAndSize(font, 36).
                showText(str.toString()).
                endText().
                restoreState();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolTrueTypeFontIdentityTest() throws IOException, InterruptedException {
        String fileName = "symbolTrueTypeFontIdentityTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "Symbols1.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        StringBuilder builder = new StringBuilder();
        for (int i = 32; i <= 100; i++) {
            builder.append((char) i);
        }
        StringBuilder str = new StringBuilder(builder.toString());
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 36).
                moveText(36, 786).
                showText(str.toString()).
                endText().
                restoreState();

        str = new StringBuilder();
        for (int i = 101; i <= 190; i++) {
            str.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 36).
                moveText(36, 746).
                showText(str.toString()).
                endText().
                restoreState();
        str = new StringBuilder();
        for (int i = 191; i <= 254; i++) {
            str.append((char) i);
        }
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 36).
                moveText(36, 766).
                showText(str.toString()).
                endText().
                restoreState();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolTrueTypeFontSameCharsIdentityTest() throws IOException, InterruptedException {
        String fileName = "symbolTrueTypeFontSameCharsIdentityTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "Symbols1.ttf", PdfEncodings.IDENTITY_H);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        String line = "AABBCCDDEEFFGGHHIIJJ";
        canvas.
                saveState().
                beginText().
                setFontAndSize(font, 36).
                moveText(36, 786).
                showText(line).
                endText().
                restoreState();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void encodingStreamExtractionTest() throws IOException {
        String fileName = sourceFolder + "encodingStream01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileName));
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
        Assertions.assertEquals("abc", extractedText);
    }

    @Test
    public void differentCodeSpaceRangeLengthsExtractionTest() throws IOException {
        String fileName = sourceFolder + "differentCodeSpaceRangeLengths01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileName));
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
        Assertions.assertEquals("Hello\u7121\u540dworld\u6b98\u528d", extractedText);
    }
}
