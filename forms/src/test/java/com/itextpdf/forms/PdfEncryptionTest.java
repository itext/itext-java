package com.itextpdf.forms;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfEncryptionTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfEncryptionTest/";

    @Test
    public void encryptedDocumentWithFormFields() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "encryptedDocumentWithFormFields.pdf",
                new ReaderProperties().setPassword("12345".getBytes()));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        acroForm.getField("personal.name").getPdfObject();
        pdfDocument.close();
    }
}
