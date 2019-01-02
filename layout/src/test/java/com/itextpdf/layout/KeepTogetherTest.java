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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
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
public class KeepTogetherTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/KeepTogetherTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/KeepTogetherTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void keepTogetherParagraphTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherParagraphTest01.pdf";
        String outFile = destinationFolder + "keepTogetherParagraphTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);


        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 29; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanasdadasdadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Paragraph p1 = new Paragraph(str);
        p1.setKeepTogether(true);
        doc.add(p1);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherParagraphTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherParagraphTest02.pdf";
        String outFile = destinationFolder + "keepTogetherParagraphTest02.pdf";

        PdfWriter writer = new PdfWriter(outFile);


        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanasdadasdadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        for (int i = 0; i < 5; i++) {
            str += str;
        }

        Paragraph p1 = new Paragraph(str);
        p1.setKeepTogether(true);
        doc.add(p1);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepTogetherListTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherListTest01.pdf";
        String outFile = destinationFolder + "keepTogetherListTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        List list = new List();
        list.add("firstItem").add("secondItem").add("thirdItem").setKeepTogether(true).setListSymbol(ListNumberingType.DECIMAL);
        doc.add(list);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepTogetherDivTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherDivTest01.pdf";
        String outFile = destinationFolder + "keepTogetherDivTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph("Test String");

        for (int i = 0; i < 28; i++) {
            doc.add(p);
        }

        Div div = new Div();
        div.add(new Paragraph("first paragraph"));
        div.add(new Paragraph("second paragraph"));
        div.add(new Paragraph("third paragraph"));
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepTogetherMinHeightTest() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherMinHeightTest.pdf";
        String outFile = destinationFolder + "keepTogetherMinHeightTest.pdf";

        PdfWriter writer = new PdfWriter(outFile);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph("Test String");

        for (int i = 0; i < 15; i++) {
            doc.add(p);
        }

        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setMinHeight(500);
        div.setKeepTogether(true);
        div.add(new Paragraph("Hello"));
        doc.add(div);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherDivTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherDivTest02.pdf";
        String outFile = destinationFolder + "keepTogetherDivTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        Rectangle[] columns = {new Rectangle(100, 100, 100, 500), new Rectangle(400, 100, 100, 500)};
        doc.setRenderer(new ColumnDocumentRenderer(doc, columns));
        Div div = new Div();
        doc.add(new Paragraph("first string"));
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("String number " + i));
        }
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherDefaultTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherDefaultTest01.pdf";
        String outFile = destinationFolder + "keepTogetherDefaultTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        Div div = new KeepTogetherDiv();
        doc.add(new Paragraph("first string"));
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("String number " + i));
        }

        doc.add(div);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    private static class KeepTogetherDiv extends Div {
        @Override
        public <T1> T1 getDefaultProperty(int property) {
            if (property == Property.KEEP_TOGETHER) {
                return (T1) (Object) true;
            }
            return super.<T1>getDefaultProperty(property);
        }
    }

    @Test
    @Ignore("DEVSIX-1837: NPE")
    public void keepTogetherInlineDiv01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherInlineDiv01.pdf";
        String outFile = destinationFolder + "keepTogetherInlineDiv01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);


        doc.add(new Paragraph("first string"));

        Div div = new Div().setWidth(200);
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("Part of inline div; string number " + i));
        }
        div.setKeepTogether(true);

        doc.add(new Paragraph().add(div));
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherInlineDiv02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_keepTogetherInlineDiv02.pdf";
        String outFile = destinationFolder + "keepTogetherInlineDiv02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);


        doc.add(new Paragraph("first string"));

        Div div = new Div().setWidth(200);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 130; i++) {
            buffer.append("Part #" + i + " of inline div");
        }
        div.add(new Paragraph(buffer.toString()));
        div.setKeepTogether(true);

        doc.add(new Paragraph().add(div));
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 8)
    })
    public void narrowPageTest01() throws IOException, InterruptedException {
        String testName = "narrowPageTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table tbl = new Table(UnitValue.createPointArray(new float[]{30.0F, 30.0F, 30.0F, 30.0F}));
        tbl.setWidth(120.0F);
        tbl.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
        tbl.setFontSize(8.0F);

        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 4; y++) {
                Cell cell = new Cell();
                cell.add(new Paragraph("row " + x));
                cell.setHeight(10.5f);
                cell.setMaxHeight(10.5f);
                cell.setKeepTogether(true);
                tbl.addCell(cell);
            }
        }

        doc.add(tbl);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    public void narrowPageTest02() throws IOException, InterruptedException {
        String testName = "narrowPageTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new SpecialOddPagesDocumentRenderer(doc, new PageSize(102.0F, 132.0F)));

        Paragraph p = new Paragraph("row 10");
        Div div = new Div();
        div.add(p);
        div.setKeepTogether(true);

        doc.add(new Paragraph("a"));
        doc.add(div);
        doc.add(new AreaBreak());

        div.setHeight(30);
        doc.add(new Paragraph("a"));
        doc.add(div);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        div.deleteOwnProperty(Property.HEIGHT);
        doc.add(div);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        div.setHeight(30);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void narrowPageTest02A() throws IOException, InterruptedException {
        String testName = "narrowPageTest02A.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new SpecialOddPagesDocumentRenderer(doc, new PageSize(102.0F, 102.0F)));

        Paragraph p = new Paragraph("row 10");
        p.setKeepTogether(true);

        doc.add(new Paragraph("a"));
        doc.add(p);
        doc.add(new AreaBreak());

        p.setHeight(30);
        doc.add(new Paragraph("a"));
        doc.add(p);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        p.deleteOwnProperty(Property.HEIGHT);
        doc.add(p);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        p.setHeight(30);
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private static class SpecialOddPagesDocumentRenderer extends DocumentRenderer {
        private PageSize firstPageSize;

        public SpecialOddPagesDocumentRenderer(Document document, PageSize firstPageSize) {
            super(document);
            this.firstPageSize = new PageSize(firstPageSize);
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize newPageSize = null;
            switch (currentPageNumber % 2) {
                case 1:
                    newPageSize = firstPageSize;
                    break;
                case 0:
                default:
                    newPageSize = PageSize.A4;
                    break;
            }
            return super.addNewPage(newPageSize);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void updateHeightTest01() throws IOException, InterruptedException {
        String testName = "updateHeightTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setDefaultPageSize(new PageSize(102.0F, 102.0F));
        Document doc = new Document(pdfDoc);


        Div div = new Div();
        div.setBackgroundColor(ColorConstants.RED);
        div.add(new Paragraph("row"));
        div.add(new Paragraph("row 10"));

        div.setKeepTogether(true);
        div.setHeight(100);

        doc.add(new Paragraph("a"));
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 1),
            @LogMessage(messageTemplate = LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, count = 22),

    })
    //TODO DEVSIX-1977
    public void partialTest01() throws IOException, InterruptedException {
        String testName = "partialTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setDefaultPageSize(PageSize.A7);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setBackgroundColor(ColorConstants.RED);
        div.setKeepTogether(true);
        div.setHeight(200);

        for (int i = 0; i < 30; i++) {
            div.add(new Paragraph("row " + i));
        }

        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void fixedHeightOverflowTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_fixedHeightOverflowTest01.pdf";
        String outFile = destinationFolder + "fixedHeightOverflowTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        pdfDoc.setDefaultPageSize(PageSize.A4);
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("first string"));

        int divHeight = 1000; // specifying height definitely bigger than page height
        // test keep-together processing on height-only overflow for blocks
        Div div = new Div()
                .setHeight(divHeight)
                .setBorder(new SolidBorder(3));
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }
}
