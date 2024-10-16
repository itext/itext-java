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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class MinWidthTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/MinWidthTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/MinWidthTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void paragraphTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "paragraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_paragraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str).setBorder(new SolidBorder(ColorConstants.BLACK, 5))).setBorder(new SolidBorder(ColorConstants.BLUE, 5));
        MinMaxWidth result = ((AbstractRenderer)p.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        p.setWidth(toEffectiveWidth(p, result.getMinWidth()));
        doc.add(p);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(ColorConstants.GREEN, 5)).setMargin(6);
        d.add(p);
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divWithSmallRotatedParagraph() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divSmallRotatedParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divSmallRotatedParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(ColorConstants.GREEN, 5)).setMargin(6);
        d.add(new Paragraph(("iText")).setRotationAngle(Math.PI/8).setBorder(new SolidBorder(ColorConstants.BLUE, 2f)));
        d.add(p);
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void divWithBigRotatedParagraph() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divBigRotatedParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divBigRotatedParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY).setRotationAngle(Math.PI/8);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(ColorConstants.GREEN, 5)).setMargin(6);
        d.add(p);
        d.add(new Paragraph(("iText")));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divWithSmallRotatedDiv() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divSmallRotatedDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divSmallRotatedDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(ColorConstants.GREEN, 5)).setMargin(6);
        d.add(p);
        Div dRotated = new Div().setRotationAngle(Math.PI/8).setBorder(new SolidBorder(ColorConstants.BLUE, 2f));
        d.add(dRotated.add(new Paragraph(("iText"))));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void divWithBigRotatedDiv() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divBigRotatedDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divBigRotatedDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Div dRotated = new Div().setPadding(4f).setBorder(new SolidBorder(ColorConstants.GREEN, 5)).setMargin(6);
        dRotated.add(p).setRotationAngle(Math.PI * 3 / 8);
        Div d = new Div().add(new Paragraph(("iText"))).add(dRotated).setBorder(new SolidBorder(ColorConstants.BLUE, 2f));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divWithPercentImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divPercentImage.pdf";
        String cmpFileName = sourceFolder + "cmp_divPercentImage.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        Image img = new Image(imageXObject);
        Div d = new Div().add(img).setBorder(new SolidBorder(ColorConstants.BLUE, 2f)).setMarginBottom(10);

        Image imgPercent = new Image(imageXObject).setWidth(UnitValue.createPercentValue(50));
        Div dPercent = new Div().add(imgPercent).setBorder(new SolidBorder(ColorConstants.BLUE, 2f));

        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        MinMaxWidth resultPercent = ((AbstractRenderer)dPercent.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        dPercent.setWidth(toEffectiveWidth(dPercent, resultPercent.getMaxWidth()));

        doc.add(d);
        doc.add(dPercent);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divWithRotatedPercentImage() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divRotatedPercentImage.pdf";
        String cmpFileName = sourceFolder + "cmp_divRotatedPercentImage.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        Image img = new Image(imageXObject).setRotationAngle(Math.PI * 3 / 8);
        Div d = new Div().add(img).setBorder(new SolidBorder(ColorConstants.BLUE, 2f)).setMarginBottom(10);

        Image imgPercent = new Image(imageXObject).setWidth(UnitValue.createPercentValue(50)).setRotationAngle(Math.PI * 3 / 8);
        Div dPercent = new Div().add(imgPercent).setBorder(new SolidBorder(ColorConstants.BLUE, 2f));

        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        MinMaxWidth resultPercent = ((AbstractRenderer)dPercent.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        dPercent.setWidth(toEffectiveWidth(dPercent, resultPercent.getMaxWidth()));

        doc.add(d);
        doc.add(dPercent);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleDivTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_multipleDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);
        Border[] borders = {new SolidBorder(ColorConstants.BLUE, 2f), new SolidBorder(ColorConstants.RED, 2f), new SolidBorder(ColorConstants.GREEN, 2f)};

        Div externalDiv = new Div().setPadding(2f).setBorder(borders[2]);
        Div curr = externalDiv;
        for (int i = 0; i < 100; ++i) {
            Div d = new Div().setBorder(borders[i%3]);
            curr.add(d);
            curr = d;
        }

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(ColorConstants.BLACK, 2)).setMargin(3).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        curr.add(p);
        MinMaxWidth result = ((AbstractRenderer)externalDiv.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth();
        externalDiv.setWidth(toEffectiveWidth(externalDiv, result.getMinWidth()));
        doc.add(externalDiv);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)})
    public void simpleTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "simpleTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_simpleTableTest.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell cell1 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell2 = new Cell().add(new Paragraph("I am table"))
                .setBorder(new SolidBorder(ColorConstants.YELLOW, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        Table minTable = new Table(new float[] {-1, -1})
                .setWidth(UnitValue.createPointValue(1))
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        Table maxTable = new Table(new float[] {-1, -1})
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)})
    public void colspanTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "colspanTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_colspanTableTest.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell bigCell = new Cell(1, 2).add(new Paragraph("I am veryveryvery big cell"))
                .setBorder(new SolidBorder(ColorConstants.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add(new Paragraph("I am cell"))
                .setBorder(new SolidBorder(ColorConstants.YELLOW, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        Table minTable = new Table(new float[] {-1, -1, -1})
                .setWidth(UnitValue.createPointValue(1))
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        Table maxTable = new Table(new float[] {-1, -1, -1})
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)})
    public void colspanRowspanTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "colspanRowspanTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_colspanRowspanTableTest.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell colspanCell = new Cell(1, 2).add(new Paragraph("I am veryveryvery big cell"))
                .setBorder(new SolidBorder(ColorConstants.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell rowspanCell = new Cell(2, 1).add(new Paragraph("I am very very very long cell"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add(new Paragraph("I am cell"))
                .setBorder(new SolidBorder(ColorConstants.BLUE, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        Table minTable = new Table(new float[] {-1, -1, -1})
                .setWidth(UnitValue.createPointValue(1))
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        Table maxTable = new Table(new float[] {-1, -1, -1})
                .setMarginTop(10).setBorder(new SolidBorder(ColorConstants.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)})
    public void headerFooterTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "headerFooterTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_headerFooterTableTest.pdf";

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell bigCell = new Cell().add(new Paragraph("veryveryveryvery big cell"))
                .setBorder(new SolidBorder(ColorConstants.RED, 40))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell mediumCell = new Cell().add(new Paragraph("mediumsize cell"))
                .setBorder(new SolidBorder(ColorConstants.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add(new Paragraph("cell"))
                .setBorder(new SolidBorder(ColorConstants.BLUE, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.BLACK, 20))
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        TableRenderer renderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();

        Table minTable = new Table(new float[] {-1, -1, -1})
                .setWidth(UnitValue.createPointValue(1))
                .setBorder(new SolidBorder(ColorConstants.BLACK, 20)).setMarginTop(20)
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        Table maxTable = new Table(new float[] {-1, -1, -1})
                .setBorder(new SolidBorder(ColorConstants.BLACK, 20)).setMarginTop(20)
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static float toEffectiveWidth(IBlockElement b, float fullWidth) {
        if (b instanceof Table) {
            return fullWidth + ((Table) b).getNumberOfColumns() * MinMaxWidthUtils.getEps();
        } else {
            return fullWidth - MinMaxWidthUtils.getBorderWidth(b) - MinMaxWidthUtils.getMarginsWidth(b)
                             - MinMaxWidthUtils.getPaddingWidth(b) + MinMaxWidthUtils.getEps();
        }
    }

    private static float[] toEffectiveTableColumnWidth(float[] tableColumnWidth) {
        float[] result = new float[tableColumnWidth.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = tableColumnWidth[i] + MinMaxWidthUtils.getEps();
        }
        return result;
    }
}
