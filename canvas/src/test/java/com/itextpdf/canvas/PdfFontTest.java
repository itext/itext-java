package com.itextpdf.canvas;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.CidFont;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.font.PdfType3Font;
import com.itextpdf.canvas.font.Type3Glyph;
import com.itextpdf.core.font.PdfTrueTypeFont;
import com.itextpdf.core.font.PdfType0Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfFontTest {
    static final public int PageCount = 1;
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfFontTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfFontTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createDocumentWithKozmin() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithKozmin.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Type3 test";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        CidFont KozMin = new CidFont("KozMinPro-Regular");
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType0Font(pdfDoc, KozMin, "UniJIS-UCS2-H"), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test @Ignore
    public void createDocumentWithTrueTypeAsType0() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithWithTrueTypeAsType0.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Type3 test";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        byte[] ttf = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "abserif4_5.ttf"));
        TrueTypeFont abSerif = new TrueTypeFont("Aboriginal Serif", "Identity-H", ttf);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType0Font(pdfDoc, abSerif, "Identity-H"), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void createDocumentWithType3Font() throws IOException {
        String filename = destinationFolder + "DocumentWithType3Font.pdf";
        String testString = "A A A A E E E ~ é";

        //writing type3 font characters
        final String author = "Dmitry Trusevich";
        final String creator = "iText 6";
        final String title = "Type3 font iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfType3Font type3 = new PdfType3Font(pdfDoc,false);
        Type3Glyph a = type3.createGlyph('A', 600, 0, 0, 600, 700);
        a.setLineWidth(100);
        a.moveTo(5, 5);
        a.lineTo(300, 695);
        a.lineTo(595, 5);
        a.closePathFillStroke();

        Type3Glyph space = type3.createGlyph(' ', 600, 0, 0, 600, 700);
        space.setLineWidth(10);
        space.closePathFillStroke();

        Type3Glyph e = type3.createGlyph('E', 600, 0, 0, 600, 700);
        e.setLineWidth(100);
        e.moveTo(595, 5);
        e.lineTo(5, 5);
        e.lineTo(300, 350);
        e.lineTo(5, 695);
        e.lineTo(595, 695);
        e.stroke();

        Type3Glyph tilde = type3.createGlyph('~', 600, 0, 0, 600, 700);
        tilde.setLineWidth(100);
        tilde.moveTo(595, 5);
        tilde.lineTo(5, 5);
        tilde.stroke();

        Type3Glyph symbol233 = type3.createGlyph('é', 600, 0, 0, 600, 700);
        symbol233.setLineWidth(100);
        symbol233.moveTo(540, 5);
        symbol233.lineTo(5,340);
        symbol233.stroke();

        pdfDoc.getInfo().setAuthor(author).
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
        PdfReader reader = new PdfReader(new FileInputStream(filename));
        PdfDocument document = new PdfDocument(reader);
        PdfPage page = document.getPage(PageCount);
        String content = PdfEncodings.convertToString(page.getContentStream(0).getBytes(), "PDF");
        Assert.assertTrue(content.contains("(" + testString + ")"));
    }

    @Test
    public void createDocumentWithHelvetica() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithHelvetica.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Type3 test";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.HELVETICA, "")), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void createDocumentWithHelveticaOblique() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithHelveticaOblique.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.HELVETICA_OBLIQUE, "")), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void createDocumentWithHelveticaBoldOblique() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithHelveticaBoldOblique.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.HELVETICA_BOLDOBLIQUE, "")), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void createDocumentWithCourierBold() throws IOException {
        int pageCount = 1;
        String filename = destinationFolder + "DocumentWithCourierBold.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas
                    .saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(new PdfType1Font(pdfDoc, new Type1Font(FontConstants.COURIER_BOLD, "")), 72)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
            canvas.rectangle(100, 500, 100, 100).fill();
            canvas.release();
            page.flush();
        }
        pdfDoc.close();
    }

    @Test
    public void createDocumentWithType1FontAfm() throws IOException {
        String filename = destinationFolder + "DocumentWithCMR10Afm.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        byte[] pfb = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.pfb"));
        byte[] afm = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.afm"));
        Type1Font type1Font = new Type1Font("CMR10.afm", "", afm, pfb);
        PdfType1Font pdfType1Font = new PdfType1Font(pdfDoc, type1Font, true);
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
    }

    @Test
    public void createDocumentWithType1FontPfm() throws IOException {
        String filename = destinationFolder + "DocumentWithCMR10Pfm.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        byte[] pfb = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.pfb"));
        byte[] afm = Utilities.inputStreamToArray(new FileInputStream(sourceFolder +"cmr10.pfm"));
        Type1Font type1Font = new Type1Font("CMR10.pfm", "", afm, pfb);
        PdfType1Font pdfType1Font = new PdfType1Font(pdfDoc, type1Font, true);
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
    }

    @Test
    public void createDocumentWithType1Font5Pfm() throws IOException, PdfRuntimeException, DocumentException {
        String filename = destinationFolder + "DocumentWithCMR10_5Pfm.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        Document.compress = false;
        Document document = new Document();
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
        document.addAuthor(author);
        document.addCreator(creator);
        document.addTitle(title);
        document.open();
        document.newPage();
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        cb.beginText();
        cb.moveText(36, 700);
        byte[] pfb = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.pfb"));
        byte[] afm = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.pfm"));
        cb.setFontAndSize(BaseFont.createFont("CMR10.pfm", "", true, false, afm, pfb), 72);
        cb.showText("Hello world");
        cb.endText();
        cb.restoreState();
        cb.rectangle(100, 500, 100, 100);
        cb.fill();
        document.close();
    }

    @Test
    public void createDocumentWithType1Font5Afm() throws IOException, PdfRuntimeException, DocumentException {
        String filename = destinationFolder + "DocumentWithCMR10_5Afm.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        Document.compress = false;
        Document document = new Document();
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
        document.addAuthor(author);
        document.addCreator(creator);
        document.addTitle(title);
        document.open();
        document.newPage();
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        cb.beginText();
        cb.moveText(36, 700);
        byte[] pfb = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.pfb"));
        byte[] afm = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "cmr10.afm"));
        cb.setFontAndSize(BaseFont.createFont("CMR10.afm", "", true, false, afm, pfb), 72);
        cb.showText("Hello world");
        cb.endText();
        cb.restoreState();
        cb.rectangle(100, 500, 100, 100);
        cb.fill();
        document.close();
    }

    @Test @Ignore
    public void createDocumentWithTrueTypeFont1() throws IOException {
        String filename = destinationFolder + "DocumentWithTrueTypeFont1.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        byte[] ttf = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "abserif4_5.ttf"));
        TrueTypeFont abSerif = new TrueTypeFont("Aboriginal Serif", "WinAnsi", ttf);
        PdfTrueTypeFont pdfTrueTypeFont = new PdfTrueTypeFont(pdfDoc, abSerif, true);
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
        pdfDoc.close();
    }

    @Test @Ignore
    public void createDocumentWithTrueTypeFont2() throws IOException {
        String filename = destinationFolder + "DocumentWithTrueTypeFont2.pdf";

        final String author = "Alexander Chingarev";
        final String creator = "iText 6";
        final String title = "Empty iText 6 Document";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        byte[] ttf = Utilities.inputStreamToArray(new FileInputStream(sourceFolder + "Puritan2.otf"));
        TrueTypeFont puritan = new TrueTypeFont("Puritan", "WinAnsi", ttf);
        PdfTrueTypeFont pdfTrueTypeFont = new PdfTrueTypeFont(pdfDoc, puritan, true);
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
        pdfDoc.close();
    }

    @Test
    public void testNewType3FontBasedExistingFont() throws IOException {

        final String author = "Dmitry Trusevich";
        final String creator = "iText 6";
        final String title = "Type3 font iText 6 Document";
        String inputFileName =  sourceFolder+"type3Font.pdf";
        String outputFileName =  destinationFolder +"new_type3Font.pdf";
        PdfReader reader =  new PdfReader(inputFileName);
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(outputFileName));
        pdfWriter.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument inputPdfDoc = new PdfDocument(reader);
        PdfDocument outputPdfDoc = new PdfDocument(pdfWriter);

        outputPdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfType3Font pdfType3Font = new PdfType3Font(outputPdfDoc,(PdfDictionary)inputPdfDoc.getPdfObject(4));

        Type3Glyph newGlyph = pdfType3Font.createGlyph('ö',600, 0, 0, 600, 700);
        newGlyph.setLineWidth(100);
        newGlyph.moveTo(540, 5);
        newGlyph.lineTo(5,840);
        newGlyph.stroke();

        PdfPage page = outputPdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(pdfType3Font,12)
                .moveText(50, 800)
                .showText("AAAAAA EEEE ~ é ö")
                .endText();
        page.flush();
        outputPdfDoc.close();

        Assert.assertEquals(6,pdfType3Font.getCharGlyphs().size());
    }

    @Test
    public void testNewType1FontBasedExistingFont() throws IOException {
        final String author = "Dmitry Trusevich";
        final String creator = "iText 6";
        final String title = "Type3 font iText 6 Document";
        String inputFileName1 =  sourceFolder + "DocumentWithCMR10Afm.pdf";


        PdfReader reader1 =  new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        String filename = destinationFolder + "DocumentWithCMR10Afm_new.pdf";
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);
        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfType1Font pdfType1Font = new PdfType1Font(pdfDoc,pdfDictionary);
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

    }

    @Test
    public void testNewTrueTypeFont1BasedExistingFont() throws IOException, PdfRuntimeException, InterruptedException {
        final String author = "Dmitry Trusevich";
        final String creator = "iText 6";
        final String title = "Type3 font iText 6 Document";
        String inputFileName1 =  sourceFolder + "DocumentWithTrueTypeFont1.pdf";


        PdfReader reader1 =  new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);


        String filename = destinationFolder + "DocumentWithTrueTypeFont1_new.pdf";


        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);
        PdfTrueTypeFont pdfTrueTypeFont = new PdfTrueTypeFont(pdfDoc,pdfDictionary);
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
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_DocumentWithTrueTypeFont1_new.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void testNewTrueTypeFont2BasedExistingFont() throws IOException, PdfRuntimeException, InterruptedException {
        final String author = "Dmitry Trusevich";
        final String creator = "iText 6";
        final String title = "Type3 font iText 6 Document";
        String inputFileName1 = sourceFolder +  "DocumentWithTrueTypeFont2.pdf";


        PdfReader reader1 =  new PdfReader(inputFileName1);
        PdfDocument inputPdfDoc1 = new PdfDocument(reader1);
        PdfDictionary pdfDictionary = (PdfDictionary) inputPdfDoc1.getPdfObject(4);

        String filename = destinationFolder + "DocumentWithTrueTypeFont2_new.pdf";


        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(PdfOutputStream.NO_COMPRESSION);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);

        PdfTrueTypeFont pdfTrueTypeFont = new PdfTrueTypeFont(pdfDoc,pdfDictionary);
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
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder +  "cmp_DocumentWithTrueTypeFont2_new.pdf", destinationFolder, "diff_"));

    }

}
