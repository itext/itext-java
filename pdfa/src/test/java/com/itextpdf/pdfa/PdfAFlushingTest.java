package com.itextpdf.pdfa;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

@Category(IntegrationTest.class)
public class PdfAFlushingTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void flushingTest01() throws FileNotFoundException, XMPException, MalformedURLException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        imageXObject.flush();
        if (imageXObject.isFlushed()) {
            Assert.fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();
    }

    @Test
    public void flushingTest02() throws FileNotFoundException, XMPException, MalformedURLException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flush();
        if (lastPage.isFlushed()) {
            Assert.fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();
    }

    @Test
    public void flushingTest03() throws FileNotFoundException, XMPException, MalformedURLException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flushPageAndItsResources();
        if (!imageXObject.isFlushed()) {
            Assert.fail("When flushing the page along with it's resources, page check should be performed also page and all resources should be flushed.");
        }

        doc.close();
    }
}
