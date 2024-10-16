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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class SmartModeTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/SmartModeTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/SmartModeTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void smartModeSameResourcesCopyingAndFlushing() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingAndFlushing.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile, new WriterProperties().useSmartMode()));

        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(CompareTool.createOutputReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assertions.assertTrue(page1ResFontObj.equals(page2ResFontObj));
        Assertions.assertTrue(page1ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing() throws IOException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };
        boolean exceptionCaught = false;

        PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfCanvas canvas;
                try {
                    canvas = new PdfCanvas(outputDoc.getPage(i));
                } catch (NullPointerException expected) {
                    // Smart mode makes it possible to share objects coming from different source documents.
                    // Flushing one object documents might make it impossible to modify further copied objects.
                    Assertions.assertEquals(2, i);
                    exceptionCaught = true;
                    break;
                }
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            if (exceptionCaught) {
                break;
            }

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        if (!exceptionCaught) {
            Assertions.fail();
        }
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingModifyingAndFlushing_ensureObjectFresh.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile, new WriterProperties().useSmartMode()));

        int lastPageNum = 1;
        PdfFont font = PdfFontFactory.createFont();
        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            for (int i = 1; i <= sourceDoc.getNumberOfPages(); ++i) {
                PdfDictionary srcRes = sourceDoc.getPage(i).getPdfObject().getAsDictionary(PdfName.Resources);

                // Ensures that objects copied to the output document are fresh,
                // i.e. are not reused from already copied objects cache.
                boolean ensureObjectIsFresh = true;
                // it's crucial to copy first inner objects and then the container object!
                for (PdfObject v : srcRes.values()) {
                    if (v.getIndirectReference() != null) {
                        // We are not interested in returned copied objects instances, they will be picked up by
                        // general copying mechanism from copied objects cache by default.
                        v.copyTo(outputDoc, ensureObjectIsFresh);
                    }
                }
                if (srcRes.getIndirectReference() != null) {
                    srcRes.copyTo(outputDoc, ensureObjectIsFresh);
                }
            }
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            int i;
            for (i = lastPageNum; i <= outputDoc.getNumberOfPages(); ++i) {
                PdfPage page = outputDoc.getPage(i);
                PdfCanvas canvas = new PdfCanvas(page);
                canvas.beginText().moveText(36, 36).setFontAndSize(font, 12).showText("Page " + i).endText();
            }
            lastPageNum = i;

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(CompareTool.createOutputReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assertions.assertFalse(page1ResFontObj.equals(page2ResFontObj));
        Assertions.assertFalse(page1ResFontObj.equals(page3ResFontObj));
        Assertions.assertFalse(page2ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void pageCopyAsFormXObjectWithInheritedResourcesTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";
        String srcFile = sourceFolder + "pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";
        String destFile = destinationFolder + "pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";

        PdfDocument origPdf = new PdfDocument(new PdfReader(srcFile));
        PdfDocument copyPdfX = new PdfDocument(CompareTool.createTestPdfWriter(destFile).setSmartMode(true));
        PdfDictionary pages = origPdf.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
        if (pages != null) {
            for (int i = 1; i < origPdf.getNumberOfPages() + 1; i++) {
                PdfPage origPage = origPdf.getPage(i);
                Rectangle ps = origPage.getPageSize();
                PdfPage page = copyPdfX.addNewPage(new PageSize(ps));
                PdfCanvas canvas = new PdfCanvas(page);
                PdfFormXObject pageCopy = origPage.copyAsFormXObject(copyPdfX);
                canvas.addXObjectAt(pageCopy, 0, 0);
            }
        }

        copyPdfX.close();
        origPdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameImageResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sameImageResources.pdf";
        String outFile = destinationFolder + "smartModeSameImageResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameImageResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile).setSmartMode(true))) {

            try (PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile))) {
                srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), newDoc);
            }

            PdfIndirectReference page1ImgRes = newDoc.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .getAsStream(new PdfName("Im0")).getIndirectReference();

            PdfIndirectReference page2ImgRes = newDoc.getPage(2).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .getAsStream(new PdfName("Im0")).getIndirectReference();

            Assertions.assertEquals(page1ImgRes, page2ImgRes);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameColorSpaceResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "colorSpaceResource.pdf";
        String outFile = destinationFolder + "smartModeSameColorSpaceResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameColorSpaceResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile).setSmartMode(true))) {

            for (int i = 0; i < 2; i++) {
                try (PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile))) {
                    srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), newDoc);
                }
            }

            PdfObject page1CsRes = newDoc.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .get(new PdfName("Im0"))
                    .getIndirectReference();

            PdfObject page2CsRes = newDoc.getPage(2).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .get(new PdfName("Im0"))
                    .getIndirectReference();

            // It's expected that indirect arrays are not processed by smart mode.
            // Smart mode only merges duplicate dictionaries and streams.
            Assertions.assertEquals(page1CsRes, page2CsRes);

            PdfIndirectReference page1CsStm = newDoc.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ColorSpace).getAsArray(new PdfName("CS0"))
                    .getAsStream(1).getIndirectReference();

            PdfIndirectReference page2CsStm = newDoc.getPage(2).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ColorSpace).getAsArray(new PdfName("CS0"))
                    .getAsStream(1).getIndirectReference();

            Assertions.assertEquals(page1CsStm, page2CsStm);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameExtGStateResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "extGStateResource.pdf";
        String outFile = destinationFolder + "smartModeSameExtGStateResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameExtGStateResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile).setSmartMode(true))) {

            for (int i = 0; i < 2; i++) {
                try (PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile))) {
                    srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), newDoc);
                }
            }

            PdfIndirectReference page1GsRes = newDoc.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ExtGState)
                    .getAsDictionary(new PdfName("Gs1")).getIndirectReference();

            PdfIndirectReference page2GsRes = newDoc.getPage(2).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ExtGState)
                    .getAsDictionary(new PdfName("Gs1")).getIndirectReference();

            Assertions.assertEquals(page1GsRes, page2GsRes);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInTaggedPdfTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleTaggedDocument.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInTaggedPdf.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeCopyingInTaggedPdf.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            pdfDest.setTagged();

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            Assertions.assertNotNull(pdfDest.getStructTreeRoot()
                    .getKidsObject().getAsDictionary(0).getAsArray(PdfName.K)
                    .getAsDictionary(0).get(PdfName.K));
        }

        Assertions.assertNull(new CompareTool().compareByContent(dstFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInPdfWithIdenticalPagesTaggedTest() throws IOException {
        String srcFile = sourceFolder + "docWithAllPagesIdenticalTagged.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithIdenticalPagesTagged.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            pdfDest.setTagged();

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedPageObj = pdfDest.getPage(1).getPdfObject().getIndirectReference();

            PdfIndirectReference expectedContStm = pdfDest.getPage(1).getPdfObject()
                    .getAsStream(PdfName.Contents).getIndirectReference();

            for (int i = 2; i <= 10; i++) {
                PdfIndirectReference pageObj = pdfDest.getPage(i).getPdfObject().getIndirectReference();

                Assertions.assertNotEquals(expectedPageObj, pageObj);

                PdfIndirectReference pageContentStm = pdfDest.getPage(i).getPdfObject()
                        .getAsStream(PdfName.Contents).getIndirectReference();

                Assertions.assertEquals(expectedContStm, pageContentStm);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfWithIdenticalPagesTest() throws IOException {
        String srcFile = sourceFolder + "docWithAllPagesIdenticalNotTagged.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithIdenticalPagesNotTagged.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedPageObj = pdfDest.getPage(1).getPdfObject().getIndirectReference();

            PdfIndirectReference expectedContStm = pdfDest.getPage(1).getPdfObject()
                    .getAsStream(PdfName.Contents).getIndirectReference();

            for (int i = 2; i <= 10; i++) {
                PdfIndirectReference pageObj = pdfDest.getPage(i).getPdfObject().getIndirectReference();

                Assertions.assertNotEquals(expectedPageObj, pageObj);

                PdfIndirectReference pageContentStm = pdfDest.getPage(i).getPdfObject()
                        .getAsStream(PdfName.Contents).getIndirectReference();

                Assertions.assertEquals(expectedContStm, pageContentStm);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfSamePagesDifferentXObjectsTest() throws IOException {
        String srcFile = sourceFolder + "identicalPagesDifferentXObjects.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfSamePagesDifferentXObjects.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedImgRes = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .getAsStream(new PdfName("Im1")).getIndirectReference();

            for (int i = 2; i <= 99; i++) {
                PdfIndirectReference pagesImgRes = pdfDest.getPage(i).getPdfObject()
                        .getAsDictionary(PdfName.Resources)
                        .getAsDictionary(PdfName.XObject)
                        .getAsStream(new PdfName("Im1")).getIndirectReference();

                Assertions.assertEquals(expectedImgRes, pagesImgRes);
            }
        }
    }

    @Test
    public void smartCopyingOfArrayWithStringsTest() throws IOException {
        String srcFile = sourceFolder + "keyValueStructure.pdf";
        String dstFile = destinationFolder + "smartCopyingOfArrayWithStrings.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference key1Ref = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(new PdfName("Key1")).getIndirectReference();

            PdfIndirectReference key2Ref = pdfDest.getPage(2).getPdfObject()
                    .getAsDictionary(new PdfName("Key2")).getIndirectReference();

            // Currently smart mode copying doesn't affect any other objects except streams and dictionaries
            Assertions.assertNotEquals(key1Ref, key2Ref);
        }
    }

    @Test
    public void smartCopyingOfNestedIndirectDictionariesTest() throws IOException {
        String srcFile = sourceFolder + "nestedIndirectDictionaries.pdf";
        String dstFile = destinationFolder + "smartCopyingOfNestedIndirectDictionariesTest.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfObject key1Page1Ref = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(new PdfName("Key1")).getIndirectReference();

            PdfIndirectReference key1Page2Ref = pdfDest.getPage(2).getPdfObject()
                    .getAsDictionary(new PdfName("Key1")).getIndirectReference();

            PdfIndirectReference key2Page1Ref = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(new PdfName("Key1"))
                    .getAsDictionary(new PdfName("Key2")).getIndirectReference();

            PdfIndirectReference key2Page2Ref = pdfDest.getPage(2).getPdfObject()
                    .getAsDictionary(new PdfName("Key1"))
                    .getAsDictionary(new PdfName("Key2")).getIndirectReference();

            PdfIndirectReference key3Page1Ref = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(new PdfName("Key1"))
                    .getAsDictionary(new PdfName("Key2"))
                    .getAsArray(new PdfName("Key3")).getIndirectReference();

            PdfIndirectReference key3Page2Ref = pdfDest.getPage(2).getPdfObject()
                    .getAsDictionary(new PdfName("Key1"))
                    .getAsDictionary(new PdfName("Key2"))
                    .getAsArray(new PdfName("Key3")).getIndirectReference();

            // Currently smart mode copying doesn't affect any other objects except streams and dictionaries
            Assertions.assertEquals(key1Page1Ref, key1Page2Ref);
            Assertions.assertEquals(key2Page1Ref, key2Page2Ref);
            Assertions.assertEquals(key3Page1Ref, key3Page2Ref);
        }
    }

    @Test
    public void smartModeSeparatedOutlinesCopyingTest() throws IOException, InterruptedException {
        String dstFile = destinationFolder + "smartModeSeparatedOutlinesCopying.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSeparatedOutlinesCopying.pdf";
        String[] srcFiles = new String[] {
                sourceFolder + "separatedOutlinesCopying.pdf",
                sourceFolder + "separatedOutlinesCopying.pdf"
        };

        try (PdfDocument outputDoc = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            outputDoc.initializeOutlines();

            for (String srcFile : srcFiles) {
                PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
                sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
                sourceDoc.close();
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(dstFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInPdfWithLinksOnOnePageTest() throws IOException {
        String srcFile = sourceFolder + "identical100PagesDiffObjectsLinksOnOnePage.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithLinksOnOnePage.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedImgRes = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .getAsStream(new PdfName("Im1")).getIndirectReference();

            for (int i = 2; i <= 99; i++) {
                PdfIndirectReference pagesImgRes = pdfDest.getPage(i).getPdfObject()
                        .getAsDictionary(PdfName.Resources)
                        .getAsDictionary(PdfName.XObject)
                        .getAsStream(new PdfName("Im1")).getIndirectReference();

                Assertions.assertEquals(expectedImgRes, pagesImgRes);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfWithDiffImagesTest() throws IOException {
        String srcFile = sourceFolder + "docWithDifferentImages.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithDiffImages.pdf";

        try (PdfDocument pdfDest = new PdfDocument(CompareTool.createTestPdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedImgRes = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.XObject)
                    .getAsStream(new PdfName("Im1")).getIndirectReference();

            for (int i = 2; i <= 99; i++) {
                PdfIndirectReference pagesImgRes = pdfDest.getPage(i).getPdfObject()
                        .getAsDictionary(PdfName.Resources)
                        .getAsDictionary(PdfName.XObject)
                        .getAsStream(new PdfName("Im1")).getIndirectReference();

                Assertions.assertNotEquals(expectedImgRes, pagesImgRes);
            }
        }
    }
}
