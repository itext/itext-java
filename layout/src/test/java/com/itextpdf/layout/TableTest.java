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

import com.itextpdf.commons.utils.PlaceHolderTextUtil;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.RoundDotsBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.CaptionSide;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class TableTest extends AbstractTableTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TableTest/";

    private static final String TEXT_CONTENT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
    private static final String SHORT_TEXT_CONTENT = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
    private static final String MIDDLE_TEXT_CONTENT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleTableTest01() throws IOException, InterruptedException {
        String testName = "tableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
        Assertions.assertEquals("Cell[row=0, col=0, rowspan=1, colspan=1]", table.getCell(0, 0).toString());
    }

    @Test
    public void simpleTableTest02() throws IOException, InterruptedException {
        String testName = "tableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")))
                .addCell(new Cell().add(new Paragraph("cell 3, 1")))
                .addCell(new Cell());
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest03() throws IOException, InterruptedException {
        String testName = "tableTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent1 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n";

        String textContent2 = "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n" +
                "Aenean nec lorem. In porttitor. Donec laoreet nonummy augue.\n" +
                "Suspendisse dui purus, scelerisque at, vulputate vitae, pretium mattis, nunc. Mauris eget neque at sem venenatis eleifend. Ut nonummy.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent1)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent1 + textContent2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent2 + textContent1)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent2)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest04() throws IOException, InterruptedException {
        String testName = "tableTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)));
        table.addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + TEXT_CONTENT + TEXT_CONTENT + TEXT_CONTENT)));
        table.addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)));
        table.addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest05() throws IOException, InterruptedException {
        String testName = "tableTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest06() throws IOException, InterruptedException {
        String testName = "tableTest06.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest07() throws IOException, InterruptedException {
        String testName = "tableTest07.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest08() throws IOException, InterruptedException {
        String testName = "tableTest08.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest09() throws IOException, InterruptedException {
        String testName = "tableTest09.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 2:2, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest10() throws IOException, InterruptedException {
        String testName = "tableTest10.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("Table 1"));
        Table table = new Table(new float[]{100, 100})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(new Paragraph("1, 2")))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table);

        doc.add(new Paragraph("Table 2"));

        Table table2 = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(new Paragraph("1, 2")))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table2);

        doc.add(new Paragraph("Table 3"));

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image image = new Image(xObject, 50);

        Table table3 = new Table(new float[]{100, 100})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(image))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table3);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest11() throws IOException, InterruptedException {
        String testName = "tableTest11.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 2\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest12() throws IOException, InterruptedException {
        String testName = "tableTest12.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 8, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 8, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 9, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 9, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 10, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 10, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 11, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 11, 2\n" + shortTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest13() throws IOException, InterruptedException {
        String testName = "tableTest13.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest14() throws IOException, InterruptedException {
        String testName = "tableTest14.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest15() throws IOException, InterruptedException {
        String testName = "tableTest15.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 3:2, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest16() throws IOException, InterruptedException {
        String testName = "tableTest16.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. " + TEXT_CONTENT
                + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. " + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + middleTextContent)).setBorder(new SolidBorder(ColorConstants.RED, 2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void wideFirstCellBorderDoesntAffectSecondCellTest() throws IOException, InterruptedException {
        String testName = "wideFirstCellBorderDoesntAffectSecondCellTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. "
                + TEXT_CONTENT + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. "
                + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[] {250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")).setBorder(new SolidBorder(ColorConstants.RED, 100)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest17() throws IOException, InterruptedException {
        String testName = "tableTest17.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")));

        String longText = "Long text, very long text. ";
        for (int i = 0; i < 4; i++) {
            longText += longText;
        }
        table.addCell(new Cell().add(new Paragraph("cell 2.1\n" + longText).setKeepTogether(true)));
        table.addCell("cell 2.2\nShort text.");
        table.addCell(new Cell().add(new Paragraph("cell 2.3\n" + longText)));

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest18() throws IOException, InterruptedException {
        String testName = "tableTest18.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(TEXT_CONTENT));

        Table table = new Table(new float[]{50, 50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")));

        String longText = "Long text, very long text. ";
        for (int i = 0; i < 4; i++) {
            longText += longText;
        }
        table.addCell(new Cell().add(new Paragraph("cell 2.1\n" + longText).setKeepTogether(true)));
        table.addCell("cell 2.2\nShort text.");
        table.addCell(new Cell().add(new Paragraph("cell 2.3\n" + longText)));

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest19() throws IOException, InterruptedException {
        String testName = "tableTest19.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + MIDDLE_TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest20() throws IOException, InterruptedException {
        String testName = "tableTest20.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + SHORT_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + MIDDLE_TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + MIDDLE_TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest21() throws IOException, InterruptedException {
        String testName = "tableTest21.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        doc.add(new Paragraph(TEXT_CONTENT));

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest22() throws IOException, InterruptedException {
        String testName = "tableTest22.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new UnitValue[]{UnitValue.createPointValue(30), UnitValue.createPointValue(30), UnitValue.createPercentValue(30), UnitValue.createPercentValue(30)})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest23() throws IOException, InterruptedException {
        String testName = "tableTest23.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(2)
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("longer cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")));
        doc.add(table);

        table = new Table(2).setFixedLayout()
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("longer cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")));
        doc.add(table);

        table = new Table(2, true)
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("longer cell 1, 2")));
        doc.add(table);
        table
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")))
                .flush();
        table.complete();

        table = new Table(2, true).setFixedLayout()
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("longer cell 1, 2")));
        doc.add(table);
        table
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")))
                .flush();
        table.complete();

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void widthInPercentShouldBeResetAfterOverflow() throws IOException, InterruptedException {
        String testName = "widthInPercentShouldBeResetAfterOverflow.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Div().setHeight(730).setWidth(523));
        Table table = new Table(2).useAllAvailableWidth().setFixedLayout()
                .addCell(new Cell().add(new Paragraph("Hello")).setWidth(UnitValue.createPercentValue(20)))
                .addCell(new Cell().add(new Paragraph("World")).setWidth(UnitValue.createPercentValue(80)));
        // will be added on the first page
        doc.add(table);

        // will be added on the second page
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest01() throws IOException, InterruptedException {
        String testName = "bigRowspanTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. " + TEXT_CONTENT
                + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. " + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest02() throws IOException, InterruptedException {
        String testName = "bigRowspanTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. " + TEXT_CONTENT
                + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. " + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest03() throws IOException, InterruptedException {
        String testName = "bigRowspanTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest04() throws IOException, InterruptedException {
        String testName = "bigRowspanTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. " + TEXT_CONTENT
                + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. " + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 4, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + TEXT_CONTENT)));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest05() throws IOException, InterruptedException {
        String testName = "bigRowspanTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String longTextContent = "1. " + TEXT_CONTENT + "2. " + TEXT_CONTENT + "3. " + TEXT_CONTENT + "4. " + TEXT_CONTENT
                + "5. " + TEXT_CONTENT + "6. " + TEXT_CONTENT + "7. " + TEXT_CONTENT + "8. " + TEXT_CONTENT + "9. " + TEXT_CONTENT;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell(2, 1).add(new Paragraph("cell 1, 1 and 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + TEXT_CONTENT)));

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest06() throws IOException, InterruptedException {
        String testName = "bigRowspanTest06.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
                .addCell(new Cell(2, 1).add(new Paragraph("col 1 row 2")))
                .addCell(new Cell(2, 1).add(new Paragraph("col 2 row 2")))
                .addCell(new Cell(1, 1).add(new Paragraph("col 1 row 3")))
                .addCell(new Cell(1, 1).add(new Paragraph("col 2 row 3")));

        table.setBorderTop(new SolidBorder(ColorConstants.GREEN, 50)).setBorderBottom(new SolidBorder(ColorConstants.ORANGE, 40));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest07() throws IOException, InterruptedException {
        String testName = "bigRowspanTest07.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        for (int i = 0; i < 100; i++) {
            Cell cell = new Cell();
            cell.add(new Paragraph("Cell " + i));

            Cell cell2 = new Cell(2, 1);
            cell2.add(new Paragraph("Cell with Rowspan"));

            Cell cell3 = new Cell();
            cell3.add(new Paragraph("Cell " + i + ".2"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
        }

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void differentPageOrientationTest01() throws IOException, InterruptedException {
        String testName = "differentPageOrientationTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        final PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent1 = "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.";
        String textContent2 = "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.";
        String textContent3 = "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.";

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        for (int i = 0; i < 20; i++) {
            table.addCell(new Cell().add(new Paragraph(textContent1)))
                    .addCell(new Cell().add(new Paragraph(textContent3)))
                    .addCell(new Cell().add(new Paragraph(textContent2)))

                    .addCell(new Cell().add(new Paragraph(textContent3)))
                    .addCell(new Cell().add(new Paragraph(textContent2)))
                    .addCell(new Cell().add(new Paragraph(textContent1)))

                    .addCell(new Cell().add(new Paragraph(textContent2)))
                    .addCell(new Cell().add(new Paragraph(textContent1)))
                    .addCell(new Cell().add(new Paragraph(textContent3)));
        }
        doc.setRenderer(new RotatedDocumentRenderer(doc, pdfDoc));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void extendLastRowTest01() throws IOException, InterruptedException {
        String testName = "extendLastRowTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image image = new Image(xObject, 100);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        for (int i = 0; i < 20; i++) {
            table.addCell(image);
        }
        doc.add(new Paragraph("Extend the last row on each page"));
        table.setExtendBottomRow(true);
        doc.add(table);
        doc.add(new Paragraph("Extend all last rows on each page except final one"));
        table.setExtendBottomRow(false);
        table.setExtendBottomRowOnSplit(true);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    @Test
    public void toLargeElementWithKeepTogetherPropertyInTableTest01() throws IOException, InterruptedException {
        String testName = "toLargeElementWithKeepTogetherPropertyInTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        Cell cell = new Cell();
        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String result = "";
        for (int i = 0; i < 53; i++) {
            result += str;
        }
        Paragraph p = new Paragraph(new Text(result));
        p.setProperty(Property.KEEP_TOGETHER, true);
        cell.add(p);
        table.addCell(cell);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })

    @Test
    public void toLargeElementInTableTest01() throws IOException, InterruptedException {
        String testName = "toLargeElementInTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "toLargeElementInTableTest01.pdf"));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{5});
        table.setWidth(5).setProperty(Property.TABLE_LAYOUT, "fixed");
        Cell cell = new Cell();
        Paragraph p = new Paragraph(new Text("a"));
        cell.add(p);
        table.addCell(cell);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void nestedTablesCollapseTest01() throws IOException, InterruptedException {
        String testName = "nestedTablesCollapseTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Cell cell;
        Table outertable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Table innertable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        // first row
        // column 1
        cell = new Cell().add(new Paragraph("Record Ref:"));
        cell.setBorder(Border.NO_BORDER);
        innertable.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("GN Staff"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // spacing
        cell = new Cell(1, 2);
        cell.setHeight(3);
        cell.setBorder(Border.NO_BORDER);
        innertable.addCell(cell);
        // second row
        // column 1
        cell = new Cell().add(new Paragraph("Hospital:"));
        cell.setBorder(Border.NO_BORDER);
        innertable.addCell(cell);
        // column 2
        cell = new Cell().add(new Paragraph("Derby Royal"));
        cell.setPaddingLeft(2);
        innertable.addCell(cell);
        // spacing
        cell = new Cell(1, 2);
        cell.setHeight(3);
        cell.setBorder(Border.NO_BORDER);
        innertable.addCell(cell);

        // first nested table
        cell = new Cell().add(innertable);
        outertable.addCell(cell);
        // add the table
        doc.add(outertable);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void nestedTableSkipHeaderFooterTest() throws IOException, InterruptedException {
        String testName = "nestedTableSkipHeaderFooter.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        table.addHeaderCell(new Cell(1, 5).
                add(new Paragraph("Table XYZ (Continued)")));
        table.addFooterCell(new Cell(1, 5).
                add(new Paragraph("Continue on next page")));
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);
        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
        }

        Table t = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        t.addCell(new Cell().
                setBorder(new SolidBorder(ColorConstants.RED, 1)).
                setPaddings(3, 3, 3, 3).
                add(table));

        doc.add(t);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void nestedTablesWithMarginsTest01() throws IOException, InterruptedException {
        String testName = "nestedTablesWithMarginsTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A8.rotate());

        Table innerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 4; i++) {
            innerTable.addCell(new Cell().add(new Paragraph("Hello" + i)));
        }

        Table outerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(innerTable));
        outerTable.setMarginTop(10);
        doc.add(outerTable);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    @Test
    public void splitTableOnShortPage() throws IOException, InterruptedException {
        String testName = "splitTableOnShortPage.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(300, 98));

        doc.add(new Paragraph("Table with setKeepTogether(true):"));
        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        table.setKeepTogether(true);
        Cell cell = new Cell(3, 1);
        cell.add(new Paragraph("G"));
        cell.add(new Paragraph("R"));
        cell.add(new Paragraph("P"));
        table.addCell(cell);
        table.addCell("middle row 1");
        cell = new Cell(3, 1);
        cell.add(new Paragraph("A"));
        cell.add(new Paragraph("B"));
        cell.add(new Paragraph("C"));
        table.addCell(cell);
        table.addCell("middle row 2");
        table.addCell("middle row 3");
        doc.add(table);

        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table with setKeepTogether(false):"));
        table.setKeepTogether(false);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void splitCellWithStyles() throws IOException, InterruptedException {
        String testName = "splitCellWithStyles.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        String text = "Make Gretzky Great Again";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A7);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
                .setBorder(Border.NO_BORDER)
                .setMarginTop(10)
                .setMarginBottom(10);
        Style cellStyle = new Style();
        cellStyle.setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(ColorConstants.BLUE, 1))
                .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 1));
        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph(Integer.toString(i))).addStyle(cellStyle));
            table.addCell(new Cell().add(new Paragraph(text)).addStyle(cellStyle));
        }

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void imageInTableTest_HA() throws IOException, InterruptedException {
        String testName = "imageInTableTest_HA.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image imageL = new Image(xObject);
        imageL.setHorizontalAlignment(HorizontalAlignment.LEFT);
        Image imageC = new Image(xObject);
        imageC.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Image imageR = new Image(xObject);
        imageR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        doc.add(new Paragraph("Table"));
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(imageL))
                .addCell(new Cell().add(imageC))
                .addCell(new Cell().add(imageR));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void cellAlignmentAndSplittingTest01() throws IOException, InterruptedException {
        String testName = "cellAlignmentAndSplittingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 20; i++) {
            table.addCell(new Cell().add(new Paragraph(i + " Liberté!\nÉgalité!\nFraternité!")).setHeight(100).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void cellAlignmentAndKeepTogetherTest01() throws IOException, InterruptedException {
        String testName = "cellAlignmentAndKeepTogetherTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 20; i++) {
            table.addCell(new Cell().add(new Paragraph(i + " Liberté!\nÉgalité!\nFraternité!")).setHeight(100).setKeepTogether(true).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 3)
    })
    @Test
    public void tableWithSetHeightProperties01() throws IOException, InterruptedException {
        String testName = "tableWithSetHeightProperties01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n";


        doc.add(new Paragraph("Default layout:"));

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setHeight(1700);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is shorter than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setHeight(200);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Some cells' heights are set:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)).setHeight(300))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)).setHeight(40))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)).setHeight(20));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setHeight(1700);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 3)
    })
    @Test
    public void tableWithSetHeightProperties02() throws IOException, InterruptedException {
        String testName = "tableWithSetHeightProperties02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n";


        doc.add(new Paragraph("Default layout:"));

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's max height is bigger than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setMaxHeight(1300);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's max height is shorter than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setMaxHeight(300);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's min height is bigger than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setMinHeight(1300);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's min height is shorter than needed:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setMinHeight(300);
        doc.add(table);
        doc.add(new AreaBreak());


        doc.add(new Paragraph("Some cells' heights are set:"));
        table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)).setMinHeight(300))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GREEN, 1)))
                .addCell(new Cell(1, 2).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.YELLOW, 3)).setMaxHeight(40))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.RED, 5)))
                .addCell(new Cell(2, 1).add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.GRAY, 7)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.BLUE, 12)))
                .addCell(new Cell().add(new Paragraph(textByron)).setBorder(new SolidBorder(ColorConstants.CYAN, 1)).setMaxHeight(20));
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
        table.setHeight(1700);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithSetHeightProperties03() throws IOException, InterruptedException {
        String testName = "tableWithSetHeightProperties03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n";

        String textFrance = "Liberte Egalite Fraternite";

        doc.add(new Paragraph("Default layout:"));

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE));
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed:"));

        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE));
        table.setHeight(600);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed and some cells have HEIGHT property:"));

        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN).setHeight(30))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE));
        table.setHeight(600);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed and all cells have HEIGHT property:"));

        table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED).setHeight(25))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN).setHeight(75))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE).setHeight(50));
        table.setHeight(600);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed and some cells have HEIGHT property:"));

        table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED).setHeight(25))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE).setHeight(50))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN));
        table.setHeight(600);
        doc.add(table);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table's height is bigger than needed, some cells have big rowspan and HEIGHT property:"));

        table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.BLUE))
                .addCell(new Cell(2, 1).add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.RED))
                .addCell(new Cell().add(new Paragraph(textFrance)).setBackgroundColor(ColorConstants.GREEN).setHeight(50));
        table.setHeight(600);
        doc.add(table);


        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithHeaderInTheBottomOfPageTest() throws IOException, InterruptedException {
        String testName = "tableWithHeaderInTheBottomOfPageTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("Text"));
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 10}));
        table.addHeaderCell(new Cell().add(new Paragraph("Header One")));
        table.addHeaderCell(new Cell().add(new Paragraph("Header Two")));
        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("World")));

        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void bigFooterTest01() throws IOException, InterruptedException {
        String testName = "bigFooterTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setHeight(650).setBorderTop(new SolidBorder(ColorConstants.GREEN, 100)));
        table.addCell(new Cell().add(new Paragraph("Body")).setHeight(30));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void bigFooterTest02() throws IOException, InterruptedException {
        String testName = "bigFooterTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setHeight(380).setBackgroundColor(ColorConstants.YELLOW));
        table.addHeaderCell(new Cell().add(new Paragraph("Header")).setHeight(380).setBackgroundColor(ColorConstants.BLUE));
        table.addCell(new Cell().add(new Paragraph("Body")));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithDocumentRelayoutTest() throws IOException, InterruptedException {
        String testName = "tableWithDocumentRelayoutTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4, false);

        Table table = new Table(UnitValue.createPercentArray(new float[]{10}));
        for (int i = 0; i < 40; i++) {
            table.addCell(new Cell().add(new Paragraph("" + (i + 1))));
        }

        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithKeepTogetherOnCells() throws IOException, InterruptedException {
        String testName = "tableWithKeepTogetherOnCells.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1.3f, 1f, 1f, 1f, 1f, 1f, 1f}));
        table.setWidth(UnitValue.createPercentValue(100)).setFixedLayout();
        for (int i = 1; i <= 7 * 100; i++) {
            Cell cell = new Cell().setKeepTogether(true).setMinHeight(45).add(new Paragraph("" + i));
            table.addCell(cell);
        }
        document.add(table);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void emptyTableTest01() throws IOException, InterruptedException {
        String testName = "emptyTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setBorderTop(new SolidBorder(ColorConstants.ORANGE, 50))
                .setBorderBottom(new SolidBorder(ColorConstants.MAGENTA, 100))
        );

        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.add(new AreaBreak());

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().add(new Paragraph("Hello"))
                ));
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.add(new AreaBreak());

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setMinHeight(300).setBorderRight(new SolidBorder(ColorConstants.ORANGE, 5)).setBorderTop(new SolidBorder(100)).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 50)));
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void emptyTableTest02() throws IOException, InterruptedException {
        String testName = "emptyTableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(1));

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setVerticalBorderSpacing(20);
        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));

    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)})
    public void tableWithIncompleteFooter() throws IOException, InterruptedException {
        String testName = "tableWithIncompleteFooter.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();

        table.addCell("Liberte");
        table.addCell("Egalite");
        table.addCell("Fraternite");
        table.addFooterCell(new Cell(1, 2).add(new Paragraph("Liberte Egalite")));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE),
            @LogMessage(messageTemplate = IoLogMessageConstant.GET_NEXT_RENDERER_SHOULD_BE_OVERRIDDEN)})
    public void tableWithCustomRendererTest01() throws IOException, InterruptedException {
        String testName = "tableWithCustomRendererTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 100));

        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph("Cell No." + i)));
        }
        table.setNextRenderer(new CustomRenderer(table, new Table.RowRange(0, 10)));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    // This test checks that the table occupies exactly one page and does not draw its footer.
    // A naive algorithm would have this table on two pages with only one row with data on the second page
    // However, as setSkipLastFooter is true, we can lay out that row with data on the first page and avoid unnecessary footer placement.
    public void skipLastRowTest() throws IOException, InterruptedException {
        String testName = "skipLastRowTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addHeaderCell("Header 1");
        table.addHeaderCell("Header 2");
        table.addFooterCell(new Cell(1, 2).add(new Paragraph("Footer")));
        table.setSkipLastFooter(true);
        for (int i = 0; i < 33; i++) {
            table.addCell("text 1");
            table.addCell("text 2");
        }

        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void skipFooterTest01() throws IOException, InterruptedException {
        String testName = "skipFooterTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 19; i++) {
            table.addCell(new Cell().add(new Paragraph(i + " Liberté!\nÉgalité!\nFraternité!")).setHeight(100));
        }
        table.addFooterCell(new Cell().add(new Paragraph("Footer")).setHeight(116).setBackgroundColor(ColorConstants.RED));
        // the next line cause the reuse
        table.setSkipLastFooter(true);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void skipHeaderTest01() throws IOException, InterruptedException {
        String testName = "skipHeaderTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        // construct a table
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 2; i++) {
            table.addCell(new Cell().add(new Paragraph(i + " Hello").setFontSize(18)));
        }
        table.addHeaderCell(new Cell().add(new Paragraph(" Header")));
        table.setSkipFirstHeader(true);

        // add meaningless text to occupy enough place
        for (int i = 0; i < 29; i++) {
            doc.add(new Paragraph(i + " Hello"));
        }

        // add the table
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableSplitTest01() throws IOException, InterruptedException {
        String testName = "tableSplitTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        String gretzky = "Make Gretzky great again!";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A8.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 15));

        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableSplitTest02() throws IOException, InterruptedException {
        String testName = "tableSplitTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        String gretzky = "Make Gretzky great again!";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A7.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 15));

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image image = new Image(xObject, 50);


        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(image));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));


        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableSplitTest03() throws IOException, InterruptedException {
        String testName = "tableSplitTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        String gretzky = "Make Gretzky great again!";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A8.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 15));

        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell(2, 1).add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableSplitTest04() throws IOException, InterruptedException {
        String testName = "tableSplitTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        String gretzky = "Make Gretzky great again!";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A7.rotate());

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 15));

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image image = new Image(xObject, 50);


        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell(2, 1).add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(image));
        table.addCell(new Cell().add(new Paragraph(gretzky)));
        table.addCell(new Cell().add(new Paragraph(gretzky)));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE),
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void tableNothingResultTest() throws IOException, InterruptedException {
        String testName = "tableNothingResultTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.setKeepTogether(true);
        for (int i = 0; i < 40; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello")));
            table.addCell(new Cell().add(new Paragraph("World")));
            table.startNewRow();
        }
        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void tableWithEmptyLastRowTest() throws IOException, InterruptedException {
        String testName = "tableWithEmptyLastRowTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("World")));
        startSeveralEmptyRows(table);
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

	@Test
	public void tableWithEmptyRowsBetweenFullRowsTest() throws IOException, InterruptedException {
		String testName = "tableWithEmptyRowsBetweenFullRowsTest.pdf";
		String outFileName = destinationFolder + testName;
		String cmpFileName = sourceFolder + "cmp_" + testName;

		PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
		Document doc = new Document(pdfDoc);

		Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
		table.addCell(new Cell().add(new Paragraph("Hello")));
		table.addCell(new Cell().add(new Paragraph("World")));
		startSeveralEmptyRows(table);
		table.addCell(new Cell().add(new Paragraph("Hello")));
		table.addCell(new Cell().add(new Paragraph("World")));
		doc.add(table);

		doc.close();
		Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
	}

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void tableWithEmptyRowAfterJustOneCellTest() throws IOException, InterruptedException {
        String testName = "tableWithEmptyRowAfterJustOneCellTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <= i; j++) {
                table.addCell(new Cell().add(new Paragraph("Hello")));
            }
            startSeveralEmptyRows(table);
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void tableWithAlternatingRowsTest() throws IOException, InterruptedException {
        String testName = "tableWithAlternatingRowsTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        for (int i = 0; i < 40; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello")));
            table.addCell(new Cell().add(new Paragraph("World")));
            startSeveralEmptyRows(table);
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void coloredTableWithColoredCellsTest() throws IOException, InterruptedException {
        String testName = "coloredTableWithColoredCellsTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.setBackgroundColor(ColorConstants.RED);
        for (int i = 0; i < 40; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello")).setBackgroundColor(ColorConstants.GREEN));
            table.startNewRow();
        }
        table.addCell(new Cell().add(new Paragraph("Hello")).setBackgroundColor(ColorConstants.GREEN));
        table.addCell(new Cell().add(new Paragraph("World")).setBackgroundColor(ColorConstants.GREEN));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithEmptyRowsAndSpansTest() throws IOException, InterruptedException {
        String testName = "tableWithEmptyRowsAndSpansTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30, 30}));
        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("Lovely")));
        table.addCell(new Cell().add(new Paragraph("World")));
        startSeveralEmptyRows(table);
        table.addCell(new Cell(2, 2).add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("Lovely")));
        table.addCell(new Cell().add(new Paragraph("World")));

        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithEmptyRowsAndSeparatedBordersTest() throws IOException, InterruptedException {
        String testName = "tableWithEmptyRowsAndSeparatedBordersTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("World")));
        startSeveralEmptyRows(table);
        table.addCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("World")));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithCollapsedBordersTest() throws IOException, InterruptedException {
        String testName = "tableWithCollapsedBordersTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.addCell(new Cell().add(new Paragraph("Hello")).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));
        table.addCell(new Cell().add(new Paragraph("World")).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));
        startSeveralEmptyRows(table);
        table.addCell(new Cell().add(new Paragraph("Hello")).setBorderTop(new SolidBorder(ColorConstants.RED, 20)));
        table.addCell(new Cell().add(new Paragraph("World")).setBorderTop(new SolidBorder(ColorConstants.RED, 20)));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE)
    })
    public void tableWithCollapsedBordersAndFooterTest() throws IOException, InterruptedException {
        String testName = "tableWithCollapsedBordersAndFooterTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 30}));
        table.addCell(new Cell().add(new Paragraph("Hello")).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));
        table.addCell(new Cell().add(new Paragraph("World")).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 10)));
        startSeveralEmptyRows(table);
        table.addFooterCell(new Cell().add(new Paragraph("Hello")).setBorderTop(new SolidBorder(ColorConstants.RED, 20)));
        table.addFooterCell(new Cell().add(new Paragraph("World")).setBorderTop(new SolidBorder(ColorConstants.RED, 20)));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void autoLayoutTest01() throws IOException, InterruptedException {
        String testName = "autoLayoutTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        // Initialize document
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Simple cell:"));

        Table table = new Table(new float[1]);
        table.addCell("A cell");
        doc.add(table);

        doc.add(new Paragraph("A cell with bold text:"));

        table = new Table(new float[1]);
        table.addCell("A cell").simulateBold();
        doc.add(table);

        doc.add(new Paragraph("A cell with italic text:"));

        table = new Table(new float[1]);
        table.addCell("A cell").simulateItalic();
        doc.add(table);


        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void autoLayoutTest02() throws IOException, InterruptedException {
        String testName = "autoLayoutTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Simple cell:"));
        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 95}));
        table.addCell(new Cell()
                .add(new Paragraph("Hellowor ld!")));
        table.addCell(new Cell()
                .add(new Paragraph("Long long long Long long long Long long long Long long long text")));
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void autoLayoutTest03() throws IOException, InterruptedException {
        String testName = "autoLayoutTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        table.setBorder(new SolidBorder(ColorConstants.RED, 100));
        for (int i = 0; i < 3; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello")));
        }
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void fixedLayoutTest01() throws IOException, InterruptedException {
        String testName = "fixedLayoutTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        // Initialize document
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Simple table with proportional width. Ignore cell width, because sum(col[*]) < tableWidth:"));
        Table table = new Table(new float[]{1, 2, 3}).setFixedLayout().setWidth(400);
        table.addCell("1x");
        table.addCell("2x");
        table.addCell("3x");
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void fixedLayoutTest02() throws IOException, InterruptedException {
        String testName = "fixedLayoutTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        // Initialize document
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Simple table with proportional width. Ignore table width, because sum(col[*]) > tableWidth."));
        Table table = new Table(new float[]{20, 40, 60}).setFixedLayout().setWidth(10);
        table.addCell("1x");
        table.addCell("2x");
        table.addCell("3x");
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    public void fixedPositionTest01() throws IOException, InterruptedException {
        String testName = "fixedPositionTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        // Initialize document
        Document doc = new Document(pdf);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        for (int i = 0; i < 100; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello " + i)).setBackgroundColor(ColorConstants.RED));
        }
        table.setFixedPosition(150, 300, 200);
        table.setHeight(300);
        table.setBackgroundColor(ColorConstants.YELLOW);

        doc.add(new Paragraph("The next table has fixed position and height property. However set height is shorter than needed and we can place table only partially."));
        doc.add(table);

        doc.add(new AreaBreak());
        table.setHeight(10);
        doc.add(new Paragraph("The next table has fixed position and height property. However set height is shorter than needed and we cannot fully place even a cell."));
        doc.add(table);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    // When the test was created, only first line of text was displayed on the first page
    public void nestedTableLostContent() throws IOException, InterruptedException {
        String testName = "nestedTableLostContent.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        String text = "abacaba absa ";
        for (int i = 0; i < 7; i++) {
            text += text;
        }

        Table innerTable = new Table(UnitValue.createPointArray(new float[]{50}));
        innerTable.addCell(text);
        Table outerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        outerTable.addCell(new Cell().add(innerTable));
        outerTable.addCell(new Cell().setBackgroundColor(ColorConstants.RED).add(new Div().setMinHeight(850).setKeepTogether(true)));
        doc.add(outerTable);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    // When the test was created, an exception was thrown due to min-max width calculations for an inner table.
    // At some point isOriginalNonSplitRenderer was true for a parent renderer but false for the inner table renderer
    public void nestedTableMinMaxWidthException() throws IOException, InterruptedException {
        String testName = "nestedTableMinMaxWidthException.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdf);

        String text = "abacaba absa ";
        for (int i = 0; i < 9; i++) {
            text += text;
        }

        Table innerTable = new Table(UnitValue.createPointArray(new float[]{50}));
        innerTable.addCell("Small text");
        innerTable.addCell(new Cell().add(new Paragraph(text)).setKeepTogether(true));
        Table outerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        outerTable.addCell(new Cell().add(innerTable));
        doc.add(outerTable);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest01() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{100}));
        Cell cell = new Cell().setWidth(UnitValue.createPointValue(216)).add(new Paragraph("width:72pt"));
        cell.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(72));
        table.addCell(cell);
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest02() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{100}));
        Cell cell = new Cell().setWidth(UnitValue.createPointValue(216)).add(new Paragraph("width:72pt"));
        cell.setMaxWidth(72);
        table.addCell(cell);
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest03() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{100}));
        Cell cell = new Cell().setWidth(UnitValue.createPointValue(50)).add(new Paragraph("width:72pt"));
        cell.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(72));
        table.addCell(cell);
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest04() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(new float[]{100}));
        Cell cell = new Cell().setWidth(UnitValue.createPointValue(50)).add(new Paragraph("width:72pt"));
        cell.setMinWidth(72);
        table.addCell(cell);
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest05() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(80));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.addCell(new Cell(1, 3).add(new Paragraph("Cell with colspan 3")));
        table.addCell(new Cell(2, 1).add(new Paragraph("Cell with rowspan 2")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")).setMinWidth(200));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")).setMaxWidth(50));
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void cellsWithEdgeCaseLeadingTest01() throws IOException, InterruptedException {
        String testName = "cellsWithEdgeCaseLeadingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        SolidBorder border = new SolidBorder(1f);

        Table table = new Table(UnitValue.createPointArray(new float[]{20, 20, 20, 20}));

        Paragraph paragraph5 = new Paragraph(new Text("Cell5"));
        Paragraph paragraph6 = new Paragraph(new Text("Cell6"));
        Paragraph paragraph7 = new Paragraph(new Text("Cell7"));
        Paragraph paragraph8 = new Paragraph(new Text("Cell8"));

        Paragraph paragraph13 = new Paragraph("Cell13");
        Paragraph paragraph14 = new Paragraph(new Text(""));
        Paragraph paragraph15 = new Paragraph(new Text("Cell15VVVVVVVVV"));
        Paragraph paragraph16 = new Paragraph(new Text(""));

        Cell cell1 = new Cell().add(new Paragraph().add("Cell1")).setBorder(border);
        Cell cell2 = new Cell().add(new Paragraph().add("Cell2")).setBorder(border);
        Cell cell3 = new Cell().add(new Paragraph().add("Cell3")).setBorder(border);
        Cell cell4 = new Cell().add(new Paragraph().add("Cell4")).setBorder(border);
        Cell cell5 = new Cell().add(paragraph5.setFixedLeading(8)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell6 = new Cell().add(paragraph6.setFixedLeading(0)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell7 = new Cell().add(paragraph7.setFixedLeading(8)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell8 = new Cell().add(paragraph8.setFixedLeading(-4)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell9 = new Cell().add(new Paragraph().add("Cell9")).setBorder(border);
        Cell cell10 = new Cell().add(new Paragraph().add("Cell10")).setBorder(border);
        Cell cell11 = new Cell().add(new Paragraph().add("Cell11")).setBorder(border);
        Cell cell12 = new Cell().add(new Paragraph().add("Cell12")).setBorder(border);
        Cell cell13 = new Cell().add(paragraph13.setMultipliedLeading(-1)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell14 = new Cell().add(paragraph14.setMultipliedLeading(4)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell15 = new Cell().add(paragraph15.setMultipliedLeading(8)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell16 = new Cell().add(paragraph16.setMultipliedLeading(-4)).setBorder(border).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Cell cell17 = new Cell().add(new Paragraph().add("Cell17")).setBorder(border);
        Cell cell18 = new Cell().add(new Paragraph().add("Cell18")).setBorder(border);
        Cell cell19 = new Cell().add(new Paragraph().add("Cell19")).setBorder(border);
        Cell cell20 = new Cell().add(new Paragraph().add("Cell20")).setBorder(border);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cell6);
        table.addCell(cell7);
        table.addCell(cell8);
        table.addCell(cell9);
        table.addCell(cell10);
        table.addCell(cell11);
        table.addCell(cell12);
        table.addCell(cell13);
        table.addCell(cell14);
        table.addCell(cell15);
        table.addCell(cell16);
        table.addCell(cell17);
        table.addCell(cell18);
        table.addCell(cell19);
        table.addCell(cell20);

        document.add(table);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableMinMaxWidthTest06() throws IOException, InterruptedException {
        String testName = "tableMinMaxWidthTest06.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(2));
        table.setBorder(new SolidBorder(ColorConstants.RED, 1));
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        table.setHorizontalBorderSpacing(20);
        table.setVerticalBorderSpacing(20);
        table.addCell(new Cell().add(new Paragraph("The cell with width 50. Number 1").setWidth(50)));
        table.addCell(new Cell().add(new Paragraph("The cell with width 50. Number 1").setWidth(50)));

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    public void splitTableMinMaxWidthTest01() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);

        Table table = new Table(2);
        for (int i = 0; i < 26; i++) {
            table.addCell(new Cell().add(new Paragraph("abba a")));
            table.addCell(new Cell().add(new Paragraph("ab ab ab")));
        }

        // not enough to place even if min-width approach is used
        float areaWidth = 20;

        LayoutResult result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(areaWidth, 100))));
        TableRenderer overflowRenderer = (TableRenderer) result.getOverflowRenderer();

        MinMaxWidth minMaxWidth = overflowRenderer.getMinMaxWidth();

        Assertions.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assertions.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);

        // not enough to place using max-width approach, but more than required for min-width approach
        areaWidth = 70;

        result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(areaWidth, 100))));
        overflowRenderer = (TableRenderer) result.getOverflowRenderer();

        minMaxWidth = overflowRenderer.getMinMaxWidth();

        Assertions.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assertions.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);


        // enough to place using max-width approach
        areaWidth = 400f;

        result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(areaWidth, 100))));
        overflowRenderer = (TableRenderer) result.getOverflowRenderer();

        minMaxWidth = overflowRenderer.getMinMaxWidth();

        Assertions.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assertions.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);
    }


    @Test
    public void marginPaddingTest01() throws IOException, InterruptedException {
        String testName = "marginPaddingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Body Cell 1")).setBorder(new SolidBorder(30)));
        table.addCell(new Cell().add(new Paragraph("Body Cell 2")).setBorder(new SolidBorder(30)));

        table.addFooterCell(new Cell().add(new Paragraph("Footer Cell 1")).setBorder(new SolidBorder(70)));
        table.addFooterCell(new Cell().add(new Paragraph("Footer Cell 2")).setBorder(new SolidBorder(70)));

        table.addHeaderCell(new Cell().add(new Paragraph("Header Cell 1")).setBorder(new SolidBorder(70)));
        table.addHeaderCell(new Cell().add(new Paragraph("Header Cell 2")).setBorder(new SolidBorder(70)));


        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);

        table.setMargin(20);
        table.setPadding(20);
        table.setBorder(new SolidBorder(ColorConstants.RED, 10));
        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell(new Cell().add(new Paragraph("Hello"))).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void spacingTest01() throws IOException, InterruptedException {
        String testName = "spacingTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        int n = 4;
        Table table = new Table(UnitValue.createPercentArray(n)).useAllAvailableWidth();

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                table.addCell(new Cell().add(new Paragraph(j + "Body Cell" + i)));
                table.addFooterCell(new Cell().add(new Paragraph(j + "Footer Cell 1")));
                table.addHeaderCell(new Cell().add(new Paragraph(j + "Header Cell 1")));
            }
        }

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);

        table.setHorizontalBorderSpacing(20f);
        table.setVerticalBorderSpacing(20f);

        table.setBorder(new SolidBorder(ColorConstants.RED, 10));

        doc.add(table);
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().addCell(new Cell().add(new Paragraph("Hello"))).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void taggedTableWithCaptionTest01() throws IOException, InterruptedException {
        String testName = "taggedTableWithCaptionTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 10, 2, 2, (UnitValue) null, BorderCollapsePropertyValue.SEPARATE, new Style().setBorder(new SolidBorder(ColorConstants.RED, 10)));

        Paragraph pCaption = new Paragraph("I'm a caption!").setBackgroundColor(ColorConstants.CYAN);
        table.setCaption(new Div().add(pCaption));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void wideCaptionTest01() throws IOException, InterruptedException {
        String testName = "wideCaptionTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 3, 3, 3, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.RED, 10)));

        // no caption
        addTable(table, true, true, doc);

        // the caption as a paragraph
        Paragraph pCaption = new Paragraph("I'm a caption!").setBackgroundColor(ColorConstants.CYAN);
        table.setCaption(new Div().add(pCaption).setWidth(500));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a div
        Div divCaption = new Div().add(pCaption).add(pCaption).add(pCaption).setBackgroundColor(ColorConstants.MAGENTA).setWidth(500);
        table.setCaption(divCaption).setWidth(500);
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a table
        Table tableCaption = createTestTable(1, 1, 0, 0, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.BLUE, 10)).setBackgroundColor(ColorConstants.YELLOW)).setWidth(500);
        table.setCaption(new Div().add(tableCaption).setWidth(500));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void splitTableWithCaptionTest01() throws IOException, InterruptedException {
        String testName = "splitTableWithCaptionTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 30, 3, 3, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.RED, 10)));
        table.getFooter().setBorder(new SolidBorder(ColorConstants.ORANGE, 20));
        table.getHeader().setBorder(new SolidBorder(ColorConstants.ORANGE, 20));

        Paragraph pCaption = new Paragraph("I'm a caption!").setBackgroundColor(ColorConstants.CYAN);

        // no caption
        addTable(table, true, true, doc);

        // top caption
        table.setCaption(new Div().add(pCaption));
        addTable(table, true, true, doc);

        // bottom caption
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void captionedTableOfOnePageWithCollapsedBordersTest01() throws IOException, InterruptedException {
        String testName = "captionedTableOfOnePageWithCollapsedBordersTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 10, 2, 2, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.RED, 10)));
        table.getHeader().setBorder(new SolidBorder(ColorConstants.ORANGE, 5f));
        table.getFooter().setBorder(new SolidBorder(ColorConstants.ORANGE, 5f));

        // no caption
        addTable(table, true, true, doc);

        // the caption as a paragraph
        Paragraph pCaption = new Paragraph("I'm a caption!").setBackgroundColor(ColorConstants.CYAN);
        table.setCaption(new Div().add(pCaption));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a div
        Div divCaption = new Div().add(pCaption).add(pCaption).add(pCaption).setBackgroundColor(ColorConstants.MAGENTA);
        table.setCaption(divCaption);
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a table
        Table tableCaption = createTestTable(1, 1, 0, 0, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.BLUE, 10)).setBackgroundColor(ColorConstants.YELLOW));
        table.setCaption(new Div().add(tableCaption));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableWithDifferentStylesOfCollapsedBordersTest() throws IOException, InterruptedException {
        String testName = "tableWithDifferentStylesOfCollapsedBordersTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 10, 2, 2, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new DashedBorder(ColorConstants.RED, 10)));
        table.getHeader().setBorder(new DottedBorder(ColorConstants.ORANGE, 5f));
        table.getFooter().setBorder(new RoundDotsBorder(ColorConstants.ORANGE, 5f));

        // no caption
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));

    }

    @Test
    public void captionedTableOfOnePageWithSeparatedBordersTest01() throws IOException, InterruptedException {
        String testName = "captionedTableOfOnePageWithSeparatedBordersTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = createTestTable(2, 10, 2, 2, (UnitValue) null, BorderCollapsePropertyValue.SEPARATE,
                new Style().setBorder(new SolidBorder(ColorConstants.RED, 10)));

        // no caption
        addTable(table, true, true, doc);

        // the caption as a paragraph
        Paragraph pCaption = new Paragraph("I'm a caption!").setBackgroundColor(ColorConstants.CYAN);
        table.setCaption(new Div().add(pCaption));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a div
        Div divCaption = new Div().add(pCaption).add(pCaption).add(pCaption).setBackgroundColor(ColorConstants.MAGENTA);
        table.setCaption(divCaption);
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        // the caption as a table
        Table tableCaption = createTestTable(1, 1, 0, 0, (UnitValue) null, BorderCollapsePropertyValue.COLLAPSE,
                new Style().setBorder(new SolidBorder(ColorConstants.BLUE, 10)).setBackgroundColor(ColorConstants.YELLOW));
        table.setCaption(new Div().add(tableCaption));
        addTable(table, true, true, doc);
        table.getCaption().setProperty(Property.CAPTION_SIDE, CaptionSide.BOTTOM);
        addTable(table, true, true, doc);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private void addTable(Table table, boolean addParagraphBefore, boolean addParagraphAfter, Document doc) {
        if (addParagraphBefore) {
            doc.add(new Paragraph("I'm the paragraph placed before the table. I'm green and have no border.").setBackgroundColor(ColorConstants.GREEN));
        }
        doc.add(table);
        if (addParagraphAfter) {
            doc.add(new Paragraph("I'm the paragraph placed after the table. I'm green and have no border.").setBackgroundColor(ColorConstants.GREEN));
        }
        doc.add(new AreaBreak());
    }

    private Table createTestTable(int colNum, int bodyRowNum, int headerRowNum, int footerRowNum, UnitValue width, BorderCollapsePropertyValue collapseValue, Style style) {
        Table table = new Table(colNum);
        if (null != width) {
            table.setWidth(width);
        }
        if (null != style) {
            table.addStyle(style);
        }
        if (BorderCollapsePropertyValue.SEPARATE.equals(collapseValue)) {
            table.setBorderCollapse(collapseValue);
        }
        for (int i = 0; i < bodyRowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                table.addCell("Body Cell row " + i + " col " + j);
            }
        }
        for (int i = 0; i < headerRowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                table.addHeaderCell("Header Cell row " + i + " col " + j);
            }
        }
        for (int i = 0; i < footerRowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                table.addFooterCell("Footer Cell row " + i + " col " + j);
            }
        }

        return table;
    }

    @Test
    public void skipLastFooterAndProcessBigRowspanTest01() throws IOException, InterruptedException {
        String testName = "skipLastFooterAndProcessBigRowspanTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 140));
        Table table = new Table(2);

        table.setSkipLastFooter(true);

        table.addFooterCell(new Cell(1, 2).add(new Paragraph("Footer")));
        table.addCell(new Cell(3, 1).add(new Paragraph(Integer.toString(1))));
        for (int z = 0; z < 3; z++) {
            table.addCell(new Cell().add(new Paragraph(Integer.toString(z))));
        }

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void skipLastFooterAndProcessBigRowspanTest02() throws IOException, InterruptedException {
        String testName = "skipLastFooterAndProcessBigRowspanTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        int numRows = 3;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(numRows);
        table.setSkipLastFooter(true);

        table.addHeaderCell(new Cell(1, numRows).add(new Paragraph("Header")));
        table.addFooterCell(new Cell(1, numRows).add(new Paragraph("Footer")));

        for (int rows = 0; rows < 11; rows++) {
            table.addCell(new Cell(numRows, 1).add(new Paragraph("Filled Cell: " + Integer.toString(rows) + ", 0")));

            //Number of cells to complete the table rows filling up to the cell of colSpan
            int numFillerCells = (numRows - 1) * numRows;
            for (int cells = 0; cells < numFillerCells; cells++) {
                table.addCell(new Cell().add(new Paragraph("Filled Cell: " + Integer.toString(rows) + ", " + Integer.toString(cells))));
            }
        }
        doc.add(table);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void skipLastFooterOnShortPageTest01() throws IOException, InterruptedException {
        String testName = "skipLastFooterOnShortPageTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595, 120));
        Table table = new Table(2);

        table.setSkipLastFooter(true);

        table.addFooterCell(new Cell(1, 2).add(new Paragraph("Footer")));
        for (int z = 0; z < 2; z++) {
            for (int i = 0; i < 2; i++) {
                table.addCell(new Cell().add(new Paragraph(Integer.toString(z))));
            }
        }

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void firstRowPartiallyFitWideBottomBorderTest() throws IOException, InterruptedException {
        String testName = "firstRowPartiallyFitWideBottomBorderTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4);

        Table table = new Table(1);
        table.setBorderBottom(new SolidBorder(ColorConstants.RED, 250));

        Cell notFitCell = new Cell();
        notFitCell.add(new Paragraph("Some text which should be big enough."));
        notFitCell.setFontSize(100);
        table.addCell(notFitCell);

        table.addCell("row 2 col 1");
        table.addCell("row 2 col 2");

        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void collapseWithNextRowWiderThanWithTableBorderTest() throws IOException, InterruptedException {
        String testName = "collapseWithNextRowWiderThanWithTableBorderTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4);

        Table table = new Table(1);

        Cell cell1 = new Cell();
        cell1.add(new Paragraph("Usual bottom border"));
        cell1.setHeight(300);
        table.addCell(cell1);

        Cell cell2 = new Cell();
        cell2.add(new Paragraph("Top border: 600pt"));
        cell2.setBorderTop(new SolidBorder(600));
        table.addCell(cell2);

        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void tableBottomBorderWideTest() throws IOException, InterruptedException {
        String testName = "tableBottomBorderWideTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(1)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 500))
                .addCell(new Cell().add(new Paragraph(TEXT_CONTENT + TEXT_CONTENT + TEXT_CONTENT + TEXT_CONTENT)))
                .addCell(new Cell().add(new Paragraph("Hello World")));
        doc.add(table);
        doc.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        doc.add(table);
        addTableBelowToCheckThatOccupiedAreaIsCorrect(doc);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void cellWithBigRowspanCompletedRowTooTest() throws IOException, InterruptedException {
        String testName = "cellWithBigRowspanCompletedRowTooTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setHeight(700);

        Table table = new Table(2);
        table.setBorder(new SolidBorder(1));
        table.setHorizontalBorderSpacing(5);
        table.setVerticalBorderSpacing(5);

        table.addCell(new Cell(7, 1).add(new Paragraph("Rowspan 7")).setBackgroundColor(ColorConstants.RED));
        for (int i = 0; i < 7; i++) {
            table.addCell(new Cell().add(new Paragraph("Rowspan 1")));
        }

        // test separated borders when j == 0 and collapsed borders when j == 1
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        for (int j = 0; j < 2; j++) {
            doc.add(div);
            doc.add(table);

            if (0 == j) {
                doc.add(new AreaBreak());
                table.setBorderCollapse(BorderCollapsePropertyValue.COLLAPSE);
            }
        }

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void cellWithBigRowspanCompletedRowNotTest() throws IOException, InterruptedException {
        String testName = "cellWithBigRowspanCompletedRowNotTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setHeight(700);

        Table table = new Table(2);
        table.setBorder(new SolidBorder(1));
        table.setHorizontalBorderSpacing(5);
        table.setVerticalBorderSpacing(5);

        table.addCell(new Cell(7, 1).add(new Paragraph("Rowspan 7")).setBackgroundColor(ColorConstants.RED));
        table.addCell(new Cell().add(new Paragraph(TEXT_CONTENT)));
        for (int i = 0; i < 6; i++) {
            table.addCell(new Cell().add(new Paragraph("Rowspan 1")));
        }

        // test separated borders when j == 0 and collapsed borders when j == 1
        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        for (int j = 0; j < 2; j++) {
            doc.add(div);
            doc.add(table);

            if (0 == j) {
                doc.add(new AreaBreak());
                table.setBorderCollapse(BorderCollapsePropertyValue.COLLAPSE);
            }
        }

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void inheritHeaderPropsWhileMinMaxWidthCalculationsTest() throws IOException, InterruptedException {
        String filename = "inheritHeaderPropsWhileMinMaxWidthCalculations.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + filename));
        Document document = new Document(pdf);

        Paragraph p = new Paragraph("Some text is placed at the beginning"
                + " of the page, so that page isn't being empty.");
        document.add(p);

        Table table = new Table(new float[1]);

        // The header's text is longer than the body's text, hence the width
        // of the table will be calculated by the header.
        table.addHeaderCell(new Cell().add(new Paragraph("Hello")));
        table.addCell(new Cell().add(new Paragraph("He")));

        // If this property is not inherited while calculating min/max widths,
        // then while layouting header will request more space than the layout box's width
        table.getHeader().simulateBold();
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void infiniteLoopOnUnfitCellAndBigRowspanTest() throws IOException, InterruptedException {
        String testName = "infiniteLoopOnUnfitCellAndBigRowspanTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(38);
        table.useAllAvailableWidth();
        table.setFixedLayout();

        Cell cellNum1 = new Cell(1, 1);
        table.addCell(cellNum1);

        Cell cellNum2 = new Cell(2, 2);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "itext.png"));
        cellNum2.add(img);
        table.addCell(cellNum2);

        Cell cellNum3 = new Cell(2, 36);
        cellNum3.add(new Paragraph("text"));
        table.addCell(cellNum3);

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA),
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    public void firstRowNotFitBigRowspanTest() throws IOException, InterruptedException {
        String testName = "firstRowNotFitBigRowspanTest.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4);

        Table table = new Table(4);

        table.addCell("row 1 col 1");

        Cell notFitCell = new Cell(2, 1);
        notFitCell.add(new Paragraph("row 1-2 col 2"));
        notFitCell.setFontSize(1000);
        table.addCell(notFitCell);

        Cell fitCell = new Cell(2, 2);
        fitCell.add(new Paragraph("row 1-2 col 3-4"));
        table.addCell(fitCell);

        table.addCell("row 2 col 1");

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowSpanTooFarFullTest() throws IOException, InterruptedException {
        String filename = "bigRowSpanTooFarFullTest.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + filename));
        Document document = new Document(pdf);

        Table table = new Table(2);

        int bigRowSpan = 5;
        table.addCell(
                new Cell(bigRowSpan, 1)
                        .add(new Paragraph("row span " + bigRowSpan))
                        .setBackgroundColor(ColorConstants.RED));
        for (int i = 0; i < bigRowSpan; i++) {
            table.addCell(
                    new Cell()
                            .add(new Paragraph(Integer.toString(i)))
                            .setHeight(375)
                            .setBackgroundColor(ColorConstants.BLUE));
        }

        document.add(table);
        document.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void bigRowSpanTooFarPartialTest() throws IOException, InterruptedException {
        String filename = "bigRowSpanTooFarPartialTest.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + filename));
        Document document = new Document(pdf);

        Table table = new Table(2);

        int bigRowSpan = 5;
        table.addCell(
                new Cell(bigRowSpan, 1)
                        .add(new Paragraph("row span " + bigRowSpan))
                        .setHeight(800)
                        .setBackgroundColor(ColorConstants.RED));
        for (int i = 0; i < bigRowSpan; i++) {
            table.addCell(
                    new Cell()
                            .add(new Paragraph(Integer.toString(i)))
                            .setHeight(375)
                            .setBackgroundColor(ColorConstants.BLUE));
        }

        document.add(table);
        document.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    public void bigRowSpanTooFarNothingTest() throws IOException, InterruptedException {
        String filename = "bigRowSpanTooFarNothingTest.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + filename));
        Document document = new Document(pdf);

        Table table = new Table(2);

        int bigRowSpan = 5;
        table.addCell(
                new Cell(bigRowSpan, 1)
                        .add(new Paragraph("row span " + bigRowSpan))
                        .setHeight(800)
                        .setKeepTogether(true)
                        .setBackgroundColor(ColorConstants.RED));
        for (int i = 0; i < bigRowSpan; i++) {
            table.addCell(
                    new Cell()
                            .add(new Paragraph(Integer.toString(i)))
                            .setHeight(375)
                            .setBackgroundColor(ColorConstants.BLUE));
        }

        document.add(table);
        document.add(new AreaBreak());

        table.setBorderCollapse(BorderCollapsePropertyValue.SEPARATE);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    // TODO DEVSIX-5916 The first cell's width is the same as the second one's, however, it's not respected
    public void setWidthShouldBeRespectedTest() throws IOException, InterruptedException {
        String fileName = "setWidthShouldBeRespectedTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + fileName));
        Document doc = new Document(pdfDocument, new PageSize(842, 1400));

        Table table = new Table(2);
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 90f));

        Cell cell;
        cell = new Cell().add(new Paragraph("100pt"));
        cell.setBorder(new SolidBorder(ColorConstants.BLUE, 20f));
        cell.setWidth(100).setMargin(0).setPadding(0);
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("100pt"));
        cell.setBorder(new SolidBorder(ColorConstants.RED, 120f));
        cell.setWidth(100).setMargin(0).setPadding(0);
        table.addCell(cell);

        doc.add(table);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    //creates 2 empty lines, where 2 is random number
    private static void startSeveralEmptyRows(Table table) {
        table.startNewRow();
        table.startNewRow();
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void preciseFittingBoldSimulatedTextInCellsTest() throws IOException, InterruptedException {
        String fileName = "preciseFittingBoldSimulatedTextInCells.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + fileName));
            Document doc = new Document(pdfDocument)) {

            int numberOfColumns = 9;
            Table table = new Table(UnitValue.createPercentArray(numberOfColumns));
            table.useAllAvailableWidth();
            table.setFixedLayout();

            for (int i = 0; i < numberOfColumns; i++) {
                table.addCell(new Cell().add(new Paragraph("Description").simulateBold()));
            }

            doc.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    @Test
    public void tableRelayoutTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc)) {

        float width = 142f;

        Table table = new Table(1);
        table.setWidth(width);
        table.setFixedLayout();
        Cell cell = new Cell();
        cell.setWidth(width);
        cell.add(new Paragraph("Testing, FinancialProfessional Associate adasdasdasdasada.gmail.com"));
        table.addCell(cell);

        LayoutResult result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1,
                        new Rectangle(0, 0, 10000, 10000.0F))));

        Rectangle tableRect = result.getOccupiedArea().getBBox();

        result = table.createRendererSubTree().setParent(doc.getRenderer()).layout(new LayoutContext(
                new LayoutArea(1, new Rectangle(0, 0, 10000, 10000.0F))));

        Rectangle tableRectRelayout = result.getOccupiedArea().getBBox();

        Assertions.assertTrue(tableRect.equalsWithEpsilon(tableRectRelayout));
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN)
    })
    public void infiniteLoopKeepTogetherTest() throws IOException, InterruptedException {
        String fileName = "infiniteLoopKeepTogether.pdf";
        float fontSize = 8;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + fileName));
                Document doc = new Document(pdfDoc)) {
            doc.setMargins(138, 20, 75, 20);

            Table table = new Table(5);
            table.setKeepTogether(true);

            for (int i = 0; i < 37; i++) {
                table.addCell(new Cell(1, 5).add(new Paragraph(new Text("Cell"))).setFontSize(fontSize));
                table.startNewRow();
            }

            Table commentsTable = new Table(1);
            Cell commentsCell = new Cell().add(new Paragraph(new Text("First line\nSecond line")));
            commentsTable.addCell(commentsCell);

            Cell outerCommentsCell = new Cell(1, 5).setFontSize(fontSize);
            outerCommentsCell.add(commentsTable);
            table.addCell(outerCommentsCell);

            doc.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN),
            @LogMessage(messageTemplate = IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, logLevel = LogLevelConstants.WARN)
    })
    public void negativeLayoutAreaTest() throws IOException, InterruptedException {
        String testName = "negativeLayoutAreaTable.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(595.0f, 50.0f));

        doc.add(new Table(new float[]{1, 1}).addCell(new Cell().setHeight(10.0f)));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN),
    })
    public void keepTogetherCaptionAndHugeCellTest() throws IOException, InterruptedException {
        String fileName = "keepTogetherCaptionAndHugeCell.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName));
        Document document = new Document(pdfDocument, PageSize.A4);

        Table table = new Table(1)
                .setCaption(new Div().add(new Paragraph("hello world")));

        Cell dataCell = new Cell()
                .setKeepTogether(true)
                .add(new Paragraph(PlaceHolderTextUtil
                        .getPlaceHolderText(PlaceHolderTextUtil.PlaceHolderTextBy.WORDS, 600)));

        table.addCell(dataCell);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN),
    })
    public void keepTogetherCaptionDoesntFitPageTest() throws IOException, InterruptedException {
        String fileName = "keepTogetherCaptionDoesntFitPage.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName));
        Document document = new Document(pdfDocument, PageSize.A4);

        document.add(new Paragraph(PlaceHolderTextUtil
                .getPlaceHolderText(PlaceHolderTextUtil.PlaceHolderTextBy.WORDS, 580)));

        Table table = new Table(1)
                .setCaption(new Div().add(new Paragraph("hello world")));

        Cell dataCell = new Cell()
                .setKeepTogether(true)
                .add(new Paragraph(PlaceHolderTextUtil
                        .getPlaceHolderText(PlaceHolderTextUtil.PlaceHolderTextBy.WORDS, 600)));

        table.addCell(dataCell);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN),
    })
    public void keepTogetherCaptionAndSplitCellTest() throws IOException, InterruptedException {
        String fileName = "keepTogetherCaptionAndSplitCell.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName));
        Document document = new Document(pdfDocument, PageSize.A4);

        Table table = new Table(1)
                .setCaption(new Div().add(new Paragraph("hello world").setFontSize(40)),CaptionSide.BOTTOM);


        Cell dataCell = new Cell()
                .setKeepTogether(true)
                .add(new Paragraph(PlaceHolderTextUtil
                        .getPlaceHolderText(PlaceHolderTextUtil.PlaceHolderTextBy.WORDS, 540)));

        table.addCell(dataCell);
        document.add(table);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName,
                sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    private static class RotatedDocumentRenderer extends DocumentRenderer {
        private final PdfDocument pdfDoc;

        public RotatedDocumentRenderer(Document doc, PdfDocument pdfDoc) {
            super(doc);
            this.pdfDoc = pdfDoc;
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            int currentNumberOfPages = document.getPdfDocument().getNumberOfPages();
            PageSize pageSize = currentNumberOfPages % 2 == 1 ? PageSize.A4.rotate() : PageSize.A4;
            pdfDoc.addNewPage(pageSize);
            return pageSize;
        }
    }

    static class CustomRenderer extends TableRenderer {
        public CustomRenderer(Table modelElement, Table.RowRange rowRange) {
            super(modelElement, rowRange);
        }
    }
}
