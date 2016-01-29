package com.itextpdf.pdfa;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfA1AnnotationCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest01() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(rect);
        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest02() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setOpacity(new PdfNumber(0.5));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest03() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(0);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest04() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setFlag(PdfAnnotation.Invisible);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest05() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);

        PdfStream s = new PdfStream("Hello World".getBytes());
        annot.setDownAppearance(new PdfDictionary());
        annot.setNormalAppearance(s);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest06() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest07() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlags(PdfAnnotation.Print | PdfAnnotation.NoZoom | PdfAnnotation.NoRotate);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void annotationCheckTest08() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest09() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setContents("Hello world");

        page.addAnnotation(annot);
        doc.close();
    }
}
