package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.layer.PdfLayer;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.CompareTool;
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
            images[i] = new PdfImageXObject(document, ImageFactory.getImage(PdfXObjectTest.images[i]));
            images[i].setLayer(new PdfLayer("layer" + i, document));
            if (i % 2 == 0)
                images[i].flush();
        }
        for (int i = 0; i < 4; i++) {
            PdfPage page = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.addXObject(images[i], PageSize.Default);
            page.flush();
        }
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(images[0], 0, 0, 200);
        canvas.addXObject(images[1], 300, 0, 200);
        canvas.addXObject(images[2], 0, 300, 200);
        canvas.addXObject(images[3], 300, 300, 200);
        canvas.release();
        page.flush();
        document.close();

        Assert.assertTrue(new File(destinationDocument).length() < 20 * 1024 * 1024);
        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentFromImages1.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentFromImages2() throws IOException, PdfException, DocumentException, InterruptedException {
        final String destinationDocument = destinationFolder + "documentFromImages2.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        Image image = ImageFactory.getImage(sourceFolder + "itext.jpg");
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImage(image, 50, 500, 100, true);
        canvas.addImage(image, 200, 500, 100, false).flush();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentFromImages2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithForms() throws IOException, PdfException, DocumentException, InterruptedException {
        final String destinationDocument = destinationFolder + "documentWithForms1.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        //Create form XObject and flush to document.
        PdfFormXObject form = new PdfFormXObject(document, new Rectangle(0, 0, 50, 50));
        PdfCanvas canvas = new PdfCanvas(form);
        canvas.rectangle(10, 10, 30, 30);
        canvas.fill();
        canvas.release();
        form.flush();

        //Create page1 and add forms to the page.
        PdfPage page1 = document.addNewPage();
        canvas = new PdfCanvas(page1);
        canvas.addXObject(form, 0, 0).addXObject(form, 50, 0).addXObject(form, 0, 50).addXObject(form, 50, 50);
        canvas.release();

        //Create form from the page1 and flush it.
        form = new PdfFormXObject(page1);
        form.flush();

        //Now page1 can be flushed. It's not needed anymore.
        page1.flush();

        //Create page2 and add forms to the page.
        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.addXObject(form, 0, 0);
        canvas.addXObject(form, 0, 200);
        canvas.addXObject(form, 200, 0);
        canvas.addXObject(form, 200, 200);
        canvas.release();
        page2.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentWithForms1.pdf", destinationFolder, "diff_"));

    }

}
