package com.itextpdf.pdfa;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
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
public class PdfA2AnnotationCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final public String cmpFolder = sourceFolder + "cmp/PdfA2AnnotationCheckTest/";
    static final public String destinationFolder = "./target/test/PdfA2AnnotationCheckTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void annotationCheckTest01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AnAnnotationDictionaryShallContainTheFKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(rect);
        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest02() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfPopupAnnotation(rect);

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest03() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest03.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 0, 0);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest04() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.EveryAnnotationShallHaveAtLeastOneAppearanceDictionary);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest05() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(0);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest06() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.TheFKeysPrintFlagBitShallBeSetTo1AndItsHiddenInvisibleNoviewAndTogglenoviewFlagBitsShallBeSetTo0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlags(PdfAnnotation.Print | PdfAnnotation.Invisible);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest07() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        annot.setDownAppearance(new PdfDictionary());
        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest08() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AppearanceDictionaryOfWidgetSubtypeAndBtnFieldTypeShallContainOnlyTheNKeyWithDictionaryValue);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);
        annot.getPdfObject().put(PdfName.FT, PdfName.Btn);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest09() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AppearanceDictionaryShallContainOnlyTheNKeyWithStreamValue);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest10() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest10.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest10.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest11() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest11.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest11.pdf";
        PdfWriter writer = new PdfWriter(outPdf);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.Print |PdfAnnotation.NoZoom | PdfAnnotation.NoRotate);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest12() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AnnotationOfType1ShouldHaveContentsKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.Print);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest13() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.EveryAnnotationShallHaveAtLeastOneAppearanceDictionary);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.Print);
        annot.setContents("Hello world");

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest14() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2a_annotationCheckTest14.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2a_annotationCheckTest14.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));

        PdfPage page = doc.addNewPage();

        Rectangle annotRect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(annotRect);
        annot.setFlags(PdfAnnotation.Print);
        annot.setContents("Hello World");

        annot.setNormalAppearance(createAppearance(doc, formRect));
        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private PdfStream createAppearance(PdfADocument doc, Rectangle formRect) throws IOException {
        PdfFormXObject form = new PdfFormXObject(formRect);

        PdfCanvas canvas = new PdfCanvas(form, doc);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        canvas.setFontAndSize(font, 12);
        canvas.beginText().setTextMatrix(200, 50).showText("Hello World").endText();
        return form.getPdfObject();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
