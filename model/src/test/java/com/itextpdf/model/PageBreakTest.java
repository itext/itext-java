package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.PageBreak;
import com.itextpdf.model.element.Paragraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PageBreakTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PageBreakTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PageBreakTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void pageBreakTest1() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "pageBreak1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);
        document.add(new PageBreak());

        document.close();
    }

    @Test
    public void pageBreakTest2() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "pageBreak2.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Hello World!")).add(new PageBreak(new PageSize(200, 200)));

        document.close();
    }

}
