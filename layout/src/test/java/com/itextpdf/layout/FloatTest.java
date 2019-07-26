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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
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
public class FloatTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatTest/";

    private static final String text =
            "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. " +
            "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. " +
            "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. " +
            "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. " +
            "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device. ";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void floatParagraphTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatParagraphTest01.pdf";
        String outFile = destinationFolder + "floatParagraphTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        Paragraph p = new Paragraph();
        p.add("paragraph1");
        p.setWidth(70);
        p.setHeight(100);
        p.setBorder(new SolidBorder(1));
        p.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Paragraph p1 = new Paragraph();
        p1.add("paragraph2");
        p1.setWidth(70);
        p1.setHeight(100);
        p1.setBorder(new SolidBorder(1));
        p1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        doc.add(p);
        doc.add(p1);
        Paragraph p2 = new Paragraph();
        p2.add("paragraph3");
        p2.setBorder(new SolidBorder(1));
        doc.add(p2);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff01_"));
    }

    @Test
    public void floatParagraphTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatParagraphTest02.pdf";
        String outFile = destinationFolder + "floatParagraphTest02.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph();
        p.add("paragraph1");
        p.setWidth(70);
        p.setHeight(100);
        p.setBorder(new SolidBorder(1));
        Paragraph p1 = new Paragraph();
        p1.add("paragraph2");
        p1.setBorder(new SolidBorder(1));

        p.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        doc.add(p);
        doc.add(p1);
        Paragraph p2 = new Paragraph();
        p2.add("paragraph3");
        p2.setBorder(new SolidBorder(1));
        doc.add(p2);
        doc.add(p2);
        Paragraph p3 = new Paragraph("paragraph4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        p3.setBorder(new SolidBorder(1));
        doc.add(p3);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff02_"));
    }

    @Test
    public void floatDivTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatDivTest01.pdf";
        String outFile = destinationFolder + "floatDivTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setWidth(70);

        Paragraph p = new Paragraph();
        p.add("div1");
        div.setBorder(new SolidBorder(1));
        p.setBorder(new SolidBorder(1));
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(p);
        doc.add(div);
        doc.add(new Paragraph("div2"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff03_"));
    }

    @Test
    public void floatDivTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatDivTest02.pdf";
        String outFile = destinationFolder + "floatDivTest02.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setMargin(0);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        Paragraph p = new Paragraph();
        p.add("More news");
        div.add(p);
        doc.add(div);

        div = new Div();
        div.setMargin(0);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p = new Paragraph();
        p.add("Even more news");
        div.add(p);
        doc.add(div);

        Div coloredDiv = new Div();
        coloredDiv.setMargin(0);
        coloredDiv.setBackgroundColor(ColorConstants.RED);
        Paragraph p1 = new Paragraph();
        p1.add("Some div");
        coloredDiv.add(p1);
        doc.add(coloredDiv);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff04_"));
    }

    @Test
    public void floatDivTest03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatDivTest03.pdf";
        String outFile = destinationFolder + "floatDivTest03.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setMargin(0);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.setHeight(760);
        div.setWidth(523);
        div.setBorder(new SolidBorder(1));

        Paragraph p = new Paragraph();
        p.add("More news");
        div.add(p);
        doc.add(div);

        div = new Div();
        div.setMargin(0);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        div.setBorder(new SolidBorder(1));
        p = new Paragraph();
        p.add("Even more news");
        div.add(p);
        doc.add(div);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff05_"));
    }

    @Test
    public void floatingImageInCell() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingImageInCell.pdf";
        String outFile = destinationFolder + "floatingImageInCell.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        table.addCell(new Cell().add(img1));
        table.addCell(new Cell().add(img2).add(new Paragraph("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
                "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
                "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
                "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
                "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n")));

        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff06_"));
    }

    @Test
    public void floatingImageToNextPage() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingImageToNextPage.pdf";
        String outFile = destinationFolder + "floatingImageToNextPage.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleAbsolute(100, 500);
        img1.setMarginLeft(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(new Paragraph(text));
        document.add(new Paragraph(text));
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff07_"));
    }

    @Test
    public void inlineFloatingImageToNextPage() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_inlineFloatingImageToNextPage.pdf";
        String outFile = destinationFolder + "inlineFloatingImageToNextPage.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleAbsolute(100, 500);
        img1.setMarginLeft(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(new Paragraph(text));
        document.add(new Paragraph(text));
        Paragraph p = new Paragraph();
        p.add(img2).add(text);
        document.add(p);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff08_"));
    }

    @Test
    public void floatingTwoImages() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTwoImages.pdf";
        String outFile = destinationFolder + "floatingTwoImages.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(400, 400);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(400, 400);
        img1.setMarginRight(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff09_"));
    }

    @Test
    public void floatingTwoImagesLR() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTwoImagesLR.pdf";
        String outFile = destinationFolder + "floatingTwoImagesLR.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(350, 350);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(350, 350);
        img1.setMarginLeft(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff10_"));
    }

    @Test
    public void floatingImageInParagraph() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingImageInParagraph.pdf";
        String outFile = destinationFolder + "floatingImageInParagraph.pdf";
        String imageSrc = sourceFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        // Image floats on the left inside the paragraph
        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img1.setMarginRight(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Paragraph p = new Paragraph();
        p.add(img1).add(text);
        document.add(p);

        // Image floats on the right inside the paragraph
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img2.setMarginLeft(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        p = new Paragraph();
        p.add(img2).add(text);
        document.add(p);

        // Paragraph containing image floats on the right inside the paragraph
        Image img3 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img3.setMarginLeft(10);
        p = new Paragraph();
        p.add(img3);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(p);
        document.add(new Paragraph(text));

        // Image floats on the left inside short paragraph
        Image img4 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img4.setMarginRight(10);
        img4.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p = new Paragraph();
        p.add(img4).add("A little text.");
        document.add(p);
        document.add(new Paragraph(text));

        // Image floats on the left inside short paragraph
        Image img5 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        img5.setMarginRight(10);
        img5.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p = new Paragraph();
        p.add(img4).add("A little text.");
        document.add(p);
        p = new Paragraph(text);
        p.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff11_"));
    }

    @Test
    public void floatsOnCanvas() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnCanvas.pdf";
        String outFile = destinationFolder + "floatsOnCanvas.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile)).setTagged();
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        Canvas canvas = new Canvas(pdfCanvas, pdfDoc, page.getPageSize().applyMargins(36, 36, 36, 36, false));
        canvas.enableAutoTagging(page);

        Div div = new Div().setBackgroundColor(ColorConstants.RED);
        Div fDiv = new Div().setBackgroundColor(ColorConstants.BLUE).setWidth(200).setHeight(200);
        fDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        Div fInnerDiv1 = new Div().setWidth(50).setHeight(50);
        fInnerDiv1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        fInnerDiv1.setBackgroundColor(ColorConstants.YELLOW);
        Div fInnerDiv2 = new Div().setWidth(50).setHeight(50);
        fInnerDiv2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        fInnerDiv2.setBackgroundColor(ColorConstants.CYAN);
        fDiv.add(fInnerDiv1);
        fDiv.add(fInnerDiv2);
        fDiv.add(new Paragraph("Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add"));

        div.add(fDiv).add(new Paragraph("Hello"));
        canvas.add(div);

        div = new Div().setBackgroundColor(ColorConstants.GREEN);
        div.add(new Paragraph("World"));
        canvas.add(div);
        canvas.add(div);

        canvas.close();
        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff12_"));
    }

    @Test
    public void floatsFixedWidthTest01_floatRight() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFixedWidthTest01_floatRight.pdf";
        String outFile = destinationFolder + "floatsFixedWidthTest01_floatRight.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div containerDiv = new Div().setBorder(new SolidBorder(3)).setPadding(10);
        Div parentFixedDiv = new Div().setWidth(300).setMarginLeft(150).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div childFixedDiv = new Div().setWidth(400).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
        childFixedDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        childFixedDiv.add(new Paragraph("inside float; float right, width 400pt").setMargin(0));
        parentFixedDiv.add(new Paragraph("before float; width 300pt").setMargin(0));
        parentFixedDiv.add(childFixedDiv);
        parentFixedDiv.add(new Paragraph("after float").setMargin(0));
        containerDiv.add(parentFixedDiv);
        Paragraph clearfix = new Paragraph("clearfix");
        clearfix.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(clearfix);
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_width01_"));
    }

    @Test
    public void floatsFixedWidth01_noFloat() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFixedWidth01_noFloat.pdf";
        String outFile = destinationFolder + "floatsFixedWidth01_noFloat.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div containerDiv = new Div().setBorder(new SolidBorder(3)).setPadding(10);
        Div parentFixedDiv = new Div().setWidth(300).setMarginLeft(150).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div childFixedDiv = new Div().setWidth(400).setBorder(new SolidBorder(ColorConstants.GREEN, 3));

        childFixedDiv.add(new Paragraph("inside child; width 400pt").setMargin(0));
        parentFixedDiv.add(new Paragraph("before child; width 300pt").setMargin(0));
        parentFixedDiv.add(childFixedDiv);
        parentFixedDiv.add(new Paragraph("after child").setMargin(0));
        containerDiv.add(parentFixedDiv);
        Paragraph clearfix = new Paragraph("clearfix");
        clearfix.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(clearfix);
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_width01_"));
    }

    @Test
    public void floatsFixedWidth01_floatLeft() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFixedWidth01_floatLeft.pdf";
        String outFile = destinationFolder + "floatsFixedWidth01_floatLeft.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div containerDiv = new Div().setBorder(new SolidBorder(3)).setPadding(10);
        Div parentFixedDiv = new Div().setWidth(300).setMarginLeft(150).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div childFixedDiv = new Div().setWidth(400).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
        childFixedDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        childFixedDiv.add(new Paragraph("inside float; float left; width 400pt").setMargin(0));
        parentFixedDiv.add(new Paragraph("before float; width 300pt").setMargin(0));
        parentFixedDiv.add(childFixedDiv);
        parentFixedDiv.add(new Paragraph("after float").setMargin(0));
        containerDiv.add(parentFixedDiv);
        Paragraph clearfix = new Paragraph("clearfix");
        clearfix.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(clearfix);
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_width01_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 3))
    public void floatFixedHeightContentNotFit() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatFixedHeightContentNotFit.pdf";
        String outFile = destinationFolder + "floatFixedHeightContentNotFit.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div.")).add(new Paragraph(text));
        div.setHeight(200).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);
        document.add(new Paragraph(text));

        Paragraph p = new Paragraph("Floating p.\n" + text).setBorder(new SolidBorder(ColorConstants.RED, 2));
        p.setHeight(200).setWidth(100);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(p);
        document.add(new Paragraph(text));

        Table table = new Table(UnitValue.createPercentArray(new float[]{0.3f, 0.7f})).setBorder(new SolidBorder(ColorConstants.RED, 2));
        table.addCell(new Paragraph("Floating table.")).addCell(new Paragraph(text));
        table.setHeight(200).setWidth(300);
        table.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(table);
        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff13_"));
    }

    @Test
    public void clearanceFixedHeightPageSplitInRoot01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceFixedHeightPageSplitInRoot01.pdf";
        String outFile = destinationFolder + "clearanceFixedHeightPageSplitInRoot01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(200).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div.")).add(new Paragraph(text));
        divClear.setHeight(400);
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff13_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInRoot01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInRoot01.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInRoot01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared floating div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(divClear);

        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff14_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInRoot02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInRoot02.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInRoot02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear);

        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff15_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInRoot03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInRoot03.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInRoot03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared floating div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(divClear);

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.BLUE, 2));
        div2.add(new Paragraph("Last float."));
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(div2);

        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff14_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInBlock01.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared floating div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        containerDiv.add(divClear); // Float with clear shall be drawn under the previous float on second page.

        containerDiv.add(new Paragraph(text)); // text shall start on the first page.
        document.add(containerDiv);
        document.add(new Paragraph(text));

        // TODO DEVSIX-1270: text around green cleared float is trying to wrap to the left of it (there are 2px of space)

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff23_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInBlock02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInBlock02.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInBlock02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(divClear);

        containerDiv.add(new Paragraph(text));
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff24_"));
    }

    @Test
    public void clearancePageSplitFloatPartialInBlock03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatPartialInBlock03.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatPartialInBlock03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared floating div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        containerDiv.add(divClear); // Float with clear shall be drawn under the previous float on second page.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.BLUE, 2));
        div2.add(new Paragraph("Last float."));
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        containerDiv.add(div2); // This float top shall not appear higher than floats tops added before this one.

        containerDiv.add(new Paragraph(text + text)); // text shall start on the first page.
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff23_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void clearancePageSplitFloatNothingInRoot01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInRoot01.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInRoot01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // Adding float at the end of the page, it doesn't fit and is to be forced placed.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear); // Adding cleared element which shall be after the previous float.
        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff16_01_"));
    }

    @Test
    public void clearancePageSplitFloatNothingInRoot02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInRoot02.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInRoot02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(300));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // Adding float at the end of the page, it doesn't fit vertically.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear); // Adding cleared element which shall be after the previous float.
        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff16_02_"));
    }

    @Test
    public void clearancePageSplitFloatNothingInRoot03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInRoot03.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInRoot03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(300));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // Adding float at the end of the page, it doesn't fit vertically.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(divClear); // Adding cleared element which shall be after the previous float.
        document.add(new Paragraph(text + text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff16_02_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void clearancePageSplitFloatNothingInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInBlock01.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float at the end of the page, it doesn't fit and is to be forced placed.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(divClear); // Adding cleared element which shall be after the previous float.
        containerDiv.add(new Paragraph(text));

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff25_"));
    }

    @Test
    public void clearancePageSplitFloatNothingInBlock02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInBlock02.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInBlock02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(300));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float at the end of the page, it doesn't fit vertically.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(divClear); // Adding cleared element which shall be after the previous float.
        containerDiv.add(new Paragraph(text));

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff25_"));
    }

    @Test
    public void clearancePageSplitFloatNothingInBlock03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearancePageSplitFloatNothingInBlock03.pdf";
        String outFile = destinationFolder + "clearancePageSplitFloatNothingInBlock03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(300));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float at the end of the page, it doesn't fit vertically.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        divClear.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        containerDiv.add(divClear); // Adding cleared element which shall be after the previous float.
        containerDiv.add(new Paragraph(text + text));

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff25_"));
    }

    @Test
    public void clearanceNoContentPageSplitFloatPartialInRoot01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceNoContentPageSplitFloatPartialInRoot01.pdf";
        String outFile = destinationFolder + "clearanceNoContentPageSplitFloatPartialInRoot01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // Adding float at the end of the page, it is split.

        Div divClear = new Div();
        divClear.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear); // Adding empty element with clearance - it shall be placed after the overflow part of the float.
        document.add(new Paragraph(text));

        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff15_"));
    }

    @Test
    public void clearanceNoContentPageSplitFloatPartialInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceNoContentPageSplitFloatPartialInBlock01.pdf";
        String outFile = destinationFolder + "clearanceNoContentPageSplitFloatPartialInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        Div divClear = new Div();
        divClear.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(divClear);
        containerDiv.add(new Paragraph(text));

        containerDiv.add(new Paragraph(text));

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff26_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2))
    public void floatsOnPageSplit01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit01.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        document.add(img);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff17_"));
    }

    @Test
    public void floatsOnPageSplit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit02.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(200);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        document.add(img);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff18_"));
    }

    @Test
    public void floatsOnPageSplit03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit03.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph(text).setWidth(250));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(250);
        document.add(img);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff19_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit04() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit04.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit04.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff20_"));
    }

    @Test
    public void floatsOnPageSplit05() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit05.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit05.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(280);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // Adding float that doesn't fit on first page.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div2); // Adding float that shall be after the previous float.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff21_"));
    }

    @Test
    public void floatsOnPageSplit06_01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit06_01.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit06_01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setHeight(600); // Setting fixed height for the div, that will be split between pages.
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(280);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(img); // Adding float that will not fit on the first page.
        div.add(new Paragraph("some small text"));

        document.add(div); // div height shall be correct on the second page.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff22_01_"));
    }

    @Test
    public void floatsOnPageSplit06_02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit06_02.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit06_02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setHeight(600); // Setting fixed height for the div, that will be split between pages.
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(250);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(img); // Adding float that WILL fit on the first page.
        div.add(new Paragraph("some small text"));

        document.add(div); // div height shall be correct on the second page.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff22_02"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit06_03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit06_03.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit06_03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        // Setting min height for the div, that will be split between pages.
        // Not setting max height, because float won't be forced placed because it doesn't fit in max height constraints.
        div.setMinHeight(600);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(img); // Adding float that will not fit on the first page and will have FORCED_PLACEMENT on the second.
        div.add(new Paragraph("some small text"));

        document.add(div); // TODO DEVSIX-1001: blocks don't extend their height to MIN_HEIGHT if forced placement is applied, why?

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff22_03"));
    }

    @Test
    public void floatsOnPageSplit07() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit07.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit07.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(200);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float that WILL fit on the first page.

        containerDiv.add(img); // Adding img that shall be overflowed to the next page. containerDiv occupied area shall not have zero height on first page.

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff27_"));
    }

    @Test
    public void floatsOnPageSplit08_01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit08_01.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit08_01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(310).setWidth(310);
        div.add(img); // Adding image that will not fit on first page in floating div.
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        containerDiv.add(img); // Adding normal image that will not fit on the first page.

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff28_01_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit08_02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit08_02.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit08_02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img); // Adding image that will not fit on first page in floating div.
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        containerDiv.add(img); // Adding normal image that will not fit on the first page and requires forced placement.

        document.add(containerDiv);
        document.close();

        // TODO DEVSIX-1001: currently forced placement is applied on containerDiv, which results in all it's content
        // being forced placed at once, rather than content being split more gracefully (it makes sense to put the second
        // image on the next empty area, not on current area).

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff28_02_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit08_03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit08_03.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit08_03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        containerDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(750).setWidth(600);
        containerDiv.add(img); // Adding normal image that will not fit on the first page and requires forced placement.
        containerDiv.add(new Paragraph(text)); // Adding more text that is naturally expected to be correctly shown.

        document.add(containerDiv);
        document.close();

        // TODO DEVSIX-1001: text in the container div gets lost. And floating property doesn't actually affect this.

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff28_03_"));
    }

    @Test
    public void floatsOnPageSplit09() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit09.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit09.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph(text).setWidth(250));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float that will be split.

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(250);
        containerDiv.add(img); // Adding image that will not fit on first page. containerDiv shall return PARTIAL status

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff29_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit10() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit10.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit10.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff30_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsOnPageSplit11() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit11.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit11.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div); // Adding float that will not fit.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div2); // Adding float that shall be after the previous float. And shall overflow to the third page.

        document.add(containerDiv);
        document.close();

        // TODO DEVSIX-1001: Forced placement is applied to the parent element, forcing it to return FULL even though part of the child element overflowed.

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff31_"));
    }

    @Test
    public void floatsOnPageSplit12_01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit12_01.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit12_01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(100);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.setMinHeight(300).add(img); // Div shall have height of 300pt.
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff32_01_"));
    }

    @Test
    public void floatsOnPageSplit12_02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit12_02.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit12_02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(100);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.setHeight(500).add(img); // Div shall have height of 500pt.
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff32_02_"));
    }

    @Test
    public void floatsOnPageSplit14() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit14.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit14.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        ImageData imgData = ImageDataFactory.create(sourceFolder + "itis.jpg");
        Image img1 = new Image(imgData).setHeight(200);
        div.add(img1);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div);


        Image img2 = new Image(imgData).setHeight(200);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(img2);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff33_"));
    }

    @Test
    public void floatsOnPageSplit15() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit15.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit15.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)).setTagged());

        Div mainDiv = new Div().setBorder(new SolidBorder(ColorConstants.CYAN, 3));

        mainDiv.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(280);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        mainDiv.add(div); // Adding float that doesn't fit on first page.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        mainDiv.add(div2); // Adding float that shall be after the previous float.

        document.add(mainDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff34_"));
    }

    @Test
    public void floatsOnPageSplit16() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit16.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit16.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.CYAN, 3));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(280);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        p.add(div); // Adding float that doesn't fit on first page.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        p.add(div2); // Adding float that shall be after the previous float.

        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff34_"));
    }

    @Test
    public void floatsOnPageSplit17() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit17.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit17.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div1 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.BLUE);
        Div div2 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.GREEN);
        Div div3 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.YELLOW);

        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div3.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div1);
        document.add(div2);
        document.add(div3);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff35_"));
    }

    @Test
    public void floatsOnPageSplit18() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit18.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit18.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div mainDiv = new Div();

        Div div1 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.BLUE);
        Div div2 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.GREEN);
        Div div3 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.YELLOW);

        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div3.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        mainDiv.add(div1);
        mainDiv.add(div2);
        mainDiv.add(div3);

        document.add(mainDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff36_"));
    }

    @Test
    public void floatsOnPageSplit19() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit19.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit19.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph();

        Div div1 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.BLUE);
        Div div2 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.GREEN);
        Div div3 = new Div().setWidth(100).setHeight(500).setBackgroundColor(ColorConstants.YELLOW);

        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div3.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        mainP.add(div1);
        mainP.add(div2);
        mainP.add(div3);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff37_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsKeepTogetherOnPageSplit01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsKeepTogetherOnPageSplit01.pdf";
        String outFile = destinationFolder + "floatsKeepTogetherOnPageSplit01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph floatP = new Paragraph(text + text)
                .setKeepTogether(true)
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 3));
        floatP.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(floatP);
        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff38_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsKeepTogetherOnPageSplit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsKeepTogetherOnPageSplit02.pdf";
        String outFile = destinationFolder + "floatsKeepTogetherOnPageSplit02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph("A bit of text."));
        Paragraph floatP = new Paragraph(text + text)
                .setKeepTogether(true)
                .setWidth(300)
                .setBorder(new SolidBorder(ColorConstants.RED, 3));
        floatP.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(floatP);
        for (int i = 0; i < 5; ++i) {
            document.add(new Paragraph(text));
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff39_"));
    }

    @Test
    public void floatsInParagraphPartialSplit01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit01.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph();

        Div div = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));
        mainP.add(div);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff40_"));
    }

    @Test
    public void floatsInParagraphPartialSplit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit02.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div div1 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.RED, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div0.add(new Paragraph(text));
        mainP.add(div0);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div1.add(new Paragraph(text));
        mainP.add(div1);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff41_"));
    }

    @Test
    public void floatsInParagraphPartialSplit03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit03.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div div1 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.RED, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        mainP.add(div0);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.add(new Paragraph(text));
        mainP.add(div1);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff42_"));
    }

    @Test
    public void floatsInParagraphPartialSplit04() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit04.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit04.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));
        Div div1 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.RED, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        mainP.add(div0);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div1.add(new Paragraph(text));
        mainP.add(div1);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff43_"));
    }

    @Test
    public void floatsInParagraphPartialSplit05() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit05.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit05.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        mainP.add(div0);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff44_"));
    }

    @Test
    public void floatsInParagraphPartialSplit06() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit06.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit06.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(220).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div1 = new Div().setWidth(220).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div2 = new Div().setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        div1.add(new Paragraph(text));
        div2.add(new Paragraph(text));
        mainP.add(div0);
        mainP.add(div1);
        mainP.add(new Text("Small text.").setFontColor(ColorConstants.LIGHT_GRAY));
        mainP.add(div2);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff45_"));
    }

    @Test
    public void floatsInParagraphPartialSplit07() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit07.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit07.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(200).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div1 = new Div().setWidth(200).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div2 = new Div().setWidth(70).setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        div1.add(new Paragraph(text));
        div2.add(new Paragraph(text));
        mainP.add(div0);
        mainP.add(div1);
        mainP.add(new Text("Small text.").setFontColor(ColorConstants.LIGHT_GRAY));
        mainP.add(div2);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff46_"));
    }

    @Test
    public void floatsInParagraphPartialSplit08() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsInParagraphPartialSplit08.pdf";
        String outFile = destinationFolder + "floatsInParagraphPartialSplit08.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph();

        Div div0 = new Div().setWidth(200).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div1 = new Div().setWidth(200).setBorder(new SolidBorder(ColorConstants.RED, 3));
        Div div2 = new Div().setWidth(70).setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div0.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div0.add(new Paragraph(text));
        div1.add(new Paragraph(text));
        div2.add(new Paragraph(text));
        mainP.add(div0);
        mainP.add(div1);
        mainP.add(div2);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff47_"));
    }

    @Test
    public void floatingTextInParagraphPartialSplit01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTextInParagraphPartialSplit01.pdf";
        String outFile = destinationFolder + "floatingTextInParagraphPartialSplit01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph().setBorder(new SolidBorder(ColorConstants.BLUE, 1.5f));

        Text floatText = new Text(text).setBorder(new SolidBorder(ColorConstants.RED, 3));
        floatText.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatText);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff51_"));
    }

    @Test
    public void floatingTextInParagraphPartialSplit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTextInParagraphPartialSplit02.pdf";
        String outFile = destinationFolder + "floatingTextInParagraphPartialSplit02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph().setBorder(new SolidBorder(ColorConstants.BLUE, 1.5f));

        Div div1 = new Div()
                .setWidth(220)
                .setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2.8f))
                .setBorderBottom(new SolidBorder(ColorConstants.DARK_GRAY, 1f))
                .setFontColor(ColorConstants.DARK_GRAY);
        Div div2 = new Div()
                .setWidth(220)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2.8f))
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1f))
                .setFontColor(ColorConstants.LIGHT_GRAY);

        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.add(new Paragraph(text));
        div2.add(new Paragraph(text));
        mainP.add(div1);
        mainP.add(div2);

        mainP.add("Text. ");

        Text floatText = new Text(text).setBorder(new SolidBorder(ColorConstants.RED, 3));
        floatText.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatText);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff52_"));
    }

    @Test
    public void floatingTextInParagraphPartialSplit03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTextInParagraphPartialSplit03.pdf";
        String outFile = destinationFolder + "floatingTextInParagraphPartialSplit03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph().setBorder(new SolidBorder(ColorConstants.BLUE, 1.5f));

        Div div1 = new Div().setWidth(190).setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 3)).setFontColor(ColorConstants.DARK_GRAY);
        Div div2 = new Div().setWidth(190).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3)).setFontColor(ColorConstants.LIGHT_GRAY);

        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div1.add(new Paragraph(text));
        div2.add(new Paragraph(text));
        mainP.add(div1);
        mainP.add(div2);

        Text floatText = new Text("A little bit of text.").setBorder(new SolidBorder(ColorConstants.RED, 3));
        floatText.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatText);
        mainP.add(text);

        document.add(mainP);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff53"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsFirstOnPageNotFit01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFirstOnPageNotFit01.pdf";
        String outFile = destinationFolder + "floatsFirstOnPageNotFit01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph mainP = new Paragraph();

        Div div = new Div()
                .setWidth(150)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3))
                .setKeepTogether(true);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        mainP.add(div);
        mainP.add(text);

        document.add(mainP);
        document.add(new Paragraph(text + text).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3)).setFontColor(ColorConstants.RED));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff48_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsFirstOnPageNotFit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFirstOnPageNotFit02.pdf";
        String outFile = destinationFolder + "floatsFirstOnPageNotFit02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div mainDiv = new Div();

        Div div = new Div()
                .setWidth(150)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3))
                .setKeepTogether(true);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        mainDiv.add(div);
        mainDiv.add(new Paragraph(text).setMargin(0));

        document.add(mainDiv);
        document.add(new Paragraph(text + text).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3)).setFontColor(ColorConstants.RED));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff49_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void floatsFirstOnPageNotFit03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFirstOnPageNotFit03.pdf";
        String outFile = destinationFolder + "floatsFirstOnPageNotFit03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setWidth(150)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3))
                .setKeepTogether(true);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        document.add(div);
        document.add(new Paragraph(text).setMargin(0));

        document.add(new Paragraph(text + text).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3)).setFontColor(ColorConstants.RED));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff50_"));
    }

    @Test
    public void floatPartialSplitBigGapAtPageEnd01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatPartialSplitBigGapAtPageEnd01.pdf";
        String outFile = destinationFolder + "floatPartialSplitBigGapAtPageEnd01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setWidth(350)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div.setFillAvailableAreaOnSplit(true); // specifying fill available area option

        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setWidth(345).setHeight(500));
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        document.add(div);
        document.add(new Paragraph(text + text + text).setMargin(0));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff54_"));
    }

    @Test
    public void floatPartialSplitBigGapAtPageEnd02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatPartialSplitBigGapAtPageEnd02.pdf";
        String outFile = destinationFolder + "floatPartialSplitBigGapAtPageEnd02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setWidth(350)
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3));

        div.setFillAvailableAreaOnSplit(true); // specifying fill available area option

        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setWidth(345).setHeight(500));
        div.add(new Paragraph(text).setFontColor(ColorConstants.LIGHT_GRAY));
        document.add(div);
        document.add(new Paragraph(text).setMargin(0));

        Div wideFloatingDiv = new Div()
                .add(new Paragraph(text))
                .setWidth(450)
                .setBorder(new SolidBorder(ColorConstants.RED, 3));
        wideFloatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(wideFloatingDiv);

        document.add(new Paragraph(text + text).setMargin(0));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff55_"));
    }

    @Test
    public void floatInParagraphLastLineLeadingOverflow01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatInParagraphLastLineLeadingOverflow01.pdf";
        String outFile = destinationFolder + "floatInParagraphLastLineLeadingOverflow01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text).setMargin(0).setMultipliedLeading(1.3f));
        Paragraph p = new Paragraph()
                .setFontColor(ColorConstants.RED)
                .setFixedLeading(20f);
        p.add("First line of red paragraph.\n");

        ImageData img = ImageDataFactory.create(sourceFolder + "itis.jpg");
        Image image = new Image(img);
        image.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p.add(image.setHeight(730).setWidth(300));

        p.add(text);
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff56_"));
    }

    @Test
    public void floatOverflowNothingInParagraph01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatOverflowNothingInParagraph01.pdf";
        String outFile = destinationFolder + "floatOverflowNothingInParagraph01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph p = new Paragraph()
                .setFontColor(ColorConstants.RED);

        ImageData img = ImageDataFactory.create(sourceFolder + "itis.jpg");
        Image image = new Image(img);
        image.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p.add(image.setHeight(400).setWidth(300));

        p.add("Some text goes here. ");
        Div div1 = new Div()
                .setBorder(new SolidBorder(ColorConstants.BLUE, 3))
                .add(new Paragraph("Floating div text"));
        Div div2 = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 3))
                .add(new Paragraph("Floating div text"));
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        p.add(div1);
        p.add(div2);

        p.add(text);
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff57_"));
    }

    @Test
    public void floatOverflowNothingInParagraph02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatOverflowNothingInParagraph02.pdf";
        String outFile = destinationFolder + "floatOverflowNothingInParagraph02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Paragraph p = new Paragraph()
                .setFontColor(ColorConstants.RED);

        ImageData img = ImageDataFactory.create(sourceFolder + "itis.jpg");
        Image image = new Image(img);
        image.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        p.add(image.setHeight(400).setWidth(300));

        document.add(p);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff58_"));
    }

    @Test
    public void floatInlineBlockTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatInlineBlockTest01.pdf";
        String outFile = destinationFolder + "floatInlineBlockTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(1));
        p.add("Float with large borders shall fit on first line with this text. ");
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 40));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph("Floating div."));
        p.add(div);
        p.add("Inline block with large borders floating. Inline block with large borders floating. " +
                "Inline block with large borders floating. Inline block with large borders floating.");
        doc.add(p);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff14_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsHeightFixedInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsHeightFixedInBlock01.pdf";
        String outFile = destinationFolder + "floatsHeightFixedInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setHeight(100);
        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_height_01_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsHeightFixedInBlock02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsHeightFixedInBlock02.pdf";
        String outFile = destinationFolder + "floatsHeightFixedInBlock02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setHeight(200);

        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_height_02_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsHeightFixedInParagraph01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsHeightFixedInParagraph01.pdf";
        String outFile = destinationFolder + "floatsHeightFixedInParagraph01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setHeight(100);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_height_03_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsHeightFixedInParagraph02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsHeightFixedInParagraph02.pdf";
        String outFile = destinationFolder + "floatsHeightFixedInParagraph02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setHeight(200);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_height_04_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsMaxHeightFixedInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMaxHeightFixedInBlock01.pdf";
        String outFile = destinationFolder + "floatsMaxHeightFixedInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setMaxHeight(100);
        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_maxheight_01_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsMaxHeightFixedInBlock02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMaxHeightFixedInBlock02.pdf";
        String outFile = destinationFolder + "floatsMaxHeightFixedInBlock02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setMaxHeight(200);

        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_maxheight_02_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsMaxHeightFixedInParagraph01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMaxHeightFixedInParagraph01.pdf";
        String outFile = destinationFolder + "floatsMaxHeightFixedInParagraph01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setMaxHeight(100);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_maxheight_03_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT))
    public void floatsMaxHeightFixedInParagraph02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMaxHeightFixedInParagraph02.pdf";
        String outFile = destinationFolder + "floatsMaxHeightFixedInParagraph02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setMaxHeight(200);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_maxheight_04_"));
    }

    @Test
    public void floatsMinHeightFixedInBlock01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightFixedInBlock01.pdf";
        String outFile = destinationFolder + "floatsMinHeightFixedInBlock01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setMinHeight(100);
        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheight_01_"));
    }

    @Test
    public void floatsMinHeightFixedInBlock02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightFixedInBlock02.pdf";
        String outFile = destinationFolder + "floatsMinHeightFixedInBlock02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Div div = new Div()
                .setBorder(new SolidBorder(ColorConstants.RED, 2))
                .setMinHeight(200);

        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        div.add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheight_02_"));
    }

    @Test
    public void floatsMinHeightFixedInParagraph01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightFixedInParagraph01.pdf";
        String outFile = destinationFolder + "floatsMinHeightFixedInParagraph01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setMinHeight(100);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheight_03_"));
    }

    @Test
    public void floatsMinHeightFixedInParagraph02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightFixedInParagraph02.pdf";
        String outFile = destinationFolder + "floatsMinHeightFixedInParagraph02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setMinHeight(200);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(new Paragraph(text));

        parentParagraph.add(div);
        document.add(parentParagraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheight_04_"));
    }

    @Test
    public void floatsMinHeightApplyingOnSplitTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightApplyingOnSplitTest01.pdf";
        String outFile = destinationFolder + "floatsMinHeightApplyingOnSplitTest01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text));

        // Gray area in this test is expected to be not split, continuous and have height equal
        // exactly to mainDiv min height property value. Floating elements shall not affect
        // occupied area of parent and also there is no proper way to split it.

        Div mainDiv = new Div();
        mainDiv.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMinHeight(400);
        mainDiv.add(new Paragraph(text));

        addFloatingElements(mainDiv);

        document.add(mainDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheightapplying_01_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)
    })
    public void floatsMinHeightApplyingOnSplitTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightApplyingOnSplitTest02.pdf";
        String outFile = destinationFolder + "floatsMinHeightApplyingOnSplitTest02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text));

        // Gray area in this test is expected to be not split, continuous and have height equal
        // exactly to mainDiv min height property value. Floating elements shall not affect
        // occupied area of parent and also there is no proper way to split it.

        // Floats on the second page are expected to be clipped, due to max_height constraints.

        Div mainDiv = new Div();
        mainDiv.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMinHeight(400)
                .setMaxHeight(750);
        mainDiv.add(new Paragraph(text));

        addFloatingElements(mainDiv);

        document.add(mainDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheightapplying_02_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)
    })
    public void floatsMinHeightApplyingOnSplitTest03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightApplyingOnSplitTest03.pdf";
        String outFile = destinationFolder + "floatsMinHeightApplyingOnSplitTest03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text));

        // Gray area in this test is expected to be split, however also not to have a gap before page end.
        // Min height shall be resolved exactly the way as it would be resolved if no floats were there.

        // The place at which floats are clipped on the second page shall be the same as in previous two tests.

        Div mainDiv = new Div();
        mainDiv.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMinHeight(750)
                .setMaxHeight(750);
        mainDiv.add(new Paragraph(text));

        addFloatingElements(mainDiv);

        document.add(mainDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheightapplying_03_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    public void floatsMinHeightApplyingOnSplitTest04() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightApplyingOnSplitTest04.pdf";
        String outFile = destinationFolder + "floatsMinHeightApplyingOnSplitTest04.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div mainDiv = new Div();
        mainDiv.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMaxHeight(750);

        mainDiv.add(new Paragraph(text));
        addFloatingElements(mainDiv);

        // Both additions of mainDiv to document are the same except the second one also contains a bit of non-floating
        // content in it. Places at which floating elements are clipped due to max_height shall be the same in both cases.
        // The place at which they are clipped shall also be the same with tests floatsMinHeightApplyingOnSplitTest01-03.

        // first addition
        document.add(new Paragraph(text));
        document.add(mainDiv);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        // second addition
        document.add(new Paragraph(text));
        int textLen = 100;
        mainDiv.add(new Paragraph(text.length() > textLen ? text.substring(0, textLen) : text));
        document.add(mainDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheightapplying_04_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    public void floatsMinHeightApplyingOnSplitTest05() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsMinHeightApplyingOnSplitTest05.pdf";
        String outFile = destinationFolder + "floatsMinHeightApplyingOnSplitTest05.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        // Since mainDiv is floating here, it encompasses all the floating children in it's occupied area.
        // In this case, behaviour is expected to be the same as with just normal content and min_height property:
        // height is not extended to all available height on first page in order not to "spend" height and ultimately
        // to have more space to show content constrained by max_height.

        Div mainDiv = new Div();
        mainDiv.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMinHeight(500)
                .setMaxHeight(750);
        mainDiv.add(new Paragraph(text));

        mainDiv
                .setWidth(UnitValue.createPercentValue(100))
                .setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        addFloatingElements(mainDiv);

        // Places where floating elements are clipped shall be the same in both additions. However these places are
        // different from previous tests because floats are included in parent's occupied area here.

        // first addition
        document.add(new Paragraph(text));
        document.add(mainDiv);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); // TODO DEVSIX-1819: floats break area-break logic if min_height doesn't overflow to the next page on first addition: SMALL TICKET
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); // adding two page breaks two work around the issue

        // second addition
        mainDiv.setMinHeight(50);
        document.add(new Paragraph(text));
        document.add(mainDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_minheightapplying_05_"));
    }

    @Test
    public void floatsFixedMaxHeightAndOverflowHidden01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsFixedMaxHeightAndOverflowHidden01.pdf";
        String outFile = destinationFolder + "floatsFixedMaxHeightAndOverflowHidden01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text + text.substring(0, text.length() / 2) + "."));

        Paragraph parentParagraph = new Paragraph()
                .setBorder(new SolidBorder(ColorConstants.MAGENTA, 2))
                .setMaxHeight(200);

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        Paragraph p = new Paragraph(text);
        div.add(p);
        parentParagraph.add(div);

        parentParagraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        parentParagraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
        div.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

        document.add(parentParagraph);

        document.close();

        // TODO DEVSIX-1818: overflow value HIDDEN doesn't clip floats because they are drawn later in different part of content stream.

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_maxheighthidden_01_"));
    }

    @Test
    public void floatsOverflowToNextLineAtPageEndInParagraph01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOverflowToNextLineAtPageEndInParagraph01.pdf";
        String outFile = destinationFolder + "floatsOverflowToNextLineAtPageEndInParagraph01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(3)).setMargin(0)
                .setFontSize(30)
                .setMinHeight(240);
        int textLen = 180;
        mainP.add(text.length() > textLen ? text.substring(0, textLen) : text);

        Div floatingDiv = new Div().setBackgroundColor(ColorConstants.YELLOW);
        floatingDiv.add(new Paragraph("Floating div contents.").setMargin(0));
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatingDiv);

        // On second page there shall be no parent paragraph artifacts: background and borders,
        // since min_height completely fits on first page.
        // Only floating element is overflown to the next page.

        document.add(mainP);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_overflowNextLineAtPageEnd_01_"));
    }

    @Test
    public void floatsOverflowToNextLineAtPageEndInParagraph02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOverflowToNextLineAtPageEndInParagraph02.pdf";
        String outFile = destinationFolder + "floatsOverflowToNextLineAtPageEndInParagraph02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(3)).setMargin(0);
        mainP.add(text.substring(0, (int) (text.length() * 0.8)));

        Text floatingText = new Text(text.substring(0, text.length() / 3))
                .setBorder(new SolidBorder(ColorConstants.CYAN, 3));
        floatingText.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatingText);

        // Since it's floats-only split, min_height is expected to be applied fully on the first page (it fits there)
        // and also no parent artifacts (borders, background) shall be drawn on second page.

        document.add(mainP);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_overflowNextLineAtPageEnd_02_"));
    }

    @Test
    public void floatsOverflowToNextLineAtPageEndInParagraph03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOverflowToNextLineAtPageEndInParagraph03.pdf";
        String outFile = destinationFolder + "floatsOverflowToNextLineAtPageEndInParagraph03.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));
        document.add(new Paragraph(text + text));

        Paragraph mainP = new Paragraph()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(3)).setMargin(0)
                .setMinHeight(240);
        mainP.add(text.substring(0, (int) (text.length() * 0.6)));

        Text floatingText = new Text(text.substring(0, text.length() / 8))
                .setBorder(new SolidBorder(ColorConstants.CYAN, 3))
                .setFontSize(40);
        floatingText.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        mainP.add(floatingText);

        // Since it's floats-only split, min_height is expected to be applied fully on the first page (it fits there)
        // and also no parent artifacts (borders, background) shall be drawn on second page.

        document.add(mainP);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_overflowNextLineAtPageEnd_03_"));
    }

    private void addFloatingElements(Div mainDiv) {
        Div yellow = new Div().setBackgroundColor(ColorConstants.YELLOW).setHeight(150).setWidth(UnitValue.createPercentValue(40)).setMargin(5);
        Div green = new Div().setBackgroundColor(ColorConstants.GREEN).setHeight(150).setWidth(UnitValue.createPercentValue(40)).setMargin(5);
        Div blue = new Div().setBackgroundColor(ColorConstants.BLUE).setHeight(150).setWidth(UnitValue.createPercentValue(90)).setMargin(5);

        Div orange = new Div().setBackgroundColor(ColorConstants.ORANGE).setWidth(UnitValue.createPercentValue(40)).setMargin(5);
        Div cyan = new Div().setBackgroundColor(ColorConstants.CYAN).setWidth(UnitValue.createPercentValue(40)).setMargin(5);

        yellow.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        green.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        blue.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        orange.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        cyan.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        blue.setKeepTogether(true);

        orange.add(new Paragraph(text + text));
        cyan.add(new Paragraph(text + text));

        mainDiv.add(yellow);
        mainDiv.add(green);
        mainDiv.add(blue);
        mainDiv.add(orange);
        mainDiv.add(cyan);
    }

    /**
     * Suggested by Richard Cohn.
     */
    @Test
    public void floatRootElementNotFitPage01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatRootElementNotFitPage01.pdf";
        String outFile = destinationFolder + "floatRootElementNotFitPage01.pdf";

        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(outFile);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new PageSize(600, 350));
        pdf.setTagged();

        // Initialize document
        Document document = new Document(pdf);

        // Document layout is correct if COLLAPSING_MARGINS is not true
        document.setProperty(Property.COLLAPSING_MARGINS, true);

        document.add(new Paragraph("Some text\nSome text\nSome text\nSome text\nSome text\nSome text"));
        byte data[] = new byte[1];
        ImageData raw = ImageDataFactory.create(1, 1, 1, 8, data, null);
        Image image = new Image(raw).setHeight(200);
        Div div = new Div();
        div.add(image);
        Div captionDiv = new Div();
        captionDiv.add(new Paragraph("Caption line 1\n").add("line 2"));
        div.add(captionDiv);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        //div.setKeepTogether(true);
        document.add(div);
        document.add(new Paragraph("After float"));
        document.add(new List(ListNumberingType.DECIMAL)
                .add("Some text\nSome text\nSome text\nSome text")
                .add("Some text\nSome text\nSome text")
                .add("Some text\nSome text")
                .add("Some text\nSome text"));

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff15_"));
    }

    /**
     * Suggested by Richard Cohn.
     */
    @Test
    public void floatRootElementNotFitPage02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatRootElementNotFitPage02.pdf";
        String outFile = destinationFolder + "floatRootElementNotFitPage02.pdf";

        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(outFile);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new PageSize(600, 350));
        pdf.setTagged();

        // Initialize document
        Document document = new Document(pdf);

        // Document layout is correct if COLLAPSING_MARGINS is not true
        document.setProperty(Property.COLLAPSING_MARGINS, true);

        document.add(new Paragraph("Some text\nSome text\nSome text\nSome text\nSome text\nSome text\nSome text"));
        byte data[] = new byte[1];
        ImageData raw = ImageDataFactory.create(1, 1, 1, 8, data, null);
        Image image = new Image(raw).setHeight(200);
        Div div = new Div();
        div.add(image);
        Div captionDiv = new Div();
        captionDiv.add(new Paragraph("Caption line 1\n").add("line 2"));
        div.add(captionDiv);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.setKeepTogether(true);
        //document.add(div);
        div = new Div();
        image = new Image(raw).setHeight(200);
        div.add(image);
        div.add(captionDiv);
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.setKeepTogether(true);
        document.add(div);
        document.add(new Paragraph("After float").setKeepWithNext(true));
        document.add(new List(ListNumberingType.DECIMAL)
                .add("List text\nList text\nList text\nList text")
                .add("List text\nList text\nList text")
                .add("List text\nList text")
                .add("List text\nList text"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff16_"));
    }

    @Test
    public void floatOverflowAlongWithNewContent01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatOverflowAlongWithNewContent01.pdf";
        String outFile = destinationFolder + "floatOverflowAlongWithNewContent01.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div divContainer = new Div();
        divContainer.setMargin(20);
        divContainer.setBorder(new SolidBorder(ColorConstants.BLACK, 10));
        divContainer.add(new Paragraph(text + text));
        Paragraph pFloat = new Paragraph(text)
                .setFontColor(ColorConstants.RED)
                .setWidth(300)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        pFloat.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        divContainer.add(pFloat);
        document.add(divContainer);
        document.add(new Paragraph(text + text));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_overflowNewContent01_"));
    }

    @Test
    public void floatOverflowAlongWithNewContent02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatOverflowAlongWithNewContent02.pdf";
        String outFile = destinationFolder + "floatOverflowAlongWithNewContent02.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div divContainer = new Div();
        divContainer.setMargin(20);
        divContainer.setBorder(new SolidBorder(ColorConstants.BLACK, 10));
        divContainer.add(new Paragraph(text + text));
        Paragraph pFloat = new Paragraph(text + text + text)
                .setFontColor(ColorConstants.RED)
                .setWidth(300)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        pFloat.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        divContainer.add(pFloat);
        document.add(divContainer);
        document.add(new Paragraph(text + text + text));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff_overflowNewContent02_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH))
    public void floatTableTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatTableTest01.pdf";
        String outFile = destinationFolder + "floatTableTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setWidth(38);

        Div floatDiv = new Div();
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        Table table = new Table(2);
        for (int i = 0; i < 26; i++) {
            table.addCell(new Cell().add(new Paragraph("abba a")));
            table.addCell(new Cell().add(new Paragraph("ab ab ab")));
        }

        floatDiv.add(table);
        div.add(floatDiv);

        doc.add(div);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff03_"));
    }
}
