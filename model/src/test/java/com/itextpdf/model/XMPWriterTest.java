package com.itextpdf.model;

import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPConst;
import com.itextpdf.core.xmp.XMPException;


import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.options.PropertyOptions;
import com.itextpdf.model.element.Paragraph;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class XMPWriterTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/XMPWriterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/XMPWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createPdfTest() throws IOException, XMPException, com.itextpdf.xmp.XMPException {
        String fileName = "xmp_metadata.pdf";
        // step 1
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "xmp_metadata.pdf")));
        Document document = new Document(pdfDocument);
        // step 2

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        XMPMeta xmp = XMPMetaFactory.create();
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "Hello World", null);
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "XMP & Metadata", null);
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "Metadata", null);
        pdfDocument.setXmpMetadata(xmp);

        // step 4
        document.add(new Paragraph("Hello World"));
        // step 5
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareXmp(destinationFolder + fileName, sourceFolder + "cmp_"+fileName, true));
    }

}
