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

import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.itextpdf.test.annotations.type.IntegrationTest;

import java.util.Collections;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfStreamTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStreamTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStreamTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void streamAppendDataOnJustCopiedWithCompression() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "pageWithContent.pdf";
        String cmpFile = sourceFolder + "cmp_streamAppendDataOnJustCopiedWithCompression.pdf";
        String destFile = destinationFolder + "streamAppendDataOnJustCopiedWithCompression.pdf";

        PdfDocument srcDocument = new PdfDocument(new PdfReader(srcFile));
        PdfDocument document = new PdfDocument(new PdfWriter(destFile));
        srcDocument.copyPagesTo(1, 1, document);
        srcDocument.close();

        String newContentString = "BT\n" +
                "/F1 36 Tf\n" +
                "50 700 Td\n" +
                "(new content here!) Tj\n" +
                "ET";
        byte[] newContent = newContentString.getBytes(StandardCharsets.UTF_8);
        document.getPage(1).getLastContentStream().setData(newContent, true);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(destFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void runLengthEncodingTest01() throws IOException {
        String srcFile = sourceFolder + "runLengthEncodedImages.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(srcFile));

        PdfImageXObject im1 = document.getPage(1).getResources().getImage(new PdfName("Im1"));
        PdfImageXObject im2 = document.getPage(1).getResources().getImage(new PdfName("Im2"));

        byte[] imgBytes1 = im1.getImageBytes();
        byte[] imgBytes2 = im2.getImageBytes();

        document.close();

        byte[] cmpImgBytes1 = readFile(sourceFolder + "cmp_img1.jpg");
        byte[] cmpImgBytes2 = readFile(sourceFolder + "cmp_img2.jpg");

        Assert.assertArrayEquals(imgBytes1, cmpImgBytes1);
        Assert.assertArrayEquals(imgBytes2, cmpImgBytes2);
    }

    @Test
    public void indirectRefInFilterAndNoTaggedPdfTest() throws IOException {
        String inFile = sourceFolder + "indirectRefInFilterAndNoTaggedPdf.pdf";
        String outFile = destinationFolder + "destIndirectRefInFilterAndNoTaggedPdf.pdf";

        PdfDocument srcDoc = new PdfDocument(new PdfReader(inFile));
        PdfDocument outDoc = new PdfDocument(new PdfReader(inFile), new PdfWriter(outFile));
        outDoc.close();

        PdfDocument doc = new PdfDocument(new PdfReader(outFile));

        PdfStream outStreamIm1 = doc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im1"));
        PdfStream outStreamIm2 = doc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im2"));

        PdfStream cmpStreamIm1 = srcDoc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im1"));
        PdfStream cmpStreamIm2 = srcDoc.getFirstPage().getResources().getResource(PdfName.XObject)
                .getAsStream(new PdfName("Im2"));

        Assert.assertNull(new CompareTool().compareStreamsStructure(outStreamIm1, cmpStreamIm1));
        Assert.assertNull(new CompareTool().compareStreamsStructure(outStreamIm2, cmpStreamIm2));

        srcDoc.close();
        outDoc.close();
    }

    @Test
    // TODO DEVSIX-1193 remove junitExpectedException and expected NullPointerException after fix
    public void indirectFilterInCatalogTest() throws IOException {
        String inFile = sourceFolder + "indFilterInCatalog.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(inFile),
                new PdfWriter(destinationFolder + "indFilterInCatalog.pdf"));

        junitExpectedException.expect(NullPointerException.class);
        doc.close();
    }

    @Test
    // TODO DEVSIX-1193 remove junitExpectedException after fix
    public void indirectFilterFlushedBeforeStreamTest() throws IOException {
        String inFile = sourceFolder + "indFilterInCatalog.pdf";
        String out = destinationFolder + "indirectFilterFlushedBeforeStreamTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile), new PdfWriter(out));

        // Simulate the case in which filter is somehow already flushed before stream.
        // Either directly by user or because of any other reason.
        PdfObject filterObject = pdfDoc.getPdfObject(6);
        filterObject.flush();

        junitExpectedException.expect(NullPointerException.class);
        pdfDoc.close();
    }

    @Test
    // TODO DEVSIX-1193 remove junitExpectedException after fix
    public void indirectFilterMarkedToBeFlushedBeforeStreamTest() throws IOException {
        String inFile = sourceFolder + "indFilterInCatalog.pdf";
        String out = destinationFolder + "indirectFilterMarkedToBeFlushedBeforeStreamTest.pdf";

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile), writer);

        // Simulate the case when indirect filter object is marked to be flushed before the stream itself.
        PdfObject filterObject = pdfDoc.getPdfObject(6);
        filterObject.getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);

        // The image stream will be marked as MUST_BE_FLUSHED after page is flushed.
        pdfDoc.getFirstPage().getPdfObject().getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);

        junitExpectedException.expect(NullPointerException.class);
        writer.flushWaitingObjects(Collections.<PdfIndirectReference>emptySet());

        pdfDoc.close();
    }
}
