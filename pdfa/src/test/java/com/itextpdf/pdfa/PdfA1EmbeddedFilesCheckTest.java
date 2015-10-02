package com.itextpdf.pdfa;

import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;
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
public class PdfA1EmbeddedFilesCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

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
        pdfDocument.setXmpMetadata();

        PdfDictionary fileNames = new PdfDictionary();
        pdfDocument.getCatalog().getPdfObject().put(PdfName.Names, fileNames);

        PdfDictionary embeddedFiles = new PdfDictionary();
        fileNames.put(PdfName.EmbeddedFiles, embeddedFiles);

        PdfArray names = new PdfArray();
        fileNames.put(PdfName.Names, names);

        names.add(new PdfString("some/file/path"));
        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", true);
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
        pdfDocument.setXmpMetadata();

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().getPdfObject().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", true);
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
        pdfDocument.setXmpMetadata();

        PdfStream stream = new PdfStream();
        pdfDocument.getCatalog().getPdfObject().put(new PdfName("testStream"), stream);

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, sourceFolder + "sample.wav", "sample.wav", true);
        stream.put(new PdfName("fileData"), spec.getPdfObject());

        pdfDocument.addNewPage();

        pdfDocument.close();
    }
}
