package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(UnitTest.class)
public class PdfADocumentTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void checkCadesSignatureTypeIsoConformance() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument document = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        document.checkIsoConformance(true, IsoKey.SIGNATURE_TYPE, null, null);
    }

    @Test
    public void checkCMSSignatureTypeIsoConformance() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = new FileInputStream(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument document = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> document.checkIsoConformance(false, IsoKey.SIGNATURE_TYPE, null, null));
        Assert.assertEquals(PdfaExceptionMessageConstant.SIGNATURE_SHALL_CONFORM_TO_ONE_OF_THE_PADES_PROFILE, e.getMessage());
    }
}
