package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class PdfA1AcroFormCheckTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA1AcroFormCheckTest/";
    public static final String destinationFolder = "./target/test/PdfA1AcroFormCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void acroFormCheck01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.NeedAppearancesFlagOfTheInteractiveFormDictionaryShallEitherNotBePresentedOrShallBeFalse);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.NeedAppearances, new PdfBoolean(true));
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
    }

    @Test
    public void acroFormCheck02() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_acroFormCheck02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_acroFormCheck02.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        acroForm.put(PdfName.NeedAppearances, new PdfBoolean(false));
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void acroFormCheck03() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_acroFormCheck03.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1b_acroFormCheck03.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        doc.addNewPage();
        PdfDictionary acroForm = new PdfDictionary();
        doc.getCatalog().put(PdfName.AcroForm, acroForm);

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
