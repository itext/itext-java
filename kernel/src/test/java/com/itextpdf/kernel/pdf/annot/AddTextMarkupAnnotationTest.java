/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
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
public class AddTextMarkupAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder =
            "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddTextMarkupAnnotationTest/";
    public static final String destinationFolder =
            "./target/test/com/itextpdf/kernel/pdf/annot/AddTextMarkupAnnotationTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void textMarkupTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Underline!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createUnderline(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation01.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Highlight!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createHighLight(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation02.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation03.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Squiggly!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createSquiggly(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation03.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest04() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation04.pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Strikeout!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createStrikeout(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation04.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
}
