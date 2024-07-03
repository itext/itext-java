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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfObjectReleaseTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfObjectReleaseTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfObjectReleaseTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET, count = 108))
    public void releaseObjectsInDocWithStructTreeRootTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInDocWithStructTreeRoot.pdf",
                "releaseObjectsInDocWithStructTreeRoot_stamping.pdf",
                "releaseObjectsInDocWithStructTreeRoot_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET, count = 5))
    public void releaseObjectsInDocWithXfaTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInDocWithXfa.pdf",
                "releaseObjectsInDocWithXfa_stamping.pdf",
                "releaseObjectsInDocWithXfa_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET, count = 3))
    public void releaseObjectsInSimpleDocTest() throws IOException, InterruptedException {
        singlePdfObjectReleaseTest("releaseObjectsInSimpleDoc.pdf",
                "releaseObjectsInSimpleDoc_stamping.pdf",
                "releaseObjectsInSimpleDoc_stamping_release.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releaseCatalogTest() throws IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "releaseObjectsInSimpleDoc.pdf";
        String release = DESTINATION_FOLDER + "outReleaseObjectsInSimpleDoc.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(release))) {
            doc.getCatalog().getPdfObject().release();
        }

        Assertions.assertNull(new CompareTool().compareByContent(release, srcFile, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releasePagesTest() throws IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "releaseObjectsInSimpleDoc.pdf";
        String release = DESTINATION_FOLDER + "outReleaseObjectsInSimpleDoc.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(release))) {
            doc.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).release();
        }

        Assertions.assertNull(new CompareTool().compareByContent(release, srcFile, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releaseStructTreeRootTest() throws IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "releaseObjectsInDocWithStructTreeRoot.pdf";
        String release = DESTINATION_FOLDER + "outReleaseObjectsInDocWithStructTreeRoot.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(release))) {
            doc.getStructTreeRoot().getPdfObject().release();
        }

        Assertions.assertNull(new CompareTool().compareByContent(release, srcFile, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.FORBID_RELEASE_IS_SET))
    public void releaseModifiedObjectTest() throws IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "releaseModifiedObject.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_releaseModifiedObject.pdf";
        String outFile = DESTINATION_FOLDER + "releaseModifiedObject.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outFile))) {

            PdfAnnotation annots = doc.getPage(1).getAnnotations().get(0);

            annots.setRectangle(new PdfArray(new Rectangle(100, 100, 80, 50)));
            annots.getPdfObject().release();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, DESTINATION_FOLDER));
    }

    @Test
    public void addingReleasedObjectToDocumentTest() throws IOException {
        String srcFile = SOURCE_FOLDER + "releaseObjectsInSimpleDoc.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(srcFile),
                CompareTool.createTestPdfWriter(DESTINATION_FOLDER + "addingReleasedObjectToDocument.pdf"));
        try {
            PdfObject releasedObj = doc.getPdfObject(1);
            releasedObj.release();

            doc.getCatalog().put(PdfName.Outlines, releasedObj);
        } finally {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> doc.close()
            );
            Assertions.assertEquals("Cannot write object after it was released."
                    + " In normal situation the object must be read once again before being written.", e.getMessage());
        }
    }

    private void singlePdfObjectReleaseTest(String inputFilename, String outStampingFilename, String outStampingReleaseFilename) throws IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + inputFilename;
        String outPureStamping = DESTINATION_FOLDER + outStampingFilename;
        String outStampingRelease = DESTINATION_FOLDER + outStampingReleaseFilename;

        PdfDocument doc = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outPureStamping));
        // We open/close document to make sure that the results of release logic and simple overwriting coincide.
        doc.close();

        PdfDocument stamperRelease = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outStampingRelease));

        for (int i = 0; i < stamperRelease.getNumberOfPdfObjects(); i++) {
            PdfObject pdfObject = stamperRelease.getPdfObject(i);
            if (pdfObject != null) {
                stamperRelease.getPdfObject(i).release();
            }
        }

        stamperRelease.close();

        Assertions.assertNull(new CompareTool().compareByContent(outStampingRelease, outPureStamping, DESTINATION_FOLDER));
    }
}
