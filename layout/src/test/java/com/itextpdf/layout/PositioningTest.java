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
package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PositioningTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PositioningTest/";
    public static final String destinationFolder = TestUtil.getOutputPath() + "/layout/PositioningTest/";

    @BeforeAll
    public static void beforeClass() {
       createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void relativePositioningTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "relativePositioningTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new SolidBorder(new DeviceGray(0), 5)).
                setWidth(260).
                setPaddings(20, 20, 20, 20).
                add("Here is a line of text.").
                add(new Text("This part is shifted\n up a bit,").setRelativePosition(0, -10, 0, 0).setBackgroundColor(new DeviceGray(0.8f))).
                add("but the rest of the line is in its original position.");

        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void relativePositioningTest02() throws IOException, InterruptedException{
        String outFileName = destinationFolder + "relativePositioningTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new SolidBorder(new DeviceGray(0), 5)).
                        setWidth(140).
                        setPaddings(20, 20, 20, 20).
                        add("Here is a line of text.").
                        add(new Text("This part is shifted\n up a bit,").setRelativePosition(0, -10, 0, 0).setBackgroundColor(new DeviceGray(0.8f))).
                        add("but the rest of the line is in its original position.").
                        setRelativePosition(50, 0, 0, 0);

        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void relativePositioningTable01Test() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "relativePositioningTable01Test.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTable01Test.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table = new Table(new UnitValue[] {UnitValue.createPointValue(100), UnitValue.createPointValue(100)});
        table.addCell("One");
        table.addCell("Two");
        table.setRelativePosition(100, 20, 0, 0);

        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedPositioningTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        List list = new List(ListNumberingType.ROMAN_UPPER).
                setFixedPosition(2, 300, 300, 50).
                setBackgroundColor(ColorConstants.BLUE).
                setHeight(100);
        list.add("Hello").
            add("World").
            add("!!!");
        document.add(list);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedPositioningTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.getPdfDocument().addNewPage();

        new PdfCanvas(document.getPdfDocument().getPage(1)).setFillColor(ColorConstants.BLACK).rectangle(300, 300, 100, 100).fill().release();

        Paragraph p = new Paragraph("Hello").setBackgroundColor(ColorConstants.BLUE).setHeight(100).
                setFixedPosition(1, 300, 300, 100);
        document.add(p);


        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 1))
    public void fixedPositioningTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.getPdfDocument().addNewPage();

        Paragraph p = new Paragraph("Hello,  this is fairly long text. Lorem ipsum dolor sit amet, " +
                "consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna " +
                "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex " +
                "ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                "mollit anim id est laborum.").setMargin(0).setBackgroundColor(ColorConstants.LIGHT_GRAY).setHeight(100).
                setFixedPosition(1, 300, 300, 100);
        document.add(p);

        new PdfCanvas(document.getPdfDocument().getPage(1)).setStrokeColor(ColorConstants.BLACK).rectangle(300, 300, 100, 100).stroke().release();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 1))
    public void fixedPositioningTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.getPdfDocument().addNewPage();

        Div div = new Div().setBackgroundColor(ColorConstants.LIGHT_GRAY).setHeight(100)
                .setFixedPosition(1, 300, 300, 100)
                .add(new Paragraph("Hello,  this is fairly long text. Lorem ipsum dolor sit amet, " +
                        "consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna " +
                        "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex " +
                        "ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                        "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                        "mollit anim id est laborum.").setMargin(0));
        document.add(div);

        new PdfCanvas(document.getPdfDocument().getPage(1)).setStrokeColor(ColorConstants.BLACK).rectangle(300, 300, 100, 100).stroke().release();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void showTextAlignedTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "showTextAlignedTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_showTextAlignedTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        String text = "textapqgaPQGatext";
        float width = 200;
        float x, y;

        y = 700;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.BOTTOM, (float) (Math.PI / 6 * 1));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.MIDDLE, (float) (Math.PI/6 * 3));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, TextAlignment.LEFT, VerticalAlignment.TOP, (float) (Math.PI/6 * 5));

        y = 400;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.BOTTOM, (float) (Math.PI/6 * 2));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) (Math.PI / 6 * 4));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, TextAlignment.CENTER, VerticalAlignment.TOP, (float) (Math.PI/6 * 8));

        y = 100;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, (float) (Math.PI/6 * 9));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.MIDDLE, (float) (Math.PI/6 * 7));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, TextAlignment.RIGHT, VerticalAlignment.TOP, (float) (Math.PI/6 * 6));

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    @Test
    public void showTextAlignedTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "showTextAlignedTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_showTextAlignedTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);

        String watermarkText = "WATERMARK";
        Paragraph watermark = new Paragraph(watermarkText);
        watermark.setFontColor(new DeviceGray(0.75f)).setFontSize(72);
        document.showTextAligned(watermark, PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 1, TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) (Math.PI / 4));

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        document.add(new Paragraph(textContent + textContent + textContent));
        document.add(new Paragraph(textContent + textContent + textContent));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    @Test
    public void showTextAlignedTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "showTextAlignedTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_showTextAlignedTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Image img = new Image(ImageDataFactory.create(sourceFolder + "bruno.jpg"));
        float width = img.getImageScaledWidth();
        float height = img.getImageScaledHeight();
        PdfFormXObject template = new PdfFormXObject(new Rectangle(width, height));

        Canvas canvas = new Canvas(template, pdfDoc);
        canvas
                .add(img)
                .showTextAligned("HELLO BRUNO", width / 2, height / 2, TextAlignment.CENTER);

        doc.add(new Image(template));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void showTextAlignedOnFlushedPageTest01() throws IOException {
        String outFileName = destinationFolder + "showTextAlignedOnFlushedPageTest01.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document doc = new Document(pdfDoc)) {

            Paragraph p = new Paragraph();
            for (int i = 0; i < 1000; ++i) {
                p.add("abcdefghijklkmnopqrstuvwxyz");
            }

            doc.add(p);
            // First page will be flushed by now, because immediateFlush is set to false by default.
            int pageNumberToDrawTextOn = 1;

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> doc.showTextAligned(new Paragraph("Hello Bruno on page 1!"), 36, 36, pageNumberToDrawTextOn,
                            TextAlignment.LEFT, VerticalAlignment.TOP, 0)
            );
            Assertions.assertEquals(LayoutExceptionMessageConstant.CANNOT_DRAW_ELEMENTS_ON_ALREADY_FLUSHED_PAGES, e.getMessage());
        }
    }


    private void drawCross(PdfCanvas canvas, float x, float y) {
        drawLine(canvas, x - 50, y, x + 50, y);
        drawLine(canvas, x, y - 50, x, y + 50);
    }

    private void drawLine(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.saveState().setLineWidth(0.5f).setLineDash(3).moveTo(x1, y1).lineTo(x2,y2).stroke().restoreState();
    }
}
