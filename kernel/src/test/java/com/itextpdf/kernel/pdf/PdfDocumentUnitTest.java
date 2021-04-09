/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfType3Font;
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
        junitExpectedException.expect(PdfException.class);

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "pdfWithMetadata.pdf"));
        pdfDocument.close();

        pdfDocument.getDocumentInfo();

        Assert.fail();
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
}
