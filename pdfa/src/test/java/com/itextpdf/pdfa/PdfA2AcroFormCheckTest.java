package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

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
public class PdfA2AcroFormCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2AcroFormCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2AcroFormCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test(expected = PdfAConformanceException.class)
    public void acroFormCheck01() throws FileNotFoundException, XMPException {
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
    public void acroFormCheck02() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_acroFormCheck02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_acroFormCheck02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.NeedAppearances, new PdfBoolean(false));
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void acroFormCheck03() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_acroFormCheck03.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_acroFormCheck03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void acroFormCheck04() throws FileNotFoundException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.TheInteractiveFormDictionaryShallNotContainTheXfaKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.XFA, new PdfArray());
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
