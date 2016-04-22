package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfA1ActionCheckTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void actionCheck01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException._1ActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
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
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        page.setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        doc.close();
    }

    @Test
    public void actionCheck10() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.DeprecatedSetStateAndNoOpActionsAreNotAllowed);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfDictionary action = new PdfDictionary();
        action.put(PdfName.S, PdfName.SetState);
        page.setAdditionalAction(PdfName.C, new PdfAction(action));

        doc.close();
    }

    @Test
    public void actionCheck11() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.CatalogDictionaryShallNotContainAAEntry);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.getCatalog().setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        doc.close();
    }
}
