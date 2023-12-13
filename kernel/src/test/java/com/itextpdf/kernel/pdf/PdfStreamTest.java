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

import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.itextpdf.test.annotations.type.IntegrationTest;

import java.util.Collections;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfStreamTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStreamTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStreamTest/";

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
    // TODO DEVSIX-1193 remove NullPointerException after fix
    public void indirectFilterInCatalogTest() throws IOException {
        String inFile = sourceFolder + "indFilterInCatalog.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(inFile),
                new PdfWriter(destinationFolder + "indFilterInCatalog.pdf"));

        Assert.assertThrows(NullPointerException.class, () -> doc.close());
    }

    @Test
    // TODO DEVSIX-1193 remove NullPointerException after fix
    public void indirectFilterFlushedBeforeStreamTest() throws IOException {
        String inFile = sourceFolder + "indFilterInCatalog.pdf";
        String out = destinationFolder + "indirectFilterFlushedBeforeStreamTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFile), new PdfWriter(out));

        // Simulate the case in which filter is somehow already flushed before stream.
        // Either directly by user or because of any other reason.
        PdfObject filterObject = pdfDoc.getPdfObject(6);
        filterObject.flush();

        Assert.assertThrows(NullPointerException.class, () -> pdfDoc.close());
    }

    @Test
    // TODO DEVSIX-1193 remove NullPointerException after fix
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

        Assert.assertThrows(NullPointerException.class,
                () -> writer.flushWaitingObjects(Collections.<PdfIndirectReference>emptySet())
        );
        Assert.assertThrows(NullPointerException.class, () -> pdfDoc.close());
    }
}
