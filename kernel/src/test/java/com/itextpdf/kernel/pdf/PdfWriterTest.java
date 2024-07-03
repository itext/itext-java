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
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.OutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

@Tag("IntegrationTest")
public class PdfWriterTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfWriterTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void createEmptyDocument() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "emptyDocument.pdf"));
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "emptyDocument.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertNotNull(pdfDocument.getPage(1));
        String date = pdfDocument.getDocumentInfo().getPdfObject().getAsString(PdfName.CreationDate).getValue();
        Calendar cl = PdfDate.decode(date);
        double diff = DateTimeUtil.getUtcMillisFromEpoch(null) - DateTimeUtil.getUtcMillisFromEpoch(cl);
        String message = "Unexpected creation date. Different from now is " + (float) diff / 1000 + "s";
        Assertions.assertTrue(diff < 5000, message);
        pdfDocument.close();

    }

    @Test
    public void useObjectForMultipleTimes1() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "useObjectForMultipleTimes1.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes1.pdf");
    }

    @Test
    public void useObjectForMultipleTimes2() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "useObjectForMultipleTimes2.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        helloWorld.flush();
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes2.pdf");
    }

    @Test
    public void useObjectForMultipleTimes3() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "useObjectForMultipleTimes3.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        helloWorld.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes3.pdf");
    }

    @Test
    public void useObjectForMultipleTimes4() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "useObjectForMultipleTimes4.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        helloWorld.flush();
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes4.pdf");
    }

    private void validateUseObjectForMultipleTimesTest(String filename) throws IOException {
        PdfReader reader = CompareTool.createOutputReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary page = pdfDoc.getPage(1).getPdfObject();
        Assertions.assertNotNull(page);
        PdfDictionary helloWorld = page.getAsDictionary(new PdfName("HelloWorld"));
        Assertions.assertNotNull(helloWorld);
        PdfString world = helloWorld.getAsString(new PdfName("Hello"));
        Assertions.assertEquals("World", world.toString());
        helloWorld = pdfDoc.getCatalog().getPdfObject().getAsDictionary(new PdfName("HelloWorld"));
        Assertions.assertNotNull(helloWorld);
        world = helloWorld.getAsString(new PdfName("Hello"));
        Assertions.assertEquals("World", world.toString());
        pdfDoc.close();
    }

    /**
     * Copying direct objects. Objects of all types are added into document catalog.
     *
     * @throws IOException
     */
    @Test
    public void copyObject1() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject1_1.pdf"));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfArray aDirect = new PdfArray();
        ArrayList<PdfObject> tmpArray = new ArrayList<PdfObject>(2);
        tmpArray.add(new PdfNumber(1));
        tmpArray.add(new PdfNumber(2));
        aDirect.add(new PdfArray(tmpArray));
        aDirect.add(new PdfBoolean(true));
        TreeMap<PdfName, PdfObject> tmpMap = new TreeMap<PdfName, PdfObject>();
        tmpMap.put(new PdfName("one"), new PdfNumber(1));
        tmpMap.put(new PdfName("two"), new PdfNumber(2));
        aDirect.add(new PdfDictionary(tmpMap));
        aDirect.add(new PdfName("name"));
        aDirect.add(new PdfNull());
        aDirect.add(new PdfNumber(100));
        aDirect.add(new PdfString("string"));
        catalog1.put(new PdfName("aDirect"), aDirect);

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject1_2.pdf"));
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("aDirect"), aDirect.copyTo(pdfDoc2));

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject1_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfArray a = (PdfArray) catalog.get(new PdfName("aDirect"));
        Assertions.assertNotNull(a);
        Assertions.assertEquals(1, ((PdfNumber) ((PdfArray) a.get(0)).get(0)).intValue());
        Assertions.assertEquals(2, ((PdfNumber) ((PdfArray) a.get(0)).get(1)).intValue());
        Assertions.assertEquals(true, ((PdfBoolean) a.get(1)).getValue());
        Assertions.assertEquals(1, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("one"))).intValue());
        Assertions.assertEquals(2, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("two"))).intValue());
        Assertions.assertEquals(new PdfName("name"), a.get(3));
        Assertions.assertTrue(a.get(4).isNull());
        Assertions.assertEquals(100, ((PdfNumber) a.get(5)).intValue());
        Assertions.assertEquals("string", ((PdfString) a.get(6)).toUnicodeString());
        pdfDocument.close();

    }

    /**
     * Copying objects, some of those are indirect. Objects of all types are added into document catalog.
     *
     * @throws IOException
     */
    @Test
    public void copyObject2() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject2_1.pdf"));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfName aDirectName = new PdfName("aDirect");
        PdfArray aDirect = (PdfArray) new PdfArray().makeIndirect(pdfDoc1);
        ArrayList<PdfObject> tmpArray = new ArrayList<PdfObject>(2);
        tmpArray.add(new PdfNumber(1));
        tmpArray.add(new PdfNumber(2).makeIndirect(pdfDoc1));
        aDirect.add(new PdfArray(tmpArray));
        aDirect.add(new PdfBoolean(true));
        TreeMap<PdfName, PdfObject> tmpMap = new TreeMap<PdfName, PdfObject>();
        tmpMap.put(new PdfName("one"), new PdfNumber(1));
        tmpMap.put(new PdfName("two"), new PdfNumber(2).makeIndirect(pdfDoc1));
        aDirect.add(new PdfDictionary(tmpMap));
        aDirect.add(new PdfName("name"));
        aDirect.add(new PdfNull().makeIndirect(pdfDoc1));
        aDirect.add(new PdfNumber(100));
        aDirect.add(new PdfString("string"));
        catalog1.put(aDirectName, aDirect);
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copyObject2_1.pdf"));
        aDirect = (PdfArray) pdfDoc1R.getCatalog().getPdfObject().get(aDirectName);

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject2_2.pdf"));
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(aDirectName, aDirect.copyTo(pdfDoc2));

        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfArray a = catalog.getAsArray(new PdfName("aDirect"));
        Assertions.assertNotNull(a);
        Assertions.assertEquals(1, ((PdfNumber) ((PdfArray) a.get(0)).get(0)).intValue());
        Assertions.assertEquals(2, ((PdfArray) a.get(0)).getAsNumber(1).intValue());
        Assertions.assertEquals(true, ((PdfBoolean) a.get(1)).getValue());
        Assertions.assertEquals(1, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("one"))).intValue());
        Assertions.assertEquals(2, ((PdfDictionary) a.get(2)).getAsNumber(new PdfName("two")).intValue());
        Assertions.assertEquals(new PdfName("name"), a.get(3));

        Assertions.assertTrue(a.get(4).isNull());
        Assertions.assertEquals(100, ((PdfNumber) a.get(5)).intValue());
        Assertions.assertEquals("string", ((PdfString) a.get(6)).toUnicodeString());
        pdfDocument.close();
    }

    /**
     * Copy objects recursively.
     *
     * @throws IOException
     */
    @Test
    public void copyObject3() throws IOException {
        {
            PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject3_1.pdf"));
            PdfPage page1 = pdfDoc1.addNewPage();
            page1.flush();
            PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
            PdfArray arr1 = (PdfArray) new PdfArray().makeIndirect(pdfDoc1);
            PdfArray arr2 = (PdfArray) new PdfArray().makeIndirect(pdfDoc1);
            arr1.add(arr2);
            PdfDictionary dic1 = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc1);
            arr2.add(dic1);
            PdfDictionary dic2 = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc1);
            dic1.put(new PdfName("dic2"), dic2);
            PdfName arr1Name = new PdfName("arr1");
            dic2.put(arr1Name, arr1);
            catalog1.put(arr1Name, arr1);
            pdfDoc1.close();

            PdfDocument pdfDoc1R = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copyObject3_1.pdf"));
            arr1 = (PdfArray) pdfDoc1R.getCatalog().getPdfObject().get(arr1Name);

            PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject3_2.pdf"));
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.flush();
            PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
            catalog2.put(arr1Name, arr1.copyTo(pdfDoc2));

            pdfDoc1R.close();
            pdfDoc2.close();
        }

        {
            PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject3_2.pdf");
            PdfDocument pdfDocument = new PdfDocument(reader);
            Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
            PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
            PdfArray arr1 = catalog.getAsArray(new PdfName("arr1"));
            PdfArray arr2 = arr1.getAsArray(0);
            PdfDictionary dic1 = arr2.getAsDictionary(0);
            PdfDictionary dic2 = dic1.getAsDictionary(new PdfName("dic2"));
            Assertions.assertEquals(arr1, dic2.getAsArray(new PdfName("arr1")));
            pdfDocument.close();
        }
    }

    /**
     * Copies stream.
     *
     * @throws IOException
     */
    @Test
    public void copyObject4() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject4_1.pdf"));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfStream stream1 = (PdfStream) new PdfStream().makeIndirect(pdfDoc1);
        ArrayList<PdfObject> tmpArray = new ArrayList<PdfObject>(3);
        tmpArray.add(new PdfNumber(1));
        tmpArray.add(new PdfNumber(2));
        tmpArray.add(new PdfNumber(3));
        stream1.getOutputStream().write(new PdfArray(tmpArray));
        catalog1.put(new PdfName("stream"), stream1);
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copyObject4_1.pdf"));
        stream1 = (PdfStream) pdfDoc1R.getCatalog().getPdfObject().get(new PdfName("stream"));

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject4_2.pdf"));
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("stream"), stream1.copyTo(pdfDoc2));

        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject4_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfStream stream = (PdfStream) catalog.getAsStream(new PdfName("stream"));
        byte[] bytes = stream.getBytes();
        Assertions.assertArrayEquals(ByteUtils.getIsoBytes("[1 2 3]"), bytes);
        pdfDocument.close();
    }

    /**
     * Copies page.
     *
     * @throws IOException
     */
    @Test
    public void copyObject5() throws IOException {
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject5_1.pdf"));
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Page_1"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copyObject5_1.pdf"));
        page1 = pdfDoc1R.getPage(1);

        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject5_2.pdf"));
        PdfPage page2 = page1.copyTo(pdfDoc2);
        pdfDoc2.addPage(page2);
        page2.flush();
        page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(ByteUtils.getIsoBytes("%Page_2"));

        page2.flush();
        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject5_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertEquals(8, reader.trailer.getAsNumber(PdfName.Size).intValue());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        // getting content bytes results in adding '\n' for each content stream
        // so we should compare String with '\n' at the end
        Assertions.assertArrayEquals(ByteUtils.getIsoBytes("%Page_1\n"), bytes);
        bytes = pdfDocument.getPage(2).getContentBytes();
        Assertions.assertArrayEquals(ByteUtils.getIsoBytes("%Page_2\n"), bytes);
        pdfDocument.close();
    }

    /**
     * Copies object with different method overloads.
     *
     * @throws IOException
     */
    @Test
    public void copyObject6() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject6_1.pdf"));

        PdfDictionary helloWorld = (PdfDictionary) new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        pdfDoc = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "copyObject6_1.pdf"));
        helloWorld = (PdfDictionary) pdfDoc.getPage(1).getPdfObject().get(new PdfName("HelloWorld"));
        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject6_2.pdf"));
        PdfPage page1 = pdfDoc1.addNewPage();

        page1.getPdfObject().put(new PdfName("HelloWorldCopy1"), helloWorld.copyTo(pdfDoc1));
        page1.getPdfObject().put(new PdfName("HelloWorldCopy2"), helloWorld.copyTo(pdfDoc1, true));
        page1.getPdfObject().put(new PdfName("HelloWorldCopy3"), helloWorld.copyTo(pdfDoc1, false));
        page1.flush();

        pdfDoc.close();
        pdfDoc1.close();


        PdfReader reader = CompareTool.createOutputReader(destinationFolder + "copyObject6_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");

        PdfObject obj1 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy1"));

        PdfIndirectReference ref1 = obj1.getIndirectReference();
        Assertions.assertEquals(6, ref1.objNr);
        Assertions.assertEquals(0, ref1.genNr);

        PdfObject obj2 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy2"));

        PdfIndirectReference ref2 = obj2.getIndirectReference();
        Assertions.assertEquals(7, ref2.getObjNumber());
        Assertions.assertEquals(0, ref2.getGenNumber());

        PdfObject obj3 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy3"));

        PdfIndirectReference ref3 = obj3.getIndirectReference();
        Assertions.assertEquals(7, ref3.getObjNumber());
        Assertions.assertEquals(0, ref3.getGenNumber());

        pdfDocument.close();
    }

    /**
     * Attempts to copy from the document that is being written.
     *
     * @throws IOException
     */
    @Test()
    public void copyObject7() throws IOException {
        String exceptionMessage = null;

        PdfDocument pdfDoc1 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject6_1.pdf"));
        PdfDocument pdfDoc2 = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject6_2.pdf"));
        try {

            PdfPage page1 = pdfDoc1.addNewPage();
            PdfDictionary directDict = new PdfDictionary();
            PdfObject indirectDict = new PdfDictionary().makeIndirect(pdfDoc1);
            page1.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict);
            page1.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict);

            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict.copyTo(pdfDoc2));
            page2.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict.copyTo(pdfDoc2));
        } catch (PdfException ex) {
            exceptionMessage = ex.getMessage();
        } finally {
            pdfDoc1.close();
            pdfDoc2.close();
        }

        Assertions.assertEquals(
                KernelExceptionMessageConstant.CANNOT_COPY_INDIRECT_OBJECT_FROM_THE_DOCUMENT_THAT_IS_BEING_WRITTEN, exceptionMessage);
    }

    /**
     * Attempts to copy to copy with null document
     *
     * @throws IOException
     */
    @Test()
    public void copyObject8() throws IOException {
        String exceptionMessage = null;

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "copyObject6_1.pdf"));
        try {
            PdfPage page1 = pdfDoc.addNewPage();
            PdfDictionary directDict = new PdfDictionary();
            PdfObject indirectDict = new PdfDictionary().makeIndirect(pdfDoc);
            page1.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict);
            page1.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict);

            indirectDict.copyTo(null);

        } catch (PdfException ex) {
            exceptionMessage = ex.getMessage();
        } finally {
            pdfDoc.close();
        }

        Assertions.assertEquals(
                KernelExceptionMessageConstant.DOCUMENT_FOR_COPY_TO_CANNOT_BE_NULL, exceptionMessage);
    }

    @Test
    public void closeStream1() throws IOException {
        OutputStream fos = FileUtil.getFileOutputStream(destinationFolder + "closeStream1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        try {
            fos.write(1);
            Assertions.fail("Exception expected");
        } catch (Exception e) {
            //ignored
        }
    }

    @Test
    public void closeStream2() throws IOException {
        OutputStream fos = FileUtil.getFileOutputStream(destinationFolder + "closeStream2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCloseStream(false);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        fos.write(1);
    }

    @Test
    public void directInIndirectChain() throws IOException {
        String filename = destinationFolder + "directInIndirectChain.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        PdfArray level1 = new PdfArray();
        level1.add(new PdfNumber(1).makeIndirect(pdfDoc));
        PdfDictionary level2 = new PdfDictionary();
        level1.add(level2);
        PdfArray level3 = new PdfArray();
        level2.put(new PdfName("level3"), level3);
        level2.put(new PdfName("num"), new PdfNumber(2).makeIndirect(pdfDoc));
        level3.add(new PdfNumber(3).makeIndirect(pdfDoc));
        level3.add(new PdfNumber(3).makeIndirect(pdfDoc));
        PdfDictionary level4 = new PdfDictionary();
        level4.put(new PdfName("num"), new PdfNumber(4).makeIndirect(pdfDoc));
        level3.add(level4);
        PdfPage page1 = pdfDoc.addNewPage();

        page1.getPdfObject().put(new PdfName("test"), level1);

        pdfDoc.close();

        PdfReader reader = CompareTool.createOutputReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertEquals(1, pdfDocument.getNumberOfPages(), "Page count");
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assertions.assertEquals(PdfName.Page, page.get(PdfName.Type));
        pdfDocument.close();
    }

    @Test
    public void createPdfStreamByInputStream() throws IOException {
        String filename = destinationFolder + "createPdfStreamByInputStream.pdf";

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(filename));
        document.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = document.addNewPage();
        page.flush();

        String streamContent = "Some text content with strange symbols ∞²";
        PdfStream stream = new PdfStream(document, new ByteArrayInputStream(streamContent.getBytes()));
        stream.flush();
        int streamIndirectNumber = stream.getIndirectReference().getObjNumber();
        document.close();

//        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(filename);
//        Assertions.assertEquals("Rebuilt", false, reader.isRebuilt());
//        Assertions.assertNotNull(reader.getPageN(1));
//        String date = reader.getDocumentInfo().get("CreationDate");
//        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
//        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
//        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
//        Assertions.assertTrue(diff < 5000, message);
//        reader.close();

        PdfReader reader6 = CompareTool.createOutputReader(filename);
        document = new PdfDocument(reader6);
        Assertions.assertEquals(false, reader6.hasRebuiltXref(), "Rebuilt");
        Assertions.assertEquals(false, reader6.hasFixedXref(), "Fixed");
        PdfStream pdfStream = (PdfStream) document.getXref().get(streamIndirectNumber).getRefersTo();
        Assertions.assertArrayEquals(streamContent.getBytes(), pdfStream.getBytes(), "Stream by InputStream");
        document.close();
    }
}
