package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.PageBreak;
import com.itextpdf.model.element.Paragraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class RendererTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PageBreakTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PageBreakTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }


    @Test
    public void multipleAdditionsOfSameModelElementTest() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "pageBreak1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Hello. I am a paragraph. I want you to process me correctly");
        document.add(p).add(p).add(new PageBreak()).add(p);

        document.close();
    }

}
