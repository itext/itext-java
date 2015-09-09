package com.itextpdf.pdfa;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.*;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.PdfOutputIntent;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA2AnnotationCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest01() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(doc, rect);
        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest02() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfPopupAnnotation(doc, rect);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest03() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 0, 0);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest04() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest05() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfTextAnnotation(doc, rect);
        annot.setFlag(0);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest06() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfTextAnnotation(doc, rect);
        annot.setFlags(PdfAnnotation.Print | PdfAnnotation.Invisible);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest07() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        PdfStream s = new PdfStream("Hello wrold".getBytes());
        annot.setDownAppearance(new PdfDictionary());
        annot.setNormalAppearance(s);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest08() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);
        annot.getPdfObject().put(PdfName.FT, PdfName.Btn);

        PdfStream s = new PdfStream("Hello wrold".getBytes());
        annot.setNormalAppearance(s);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest09() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest10() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        annot.setNormalAppearance(new PdfStream("Hello world".getBytes()));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest11() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfTextAnnotation(doc, rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print |PdfAnnotation.NoZoom | PdfAnnotation.NoRotate);

        annot.setNormalAppearance(new PdfStream("Hello world".getBytes()));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest12() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(doc, rect);
        annot.setFlags(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest13() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));

        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(doc, rect);
        annot.setFlags(PdfAnnotation.Print);
        annot.setContents("Hello World");
        annot.setNormalAppearance(new PdfStream("Hello World".getBytes()));

        page.addAnnotation(annot);
        doc.close();
    }
}
