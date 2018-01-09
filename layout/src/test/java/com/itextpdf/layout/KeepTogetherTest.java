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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.ListNumberingType;
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


}
