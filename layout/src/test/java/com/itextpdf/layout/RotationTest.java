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
package com.itextpdf.layout;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class RotationTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/RotationTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/RotationTest/";
    public static final String cmpPrefix = "cmp_";

    private static final String para1Text = "The first published account of what would evolve into the Mafia in the United States came in the spring of 1869. " +
            "The New Orleans Times reported that the city's Second District had become overrun by \"well-known and notorious Sicilian murderers, " +
            "counterfeiters and burglars, who, in the last month, have formed a sort of general co-partnership or stock company for the plunder " +
            "and disturbance of the city.\" Emigration from southern Italy to the Americas was primarily to Brazil and Argentina, and New Orleans " +
            "had a heavy volume of port traffic to and from both locales.";
    private static final String para2Text = "Mafia groups in the United States first became influential in the New York City area, gradually progressing from small neighborhood" +
            " operations in Italian ghettos to citywide and eventually national organizations. The Black Hand was a name given to an extortion method used " +
            "in Italian neighborhoods at the turn of the 20th century. It has been sometimes mistaken for the Mafia itself, which it is not. Although the Black" +
            " Hand was a criminal society, there were many small Black Hand gangs.";
    private static final String para3Text = "From the 1890s to the 1900s (decade) in New York City, the Sicilian Mafia developed into the Five Points Gang and were very powerful in the" +
            " Little Italy of the Lower East Side. They were often in conflict with the Jewish Eastmans of the same area.";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void fixedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedTextRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        SolidBorder border = new SolidBorder(0.5f);
        int x1 = 350;
        int y1 = 600;
        int width1 = 100;
        document.add(new Paragraph("text to be rotatedg").setMargin(0).setRotationAngle((Math.PI / 6)).setFixedPosition(x1, y1, width1)
                .setBorder(border));
        document.add(new Paragraph("text to be rotatedg").setMargin(0).setFixedPosition(x1, y1, width1)
                .setBorder(border));

        String longText = "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        int x2 = 50;
        int y2 = 300;
        int width2 = 450;
        document.add(new Paragraph(longText).setMargin(0).setRotationAngle((Math.PI / 6)).setFixedPosition(x2, y2, width2));
        document.add(new Paragraph(longText).setMargin(0).setFixedPosition(x2, y2, width2));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedTextRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String longText = "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        document.add(new Paragraph(longText).setMargin(0).setRotationAngle(-(Math.PI / 6)).setFixedPosition(50, 50, 450));
        document.add(new Paragraph(longText).setMargin(0).setFixedPosition(50, 50, 450));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedTextRotationTest03.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedTextRotationTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String simpleText = "text simple text";
        float x = 50;
        float y = 380;
        float width = 200;
        document.add(new Paragraph(simpleText).setMargin(0).setRotationAngle((Math.PI / 2)).setFixedPosition(x, y, width));
        document.add(new Paragraph(simpleText).setMargin(0).setFixedPosition(x, y, width));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
        drawCross(canvas, x, y);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedTextRotationTest04.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedTextRotationTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String simpleText = "text simple text";
        float x = 50;
        float y = 380;
        float width = 100;
        document.add(new Paragraph(simpleText).setMargin(0).setRotationAngle(-(Math.PI / 4)).setBackgroundColor(ColorConstants.RED).setFixedPosition(x, y, width));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
        drawCross(canvas, x, y);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void staticTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "staticTextRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "staticTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        for (int i = 0; i < 7; ++i)
            p.add(para2Text);
        document.add(p.setRotationAngle((68 * Math.PI / 180)).setBackgroundColor(ColorConstants.BLUE));
        document.add(new Paragraph("text line text line text line text line text line text line text line text line text line text line text line"));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void staticTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "staticTextRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "staticTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 12)));
        document.add(new Paragraph(new Text(para2Text).setBackgroundColor(ColorConstants.GREEN)).
                setRotationAngle((-Math.PI / 12)).setBackgroundColor(ColorConstants.BLUE));
        document.add(new Paragraph(para3Text));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void staticTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "staticTextRotationTest03.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "staticTextRotationTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 6)).setBackgroundColor(ColorConstants.RED));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void staticTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "staticTextRotationTest04.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "staticTextRotationTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph("short text string").setRotationAngle((Math.PI / 6)).setBackgroundColor(ColorConstants.RED));
        document.add(new Paragraph(para3Text));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void splitTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "splitTextRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "splitTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 4)));
        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void splitTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "splitTextRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "splitTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text));

        String extremelyLongText = "";
        for (int i = 0; i < 300; ++i) {
            extremelyLongText += para2Text;
        }

        document.add(new Paragraph(extremelyLongText).setRotationAngle(Math.PI / 2));
        document.add(new Paragraph(extremelyLongText).setRotationAngle(Math.PI / 4));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void rotationInfiniteLoopTest01() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest01.pdf";
        String outFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void rotationInfiniteLoopTest02() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest02.pdf";
        String outFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new List().add(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    @Test
    public void tableRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "tableRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "tableRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50});
        table.setWidth(100);
        table.addCell(new Cell().add(new Paragraph("cell 1, 1").setRotationAngle((Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 1, 2").setRotationAngle((Math.PI / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 1").setRotationAngle((Math.PI * 2 / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 2").setRotationAngle((Math.PI))));
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void tableRotationTest03() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "tableRotationTest03.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "tableRotationTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{25, 50});
        table.setWidth(75).setFixedLayout();
        table.addCell(new Cell().add(new Paragraph("cell 1, 1").setRotationAngle((Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 1, 2").setRotationAngle((Math.PI / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")))
                .addCell(new Cell().add(new Paragraph("cell 3, 1").setRotationAngle(-(Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 3, 2").setRotationAngle((Math.PI))));
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void cellRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "cellRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "cellRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setWidth(50);
        table.addCell(new Cell()
                .add(new Paragraph("Hello"))
                .setRotationAngle(Math.PI * 70 / 180.0)
                .setBackgroundColor(ColorConstants.GREEN));
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void cellRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "cellRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "cellRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 95}));
        table.addCell(new Cell()
                .add(new Paragraph("Hello world").setRotationAngle(Math.PI / 2)));
        table.addCell(new Cell()
                .add(new Paragraph("Long long long Long long long Long long long Long long long text")));
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void cellRotationTest03() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "cellRotationTest03.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "cellRotationTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPointArray(new float[]{-1, -1}));
        table.addCell(new Cell()
                .add(new Paragraph("Hello world").setRotationAngle(Math.PI / 2)));
        table.addCell(new Cell()
                .add(new Paragraph("Long long long Long long long Long long long Long long long text")));
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void cellRotationDependsOnNeighbourCell() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "cellRotationDependsOnNeighbourCell.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "cellRotationDependsOnNeighbourCell.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(300, 180));

        doc.add(createTable(60));
        doc.add(new AreaBreak());

        doc.add(createTable(80));
        doc.add(new AreaBreak());

        doc.add(createTable(100));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    // TODO DEVSIX-5029 Content of the first cell is missing
    public void cellRotationParagraphIsGone() throws IOException, InterruptedException {
        String testName = "cellRotationParagraphIsGone.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        Table table = new Table(2);
        table.setFixedLayout();

        Cell cell = new Cell().add(new Paragraph().add("Hello World"));
        cell.setRotationAngle(Math.toRadians(90));
        cell.setBackgroundColor(ColorConstants.RED);
        table.addCell(cell);
        cell = new Cell().add(new Paragraph().add("AAAAAAAAAAAAAAAAA aaaaaaaaaaaaaaaaaaaaaaaa "
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        cell.setRotationAngle(Math.toRadians(90));
        cell.setBackgroundColor(ColorConstants.BLUE);
        table.addCell(cell);

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));

    }

    private Table createTable(float height) {
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        Cell rotatedCell = new Cell();
        rotatedCell.add(new Paragraph("ROTATED"));
        rotatedCell.setRotationAngle(Math.toRadians(90));
        table.addCell(rotatedCell);

        Cell cell = new Cell().add(new Paragraph("USUAL"));
        cell.setHeight(height);
        table.addCell(cell);

        return table;
    }

    @Test
    public void divRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "divRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "divRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Div div = new Div().setBackgroundColor(ColorConstants.GREEN);
        div.add(new Paragraph(para1Text).setBackgroundColor(ColorConstants.RED)).setRotationAngle(Math.PI / 4);
        doc.add(div);

        div = new Div();
        div.add(new Paragraph(para1Text)).setRotationAngle(Math.PI / 2);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void divRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "divRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "divRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));

        String extremelyLongText = "";
        for (int i = 0; i < 300; ++i) {
            extremelyLongText += para2Text;
        }

        doc.add(new Div().add(new Paragraph(extremelyLongText)).setRotationAngle(Math.PI / 2));
        doc.add(new Div().add(new Paragraph(extremelyLongText)).setRotationAngle(Math.PI / 4));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void listRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "listRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "listRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));

        List list = new List().setRotationAngle(3 * Math.PI / 4).setBackgroundColor(ColorConstants.GREEN);
        list.add(new ListItem("text of first list item"));
        list.add("text of second list item");
        list.add("text of third list item");
        doc.add(list);

        doc.add(new Paragraph(para2Text));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }


    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void listRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "listRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "listRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));

        List list = new List().setRotationAngle(Math.PI / 2).setBackgroundColor(ColorConstants.GREEN);
        String itemText = "list item text long item txt list item text long item txt list item text long item txt list item text long item txt list item text long item txt END";
        for (int i = 0; i < 10; ++i) {
            list.add(itemText);
        }
        doc.add(list);

        doc.add(new Paragraph(para2Text));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void alignedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "alignedTextRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "alignedTextRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));

        Paragraph p = new Paragraph();
        p.add("texttext").setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);
        p.setRotationAngle(Math.PI / 4);
        doc.add(p);

        doc.add(new Paragraph(para3Text));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void innerRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "innerRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "innerRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Div().
                setBackgroundColor(ColorConstants.GREEN).
                setHeight(300).setWidth(300).
                add(new Div().
                        setBackgroundColor(ColorConstants.RED).
                        setHeight(100).
                        setWidth(100).
                        setRotationAngle(Math.PI / 4)).
                setRotationAngle(Math.PI / 8)
        );

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 3)
    })
    @Test
    public void innerRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "innerRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "innerRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(6400, 6400));

        String longText = para1Text + para2Text + para3Text;
        String extremeLongText = longText + longText + longText;
        doc.add(new Div().
                setBackgroundColor(ColorConstants.GREEN).
                setMinHeight(300).setWidth(300).
                add(new Div().
                        setBackgroundColor(ColorConstants.RED).
                        setWidth(30).
                        setRotationAngle(5 * Math.PI / 16).
                        add(new Paragraph(extremeLongText))).
                add(new Paragraph("smaaaaaaaaaaaaaaaaaaaall taaaaaaaaaaaaaaaaaaalk")).
                add(new Paragraph("smaaaaaaaaaaaaaaaaaaaall taaaaaaaaaaaaaaaaaaalk")).
                setRotationAngle(Math.PI / 8)
        );

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedWidthRotationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedWidthRotationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedWidthRotationTest01.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 5))
                .setPadding(5);
        Paragraph p = new Paragraph(text)
                .setWidth(600)
                .setRotationAngle(Math.PI / 2)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(p));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedWidthRotationTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedWidthRotationTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedWidthRotationTest02.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 5))
                .setPadding(5);
        Paragraph p = new Paragraph(text)
                .setWidth(500)
                .setRotationAngle(Math.PI * 3 / 8)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(p));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fixedWidthRotationTest03() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fixedWidthRotationTest03.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "fixedWidthRotationTest03.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 5))
                .setPadding(5);
        Div d1 = new Div().add(new Paragraph(text))
                .setWidth(500)
                .setRotationAngle(Math.PI * 5 / 8)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(d1));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void imageInRotatedBlockTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "imageInRotatedBlockTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_imageInRotatedBlockTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "Desert.jpg"));
        image.setWidth(200);

        Div div = new Div();
        div.setRotationAngle(Math.PI / 2);
        div.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        div.add(image);

        doc.add(div);
        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT),
            @LogMessage(messageTemplate = IoLogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, count = 2)
    })
    public void imageInRotatedBlockTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "imageInRotatedBlockTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_imageInRotatedBlockTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "Desert.jpg"));
        image.setWidth(200);

        Div div = new Div();
        div.setHeight(100);
        div.setRotationAngle(Math.PI / 2);
        div.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        div.add(image);

        doc.add(div);
        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void blockWithBorderBoxSizingTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "blockWithBorderBoxSizingTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_blockWithBorderBoxSizingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Div div = new Div();
        div.setRotationAngle(Math.PI / 3);
        div.setBorder(new SolidBorder(ColorConstants.BLUE, 50));
        div.add(new Paragraph("Long long long Long long long Long long long Long long long Long long long Long long long text"));
        doc.add(div);

        doc.add(new AreaBreak());
        div.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    //TODO: currently is incorrect. See DEVSIX-989
    public void marginsRotatedTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginsRotatedTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "marginsRotatedTest01.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(400)
                .setBorder(new SolidBorder(ColorConstants.RED, 5));
        Div d1 = new Div().add(new Paragraph(text))
                .setWidth(200)
                .setRotationAngle(Math.PI / 4)
                .setMargins(100, 10, 100, 10)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(d1));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    //TODO: currently is incorrect. See DEVSIX-989
    public void marginsRotatedTest02() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginsRotatedTest02.pdf";
        String cmpFileName = SOURCE_FOLDER + cmpPrefix + "marginsRotatedTest02.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        doc.setProperty(Property.COLLAPSING_MARGINS, true);
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(400)
                .setBorder(new SolidBorder(ColorConstants.RED, 5));
        Div d1 = new Div().add(new Paragraph(text))
                .setWidth(200)
                .setRotationAngle(Math.PI / 4)
                .setMargins(100, 10, 100, 10)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(d1).add(new Paragraph("Hello").setMargin(50).setBorder(new SolidBorder(ColorConstants.GREEN, 5))));
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-4458
    public void zeroDegreeRotatedWithAlignmentParagraphInDivTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "zeroDegreeRotatedWithAlignmentParagraphInDiv.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_zeroDegreeRotatedWithAlignmentParagraphInDiv.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setWidth(300);

        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.LEFT)
                .setRotationAngle(Math.toRadians(0)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.CENTER)
                .setRotationAngle(Math.toRadians(0)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.RIGHT)
                .setRotationAngle(Math.toRadians(0)));

        doc.add(div);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-4458
    public void rotated180DegreesWithAlignmentParagraphInDivTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "rotated180DegreesWithAlignmentParagraphInDiv.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_rotated180DegreesWithAlignmentParagraphInDiv.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setWidth(300);

        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.LEFT)
                .setRotationAngle(Math.toRadians(180)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.CENTER)
                .setRotationAngle(Math.toRadians(180)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.RIGHT)
                .setRotationAngle(Math.toRadians(180)));

        doc.add(div);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-4458
    public void rotated90DegreesWithAlignmentParagraphInDivTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "rotated90DegreesWithAlignmentParagraphInDiv.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_rotated90DegreesWithAlignmentParagraphInDiv.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setHeight(300);

        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.LEFT)
                .setRotationAngle(Math.toRadians(90)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.CENTER)
                .setRotationAngle(Math.toRadians(90)));
        div.add(new Paragraph("The quick brown fox\njumps")
                .setTextAlignment(TextAlignment.RIGHT)
                .setRotationAngle(Math.toRadians(90)));

        doc.add(div);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    //TODO: update cmp file after fixing DEVSIX-4458
    public void rotatedWithAlignmentCellInTableTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "rotatedWithAlignmentCellInTable.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_rotatedWithAlignmentCellInTable.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Table table = new Table(1);
        table.setWidth(300);

        Cell cell = new Cell()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setRotationAngle(Math.toRadians(180));
        cell.add(new Paragraph("The quick brown fox\njumps").setTextAlignment(TextAlignment.LEFT));
        cell.add(new Paragraph("The quick brown fox\njumps").setTextAlignment(TextAlignment.CENTER));
        cell.add(new Paragraph("The quick brown fox\njumps").setTextAlignment(TextAlignment.RIGHT));

        table.addCell(cell);

        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void ignorePageRotationForContentTest() throws IOException, InterruptedException {
        String inputFile = SOURCE_FOLDER + "rotated.pdf";
        String compareFile = SOURCE_FOLDER + "cmp_ignorePageRotationForContent.pdf";
        String outputFile = DESTINATION_FOLDER + "ignorePageRotationForContent.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(outputFile));
             Document doc = new Document(pdfDoc)) {

            PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.5f);
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            Paragraph paragraph = new Paragraph("My watermark (text)")
                    .setFont(font)
                    .setFontSize(30);

            PdfPage pdfPage = pdfDoc.getPage(1);
            Rectangle pageSize = pdfPage.getPageSizeWithRotation();

            // When "true": in case the page has a rotation, then new content will be automatically rotated in the
            // opposite direction. On the rotated page this would look as if new content ignores page rotation.
            pdfPage.setIgnorePageRotationForContent(true);

            float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
            PdfCanvas over = new PdfCanvas(pdfPage);
            over.saveState();
            over.setExtGState(gs1);
            // Each showTextAligned call creates new PdfCanvas instance for the same page.
            doc.showTextAligned(paragraph, x, y + 100, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
            doc.showTextAligned(paragraph, x, y, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
            doc.showTextAligned(paragraph, x, y - 100, 1, TextAlignment.CENTER, VerticalAlignment.TOP, 0);
            over.restoreState();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outputFile, compareFile, DESTINATION_FOLDER));
        Assertions.assertNull(new CompareTool().compareVisually(outputFile, compareFile, DESTINATION_FOLDER, "diff_"));
    }

    private void drawCross(PdfCanvas canvas, float x, float y) {
        drawLine(canvas, x - 50, y, x + 50, y);
        drawLine(canvas, x, y - 50, x, y + 50);
    }

    private void drawLine(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.saveState().setLineWidth(0.5f).setLineDash(3).moveTo(x1, y1).lineTo(x2, y2).stroke().restoreState();
    }
}
