package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfA1EmbeddedFilesCheckTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fileSpecCheckTest01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.NameDictionaryShallNotContainTheEmbeddedFilesKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfDictionary fileNames = new PdfDictionary();
        pdfDocument.getCatalog().put(PdfName.Names, fileNames);

        PdfDictionary embeddedFiles = new PdfDictionary();
        fileNames.put(PdfName.EmbeddedFiles, embeddedFiles);

        PdfArray names = new PdfArray();
        fileNames.put(PdfName.Names, names);

        names.add(new PdfString("some/file/path"));
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null, true);
        names.add(spec.getPdfObject());

        pdfDocument.addNewPage();

        pdfDocument.close();
    }

    @Test
    public void fileSpecCheckTest02() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.StreamObjDictShallNotContainForFFilterOrFDecodeParams);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null,  true);
        stream.put(PdfName.F, spec.getPdfObject());

        pdfDocument.addNewPage();

        pdfDocument.close();
    }

    @Test
    public void fileSpecCheckTest03() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.FileSpecificationDictionaryShallNotContainTheEFKey);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", "sample", null, null,  true);
        stream.put(new PdfName("fileData"), spec.getPdfObject());

        pdfDocument.addNewPage();

        pdfDocument.close();
    }
}
