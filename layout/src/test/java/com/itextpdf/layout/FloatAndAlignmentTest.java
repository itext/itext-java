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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.FlexContainerRenderer;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileNotFoundException;
import java.io.IOException;

@Tag("IntegrationTest")
public class FloatAndAlignmentTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatAndAlignmentTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatAndAlignmentTest/";

    private static String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
            "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
            "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
            "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
            "qui officia deserunt mollit anim id est laborum.";


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }


    @Test
    public void blocksInsideDiv() throws IOException, InterruptedException {
    /* this test shows different combinations of 3 float values blocks  within divParent containers
    */
        String testName = "blocksInsideDiv";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter( outFileName ) );
        pdfDocument.setTagged();
        Document document = new Document( pdfDocument );

        Div div1 = createDiv(ColorConstants.RED, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPercentValue(80));
        Div div2 = createDiv(ColorConstants.BLUE, HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPercentValue(80));
        Div div3 = createDiv(ColorConstants.GREEN, HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPercentValue(80));

        Div divParent1 = createParentDiv(HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, UnitValue.createPercentValue(80));
        divParent1.add( div3 );
        divParent1.add( div2 );
        divParent1.add( div1 );
        document.add( divParent1 );

        Div divParent2 = createParentDiv(HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(80));
        divParent2.add( div2 );
        divParent2.add( div1 );
        divParent2.add( div3 );
        document.add( divParent2 );

        Div divParent3 = createParentDiv(HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(80));

        divParent3.add( div1 );
        divParent3.add( div2 );
        divParent3.add( div3 );
        document.add( divParent3 );

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

    @Test
    public void blocksInsideDivFloat() throws IOException, InterruptedException {
    /* this test shows different combinations of 3 float values blocks  within divParent containers
    */
        String testName = "blocksInsideDivFloat";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter( outFileName ) );
        pdfDocument.setTagged();
        Document document = new Document( pdfDocument );

        Div div1 = createDiv(ColorConstants.RED, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPercentValue(80));
        Div div2 = createDiv(ColorConstants.BLUE, HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, FloatPropertyValue.RIGHT, UnitValue.createPercentValue(80));
        Div div3 = createDiv(ColorConstants.GREEN, HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, FloatPropertyValue.LEFT, UnitValue.createPercentValue(80));

        Div divParent1 = createParentDiv(HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, UnitValue.createPercentValue(75));
        divParent1.add( div3 );
        divParent1.add( div2 );
        divParent1.add( div1 );
        document.add( divParent1 );

        Div divParent2 = createParentDiv(HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(75));
        divParent2.add( div2 );
        divParent2.add( div1 );
        divParent2.add( div3 );
        document.add( divParent2 );

        Div divParent3 = createParentDiv(HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(75));

        divParent3.add( div1 );
        divParent3.add( div2 );
        divParent3.add( div3 );
        document.add( divParent3 );

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

    @Test
    public void blocksInsideEachOther() throws IOException, InterruptedException {
    /* this test shows different combinations of float blocks  inside each other
    */
        String testName = "blocksInsideEachOther";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter( outFileName ) );
        pdfDocument.setTagged();
        Document document = new Document( pdfDocument );

        Div divRed = createDiv(ColorConstants.RED, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPercentValue(80));
        Div divBlue = createDiv(ColorConstants.BLUE, HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, FloatPropertyValue.RIGHT, UnitValue.createPercentValue(80));
        Div divGreen = createDiv(ColorConstants.GREEN, HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, FloatPropertyValue.LEFT, UnitValue.createPercentValue(80));
        Div divYellow = createDiv(ColorConstants.YELLOW, HorizontalAlignment.RIGHT, ClearPropertyValue.NONE, FloatPropertyValue.RIGHT, UnitValue.createPercentValue(80));
        Div divOrange = createDiv(ColorConstants.ORANGE, HorizontalAlignment.LEFT, ClearPropertyValue.NONE, FloatPropertyValue.LEFT, UnitValue.createPercentValue(80));

        Div divParent1 = createParentDiv(HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, UnitValue.createPercentValue(85));
        divParent1.add( divRed );
        divRed.add( divBlue );
        divBlue.add( divGreen );
        document.add( divParent1 );

        Div divParent2 = createParentDiv(HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(85));
        divParent2.add( divYellow );
        divYellow.add( divRed );
        document.add( divParent2 );

        Div divParent3 = createParentDiv(HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(85));
        divParent3.add( divOrange );
        divOrange.add( divYellow );
        document.add( divParent3 );

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff02_"));
    }

    @Test
    public void blocksInsideEachOther_sameFixedWidthsNesting() throws IOException, InterruptedException {
    /* this test shows different combinations of float blocks inside each other with blocks nested inside each other that have the same fixed width
    */

        String testName = "blocksInsideEachOther_sameFixedWidthsNesting";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter( outFileName ) );
        pdfDocument.setTagged();
        Document document = new Document( pdfDocument );

        Div divRed = createDiv(ColorConstants.RED, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, FloatPropertyValue.NONE, UnitValue.createPointValue(300));
        Div divBlue = createDiv(ColorConstants.BLUE, HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, FloatPropertyValue.RIGHT, UnitValue.createPointValue(300));
        Div divGreen = createDiv(ColorConstants.GREEN, HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, FloatPropertyValue.LEFT, UnitValue.createPointValue(300));
        Div divYellow = createDiv(ColorConstants.YELLOW, HorizontalAlignment.RIGHT, ClearPropertyValue.NONE, FloatPropertyValue.RIGHT, UnitValue.createPointValue(300));
        Div divOrange = createDiv(ColorConstants.ORANGE, HorizontalAlignment.LEFT, ClearPropertyValue.NONE, FloatPropertyValue.LEFT, UnitValue.createPointValue(300));

        Div divParent1 = createParentDiv(HorizontalAlignment.CENTER, ClearPropertyValue.BOTH, UnitValue.createPercentValue(70));
        divParent1.add( divRed );
        divRed.add( divBlue );
        divBlue.add( divGreen );
        document.add( divParent1 );

        Div divParent2 = createParentDiv(HorizontalAlignment.LEFT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(70));
        divParent2.add( divYellow );
        divYellow.add( divRed );
        document.add( divParent2 );

        Div divParent3 = createParentDiv(HorizontalAlignment.RIGHT, ClearPropertyValue.BOTH, UnitValue.createPercentValue(70));
        divParent3.add( divOrange );
        divOrange.add( divYellow );
        document.add( divParent3 );

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff02_sameFixedWidth_"));
    }


    @Test
    public void blocksNotInDivCenter() throws IOException, InterruptedException {
    /* this test shows different combinations of 3 float values blocks
     * NOTE, that div1 text is partly overlapped
    */
        String testName = "blocksNotInDivCenter";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        createDocumentWithBlocks( outFileName, HorizontalAlignment.CENTER );
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff03_"));
    }

    @Test
    public void blocksNotInDivLeft() throws IOException, InterruptedException {
    /* this test shows different combinations of 3 float values blocks
     * NOTE, that div1 text is partly overlapped
    */
        String testName = "blocksNotInDivLeft";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        createDocumentWithBlocks( outFileName, HorizontalAlignment.LEFT );
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff04_"));
    }

    @Test
    public void blocksNotInDivRight() throws IOException, InterruptedException {
    /* this test shows different combinations of 3 float values blocks
     * NOTE, that div1 text is partly overlapped
     */
        String testName = "blocksNotInDivRight";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";

    /*
     * Please, NOTE: in current example HorizontalAlignment values are ignored, if FloatPropertyValue !=NONE
     * So, only FloatPropertyValue defines the position of element in such cases
     */
        createDocumentWithBlocks( outFileName, HorizontalAlignment.RIGHT );
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff05_"));
    }

    private void createDocumentWithBlocks(String outFileName, HorizontalAlignment horizontalAlignment)
            throws IOException {
        PdfDocument pdfDocument = new PdfDocument( new PdfWriter( outFileName ) );
        pdfDocument.setTagged();
        Document document = new Document( pdfDocument );

        Div divRed = createDiv( ColorConstants.RED, horizontalAlignment, ClearPropertyValue.NONE, FloatPropertyValue.NONE, UnitValue.createPointValue(300));
        Div divBlue = createDiv(ColorConstants.BLUE, HorizontalAlignment.LEFT, ClearPropertyValue.NONE, FloatPropertyValue.RIGHT, UnitValue.createPointValue(300));
        Div divGreen = createDiv(ColorConstants.GREEN, HorizontalAlignment.RIGHT, ClearPropertyValue.NONE, FloatPropertyValue.LEFT, UnitValue.createPointValue(300));
        Div divYellow = createDiv(ColorConstants.YELLOW, HorizontalAlignment.RIGHT, ClearPropertyValue.NONE, FloatPropertyValue.RIGHT, UnitValue.createPointValue(300));
        Div divOrange = createDiv(ColorConstants.ORANGE, HorizontalAlignment.LEFT, ClearPropertyValue.NONE, FloatPropertyValue.LEFT, UnitValue.createPointValue(300));

        document.add( divOrange );
        document.add( divYellow );
        document.add( divGreen );
        document.add( divBlue );
        document.add( divRed );

        document.add( divOrange );
        document.add( divYellow );
        document.add( divGreen );
        document.add( divBlue );
        document.add( divRed );

        document.add( divRed );

        document.add( divRed );

        document.close();
    }

    @Test
    public void inlineBlocksAndFloatsWithTextAlignmentTest01() throws IOException, InterruptedException {
        String testName = "inlineBlocksAndFloatsWithTextAlignmentTest01";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        Paragraph parentPara = new Paragraph().setTextAlignment(TextAlignment.RIGHT);
        Div floatingDiv = new Div();
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        parentPara
                .add("Text begin")
                .add(new Div()
                            .add(new Paragraph("div text").setBorder(new SolidBorder(2))))
                .add("More text")
                .add(floatingDiv
                            .add(new Paragraph("floating div text")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));

        document.add(parentPara);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign01_"));
    }

    @Test
    public void inlineBlocksAndFloatsWithTextAlignmentTest02() throws IOException, InterruptedException {
        String testName = "inlineBlocksAndFloatsWithTextAlignmentTest02";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        Paragraph parentPara = new Paragraph().setTextAlignment(TextAlignment.JUSTIFIED);
        Div floatingDiv = new Div();
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        parentPara
                .add("Text begin")
                .add(new Div()
                            .add(new Paragraph("div text").setBorder(new SolidBorder(2))))
                .add(floatingDiv
                        .add(new Paragraph("floating div text")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)))
                .add("MoretextMoretextMoretext. MoretextMoretextMoretext. MoretextMoretextMoretext. MoretextMoretextMoretext. MoretextMoretextMoretext. ");

        document.add(parentPara.setBorder(new DashedBorder(2)));

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign02_"));
    }

    @Test
    public void inlineBlocksAndFloatsWithTextAlignmentTest03() throws IOException, InterruptedException {
        String testName = "inlineBlocksAndFloatsWithTextAlignmentTest03";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        // making an inline float a last element in the line

        Paragraph parentPara = new Paragraph().setTextAlignment(TextAlignment.JUSTIFIED);
        Div floatingDiv = new Div();
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        parentPara
                .add("Text begin")
                .add(new Div()
                            .add(new Paragraph("div text").setBorder(new SolidBorder(2))))
                .add("MoretextMoretextMoretext. MoretextMoretextMoretext. MoretextMoretextMoretext. MoretextMoretextMoretext. ")
                .add(floatingDiv
                        .add(new Paragraph("floating div text")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)))
                .add("MoretextMoretextMoretext.");

        document.add(parentPara.setBorder(new DashedBorder(2)));

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign03_"));
    }

    @Test
    public void inlineBlocksAndFloatsWithTextAlignmentTest04() throws IOException, InterruptedException {
        String testName = "inlineBlocksAndFloatsWithTextAlignmentTest04";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        try(PdfWriter writer = new PdfWriter(outFileName)) {
            try (PdfDocument pdfDocument = new PdfDocument(writer)) {
                pdfDocument.setDefaultPageSize(PageSize.A5);
                try (Document document = new Document(pdfDocument)) {

                    Table table2 = new Table(1)
                            .setWidth(150f)
                            .setBorder(new SolidBorder(1f))
                            .setMargin(5f)
                            .setHorizontalAlignment(HorizontalAlignment.LEFT)
                            .addCell(new Cell()
                                    .add(new Paragraph(text.substring(0, text.length() / 2))));
                    table2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
                    document.add(table2);
                    document.add(new Paragraph(text)
                            .setTextAlignment(TextAlignment.JUSTIFIED));
                    Table table3 = new Table(1)
                            .setWidth(150f)
                            .setBorder(new SolidBorder(1f))
                            .setMargin(5f)
                            .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                            .addCell(new Cell()
                                    .add(new Paragraph(text.substring(0, text.length() / 2))));
                    table3.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
                    document.add(table3);
                    document.add(new Paragraph(text)
                            .setTextAlignment(TextAlignment.JUSTIFIED));
                }
            }
        } catch(Exception ex){}
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign04_"));
    }

    @Test
    public void floatsOnlyJustificationTest01() throws IOException, InterruptedException {
        String testName = "floatsOnlyJustificationTest01";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        Paragraph parentPara = new Paragraph().setTextAlignment(TextAlignment.JUSTIFIED);
        Div floatingDiv = new Div();
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        parentPara
                .add(floatingDiv
                        .add(new Paragraph("floating div text")).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));

        document.add(parentPara.setBorder(new DashedBorder(2)));

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithAlignmentNextToRightFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithAlignmentNextToRightFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithAlignmentNextToRightFloat.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT);
        Table table2 = createTable(HorizontalAlignment.LEFT);
        Table table3 = createTable(HorizontalAlignment.CENTER);

        Div div = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.RIGHT, UnitValue.createPointValue(200));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithAlignmentNextToLeftFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithAlignmentNextToLeftFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithAlignmentNextToLeftFloat.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT);
        Table table2 = createTable(HorizontalAlignment.LEFT);
        Table table3 = createTable(HorizontalAlignment.CENTER);

        Div div = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.LEFT, UnitValue.createPointValue(200));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithAlignmentBetweenFloatsTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithAlignmentBetweenFloats.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithAlignmentBetweenFloats.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT).setWidth(250);
        Table table2 = createTable(HorizontalAlignment.LEFT).setWidth(250);
        Table table3 = createTable(HorizontalAlignment.CENTER).setWidth(250);

        Div div1 = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.LEFT, UnitValue.createPointValue(100));

        Div div2 = createDiv(ColorConstants.BLUE, ClearPropertyValue.NONE,
                FloatPropertyValue.RIGHT, UnitValue.createPointValue(100));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div1);
        document.add(div2);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div1);
        document.add(div2);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div1);
        document.add(div2);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithBigLeftMarginAfterFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithBigLeftMarginAfterFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithBigLeftMarginAfterFloat.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT);
        table1.setMarginLeft(300);
        Table table2 = createTable(HorizontalAlignment.LEFT);
        table2.setMarginLeft(300);
        Table table3 = createTable(HorizontalAlignment.CENTER);
        table3.setMarginLeft(300);

        Div div = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.RIGHT, UnitValue.createPointValue(200));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithBigRightMarginAfterFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithBigRightMarginAfterFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithBigRightMarginAfterFloat.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT);
        table1.setMarginRight(300);
        Table table2 = createTable(HorizontalAlignment.LEFT);
        table2.setMarginRight(300);
        Table table3 = createTable(HorizontalAlignment.CENTER);
        table3.setMarginRight(300);

        Div div = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.LEFT, UnitValue.createPointValue(200));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    //TODO DEVSIX-4021 update cmp file after fix
    public void tableWithSideMarginsBetweenFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableWithSideMarginsBetweenFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_tableWithSideMarginsBetweenFloat.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Table table1 = createTable(HorizontalAlignment.RIGHT);
        table1.setMarginRight(150)
              .setMarginLeft(150);
        Table table2 = createTable(HorizontalAlignment.LEFT);
        table2.setMarginRight(300);
        Table table3 = createTable(HorizontalAlignment.CENTER);
        table3.setMarginLeft(300);

        Div div1 = createDiv(ColorConstants.GREEN, ClearPropertyValue.NONE,
                FloatPropertyValue.LEFT, UnitValue.createPointValue(100));
        Div div2 = createDiv(ColorConstants.BLUE, ClearPropertyValue.NONE,
                FloatPropertyValue.RIGHT, UnitValue.createPointValue(100));

        Div spaceDiv = new Div();
        spaceDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        spaceDiv.add(new Paragraph("Space Div").setFontColor(ColorConstants.BLUE));

        document.add(div1);
        document.add(div2);
        document.add(table1);
        document.add(spaceDiv);

        document.add(div1);
        document.add(div2);
        document.add(table2);
        document.add(spaceDiv);

        document.add(div1);
        document.add(div2);
        document.add(table3);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void floatPositioningOutsideBlocksTest() throws IOException, InterruptedException {
        String testName = "floatPositioningOutsideBlocks";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div floatLeft = new Div()
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 3))
                    .setBackgroundColor(ColorConstants.GREEN, 0.3f)
                    .setWidth(100).setHeight(100);
            floatLeft.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            floatLeft.add(new Paragraph("float left"));

            Div floatRight = new Div()
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 3))
                    .setBackgroundColor(ColorConstants.YELLOW, 0.3f)
                    .setWidth(100).setHeight(100);
            floatRight.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            floatRight.add(new Paragraph("float right"));

            Div divWithBfc = new Div()
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 3))
                    .setBackgroundColor(ColorConstants.BLUE, 0.3f)
                    .setHeight(100);
            divWithBfc.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
            divWithBfc.add(new Paragraph("div with own block formatting context"));

            Div wideDivWithBfc = new Div()
                    .setBorder(new SolidBorder(ColorConstants.CYAN, 3))
                    .setBackgroundColor(ColorConstants.CYAN, 0.3f)
                    .setWidth(UnitValue.createPercentValue(100));
            wideDivWithBfc.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
            wideDivWithBfc.add(new Paragraph("wide div with own block formatting context"));

            Div divWithoutBfc = new Div()
                    .setBorder(new SolidBorder(ColorConstants.PINK, 3))
                    .setBackgroundColor(ColorConstants.PINK, 0.3f)
                    .setHeight(100);
            divWithoutBfc.add(new Paragraph("div without own block formatting context"));

            document.add(floatLeft);
            document.add(divWithBfc);
            document.add(floatRight);
            document.add(divWithBfc);
            document.add(floatLeft);
            document.add(floatRight);
            document.add(divWithBfc);
            document.add(floatLeft);
            document.add(floatRight);
            document.add(divWithoutBfc);
            document.add(floatLeft);
            document.add(floatRight);
            document.add(wideDivWithBfc);
            document.add(new Paragraph("Plain text after wide div"));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

    @Test
    public void floatPositioningOutsideFlexContainerTest() throws IOException, InterruptedException {
        String testName = "floatPositioningOutsideFlexContainer";
        String outFileName = destinationFolder + testName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + testName + ".pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div floatLeft = new Div()
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 1))
                    .setBackgroundColor(ColorConstants.GREEN, 0.3f)
                    .setWidth(100).setHeight(100);
            floatLeft.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            floatLeft.add(new Paragraph("float left"));

            Div floatRight = new Div()
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 1))
                    .setBackgroundColor(ColorConstants.YELLOW, 0.3f)
                    .setWidth(100).setHeight(100);
            floatRight.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            floatRight.add(new Paragraph("float right"));

            Div flexContainer = new Div()
                    .setBorder(new SolidBorder(ColorConstants.BLUE, 1))
                    .setBackgroundColor(ColorConstants.BLUE, 0.3f);
            flexContainer.setNextRenderer(new FlexContainerRenderer(flexContainer));
            flexContainer.add(new Paragraph("flex container"));

            Div flexContainer2 = new Div()
                    .setBorder(new SolidBorder(ColorConstants.PINK, 1))
                    .setBackgroundColor(ColorConstants.PINK, 0.1f)
                    .setWidth(UnitValue.createPercentValue(100));
            flexContainer2.setNextRenderer(new FlexContainerRenderer(flexContainer2));
            flexContainer2.add(new Paragraph("flex container with 100% width"));

            document.add(flexContainer);
            document.add(floatLeft);
            document.add(floatRight);
            document.add(flexContainer);
            document.add(floatLeft);
            document.add(floatRight);
            document.add(flexContainer2);
            document.add(new Paragraph("Plain text after wide flex container"));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

    private Div createParentDiv(HorizontalAlignment horizontalAlignment, ClearPropertyValue clearPropertyValue, UnitValue width) {
        Div divParent1 = new Div()
                .setBorder(new SolidBorder(5) )
                .setWidth( width );
        divParent1.setHorizontalAlignment( horizontalAlignment);
        divParent1.setProperty( Property.CLEAR, clearPropertyValue);
        divParent1.add( new Paragraph( "Div with HorizontalAlignment."
                +horizontalAlignment+", ClearPropertyValue."+ clearPropertyValue ) );
        return divParent1;
    }

    private static Div createDiv(Color color, HorizontalAlignment horizontalAlignment, ClearPropertyValue clearPropertyValue,
                                 FloatPropertyValue floatPropertyValue, UnitValue width) {
        Div div = new Div()
                .setBorder(new SolidBorder( color, 1) )
                .setBackgroundColor(color, 0.3f)
                .setMargins(10, 10, 10, 10)
                .setWidth(width);
        div.setHorizontalAlignment( horizontalAlignment);
        div.setProperty( Property.CLEAR, clearPropertyValue);
        div.setProperty(Property.FLOAT, floatPropertyValue);
        String cont = "Div with HorizontalAlignment."+ horizontalAlignment
                +", ClearPropertyValue." +clearPropertyValue
                + ", FloatPropertyValue." +floatPropertyValue;
        div.add( new Paragraph(cont) );
        return div;
    }

    private static Div createDiv(Color color, ClearPropertyValue clearPropertyValue,
            FloatPropertyValue floatPropertyValue, UnitValue width) {
        Div div = new Div()
                .setBorder(new SolidBorder( color, 1) )
                .setBackgroundColor(color, 0.3f)
                .setMargins(10, 10, 10, 10)
                .setWidth(width);
        div.setProperty( Property.CLEAR, clearPropertyValue);
        div.setProperty(Property.FLOAT, floatPropertyValue);
        String cont = "Div with ClearPropertyValue." +clearPropertyValue
                + ", FloatPropertyValue." +floatPropertyValue;
        div.add( new Paragraph(cont) );
        return div;
    }

    private static Table createTable(HorizontalAlignment horizontalAlignment) {
        Table table = new Table(3);
        table.addCell("Align" + horizontalAlignment.toString());
        table.addCell("Cell number two");
        table.addCell("Cell number three");
        table.setHorizontalAlignment(horizontalAlignment);

        return table;
    }


}
