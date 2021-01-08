/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfObjectReleaseTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfObjectReleaseTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfObjectReleaseTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET, count = 108))
    public void releaseObjectsInDocWithStructTreeRootTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInDocWithStructTreeRoot.pdf",
                "releaseObjectsInDocWithStructTreeRoot_stamping.pdf",
                "releaseObjectsInDocWithStructTreeRoot_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET, count = 5))
    public void releaseObjectsInDocWithXfaTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInDocWithXfa.pdf",
                "releaseObjectsInDocWithXfa_stamping.pdf",
                "releaseObjectsInDocWithXfa_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET, count = 3))
    public void releaseObjectsInSimpleDocTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInSimpleDoc.pdf",
                "releaseObjectsInSimpleDoc_stamping.pdf",
                "releaseObjectsInSimpleDoc_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releaseCatalogTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "releaseObjectsInSimpleDoc.pdf";
        String release = destinationFolder + "outReleaseObjectsInSimpleDoc.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), new PdfWriter(release))) {
            doc.getCatalog().getPdfObject().release();
        }

        Assert.assertNull(new CompareTool().compareByContent(release, srcFile, destinationFolder));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releasePagesTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "releaseObjectsInSimpleDoc.pdf";
        String release = destinationFolder + "outReleaseObjectsInSimpleDoc.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), new PdfWriter(release))) {
            doc.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).release();
        }

        Assert.assertNull(new CompareTool().compareByContent(release, srcFile, destinationFolder));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releaseStructTreeRootTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "releaseObjectsInDocWithStructTreeRoot.pdf";
        String release = destinationFolder + "outReleaseObjectsInDocWithStructTreeRoot.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), new PdfWriter(release))) {
            doc.getStructTreeRoot().getPdfObject().release();
        }

        Assert.assertNull(new CompareTool().compareByContent(release, srcFile, destinationFolder));
    }

    @Test
    public void noForbidReleaseObjectsModifyingTest() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "noForbidReleaseObjectsModifying.pdf";
        String stampReleased = sourceFolder + "noForbidReleaseObjectsModified.pdf";

        try (PdfDocument doc = new PdfDocument(
                new PdfReader(srcFile),
                new PdfWriter(destinationFolder + "noForbidReleaseObjectsModifying.pdf"),
                new StampingProperties().useAppendMode())) {

            PdfAnnotation annots = doc.getPage(1).getAnnotations().get(0);

            annots.setRectangle(new PdfArray(new Rectangle(100, 100, 80, 50)));
            annots.getRectangle().release();
        }

        try (PdfDocument openPrev = new PdfDocument(new PdfReader(stampReleased))) {
            Assert.assertTrue(new Rectangle(100, 100, 80, 50).equalsWithEpsilon(
                    openPrev.getPage(1).getAnnotations().get(0).getRectangle().toRectangle()));
        }

        Assert.assertNotNull(new CompareTool().compareByContent(srcFile, stampReleased, destinationFolder));
    }

    @Test
    public void addingReleasedObjectToDocumentTest() throws IOException {
        String srcFile = sourceFolder + "releaseObjectsInSimpleDoc.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(srcFile),
                new PdfWriter(sourceFolder + "addingReleasedObjectToDocument.pdf"));
        try {
            PdfObject releasedObj = doc.getPdfObject(1);
            releasedObj.release();

            doc.getCatalog().put(PdfName.Outlines, releasedObj);
        } finally {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage("Cannot write object after it was released."
                    + " In normal situation the object must be read once again before being written.");
            doc.close();
        }
    }

    private void singlePdfObjectReleaseTest(String inputFilename, String outStampingFilename, String outStampingReleaseFilename) throws IOException, InterruptedException {
        String srcFile = sourceFolder + inputFilename;
        String outPureStamping = destinationFolder + outStampingFilename;
        String outStampingRelease = destinationFolder + outStampingReleaseFilename;

        PdfDocument doc = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outPureStamping));
        // We open/close document to make sure that the results of release logic and simple overwriting coincide.
        doc.close();

        PdfDocument stamperRelease = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outStampingRelease));

        for (int i = 0; i < stamperRelease.getNumberOfPdfObjects(); i++) {
            PdfObject pdfObject = stamperRelease.getPdfObject(i);
            if (pdfObject != null) {
                stamperRelease.getPdfObject(i).release();
            }
        }

        stamperRelease.close();

        Assert.assertNull(new CompareTool().compareByContent(outStampingRelease, outPureStamping, destinationFolder));
    }
}
