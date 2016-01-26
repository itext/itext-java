package com.itextpdf.pdfa;

import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.*;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.*;

@Category(IntegrationTest.class)
public class PdfA3EmbeddedFilesCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fileSpecCheckTest01() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, outputIntent);
        pdfDocument.setXmpMetadata();

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        ByteArrayOutputStream txt = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(txt);
        out.print("<foo><foo2>Hello world</foo2></foo>");
        out.close();
        pdfDocument.addFileAttachment("foo file", txt.toByteArray(), "foo.xml", "application/xml", null, PdfName.Source);

        pdfDocument.close();
    }

    @Test
    public void fileSpecCheckTest02() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, outputIntent);
        pdfDocument.setXmpMetadata();

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        ByteArrayOutputStream txt = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(txt);
        out.print("<foo><foo2>Hello world</foo2></foo>");
        out.close();
        pdfDocument.addFileAttachment("foo file", txt.toByteArray(), "foo.xml", null, null, PdfName.Unspecified);

        pdfDocument.close();
    }

    @Test
    public void fileSpecCheckTest03() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, outputIntent);
        pdfDocument.setXmpMetadata();

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();


        byte[] somePdf = new byte[25];
        pdfDocument.addFileAttachment("some pdf file", somePdf, "foo.pdf", "application/pdf", null,
                PdfName.Data);

        pdfDocument.close();
    }

    @Test
    public void fileSpecCheckTest04() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, outputIntent);
        pdfDocument.setXmpMetadata();

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        ByteArrayOutputStream txt = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(txt);
        out.print("<foo><foo2>Hello world</foo2></foo>");
        out.close();
        pdfDocument.addFileAttachment("foo file", txt.toByteArray(), "foo.xml", null, null, null);

        pdfDocument.close();
    }
}
