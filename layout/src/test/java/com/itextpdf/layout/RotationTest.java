/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
import com.itextpdf.layout.property.BoxSizingPropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class RotationTest extends ExtendedITextTest{
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/RotationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/RotationTest/";
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

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void fixedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest01.pdf";
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String longText = "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        document.add(new Paragraph(longText).setMargin(0).setRotationAngle(-(Math.PI / 6)).setFixedPosition(50, 50, 450));
        document.add(new Paragraph(longText).setMargin(0).setFixedPosition(50, 50, 450));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest03.pdf";
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest04.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest04.pdf";
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void staticTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        for (int i = 0; i < 7; ++i)
            p.add(para2Text);
        document.add(p.setRotationAngle((68 * Math.PI / 180)).setBackgroundColor(ColorConstants.BLUE));
        document.add(new Paragraph("text line text line text line text line text line text line text line text line text line text line text line"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void staticTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 12)));
        document.add(new Paragraph(new Text(para2Text).setBackgroundColor(ColorConstants.GREEN)).
                setRotationAngle((-Math.PI / 12)).setBackgroundColor(ColorConstants.BLUE));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void staticTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 6)).setBackgroundColor(ColorConstants.RED));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void staticTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest04.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph("short text string").setRotationAngle((Math.PI / 6)).setBackgroundColor(ColorConstants.RED));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void splitTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "splitTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "splitTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 4)));
        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void splitTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "splitTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "splitTextRotationTest02.pdf";
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotationInfiniteLoopTest01() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest01.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void rotationInfiniteLoopTest02() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest02.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new List().add(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    @Test
    public void tableRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "tableRotationTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void tableRotationTest03() throws IOException,InterruptedException {
        String outFileName = destinationFolder + "tableRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "tableRotationTest03.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cellRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cellRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "cellRotationTest01.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cellRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cellRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "cellRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[] {5, 95}));
        table.addCell(new Cell()
                .add(new Paragraph("Hello world").setRotationAngle(Math.PI / 2)));
        table.addCell(new Cell()
                .add(new Paragraph("Long long long Long long long Long long long Long long long text")));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cellRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cellRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "cellRotationTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPointArray(new float[] {-1, -1}));
        table.addCell(new Cell()
                .add(new Paragraph("Hello world").setRotationAngle(Math.PI / 2)));
        table.addCell(new Cell()
                .add(new Paragraph("Long long long Long long long Long long long Long long long text")));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "divRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Div div = new Div().setBackgroundColor(ColorConstants.GREEN);
        div.add(new Paragraph(para1Text).setBackgroundColor(ColorConstants.RED)).setRotationAngle(Math.PI / 4);
        doc.add(div);

        div = new Div();
        div.add(new Paragraph(para1Text)).setRotationAngle(Math.PI / 2);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void divRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "divRotationTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void listRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "listRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "listRotationTest01.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void listRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "listRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "listRotationTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "alignedTextRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));

        Paragraph p = new Paragraph();
        p.add("texttext").setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);
        p.setRotationAngle(Math.PI / 4);
        doc.add(p);

        doc.add(new Paragraph(para3Text));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void innerRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "innerRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "innerRotationTest01.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 3)
    })
    @Test
    public void innerRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "innerRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "innerRotationTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedWidthRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedWidthRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedWidthRotationTest01.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Text text = new Text("Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.");
        Div d = new Div()
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 5))
                .setPadding(5);
        Paragraph p = new Paragraph(text)
                .setWidth(600)
                .setRotationAngle(Math.PI/2)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(d.add(p));
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedWidthRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedWidthRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedWidthRotationTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedWidthRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedWidthRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedWidthRotationTest03.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void ImageInRotatedBlockTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageInRotatedBlockTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageInRotatedBlockTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setWidth(200);

        Div div = new Div();
        div.setRotationAngle(Math.PI / 2);
        div.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        div.add(image);

        doc.add(div);
        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT),
            @LogMessage(messageTemplate = LogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, count = 2)
    })
    public void ImageInRotatedBlockTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageInRotatedBlockTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_imageInRotatedBlockTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setWidth(200);

        Div div = new Div();
        div.setHeight(100);
        div.setRotationAngle(Math.PI / 2);
        div.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        div.add(image);

        doc.add(div);
        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void blockWithBorderBoxSizingTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockWithBorderBoxSizingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithBorderBoxSizingTest01.pdf";
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    //TODO: currently is incorrect. See DEVSIX-989
    public void marginsRotatedTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "marginsRotatedTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "marginsRotatedTest01.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    //TODO: currently is incorrect. See DEVSIX-989
    public void marginsRotatedTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "marginsRotatedTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "marginsRotatedTest02.pdf";

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void drawCross(PdfCanvas canvas, float x, float y) {
        drawLine(canvas, x - 50, y, x + 50, y);
        drawLine(canvas, x, y - 50, x, y + 50);
    }

    private void drawLine(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.saveState().setLineWidth(0.5f).setLineDash(3).moveTo(x1, y1).lineTo(x2,y2).stroke().restoreState();
    }
}
