/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.events.utils.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfDocumentUnitTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentUnitTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)
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


        Assert.assertNull(initialFontDict.getIndirectReference());
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // prevent no pages exception on close
            doc.addNewPage();

            PdfType3Font font1 = (PdfType3Font) doc.getFont(initialFontDict);
            Assert.assertNotNull(font1);

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
            @LogMessage(messageTemplate = LogMessageConstant.DOCUMENT_HAS_CONFLICTING_OCG_NAMES, count = 3),
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
            @LogMessage(messageTemplate = LogMessageConstant.OCG_COPYING_ERROR, logLevel = LogLevelConstants.ERROR)
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

                Assert.assertNotNull(outDocument.getCatalog());
                PdfOCProperties ocProperties = outDocument.getCatalog().getOCProperties(false);
                Assert.assertNotNull(ocProperties);
                Assert.assertEquals(1, ocProperties.getLayers().size());
                PdfLayer layer = ocProperties.getLayers().get(0);
                Assert.assertTrue(layer.getPdfObject().isFlushed());
            }
        }
    }

    @Test
    public void pdfDocumentInstanceNoWriterInfoAndConformanceLevelInitialization() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf"));

        Assert.assertNull(pdfDocument.info);
        Assert.assertNull(pdfDocument.reader.pdfAConformanceLevel);

        pdfDocument.close();

        Assert.assertNull(pdfDocument.info);
        Assert.assertNull(pdfDocument.reader.pdfAConformanceLevel);
    }

    @Test
    public void pdfDocumentInstanceWriterInfoAndConformanceLevelInitialization() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "pdfWithMetadata.pdf"), new PdfWriter(new ByteArrayOutputStream()));

        Assert.assertNotNull(pdfDocument.info);
        Assert.assertNull(pdfDocument.reader.pdfAConformanceLevel);

        pdfDocument.close();

        Assert.assertNotNull(pdfDocument.info);
        Assert.assertNull(pdfDocument.reader.pdfAConformanceLevel);
    }

    @Test
    public void extendedPdfDocumentNoWriterInfoAndConformanceLevelInitialization() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf")) {
            // This class instance extends pdfDocument
        };

        // TODO DEVSIX-5292 These fields shouldn't be initialized during the document's opening
        Assert.assertNotNull(pdfDocument.info);
        Assert.assertNotNull(pdfDocument.reader.pdfAConformanceLevel);

        pdfDocument.close();

        Assert.assertNotNull(pdfDocument.info);
        Assert.assertNotNull(pdfDocument.reader.pdfAConformanceLevel);
    }

    @Test
    public void extendedPdfDocumentWriterInfoAndConformanceLevelInitialization() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(sourceFolder + "pdfWithMetadata.pdf"), new PdfWriter(new ByteArrayOutputStream())) {
            // This class instance extends pdfDocument
        };

        Assert.assertNotNull(pdfDocument.info);
        // TODO DEVSIX-5292 pdfAConformanceLevel shouldn't be initialized during the document's opening
        Assert.assertNotNull(pdfDocument.reader.pdfAConformanceLevel);

        pdfDocument.close();

        Assert.assertNotNull(pdfDocument.info);
        Assert.assertNotNull(pdfDocument.reader.pdfAConformanceLevel);
    }

    @Test
    public void getDocumentInfoAlreadyClosedTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf"));
        pdfDocument.close();

        Assert.assertThrows(PdfException.class, () -> pdfDocument.getDocumentInfo());
    }

    @Test
    public void getDocumentInfoNotInitializedTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf"));

        Assert.assertNull(pdfDocument.info);
        Assert.assertNotNull(pdfDocument.getDocumentInfo());

        pdfDocument.close();
    }

    @Test
    public void getPdfAConformanceLevelNotInitializedTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf"));

        Assert.assertNull(pdfDocument.reader.pdfAConformanceLevel);
        Assert.assertNotNull(pdfDocument.reader.getPdfAConformanceLevel());

        pdfDocument.close();
    }

    private static void assertLayerNames(PdfDocument outDocument, List<String> layerNames) {
        Assert.assertNotNull(outDocument.getCatalog());
        PdfOCProperties ocProperties = outDocument.getCatalog().getOCProperties(true);
        Assert.assertNotNull(ocProperties);
        Assert.assertEquals(layerNames.size(), ocProperties.getLayers().size());
        for (int i = 0; i < layerNames.size(); i++) {
            PdfLayer layer = ocProperties.getLayers().get(i);
            Assert.assertNotNull(layer);
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
        Assert.assertNotNull(layerDictionary);
        Assert.assertNotNull(layerDictionary.get(PdfName.Name));
        String layerNameString = layerDictionary.get(PdfName.Name).toString();
        Assert.assertEquals(name, layerNameString);
    }

    @Test
    public void cannotGetTagStructureForUntaggedDocumentTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.getTagStructureContext();
    }

    @Test
    public void cannotAddPageAfterDocumentIsClosedTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException
                .expectMessage(KernelExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage(1);
        pdfDoc.close();
        pdfDoc.addNewPage(2);
    }

    @Test
    public void cannotMovePageToZeroPositionTest() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        junitExpectedException.expectMessage(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 0));

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        pdfDoc.movePage(1, 0);
    }

    @Test
    public void cannotMovePageToNegativePosition() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        junitExpectedException.expectMessage(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, -1));

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        pdfDoc.movePage(1, -1);
    }

    @Test
    public void cannotMovePageToOneMorePositionThanPagesNumberTest() {
        junitExpectedException.expect(IndexOutOfBoundsException.class);
        junitExpectedException.expectMessage(
                MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 3));

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.addNewPage();
        pdfDoc.movePage(1, 3);
    }

    @Test
    public void cannotAddPageToAnotherDocumentTest01() {
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc1.addNewPage(1);

        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(
                KernelExceptionMessageConstant.PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT,
                pdfDoc1,
                1,
                pdfDoc2));

        pdfDoc2.checkAndAddPage(1, pdfDoc1.getPage(1));
    }

    @Test
    public void cannotAddPageToAnotherDocumentTest02() {
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfDocument pdfDoc2 = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc1.addNewPage(1);

        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(
                KernelExceptionMessageConstant.PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT,
                pdfDoc1,
                1,
                pdfDoc2));

        pdfDoc2.checkAndAddPage(pdfDoc1.getPage(1));
    }

    @Test
    public void cannotSetEncryptedPayloadInReadingModeTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_DOCUMENT_OPENED_IN_READING_MODE);

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "setEncryptedPayloadInReadingModeTest.pdf"));
        pdfDoc.setEncryptedPayload(null);
    }

    @Test
    public void cannotSetEncryptedPayloadToEncryptedDocTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(
                KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_ENCRYPTED_DOCUMENT);

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setStandardEncryption(new byte[] {}, new byte[] {}, 1, 1);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream(), writerProperties));
        PdfFileSpec fs = PdfFileSpec
                .createExternalFileSpec(pdfDoc, sourceFolder + "testPath");
        pdfDoc.setEncryptedPayload(fs);
    }
}
