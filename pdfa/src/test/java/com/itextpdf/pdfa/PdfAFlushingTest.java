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
package com.itextpdf.pdfa;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfAFlushingTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAFlushingTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED)})
    public void flushingTest01() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_flushingTest01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA1b_flushingTest01.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(30, 300, 300, 300));

        imageXObject.flush();
        if (imageXObject.isFlushed()) {
            fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED)})
    public void flushingTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_flushingTest02.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA2b_flushingTest02.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flush();
        if (lastPage.isFlushed()) {
            fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void flushingTest03() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA3b_flushingTest03.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA3b_flushingTest03.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        canvas.addXObjectFittedIntoRectangle(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flush(true);
        if (!imageXObject.isFlushed()) {
            fail("When flushing the page along with it's resources, page check should be performed also page and all resources should be flushed.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED)})
    public void addUnusedStreamObjectsTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_docWithUnusedObjects_3.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA1b_docWithUnusedObjects_3.pdf";

        PdfWriter writer = new PdfWriter(outPdf);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = (PdfArray) new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        PdfStream stream = new PdfStream(new byte[]{1, 2, 34, 45}, 0);
        unusedArray.add(stream);
        unusedDictionary.put(new PdfName("testName"), unusedArray);
        unusedDictionary.makeIndirect(pdfDocument).flush();
        unusedDictionary.flush();
        pdfDocument.close();

        PdfReader testerReader = new PdfReader(outPdf);
        PdfDocument testerDocument = new PdfDocument(testerReader);

        assertEquals(testerDocument.listIndirectReferences().size(), 11);

        testerDocument.close();

        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outFile, String cmpFile) throws IOException, InterruptedException {
        String differences = new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_");
        if (differences != null) {
            fail(differences);
        }
    }
}
