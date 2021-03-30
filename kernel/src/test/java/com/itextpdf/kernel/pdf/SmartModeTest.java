/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SmartModeTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/SmartModeTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/SmartModeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void smartModeSameResourcesCopyingAndFlushing() throws IOException, InterruptedException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingAndFlushing.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameResourcesCopyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

        for (String srcFile : srcFiles) {
            PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
            sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
            sourceDoc.close();

            outputDoc.flushCopiedObjects(sourceDoc);
        }

        outputDoc.close();

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertTrue(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertTrue(page1ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameResourcesCopyingModifyingAndFlushing() throws IOException {
        String outFile = destinationFolder + "smartModeSameResourcesCopyingModifyingAndFlushing.pdf";
        String[] srcFiles = new String[]{
                sourceFolder + "indirectResourcesStructure.pdf",
                sourceFolder + "indirectResourcesStructure2.pdf"
        };
        boolean exceptionCaught = false;

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

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
                    Assert.assertEquals(2, i);
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
            Assert.fail();
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

        PdfDocument outputDoc = new PdfDocument(new PdfWriter(outFile, new WriterProperties().useSmartMode()));

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

        PdfDocument assertDoc = new PdfDocument(new PdfReader(outFile));
        PdfIndirectReference page1ResFontObj = assertDoc.getPage(1).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page2ResFontObj = assertDoc.getPage(2).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();
        PdfIndirectReference page3ResFontObj = assertDoc.getPage(3).getPdfObject().getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.Font).getIndirectReference();

        Assert.assertFalse(page1ResFontObj.equals(page2ResFontObj));
        Assert.assertFalse(page1ResFontObj.equals(page3ResFontObj));
        Assert.assertFalse(page2ResFontObj.equals(page3ResFontObj));
        assertDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void pageCopyAsFormXObjectWithInheritedResourcesTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";
        String srcFile = sourceFolder + "pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";
        String destFile = destinationFolder + "pageCopyAsFormXObjectWithInheritedResourcesTest.pdf";

        PdfDocument origPdf = new PdfDocument(new PdfReader(srcFile));
        PdfDocument copyPdfX = new PdfDocument(new PdfWriter(destFile).setSmartMode(true));
        PdfDictionary pages = origPdf.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
        if (pages != null) {
            for (int i = 1; i < origPdf.getNumberOfPages() + 1; i++) {
                PdfPage origPage = origPdf.getPage(i);
                Rectangle ps = origPage.getPageSize();
                PdfPage page = copyPdfX.addNewPage(new PageSize(ps));
                PdfCanvas canvas = new PdfCanvas(page);
                PdfFormXObject pageCopy = origPage.copyAsFormXObject(copyPdfX);
                canvas.addXObject(pageCopy, 0, 0);
            }
        }

        copyPdfX.close();
        origPdf.close();

        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameImageResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sameImageResources.pdf";
        String outFile = destinationFolder + "smartModeSameImageResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameImageResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(outFile).setSmartMode(true))) {

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

            Assert.assertEquals(page1ImgRes, page2ImgRes);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameColorSpaceResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "colorSpaceResource.pdf";
        String outFile = destinationFolder + "smartModeSameColorSpaceResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameColorSpaceResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(outFile).setSmartMode(true))) {

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
            Assert.assertEquals(page1CsRes, page2CsRes);

            PdfIndirectReference page1CsStm = newDoc.getPage(1).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ColorSpace).getAsArray(new PdfName("CS0"))
                    .getAsStream(1).getIndirectReference();

            PdfIndirectReference page2CsStm = newDoc.getPage(2).getPdfObject()
                    .getAsDictionary(PdfName.Resources)
                    .getAsDictionary(PdfName.ColorSpace).getAsArray(new PdfName("CS0"))
                    .getAsStream(1).getIndirectReference();

            Assert.assertEquals(page1CsStm, page2CsStm);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeSameExtGStateResourcesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "extGStateResource.pdf";
        String outFile = destinationFolder + "smartModeSameExtGStateResources.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeSameExtGStateResources.pdf";

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(outFile).setSmartMode(true))) {

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

            Assert.assertEquals(page1GsRes, page2GsRes);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInTaggedPdfTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleTaggedDocument.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInTaggedPdf.pdf";
        String cmpFile = sourceFolder + "cmp_smartModeCopyingInTaggedPdf.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            pdfDest.setTagged();

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            Assert.assertNotNull(pdfDest.getStructTreeRoot()
                    .getKidsObject().getAsDictionary(0).getAsArray(PdfName.K)
                    .getAsDictionary(0).get(PdfName.K));
        }

        Assert.assertNull(new CompareTool().compareByContent(dstFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInPdfWithIdenticalPagesTaggedTest() throws IOException {
        String srcFile = sourceFolder + "docWithAllPagesIdenticalTagged.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithIdenticalPagesTagged.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            pdfDest.setTagged();

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedPageObj = pdfDest.getPage(1).getPdfObject().getIndirectReference();

            PdfIndirectReference expectedContStm = pdfDest.getPage(1).getPdfObject()
                    .getAsStream(PdfName.Contents).getIndirectReference();

            for (int i = 2; i <= 10; i++) {
                PdfIndirectReference pageObj = pdfDest.getPage(i).getPdfObject().getIndirectReference();

                Assert.assertNotEquals(expectedPageObj, pageObj);

                PdfIndirectReference pageContentStm = pdfDest.getPage(i).getPdfObject()
                        .getAsStream(PdfName.Contents).getIndirectReference();

                Assert.assertEquals(expectedContStm, pageContentStm);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfWithIdenticalPagesTest() throws IOException {
        String srcFile = sourceFolder + "docWithAllPagesIdenticalNotTagged.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithIdenticalPagesNotTagged.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference expectedPageObj = pdfDest.getPage(1).getPdfObject().getIndirectReference();

            PdfIndirectReference expectedContStm = pdfDest.getPage(1).getPdfObject()
                    .getAsStream(PdfName.Contents).getIndirectReference();

            for (int i = 2; i <= 10; i++) {
                PdfIndirectReference pageObj = pdfDest.getPage(i).getPdfObject().getIndirectReference();

                Assert.assertNotEquals(expectedPageObj, pageObj);

                PdfIndirectReference pageContentStm = pdfDest.getPage(i).getPdfObject()
                        .getAsStream(PdfName.Contents).getIndirectReference();

                Assert.assertEquals(expectedContStm, pageContentStm);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfSamePagesDifferentXObjectsTest() throws IOException {
        String srcFile = sourceFolder + "identicalPagesDifferentXObjects.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfSamePagesDifferentXObjects.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

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

                Assert.assertEquals(expectedImgRes, pagesImgRes);
            }
        }
    }

    @Test
    public void smartCopyingOfArrayWithStringsTest() throws IOException {
        String srcFile = sourceFolder + "keyValueStructure.pdf";
        String dstFile = destinationFolder + "smartCopyingOfArrayWithStrings.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

            try (PdfDocument pdfSrc = new PdfDocument(new PdfReader(srcFile))) {
                pdfSrc.copyPagesTo(1, pdfSrc.getNumberOfPages(), pdfDest);
            }

            PdfIndirectReference key1Ref = pdfDest.getPage(1).getPdfObject()
                    .getAsDictionary(new PdfName("Key1")).getIndirectReference();

            PdfIndirectReference key2Ref = pdfDest.getPage(2).getPdfObject()
                    .getAsDictionary(new PdfName("Key2")).getIndirectReference();

            // Currently smart mode copying doesn't affect any other objects except streams and dictionaries
            Assert.assertNotEquals(key1Ref, key2Ref);
        }
    }

    @Test
    public void smartCopyingOfNestedIndirectDictionariesTest() throws IOException {
        String srcFile = sourceFolder + "nestedIndirectDictionaries.pdf";
        String dstFile = destinationFolder + "smartCopyingOfNestedIndirectDictionariesTest.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

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
            Assert.assertEquals(key1Page1Ref, key1Page2Ref);
            Assert.assertEquals(key2Page1Ref, key2Page2Ref);
            Assert.assertEquals(key3Page1Ref, key3Page2Ref);
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

        try (PdfDocument outputDoc = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {
            outputDoc.initializeOutlines();

            for (String srcFile : srcFiles) {
                PdfDocument sourceDoc = new PdfDocument(new PdfReader(srcFile));
                sourceDoc.copyPagesTo(1, sourceDoc.getNumberOfPages(), outputDoc);
                sourceDoc.close();
            }
        }

        Assert.assertNull(new CompareTool().compareByContent(dstFile, cmpFile, destinationFolder));
    }

    @Test
    public void smartModeCopyingInPdfWithLinksOnOnePageTest() throws IOException {
        String srcFile = sourceFolder + "identical100PagesDiffObjectsLinksOnOnePage.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithLinksOnOnePage.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

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

                Assert.assertEquals(expectedImgRes, pagesImgRes);
            }
        }
    }

    @Test
    public void smartModeCopyingInPdfWithDiffImagesTest() throws IOException {
        String srcFile = sourceFolder + "docWithDifferentImages.pdf";
        String dstFile = destinationFolder + "smartModeCopyingInPdfWithDiffImages.pdf";

        try (PdfDocument pdfDest = new PdfDocument(new PdfWriter(dstFile, new WriterProperties().useSmartMode()))) {

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

                Assert.assertNotEquals(expectedImgRes, pagesImgRes);
            }
        }
    }
}
