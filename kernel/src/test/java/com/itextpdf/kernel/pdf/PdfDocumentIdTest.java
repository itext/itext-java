package com.itextpdf.kernel.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Demey
 */
public class PdfDocumentIdTest {

    @Test
    public void changeIdTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        String value = "Modified ID 1234";
        pdfDocument.setModifiedDocumentId(new PdfString(value));
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        Assert.assertNotNull(idArray);
        String extractedValue = idArray.getAsString(1).getValue();
        pdfDocument.close();

        Assert.assertEquals(value, extractedValue);
    }

}
