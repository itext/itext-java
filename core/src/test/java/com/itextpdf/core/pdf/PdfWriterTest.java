package com.itextpdf.core.pdf;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfWriterTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfWriterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createEmptyDocument() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "emptyDocument.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page1 = pdfDoc.addNewPage();
        PdfPage page2 = pdfDoc.addNewPage();
        page2.flush();
        pdfDoc.close();
    }

}
