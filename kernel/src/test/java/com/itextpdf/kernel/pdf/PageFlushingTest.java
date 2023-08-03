/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PageFlushingTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PageFlushingTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PageFlushingTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void baseWriting01() throws IOException {
        // not all objects are made indirect before closing
        int total = 414;
        int flushedExpected = 0;
        int notReadExpected = 0;

        test("baseWriting01.pdf", DocMode.WRITING, FlushMode.NONE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void pageFlushWriting01() throws IOException {
        int total = 715;
        int flushedExpected = 400;
        int notReadExpected = 0;

        test("pageFlushWriting01.pdf", DocMode.WRITING, FlushMode.PAGE_FLUSH, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void unsafeDeepFlushWriting01() throws IOException {
        int total = 816;
        // 100 still hanging: new font dictionaries on every page shall not be flushed before closing
        int flushedExpected = 702;
        int notReadExpected = 0;

        test("unsafeDeepFlushWriting01.pdf", DocMode.WRITING, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushWriting01() throws IOException {
        int total = 715;
        int flushedExpected = 400;
        int notReadExpected = 0;

        test("appendModeFlushWriting01.pdf", DocMode.WRITING, FlushMode.APPEND_MODE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void baseReading01() throws IOException {
        int total = 817;
        int flushedExpected = 0;
        // link annots, line annots, actions and images: one hundred of each
        int notReadExpected = 402;

        test("baseReading01.pdf", DocMode.READING, FlushMode.NONE, PagesOp.READ,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepReading01() throws IOException {
        int total = 817;
        int flushedExpected = 0;
        int notReadExpected = 804;

        test("releaseDeepReading01.pdf", DocMode.READING, FlushMode.RELEASE_DEEP, PagesOp.READ,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void baseStamping01() throws IOException {
        // not all objects are made indirect before closing
        int total = 1618;
        int flushedExpected = 0;
        int notReadExpected = 603;

        test("baseStamping01.pdf", DocMode.STAMPING, FlushMode.NONE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void pageFlushStamping01() throws IOException {
        int total = 2219;
        int flushedExpected = 1200;
        int notReadExpected = 403;

        test("pageFlushStamping01.pdf", DocMode.STAMPING, FlushMode.PAGE_FLUSH, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void unsafeDeepFlushStamping01() throws IOException {
        int total = 2420;
        // 200 still hanging: new font dictionaries on every page shall not be flushed before closing
        int flushedExpected = 1602;
        int notReadExpected = 603;

        test("unsafeDeepFlushStamping01.pdf", DocMode.STAMPING, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushStamping01() throws IOException {
        int total = 2219;
        // 300 less than with page#flush, because of not modified released objects
        int flushedExpected = 900;
        int notReadExpected = 703;

        test("appendModeFlushStamping01.pdf", DocMode.STAMPING, FlushMode.APPEND_MODE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepStamping01() throws IOException {
        int total = 1618;
        int flushedExpected = 0;
        // new objects cannot be released
        int notReadExpected = 703;

        test("releaseDeepStamping01.pdf", DocMode.STAMPING, FlushMode.RELEASE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void baseAppendMode01() throws IOException {
        int total = 1618;
        int flushedExpected = 0;
        int notReadExpected = 603;

        test("baseAppendMode01.pdf", DocMode.APPEND, FlushMode.NONE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void pageFlushAppendMode01() throws IOException {
        int total = 2219;
        int flushedExpected = 900;
        int notReadExpected = 403;

        test("pageFlushAppendMode01.pdf", DocMode.APPEND, FlushMode.PAGE_FLUSH, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void unsafeDeepFlushAppendMode01() throws IOException {
        int total = 2420;
        // 200 still hanging: new font dictionaries on every page shall not be flushed before closing
        int flushedExpected = 1502;
        int notReadExpected = 703;

        test("unsafeDeepFlushAppendMode01.pdf", DocMode.APPEND, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushAppendMode01() throws IOException {
        int total = 2219;
        // 600 still hanging: every new page contains image, font and action
        int flushedExpected = 900;
        int notReadExpected = 703;

        test("appendModeFlushAppendMode01.pdf", DocMode.APPEND, FlushMode.APPEND_MODE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepAppendMode01() throws IOException {
        int total = 1618;
        int flushedExpected = 0;
        // new objects cannot be released
        int notReadExpected = 703;

        test("releaseDeepAppendMode01.pdf", DocMode.APPEND, FlushMode.RELEASE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void baseLightAppendMode01() throws IOException {
        int total = 1018;
        int flushedExpected = 0;
        int notReadExpected = 603;

        test("baseLightAppendMode01.pdf", DocMode.APPEND, FlushMode.NONE, PagesOp.MODIFY_LIGHTLY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void pageFlushLightAppendMode01() throws IOException {
        int total = 1318;
        int flushedExpected = 500;
        // in default PdfPage#flush annotations are always read and attempted to be flushed.
        int notReadExpected = 403;

        test("pageFlushLightAppendMode01.pdf", DocMode.APPEND, FlushMode.PAGE_FLUSH, PagesOp.MODIFY_LIGHTLY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void unsafeDeepFlushLightAppendMode01() throws IOException {
        int total = 1318;
        int flushedExpected = 600;
        int notReadExpected = 703;

        test("unsafeDeepFlushLightAppendMode01.pdf", DocMode.APPEND, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY_LIGHTLY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushLightAppendMode01() throws IOException {
        int total = 1318;
        // resources are not flushed, here it's font dictionaries for every page which in any case shall not be flushed before closing.
        int flushedExpected = 500;
        int notReadExpected = 703;

        test("appendModeFlushLightAppendMode01.pdf", DocMode.APPEND, FlushMode.APPEND_MODE, PagesOp.MODIFY_LIGHTLY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepLightAppendMode01() throws IOException {
        int total = 1018;
        int flushedExpected = 0;
        int notReadExpected = 703;

        test("releaseDeepLightAppendMode01.pdf", DocMode.APPEND, FlushMode.RELEASE_DEEP, PagesOp.MODIFY_LIGHTLY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void modifyAnnotationOnlyAppendMode() throws IOException {
        String input = sourceFolder + "100pages.pdf";
        String output = destinationFolder + "modifyAnnotOnly.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), new PdfWriter(output), new StampingProperties().useAppendMode());

        PdfPage page = pdfDoc.getPage(1);
        PdfIndirectReference pageIndRef = page.getPdfObject().getIndirectReference();
        PdfDictionary annotObj = page.getAnnotations().get(0)
                .setRectangle(new PdfArray(new Rectangle(0, 0, 300, 300))).setPage(page)
                .getPdfObject();

        PageFlushingHelper flushingHelper = new PageFlushingHelper(pdfDoc);
        flushingHelper.appendModeFlush(1);

        // annotation is flushed
        Assert.assertTrue(annotObj.isFlushed());
        // page is not flushed
        Assert.assertFalse(pageIndRef.checkState(PdfObject.FLUSHED));
        // page is released
        Assert.assertNull(pageIndRef.refersTo);

        // exception is not thrown

        pdfDoc.close();
    }

    @Test
    public void setLinkDestinationToPageAppendMode() throws IOException {
        String input = sourceFolder + "100pages.pdf";
        String output = destinationFolder + "setLinkDestinationToPageAppendMode.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), new PdfWriter(output), new StampingProperties().useAppendMode());

        PdfPage page1 = pdfDoc.getPage(1);
        PdfPage page2 = pdfDoc.getPage(2);
        PdfIndirectReference page1IndRef = page1.getPdfObject().getIndirectReference();
        PdfIndirectReference page2IndRef = page2.getPdfObject().getIndirectReference();

        PdfDictionary aDict = ((PdfLinkAnnotation) page1.getAnnotations().get(0)).getAction();
        new PdfAction(aDict).put(PdfName.D, PdfExplicitDestination.createXYZ(page2, 300, 400, 1).getPdfObject());

        PageFlushingHelper flushingHelper = new PageFlushingHelper(pdfDoc);

        flushingHelper.appendModeFlush(2);
        flushingHelper.unsafeFlushDeep(1);

        // annotation is flushed
        Assert.assertTrue(aDict.isFlushed());
        // page is not flushed
        Assert.assertFalse(page1IndRef.checkState(PdfObject.FLUSHED));
        // page is released
        Assert.assertNull(page1IndRef.refersTo);
        // page is not flushed
        Assert.assertFalse(page2IndRef.checkState(PdfObject.FLUSHED));
        // page is released
        Assert.assertNull(page2IndRef.refersTo);

        // exception is not thrown

        pdfDoc.close();
    }

    @Test
    public void flushSelfContainingObjectsWritingMode() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary pageDict = pdfDoc.addNewPage().getPdfObject();
        PdfDictionary dict1 = new PdfDictionary();
        pageDict.put(new PdfName("dict1"), dict1);
        PdfArray arr1 = new PdfArray();
        pageDict.put(new PdfName("arr1"), arr1);

        dict1.put(new PdfName("dict1"), dict1);
        dict1.put(new PdfName("arr1"), arr1);
        arr1.add(arr1);
        arr1.add(dict1);

        arr1.makeIndirect(pdfDoc);
        dict1.makeIndirect(pdfDoc);

        PageFlushingHelper flushingHelper = new PageFlushingHelper(pdfDoc);
        flushingHelper.unsafeFlushDeep(1);

        Assert.assertTrue(dict1.isFlushed());
        Assert.assertTrue(arr1.isFlushed());

        pdfDoc.close();

        // exception is not thrown
    }

    @Test
    public void flushingPageResourcesMadeIndependent() throws IOException {
        String inputFile = sourceFolder + "100pagesSharedResDict.pdf";
        String outputFile = destinationFolder + "flushingPageResourcesMadeIndependent.pdf";
        PdfDocument pdf = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputFile));
        int numOfAddedXObjectsPerPage = 10;
        for (int i = 1; i <= pdf.getNumberOfPages(); ++i) {
            PdfPage sourcePage = pdf.getPage(i);

            PdfDictionary res = sourcePage.getPdfObject().getAsDictionary(PdfName.Resources);
            PdfDictionary resClone = new PdfDictionary();
            // clone dictionary manually to ensure this object is direct and is flushed together with the page
            for (Map.Entry<PdfName, PdfObject> e : res.entrySet()) {
                resClone.put(e.getKey(), e.getValue().clone());
            }
            sourcePage.getPdfObject().put(PdfName.Resources, resClone);

            PdfCanvas pdfCanvas = new PdfCanvas(sourcePage);
            pdfCanvas.saveState();
            for (int j = 0; j < numOfAddedXObjectsPerPage; ++j) {
                PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "simple.jpg"));
                pdfCanvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(36, 720 - j * 150, 20, 20));
                xObject.makeIndirect(pdf).flush();
            }
            pdfCanvas.restoreState();
            pdfCanvas.release();

            sourcePage.flush();
        }

        verifyFlushedObjectsNum(pdf, 1416, 1400, 0);
        pdf.close();

        printOutputPdfNameAndDir(outputFile);
        PdfDocument result = new PdfDocument(new PdfReader(outputFile));
        PdfObject page15Res = result.getPage(15).getPdfObject().get(PdfName.Resources, false);
        PdfObject page34Res = result.getPage(34).getPdfObject().get(PdfName.Resources, false);
        Assert.assertTrue(page15Res.isDictionary());
        Assert.assertEquals(numOfAddedXObjectsPerPage, ((PdfDictionary)page15Res).getAsDictionary(PdfName.XObject).size());
        Assert.assertTrue(page34Res.isDictionary());
        Assert.assertNotEquals(page15Res, page34Res);

        result.close();
    }

    private static void test(String filename, DocMode docMode, FlushMode flushMode, PagesOp pagesOp,
                             int total, int flushedExpected, int notReadExpected) throws IOException {
        String input = sourceFolder + "100pages.pdf";
        String output = destinationFolder + filename;
        PdfDocument pdfDoc;
        switch (docMode) {
            case WRITING:
                pdfDoc = new PdfDocument(new PdfWriter(output));
                break;
            case READING:
                pdfDoc = new PdfDocument(new PdfReader(input));
                break;
            case STAMPING:
                pdfDoc = new PdfDocument(new PdfReader(input), new PdfWriter(output));
                break;
            case APPEND:
                pdfDoc = new PdfDocument(new PdfReader(input), new PdfWriter(output), new StampingProperties().useAppendMode());
                break;
            default:
                throw new IllegalStateException();
        }

        PageFlushingHelper flushingHelper = new PageFlushingHelper(pdfDoc);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itext.png"));
        if (docMode != DocMode.WRITING) {
            for (int i = 0; i < 100; ++i) {
                PdfPage page = pdfDoc.getPage(i + 1);
                switch (pagesOp) {
                    case READ:
                        PdfTextExtractor.getTextFromPage(page);
                        break;
                    case MODIFY:
                        addContentToPage(page, font, xObject);
                        break;
                    case MODIFY_LIGHTLY:
                        addBasicContent(page, font);
                        break;
                }

                switch (flushMode) {
                    case UNSAFE_DEEP:
                        flushingHelper.unsafeFlushDeep(i + 1);
                        break;
                    case RELEASE_DEEP:
                        flushingHelper.releaseDeep(i + 1);
                        break;
                    case APPEND_MODE:
                        flushingHelper.appendModeFlush(i + 1);
                        break;
                    case PAGE_FLUSH:
                        page.flush();
                        break;
                }
            }
        }
        if (docMode != DocMode.READING && pagesOp == PagesOp.MODIFY) {
            for (int i = 0; i < 100; ++i) {
                PdfPage page = pdfDoc.addNewPage();
                addContentToPage(page, font, xObject);

                switch (flushMode) {
                    case UNSAFE_DEEP:
                        flushingHelper.unsafeFlushDeep(pdfDoc.getNumberOfPages());
                        break;
                    case RELEASE_DEEP:
                        flushingHelper.releaseDeep(pdfDoc.getNumberOfPages());
                        break;
                    case APPEND_MODE:
                        flushingHelper.appendModeFlush(pdfDoc.getNumberOfPages());
                        break;
                    case PAGE_FLUSH:
                        page.flush();
                        break;
                }
            }
        }

        verifyFlushedObjectsNum(pdfDoc, total, flushedExpected, notReadExpected);
        pdfDoc.close();
    }

    private static void verifyFlushedObjectsNum(PdfDocument pdfDoc, int total, int flushedExpected, int notReadExpected) {
        int flushedActual = 0;
        int notReadActual = 0;
        for (int i = 0; i < pdfDoc.getXref().size(); ++i) {
            PdfIndirectReference indRef = pdfDoc.getXref().get(i);
            if (indRef.checkState(PdfObject.FLUSHED)) {
                ++flushedActual;
            } else if (!indRef.isFree() && indRef.refersTo == null) {
                ++notReadActual;
            }
        }

        if (pdfDoc.getXref().size() != total || flushedActual != flushedExpected || notReadActual != notReadExpected) {
            Assert.fail(MessageFormatUtil.format("\nExpected total: {0}, flushed: {1}, not read: {2};" +
                            "\nbut actual was: {3}, flushed: {4}, not read: {5}.",
                    total, flushedExpected, notReadExpected, pdfDoc.getXref().size(), flushedActual, notReadActual
            ));
        }
        Assert.assertEquals("wrong num of total objects", total, pdfDoc.getXref().size());
        Assert.assertEquals("wrong num of flushed objects", flushedExpected, flushedActual);
        Assert.assertEquals("wrong num of not read objects", notReadExpected, notReadActual);
    }

    private static void addContentToPage(PdfPage pdfPage, PdfFont font, PdfImageXObject xObject) throws IOException {
        PdfCanvas canvas = addBasicContent(pdfPage, font);
        canvas
                .saveState()
                .rectangle(250, 500, 100, 100)
                .fill()
                .restoreState();

        PdfFont courier = PdfFontFactory.createFont(StandardFonts.COURIER);
        courier.makeIndirect(pdfPage.getDocument());
        canvas
                .saveState()
                .beginText()
                .moveText(36, 650)
                .setFontAndSize(courier, 16)
                .showText("Hello Courier!")
                .endText()
                .restoreState();

        canvas
                .saveState()
                .circle(100, 400, 25)
                .fill()
                .restoreState();

        canvas
                .saveState()
                .roundRectangle(100, 650, 100, 100, 10)
                .fill()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(10)
                .roundRectangle(250, 650, 100, 100, 10)
                .stroke()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(5)
                .arc(400, 650, 550, 750, 0, 180)
                .stroke()
                .restoreState();

        canvas
                .saveState()
                .setLineWidth(5)
                .moveTo(400, 550)
                .curveTo(500, 570, 450, 450, 550, 550)
                .stroke()
                .restoreState();

        canvas.addXObjectFittedIntoRectangle(xObject, new Rectangle(100, 500, 400, xObject.getHeight()));

        PdfImageXObject xObject2 = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itext.png"));
        xObject2.makeIndirect(pdfPage.getDocument());

        canvas.addXObjectFittedIntoRectangle(xObject2, new Rectangle(100, 500, 400, xObject2.getHeight()));
    }

    private static PdfCanvas addBasicContent(PdfPage pdfPage, PdfFont font) {
        Rectangle lineAnnotRect = new Rectangle(0, 0, PageSize.A4.getRight(), PageSize.A4.getTop());
        pdfPage.addAnnotation(
                new PdfLinkAnnotation(new Rectangle(100, 600, 100, 20))
                        .setAction(PdfAction.createURI("http://itextpdf.com"))
        ).addAnnotation(
                new PdfLineAnnotation(lineAnnotRect, new float[]{lineAnnotRect.getX(), lineAnnotRect.getY(), lineAnnotRect.getRight(), lineAnnotRect.getTop()})
                        .setColor(ColorConstants.BLACK)
        );

        PdfCanvas canvas = new PdfCanvas(pdfPage);
        canvas.rectangle(100, 100, 100, 100).fill();

        canvas
                .saveState()
                .beginText()
                .setTextMatrix(AffineTransform.getRotateInstance(Math.PI / 4, 36, 350))
                .setFontAndSize(font, 72)
                .showText("Hello Helvetica!")
                .endText()
                .restoreState();
        return canvas;
    }

    private enum DocMode {
        WRITING,
        READING,
        STAMPING,
        APPEND
    }

    private enum FlushMode {
        NONE,
        PAGE_FLUSH,
        UNSAFE_DEEP,
        RELEASE_DEEP,
        APPEND_MODE,
    }

    private enum PagesOp {
        NONE,
        READ,
        MODIFY,
        MODIFY_LIGHTLY,
    }
}
