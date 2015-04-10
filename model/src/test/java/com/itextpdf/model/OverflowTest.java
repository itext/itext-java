package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.Paragraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OverflowTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/OverflowTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/OverflowTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void textOverflowTest01() throws IOException, PdfException {
        String outFileName = destinationFolder + "textOverflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 29; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        document.add(new Paragraph(text.toString()).setFont(new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, ""))));

        document.close();
    }
}
