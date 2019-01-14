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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OpacityTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/OpacityTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/OpacityTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void backgroundOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "backgroundOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_backgroundOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(200);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) background.").setBackgroundColor(ColorConstants.RED, 0f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) background.").setBackgroundColor(ColorConstants.RED, 0.3f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) background.").setBackgroundColor(ColorConstants.RED, 0.5f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) background.").setBackgroundColor(ColorConstants.RED, 0.7f));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) background.").setBackgroundColor(ColorConstants.RED, 1f));
        div.add(new Paragraph("Simple text inside of the div with background.").setBackgroundColor(ColorConstants.RED));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_borderOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f, 0.0f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f, 0.3f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f, 0.5f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f, 0.7f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f, 1.0f)));
        div.add(new Paragraph("Simple text inside of the div with border.").setBorder(new DoubleBorder(ColorConstants.RED, 7f)));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) text.").setFontColor(ColorConstants.RED, 0f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) text.").setFontColor(ColorConstants.RED, 0.3f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) text.").setFontColor(ColorConstants.RED, 0.5f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) text.").setFontColor(ColorConstants.RED, 0.7f));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) text.").setFontColor(ColorConstants.RED, 1f));
        div.add(new Paragraph("Simple text inside of the div with text.").setFontColor(ColorConstants.RED));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void underlineOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "underlineOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_underlineOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) underline.").setUnderline(ColorConstants.RED, 0.0f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) underline.").setUnderline(ColorConstants.RED, 0.3f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) underline.").setUnderline(ColorConstants.RED, 0.5f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) underline.").setUnderline(ColorConstants.RED, 0.7f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) underline.").setUnderline(ColorConstants.RED, 1.0f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with underline.").setUnderline(ColorConstants.RED, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("text");
    }

    @Test
    public void divElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("div");
    }

    @Test
    public void paraElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("para");
    }

    @Test
    public void imageElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("image");
    }

    @Test
    public void cellElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("cell");
    }

    @Test
    public void tableElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("table");
    }

    @Test
    public void listElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("list");
    }

    @Test
    public void listItemElementOpacity01() throws IOException, InterruptedException {
        elementOpacityTest("listItem");
    }

    private void elementOpacityTest(String elem) throws IOException, InterruptedException {
        String outFileName = destinationFolder + elem + "ElementOpacity01.pdf";
        String cmpFileName = sourceFolder + "cmp_" + elem  + "ElementOpacity01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb divBackground = WebColors.getRGBColor("#82abd6");
        DeviceRgb paraBackground = WebColors.getRGBColor("#994ec7");
        DeviceRgb textBackground = WebColors.getRGBColor("#009688");
        DeviceRgb tableBackground = WebColors.getRGBColor("#ffc107");

        document.setFontColor(ColorConstants.WHITE);

        Div div = new Div().setBackgroundColor(divBackground);
        if ("div".equals(elem)) {
            div.setOpacity(0.3f);
        }
        div.add(new Paragraph("direct div content"));

        Paragraph p = new Paragraph("direct paragraph content").setBackgroundColor(paraBackground);
        if ("para".equals(elem)) {
            p.setOpacity(0.3f);
        }
        Text text = new Text("text content").setBackgroundColor(textBackground);
        p.add(text);
        if ("text".equals(elem)) {
            text.setOpacity(0.3f);
        }
        div.add(p);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        div.add(image);
        if ("image".equals(elem)) {
            image.setOpacity(0.3f);
        }

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth().setBackgroundColor(tableBackground);
        table.addCell("Cell00");
        table.addCell("Cell01");
        Cell cell10 = new Cell().add(new Paragraph("Cell10"));
        if ("cell".equals(elem)) {
            cell10.setOpacity(0.3f);
        }
        table.addCell(cell10);
        table.addCell(new Cell().add(new Paragraph("Cell11")));
        if ("table".equals(elem)) {
            table.setOpacity(0.3f);
        }
        div.add(table);

        List list = new List();
        if ("list".equals(elem)) {
            list.setOpacity(0.3f);
        }
        ListItem listItem = new ListItem("item 0");
        list.add(listItem);
        if ("listItem".equals(elem)) {
            listItem.setOpacity(0.3f);
        }
        list.add("item 1");
        div.add(list);

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
