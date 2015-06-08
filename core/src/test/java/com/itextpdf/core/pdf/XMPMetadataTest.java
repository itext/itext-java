package com.itextpdf.core.pdf;

import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class XMPMetadataTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/XmpWriterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/XmpWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createEmptyDocumentWithXmp() throws IOException, XMPException {
        String filename = "emptyDocumentWithXmp.pdf";
        FileOutputStream fos = new FileOutputStream(destinationFolder + filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(destinationFolder + filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertArrayEquals(readFile(sourceFolder + "emptyDocumentWithXmp.xml"), reader.getMetadata());
        Assert.assertNotNull(reader.getPageN(1));
        reader.close();

    }

    @Test
    public void createEmptyDocumentWithAbcXmp() throws IOException, XMPException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata("abc".getBytes());
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(new ByteArrayInputStream(fos.toByteArray()));
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertArrayEquals("abc".getBytes(), reader.getMetadata());
        Assert.assertNotNull(reader.getPageN(1));
        reader.close();

    }

    private byte[] readFile(String filename) throws IOException {
        FileInputStream input = new FileInputStream(filename);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        input.close();
        return output.toByteArray();
    }
}
