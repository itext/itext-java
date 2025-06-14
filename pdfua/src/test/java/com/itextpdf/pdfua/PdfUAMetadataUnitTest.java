/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;
import com.itextpdf.pdfua.checkers.PdfUA2Checker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfUAMetadataUnitTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAMetadataUnitTest/";

    @Test
    public void documentWithNoTitleInMetadataTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_title_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidMetadataVersionTest() throws IOException {
        PdfDocument pdfDocument = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();

        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "invalid_version_metadata.xmp"));
        catalog.put(PdfName.Metadata, new PdfStream(bytes));

        PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER,
                e.getMessage());
        e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER,
                e.getMessage());
    }

    @Test
    public void documentWithNoMetadataVersionTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();


            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_version_metadata.xmp"));
            catalog.put(PdfName.Metadata, new PdfStream(bytes));

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidMetadataTypeTest() {
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.put(PdfName.Metadata, new PdfDictionary());

            PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidPdfVersionTest() {
        PdfDocumentCustomVersion pdfDocument = new PdfDocumentCustomVersion(
                new PdfWriter(new ByteArrayOutputStream()), new PdfUAConfig(
                PdfUAConformance.PDF_UA_1, "en-us", "title"));
        pdfDocument.setPdfVersion(PdfVersion.PDF_2_0);
        pdfDocument.addNewPage();
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.INVALID_PDF_VERSION, e.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA)
    })
    public void documentWithBrokenMetadataTest() throws IOException {
        PdfDocument pdfDocument = new PdfUATestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();


        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "invalid_metadata.xmp"));
        catalog.put(PdfName.Metadata, new PdfStream(bytes));

        PdfUA1MetadataChecker checker = new PdfUA1MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM,
                e.getMessage());

        e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM,
                e.getMessage());
    }

    @Test
    public void validMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            metadata.put(PdfName.Type, PdfName.Metadata);
            metadata.put(PdfName.Subtype, PdfName.XML);
            catalog.put(PdfName.Metadata, metadata);

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            AssertUtil.doesNotThrow(() -> checker.checkMetadata(catalog));
        }
    }

    @Test
    public void catalogNoMetadataUA2Test() {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.remove(PdfName.Metadata);

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(KernelExceptionMessageConstant.METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY,
                    e.getMessage());
        }
    }

    @Test
    public void catalogInvalidMetadataUA2Test() {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.put(PdfName.Metadata, new PdfString("Error"));

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class,
                    () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA)
    })
    public void brokenMetadataUA2Test() throws IOException {
        PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();

        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "invalid_metadata_ua2.xmp"));
        PdfStream metadata = new PdfStream(bytes);
        metadata.put(PdfName.Type, PdfName.Metadata);
        metadata.put(PdfName.Subtype, PdfName.XML);
        catalog.put(PdfName.Metadata, metadata);

        PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e.getMessage());
        e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e.getMessage());
    }

    @Test
    public void documentWithNoPartInMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_no_part_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            metadata.put(PdfName.Type, PdfName.Metadata);
            metadata.put(PdfName.Subtype, PdfName.XML);
            catalog.put(PdfName.Metadata, metadata);

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, 2, null),
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidPartInMetadataUA2Test() throws IOException {
        PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();

        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_invalid_part_ua2.xmp"));
        PdfStream metadata = new PdfStream(bytes);
        metadata.put(PdfName.Type, PdfName.Metadata);
        metadata.put(PdfName.Subtype, PdfName.XML);
        catalog.put(PdfName.Metadata, metadata);

        PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, 2, 1),
                e.getMessage());
        Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
    }

    @Test
    public void documentWithNoRevInMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_no_rev_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            metadata.put(PdfName.Type, PdfName.Metadata);
            metadata.put(PdfName.Subtype, PdfName.XML);
            catalog.put(PdfName.Metadata, metadata);

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                    e.getMessage());
        }
    }

    @Test
    public void documentWithInvalidRevInMetadataUA2Test() throws IOException {
        PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();

        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_invalid_rev_ua2.xmp"));
        PdfStream metadata = new PdfStream(bytes);
        metadata.put(PdfName.Type, PdfName.Metadata);
        metadata.put(PdfName.Subtype, PdfName.XML);
        catalog.put(PdfName.Metadata, metadata);

        PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
        Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
    }

    @Test
    public void documentWithInvalidLengthRevInMetadataUA2Test() throws IOException {
        PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfCatalog catalog = pdfDocument.getCatalog();

        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_invalid_len_rev_ua2.xmp"));
        PdfStream metadata = new PdfStream(bytes);
        metadata.put(PdfName.Type, PdfName.Metadata);
        metadata.put(PdfName.Subtype, PdfName.XML);
        catalog.put(PdfName.Metadata, metadata);

        PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
        Assertions.assertEquals(KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                e.getMessage());
        Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
    }

    @Test
    public void documentWithNoTitleInMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfUA2TestPdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_title_metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            metadata.put(PdfName.Type, PdfName.Metadata);
            metadata.put(PdfName.Subtype, PdfName.XML);
            catalog.put(PdfName.Metadata, metadata);

            PdfUA2MetadataChecker checker = new PdfUA2MetadataChecker(pdfDocument);
            Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY,
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

    private static class PdfUA2MetadataChecker extends PdfUA2Checker {
        /**
         * Creates PdfUA2Checker instance with PDF document which will be validated against PDF/UA-2 standard.
         *
         * @param pdfDocument the document to validate
         */
        public PdfUA2MetadataChecker(PdfDocument pdfDocument) {
            super(pdfDocument);
        }

        @Override
        public void checkMetadata(PdfCatalog catalog) {
            super.checkMetadata(catalog);
        }
    }

    private static class PdfDocumentCustomVersion extends PdfUADocument {
        public PdfDocumentCustomVersion(PdfWriter writer, PdfUAConfig config) {
            super(writer, config);
        }

        public void setPdfVersion(PdfVersion pdfVersion) {
            this.pdfVersion = pdfVersion;
        }
    }
}
