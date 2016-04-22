package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
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
public class PdfA2ActionCheckTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/PdfA2ActionCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void actionCheck01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Launch);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck02() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Hide);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck03() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Sound);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck04() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Movie);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck05() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ResetForm);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck06() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ImportData);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck07() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.JavaScript);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck08() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.NamedActionType1IsNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Named);
        openActions.put(PdfName.N, new PdfName("CustomName"));
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck09() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.SetOCGState);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck10() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Rendition);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck11() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Trans);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck12() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.GoTo3DView);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        doc.close();
    }

    @Test
    public void actionCheck13() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.PageDictionaryShallNotContainAAEntry);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        page.setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        doc.close();
    }

    @Test
    public void actionCheck14() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.CatalogDictionaryShallNotContainAAEntry);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        doc.getCatalog().setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        doc.close();
    }

    @Test
    public void actionCheck15() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_actionCheck15.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA2ActionCheckTest/cmp_pdfA2b_actionCheck15.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.getOutlines(true);
        PdfOutline out = doc.getOutlines(false);
        out.addOutline("New").addAction(PdfAction.createGoTo("TestDest"));
        doc.addNewPage();

        doc.close();

        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
