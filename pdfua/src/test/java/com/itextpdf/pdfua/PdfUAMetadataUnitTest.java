package com.itextpdf.pdfua;

import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Category(UnitTest.class)
public class PdfUAMetadataUnitTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAMetadataUnitTest/";

    @Test
    public void documentWithNoTitleInMetadataTest() throws IOException, InterruptedException, XMPException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_title_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assert.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidMetadataVersionTest() throws IOException, InterruptedException, XMPException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "invalid_version_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assert.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithNoMetadataVersionTest() throws IOException, InterruptedException, XMPException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_version_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assert.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidMetadataTypeTest() throws IOException, InterruptedException, XMPException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.put(PdfName.Metadata, new PdfDictionary());

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assert.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidPdfVersionTest() throws IOException, InterruptedException, XMPException {
        PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(),
                        PdfUATestPdfDocument.createWriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.addNewPage();
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.INVALID_PDF_VERSION,
                    e.getMessage());
    }

    @Test
    public void documentWithBrokenMetadataTest() throws IOException, InterruptedException, XMPException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "invalid_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assert.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM,
                    e.getMessage());
        }
    }

    private static class PdfUA1MetadataChecker extends PdfUA1Checker {
        /**
         * Creates PdfUA1Checker instance with PDF document which will be validated against PDF/UA-1 standard.
         *
         * @param pdfDocument the document to validate
         */
        public PdfUA1MetadataChecker(PdfDocument pdfDocument) {
            super(pdfDocument);
        }

        @Override
        public void checkMetadata(PdfCatalog catalog) {
            super.checkMetadata(catalog);
        }
    }
}
