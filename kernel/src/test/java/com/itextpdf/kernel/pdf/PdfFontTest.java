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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramDescriptor;
import com.itextpdf.io.font.FontProgramDescriptorFactory;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeCollection;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.font.PdfType1Font;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfFontTest extends ExtendedITextTest {
    public static final int PageCount = 1;
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfFontTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfFontTest/";

    static final String author = "Alexander Chingarev";
    static final String creator = "iText";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createDocumentWithKozmin() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithKozmin.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin.pdf";
        String title = "Type 0 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("KozMinPro-Regular", "UniJIS-UCS2-H");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithKozminAndDifferentCodespaceRanges() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithKozminDifferentCodespaceRanges.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozminDifferentCodespaceRanges.pdf";
        String title = "Type 0 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("KozMinPro-Regular",
                "83pv-RKSJ-H", EmbeddingStrategy.PREFER_EMBEDDED);
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 50)
                .showText(type0Font.createGlyphLine("Hello\u7121\u540dworld\u6b98\u528d"))
                .endText()
                .restoreState();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithStSongUni() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithStSongUni.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithStSongUni.pdf";
        String title = "Type0 test";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("STSong-Light", "UniGB-UTF16-H");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    public void createDocumentWithStSong() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithStSong.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithStSong.pdf";
        String title = "Type0 test";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont type0Font = PdfFontFactory.createFont("STSong-Light", "Adobe-GB1-4");
        Assert.assertTrue("Type0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("CidFont expected", type0Font.getFontProgram() instanceof CidFont);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0.pdf";
        String title = "Type0 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        String font = fontsFolder + "abserif4_5.ttf";
        PdfFont type0Font = PdfFontFactory.createFont(font, "Identity-H");
//        type0Font.setSubset(false);
        Assert.assertTrue("PdfType0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("TrueType expected", type0Font.getFontProgram() instanceof TrueTypeFont);
        PdfPage page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();

//        new PdfCanvas(page)
//                .saveState()
//                .beginText()
//                .moveText(36, 650)
//                .setFontAndSize(type0Font, 12)
//                .showText(pangramme)
//                .endText()
//                .restoreState()
//                .release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        type0Font = PdfFontFactory.createFont(ttf, "Identity-H");
        Assert.assertTrue("PdfType0Font expected", type0Font instanceof PdfType0Font);
        Assert.assertTrue("TrueType expected", type0Font.getFontProgram() instanceof TrueTypeFont);
        page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(type0Font, 72)
                .showText("Hello World")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType3Font() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithType3Font.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithType3Font.pdf";
        // A A A A E E E ~ é
        String testString = "A A A A E E E ~ \u00E9";

        //writing type3 font characters
        String title = "Type3 font iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfType3Font type3 = PdfFontFactory.createType3Font(pdfDoc, false);
        Type3Glyph a = type3.addGlyph('A', 600, 0, 0, 600, 700);
        a.setLineWidth(100);
        a.moveTo(5, 5);
        a.lineTo(300, 695);
        a.lineTo(595, 5);
        a.closePathFillStroke();

        Assert.assertEquals(600.0, getContentWidth(type3, 'A'), 1e-5);

        Type3Glyph space = type3.addGlyph(' ', 600, 0, 0, 600, 700);
        space.setLineWidth(10);
        space.closePathFillStroke();

        Assert.assertEquals(600.0, getContentWidth(type3, ' '), 1e-5);

        Type3Glyph e = type3.addGlyph('E', 600, 0, 0, 600, 700);
        e.setLineWidth(100);
        e.moveTo(595, 5);
        e.lineTo(5, 5);
        e.lineTo(300, 350);
        e.lineTo(5, 695);
        e.lineTo(595, 695);
        e.stroke();

        Assert.assertEquals(600.0, getContentWidth(type3, 'E'), 1e-5);

        Type3Glyph tilde = type3.addGlyph('~', 600, 0, 0, 600, 700);
        tilde.setLineWidth(100);
        tilde.moveTo(595, 5);
        tilde.lineTo(5, 5);
        tilde.stroke();

        Assert.assertEquals(600.0, getContentWidth(type3, '~'), 1e-5);

        Type3Glyph symbol233 = type3.addGlyph('\u00E9', 600, 0, 0, 600, 700);
        symbol233.setLineWidth(100);
        symbol233.moveTo(540, 5);
        symbol233.lineTo(5, 340);
        symbol233.stroke();

        Assert.assertEquals(600.0, getContentWidth(type3, '\u00E9'), 1e-5);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(type3, 12)
                    .moveText(50, 800)
                    .showText(testString)
                    .endText();
            page.flush();
        }
        pdfDoc.close();

        // reading and comparing text
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    //TODO DEVSIX-4995 This test should be updated when DEVSIX-4995 is resolved
    public void notReplaceToUnicodeMappingTest() throws IOException {
        String filename = sourceFolder + "toUnicodeAndDifferenceFor32.pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfReader(filename))) {
            PdfDictionary pdfType3FontDict = (PdfDictionary) pdf.getPdfObject(112);
            PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont(pdfType3FontDict);
            //should be another glyph defined in ToUnicode mapping
            Glyph glyph = pdfType3Font.getGlyph(32);

            Assert.assertEquals(0, glyph.getWidth());
        }
    }

    @Test
    public void createTaggedDocumentWithType3Font() throws IOException, InterruptedException {
        String filename = destinationFolder + "createTaggedDocumentWithType3Font.pdf";
        String cmpFilename = sourceFolder + "cmp_createTaggedDocumentWithType3Font.pdf";
        // A A A A E E E ~ é
        String testString = "A A A A E E E ~ \u00E9";

        //writing type3 font characters
        String title = "Type3 font iText Document";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties());
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer).setTagged();

        PdfType3Font type3 = PdfFontFactory.createType3Font(pdfDoc, "T3Font", "T3Font", false);
        Type3Glyph a = type3.addGlyph('A', 600, 0, 0, 600, 700);
        a.setLineWidth(100);
        a.moveTo(5, 5);
        a.lineTo(300, 695);
        a.lineTo(595, 5);
        a.closePathFillStroke();

        Type3Glyph space = type3.addGlyph(' ', 600, 0, 0, 600, 700);
        space.setLineWidth(10);
        space.closePathFillStroke();

        Type3Glyph e = type3.addGlyph('E', 600, 0, 0, 600, 700);
        e.setLineWidth(100);
        e.moveTo(595, 5);
        e.lineTo(5, 5);
        e.lineTo(300, 350);
        e.lineTo(5, 695);
        e.lineTo(595, 695);
        e.stroke();

        Type3Glyph tilde = type3.addGlyph('~', 600, 0, 0, 600, 700);
        tilde.setLineWidth(100);
        tilde.moveTo(595, 5);
        tilde.lineTo(5, 5);
        tilde.stroke();

        Type3Glyph symbol233 = type3.addGlyph('\u00E9', 600, 0, 0, 600, 700);
        symbol233.setLineWidth(100);
        symbol233.moveTo(540, 5);
        symbol233.lineTo(5, 340);
        symbol233.stroke();

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        for (int i = 0; i < PageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(type3, 12)
                    .moveText(50, 800)
                    .showText(testString)
                    .endText()
                    .restoreState();
            page.flush();
        }
        pdfDoc.close();

        // reading and comparing text
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelvetica() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelvetica.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelvetica.pdf";
        String title = "Type3 test";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaOblique() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelveticaOblique.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelveticaOblique.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithHelveticaBoldOblique() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithHelveticaBoldOblique.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithHelveticaBoldOblique.pdf";

        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithCourierBold() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCourierBold.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCourierBold.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
        Assert.assertTrue("PdfType1Font expected", pdfFont instanceof PdfType1Font);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1FontAfm() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCMR10Afm.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(
                fontsFolder + "cmr10.afm", fontsFolder + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.PREFER_EMBEDDED);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));
        pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(afm, pfb),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.PREFER_EMBEDDED);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1FontPfm() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithCMR10Pfm.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Pfm.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(
                fontsFolder + "cmr10.pfm", fontsFolder + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.PREFER_EMBEDDED);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    public void createDocumentWithTrueTypeFont1() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeFont1.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1.pdf";
        String title = "Empty iText Document";
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        String font = fontsFolder + "abserif4_5.ttf";
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createFont(ttf, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeFont1NotEmbedded() throws IOException, InterruptedException {
        String filename = destinationFolder + "createDocumentWithTrueTypeFont1NotEmbedded.pdf";
        String cmpFilename = sourceFolder + "cmp_createDocumentWithTrueTypeFont1NotEmbedded.pdf";
        String title = "Empty iText Document";
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        String font = fontsFolder + "abserif4_5.ttf";
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createFont(ttf, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        new PdfCanvas(page)
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill()
                .release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeOtfFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeOtfFont.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeOtfFont.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "Puritan2.otf";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createFont(ttf, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED);
        Assert.assertTrue("PdfTrueTypeFont expected", pdfTrueTypeFont instanceof PdfTrueTypeFont);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeOtfFontPdf20() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTrueTypeOtfFontPdf20.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeOtfFontPdf20.pdf";

        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        String font = fontsFolder + "Puritan2.otf";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(font, PdfEncodings.IDENTITY_H);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.release();
        page.flush();

        pdfDoc.close();

        // Assert no CIDSet is written. It is deprecated in PDF 2.0
        PdfDocument generatedDoc = new PdfDocument(new PdfReader(filename));
        PdfFont pdfFont = PdfFontFactory.createFont(generatedDoc.getPage(1).getResources().getResource(PdfName.Font).getAsDictionary(new PdfName("F1")));
        PdfDictionary descriptor = pdfFont.getPdfObject().getAsArray(PdfName.DescendantFonts).getAsDictionary(0).getAsDictionary(PdfName.FontDescriptor);
        Assert.assertFalse("CIDSet is deprecated in PDF 2.0 and should not be written", descriptor.containsKey(PdfName.CIDSet));
        generatedDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType0OtfFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithType0OtfFont.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithType0OtfFont.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "Puritan2.otf";

        PdfFont pdfFont = PdfFontFactory.createFont(font, "Identity-H");
        Assert.assertTrue("PdfType0Font expected", pdfFont instanceof PdfType0Font);
        pdfFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfFont = PdfFontFactory.createFont(ttf, "Identity-H");
        Assert.assertTrue("PdfTrueTypeFont expected", pdfFont instanceof PdfType0Font);
        pdfFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testUpdateType3FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "type3Font.pdf";
        String outputFileName = destinationFolder + "type3Font_update.pdf";
        String cmpOutputFileName = sourceFolder + "cmp_type3Font_update.pdf";
        String title = "Type3 font iText Document";

        int numberOfGlyphs = 0;
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFileName),
                new PdfWriter(outputFileName).setCompressionLevel(CompressionConstants.NO_COMPRESSION))) {

            pdfDoc.getDocumentInfo().setAuthor(author).
                    setCreator(creator).
                    setTitle(title);

            PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory
                    .createFont((PdfDictionary) pdfDoc.getPdfObject(5));

            Type3Glyph newGlyph = pdfType3Font.addGlyph('\u00F6', 600, 0, 0, 600, 700);
            newGlyph.setLineWidth(100);
            newGlyph.moveTo(540, 5);
            newGlyph.lineTo(5, 840);
            newGlyph.stroke();

            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(pdfType3Font, 12)
                    .moveText(50, 800)
                    // A A A A A A E E E E ~ é ö
                    .showText("A A A A A A E E E E ~ \u00E9 \u00F6")
                    .endText()
                    .restoreState();
            page.flush();
            numberOfGlyphs = pdfType3Font.getNumberOfGlyphs();
        }
        Assert.assertEquals(6, numberOfGlyphs);
        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpOutputFileName, destinationFolder, "diff_"));
    }

    @Test
    public void testNewType3FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "type3Font.pdf";
        String outputFileName = destinationFolder + "type3Font_new.pdf";
        String cmpOutputFileName = sourceFolder + "cmp_type3Font_new.pdf";
        String title = "Type3 font iText Document";

        int numberOfGlyphs = 0;
        try (PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(inputFileName));
                PdfDocument outputPdfDoc = new PdfDocument(new PdfWriter(outputFileName)
                        .setCompressionLevel(CompressionConstants.NO_COMPRESSION))) {

            outputPdfDoc.getDocumentInfo().setAuthor(author).
                    setCreator(creator).
                    setTitle(title);

            PdfDictionary pdfType3FontDict = (PdfDictionary) inputPdfDoc.getPdfObject(5);
            PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory
                    .createFont((PdfDictionary) pdfType3FontDict.copyTo(outputPdfDoc));

            Type3Glyph newGlyph = pdfType3Font.addGlyph('\u00F6', 600, 0, 0, 600, 700);
            newGlyph.setLineWidth(100);
            newGlyph.moveTo(540, 5);
            newGlyph.lineTo(5, 840);
            newGlyph.stroke();

            PdfPage page = outputPdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(pdfType3Font, 12)
                    .moveText(50, 800)
                    // AAAAAA EEEE ~ é ö
                    .showText("AAAAAA EEEE ~ \u00E9 \u00F6")
                    .endText();
            page.flush();
            numberOfGlyphs = pdfType3Font.getNumberOfGlyphs();
        }

        Assert.assertEquals(6, numberOfGlyphs);
        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpOutputFileName, destinationFolder, "diff_"));
    }

    @Test
    public void testAddGlyphToType3FontWithCustomNames() throws IOException {
        String inputFile = sourceFolder + "type3FontWithCustomNames.pdf";

        int initialGlyphsNumber = 0;
        int finalGlyphsNumber = 0;
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(new ByteArrayOutputStream()))) {

            PdfDictionary pdfType3FontDict = (PdfDictionary) pdfDoc.getPdfObject(6);
            PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont(pdfType3FontDict);
            initialGlyphsNumber = pdfType3Font.getNumberOfGlyphs();

            Type3Glyph newGlyph = pdfType3Font.addGlyph('\u00F6', 600, 0, 0, 600, 700);
            newGlyph.setLineWidth(100);
            newGlyph.moveTo(540, 5);
            newGlyph.lineTo(5, 840);
            newGlyph.stroke();

            PdfPage page = pdfDoc.getPage(1);
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .setFontAndSize(pdfType3Font, 12)
                    .moveText(50, 800)
                    // AAAAAA EEEE ~ é ö
                    .showText("AAAAAA EEEE ~ \u00E9 \u00F6")
                    .endText();
            page.flush();
            finalGlyphsNumber = pdfType3Font.getNumberOfGlyphs();
        }

        Assert.assertEquals(initialGlyphsNumber + 1, finalGlyphsNumber);
    }

    @Test
    public void testNewType1FontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithCMR10Afm.pdf";
        String filename = destinationFolder + "DocumentWithCMR10Afm_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm_new.pdf";
        String title = "Type 1 font iText Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfType1Font = PdfFontFactory.createFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewTrueTypeFont1BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont1.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont1_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1_new.pdf";
        String title = "testNewTrueTypeFont1BasedExistingFont";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);
        PdfFont pdfTrueTypeFont = inputPdfDoc1.getFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewTrueTypeFont2BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont2.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont2_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont2_new.pdf";
        String title = "True Type font iText Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfFont = inputPdfDoc1.getFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testTrueTypeFont1BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeFont1.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeFont1_updated.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeFont1_updated.pdf";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader1, writer);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(4);
        PdfFont pdfFont = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testUpdateCjkFontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithKozmin.pdf";
        String filename = destinationFolder + "DocumentWithKozmin_update.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin_update.pdf";
        String title = "Type0 font iText Document";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(6);
        PdfFont pdfTrueTypeFont = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNewCjkFontBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithKozmin.pdf";
        String filename = destinationFolder + "DocumentWithKozmin_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithKozmin_new.pdf";
        String title = "Type0 font iText Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(6);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = inputPdfDoc1.getFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithTrueTypeAsType0BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeAsType0.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0_new.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0_new.pdf";
        String title = "Type0 font iText Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(6);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = inputPdfDoc1.getFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createUpdatedDocumentWithTrueTypeAsType0BasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithTrueTypeAsType0.pdf";
        String filename = destinationFolder + "DocumentWithTrueTypeAsType0_update.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTrueTypeAsType0_update.pdf";
        String title = "Type0 font iText Document";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfFont pdfTrueTypeFont = pdfDoc.getFont((PdfDictionary) pdfDoc.getPdfObject(6));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("New Hello World")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithType1WithToUnicodeBasedExistingFont() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "fontWithToUnicode.pdf";
        String filename = destinationFolder + "fontWithToUnicode_new.pdf";
        String cmpFilename = sourceFolder + "cmp_fontWithToUnicode_new.pdf";
        String title = "Type1 font iText Document";

        PdfReader reader1 = new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfFont pdfType1Font = inputPdfDoc1.getFont((PdfDictionary) pdfDictionary.copyTo(pdfDoc));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 756)
                .setFontAndSize(pdfType1Font, 10)
                .showText("New MyriadPro-Bold font.")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testType1FontUpdateContent() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithCMR10Afm.pdf";
        String filename = destinationFolder + "DocumentWithCMR10Afm_updated.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm_updated.pdf";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(4);
        PdfFont pdfType1Font = PdfFontFactory.createFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testType1FontUpdateContent2() throws IOException, InterruptedException {
        String inputFileName1 = sourceFolder + "DocumentWithCMR10Afm.pdf";
        String filename = destinationFolder + "DocumentWithCMR10Afm2_updated.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithCMR10Afm2_updated.pdf";

        PdfReader reader = new PdfReader(inputFileName1);
        PdfWriter writer = new PdfWriter(filename).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfDictionary pdfDictionary = (PdfDictionary) pdfDoc.getPdfObject(4);
        PdfFont pdfType1Font = pdfDoc.getFont(pdfDictionary);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("New Hello world")
                .endText()
                .restoreState();
        PdfFont pdfType1Font2 = pdfDoc.getFont(pdfDictionary);
        Assert.assertEquals(pdfType1Font, pdfType1Font2);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 620)
                .setFontAndSize(pdfType1Font2, 72)
                .showText("New Hello world2")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void createWrongAfm1() throws IOException {
        String message = "";
        try {
            byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));
            FontProgramFactory.createType1Font(null, pfb);
        } catch (com.itextpdf.io.exceptions.IOException e) {
            message = e.getMessage();
        }
        Assert.assertEquals("Invalid afm or pfm font file.", message);
    }

    @Test
    public void createWrongAfm2() throws IOException {
        String message = "";
        String font = fontsFolder + "cmr10.pfb";
        try {
            FontProgramFactory.createType1Font(font, null);
        } catch (com.itextpdf.io.exceptions.IOException e) {
            message = e.getMessage();
        }
        Assert.assertEquals(MessageFormatUtil.format(IoExceptionMessageConstant.IS_NOT_AN_AFM_OR_PFM_FONT_FILE, font), message);

    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.START_MARKER_MISSING_IN_PFB_FILE)
    })
    public void createWrongPfb() throws IOException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(afm, afm, false), null);
        byte[] streamContent = ((Type1Font) ((PdfType1Font) font).getFontProgram()).getFontStreamBytes();
        Assert.assertTrue("Empty stream content expected", streamContent == null);
    }

    @Test
    public void autoDetect1() throws IOException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));

        Assert.assertTrue("Type1 font expected", FontProgramFactory.createFont(afm, false) instanceof Type1Font);
    }

    @Test
    public void autoDetect2() throws IOException {
        byte[] afm = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.afm"));
        byte[] pfb = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "cmr10.pfb"));

        Assert.assertTrue("Type1 font expected", FontProgramFactory.createType1Font(afm, pfb) instanceof Type1Font);
    }

    @Test
    public void autoDetect3() throws IOException {
        byte[] otf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "Puritan2.otf"));
        Assert.assertTrue("TrueType (OTF) font expected", FontProgramFactory.createFont(otf) instanceof TrueTypeFont);
    }

    @Test
    public void autoDetect4() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "abserif4_5.ttf"));
        Assert.assertTrue("TrueType (TTF) expected", FontProgramFactory.createFont(ttf) instanceof TrueTypeFont);
    }

    @Test
    public void autoDetect5() throws IOException {
        byte[] ttf = StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "abserif4_5.ttf"));
        Assert.assertTrue("TrueType (TTF) expected", FontProgramFactory.createFont(ttf) instanceof TrueTypeFont);
    }

    @Test
    public void testPdfFontFactoryTtc() throws IOException, InterruptedException {
        String filename = destinationFolder + "testPdfFontFactoryTtc.pdf";
        String cmpFilename = sourceFolder + "cmp_testPdfFontFactoryTtc.pdf";

        String txt = "The quick brown fox";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "uming.ttc,1");

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText(txt)
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testWriteTTC() throws IOException, InterruptedException {
        String filename = destinationFolder + "DocumentWithTTC.pdf";
        String cmpFilename = sourceFolder + "cmp_DocumentWithTTC.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "uming.ttc";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createTtcFont(font, 0, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED, false);

        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttc = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createTtcFont(ttc, 1, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_EMBEDDED, false);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testWriteTTCNotEmbedded() throws IOException, InterruptedException {
        String filename = destinationFolder + "testWriteTTCNotEmbedded.pdf";
        String cmpFilename = sourceFolder + "cmp_testWriteTTCNotEmbedded.pdf";
        String title = "Empty iText Document";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        String font = fontsFolder + "uming.ttc";

        PdfFont pdfTrueTypeFont = PdfFontFactory.createTtcFont(font, 0, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED, false);

        pdfTrueTypeFont.setSubset(true);
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();
        page.flush();

        byte[] ttc = StreamUtil.inputStreamToArray(new FileInputStream(font));
        pdfTrueTypeFont = PdfFontFactory.createTtcFont(ttc, 1, PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED, false);
        pdfTrueTypeFont.setSubset(true);
        page = pdfDoc.addNewPage();
        canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfTrueTypeFont, 72)
                .showText("Hello world")
                .endText()
                .restoreState();
        canvas.rectangle(100, 500, 100, 100).fill();
        canvas.release();

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testNotoFont() throws IOException, InterruptedException {
        String filename = destinationFolder + "testNotoFont.pdf";
        String cmpFilename = sourceFolder + "cmp_testNotoFont.pdf";

        String japanese = "\u713C";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf",
                "Identity-H", EmbeddingStrategy.PREFER_EMBEDDED);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText(japanese)
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void woffFontTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "testWoffFont.pdf";
        String cmpFilename = sourceFolder + "cmp_testWoffFont.pdf";

        String helloWorld = "Hello world";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(fontsFolder + "SourceSerif4-Black.woff",
                "Identity-H", EmbeddingStrategy.PREFER_EMBEDDED);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText(helloWorld)
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }


    @Test
    public void NotoSansCJKjpTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "NotoSansCJKjpTest.pdf";
        String cmpFilename = sourceFolder + "cmp_NotoSansCJKjpTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
        // font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("1")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void NotoSansCJKjpTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "NotoSansCJKjpTest02.pdf";
        String cmpFilename = sourceFolder + "cmp_NotoSansCJKjpTest02.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
        // font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("\u3000")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void NotoSansCJKjpTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "NotoSansCJKjpTest03.pdf";
        String cmpFilename = sourceFolder + "cmp_NotoSansCJKjpTest03.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");

        // font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)

                // there is no such glyph in provided cff
                .showText("\u0BA4")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void SourceHanSansHWTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "SourceHanSansHWTest.pdf";
        String cmpFilename = sourceFolder + "cmp_SourceHanSansHWTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();

        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "SourceHanSansHW-Regular.otf", "Identity-H");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("12")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void sourceHanSerifKRRegularTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "SourceHanSerifKRRegularTest.pdf";
        String cmpFilename = sourceFolder + "cmp_SourceHanSerifKRRegularTest.pdf";
        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "SourceHanSerifKR-Regular.otf");
        //font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("\ube48\uc9d1")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder));
    }

    @Test
    public void sourceHanSerifKRRegularFullTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "SourceHanSerifKRRegularFullTest.pdf";
        String cmpFilename = sourceFolder + "cmp_SourceHanSerifKRRegularFullTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(filename));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "SourceHanSerifKR-Regular.otf");
        font.setSubset(false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(36, 680)
                .setFontAndSize(font, 12)
                .showText("\ube48\uc9d1")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder));
    }

    @Test
    public void mmType1ReadTest() throws IOException {
        String src = sourceFolder + "mmtype1.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src));
        PdfFont font = PdfFontFactory.createFont((PdfDictionary) doc.getPdfObject(335));
        doc.close();
        Assert.assertEquals(PdfName.MMType1, font.getPdfObject().getAsName(PdfName.Subtype));
        Assert.assertEquals(PdfType1Font.class, font.getClass());

    }

    @Test
    public void mmType1WriteTest() throws IOException, InterruptedException {
        String src = sourceFolder + "mmtype1.pdf";
        String filename = destinationFolder + "mmtype1_res.pdf";
        String cmpFilename = sourceFolder + "cmp_mmtype1.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(filename));
        PdfFont font = PdfFontFactory.createFont((PdfDictionary) doc.getPdfObject(335));

        PdfCanvas canvas = new PdfCanvas(doc.getPage(1));
        canvas.saveState()
                .setFillColor(ColorConstants.RED)
                .beginText()
                .moveText(5, 5)
                .setFontAndSize(font, 6)
                .showText("type1 font")
                .endText()
                .restoreState();

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }



    @Test
    public void testFontStyleProcessing() throws IOException, InterruptedException {
        String filename = destinationFolder + "testFontStyleProcessing.pdf";
        String cmpFilename = sourceFolder + "cmp_testFontStyleProcessing.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        PdfFont romanDefault = PdfFontFactory.createRegisteredFont("Times-Roman", PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        PdfFont romanNormal = PdfFontFactory.createRegisteredFont("Times-Roman", PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_NOT_EMBEDDED, FontStyles.NORMAL);
        PdfFont romanBold = PdfFontFactory.createRegisteredFont("Times-Roman", PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_NOT_EMBEDDED, FontStyles.BOLD);
        PdfFont romanItalic = PdfFontFactory.createRegisteredFont("Times-Roman", PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_NOT_EMBEDDED, FontStyles.ITALIC);
        PdfFont romanBoldItalic = PdfFontFactory.createRegisteredFont("Times-Roman", PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_NOT_EMBEDDED, FontStyles.BOLDITALIC);

        PdfPage page = pdfDoc.addNewPage(PageSize.A4.rotate());
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 400)
                .setFontAndSize(romanDefault, 72)
                .showText("Times-Roman default")
                .endText()
                .beginText()
                .moveText(36, 350)
                .setFontAndSize(romanNormal, 72)
                .showText("Times-Roman normal")
                .endText()
                .beginText()
                .moveText(36, 300)
                .setFontAndSize(romanBold, 72)
                .showText("Times-Roman bold")
                .endText()
                .beginText()
                .moveText(36, 250)
                .setFontAndSize(romanItalic, 72)
                .showText("Times-Roman italic")
                .endText()
                .beginText()
                .moveText(36, 200)
                .setFontAndSize(romanBoldItalic, 72)
                .showText("Times-Roman bolditalic")
                .endText()
                .restoreState();

        canvas.release();
        page.flush();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder, "diff_"));
    }

    @Test
    public void testCheckTTCSize() throws IOException {
        TrueTypeCollection collection = new TrueTypeCollection(fontsFolder + "uming.ttc");
        Assert.assertTrue(collection.getTTCSize() == 4);
    }

    @Test
    public void testFontDirectoryRegister() throws IOException {
        PdfFontFactory.registerDirectory(sourceFolder);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        for (String name : PdfFontFactory.getRegisteredFonts()) {
            PdfFont pdfFont = PdfFontFactory.createRegisteredFont(name);
            if (pdfFont == null)
                Assert.assertTrue("Font {" + name + "} can't be empty", false);
        }

        pdfDoc.addNewPage();

        pdfDoc.close();
    }

    @Test
    public void fontRegisterTest() throws IOException {
        FontProgramFactory.registerFont(fontsFolder + "NotoSerif-Regular_v1.7.ttf", "notoSerifRegular");
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfFont pdfFont = PdfFontFactory.createRegisteredFont("notoSerifRegular");
        //clear font cache for other tests
        FontProgramFactory.clearRegisteredFonts();
        Assert.assertTrue(pdfFont instanceof PdfType0Font);
        pdfDoc.addNewPage();
        pdfDoc.close();
    }

    @Test
    public void testSplitString() throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        List<String> list1 = font.splitString("Hello", 12f, 10);
        Assert.assertTrue(list1.size() == 3);

        List<String> list2 = font.splitString("Digitally signed by Dmitry Trusevich\nDate: 2015.10.25 14:43:56 MSK\nReason: Test 1\nLocation: Ghent", 12f, 176);
        Assert.assertTrue(list2.size() == 5);
    }

    @Test
    public void kozminNames() {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor("KozMinPro-Regular");
        Assert.assertEquals(descriptor.getFontName(), "KozMinPro-Regular");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "KozMinPro-Regular".toLowerCase());
        Assert.assertEquals(descriptor.getFontWeight(), 400);
    }

    @Test
    public void helveticaNames() {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor("Helvetica");
        Assert.assertEquals(descriptor.getFontName(), "Helvetica");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "Helvetica".toLowerCase());
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "helvetica");
        Assert.assertEquals(descriptor.getFontWeight(), 500);
    }

    @Test
    public void otfByStringNames() {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor(fontsFolder + "Puritan2.otf");
        Assert.assertEquals(descriptor.getFontName(), "Puritan2");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "Puritan 2.0 Regular".toLowerCase());
        Assert.assertEquals(descriptor.getFamilyNameLowerCase(), "Puritan 2.0".toLowerCase());
        Assert.assertEquals(descriptor.getStyle(), "Normal");
        Assert.assertEquals(descriptor.getFontWeight(), 400);

    }

    @Test
    public void otfByStreamNames() throws Exception {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor(StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "Puritan2.otf")));
        Assert.assertEquals(descriptor.getFontName(), "Puritan2");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "Puritan 2.0 Regular".toLowerCase());
        Assert.assertEquals(descriptor.getFamilyNameLowerCase(), "Puritan 2.0".toLowerCase());
        Assert.assertEquals(descriptor.getStyle(), "Normal");
        Assert.assertEquals(descriptor.getFontWeight(), 400);
    }

    @Test
    public void ttfByStringNames() {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor(fontsFolder + "abserif4_5.ttf");
        Assert.assertEquals(descriptor.getFontName(), "AboriginalSerif");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "Aboriginal Serif".toLowerCase());
        Assert.assertEquals(descriptor.getFamilyNameLowerCase(), "Aboriginal Serif".toLowerCase());
        Assert.assertEquals(descriptor.getStyle(), "Regular");
        Assert.assertEquals(descriptor.getFontWeight(), 400);
    }

    @Test
    public void ttfByStreamNames() throws Exception {
        FontProgramDescriptor descriptor = FontProgramDescriptorFactory.fetchDescriptor(StreamUtil.inputStreamToArray(new FileInputStream(fontsFolder + "abserif4_5.ttf")));
        Assert.assertEquals(descriptor.getFontName(), "AboriginalSerif");
        Assert.assertEquals(descriptor.getFullNameLowerCase(), "Aboriginal Serif".toLowerCase());
        Assert.assertEquals(descriptor.getFamilyNameLowerCase(), "Aboriginal Serif".toLowerCase());
        Assert.assertEquals(descriptor.getStyle(), "Regular");
        Assert.assertEquals(descriptor.getFontWeight(), 400);
    }

    @Test
    public void testDefaultFontWithReader() throws IOException {
        String inputFileName = sourceFolder + "type3Font.pdf";

        try(PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFileName))) {
            Assert.assertNotNull(pdfDoc.getDefaultFont());
            Assert.assertNull(pdfDoc.getDefaultFont().getPdfObject().getIndirectReference());
        }
    }

    @Test
    public void mSungLightFontRanges() throws IOException, InterruptedException {
        String filename = destinationFolder + "mSungLightFontRanges.pdf";
        String cmpFilename = sourceFolder + "cmp_mSungLightFontRanges.pdf";

        PdfWriter writer = new PdfWriter(filename);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfFont mSungFont = PdfFontFactory.createFont("MSung-Light", "UniCNS-UCS2-H");

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(mSungFont, 40)
                .showText("\u98db \u6708 \u9577")
                .endText()
                .restoreState();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, cmpFilename, destinationFolder));
    }

    private float getContentWidth(PdfType3Font type3, char glyph) {
        return type3.getContentWidth(new PdfString(new byte[]{(byte) type3.getGlyph(glyph).getCode()}));
    }
}
