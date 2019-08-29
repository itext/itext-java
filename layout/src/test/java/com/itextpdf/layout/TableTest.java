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
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.CaptionSide;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class TableTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TableTest/";

    static final String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
    static final String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
    static final String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
        Assert.assertEquals("Cell[row=0, col=0, rowspan=1, colspan=1]", table.getCell(0, 0).toString());
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest04() throws IOException, InterruptedException {
        String testName = "tableTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)));
        table.addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + textContent + textContent + textContent)));
        table.addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)));
        table.addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest05() throws IOException, InterruptedException {
        String testName = "tableTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + textContent + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest06() throws IOException, InterruptedException {
        String testName = "tableTest06.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest07() throws IOException, InterruptedException {
        String testName = "tableTest07.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest08() throws IOException, InterruptedException {
        String testName = "tableTest08.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest09() throws IOException, InterruptedException {
        String testName = "tableTest09.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 2:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest14() throws IOException, InterruptedException {
        String testName = "tableTest14.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest15() throws IOException, InterruptedException {
        String testName = "tableTest15.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
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
                .addCell(new Cell(3, 2).add(new Paragraph("cell 3:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest16() throws IOException, InterruptedException {
        String testName = "tableTest16.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + middleTextContent)).setBorder(new SolidBorder(ColorConstants.RED, 2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest18() throws IOException, InterruptedException {
        String testName = "tableTest18.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(textContent));

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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest19() throws IOException, InterruptedException {
        String testName = "tableTest19.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest20() throws IOException, InterruptedException {
        String testName = "tableTest20.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest21() throws IOException, InterruptedException {
        String testName = "tableTest21.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        doc.add(new Paragraph(textContent));

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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest01() throws IOException, InterruptedException {
        String testName = "bigRowspanTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest02() throws IOException, InterruptedException {
        String testName = "bigRowspanTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest03() throws IOException, InterruptedException {
        String testName = "bigRowspanTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest04() throws IOException, InterruptedException {
        String testName = "bigRowspanTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest05() throws IOException, InterruptedException {
        String testName = "bigRowspanTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(2, 1).add(new Paragraph("cell 1, 1 and 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + textContent)));

        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 3)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 3)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().setPadding(0).setMargin(0).setBorder(Border.NO_BORDER))
                .addCell(new Cell().add(new Paragraph("Hello"))
                ));
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));
        doc.add(new AreaBreak());

        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setMinHeight(300).setBorderRight(new SolidBorder(ColorConstants.ORANGE, 5)).setBorderTop(new SolidBorder(100)).setBorderBottom(new SolidBorder(ColorConstants.BLUE, 50)));
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));

    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 2)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE, count = 1)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE),
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        table.addCell("A cell").setBold();
        doc.add(table);

        doc.add(new Paragraph("A cell with italic text:"));

        table = new Table(new float[1]);
        table.addCell("A cell").setItalic();
        doc.add(table);


        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
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

        Assert.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assert.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);

        // not enough to place using max-width approach, but more than required for min-width approach
        areaWidth = 70;

        result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(areaWidth, 100))));
        overflowRenderer = (TableRenderer) result.getOverflowRenderer();

        minMaxWidth = overflowRenderer.getMinMaxWidth();

        Assert.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assert.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);


        // enough to place using max-width approach
        areaWidth = 400f;

        result = table.createRendererSubTree().setParent(doc.getRenderer())
                .layout(new LayoutContext(new LayoutArea(1, new Rectangle(areaWidth, 100))));
        overflowRenderer = (TableRenderer) result.getOverflowRenderer();

        minMaxWidth = overflowRenderer.getMinMaxWidth();

        Assert.assertEquals(result.getOccupiedArea().getBBox().getWidth(), minMaxWidth.getMaxWidth(), 0.0001);
        Assert.assertEquals(minMaxWidth.getMaxWidth(), minMaxWidth.getMinWidth(), 0.0001);
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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
            int numFillerCells = (numRows - 1) * numRows; //Number of cells to complete the table rows filling up to the cell of colSpan
            for (int cells = 0; cells < numFillerCells; cells++) {
                table.addCell(new Cell().add(new Paragraph("Filled Cell: " + Integer.toString(rows) + ", " + Integer.toString(cells))));
            }
        }
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private static class RotatedDocumentRenderer extends DocumentRenderer {
        private final PdfDocument pdfDoc;

        public RotatedDocumentRenderer(Document doc, PdfDocument pdfDoc) {
            super(doc);
            this.pdfDoc = pdfDoc;
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize pageSize = currentPageNumber % 2 == 1 ? PageSize.A4 : PageSize.A4.rotate();
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
