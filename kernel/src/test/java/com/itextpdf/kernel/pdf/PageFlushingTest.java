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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
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
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void baseWriting01() throws IOException {
        int total = 414; // not all objects are made indirect before closing
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
        int flushedExpected = 702; // 100 still hanging: new font dictionaries on every page shall not be flushed before closing
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
        int notReadExpected = 401; // link annots, line annots, actions and images: one hundred of each

        test("baseReading01.pdf", DocMode.READING, FlushMode.NONE, PagesOp.READ,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepReading01() throws IOException {
        int total = 817;
        int flushedExpected = 0;
        int notReadExpected = 803;

        test("releaseDeepReading01.pdf", DocMode.READING, FlushMode.RELEASE_DEEP, PagesOp.READ,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void baseStamping01() throws IOException {
        int total = 1618; // not all objects are made indirect before closing
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
        int flushedExpected = 1602; // 200 still hanging: new font dictionaries on every page shall not be flushed before closing
        int notReadExpected = 603;

        test("unsafeDeepFlushStamping01.pdf", DocMode.STAMPING, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushStamping01() throws IOException {
        int total = 2219;
        int flushedExpected = 900; // 300 less than with page#flush, because of not modified released objects
        int notReadExpected = 703;

        test("appendModeFlushStamping01.pdf", DocMode.STAMPING, FlushMode.APPEND_MODE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepStamping01() throws IOException {
        int total = 1618;
        int flushedExpected = 0;
        int notReadExpected = 703; // new objects cannot be released

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
        int flushedExpected = 1502; // 200 still hanging: new font dictionaries on every page shall not be flushed before closing
        int notReadExpected = 703;

        test("unsafeDeepFlushAppendMode01.pdf", DocMode.APPEND, FlushMode.UNSAFE_DEEP, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void appendModeFlushAppendMode01() throws IOException {
        int total = 2219;
        int flushedExpected = 900; // 600 still hanging: every new page contains image, font and action
        int notReadExpected = 703;

        test("appendModeFlushAppendMode01.pdf", DocMode.APPEND, FlushMode.APPEND_MODE, PagesOp.MODIFY,
                total, flushedExpected, notReadExpected);
    }

    @Test
    public void releaseDeepAppendMode01() throws IOException {
        int total = 1618;
        int flushedExpected = 0;
        int notReadExpected = 703; // new objects cannot be released

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
        int notReadExpected = 403; // in default PdfPage#flush annotations are always read and attempted to be flushed.

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
        int flushedExpected = 500; // resources are not flushed, here it's font dictionaries for every page which in any case shall not be flushed before closing.
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

        Assert.assertTrue(annotObj.isFlushed()); // annotation is flushed
        Assert.assertFalse(pageIndRef.checkState(PdfObject.FLUSHED)); // page is not flushed
        Assert.assertNull(pageIndRef.refersTo); // page is released

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

        PdfDictionary aDict = ((PdfLinkAnnotation)page1.getAnnotations().get(0)).getAction();
        new PdfAction(aDict).put(PdfName.D, PdfExplicitDestination.createXYZ(page2, 300, 400, 1).getPdfObject());

        PageFlushingHelper flushingHelper = new PageFlushingHelper(pdfDoc);

        flushingHelper.appendModeFlush(2);
        flushingHelper.unsafeFlushDeep(1);

        Assert.assertTrue(aDict.isFlushed()); // annotation is flushed
        Assert.assertFalse(page1IndRef.checkState(PdfObject.FLUSHED)); // page is not flushed
        Assert.assertNull(page1IndRef.refersTo); // page is released
        Assert.assertFalse(page2IndRef.checkState(PdfObject.FLUSHED)); // page is not flushed
        Assert.assertNull(page2IndRef.refersTo); // page is released

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

        canvas.addXObject(xObject, 100, 500, 400);

        PdfImageXObject xObject2 = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itext.png"));
        xObject2.makeIndirect(pdfPage.getDocument());

        canvas.addXObject(xObject2, 100, 300, 400);
    }

    private static PdfCanvas addBasicContent(PdfPage pdfPage, PdfFont font) {
        Rectangle lineAnnotRect = new Rectangle(0, 0, PageSize.A4.getRight(), PageSize.A4.getTop());
        pdfPage.addAnnotation(
                new PdfLinkAnnotation(new Rectangle(100, 600, 100, 20))
                        .setAction(PdfAction.createURI("http://itextpdf.com"))
        ).addAnnotation(
                new PdfLineAnnotation(lineAnnotRect, new float[] {lineAnnotRect.getX(), lineAnnotRect.getY(), lineAnnotRect.getRight(), lineAnnotRect.getTop()})
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
