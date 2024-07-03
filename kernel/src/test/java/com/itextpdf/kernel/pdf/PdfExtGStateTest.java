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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfExtGStateTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfExtGStateTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/PdfExtGStateTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void egsTest1() throws Exception {
        String destinationDocument = destinationFolder + "egsTest1.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationDocument));

        //Create page and canvas
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        //Create ExtGState and fill it with line width and font
        PdfExtGState egs = new PdfExtGState();
        egs.getPdfObject().put(PdfName.LW, new PdfNumber(5));
        PdfArray font = new PdfArray();
        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.addFont(pdfFont);
        font.add(pdfFont.getPdfObject());
        font.add(new PdfNumber(24));
        egs.getPdfObject().put(PdfName.Font, font);

        //Write ExtGState
        canvas.setExtGState(egs);

        //Write text to check that font from ExtGState is applied
        canvas.beginText();
        canvas.moveText(50, 600);
        canvas.showText("Courier, 24pt");
        canvas.endText();

        //Draw line to check if ine width is applied
        canvas.moveTo(50, 500);
        canvas.lineTo(300, 500);
        canvas.stroke();

        //Write text again to check that font from page resources and font from ExtGState is the same.
        canvas.beginText();
        canvas.setFontAndSize(pdfFont, 36);
        canvas.moveText(50, 400);
        canvas.showText("Courier, 36pt");
        canvas.endText();
        canvas.release();

        page.flush();
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_egsTest1.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void blackPointCompensationTest1() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        pdfExtGState.setUseBlackPointCompensation(true);
        Assertions.assertTrue(pdfExtGState.isBlackPointCompensationUsed());
    }

    @Test
    public void blackPointCompensationTest2() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        pdfExtGState.setUseBlackPointCompensation(false);
        Assertions.assertFalse(pdfExtGState.isBlackPointCompensationUsed());
    }

    @Test
    public void blackPointCompensationTest3() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        PdfObject useBlackPoint = pdfExtGState.getPdfObject().getAsName(PdfName.UseBlackPtComp);
        Assertions.assertNull(useBlackPoint);
    }

    @Test
    public void blackPointCompensationTest4() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        PdfDictionary pdfExtGStateObj = pdfExtGState.getPdfObject();
        pdfExtGStateObj.put(PdfName.UseBlackPtComp, PdfName.ON);
        Assertions.assertTrue(pdfExtGState.isBlackPointCompensationUsed());
    }

    @Test
    public void blackPointCompensationTest5() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        PdfDictionary pdfExtGStateObj = pdfExtGState.getPdfObject();
        pdfExtGStateObj.put(PdfName.UseBlackPtComp, PdfName.OFF);
        Assertions.assertFalse(pdfExtGState.isBlackPointCompensationUsed());
    }

    @Test
    public void blackPointCompensationTest6() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        pdfExtGState.setUseBlackPointCompensation(true);
        PdfName useBlackPtComp = pdfExtGState.getPdfObject().getAsName(PdfName.UseBlackPtComp);
        Assertions.assertEquals(PdfName.ON, useBlackPtComp, "PdfName is different from expected.");
    }

    @Test
    public void blackPointCompensationTest7() {
        PdfExtGState pdfExtGState = new PdfExtGState();
        pdfExtGState.setUseBlackPointCompensation(false);
        PdfName useBlackPtComp = pdfExtGState.getPdfObject().getAsName(PdfName.UseBlackPtComp);
        Assertions.assertEquals(PdfName.OFF, useBlackPtComp, "PdfName is different from expected.");
    }
}
