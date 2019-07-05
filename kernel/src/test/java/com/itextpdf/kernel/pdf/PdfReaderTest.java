/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Category(IntegrationTest.class)
public class PdfReaderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfReaderTest/";

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";
    static final String title = "Empty iText 6 Document";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void openSimpleDoc() throws IOException {
        String filename = destinationFolder + "openSimpleDoc.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.getDocumentInfo().setAuthor(author).
                setCreator(creator).
                setTitle(title);
        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        pdfDoc = new PdfDocument(reader);
        Assert.assertEquals(author, pdfDoc.getDocumentInfo().getAuthor());
        Assert.assertEquals(creator, pdfDoc.getDocumentInfo().getCreator());
        Assert.assertEquals(title, pdfDoc.getDocumentInfo().getTitle());
        PdfObject object = pdfDoc.getPdfObject(1);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, pdfDoc.getPdfObject(5).getType());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        pdfDoc.close();
    }

    @Test
    public void openSimpleDocWithFullCompression() throws IOException {
        String filename = sourceFolder + "simpleCanvasWithFullCompression.pdf";
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfObject object = pdfDoc.getPdfObject(1);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = pdfDoc.getPdfObject(2);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = pdfDoc.getPdfObject(3);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = pdfDoc.getPdfObject(4);
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        object = pdfDoc.getPdfObject(5);
        Assert.assertEquals(PdfObject.STREAM, object.getType());
        String content = "100 100 100 100 re\nf\n";
        Assert.assertArrayEquals(ByteUtils.getIsoBytes(content), ((PdfStream) object).getBytes());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        reader.close();
        pdfDoc.close();
    }

    @Test
    public void openDocWithFlateFilter() throws IOException {
        String filename = sourceFolder + "100PagesDocumentWithFlateFilter.pdf";
        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assert.assertEquals("Page count", 100, document.getNumberOfPages());

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
            Assert.assertEquals("Page content " + i, MessageFormatUtil.format(contentTemplate, i), new String(content));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());
        document.close();
    }

    @Test
    public void primitivesRead() throws IOException {
        String filename = destinationFolder + "primitivesRead.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.put(new PdfName("a"), new PdfBoolean(true).makeIndirect(document));
        document.close();

        PdfReader reader = new PdfReader(filename);
        document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = document.getXref().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        object = document.getXref().get(6).getRefersTo();
        Assert.assertEquals(PdfObject.BOOLEAN, object.getType());
        Assert.assertNotNull(object.getIndirectReference());


        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void indirectsChain1() throws IOException {
        String filename = destinationFolder + "indirectsChain1.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject pdfObject = getTestPdfDictionary();
        for (int i = 0; i < 5; i++) {
            pdfObject = pdfObject.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), pdfObject);
        document.close();

        PdfReader reader = new PdfReader(filename);
        document = new PdfDocument(reader);

        pdfObject = document.getXref().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Catalog));

        pdfObject = document.getXref().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Pages));

        pdfObject = document.getXref().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());

        pdfObject = document.getXref().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        for (int i = 6; i < document.getXref().size(); i++)
            Assert.assertEquals(PdfObject.DICTIONARY, document.getXref().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void indirectsChain2() throws IOException {
        String filename = destinationFolder + "indirectsChain2.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(filename));
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfObject pdfObject = getTestPdfDictionary();
        for (int i = 0; i < 100; i++) {
            pdfObject = pdfObject.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), pdfObject);
        document.close();

        PdfReader reader = new PdfReader(filename);
        document = new PdfDocument(reader);

        pdfObject = document.getXref().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Catalog));

        pdfObject = document.getXref().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Pages));

        pdfObject = document.getXref().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());

        pdfObject = document.getXref().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, pdfObject.getType());
        Assert.assertTrue(objectTypeEqualTo(pdfObject, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        for (int i = 6; i < 6 + 32; i++)
            Assert.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());

        for (int i = 6 + 32; i < document.getXref().size(); i++)
            Assert.assertEquals(PdfObject.INDIRECT_REFERENCE, document.getXref().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void indirectsChain3() throws IOException {
        String filename = sourceFolder + "indirectsChain3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assert.assertTrue(object.getType() == PdfObject.DICTIONARY);

        object = document.getXref().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());

        Assert.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXref().size(); i++)
            Assert.assertEquals(PdfObject.INDIRECT_REFERENCE, document.getXref().get(i).getRefersTo().getType());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void invalidIndirect() throws IOException {
        String filename = sourceFolder + "invalidIndirect.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        PdfObject object = document.getXref().get(1).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Catalog));

        object = document.getXref().get(2).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Pages));

        object = document.getXref().get(3).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());

        object = document.getXref().get(4).getRefersTo();
        Assert.assertEquals(PdfObject.DICTIONARY, object.getType());
        Assert.assertTrue(objectTypeEqualTo(object, PdfName.Page));

        Assert.assertEquals(PdfObject.STREAM, document.getXref().get(5).getRefersTo().getType());
        Assert.assertEquals(PdfObject.DICTIONARY, document.getXref().get(6).getRefersTo().getType());
        for (int i = 7; i < document.getXref().size(); i++)
            Assert.assertNull(document.getXref().get(i).getRefersTo());

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest01() throws IOException {
        String filename = sourceFolder + "1000PagesDocument.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(reader, writer);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        PdfPage testPage = document.getPage(1000);
        int testXref = testPage.getPdfObject().getIndirectReference().getObjNumber();
        document.movePage(1000, 1000);
        Assert.assertEquals(testXref, testPage.getPdfObject().getIndirectReference().getObjNumber());

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }
        reader.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + pageNum + ")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest02() throws IOException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + pageNum + ")"));
        }
        document.close();
    }

    @Test
    public void pagesTest03() throws IOException {
        String filename = sourceFolder + "10PagesDocumentWithLeafs.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + pageNum + ")"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest04() throws IOException {
        String filename = sourceFolder + "PagesDocument.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest05() throws IOException {
        String filename = sourceFolder + "PagesDocument05.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(3, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        for (int i = 1; i < pageCount + 1; i++) {
            PdfPage page = document.getPage(1);
            document.removePage(page);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(i + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);
        for (int i = 1; i < pageCount + 1; i++) {
            int pageNum = document.getNumberOfPages();
            PdfPage page = document.getPage(pageNum);
            document.removePage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.startsWith(pageNum + "00"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest06() throws IOException {
        String filename = sourceFolder + "PagesDocument06.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(2, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.getPage(2);
        content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("300"));
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();

        reader = new PdfReader(filename);
        document = new PdfDocument(reader);

        page = document.getPage(2);
        document.removePage(page);
        content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("300"));
        page = document.getPage(1);
        document.removePage(1);
        content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("100"));

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest07() throws IOException {
        String filename = sourceFolder + "PagesDocument07.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(2, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest08() throws IOException {
        String filename = sourceFolder + "PagesDocument08.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);
        boolean exception = false;
        try {
            document.getPage(1);
        } catch (PdfException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest09() throws IOException {
        String filename = sourceFolder + "PagesDocument09.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);
        PdfPage page = document.getPage(1);
        String content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("100"));

        page = document.getPage(1);
        document.removePage(1);
        content = new String(page.getContentStream(0).getBytes());
        Assert.assertTrue(content.startsWith("100"));
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest10() throws IOException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompression.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        Random rnd = new Random();
        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            int pageNum = rnd.nextInt(document.getNumberOfPages()) + 1;
            PdfPage page = document.getPage(pageNum);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + pageNum + ")"));
        }

        List<Integer> pageNums = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++)
            pageNums.add(i + 1);

        for (int i = 1; i < pageCount + 1; i++) {
            int index = rnd.nextInt(document.getNumberOfPages()) + 1;
            int pageNum = (int) pageNums.remove(index - 1);
            PdfPage page = document.getPage(index);
            document.removePage(index);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + pageNum + ")"));
        }
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());
        document.close();
    }

    @Test
    public void pagesTest11() throws IOException {
        String filename = sourceFolder + "hello.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        try {
            document.getPage(-30);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals(MessageFormatUtil.format(PdfException.RequestedPageNumberIsOutOfBounds, -30), e.getMessage());
        }
        try {
            document.getPage(0);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals(MessageFormatUtil.format(PdfException.RequestedPageNumberIsOutOfBounds, 0), e.getMessage());
        }
        document.getPage(1);
        try {
            document.getPage(25);
        } catch (IndexOutOfBoundsException e) {
            Assert.assertEquals(MessageFormatUtil.format(PdfException.RequestedPageNumberIsOutOfBounds, 25), e.getMessage());
        }
        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void correctSimpleDoc1() throws IOException {
        String filename = sourceFolder + "correctSimpleDoc1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    public void correctSimpleDoc2() throws IOException {
        String filename = sourceFolder + "correctSimpleDoc2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need fixXref()", reader.hasFixedXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void correctSimpleDoc3() throws IOException {
        String filename = sourceFolder + "correctSimpleDoc3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);

        PdfPage page = document.getPage(1);
        Assert.assertNotNull(page.getContentStream(0).getBytes());

        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR),
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE),
    })
    public void correctSimpleDoc4() throws IOException {
        String filename = sourceFolder + "correctSimpleDoc4.pdf";

        PdfReader reader = new PdfReader(filename);
        try {
            //NOTE test with abnormal object declaration that iText can't resolve.
            PdfDocument document = new PdfDocument(reader);
            Assert.fail("Expect exception");
        } catch (PdfException e) {
            Assert.assertEquals( PdfException.InvalidPageStructurePagesPagesMustBePdfDictionary, e.getMessage());
        } finally {
            reader.close();
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest01() throws IOException {
        String filename = sourceFolder + "OnlyTrailer.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest02() throws IOException {
        String filename = sourceFolder + "CompressionShift1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());


        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest03() throws IOException {
        String filename = sourceFolder + "CompressionShift2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest04() throws IOException {
        String filename = sourceFolder + "CompressionWrongObjStm.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest05() throws IOException {
        String filename = sourceFolder + "CompressionWrongShift.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    public void fixPdfTest06() throws IOException {
        String filename = sourceFolder + "InvalidOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need fixXref()", reader.hasFixedXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE, count = 2))
    public void fixPdfTest07() throws IOException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences1.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (ClassCastException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest08() throws IOException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        Assert.assertEquals(author, document.getDocumentInfo().getAuthor());
        Assert.assertEquals(creator, document.getDocumentInfo().getCreator());
        Assert.assertEquals(title, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest09() throws IOException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        Assert.assertEquals(author, document.getDocumentInfo().getAuthor());
        Assert.assertEquals(creator, document.getDocumentInfo().getCreator());
        Assert.assertEquals(title, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE, count = 1))
    public void fixPdfTest10() throws IOException {
        String filename = sourceFolder + "XRefSectionWithFreeReferences4.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        Assert.assertEquals(null, document.getDocumentInfo().getAuthor());
        Assert.assertEquals(null, document.getDocumentInfo().getCreator());
        Assert.assertEquals(null, document.getDocumentInfo().getTitle());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest11() throws IOException {
        String filename = sourceFolder + "XRefSectionWithoutSize.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest12() throws IOException {
        String filename = sourceFolder + "XRefWithBreaks.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE)
    })
    public void fixPdfTest13() throws IOException {
        String filename = sourceFolder + "XRefWithInvalidGenerations1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());
        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < 10; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));

        }

        boolean exception = false;

        int i;
        PdfObject fontF1 = document.getPage(997).getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.Font).get(new PdfName("F1"));
        Assert.assertTrue(fontF1 instanceof PdfNull);

        //There is a generation number mismatch in xref table and object for 3093
        try {
            document.getPdfObject(3093);
        } catch (com.itextpdf.io.IOException ex) {
            exception = true;
        }
        Assert.assertTrue(exception);
        exception = false;

        try {
            for (i = 11; i < document.getNumberOfPages() + 1; i++) {
                PdfPage page = document.getPage(i);
                page.getContentStream(0).getBytes();
            }
        } catch (PdfException ex) {
            exception = true;
        }
        Assert.assertFalse(exception);
        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE)
    })
    public void fixPdfTest14() throws IOException {
        String filename = sourceFolder + "XRefWithInvalidGenerations2.pdf";

        PdfReader reader = new PdfReader(filename);
        boolean exception = false;
        try {
            new PdfDocument(reader);
        } catch (PdfException ex) {
            exception = true;
        }

        Assert.assertTrue(exception);
        reader.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest15() throws IOException {
        String filename = sourceFolder + "XRefWithInvalidGenerations3.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    public void fixPdfTest16() throws IOException {
        String filename = sourceFolder + "XrefWithInvalidOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertFalse("No need in fixXref()", reader.hasFixedXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        Assert.assertTrue("Need live fixXref()", reader.hasFixedXref());

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest17() throws IOException {
        String filename = sourceFolder + "XrefWithNullOffsets.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
        }

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void fixPdfTest18() throws IOException {
        String filename = sourceFolder + "noXrefAndTrailerWithInfo.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1, pageCount);

        Assert.assertTrue(document.getDocumentInfo().getProducer().contains("iText Group NV (AGPL-version)"));

        document.close();
    }

    @Test
    public void appendModeWith1000Pages() throws IOException {
        String filename = sourceFolder + "1000PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        document.close();
    }

    @Test
    public void appendModeWith1000PagesWithCompression() throws IOException {
        String filename = sourceFolder + "1000PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(1000, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        document.close();
    }

    @Test
    public void appendModeWith10Pages() throws IOException {
        String filename = sourceFolder + "10PagesDocumentAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        document.close();
    }

    @Test
    public void appendModeWith10PagesWithCompression() throws IOException {
        String filename = sourceFolder + "10PagesDocumentWithFullCompressionAppended.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertFalse("No need in rebuildXref()", reader.hasRebuiltXref());

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void appendModeWith10PagesFix1() throws IOException {
        String filename = sourceFolder + "10PagesDocumentAppendedFix1.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);
        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());
        Assert.assertNotNull("Invalid trailer", document.getTrailer().get(PdfName.ID));

        document.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR, count = 1))
    public void appendModeWith10PagesFix2() throws IOException {
        String filename = sourceFolder + "10PagesDocumentAppendedFix2.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        int pageCount = document.getNumberOfPages();
        Assert.assertEquals(10, pageCount);

        for (int i = 1; i < document.getNumberOfPages() + 1; i++) {
            PdfPage page = document.getPage(i);
            String content = new String(page.getContentStream(0).getBytes());
            Assert.assertFalse(content.length() == 0);
            content = new String(page.getContentStream(1).getBytes());
            Assert.assertTrue(content.contains("(" + i + ")"));
            content = new String(page.getContentStream(2).getBytes());
            Assert.assertTrue(content.contains("Append mode"));
        }

        Assert.assertTrue("Need rebuildXref()", reader.hasRebuiltXref());
        Assert.assertNotNull("Invalid trailer", document.getTrailer().get(PdfName.ID));

        document.close();
    }

    @Test
    public void incorrectXrefSizeInTrailer() throws IOException {
        String filename = sourceFolder + "HelloWorldIncorrectXRefSizeInTrailer.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assert.assertFalse("Need rebuildXref()", reader.hasRebuiltXref());
        Assert.assertNotNull("Invalid trailer", document.getTrailer().get(PdfName.ID));

        document.close();
    }

    @Test
    public void incorrectXrefSizeInTrailerAppend() throws IOException {
        String filename = sourceFolder + "10PagesDocumentAppendedIncorrectXRefSize.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument document = new PdfDocument(reader);

        Assert.assertFalse("Need rebuildXref()", reader.hasRebuiltXref());
        Assert.assertNotNull("Invalid trailer", document.getTrailer().get(PdfName.ID));

        document.close();
    }


    @Test(timeout = 1000)
    public void StreamLengthCorrection1() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "10PagesDocumentWithInvalidStreamLength.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            int pageCount = pdfDoc.getNumberOfPages();
            for (int k = 1; k < pageCount + 1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                byte[] content = page.getFirstContentStream().getBytes();
                Assert.assertEquals(57, content.length);
            }
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection2() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingLength1.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection3() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingLength2.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(697, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection4() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingLength3.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection5() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingLength4.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection6() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength1.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection7() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength2.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(696, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection8() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "simpleCanvasWithDrawingWithInvalidStreamLength3.pdf";
            PdfReader.correctStreamLength = true;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            PdfPage page = pdfDoc.getPage(1);
            page.getPdfObject().get(PdfName.MediaBox);
            byte[] content = page.getFirstContentStream().getBytes();
            Assert.assertEquals(697, content.length);
            pdfDoc.close();
        }
    }

    @Test(timeout = 1000)
    public void StreamLengthCorrection9() throws IOException {
        synchronized (this) {
            String filename = sourceFolder + "10PagesDocumentWithInvalidStreamLength2.pdf";
            PdfReader.correctStreamLength = false;

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
            int pageCount = pdfDoc.getNumberOfPages();
            for (int k = 1; k < pageCount + 1; k++) {
                PdfPage page = pdfDoc.getPage(k);
                page.getPdfObject().get(PdfName.MediaBox);
                byte[] content = page.getFirstContentStream().getBytes();
                Assert.assertEquals(20, content.length);
            }
            pdfDoc.close();
            PdfReader.correctStreamLength = true;
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void freeReferencesTest() throws IOException {
        String filename = sourceFolder + "freeReferences.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assert.assertNull(pdfDoc.getPdfObject(8));
        //Assert.assertFalse(pdfDoc.getReader().fixedXref);
        Assert.assertFalse(pdfDoc.getReader().rebuiltXref);

        pdfDoc.close();
    }

    @Test
    public void freeReferencesTest02() throws IOException, InterruptedException {

        String cmpFile = sourceFolder + "cmp_freeReferences02.pdf";
        String outputFile = destinationFolder + "freeReferences02.pdf";
        String inputFile = sourceFolder + "freeReferences02.pdf";

        PdfWriter writer = new PdfWriter(outputFile);
        PdfReader reader = new PdfReader(inputFile);

        PdfDocument inputPdfDocument = new PdfDocument(reader);
        PdfDocument outputPdfDocument = new PdfDocument(writer);

        int lastPage = inputPdfDocument.getNumberOfPages();
        inputPdfDocument.copyPagesTo(lastPage, lastPage, outputPdfDocument);

        inputPdfDocument.close();
        outputPdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void pdfVersionTest() throws IOException {
        String filename = sourceFolder + "hello.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));

        Assert.assertEquals(PdfVersion.PDF_1_4, pdfDoc.getPdfVersion());

        pdfDoc.close();
    }

    @Test
    public void zeroUpdateTest() throws IOException {
        String filename = sourceFolder + "stationery.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

//      Test such construction:
//      xref
//      0 0
//      trailer
//      <</Size 27/Root 1 0 R/Info 12 0 R//Prev 245232/XRefStm 244927>>
//      startxref
        Assert.assertFalse(reader.hasFixedXref());
        Assert.assertFalse(reader.hasRebuiltXref());
        Assert.assertTrue(((PdfDictionary) pdfDoc.getPdfObject(1)).containsKey(PdfName.AcroForm));
        pdfDoc.close();
    }

    @Test
    public void incrementalUpdateWithOnlyZeroObjectUpdate() throws IOException {
        String filename = sourceFolder + "pdfReferenceUpdated.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);

        Assert.assertFalse(reader.hasFixedXref());
        Assert.assertFalse(reader.hasRebuiltXref());

        // problem that is tested here originally was found because the StructTreeRoot dictionary wasn't read
        Assert.assertTrue(pdfDoc.isTagged());
        pdfDoc.close();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE, count =1),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR),
            @LogMessage(messageTemplate = LogMessageConstant.ENCOUNTERED_INVALID_MCR)})
    public void wrongTagStructureFlushingTest() throws IOException {
        //wrong /Pg number
        String source = sourceFolder + "wrongTagStructureFlushingTest.pdf";
        String dest = destinationFolder + "wrongTagStructureFlushingTest.pdf";
        PdfDocument   pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(dest));
        pdfDoc.setTagged();
        Assert.assertEquals(PdfNull.PDF_NULL, ((PdfDictionary)pdfDoc.getPdfObject(12)).get(PdfName.Pg));
        pdfDoc.close();
    }

    @Test
    @Ignore("DEVSIX-2649")
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE, count =1),
            @LogMessage(messageTemplate = LogMessageConstant.XREF_ERROR)})
    public void wrongStructureFlushingTest() throws IOException {
        //TODO: update after DEVSIX-2649 fix
        //wrong /key number
        String source = sourceFolder + "wrongStructureFlushingTest.pdf";
        String dest = destinationFolder + "wrongStructureFlushingTest.pdf";
        PdfDocument   pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(dest));
        pdfDoc.close();
    }

    @Test
    public void readerReuseTest() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.PdfReaderHasBeenAlreadyUtilized);

        String filename = sourceFolder + "hello.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc1 = new PdfDocument(reader);
        PdfDocument pdfDoc2 = new PdfDocument(reader);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_INDIRECT_REFERENCE))
    public void hugeInvalidIndRefObjNumberTest() throws IOException {
        String filename = sourceFolder + "hugeIndRefObjNum.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfObject pdfObject = pdfDoc.getPdfObject(4);
        Assert.assertTrue(pdfObject.isDictionary());
        Assert.assertEquals(PdfNull.PDF_NULL, ((PdfDictionary)pdfObject).get(PdfName.Pg));

        pdfDoc.close();
    }

    @Test
    @Ignore("DEVSIX-2133")
    public void testFileIsNotLockedOnException() throws IOException {
        File nonPdfFileName = new File(sourceFolder + "text_file.txt");
        Assert.assertTrue(nonPdfFileName.exists());
        boolean exceptionThrown = false;
        try {
            PdfReader reader = new PdfReader(nonPdfFileName);
        } catch (com.itextpdf.io.IOException e) {
            exceptionThrown = true;

            // File should be available for writing
            OutputStream stream = FileUtil.getFileOutputStream(nonPdfFileName);
            stream.write(new byte[] {0});
        }
        Assert.assertTrue(exceptionThrown);
    }

    private boolean objectTypeEqualTo(PdfObject object, PdfName type) {
        PdfName objectType = ((PdfDictionary) object).getAsName(PdfName.Type);
        return type.equals(objectType);
    }

    /**
     * Returns the current memory use.
     *
     * @return the current memory use
     */
    private static long getMemoryUse() {
        garbageCollect();
        garbageCollect();
        garbageCollect();
        garbageCollect();
        long totalMemory = Runtime.getRuntime().totalMemory();
        garbageCollect();
        garbageCollect();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory);
    }

    /**
     * Makes sure all garbage is cleared from the memory.
     */
    private static void garbageCollect() {
        try {
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
            System.gc();
            Thread.sleep(200);
            System.runFinalization();
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static PdfDictionary getTestPdfDictionary() {
        HashMap<PdfName, PdfObject> tmpMap = new HashMap<PdfName, PdfObject>();
        tmpMap.put(new PdfName("b"), new PdfName("c"));
        return new PdfDictionary(tmpMap);
    }
}
