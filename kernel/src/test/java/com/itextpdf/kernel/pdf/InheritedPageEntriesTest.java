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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class InheritedPageEntriesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    //TODO: update cmp-files when DEVSIX-3635 will be fixed
    public void addNewPageToDocumentWithInheritedPageRotationTest() throws InterruptedException, IOException {
        String inputFileName = sourceFolder + "srcFileTestRotationInheritance.pdf";
        String outputFileName = destinationFolder + "addNewPageToDocumentWithInheritedPageRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_addNewPageToDocumentWithInheritedPageRotation.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName), CompareTool.createTestPdfWriter(outputFileName));

        PdfPage page = outFile.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Hello Helvetica!")
                .endText()
                .saveState();

        outFile.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void setRotationToPageTest() throws InterruptedException, IOException {
        String outputFileName = destinationFolder + "setRotationToPage.pdf";
        String cmpFileName = sourceFolder + "cmp_setRotationToPage.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader
                (sourceFolder + "srcFileTestRotationInheritance.pdf"), CompareTool.createTestPdfWriter(outputFileName));

        PdfPage page = pdfDoc.getPage(1);
        page.setRotation(90);

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO: update cmp-files when DEVSIX-3635 will be fixed
    public void copySeveralPagesToDocumentWithInheritedPageRotationTest() throws InterruptedException, IOException {
        String outputFileName = destinationFolder + "copySeveralPagesToDocumentWithInheritedPageRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_copySeveralPagesToDocumentWithInheritedPageRotation.pdf";

        PdfDocument pdfDoc1 = new PdfDocument(new PdfReader(sourceFolder + "noPagesRotation.pdf"));

        PdfDocument pdfDoc2 = new PdfDocument(new PdfReader
                (sourceFolder + "addSeveralPagesToDocumentWithInheritedPageRotation.pdf"),
                CompareTool.createTestPdfWriter(outputFileName));

        pdfDoc1.copyPagesTo(1, 2, pdfDoc2);
        pdfDoc1.close();

        pdfDoc2.close();

        Assertions.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void mediaBoxInheritance() throws IOException {
        String inputFileName = sourceFolder + "mediaBoxInheritanceTestSource.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName));

        PdfObject mediaBox = outFile.getPage(1).getPdfObject().get(PdfName.MediaBox);
        //Check if MediaBox in Page is absent
        Assertions.assertNull(mediaBox);

        PdfArray array = outFile.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).getAsArray(PdfName.MediaBox);
        Rectangle rectangle = array.toRectangle();

        Rectangle pageRect = outFile.getPage(1).getMediaBox();

        outFile.close();

        Assertions.assertTrue(rectangle.equalsWithEpsilon(pageRect));
    }
    
    @Test
    public void cropBoxInheritance() throws IOException {
        String inputFileName = sourceFolder + "cropBoxInheritanceTestSource.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName));

        PdfObject cropBox = outFile.getPage(1).getPdfObject().get(PdfName.CropBox);
        //Check if CropBox in Page is absent
        Assertions.assertNull(cropBox);

        PdfArray array = outFile.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).getAsArray(PdfName.CropBox);
        Rectangle rectangle = array.toRectangle();

        Rectangle pageRect = outFile.getPage(1).getCropBox();

        outFile.close();

        Assertions.assertTrue(rectangle.equalsWithEpsilon(pageRect));
    }
}
