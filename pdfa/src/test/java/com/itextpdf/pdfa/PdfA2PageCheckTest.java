package com.itextpdf.pdfa;

import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA2PageCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test(expected = PdfAConformanceException.class)
    public void catalogCheck01() throws FileNotFoundException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        page.getPdfObject().put(PdfName.PresSteps, new PdfDictionary());

        doc.close();
    }
}
