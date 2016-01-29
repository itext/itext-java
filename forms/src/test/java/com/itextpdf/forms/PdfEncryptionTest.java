package com.itextpdf.forms;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfEncryptionTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfEncryptionTest/";

    @Test@Ignore
    public void encryptedDocumentWithFormFields() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "encryptedDocumentWithFormFields.pdf"), "12345".getBytes());
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        acroForm.getField("name").getPdfObject();
        pdfDocument.close();
    }
}
