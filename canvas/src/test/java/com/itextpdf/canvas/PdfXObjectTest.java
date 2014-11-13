package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfXObjectTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfXObjectTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfXObjectTest/";

    static final public String[] images = new String[]{sourceFolder + "WP_20140410_001.bmp",
            sourceFolder + "WP_20140410_001.JPC",
            sourceFolder + "WP_20140410_001.jpg",
            sourceFolder + "WP_20140410_001.tif"};


    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createDocumentFromImages1() throws IOException, PdfException, DocumentException, InterruptedException {
        final String destinationDocument = destinationFolder + "documentFromImages1.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfImageXObject[] images = new PdfImageXObject[4];
        for (int i = 0; i < 4; i++) {
            images[i] = new PdfImageXObject(document, Image.getInstance(PdfXObjectTest.images[i]));
            if (i % 2 == 0)
                images[i].flush();
        }
        for (int i = 0; i < 4; i++) {
            PdfPage page = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.addImage(images[i], PageSize.Default);
            page.flush();
        }
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImage(images[0], 0, 0, 200);
        canvas.addImage(images[1], 300, 0, 200);
        canvas.addImage(images[2], 0, 300, 200);
        canvas.addImage(images[3], 300, 300, 200);
        page.flush();
        document.close();

        Assert.assertTrue(new File(destinationDocument).length() < 20 * 1024 * 1024);
        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentFromImages1.pdf", destinationFolder, "diff_"));
    }

}
