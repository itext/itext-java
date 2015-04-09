package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.Paragraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TextWrappingTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/TextWrappingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/TextWrappingTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void textWrappingTest01() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "textWrapping01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        document.add(new Paragraph(text.toString()));

        document.close();
    }

}
