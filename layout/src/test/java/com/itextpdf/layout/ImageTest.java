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

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
public class ImageTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ImageTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest02() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest02.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createJpeg(UrlUtil.toURL(sourceFolder + "Desert.jpg")));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.add(new Text("before image"));
        p.add(image);
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest03() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest03.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setRotationAngle(Math.PI / 6);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest04() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest04.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.add(new Text("before image"));
        p.add(image);
        image.setRotationAngle(Math.PI / 6);
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest05() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest05.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.scale(1, 0.5f);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest06() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest06.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        p.add(image);
        image.setMarginLeft(100).setMarginTop(100);
        doc.add(p);
        doc.add(new Paragraph(new Text("Second Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest07() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest07.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest07.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        Div div = new Div();
        div.add(image);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    public void imageTest08() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest08.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest08.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);


        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        Div div = new Div();
        div.add(image);
        div.add(image);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest09() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest09.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest09.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, new PageSize(500, 300));

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setWidth(UnitValue.createPercentValue(100));
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest10() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest10.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest10.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, new PageSize(500, 300));

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScale(true);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest11() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest11.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest11.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScale(true);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest12_HorizontalAlignment_CENTER() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest12.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest12.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest13_HorizontalAlignment_RIGHT() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest13.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest13.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest14_HorizontalAlignment_LEFT() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest14.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest14.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        image.setHorizontalAlignment(HorizontalAlignment.LEFT);

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest15() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest15.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest15.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        image.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test()
    public void imageTest16() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest16.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest16.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));

        image.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        image.setAutoScale(true);
        image.setRotationAngle(Math.PI / 2);
        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test()
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 50)
    })
    public void imageTest17() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest17.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest17.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image1.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        Image image2 = new Image(ImageDataFactory.create(sourceFolder + "scarf.jpg"));
        image2.setBorder(new SolidBorder(ColorConstants.BLUE, 5));

        for (int i = 0; i <= 24; i++) {
            image1.setRotationAngle(i * Math.PI / 12);
            image2.setRotationAngle(i * Math.PI / 12);
            doc.add(image1);
            doc.add(image2);
        }

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest18() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest18.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest18.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScale(true);

        Div container = new Div();
        container.setBorder(new SolidBorder(1f));
        container.setWidth(UnitValue.createPercentValue(50f));
        container.setHeight(UnitValue.createPointValue(300f));
        container.add(image);
        doc.add(container);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    //TODO(DEVSIX-1659)
    @Test
    public void imageTest19() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest19.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest19.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScaleHeight(true);

        Div container = new Div();
        container.setBorder(new SolidBorder(1f));
        container.setWidth(UnitValue.createPercentValue(50f));
        container.setHeight(UnitValue.createPointValue(300f));
        container.add(image);
        doc.add(container);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest20() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest20.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest20.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScaleWidth(true);

        Div container = new Div();
        container.setBorder(new SolidBorder(1f));
        container.setWidth(UnitValue.createPercentValue(60f));
        container.setHeight(UnitValue.createPointValue(300f));
        container.add(image);
        doc.add(container);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    //TODO(DEVSIX-1659)
    @Test
    public void imageTest21() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest21.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest21.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setAutoScaleHeight(true);
        float[] colWidths = {1f, 1f};

        Table container = new Table(UnitValue.createPercentArray(colWidths));
        container.addCell("Text");
        container.addCell("autoscaling image, height only");

        int textIterations = 50;
        Paragraph p = new Paragraph();
        for (int i = 0; i < textIterations; i++) {
            p.add("Text will wrap");
        }
        container.addCell(p);

        container.addCell(image);

        doc.add(container);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1))
    public void imageTest22() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_imageTest22.pdf";
        String outFile = destinationFolder + "imageTest22.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph("Very small paragraph with text."));

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        // Image doesn't fit horizontally, so it's force placed.
        // However even though based on code, image should also be autoscaled to fit the available area,
        // current forced placement autoscaling implementation results in ignoring fixed dimensions in this case.
        document.add(img);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    public void imageTest23() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest23.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest23.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "encoded_tiff.tiff"));
        image.scaleToFit(500, 500);

        doc.add(image);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * Image can be reused in layout, so flushing it on the very first draw is a bad thing.
     */
    @Test
    public void flushOnDrawTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flushOnDrawTest.pdf";
        String cmpFileName = sourceFolder + "cmp_flushOnDrawTest.pdf";

        int rowCount = 60;
        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Table table = new Table(UnitValue.createPercentArray(8)).useAllAvailableWidth();
        for (int k = 0; k < rowCount; k++) {
            for (int j = 0; j < 7; j++) {
                table.addCell("Hello");
            }
            Cell c = new Cell().add(img.setWidth(UnitValue.createPercentValue(50)));
            table.addCell(c);
        }
        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * If an image is flushed automatically on draw, we will later check it for circular references
     * as it is an XObject. This is a test for {@link NullPointerException} that was caused by getting
     * a value from flushed image.
     */
    @Test
    public void flushOnDrawCheckCircularReferencesTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flushOnDrawCheckCircularReferencesTest.pdf";
        String cmpFileName = sourceFolder + "cmp_flushOnDrawCheckCircularReferencesTest.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        //Initialize document
        Document document = new Document(pdf);

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        img.setAutoScale(true);
        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        for (int k = 0; k < 5; k++) {
            table.addCell("Hello World from iText");

            List list = new List().setListSymbol("-> ");
            list.add("list item").add("list item").add("list item").add("list item").add("list item");
            Cell cell = new Cell().add(list);
            table.addCell(cell);

            Cell c = new Cell().add(img);
            table.addCell(c);

            Table innerTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
            int j = 0;
            while (j < 9) {
                innerTable.addCell("Hi");
                j++;
            }
            table.addCell(innerTable);
        }
        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageWithBordersSurroundedByTextTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageBordersTextTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageBordersTextTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        p.add(new Text("before image"));
        p.add(image);
        image.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        p.add(new Text("after image"));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageInParagraphBorderTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageParagraphBorderTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageParagraphBorderTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        p.add(image);
        image.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    //TODO(DEVSIX-1022)
    @Test
    public void imageRelativePositionTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageRelativePositionTest.pdf";
        String cmpFileName = sourceFolder + "cmp_imageRelativePositionTest.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 100).setRelativePosition(30, 30, 0, 0);

        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(ColorConstants.GREEN, 5));
        p.add(image);
        image.setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)})
    public void imageInTableTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageInTableTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageInTableTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.setMaxHeight(300);
        table.setBorder(new SolidBorder(ColorConstants.BLUE, 10));

        Cell c = new Cell().add(img.setHeight(500));
        table.addCell(c);
        document.add(table);
        document.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Is my occupied area right?"));
        document.add(new AreaBreak());

        table.setMinHeight(150);
        document.add(table);
        document.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Is my occupied area right?"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)})
    public void imageInTableTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageInTableTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_imageInTableTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        table.setMaxHeight(300);
        table.setBorder(new SolidBorder(ColorConstants.BLUE, 10));

        Cell c = new Cell().add(img.setHeight(500));
        table.addCell("First cell");
        table.addCell(c);
        document.add(table);
        document.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Is my occupied area right?"));
        document.add(new AreaBreak());

        table.setMinHeight(150);
        document.add(table);
        document.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell("Is my occupied area right?"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    //TODO(DEVSIX-1045)
    @Test
    public void fixedPositionImageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositionImageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositionImageTest01.pdf";
        String imgPath = sourceFolder + "Desert.jpg";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        document.add(new Image(ImageDataFactory.create(imgPath), 12, pdfDoc.getDefaultPageSize().getHeight() - 36, 24).setBorder(new SolidBorder(ColorConstants.RED, 5)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageWithMinMaxHeightTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageWithMinMaxHeightTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageWithMinMaxHeightTest01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        Image image = new Image(xObject, 100);

        doc.add(new Paragraph(new Text("Default height")));
        doc.add(image);

        doc.add(new Paragraph(new Text("Min height bigger than default")));
        doc.add(image.setMinHeight(200));

        doc.add(new Paragraph(new Text("Min height smaller than default")));
        image.deleteOwnProperty(Property.MIN_HEIGHT);
        doc.add(image.setMinHeight(10));


        doc.add(new Paragraph(new Text("Max height bigger than default")));
        image.deleteOwnProperty(Property.MIN_HEIGHT);
        doc.add(image.setMaxHeight(250));

        doc.add(new Paragraph(new Text("Max height smaller than default")));
        image.deleteOwnProperty(Property.MAX_HEIGHT);
        doc.add(image.setMaxHeight(30));


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void precisionTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "precisionTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_precisionTest01.pdf";
        String imageFileName = sourceFolder + "LOGO_PDF_77.jpg";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas currentPdfCanvas = new PdfCanvas(page);

        Rectangle rc = new Rectangle(56.6929131f, 649.13385f, 481.889771f, 136.062988f);
        Canvas canvas = new Canvas(currentPdfCanvas, rc);

        Table table = new Table(UnitValue.createPointArray(new float[]{158f}));
        table.setTextAlignment(TextAlignment.LEFT);

        Image logoImage = new Image(ImageDataFactory.create(imageFileName));
        Paragraph p = new Paragraph().add(logoImage.setAutoScale(true));

        Cell cell = new Cell();
        cell.setKeepTogether(true);
        cell.add(p);

        table.addCell(cell.setHeight(85.03937f).setVerticalAlignment(VerticalAlignment.TOP).setPadding(0));
        canvas.add(table);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageBorderRadiusTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageBorderRadiusTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageBorderRadiusTest01.pdf";
        String imageFileName = sourceFolder + "itis.jpg";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Image image = new Image(ImageDataFactory.create(imageFileName));

        image.setBorderRadius(new BorderRadius(20));
        image.setBorderBottomLeftRadius(new BorderRadius(35));
        image.setBorder(new SolidBorder(ColorConstants.ORANGE, 5));

        doc.add(image);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 3)})
    public void createTiffImageTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createTiffImageTest.pdf";
        String cmpFileName = sourceFolder + "cmp_createTiffImageTest.pdf";
        String imgPath = sourceFolder + "group4Compression.tif";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        ImageData id = ImageDataFactory.create(imgPath);

        ImageData idAsTiff = ImageDataFactory
                .createTiff(UrlUtil.toURL(imgPath), true, 1, true);

        ImageData idAsTiffFalse = ImageDataFactory
                .createTiff(UrlUtil.toURL(imgPath), false, 1, false);

        document.add(new Image(id));
        document.add(new Image(idAsTiff));
        document.add(new Image(idAsTiffFalse));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void tiffImageWithoutCompressionTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tiffImageWithoutCompression.pdf";
        String cmpFileName = sourceFolder + "cmp_tiffImageWithoutCompression.pdf";
        String imgPath = sourceFolder + "no-compression-tag.tiff";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDoc);

        ImageData id = ImageDataFactory.create(imgPath);
        document.add(new Image(id));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff02_"));
    }
}
