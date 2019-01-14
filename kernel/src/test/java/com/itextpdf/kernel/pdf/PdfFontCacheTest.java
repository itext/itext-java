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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Category(IntegrationTest.class)
public class PdfFontCacheTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfFontCacheTest/";
    private static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfFontCacheTest/";


    static final String pangramme = "Amazingly few discotheques provide jukeboxes " +
            "but it now while sayingly ABEFGHJKNOPQRSTUWYZ?";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    private static final String[] TextSetHelloWorld = new String[]{"Hello World"};
    private static final String[] TextSetWithABC = new String[]{"Hello World", "ABC", "XYZ"};
    private static final String[] TextSetInternational = new String[]{"Hello World", "Привет, мир", "你好，世界", "안녕 세상"};
    private static final String[] TextSetChinese = new String[]{"Hello World", "你好", "世界"};

    @Test
    public void createDocumentWithKozmin() throws IOException, InterruptedException {
        String testName = "DocumentWithKozmin";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument pdfDoc = createDocument(filename);
        addPagesWithFonts(pdfDoc, "KozMinPro-Regular", "UniJIS-UCS2-H", TextSetChinese);
        addPagesWithFonts(pdfDoc, "KozMinPro-Regular", "Adobe-Japan1-0", TextSetChinese);
        pdfDoc.close();

        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaMixEncodings() throws IOException, InterruptedException {
        String testName = "DocumentWithHelveticaMixEncodings";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.HELVETICA;
        String encoding = null;

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "MacRoman", TextSetWithABC);
        pdfDoc.close();

        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelvetica() throws IOException, InterruptedException {
        String testName = "DocumentWithHelvetica";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.HELVETICA;
        String encoding = null;

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaFlushed() throws IOException, InterruptedException {
        String testName = "DocumentWithHelveticaFlushed";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.HELVETICA;
        String encoding = null;

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        //Flushed fonts cannot be reused.
        Assert.assertEquals(3, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTimesAndCustomEncoding() throws IOException, InterruptedException {
        String testName = "DocumentTimesAndCustomEncoding";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.TIMES_ROMAN;
        String encoding = "# full 'A' Aring 0041 'E' Egrave 0045 32 space 0020";

        String[] AE = new String[] {"A E"};
        addPagesWithFonts(pdfDoc, font, encoding, AE);
        addPagesWithFonts(pdfDoc, font, encoding, AE);
        addPagesWithFonts(pdfDoc, font, encoding, AE);

        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithCourierAndWinAnsiEncodings() throws IOException, InterruptedException {
        String testName = "DocumentCourierAndWinAnsiEncodings";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.COURIER;

        //All those encodings actually the same winansi.
        addPagesWithFonts(pdfDoc, font, null, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "", TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "WinAnsi", TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "WinAnsiEncoding", TextSetWithABC);

        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTimesBoldAndMacRomanEncodings() throws IOException, InterruptedException {
        String testName = "DocumentTimesBoldAndMacRomanEncodings";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = StandardFonts.TIMES_BOLD;

        //All those encodings actually the same MacRoman.
        addPagesWithFonts(pdfDoc, font, "MacRoman", TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "MacRomanEncoding", TextSetWithABC);

        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueType() throws IOException, InterruptedException {
        String testName = "DocumentWithTrueType";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "abserif4_5.ttf";
        String encoding = null;

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeFlushed() throws IOException, InterruptedException {
        String testName = "DocumentWithTrueTypeFlushed";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "abserif4_5.ttf";
        String encoding = null;

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        //Flushed fonts cannot be reused.
        //For some reason Acrobat shows only one font in Properties.
        //RUPS shows 3 instances of the same font.
        Assert.assertEquals(3, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0() throws IOException, InterruptedException {
        String testName = "DocumentWithTrueTypeAsType0";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "abserif4_5.ttf";
        String encoding = "Identity-H";

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0Flushed() throws IOException, InterruptedException {
        String testName = "DocumentWithTrueTypeAsType0Flushed";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "abserif4_5.ttf";
        String encoding = "Identity-H";

        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetWithABC);
        pdfDoc.close();

        //Flushed fonts cannot be reused.
        Assert.assertEquals(3, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithOpenTypeAsType0() throws IOException, InterruptedException {
        String testName = "DocumentWithOpenTypeAsType0";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "NotoSansCJKjp-Bold.otf";
        String encoding = "Identity-H";

        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        pdfDoc.close();

        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithOpenTypeAsType0Flushed() throws IOException, InterruptedException {
        String testName = "DocumentWithOpenTypeAsType0Flushed";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDoc = createDocument(filename);

        String font = fontsFolder + "NotoSansCJKjp-Bold.otf";
        String encoding = "Identity-H";

        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        pdfDoc.flushFonts();
        addPagesWithFonts(pdfDoc, font, encoding, TextSetInternational);
        pdfDoc.close();

        //Flushed fonts cannot be reused.
        Assert.assertEquals(3, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaFromDocument() throws IOException, InterruptedException {
        String testName = "DocumentWithHelveticaFromDocument";

        String input = sourceFolder + "DocumentWithHelvetica.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String font = StandardFonts.HELVETICA;
        String encoding = "WinAnsiEncoding";

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());


        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, font, "WinAnsi", TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, null, TextSetWithABC);
        pdfDoc.close();

        //We cannot rely on font name for a document font, so we treat them as two different fonts.
        //However we're trying to detect standard fonts in this case, so it will work.
        Assert.assertEquals(1, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaFromDocumentWithWrongEncoding() throws IOException, InterruptedException {
        String testName = "DocumentWithHelveticaFromDocumentWithWrongEncoding";

        String input = sourceFolder + "DocumentWithHelvetica.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String font = StandardFonts.HELVETICA;
        String encoding = "WinAnsiEncoding";

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());


        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, font, null, TextSetWithABC);
        addPagesWithFonts(pdfDoc, font, "MacRoman", TextSetWithABC);
        pdfDoc.close();

        //Two different encodings were used -> two fonts are expected.
        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAboriginalFromDocument() throws IOException, InterruptedException {
        String testName = "DocumentWithTrueTypeAboriginalFromDocument";

        String input = sourceFolder + "DocumentWithTrueTypeAboriginalSerif.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String font = "AboriginalSerif";
        String encoding = "WinAnsiEncoding";

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());

        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, fontsFolder + "abserif4_5.ttf", "WinAnsi", TextSetWithABC);
        pdfDoc.close();

        //We cannot rely on font name for a document font, so we treat them as two different fonts.
        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1NotoFromDocument() throws IOException, InterruptedException {
        String testName = "DocumentWithType1NotoFromDocument";

        String input = sourceFolder + "DocumentWithType1Noto.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String font = "NotoSansCJKjp-Bold";
        String encoding = "WinAnsiEncoding";

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());

        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, fontsFolder + "NotoSansCJKjp-Bold.otf", encoding, TextSetWithABC);
        pdfDoc.close();

        //We cannot rely on font name for a document font, so we treat them as two different fonts.
        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType0AboriginalFromDocument1() throws IOException, InterruptedException {
        String testName = "DocumentWithType0AboriginalFromDocument";

        String input = sourceFolder + "DocumentWithType0AboriginalSerif.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String font = "AboriginalSerif";
        String encoding = "Identity-H";

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());

        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, fontsFolder + "abserif4_5.ttf", encoding, TextSetWithABC);
        pdfDoc.close();

        //We cannot rely on font name for a document font, so we treat them as two different fonts.
        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType0NotoFromDocument1() throws IOException, InterruptedException {
        String testName = "DocumentWithType0NotoFromDocument";

        String input = sourceFolder + "DocumentWithType0Noto.pdf";
        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfReader reader = new PdfReader(input);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        String encoding = "Identity-H";
        String font = "NotoSansCJKjp-Bold-" + encoding;

        PdfDictionary fontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
        Assert.assertEquals(font, fontDict.getAsName(PdfName.BaseFont).getValue());
        Assert.assertEquals(encoding, fontDict.getAsName(PdfName.Encoding).getValue());

        PdfFont documentFont = PdfFontFactory.createFont(fontDict);

        //Add it to PdfDocument#documentFonts via PdfCanvas.
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.beginText()
                .moveText(36, 700)
                .setFontAndSize(documentFont, 20)
                .showText(pangramme.substring(0, pangramme.length()/2))
                .endText()
                .beginText()
                .setFontAndSize(documentFont, 20)
                .moveText(36, 670)
                .showText(pangramme.substring(pangramme.length()/2))
                .endText()
                .release();

        //There is only one just loaded and used document font.
        Assert.assertEquals(1, pdfDoc.getDocumentFonts().size());

        addPagesWithFonts(pdfDoc, fontsFolder + "NotoSansCJKjp-Bold.otf", "WinAnsi", TextSetWithABC);
        pdfDoc.close();

        //We cannot rely on font name for a document font, so we treat them as two different fonts.
        Assert.assertEquals(2, countPdfFonts(filename));
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType3Font() throws IOException, InterruptedException {
        String testName = "DocumentWithType3Font";

        String filename = destinationFolder + testName + ".pdf";
        String cmpFilename = sourceFolder + "cmp_" + testName + ".pdf";

        PdfDocument pdfDoc = createDocument(filename);

        PdfType3Font type3Font = PdfFontFactory.createType3Font(pdfDoc, false);
        Type3Glyph type3Glyph = type3Font.addGlyph('A', 600, 0, 0, 600, 700);
        type3Glyph.setLineWidth(100);
        type3Glyph.moveTo(5, 5);
        type3Glyph.lineTo(300, 695);
        type3Glyph.lineTo(595, 5);
        type3Glyph.closePathFillStroke();

        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(type3Font, 36)
                .moveText(50, 700)
                .showText("AA")
                .endText();

        type3Font = PdfFontFactory.createType3Font(pdfDoc, false);
        type3Glyph = type3Font.addGlyph('A', 600, 0, 0, 600, 700);
        type3Glyph.setLineWidth(100);
        type3Glyph.moveTo(5, 5);
        type3Glyph.lineTo(300, 695);
        type3Glyph.lineTo(595, 5);
        type3Glyph.closePathFillStroke();

        canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(type3Font, 36)
                .moveText(50, 650)
                .showText("AAA")
                .endText();

        pdfDoc.close();

        //PdfType3Font comparing returns false;
        Assert.assertEquals(2, countPdfFonts(filename));

        // reading and comparing text
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    private void addPagesWithFonts(PdfDocument pdfDoc, String fontProgram, String fontEncoding, String[] text) throws IOException {
        final int top = 700;
        for (String t : text) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, top)
                    .setFontAndSize(PdfFontFactory.createFont(fontProgram, fontEncoding, pdfDoc), 72)
                    .showText(t)
                    .endText()
                    .restoreState();

            canvas.release();
            page.flush();
        }
    }

    private int countPdfFonts(String filename) throws IOException {
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        Set<PdfIndirectReference> fonts = new HashSet<>();
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            PdfPage page = pdfDoc.getPage(i);
            for (PdfObject value : page.getResources().getResource(PdfName.Font).values()) {
                fonts.add(value.getIndirectReference());
            }
        }
        return fonts.size();
    }

    private PdfDocument createDocument(String filename) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        return new PdfDocument(writer);
    }
}
