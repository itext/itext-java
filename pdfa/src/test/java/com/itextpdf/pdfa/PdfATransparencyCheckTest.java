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
package com.itextpdf.pdfa;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfATransparencyCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfATransparencyCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfATransparencyCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void textTransparencyNoOutputIntentTest() throws IOException {
        PdfWriter writer = new PdfWriter(new java.io.ByteArrayOutputStream());
        PdfDocument pdfDocument = new PdfADocument(writer, PdfAConformance.PDF_A_3B, null);

        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDocument.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page with transparency")
                .endText()
                .restoreState();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE),
                e.getMessage());
    }

    @Test
    public void transparentTextWithGroupColorSpaceTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "transparencyAndCS.pdf";
        String cmpPdf = cmpFolder + "cmp_transparencyAndCS.pdf";

        PdfDocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_3B, null);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page = pdfDocument.addNewPage();
        page.getResources().setDefaultGray(new PdfCieBasedCs.CalGray(getCalGrayArray()));
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page 1 with transparency")
                .endText()
                .restoreState();

        PdfDictionary groupObj = new PdfDictionary();
        groupObj.put(PdfName.CS, new PdfCieBasedCs.CalGray(getCalGrayArray()).getPdfObject());
        groupObj.put(PdfName.Type, PdfName.Group);
        groupObj.put(PdfName.S, PdfName.Transparency);
        page.getPdfObject().put(PdfName.Group, groupObj);

        PdfPage page2 = pdfDocument.addNewPage();
        page2.getResources().setDefaultGray(new PdfCieBasedCs.CalGray(getCalGrayArray()));
        canvas = new PdfCanvas(page2);
        canvas.saveState();
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page 2 without transparency")
                .endText()
                .restoreState();

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void imageTransparencyTest() throws IOException {
        PdfDocument pdfDoc = new PdfADocument(new PdfWriter(new java.io.ByteArrayOutputStream()), PdfAConformance.PDF_A_3B, null);

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        page.getResources().setDefaultRgb(new PdfCieBasedCs.CalRgb(new float[]{0.3f, 0.4f, 0.5f}));

        canvas.saveState();
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "itext.png"),
                new Rectangle(0, 0, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2), false);
        canvas.restoreState();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE),
                e.getMessage());
    }

    @Test
    public void nestedXObjectWithTransparencyTest() {
        PdfWriter writer = new PdfWriter(new java.io.ByteArrayOutputStream());
        PdfDocument pdfDocument = new PdfADocument(writer, PdfAConformance.PDF_A_3B, null);
        PdfFormXObject form1 = new PdfFormXObject(new Rectangle(0, 0, 50, 50));
        PdfCanvas canvas1 = new PdfCanvas(form1, pdfDocument);
        canvas1.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas1.setExtGState(state);
        canvas1.circle(25, 25, 10);
        canvas1.fill();
        canvas1.restoreState();
        canvas1.release();
        form1.flush();

        //Create form XObject and flush to document.
        PdfFormXObject form = new PdfFormXObject(new Rectangle(0, 0, 50, 50));
        PdfCanvas canvas = new PdfCanvas(form, pdfDocument);
        canvas.rectangle(10, 10, 30, 30);
        canvas.stroke();
        canvas.addXObjectAt(form1, 0, 0);
        canvas.release();
        form.flush();

        //Create page1 and add forms to the page.
        PdfPage page1 = pdfDocument.addNewPage();
        canvas = new PdfCanvas(page1);
        canvas.addXObjectAt(form, 0, 0);
        canvas.release();

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE),
                e.getMessage());
    }

    @Test
    public void testTransparencyObjectsAbsence() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "transparencyObjectsAbsence.pdf";
        String cmpPdf = cmpFolder + "cmp_transparencyObjectsAbsence.pdf";

        PdfDocument pdfDocument = new PdfADocument(new PdfWriter(outPdf), PdfAConformance.PDF_A_3B, null);
        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfCanvas canvas = new PdfCanvas(page);
        page.getResources().setDefaultGray(new PdfCieBasedCs.CalGray(getCalGrayArray()));
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page 1")
                .endText();

        PdfDictionary groupObj = new PdfDictionary();
        groupObj.put(PdfName.Type, PdfName.Group);
        groupObj.put(PdfName.S, PdfName.Transparency);
        page.getPdfObject().put(PdfName.Group, groupObj);

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    private PdfArray getCalGrayArray() {
        PdfDictionary dictionary = new PdfDictionary();

        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));

        PdfArray whitePointArray = new PdfArray();
        whitePointArray.add(new PdfNumber(0.9505));
        whitePointArray.add(new PdfNumber(1.0));
        whitePointArray.add(new PdfNumber(1.089));
        dictionary.put(PdfName.WhitePoint, whitePointArray);

        PdfArray array = new PdfArray();
        array.add(PdfName.CalGray);
        array.add(dictionary);

        return array;
    }
}
