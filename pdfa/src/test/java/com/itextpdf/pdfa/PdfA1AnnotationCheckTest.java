package com.itextpdf.pdfa;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.source.ByteArrayOutputStream;
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
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA1AnnotationCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final public String cmpFolder = sourceFolder + "cmp/PdfA1AnnotationCheckTest/";
    static final public String destinationFolder = "./target/test/PdfA1AnnotationCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void annotationCheckTest01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AnnotationType1IsNotPermitted);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(rect);
        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest02() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AnAnnotationDictionaryShallNotContainTheCaKeyWithAValueOtherThan1);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setOpacity(new PdfNumber(0.5));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest03() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(0);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest04() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleAndNoviewFlagBitsShallBeSetTo0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setFlag(PdfAnnotation.Invisible);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest05() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
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

    @Test
    public void annotationCheckTest06() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest07() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_annotationCheckTest07.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_annotationCheckTest07.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfMarkupAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlags(PdfAnnotation.Print | PdfAnnotation.NoZoom | PdfAnnotation.NoRotate);

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest08() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AnnotationOfType1ShouldHaveContentsKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest09() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1a_annotationCheckTest09.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1a_annotationCheckTest09.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 100, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlag(PdfAnnotation.Print);
        annot.setContents("Hello world");

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
