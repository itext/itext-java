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
package com.itextpdf.kernel.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

@Tag("UnitTest")
public class PdfCheckerTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/validation/PdfCheckerTest/";

    private static final Function<String, PdfException> EXCEPTION_SUPPLIER = (msg) -> new PdfException(msg);

    @Test
    public void invalidTypeSubtypeMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            catalog.put(PdfName.Metadata, metadata);

            Pdf20Checker checker = new Pdf20Checker();
            Exception e = Assertions.assertThrows(Pdf20ConformanceException.class,
                    () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.METADATA_STREAM_REQUIRES_METADATA_TYPE_AND_XML_SUBTYPE,
                    e.getMessage());

            metadata.put(PdfName.Type, PdfName.XML);
            e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.METADATA_STREAM_REQUIRES_METADATA_TYPE_AND_XML_SUBTYPE,
                    e.getMessage());

            metadata.put(PdfName.Type, PdfName.Metadata);
            e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.METADATA_STREAM_REQUIRES_METADATA_TYPE_AND_XML_SUBTYPE,
                    e.getMessage());

            metadata.put(PdfName.Type, PdfName.Metadata);
            metadata.put(PdfName.Subtype, PdfName.Metadata);
            e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> checker.checkMetadata(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.METADATA_STREAM_REQUIRES_METADATA_TYPE_AND_XML_SUBTYPE,
                    e.getMessage());
        }
    }

    @Test
    public void noMetadataUA2Test() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
            Assertions.assertEquals(KernelExceptionMessageConstant.METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY,
                    e.getMessage());
        }
    }

    @Test
    public void notStreamMetadataUA2Test() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.put(PdfName.Metadata, PdfName.Metadata);

            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
            Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA)})
    public void brokenMetadataUA2Test() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.put(PdfName.Metadata, new PdfStream(new byte[]{1, 2, 3}));

            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
            Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e.getMessage());
        }
    }

    @Test
    public void noPartInMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_version_metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            catalog.put(PdfName.Metadata, metadata);
            catalog.put(PdfName.Type, PdfName.Metadata);
            catalog.put(PdfName.Subtype, PdfName.XML);

            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
            Assertions.assertEquals(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART, 2, null),
                    e.getMessage());
        }
    }

    @Test
    public void noRevInMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "no_revision_metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            catalog.put(PdfName.Metadata, metadata);
            catalog.put(PdfName.Type, PdfName.Metadata);
            catalog.put(PdfName.Subtype, PdfName.XML);

            Exception e = Assertions.assertThrows(PdfException.class, () ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV,
                    e.getMessage());
        }
    }

    @Test
    public void validMetadataUA2Test() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();
            PdfCatalog catalog = pdfDocument.getCatalog();

            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "metadata_ua2.xmp"));
            PdfStream metadata = new PdfStream(bytes);
            catalog.put(PdfName.Metadata, metadata);
            catalog.put(PdfName.Type, PdfName.Metadata);
            catalog.put(PdfName.Subtype, PdfName.XML);

            AssertUtil.doesNotThrow(() ->
                    PdfCheckersUtil.checkMetadata(catalog.getPdfObject(), PdfConformance.PDF_UA_2, EXCEPTION_SUPPLIER));
        }
    }

    @Test
    public void validLangTest() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();

            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.setLang(new PdfString("en-US"));

            Pdf20Checker checker = new Pdf20Checker();
            AssertUtil.doesNotThrow(() -> checker.checkLang(catalog));
        }
    }

    @Test
    public void emptyLangTest() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();

            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.setLang(new PdfString(""));

            Pdf20Checker checker = new Pdf20Checker();
            AssertUtil.doesNotThrow(() -> checker.checkLang(catalog));
        }
    }

    @Test
    public void invalidLangTest() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            pdfDocument.addNewPage();

            PdfCatalog catalog = pdfDocument.getCatalog();
            catalog.setLang(new PdfString("inva:lid"));

            Pdf20Checker checker = new Pdf20Checker();
            Exception e = Assertions.assertThrows(Pdf20ConformanceException.class,
                    () -> checker.checkLang(catalog));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY,
                    e.getMessage());
        }
    }
}