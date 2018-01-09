/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
    public void clearanceApplyingPageSplit() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit.pdf";

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
    public void clearanceApplyingPageSplit02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit02.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit02.pdf";

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
    public void clearanceApplyingPageSplit03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit03.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit03.pdf";

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
    @Ignore("DEVSIX-1437")
    public void clearanceApplyingPageSplit04() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit04.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit04.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // TODO Adding float at the end of the page, it doesn't fit at all.

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear); // TODO Adding cleared element which shall be after the previous float.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff16_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void clearanceApplyingPageSplit05() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit05.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit05.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Paragraph("Floating div."));
        div.setHeight(400).setWidth(100);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // TODO Adding float at the end of the page, it is split.

        Div divClear = new Div();
        divClear.setBorder(new SolidBorder(ColorConstants.GREEN, 2)); //
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        document.add(divClear); // TODO Adding empty element with clearance - it shall be placed after the overflow part of the float.
        document.add(new Paragraph(text));

        document.add(new Paragraph(text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff15_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void clearanceApplyingPageSplit06() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit06.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit06.pdf";

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
        containerDiv.add(divClear); // TODO Float with clear shall be drawn under the previous float on second page.

        containerDiv.add(new Paragraph(text)); // TODO text shall start on the first page.
        document.add(containerDiv);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff23_"));
    }

    @Test
    public void clearanceApplyingPageSplit07() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit07.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit07.pdf";

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
    @Ignore("DEVSIX-1437")
    public void clearanceApplyingPageSplit08() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit08.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit08.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));
        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.add(new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400));
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        Div divClear = new Div().setBackgroundColor(ColorConstants.GREEN);
        divClear.add(new Paragraph("Cleared div."));
        divClear.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        containerDiv.add(divClear);

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff25_"));
    }

    @Test
    public void clearanceApplyingPageSplit09() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_clearanceApplyingPageSplit09.pdf";
        String outFile = destinationFolder + "clearanceApplyingPageSplit09.pdf";

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
    @Ignore("DEVSIX-1437")
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

        document.add(img); // TODO Image shall have overflowed to the next page.

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
    @Ignore("DEVSIX-1437")
    public void floatsOnPageSplit05() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit05.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit05.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div); // TODO Adding float that doesn't fit on first page.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        document.add(div2); // TODO Adding float that shall be after the previous float.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff21_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void floatsOnPageSplit06() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit06.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit06.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div.setHeight(600); // TODO Setting fixed height for the div, that will be split between pages.
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.add(img); // TODO Adding float that will not fit on the first page.
        div.add(new Paragraph("some small text"));

        document.add(div); // TODO div height shall be correct on the second page.

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff22_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
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
        containerDiv.add(div); // TODO Adding float that WILL fit on the first page.

        containerDiv.add(img); // TODO Adding that shall be overflowed to the next page. containerDiv occupied area shall not have zero height on first page.

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff27_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void floatsOnPageSplit08() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit08.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit08.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        document.add(new Paragraph(text + text));
        Div containerDiv = new Div();
        containerDiv.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400);
        div.add(img); // TODO Adding image that will not fit on first page to float.
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div);

        containerDiv.add(img); // TODO Adding normal image that will not fit on the first page.

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff28_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
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
        containerDiv.add(div); // TODO Adding float that will be split.

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(250);
        containerDiv.add(img); // TODO Adding image that will not fit on first page. containerDiv shall return PARTIAL status

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
    @Ignore("DEVSIX-1437")
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
        containerDiv.add(div); // TODO Adding float that will not fit.

        Div div2 = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        div2.add(new Paragraph(text)).setWidth(300);
        div2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        containerDiv.add(div2); // TODO Adding float that shall be after the previous float.

        document.add(containerDiv);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff31_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void floatsOnPageSplit12() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit12.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit12.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(400).setWidth(100);
        img.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.setHeight(300).add(img); // TODO Div shall have height of 300pt.
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff32_"));
    }

    @Test
    @Ignore("DEVSIX-1437")
    public void floatsOnPageSplit13() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatsOnPageSplit13.pdf";
        String outFile = destinationFolder + "floatsOnPageSplit13.pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div().setBorder(new SolidBorder(ColorConstants.RED, 2));
        Paragraph p = new Paragraph(text);
        p.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        div.setHeight(100).add(p);
        document.add(div);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff32_"));
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
}
