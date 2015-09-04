package com.itextd.pdfa;

import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.pdfa.PdfAConformanceException;
import com.itextpdf.pdfa.PdfAConformanceLevel;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.PdfOutputIntent;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA2AcroFormCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test(expected = PdfAConformanceException.class)
    public void acroFormCheck01() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.NeedAppearances, new PdfBoolean(true));
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    @Test
    public void acroFormCheck02() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.NeedAppearances, new PdfBoolean(false));
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    @Test
    public void acroFormCheck03() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    @Test(expected = PdfAConformanceException.class)
    public void acroFormCheck04() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.XFA, new PdfArray());
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    @Test
    public void acroFormCheck05() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.XFA, new PdfArray());
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }
}
