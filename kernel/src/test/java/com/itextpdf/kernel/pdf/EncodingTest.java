/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class EncodingTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/EncodingTest/";
    public static final String outputFolder = "./target/test/com/itextpdf/kernel/pdf/EncodingTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(outputFolder);
    }

    @Test
    public void surrogatePairTest() throws IOException, InterruptedException {
        String fileName = "surrogatePairTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }


    @Test
    public void customSimpleEncodingTimesRomanTest() throws IOException, InterruptedException {
        String fileName = "customSimpleEncodingTimesRomanTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "# simple 1 0020 041c 0456 0440 044a 0050 0065 0061 0063", true);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void customFullEncodingTimesRomanTest() throws IOException, InterruptedException {
        String fileName = "customFullEncodingTimesRomanTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInStandardFontTest() throws IOException, InterruptedException {
        String fileName = "notdefInStandardFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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


        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInTrueTypeFontTest() throws IOException, InterruptedException {
        String fileName = "notdefInTrueTypeFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "# simple 32 0020 00C5 1987", true);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36).
                showText("\u00C5 \u1987").
                endText().
                restoreState();
        font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", PdfEncodings.WINANSI, true);
        canvas.
                saveState().
                beginText().
                moveText(36, 756).
                setFontAndSize(font, 36).
                showText("\u1987").
                endText().
                restoreState();
        doc.close();


        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void notdefInType0Test() throws IOException, InterruptedException {
        String fileName = "notdefInType0Test.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolDefaultFontTest() throws IOException, InterruptedException {
        String fileName = "symbolDefaultFontTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.SYMBOL);
        fillSymbolDefaultPage(font, doc.addNewPage());
        //WinAnsi encoding doesn't support special symbols
        font = PdfFontFactory.createFont(StandardFonts.SYMBOL, PdfEncodings.WINANSI);
        fillSymbolDefaultPage(font, doc.addNewPage());
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
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
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
        PdfDocument doc = new PdfDocument(writer);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "Symbols1.ttf", true);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolTrueTypeFontIdentityTest() throws IOException, InterruptedException {
        String fileName = "symbolTrueTypeFontIdentityTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void symbolTrueTypeFontSameCharsIdentityTest() throws IOException, InterruptedException {
        String fileName = "symbolTrueTypeFontSameCharsIdentityTest.pdf";
        PdfWriter writer = new PdfWriter(outputFolder + fileName);
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

        Assert.assertNull(new CompareTool().compareByContent(outputFolder + fileName, sourceFolder + "cmp_" + fileName, outputFolder, "diff_"));
    }

    @Test
    public void encodingStreamExtractionTest() throws IOException {
        String fileName = sourceFolder + "encodingStream01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileName));
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
        Assert.assertEquals("abc", extractedText);
    }

    @Test
    public void differentCodeSpaceRangeLengthsExtractionTest() throws IOException {
        String fileName = sourceFolder + "differentCodeSpaceRangeLengths01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(fileName));
        String extractedText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1));
        Assert.assertEquals("Hello\u7121\u540dworld\u6b98\u528d", extractedText);
    }

}
