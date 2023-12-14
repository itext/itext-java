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
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CanvasTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/CanvasTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/CanvasTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN))
    public void canvasNoPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasNoPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(pdfCanvas, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasWithPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageEnableTaggingTest01() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest01";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN),
            @LogMessage(messageTemplate = LogMessageConstant.PASSED_PAGE_SHALL_BE_ON_WHICH_CANVAS_WILL_BE_RENDERED)})
    public void canvasWithPageEnableTaggingTest02() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest02";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);

        // This will disable tagging and also prevent annotations addition. Created tagged document is invalid. Expected log message.
        canvas.enableAutoTagging(null);

        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void elementWithAbsolutePositioningInCanvasTest() throws IOException, InterruptedException {
        String testName = "elementWithAbsolutePositioningInCanvas";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();
            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 60, 80));

            Div notFittingDiv = new Div().setWidth(100)
                    .add(new Paragraph("Paragraph in Div with Not set position"));
            canvas.add(notFittingDiv);

            Div divWithPosition = new Div().setFixedPosition(120, 300, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));
            canvas.add(divWithPosition);

            canvas.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    //TODO: DEVSIX-4820 (discuss the displaying of element with absolute position)
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.CANVAS_ALREADY_FULL_ELEMENT_WILL_BE_SKIPPED)})
    public void parentElemWithAbsolPositionKidNotSuitCanvasTest() throws IOException, InterruptedException {
        String testName = "parentElemWithAbsolPositionKidNotSuitCanvas";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();

            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 55, 80));

            Div notFittingDiv = new Div().setWidth(100).add(new Paragraph("Paragraph in Div with Not set position"));
            canvas.add(notFittingDiv);

            Div divWithPosition = new Div().setFixedPosition(120, 300, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));
            canvas.add(divWithPosition);

            canvas.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    //TODO: DEVSIX-4820 (NullPointerException on processing absolutely positioned elements in small canvas area)
    public void nestedElementWithAbsolutePositioningInCanvasTest() throws IOException, InterruptedException {
        String testName = "nestedElementWithAbsolutePositioningInCanvas";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();

            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 55, 80));

            Div notFittingDiv = new Div().setWidth(100).add(new Paragraph("Paragraph in Div with Not set position"));

            Div divWithPosition = new Div().setFixedPosition(50, 20, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));

            notFittingDiv.add(divWithPosition);

            Assert.assertThrows(NullPointerException.class, () -> canvas.add(notFittingDiv));
            canvas.close();
        }

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }
}
