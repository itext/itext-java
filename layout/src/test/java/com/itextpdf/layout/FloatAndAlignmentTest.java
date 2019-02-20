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
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class FloatAndAlignmentTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatAndAlignmentTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatAndAlignmentTest/";

    private static String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
            "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
            "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
            "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
            "qui officia deserunt mollit anim id est laborum.";


    @BeforeClass
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff02_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff02_sameFixedWidth_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff03_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff04_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff05_"));
    }

    private void createDocumentWithBlocks(String outFileName, HorizontalAlignment horizontalAlignment) throws FileNotFoundException {
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign01_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign02_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign03_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diffTextAlign04_"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff_"));
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
        div.add( new Paragraph( "Div with HorizontalAlignment."+ horizontalAlignment
                +", ClearPropertyValue." +clearPropertyValue
                + ", FloatPropertyValue." +floatPropertyValue) );
        return div;
    }


}
