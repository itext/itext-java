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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.InvalidXRefPrevException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.XrefCycledReferencesException;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Tag("IntegrationTest")
public class PdfReaderTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfReaderTest/";

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";
    static final String title = "Empty iText 6 Document";

    static final byte[] USER_PASSWORD = "Hello".getBytes(StandardCharsets.ISO_8859_1);

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void openSimpleDoc() throws IOException {
        String filename = DESTINATION_FOLDER + "openSimpleDoc.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfReader reader = CompareTool.createOutputReader(filename);
        pdfDoc = new PdfDocument(reader);
        Assertions.assertEquals(author, pdfDoc.getDocumentInfo().getAuthor());
        Assertions.assertEquals(creator, pdfDoc.getDocumentInfo().getCreator());
        Assertions.assertEquals(title, pdfDoc.getDocumentInfo().getTitle());
        PdfObject object = pdfDoc.getPdfObject(1);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, pdfDoc.getPdfObject(5).getType());

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        pdfDoc.close();
    }

    @Test
    public void openSimpleDocWithFullCompression() throws IOException {
        String filename = SOURCE_FOLDER + "simpleCanvasWithFullCompression.pdf";
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfObject object = pdfDoc.getPdfObject(1);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        object = pdfDoc.getPdfObject(5);
        Assertions.assertEquals(PdfObject.STREAM, object.getType());
        String content = "100 100 100 100 re\nf\n";
        Assertions.assertArrayEquals(ByteUtils.getIsoBytes(content), ((PdfStream) object).getBytes());

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void objectStreamIncrementalUpdateReading() throws IOException {
         /*
             This test ensures that if certain object stored in objects streams
             has incremental updates, the right object instance is found and initialized
             even if the object stream with the older object's increment is read as well.

             One peculiar thing covered by this test is that older object increment contains
             indirect refernce to the object number 8 which is freed in document incremental
             update. Such document and particulary this object is perfectly valid.
         */

        String filename = SOURCE_FOLDER + "objectStreamIncrementalUpdate.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDictionary catalogDict = pdfDoc.getCatalog().getPdfObject();
        PdfDictionary customDict1 = catalogDict.getAsDictionary(new PdfName("CustomDict1"));
        PdfDictionary customDict2 = catalogDict.getAsDictionary(new PdfName("CustomDict2"));

        Assertions.assertEquals(1, customDict1.size());
        Assertions.assertEquals(1, customDict2.size());

        Assertions.assertEquals("Hello world updated.", customDict1.getAsString(new PdfName("Key1")).getValue());
        Assertions.assertEquals("Hello world for second dictionary.",
                customDict2.getAsString(new PdfName("Key1")).getValue());

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        pdfDoc.close();
    }

    @Test
    public void rereadReleasedObjectFromObjectStream() throws IOException {
        String filename = SOURCE_FOLDER + "twoCustomDictionariesInObjectStream.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDictionary catalogDict = pdfDoc.getCatalog().getPdfObject();
        PdfDictionary customDict1 = catalogDict.getAsDictionary(new PdfName("CustomDict1"));
        PdfDictionary customDict2 = catalogDict.getAsDictionary(new PdfName("CustomDict2"));

        Assertions.assertTrue(customDict1.containsKey(new PdfName("CustomDict1Key1")));
        Assertions.assertTrue(customDict2.containsKey(new PdfName("CustomDict2Key1")));

        customDict2.clear();
        customDict1.release();

        // reread released dictionary and also modified dictionary
        customDict1 = catalogDict.getAsDictionary(new PdfName("CustomDict1"));
        customDict2 = catalogDict.getAsDictionary(new PdfName("CustomDict2"));

        Assertions.assertTrue(customDict1.containsKey(new PdfName("CustomDict1Key1")));
        Assertions.assertFalse(customDict2.containsKey(new PdfName("CustomDict2Key1")));

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        pdfDoc.close();
    }

    @Test
    public void openDocWithFlateFilter() throws IOException {
        String filename = SOURCE_FOLDER + "100PagesDocumentWithFlateFilter.pdf";
        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assertions.assertEquals(100, document.getNumberOfPages(), "Page count");

        String contentTemplate = "q\n" +
                "BT\n" +
                "36 700 Td\n" +
                "/F1 72 Tf\n" +
                "({0})Tj\n" +
                "ET\n" +
                "Q\n" +
                "100 500 100 100 re\n" +
                "f\n";

        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            PdfPage page = document.getPage(i);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(MessageFormatUtil.format(contentTemplate, i), new String(content), "Page content " + i);
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");
        document.close();
    }

    @Test
    public void primitivesRead() throws IOException {
        String filename = DESTINATION_FOLDER + "primitivesRead.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.put(new PdfName("a"), new PdfBoolean(true).makeIndirect(document));
        document.close();

        PdfReader reader = CompareTool.createOutputReader(filename);
        document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = document.getXref().get(4).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        object = document.getXref().get(6).getRefersTo();
        Assertions.assertEquals(PdfObject.BOOLEAN, object.getType());
        Assertions.assertNotNull(object.getIndirectReference());

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void indirectsChain1() throws IOException {
        String filename = DESTINATION_FOLDER + "indirectsChain1.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject pdfObject = getTestPdfDictionary();
        for (int i = 0; i < 5; i++) {
            pdfObject = pdfObject.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), pdfObject);
        document.close();

        PdfReader reader = CompareTool.createOutputReader(filename);
        document = new PdfDocument(reader);

        pdfObject = document.getXref().get(1).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Catalog));

        pdfObject = document.getXref().get(2).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Pages));

        pdfObject = document.getXref().get(3).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());

        pdfObject = document.getXref().get(4).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        for (int i = 6; i < document.getXref().size(); i++) {
            Assertions.assertEquals(PdfObject.DICTIONARY, document.getXref().get(i).getRefersTo().getType());
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void exponentialXObjectLoopTest() throws IOException {
        String fileName = SOURCE_FOLDER + "exponentialXObjectLoop.pdf";
        MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler();
        //setting the limit to 256mb for xobjects
        memoryLimitsAwareHandler.setMaxXObjectsSizePerPage(1024L*1024L*256L);
        PdfReader pdfReader = new PdfReader(fileName, new ReaderProperties().setMemoryLimitsAwareHandler(memoryLimitsAwareHandler));
        PdfDocument document = new PdfDocument(pdfReader);
        Exception exception = Assertions.assertThrows(MemoryLimitsAwareException.class,
                () -> PdfTextExtractor.getTextFromPage(document.getPage(1)));
        Assertions.assertEquals(KernelExceptionMessageConstant.TOTAL_XOBJECT_SIZE_ONE_PAGE_EXCEEDED_THE_LIMIT,
                exception.getMessage());
    }

    @Test
    public void indirectsChain2() throws IOException {
        String filename = DESTINATION_FOLDER + "indirectsChain2.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject pdfObject = getTestPdfDictionary();
        for (int i = 0; i < 100; i++) {
            pdfObject = pdfObject.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), pdfObject);
        document.close();

        PdfReader reader = CompareTool.createOutputReader(filename);
        document = new PdfDocument(reader);

        pdfObject = document.getXref().get(1).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Catalog));

        pdfObject = document.getXref().get(2).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Pages));

        pdfObject = document.getXref().get(3).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());

        pdfObject = document.getXref().get(4).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assertions.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        for (int i = 6; i < 6 + 32; i++) {
            Assertions.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());
        }

        for (int i = 6 + 32; i < document.getXref().size(); i++) {
            Assertions.assertEquals(PdfObject.INDIRECT_REFERENCE, document.getXref().get(i).getRefersTo().getType());
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void indirectsChain3() throws IOException {
        String filename = SOURCE_FOLDER + "indirectsChain3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assertions.assertTrue(object.getType() == PdfObject.DICTIONARY);

        object = document.getXref().get(4).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        Assertions.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXref().size(); i++) {
            Assertions.assertEquals(PdfObject.INDIRECT_REFERENCE, document.getXref().get(i).getRefersTo().getType());
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void invalidIndirect() throws IOException {
        String filename = SOURCE_FOLDER + "invalidIndirect.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = document.getXref().get(4).getRefersTo();
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());
        Assertions.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXref().size(); i++) {
            Assertions.assertNull(document.getXref().get(i).getRefersTo());
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest01() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocument.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(reader, writer);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        PdfPage testPage = document.getPage(1000);
        int testXref = testPage.getPdfObject().getIndirectReference().getObjNumber();
        document.movePage(1000, 1000);
        Assertions.assertEquals(testXref, testPage.getPdfObject().getIndirectReference().getObjNumber());

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }
        reader.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + pageNum + ")"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest02() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + pageNum + ")"));
        }
        document.close();
    }

    @Test
    public void pagesTest03() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentWithLeafs.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + pageNum + ")"));
        }
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest04() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(i + "00"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(pageNum + "00"));
        }
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest05() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument05.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(i + "00"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.startsWith(pageNum + "00"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest06() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument06.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(2, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("100"));

        page = document.getPage(2);
        content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("300"));
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);

        page = document.getPage(2);
        document.removePage(page);
        content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("300"));
        page = document.getPage(1);
        document.removePage(1);
        content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("100"));

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest07() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument07.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(2, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest08() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument08.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest09() throws IOException {
        String filename = SOURCE_FOLDER + "PagesDocument09.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("100"));

        page = document.getPage(1);
        document.removePage(1);
        content = new String(page.getContentStream(0).getBytes());
        Assertions.assertTrue(content.startsWith("100"));
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest10() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        Random rnd = new Random();
        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            int pageNum = rnd.nextInt(document.getNumberOfPages()) + 1;
            PdfPage page = document.getPage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + pageNum + ")"));
        }

        List<Integer> pageNums = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            pageNums.add(i + 1);
        }

        for (int i = 1; i < pageCount + 1; i++) {
            int index = rnd.nextInt(document.getNumberOfPages()) + 1;
            int pageNum = (int) pageNums.remove(index - 1);
            PdfPage page = document.getPage(index);
            document.removePage(index);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + pageNum + ")"));
        }
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");
        document.close();
    }

    @Test
    public void pagesTest11() throws IOException {
        String filename = SOURCE_FOLDER + "hello.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        try {
            document.getPage(-30);
        } catch (IndexOutOfBoundsException e) {
            Assertions.assertEquals(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS,
                            -30), e.getMessage());
        }
        try {
            document.getPage(0);
        } catch (IndexOutOfBoundsException e) {
            Assertions.assertEquals(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 0),
                    e.getMessage());
        }
        document.getPage(1);
        try {
            document.getPage(25);
        } catch (IndexOutOfBoundsException e) {
            Assertions.assertEquals(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, 25),
                    e.getMessage());
        }
        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(
            messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE, count = 1))
    public void correctSimpleDoc1() throws IOException {
        String filename = SOURCE_FOLDER + "correctSimpleDoc1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assertions.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    public void correctSimpleDoc2() throws IOException {
        String filename = SOURCE_FOLDER + "correctSimpleDoc2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasFixedXref(), "Need fixXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assertions.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE, count = 1))
    public void correctSimpleDoc3() throws IOException {
        String filename = SOURCE_FOLDER + "correctSimpleDoc3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assertions.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE),
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE),
    })
    public void correctSimpleDoc4() throws IOException {
        String filename = SOURCE_FOLDER + "correctSimpleDoc4.pdf";

        PdfReader reader = new PdfReader(filename);
        try {
            //NOTE test with abnormal object declaration that iText can't resolve.
            PdfDocument document = new PdfDocument(reader);
            Assertions.fail("Expect exception");
        } catch (PdfException e) {
            Assertions.assertEquals(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE_PAGES_MUST_BE_PDF_DICTIONARY,
                    e.getMessage());
        } finally {
            reader.close();
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest01() throws IOException {
        String filename = SOURCE_FOLDER + "OnlyTrailer.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest02() throws IOException {
        String filename = SOURCE_FOLDER + "CompressionShift1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest03() throws IOException {
        String filename = SOURCE_FOLDER + "CompressionShift2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest04() throws IOException {
        String filename = SOURCE_FOLDER + "CompressionWrongObjStm.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assertions.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest05() throws IOException {
        String filename = SOURCE_FOLDER + "CompressionWrongShift.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assertions.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest06() throws IOException {
        String filename = SOURCE_FOLDER + "InvalidOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasFixedXref(), "Need fixXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, count = 2))
    public void fixPdfTest07() throws IOException {
        String filename = SOURCE_FOLDER + "XRefSectionWithFreeReferences1.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (ClassCastException ex) {
            exception = true;
        }

        Assertions.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest08() throws IOException {
        String filename = SOURCE_FOLDER + "XRefSectionWithFreeReferences2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        Assertions.assertEquals(author, document.getDocumentInfo().getAuthor());
        Assertions.assertEquals(creator, document.getDocumentInfo().getCreator());
        Assertions.assertEquals(title, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest09() throws IOException {
        String filename = SOURCE_FOLDER + "XRefSectionWithFreeReferences3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        Assertions.assertEquals(author, document.getDocumentInfo().getAuthor());
        Assertions.assertEquals(creator, document.getDocumentInfo().getCreator());
        Assertions.assertEquals(title, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, count = 1))
    public void fixPdfTest10() throws IOException {
        String filename = SOURCE_FOLDER + "XRefSectionWithFreeReferences4.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        Assertions.assertEquals(null, document.getDocumentInfo().getAuthor());
        Assertions.assertEquals(null, document.getDocumentInfo().getCreator());
        Assertions.assertEquals(null, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest11() throws IOException {
        String filename = SOURCE_FOLDER + "XRefSectionWithoutSize.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest12() throws IOException {
        String filename = SOURCE_FOLDER + "XRefWithBreaks.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE)
    })
    public void fixPdfTest13() throws IOException {
        String filename = SOURCE_FOLDER + "XRefWithInvalidGenerations1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");
        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        for (int i = 1; i < 10; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));

        }

        boolean exception = false;

        int i;
        PdfObject fontF1 = document.getPage(997).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).get(new PdfName("F1"));
        Assertions.assertTrue(fontF1 instanceof PdfNull);

        //There is a generation number mismatch in xref table and object for 3093
        try {
            document.getPdfObject(3093);
        } catch (com.itextpdf.io.exceptions.IOException ex) {
            exception = true;
        }
        Assertions.assertTrue(exception);
        exception = false;

        try {
            for (i = 11; i < document.getNumberOfPages() + 1; i++) {
                PdfPage page = document.getPage(i);
                page.getContentStream(0).getBytes();
            }
        } catch (PdfException ex) {
            exception = true;
        }
        Assertions.assertFalse(exception);
        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE)
    })
    public void fixPdfTest14() throws IOException {
        String filename = SOURCE_FOLDER + "XRefWithInvalidGenerations2.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assertions.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest15() throws IOException {
        String filename = SOURCE_FOLDER + "XRefWithInvalidGenerations3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest16() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithInvalidOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertFalse(reader.hasFixedXref(), "No need in fixXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        Assertions.assertTrue(reader.hasFixedXref(), "Need live fixXref()");

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest17() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void fixPdfTest18() throws IOException {
        String filename = SOURCE_FOLDER + "noXrefAndTrailerWithInfo.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1, pageCount);

        Assertions.assertTrue(document.getDocumentInfo().getProducer().contains("iText Group NV (AGPL-version)"));

        document.close();
    }

    @Test
    public void appendModeWith1000Pages() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        document.close();
    }

    @Test
    public void appendModeWith1000PagesWithCompression() throws IOException {
        String filename = SOURCE_FOLDER + "1000PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        document.close();
    }

    @Test
    public void appendModeWith10Pages() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        document.close();
    }

    @Test
    public void appendModeWith10PagesWithCompression() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertFalse(reader.hasRebuiltXref(), "No need in rebuildXref()");

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void appendModeWith10PagesFix1() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentAppendedFix1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");
        Assertions.assertNotNull(document.getTrailer().get(PdfName.ID), "Invalid trailer");

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    public void appendModeWith10PagesFix2() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentAppendedFix2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        int pageCount = document.getNumberOfPages();
        Assertions.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assertions.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assertions.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assertions.assertTrue(content.contains("Append mode"));
        }

        Assertions.assertTrue(reader.hasRebuiltXref(), "Need rebuildXref()");
        Assertions.assertNotNull(document.getTrailer().get(PdfName.ID), "Invalid trailer");

        document.close();
    }

    @Test
    public void incorrectXrefSizeInTrailer() throws IOException {
        String filename = SOURCE_FOLDER + "HelloWorldIncorrectXRefSizeInTrailer.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assertions.assertFalse(reader.hasRebuiltXref(), "Need rebuildXref()");
        Assertions.assertNotNull(document.getTrailer().get(PdfName.ID), "Invalid trailer");

        document.close();
    }

    @Test
    public void incorrectXrefSizeInTrailerAppend() throws IOException {
        String filename = SOURCE_FOLDER + "10PagesDocumentAppendedIncorrectXRefSize.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assertions.assertFalse(reader.hasRebuiltXref(), "Need rebuildXref()");
        Assertions.assertNotNull(document.getTrailer().get(PdfName.ID), "Invalid trailer");

        document.close();
    }


    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection1() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "10PagesDocumentWithInvalidStreamLength.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            int pageCount = pdfDoc.getNumberOfPages();
            for (int k = 1; k < pageCount + 1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                byte[] content = page.getFirstContentStream().getBytes();
                Assertions.assertEquals(57, content.length);
            }
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection2() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingLength1.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection3() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingLength2.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(697, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection4() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingLength3.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection5() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingLength4.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection6() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingWithInvalidStreamLength1.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection7() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingWithInvalidStreamLength2.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection8() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "simpleCanvasWithDrawingWithInvalidStreamLength3.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assertions.assertEquals(697, content.length);
            pdfDoc.close();
        }
    }

    @Test
    @Timeout(unit = TimeUnit.MILLISECONDS, value = 1000)
    public void streamLengthCorrection9() throws IOException {
        synchronized (this) {
            String filename = SOURCE_FOLDER + "10PagesDocumentWithInvalidStreamLength2.pdf";
            PdfReader.correctStreamLength = false;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            int pageCount = pdfDoc.getNumberOfPages();
            for (int k = 1; k < pageCount + 1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                byte[] content = page.getFirstContentStream().getBytes();
                Assertions.assertEquals(20, content.length);
            }
            pdfDoc.close();
            PdfReader.correctStreamLength = true;
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void freeReferencesTest() throws IOException {
        String filename = SOURCE_FOLDER + "freeReferences.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assertions.assertNull(pdfDoc.getPdfObject(8));
        //Assertions.assertFalse(pdfDoc.getReader().fixedXref);
        Assertions.assertFalse(pdfDoc.getReader().rebuiltXref);

        pdfDoc.close();
    }

    @Test
    public void freeReferencesTest02() throws IOException, InterruptedException {

        String cmpFile = SOURCE_FOLDER + "cmp_freeReferences02.pdf";
        String outputFile = DESTINATION_FOLDER + "freeReferences02.pdf";
        String inputFile = SOURCE_FOLDER + "freeReferences02.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(outputFile);
        PdfReader reader = new PdfReader(inputFile);

        PdfDocument inputPdfDocument = new PdfDocument(reader);
        PdfDocument outputPdfDocument = new PdfDocument(writer);

        int lastPage = inputPdfDocument.getNumberOfPages();
        inputPdfDocument.copyPagesTo(lastPage, lastPage, outputPdfDocument);

        inputPdfDocument.close();
        outputPdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFile, cmpFile, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void pdfVersionTest() throws IOException {
        String filename = SOURCE_FOLDER + "hello.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assertions.assertEquals(PdfVersion.PDF_1_4, pdfDoc.getPdfVersion());

        pdfDoc.close();
    }

    @Test
    public void zeroUpdateTest() throws IOException {
        String filename = SOURCE_FOLDER + "stationery.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

//      Test such construction:
//      xref
//      0 0
//      trailer
//      <</Size 27/Root 1 0 R/Info 12 0 R//Prev 245232/XRefStm 244927>>
//      startxref
        Assertions.assertFalse(reader.hasFixedXref());
        Assertions.assertFalse(reader.hasRebuiltXref());
        Assertions.assertTrue(((PdfDictionary) pdfDoc.getPdfObject(1)).containsKey(PdfName.AcroForm));
        pdfDoc.close();
    }

    @Test
    public void incrementalUpdateWithOnlyZeroObjectUpdate() throws IOException {
        String filename = SOURCE_FOLDER + "pdfReferenceUpdated.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

        Assertions.assertFalse(reader.hasFixedXref());
        Assertions.assertFalse(reader.hasRebuiltXref());

        // problem that is tested here originally was found because the StructTreeRoot dictionary wasn't read
        Assertions.assertTrue(pdfDoc.isTagged());
        pdfDoc.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE, count = 1),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE),
            @LogMessage(messageTemplate = IoLogMessageConstant.ENCOUNTERED_INVALID_MCR)})
    public void wrongTagStructureFlushingTest() throws IOException {
        //wrong /Pg number
        String source = SOURCE_FOLDER + "wrongTagStructureFlushingTest.pdf";
        String dest = DESTINATION_FOLDER + "wrongTagStructureFlushingTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), CompareTool.createTestPdfWriter(dest));
        pdfDoc.setTagged();
        Assertions.assertEquals(PdfNull.PDF_NULL, ((PdfDictionary) pdfDoc.getPdfObject(12)).get(PdfName.Pg));
        pdfDoc.close();
    }

    @Test
    public void readerReuseTest() throws IOException {
        String filename = SOURCE_FOLDER + "hello.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc1 = new PdfDocument(reader);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> new PdfDocument(reader)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_READER_HAS_BEEN_ALREADY_UTILIZED, e.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void hugeInvalidIndRefObjNumberTest() throws IOException {
        String filename = SOURCE_FOLDER + "hugeIndRefObjNum.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfObject pdfObject = pdfDoc.getPdfObject(4);
        Assertions.assertTrue(pdfObject.isDictionary());
        Assertions.assertEquals(PdfNull.PDF_NULL, ((PdfDictionary) pdfObject).get(PdfName.Pg));

        pdfDoc.close();
    }

    @Test
    @Disabled("DEVSIX-2133")
    public void testFileIsNotLockedOnException() throws IOException {
        File nonPdfFileName = new File(SOURCE_FOLDER + "text_file.txt");
        Assertions.assertTrue(nonPdfFileName.exists());
        boolean exceptionThrown = false;
        try {
            PdfReader reader = new PdfReader(nonPdfFileName);
        } catch (com.itextpdf.io.exceptions.IOException e) {
            exceptionThrown = true;

            // File should be available for writing
            OutputStream stream = FileUtil.getFileOutputStream(nonPdfFileName);
            stream.write(new byte[] {0});
        }
        Assertions.assertTrue(exceptionThrown);
    }


    @Test
    public void testManyAppendModeUpdates() throws Exception {
        String file = SOURCE_FOLDER + "manyAppendModeUpdates.pdf";
        PdfReader reader = new PdfReader(file);
        PdfDocument document = new PdfDocument(reader);
        document.close();
    }

    private boolean objectTypeEqualTo(PdfObject object, PdfName type) {
        PdfName objectType = ((PdfDictionary) object).getAsName(PdfName.Type);
        return type.equals(objectType);
    }

    @Test
    public void hasRebuiltXrefPdfDocumentNotReadTest() throws IOException {
        PdfReader hasRebuiltXrefReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> hasRebuiltXrefReader.hasRebuiltXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void hasRebuiltXrefReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader hasRebuiltXrefReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                hasRebuiltXref();
                super.readPdf();
            }
        };

        readingNotCompletedTest(hasRebuiltXrefReader);
    }

    @Test
    public void hasHybridXrefPdfDocumentNotReadTest() throws IOException {
        PdfReader hasHybridXrefPdfReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> hasHybridXrefPdfReader.hasHybridXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void hasHybridXrefReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader hasHybridXrefPdfReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                hasHybridXref();
                super.readPdf();
            }
        };

        readingNotCompletedTest(hasHybridXrefPdfReader);
    }

    @Test
    public void hasXrefStmPdfDocumentNotReadTest() throws IOException {
        PdfReader hasXrefStmReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> hasXrefStmReader.hasXrefStm());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void hasXrefStmReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader hasXrefStmReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                hasXrefStm();
                super.readPdf();
            }
        };

        readingNotCompletedTest(hasXrefStmReader);
    }

    @Test
    public void hasFixedXrefPdfDocumentNotReadTest() throws IOException {
        PdfReader hasFixedXrefReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> hasFixedXrefReader.hasFixedXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void hasFixedXrefReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader hasFixedXrefReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                hasFixedXref();
                super.readPdf();
            }
        };

        readingNotCompletedTest(hasFixedXrefReader);
    }

    @Test
    public void getLastXrefPdfDocumentNotReadTest() throws IOException {
        PdfReader getLastXrefReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getLastXrefReader.getLastXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getLastXrefReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader getLastXrefReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                getLastXref();
                super.readPdf();
            }
        };

        readingNotCompletedTest(getLastXrefReader);
    }

    @Test
    public void getPermissionsPdfDocumentNotReadTest() throws IOException {
        PdfReader getPermissionsReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getPermissionsReader.getPermissions());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getPermissionsReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader getPermissionsReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                getPermissions();
                super.readPdf();
            }
        };

        readingNotCompletedTest(getPermissionsReader);
    }

    @Test
    public void isOpenedWithFullPPdfDocumentNotReadTest() throws IOException {
        PdfReader isOpenedWithFullPReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> isOpenedWithFullPReader.isOpenedWithFullPermission()
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void isOpenedWithFullPReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader isOpenedWithFullPReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                isOpenedWithFullPermission();
                super.readPdf();
            }
        };

        readingNotCompletedTest(isOpenedWithFullPReader);
    }

    @Test
    public void getCryptoModePdfDocumentNotReadTest() throws IOException {
        PdfReader getCryptoModeReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getCryptoModeReader.getCryptoMode());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getCryptoModeReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader getCryptoModeReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                getCryptoMode();
                super.readPdf();
            }
        };

        readingNotCompletedTest(getCryptoModeReader);
    }

    @Test
    public void computeUserPasswordPdfDocumentNotReadTest() throws IOException {
        PdfReader computeUserPasswordReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> computeUserPasswordReader.computeUserPassword()
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void computeUserPasswordReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader computeUserPasswordReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                computeUserPassword();
                super.readPdf();
            }
        };

        readingNotCompletedTest(computeUserPasswordReader);
    }

    @Test
    public void getOriginalFileIdPdfDocumentNotReadTest() throws IOException {
        PdfReader getOriginalFileIdReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getOriginalFileIdReader.getOriginalFileId());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getOriginalFileIdReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader getOriginalFileIdReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                getOriginalFileId();
                super.readPdf();
            }
        };

        readingNotCompletedTest(getOriginalFileIdReader);
    }

    @Test
    public void getModifiedFileIdPdfDocumentNotReadTest() throws IOException {
        PdfReader getModifiedFileIdReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getModifiedFileIdReader.getModifiedFileId());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getModifiedFileIdReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader getModifiedFileIdReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                getModifiedFileId();
                super.readPdf();
            }
        };

        readingNotCompletedTest(getModifiedFileIdReader);
    }

    @Test
    public void isEncryptedPdfDocumentNotReadTest() throws IOException {
        PdfReader isEncryptedReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> isEncryptedReader.isEncrypted());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void isEncryptedReadingNotCompletedTest() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        PdfReader isEncryptedReader = new PdfReader(filename) {
            @Override
            protected void readPdf() throws IOException {
                isEncrypted();
                super.readPdf();
            }
        };

        readingNotCompletedTest(isEncryptedReader);
    }

    @Test
    public void pdf11VersionValidTest() throws IOException {
        String fileName = SOURCE_FOLDER + "pdf11Version.pdf";
        new PdfDocument(new PdfReader(fileName));
    }

    @Test
    public void noPdfVersionTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "noPdfVersion.pdf");
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfReader.readPdf());
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_VERSION_IS_NOT_VALID, exception.getMessage());
    }

    @Test
    public void startxrefIsNotFollowedByANumberTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "startxrefIsNotFollowedByANumber.pdf");
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfReader.readXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_STARTXREF_IS_NOT_FOLLOWED_BY_A_NUMBER,
                exception.getMessage());
    }

    @Test
    public void startxrefNotFoundTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "startxrefNotFound.pdf");
        Exception exception = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> pdfReader.readXref());
        Assertions.assertEquals(KernelExceptionMessageConstant.PDF_STARTXREF_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void closeStreamCreatedByITextTest() throws IOException {
        String fileName = SOURCE_FOLDER + "emptyPdf.pdf";
        String copiedFileName = DESTINATION_FOLDER + "emptyPdf.pdf";
        //Later in the test we will need to delete a file. Since we do not want to delete it from sources, we will
        // copy it to destination folder.
        File copiedFile = copyFileForTest(fileName, copiedFileName);
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, () -> new PdfReader(fileName));
        Assertions.assertEquals(IoExceptionMessageConstant.PDF_HEADER_NOT_FOUND, e.getMessage());
        //This check is meaningfull only on Windows, since on other OS the fact of a stream being open doesn't
        // prevent the stream from being deleted.
        Assertions.assertTrue(FileUtil.deleteFile(copiedFile));
    }

    @Test
    public void notCloseUserStreamTest() throws IOException {
        String fileName = SOURCE_FOLDER + "emptyPdf.pdf";
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {
            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .createSource(pdfStream);
            Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                    () -> new PdfReader(randomAccessSource, new ReaderProperties()));
            //An exception would be thrown, if stream is closed.
            Assertions.assertEquals(-1, pdfStream.read());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN)
    })
    public void endDicInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayEndDictToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void endArrayClosingBracketInsteadOfEndDicTest() {
        String fileName = SOURCE_FOLDER + "endArrayClosingBracketInsteadOfEndDic.pdf";
        Exception exception = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> new PdfDocument(new PdfReader(fileName)));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, "]"),
                exception.getCause().getMessage());
    }

    @Test
    public void endDicClosingBracketInsideTheDicTest() {
        String fileName = SOURCE_FOLDER + "endDicClosingBracketInsideTheDic.pdf";
        Exception exception = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> new PdfDocument(new PdfReader(fileName)));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, ">>"),
                exception.getCause().getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN)
    })
    public void eofInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayEOFToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN)
    })
    public void endObjInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayEndObjToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void nameInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayNameToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN)
    })
    public void objInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayObjToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN)
    })
    public void refInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayRefToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN, count = 2)
    })
    public void startArrayInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayStartArrayToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelExceptionMessageConstant.UNEXPECTED_TOKEN),
            @LogMessage(messageTemplate = IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE)
    })
    public void stringInsteadOfArrayClosingBracketTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayStringToken.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(fileName));
        PdfArray actual = (PdfArray) document.getPdfObject(4);
        PdfArray expected = new PdfArray(new float[] {5, 10, 15, 20});
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void closingArrayBracketMissingConservativeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "invalidArrayObjToken.pdf";
        PdfReader reader = new PdfReader(fileName);
        reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
        PdfDocument document = new PdfDocument(reader);
        Exception exception = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                () -> document.getPdfObject(4));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_TOKEN, "obj"),
                exception.getCause().getMessage());
    }

    @Test
    public void readRASInputStreamClosedTest() throws IOException {
        String fileName = SOURCE_FOLDER + "hello.pdf";
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {

            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(pdfStream);
            RASInputStream rasInputStream = new RASInputStream(randomAccessSource);

            randomAccessSource.close();

            Exception e = Assertions.assertThrows(IllegalStateException.class,
                    () -> new PdfReader(rasInputStream));
            Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());
        }
    }

    @Test
    public void readRASInputStreamTest() throws IOException {
        String fileName = SOURCE_FOLDER + "hello.pdf";
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {
            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(pdfStream);
            RASInputStream rasInputStream = new RASInputStream(randomAccessSource);

            try (PdfReader reader = new PdfReader(rasInputStream)) {
                randomAccessSource.close();
                Exception e = Assertions.assertThrows(IllegalStateException.class, () -> new PdfDocument(reader));
                Assertions.assertEquals(IoExceptionMessageConstant.ALREADY_CLOSED, e.getMessage());
            }
        }
    }

    @Test
    public void readRASInputStreamValidTest() throws IOException {
        String fileName = SOURCE_FOLDER + "hello.pdf";
        try (InputStream pdfStream = FileUtil.getInputStreamForFile(fileName)) {
            IRandomAccessSource randomAccessSource = new RandomAccessSourceFactory()
                    .extractOrCreateSource(pdfStream);
            RASInputStream rasInputStream = new RASInputStream(randomAccessSource);

            try (PdfReader reader = new PdfReader(rasInputStream)) {
                AssertUtil.doesNotThrow(() -> new PdfDocument(reader));
            }
        }
    }

    private static File copyFileForTest(String fileName, String copiedFileName) throws IOException {
        File copiedFile = new File(copiedFileName);
        Files.copy(Paths.get(fileName), Paths.get(copiedFileName));
        return copiedFile;
    }

    private PdfReader pdfDocumentNotReadTestInit() throws IOException {
        String filename = SOURCE_FOLDER + "XrefWithNullOffsets.pdf";

        return new PdfReader(filename);
    }

    private void readingNotCompletedTest(PdfReader reader) {
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(reader));
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getPdfAConformancePdfDocumentNotReadTest() throws IOException {
        PdfReader getModifiedFileIdReader = pdfDocumentNotReadTestInit();

        Exception e = Assertions.assertThrows(PdfException.class, () -> getModifiedFileIdReader.getPdfConformance());
        Assertions.assertEquals(KernelExceptionMessageConstant.DOCUMENT_HAS_NOT_BEEN_READ_YET, e.getMessage());
    }

    @Test
    public void getPdfConformanceNoMetadataTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(createPdfDocumentForTest())));
        Assertions.assertFalse(pdfDoc.getReader().getPdfConformance().isPdfAOrUa());
    }

    @Test
    public void xrefStreamPointsItselfTest() throws IOException {
        String fileName = SOURCE_FOLDER + "xrefStreamPointsItself.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.LENIENT, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STREAM_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @Test
    public void xrefStreamPointsItselfConservativeModeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "xrefStreamPointsItself.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            pdfReader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.CONSERVATIVE, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STREAM_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void exactLimitOfObjectNrSizeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "exactLimitOfObjectNr.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            Exception exception = Assertions.assertThrows(MemoryLimitsAwareException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT,
                    exception.getMessage());
        }
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void justBeforeLimitOfObjectNrSizeTest() throws IOException, InterruptedException {
        String inputFile = SOURCE_FOLDER + "justBeforeLimitOfObjectNr.pdf";

        //trying to open the document to see that no error is thrown
        PdfReader pdfReader = new PdfReader(inputFile);
        PdfDocument document = new PdfDocument(pdfReader);
        Assertions.assertEquals(500000, document.getXref().getCapacity());
        document.close();
    }

    @Test
    public void xrefStreamsHaveCycledReferencesTest() throws IOException {
        String fileName = SOURCE_FOLDER + "cycledReferencesInXrefStreams.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.LENIENT, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STREAM_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @Test
    public void xrefStreamsHaveCycledReferencesConservativeModeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "cycledReferencesInXrefStreams.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            pdfReader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.CONSERVATIVE, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STREAM_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE, count = 1))
    public void xrefTablesHaveCycledReferencesTest() throws IOException {
        String fileName = SOURCE_FOLDER + "cycledReferencesInXrefTables.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            AssertUtil.doesNotThrow(() -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.LENIENT, pdfReader.getStrictnessLevel());
            Assertions.assertTrue(pdfReader.hasRebuiltXref());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE, count = 1))
    public void xrefTablePointsItselfTest() throws IOException {
        String fileName = SOURCE_FOLDER + "xrefTablePointsItself.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            AssertUtil.doesNotThrow(() -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.LENIENT, pdfReader.getStrictnessLevel());
            Assertions.assertTrue(pdfReader.hasRebuiltXref());
        }
    }

    @Test
    public void xrefTablePointsItselfConservativeModeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "xrefTablePointsItself.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            pdfReader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.CONSERVATIVE, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_TABLE_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @Test
    public void xrefTablesHaveCycledReferencesConservativeModeTest() throws IOException {
        String fileName = SOURCE_FOLDER + "cycledReferencesInXrefTables.pdf";

        try (PdfReader pdfReader = new PdfReader(fileName)) {
            pdfReader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(XrefCycledReferencesException.class,
                    () -> new PdfDocument(pdfReader));

            Assertions.assertEquals(StrictnessLevel.CONSERVATIVE, pdfReader.getStrictnessLevel());
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_TABLE_HAS_CYCLED_REFERENCES,
                    exception.getMessage());
        }
    }

    @Test
    public void checkXrefStreamInvalidSize() throws IOException {
        final String fileName = SOURCE_FOLDER + "xrefStreamInvalidSize.pdf";

        try (PdfReader reader = new PdfReader(fileName)) {
            Exception ex = Assertions.assertThrows(MemoryLimitsAwareException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_STRUCTURE_SIZE_EXCEEDED_THE_LIMIT, ex.getMessage());
        }
    }

    @Test
    public void checkXrefPrevWithDifferentTypesTest() throws IOException {
        final PdfNumber numberXrefPrev = new PdfNumber(20);
        final PdfString stringXrefPrev = new PdfString("iText", PdfEncodings.UNICODE_BIG);
        final PdfIndirectReference indirectReferenceXrefPrev = new PdfIndirectReference(null, 41);
        final PdfIndirectReference indirectReferenceToString = new PdfIndirectReference(null, 42);
        indirectReferenceXrefPrev.setRefersTo(numberXrefPrev);
        indirectReferenceToString.setRefersTo(stringXrefPrev);

        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(createPdfDocumentForTest()))) {
            reader.setStrictnessLevel(StrictnessLevel.LENIENT);

            AssertUtil.doesNotThrow(() -> reader.getXrefPrev(numberXrefPrev));

            AssertUtil.doesNotThrow(() -> reader.getXrefPrev(indirectReferenceXrefPrev));

            // Check string xref prev with StrictnessLevel#LENIENT.
            Exception exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> reader.getXrefPrev(stringXrefPrev));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());

            // Check indirect reference to string xref prev with StrictnessLevel#LENIENT.
            exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> reader.getXrefPrev(indirectReferenceToString));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());
        }
    }

    @Test
    public void checkXrefPrevWithDifferentTypesConservativeModeTest() throws IOException {
        final PdfNumber numberXrefPrev = new PdfNumber(20);
        final PdfString stringXrefPrev = new PdfString("iText", PdfEncodings.UNICODE_BIG);
        final PdfIndirectReference indirectReferenceXrefPrev = new PdfIndirectReference(null, 41);
        final PdfIndirectReference indirectReferenceToString = new PdfIndirectReference(null, 42);
        indirectReferenceXrefPrev.setRefersTo(numberXrefPrev);
        indirectReferenceToString.setRefersTo(stringXrefPrev);

        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(createPdfDocumentForTest()))) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);

            AssertUtil.doesNotThrow(() -> reader.getXrefPrev(numberXrefPrev));

            // Check indirect reference to number xref prev with StrictnessLevel#CONSERVATIVE.
            Exception exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> reader.getXrefPrev(indirectReferenceXrefPrev));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());

            // Check string xref prev with StrictnessLevel#CONSERVATIVE.
            exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> reader.getXrefPrev(stringXrefPrev));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());

            // Check indirect reference to string xref prev with StrictnessLevel#CONSERVATIVE.
            exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> reader.getXrefPrev(indirectReferenceToString));
            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());
        }
    }

    @Test
    public void readDocumentWithIndirectPrevTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "indirectPrev.pdf";
        final String outputName = DESTINATION_FOLDER + "documentWithIndirectPrev.pdf";

        // Open pdf doc and check that xref prev is indirect.
        try (PdfReader reader = new PdfReader(fileName);
                PdfDocument document = new PdfDocument(reader)) {
            final PdfDictionary documentTrailer = document.getTrailer();
            Assertions.assertTrue(documentTrailer.get(PdfName.Prev, false).isIndirectReference());
        }

        // Read/write pdf document to rewrite xref structure.
        try (PdfReader reader = new PdfReader(fileName);
                PdfWriter writer = CompareTool.createTestPdfWriter(outputName);
                PdfDocument document = new PdfDocument(reader, writer)) {
        }

        // Read and check that in created pdf we have valid xref prev.
        try (PdfReader reader = CompareTool.createOutputReader(outputName);
                PdfDocument document = new PdfDocument(reader)) {
            PdfDictionary trailer = document.getTrailer();
            Assertions.assertNull(trailer.get(PdfName.Prev, false));
        }
    }

    @Test
    public void notChangeInvalidPrevInAppendModeTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "indirectPrev.pdf";
        final String outputName = DESTINATION_FOLDER + "invalidPrevAppendMode.pdf";

        // Read document and check that we have indirect prev.
        try (PdfReader reader = new PdfReader(fileName);
                PdfDocument document = new PdfDocument(reader)) {
            final PdfDictionary documentTrailer = document.getTrailer();
            Assertions.assertTrue(documentTrailer.get(PdfName.Prev, false).isIndirectReference());
        }

        // Read and write document in append mode to not change previous xref prev.
        final StampingProperties properties = new StampingProperties().useAppendMode();
        try (PdfReader reader = new PdfReader(fileName);
                PdfWriter writer = CompareTool.createTestPdfWriter(outputName);
                PdfDocument document = new PdfDocument(reader, writer, properties)) {
            document.addNewPage();
        }

        // Read resulted document and check, that previous xref prev doesn't change and current is pdfNumber.
        try (PdfReader reader = CompareTool.createOutputReader(outputName);
                PdfDocument document = new PdfDocument(reader)) {
            final PdfDictionary trailer = document.getTrailer();
            Assertions.assertFalse(trailer.get(PdfName.Prev, false).isIndirectReference());
            PdfNumber prevPointer = (PdfNumber) trailer.get(PdfName.Prev);
            reader.tokens.seek(prevPointer.longValue());
            final PdfDictionary previousTrailer = reader.readXrefSection();
            Assertions.assertTrue(previousTrailer.get(PdfName.Prev, false).isIndirectReference());
        }
    }

    @Test
    public void readPdfInvalidPrevConservativeModeTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "indirectPrev.pdf";

        // Simply open document with StrictnessLevel#CONSERVATIVE.
        try (PdfReader reader = new PdfReader(fileName)) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(InvalidXRefPrevException.class, () -> new PdfDocument(reader));

            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());
        }

        // Open document for read/write with stamping properties and StrictnessLevel#CONSERVATIVE.
        final StampingProperties properties = new StampingProperties().useAppendMode();
        try (PdfReader reader = new PdfReader(fileName);
                PdfWriter writer = new PdfWriter(new ByteArrayOutputStream())) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> new PdfDocument(reader, writer, properties));

            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());
        }

        // Open document for read/write without stamping properties but with StrictnessLevel#CONSERVATIVE.
        try (PdfReader reader = new PdfReader(fileName);
                PdfWriter writer = new PdfWriter(new ByteArrayOutputStream())) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
            Exception exception = Assertions.assertThrows(InvalidXRefPrevException.class,
                    () -> new PdfDocument(reader, writer));

            Assertions.assertEquals(KernelExceptionMessageConstant.XREF_PREV_SHALL_BE_DIRECT_NUMBER_OBJECT,
                    exception.getMessage());
        }
    }

    @Test
    public void streamWithoutEndstreamKeywordTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "NoEndstreamKeyword.pdf";
        try (PdfReader reader = new PdfReader(fileName)) {
            reader.setStrictnessLevel(StrictnessLevel.LENIENT);
            try (PdfDocument document = new PdfDocument(reader)) {
                // Initialize xmp metadata, because we in reader mode in which xmp will be initialized only during closing
                byte[] metadataBytes = document.getXmpMetadataBytes();
                final PdfCatalog catalog = new PdfCatalog((PdfDictionary) reader.trailer.get(PdfName.Root, true));
                final PdfStream xmpMetadataStream = catalog.getPdfObject().getAsStream(PdfName.Metadata);
                final int xmpMetadataStreamLength = ((PdfNumber) xmpMetadataStream.get(PdfName.Length)).intValue();

                // Initial length was 27600. 3090 is expected length of the stream after the fix
                Assertions.assertEquals(3090, xmpMetadataStreamLength);
                Assertions.assertEquals(3090, metadataBytes.length);
            }
        }
    }

    @Test
    public void streamWithoutEndstreamKeywordConservativeModeTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "NoEndstreamKeyword.pdf";
        try (PdfReader reader = new PdfReader(fileName)) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);

            Exception exception = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(KernelExceptionMessageConstant.STREAM_SHALL_END_WITH_ENDSTREAM, exception.getMessage());

            PdfCatalog catalog = new PdfCatalog((PdfDictionary) reader.trailer.get(PdfName.Root, true));
            PdfStream xmpMetadataStream = catalog.getPdfObject().getAsStream(PdfName.Metadata);

            // 27600 is actual invalid length of stream. In reader StrictnessLevel#CONSERVATIVE we expect, that
            // exception would be thrown and length wouldn't be fixed.
            Assertions.assertEquals(27600, ((PdfNumber) xmpMetadataStream.get(PdfName.Length)).intValue());
        }
    }

    @Test
    public void streamWithoutEndKeyConservativeModeWithWriterTest() throws IOException {
        final String fileName = SOURCE_FOLDER + "NoEndstreamKeyword.pdf";
        try (PdfReader reader = new PdfReader(fileName)) {
            reader.setStrictnessLevel(StrictnessLevel.CONSERVATIVE);

            Exception exception = Assertions.assertThrows(PdfException.class, () ->
                    new PdfDocument(reader, new PdfWriter(new ByteArrayOutputStream())));
            Assertions.assertEquals(KernelExceptionMessageConstant.STREAM_SHALL_END_WITH_ENDSTREAM, exception.getMessage());
        }
    }

    @Test
    public void tokensPositionIsNotUpdatedWhileReadingLengthTest() throws IOException {
        String filename = SOURCE_FOLDER + "simpleDocWithIndirectLength.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename))) {
            PdfTokenizer tokenizer = pdfDoc.getReader().tokens;

            // we will try to get the content stream object
            // since it's not been gotten yet, iText will read this object,
            // which will change the tokenizer's position
            PdfStream pageContentStream = (PdfStream) pdfDoc.getPdfObject(5);

            // tokenizer's position after reading object should point to the end of the object's stream
            Assertions.assertEquals(pageContentStream.getOffset() + pageContentStream.getLength(), tokenizer.getPosition());

            // let's read next valid token and check that it means ending stream
            tokenizer.nextValidToken();
            tokenizer.tokenValueEqualsTo(ByteUtils.getIsoBytes("endstream"));
        }
    }

    @Test
    public void conformanceCacheTest() throws IOException, XMPException {
        String filename = DESTINATION_FOLDER + "simpleDoc.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        XMPMeta xmp = XMPMetaFactory.create();
        xmp.appendArrayItem(XMPConst.NS_DC, "subject",
                new PropertyOptions(PropertyOptions.ARRAY), "Hello World", null);
        pdfDoc.setXmpMetadata(xmp);

        pdfDoc.addNewPage();
        pdfDoc.close();

        TestPdfDocumentCache pdfTestDoc = new TestPdfDocumentCache(CompareTool.createOutputReader(filename));
        for (int i = 0; i < 1000; ++i) {
            pdfTestDoc.getReader().getPdfConformance();
        }
        Assertions.assertEquals(1, pdfTestDoc.getCounter());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE, count = 1))
    public void invalidXrefTableRebuildsCorrectlyWhenTrailerIsBeforeObjects() throws IOException {
        // when a pdf is Linearized the following can occur:
        // xref table
        // 00028 0000 -> some reference to the root object
        // trailer
        // << dict with root obj
        //  /Root 4 0 R
        // >>
        // %%EOF
        // 4 0 obj //the actual object
        // << some object >>
        // now itext can handle this normal case to parse it but when in the first xref table
        // some byte offsets are wrong and the xreftable has to be recalculated
        // but because the trailer comes before the object itext loaded it in reading state causing errors
        String badFilePath = "linearizedBadXrefTable.pdf";
        String goodFilePath = "linearizedGoodXrefTable.pdf";
        try (
                PdfDocument linearizedWithBadXrefTable = new PdfDocument(
                        new PdfReader(SOURCE_FOLDER + badFilePath));
                PdfDocument linearizedWithGoodXrefTable = new PdfDocument(
                        new PdfReader(SOURCE_FOLDER + goodFilePath))) {

            Assertions.assertEquals(linearizedWithGoodXrefTable.getNumberOfPages(),
                    linearizedWithBadXrefTable.getNumberOfPages());
            Assertions.assertEquals(linearizedWithGoodXrefTable.getOriginalDocumentId(),
                    linearizedWithBadXrefTable.getOriginalDocumentId());

            PdfDictionary goodTrailer = linearizedWithGoodXrefTable.getTrailer();
            PdfDictionary badTrailer = linearizedWithBadXrefTable.getTrailer();
            //everything should be the same just not the prev tag because in the rebuild we recalculate the right
            // offsets
            // and there we take the last trailer but the good document takes the fist trailer because its
            // linearized
            Assertions.assertEquals(goodTrailer.size(), badTrailer.size());
            Assertions.assertEquals(goodTrailer.get(PdfName.ID).toString(), badTrailer.get(PdfName.ID).toString());
            Assertions.assertEquals(goodTrailer.get(PdfName.Info).toString(), badTrailer.get(PdfName.Info).toString());
            Assertions.assertEquals(goodTrailer.get(PdfName.Root).toString(), badTrailer.get(PdfName.Root).toString());
        }
    }

    @Test
    public void newPdfReaderConstructorTest() throws IOException {
        String filename = SOURCE_FOLDER + "simpleDoc.pdf";

        PdfReader reader = new PdfReader(new File(filename), new ReaderProperties());
        PdfDocument pdfDoc = new PdfDocument(reader);
        Assertions.assertEquals(author, pdfDoc.getDocumentInfo().getAuthor());
        Assertions.assertEquals(creator, pdfDoc.getDocumentInfo().getCreator());
        Assertions.assertEquals(title, pdfDoc.getDocumentInfo().getTitle());
        PdfObject object = pdfDoc.getPdfObject(1);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, pdfDoc.getPdfObject(5).getType());
    }

    @Test
    public void newPdfReaderConstructorPropertiesTest() throws IOException {
        String fileName = SOURCE_FOLDER + "simpleDocWithPassword.pdf";
        PdfReader reader = new PdfReader(new File(fileName),new ReaderProperties()
                .setPassword(USER_PASSWORD));

        PdfDocument pdfDoc = new PdfDocument(reader);
        Assertions.assertEquals(author, pdfDoc.getDocumentInfo().getAuthor());
        Assertions.assertEquals(creator, pdfDoc.getDocumentInfo().getCreator());
        Assertions.assertEquals(title, pdfDoc.getDocumentInfo().getTitle());
        PdfObject object = pdfDoc.getPdfObject(1);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assertions.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assertions.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assertions.assertEquals(PdfObject.STREAM, pdfDoc.getPdfObject(5).getType());
    }

    @Test
    public void streamObjIsNullTest() throws IOException {
        ByteArrayOutputStream bsaos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(bsaos));

        new PdfDictionary().makeIndirect(pdfDocument);

        PdfStream pdfDictionary = new PdfStream();
        pdfDictionary.makeIndirect(pdfDocument);

        int objNumber = pdfDictionary.getIndirectReference().objNr;
        pdfDocument.catalog.getPdfObject().put(PdfName.StructTreeRoot, pdfDictionary);
        pdfDocument.close();

        PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(bsaos.toByteArray())) {
            @Override
            protected PdfObject readObject(PdfIndirectReference reference) {
                if (reference.objNr == objNumber) {
                    reference.setObjStreamNumber(objNumber - 1);
                    reference.setIndex(492);
                }
                return super.readObject(reference);
            }
        };

        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(pdfReader));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.INVALID_OBJECT_STREAM_NUMBER, 5, 4, 492), e.getMessage());
    }

    @Test
    public void initTagTreeStructureThrowsOOMIsCatched() throws IOException {
        File file = new File(SOURCE_FOLDER+ "big_table_lot_of_mcrs.pdf");
        MemoryLimitsAwareHandler memoryLimitsAwareHandler = new MemoryLimitsAwareHandler() {
            @Override
            public boolean isMemoryLimitsAwarenessRequiredOnDecompression(PdfArray filters) {
                return true;
            }
        };
        memoryLimitsAwareHandler.setMaxSizeOfDecompressedPdfStreamsSum(100000);

        Assertions.assertThrows(MemoryLimitsAwareException.class, () -> {
            try (final PdfReader reader = new PdfReader(file,
                    new ReaderProperties().setMemoryLimitsAwareHandler(memoryLimitsAwareHandler));
                    final PdfDocument document = new PdfDocument(reader);) {
            }
        });
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void incorrectFilePositionInSubsectionCauseTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "incorrectFilePositionInSubsection.pdf").setStrictnessLevel(StrictnessLevel.LENIENT);
        new PdfDocument(pdfReader);
        Assertions.assertTrue(pdfReader.hasRebuiltXref(), "Need rebuildXref()");
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void noSubsectionCauseTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "noSubsection.pdf").setStrictnessLevel(StrictnessLevel.LENIENT);
        new PdfDocument(pdfReader);
        Assertions.assertTrue(pdfReader.hasRebuiltXref(), "Need rebuildXref()");
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void invalidRefCauseXrefRebuildTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "invalidRefCauseXrefRebuild.pdf").setStrictnessLevel(StrictnessLevel.LENIENT);
        new PdfDocument(pdfReader);
        Assertions.assertTrue(pdfReader.hasRebuiltXref(), "Need rebuildXref()");
    }

    @LogMessages(messages = @LogMessage(messageTemplate =
            IoLogMessageConstant.XREF_ERROR_WHILE_READING_TABLE_WILL_BE_REBUILT_WITH_CAUSE))
    @Test
    public void startxrefNotFoundCauseTest() throws IOException {
        PdfReader pdfReader = new PdfReader(SOURCE_FOLDER + "startxrefNotFound.pdf").setStrictnessLevel(StrictnessLevel.LENIENT);
        new PdfDocument(pdfReader);
        Assertions.assertTrue(pdfReader.hasRebuiltXref(), "Need rebuildXref()");
    }

    @Test
    public void readAandUaDocumentTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "bothAandUa.pdf"))) {
            Assertions.assertEquals(PdfAConformance.PDF_A_2B, pdfDoc.getConformance().getAConformance());
            Assertions.assertEquals(PdfUAConformance.PDF_UA_1, pdfDoc.getConformance().getUAConformance());
        }
    }

    private static PdfDictionary getTestPdfDictionary() {
        HashMap<PdfName, PdfObject> tmpMap = new HashMap<PdfName, PdfObject>();
        tmpMap.put(new PdfName("b"), new PdfName("c"));
        return new PdfDictionary(tmpMap);
    }

    private static byte[] createPdfDocumentForTest() throws IOException {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (final PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
                pdfDoc.addNewPage();
            }
            return baos.toByteArray();
        }
    }

    private class TestPdfDocumentCache extends PdfDocument {
        private int getXmpMetadataCounter;

        public TestPdfDocumentCache(PdfReader pdfReader) {
            super(pdfReader);
        }

        @Override
        public byte[] getXmpMetadataBytes(boolean createdNew) {
            ++getXmpMetadataCounter;
            return super.getXmpMetadataBytes(createdNew);
        }

        public int getCounter() {
            return getXmpMetadataCounter;
        }
    }
}
