/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class PdfDocumentUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentUnitTest/";

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)
    })
    public void getFontWithDirectFontDictionaryTest() {
        PdfDictionary initialFontDict = new PdfDictionary();
        initialFontDict.put(PdfName.Subtype, PdfName.Type3);
        initialFontDict.put(PdfName.FontMatrix, new PdfArray(new float[]{0.001F, 0, 0, 0.001F, 0, 0}));
        initialFontDict.put(PdfName.Widths, new PdfArray());
        PdfDictionary encoding = new PdfDictionary();
        initialFontDict.put(PdfName.Encoding, encoding);
        PdfArray differences = new PdfArray();
        differences.add(new PdfNumber(AdobeGlyphList.nameToUnicode("a")));
        differences.add(new PdfName("a"));
        encoding.put(PdfName.Differences, differences);


        Assertions.assertNull(initialFontDict.getIndirectReference());
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // prevent no pages exception on close
            doc.addNewPage();

            PdfType3Font font1 = (PdfType3Font) doc.getFont(initialFontDict);
            Assertions.assertNotNull(font1);

            // prevent no glyphs for type3 font on close
            font1.addGlyph('a', 0, 0, 0, 0, 0);
        }
    }

    @Test
    public void copyPagesWithOCGDifferentNames() throws IOException {
        List<List<String>> ocgNames = new ArrayList<>();
        List<String> ocgNames1 = new ArrayList<>();
        ocgNames1.add("Name1");
        List<String> ocgNames2 = new ArrayList<>();
        ocgNames2.add("Name2");
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames2);
        List<byte[]> sourceDocuments = PdfDocumentUnitTest.initSourceDocuments(ocgNames);

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (byte[] docBytes : sourceDocuments) {
                try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                    for (int i = 1; i <= fromDocument.getNumberOfPages(); i++) {
                        fromDocument.copyPagesTo(i, i, outDocument);
                    }
                }
            }
            List<String> layerNames = new ArrayList<>();
            layerNames.add("Name1");
            layerNames.add("Name2");
            PdfDocumentUnitTest.assertLayerNames(outDocument, layerNames);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES, count = 3),
    })
    public void copyPagesWithOCGSameName() throws IOException {
        List<List<String>> ocgNames = new ArrayList<>();
        List<String> ocgNames1 = new ArrayList<>();
        ocgNames1.add("Name1");
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        ocgNames.add(ocgNames1);
        List<byte[]> sourceDocuments = PdfDocumentUnitTest.initSourceDocuments(ocgNames);

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            for (byte[] docBytes : sourceDocuments) {
                try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                    for (int i = 1; i <= fromDocument.getNumberOfPages(); i++) {
                        fromDocument.copyPagesTo(i, i, outDocument);
                    }
                }
            }
            List<String> layerNames = new ArrayList<>();
            layerNames.add("Name1");
            layerNames.add("Name1_0");
            layerNames.add("Name1_1");
            layerNames.add("Name1_2");
            PdfDocumentUnitTest.assertLayerNames(outDocument, layerNames);
        }
    }

    @Test
    public void copyPagesWithOCGSameObject() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.put(PdfName.Name, new PdfString("name1"));
                ocg.makeIndirect(document);
                pdfResource.addProperties(ocg);
                PdfPage page2 = document.addNewPage();
                PdfResources pdfResource2 = page2.getResources();
                pdfResource2.addProperties(ocg);
                document.getCatalog().getOCProperties(true);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument outDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                fromDocument.copyPagesTo(1, fromDocument.getNumberOfPages(), outDocument);
            }

            List<String> layerNames = new ArrayList<>();
            layerNames.add("name1");
            PdfDocumentUnitTest.assertLayerNames(outDocument, layerNames);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.OCG_COPYING_ERROR, logLevel = LogLevelConstants.ERROR)
    })
    public void copyPagesFlushedResources() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.put(PdfName.Name, new PdfString("name1"));
                ocg.makeIndirect(document);
                pdfResource.addProperties(ocg);
                pdfResource.makeIndirect(document);
                PdfPage page2 = document.addNewPage();
                page2.setResources(pdfResource);
                document.getCatalog().getOCProperties(true);
            }
            docBytes = outputStream.toByteArray();
        }

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        try (PdfDocument outDocument = new PdfDocument(writer)) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
                fromDocument.copyPagesTo(1, 1, outDocument);

                List<String> layerNames = new ArrayList<>();
                layerNames.add("name1");
                PdfDocumentUnitTest.assertLayerNames(outDocument, layerNames);

                outDocument.flushCopiedObjects(fromDocument);
                fromDocument.copyPagesTo(2, 2, outDocument);

                Assertions.assertNotNull(outDocument.getCatalog());
                PdfOCProperties ocProperties = outDocument.getCatalog().getOCProperties(false);
                Assertions.assertNotNull(ocProperties);
                Assertions.assertEquals(1, ocProperties.getLayers().size());
                PdfLayer layer = ocProperties.getLayers().get(0);
                Assertions.assertTrue(layer.getPdfObject().isFlushed());
            }
        }
    }

    @Test
    public void getDocumentInfoAlreadyClosedTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "pdfWithMetadata.pdf"));
        pdfDocument.close();

        Assertions.assertThrows(PdfException.class, () -> pdfDocument.getDocumentInfo());
    }

    @Test
    public void getDocumentInfoInitializationTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "pdfWithMetadata.pdf"));
        Assertions.assertNotNull(pdfDocument.getDocumentInfo());
        pdfDocument.close();
    }

    @Test
    public void getPdfAConformanceLevelInitializationTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + "pdfWithMetadata.pdf"));
        Assertions.assertTrue(pdfDocument.reader.getPdfConformance().isPdfAOrUa());
        pdfDocument.close();
    }

    private static void assertLayerNames(PdfDocument outDocument, List<String> layerNames) {
        Assertions.assertNotNull(outDocument.getCatalog());
        PdfOCProperties ocProperties = outDocument.getCatalog().getOCProperties(true);
        Assertions.assertNotNull(ocProperties);
        Assertions.assertEquals(layerNames.size(), ocProperties.getLayers().size());
        for (int i = 0; i < layerNames.size(); i++) {
            PdfLayer layer = ocProperties.getLayers().get(i);
            Assertions.assertNotNull(layer);
            PdfDocumentUnitTest.assertLayerNameEqual(layerNames.get(i), layer);
        }
    }


    private static List<byte[]> initSourceDocuments(List<List<String>> ocgNames) throws IOException {
        List<byte[]> result = new ArrayList<>();
        for(List<String> names: ocgNames) {
            result.add(PdfDocumentUnitTest.initDocument(names));
        }
        return result;
    }

    private static byte[] initDocument(List<String> names) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = document.addNewPage();
                PdfResources pdfResource = page.getResources();
                for (String name : names) {
                    PdfDictionary ocg = new PdfDictionary();
                    ocg.put(PdfName.Type, PdfName.OCG);
                    ocg.put(PdfName.Name, new PdfString(name));
                    ocg.makeIndirect(document);
                    pdfResource.addProperties(ocg);
                }
                document.getCatalog().getOCProperties(true);
            }
            return outputStream.toByteArray();
        }
    }

    private static void assertLayerNameEqual(String name, PdfLayer layer) {
        PdfDictionary layerDictionary = layer.getPdfObject();
        Assertions.assertNotNull(layerDictionary);
        Assertions.assertNotNull(layerDictionary.get(PdfName.Name));
        String layerNameString = layerDictionary.get(PdfName.Name).toString();
        Assertions.assertEquals(name, layerNameString);
    }

    @Test
    public void cannotGetTagStructureForUntaggedDocumentTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.getTagStructureContext());
        Assertions.assertEquals(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT, exception.getMessage());
    }

    @Test
    public void cannotAddPageAfterDocumentIsClosedTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage(1);
        pdfDoc.close();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.addNewPage(2));
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION,
                exception.getMessage());
    }

    @Test
    public void cannotMovePageToZeroPositionTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> pdfDoc.movePage(1, 0));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 0),
                exception.getMessage());
    }

    @Test
    public void cannotMovePageToNegativePosition() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> pdfDoc.movePage(1, -1));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, -1),
                exception.getMessage());
    }

    @Test
    public void cannotMovePageToOneMorePositionThanPagesNumberTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> pdfDoc.movePage(1, 3));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 3),
                exception.getMessage());
    }

    @Test
    public void cannotAddPageToAnotherDocumentTest() {
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc1.addNewPage(1);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc2.checkAndAddPage(1, pdfDoc1.getPage(1)));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT,
                pdfDoc1, 1, pdfDoc2), exception.getMessage());
    }

    @Test
    public void cannotAddPageToAnotherDocTest() {
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc1.addNewPage(1);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc2.checkAndAddPage(pdfDoc1.getPage(1)));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT,
                pdfDoc1, 1, pdfDoc2), exception.getMessage());
    }

    @Test
    public void cannotSetEncryptedPayloadInReadingModeTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "setEncryptedPayloadInReadingModeTest.pdf"));
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.setEncryptedPayload(null));
        Assertions.assertEquals(
                KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_DOCUMENT_OPENED_IN_READING_MODE,
                exception.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void cannotSetEncryptedPayloadToEncryptedDocTest() {
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setStandardEncryption(new byte[] {}, new byte[] {}, 1, 1);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), writerProperties));
        PdfFileSpec fs = PdfFileSpec
                .createExternalFileSpec(pdfDoc, SOURCE_FOLDER + "testPath");
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfDoc.setEncryptedPayload(fs));
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_ENCRYPTED_DOCUMENT,
                exception.getMessage());
    }

    @Test
    public void checkEmptyIsoConformanceTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            IValidationContext validationContext = new PdfDocumentValidationContext(doc, doc.getDocumentFonts());
            AssertUtil.doesNotThrow(() -> doc.checkIsoConformance(validationContext));
        }
    }

    @Test
    public void checkIsoConformanceTest() {
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ValidationContainer container = new ValidationContainer();
            final CustomValidationChecker checker = new CustomValidationChecker();
            container.addChecker(checker);
            doc.getDiContainer().register(ValidationContainer.class, container);
            Assertions.assertFalse(checker.documentValidationPerformed);
            IValidationContext validationContext = new PdfDocumentValidationContext(doc, doc.getDocumentFonts());
            doc.checkIsoConformance(validationContext);
            Assertions.assertTrue(checker.documentValidationPerformed);
        }
    }

    private static class CustomValidationChecker implements IValidationChecker {
        public boolean documentValidationPerformed = false;

        @Override
        public void validate(IValidationContext validationContext) {
            if (validationContext.getType() == ValidationType.PDF_DOCUMENT) {
                documentValidationPerformed = true;
            }
        }
    }
}
