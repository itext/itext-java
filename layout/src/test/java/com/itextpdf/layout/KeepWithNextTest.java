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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class KeepWithNextTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/KeepWithNextTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/KeepWithNextTest/";

    private static final String MIDDLE_TEXT = "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them.";

    private static final String SHORT_TEXT = "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.";

    private static final String LONG_TEXT = "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.\n" +
            "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.\n" +
            "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.\n" +
            "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign.\n" +
            "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n" +
            "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them.\n" +
            "To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device. Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar.\n" +
            "Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n" +
            "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them.\n" +
            "To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device. Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar.\n" +
            "Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device.\n" +
            "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. Save time in Word with new buttons that show up where you need them.\n";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void keepWithNextTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 28; i++) {
            document.add(new Paragraph("dummy"));
        }
        Paragraph title = new Paragraph("THIS IS THE TITLE OF A CHAPTER THAT FITS A PAGE");
        title.setKeepWithNext(true);
        document.add(title);
        for (int i = 0; i < 20; i++) {
            document.add(new Paragraph("content of chapter " + i));
        }

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest02.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 28; i++) {
            document.add(new Paragraph("dummy"));
        }
        Paragraph title = new Paragraph("THIS IS THE TITLE OF A CHAPTER THAT FITS A PAGE");
        title.setKeepWithNext(true);
        document.add(title);
        document.add(new Paragraph(LONG_TEXT));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest03.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 27; i++) {
            document.add(new Paragraph("dummy"));
        }
        Paragraph title = new Paragraph("THIS IS THE TITLE OF A CHAPTER THAT FITS A PAGE");
        title.setKeepWithNext(true);
        document.add(title);
        document.add(new Paragraph(LONG_TEXT));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest04.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 22; i++) {
            document.add(new Paragraph("dummy"));
        }
        document.setProperty(Property.FIRST_LINE_INDENT, 20f);
        Paragraph title = new Paragraph(MIDDLE_TEXT);
        title.setKeepWithNext(true);
        document.add(title);
        document.add(new Paragraph(LONG_TEXT));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest05() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest05.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 22; i++) {
            document.add(new Paragraph("dummy"));
        }
        document.setProperty(Property.FIRST_LINE_INDENT, 20f);
        Paragraph title = new Paragraph(MIDDLE_TEXT);
        title.setKeepTogether(true);
        title.setKeepWithNext(true);
        document.add(title);
        document.add(new Paragraph(LONG_TEXT));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest06() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest06.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        document.add(new Paragraph(LONG_TEXT).setKeepWithNext(true));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest07() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest07.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest07.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        document.setProperty(Property.FIRST_LINE_INDENT, 20f);
        document.add(new Paragraph(LONG_TEXT).setKeepWithNext(true));
        document.add(new Paragraph(LONG_TEXT));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest08() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest08.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest08.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 25; i++) {
            document.add(new Paragraph("dummy"));
        }
        document.add(new Paragraph("Title").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setKeepWithNext(true));
        List list = new List(ListNumberingType.DECIMAL);
        for (int i = 0; i < 10; i++) {
            list.add("item");
        }
        list.setKeepTogether(true);
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest09() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest09.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest09.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 28; i++) {
            document.add(new Paragraph("dummy"));
        }
        document.add(new Paragraph("Title").setFontSize(20).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setKeepWithNext(true));
        List list = new List(ListNumberingType.DECIMAL);
        for (int i = 0; i < 10; i++) {
            list.add("item");
        }
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest10() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest10.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest10.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf, PageSize.A4);

        for (int i = 0; i < 25; i++) {
            document.add(new Paragraph("dummy"));
        }
        document.add(new Paragraph("Title").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setKeepWithNext(true));
        List list = new List(ListNumberingType.DECIMAL);
        for (int i = 0; i < 10; i++) {
            list.add("item");
        }
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void keepWithNextTest11() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "keepWithNextTest11.pdf";
        String cmpFileName = sourceFolder + "cmp_keepWithNextTest11.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        Style style = new Style();
        style.setProperty(Property.KEEP_WITH_NEXT, true);
        document.add(new Paragraph("A").addStyle(style));

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
                .setBorderTop(new SolidBorder(2))
                .setBorderBottom(new SolidBorder(2));
        table.addCell("Body").addHeaderCell("Header");

        document.add(table);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
