package com.itextpdf.pdfa;

import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfA2EmbeddedFilesCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fileSpecCheckTest01() throws FileNotFoundException, XMPException {
//        thrown.expect(PdfAConformanceException.class);
//        thrown.expectMessage(PdfAConformanceException.NameDictionaryShallNotContainTheEmbeddedFilesKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);
        pdfDocument.setXmpMetadata();

        pdfDocument.addNewPage();

        //TODO port tests when some logic to add file attachments will be implemented

        pdfDocument.close();
    }
}
