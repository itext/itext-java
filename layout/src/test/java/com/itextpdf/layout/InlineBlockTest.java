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

import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class InlineBlockTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/InlineBlockTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/InlineBlockTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void inlineTableTest01() throws IOException, InterruptedException {
        // TODO DEVSIX-1967
        String name = "inlineTableTest01.pdf";
        String outFileName = destinationFolder + name;
        String cmpFileName = sourceFolder + "cmp_" + name;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);


        Paragraph p = new Paragraph().setMultipliedLeading(0);
        p.add(new Paragraph("This is inline table: ").setBorder(new SolidBorder(1)).setMultipliedLeading(0));
        Table inlineTable = new Table(1);
        int commonPadding = 10;
        Cell cell = new Cell();
        Paragraph paragraph = new Paragraph("Cell 1");
        inlineTable.addCell(cell.add(paragraph.setMultipliedLeading(0)).setPadding(commonPadding).setWidth(33));
        Div div = new Div();
        p.add(div.add(inlineTable).setPadding(commonPadding))
                .add(new Paragraph(". Was it fun?").setBorder(new SolidBorder(1)).setMultipliedLeading(0));

        SolidBorder border = new SolidBorder(1);

        doc.add(p);

        Paragraph p1 = new Paragraph().add(p).setBorder(border);
        doc.add(p1);

        Paragraph p2 = new Paragraph().add(p1).setBorder(border);
        doc.add(p2);

        Paragraph p3 = new Paragraph().add(p2).setBorder(border);
        doc.add(p3);

        Paragraph p4 = new Paragraph().add(p3).setBorder(border);
        doc.add(p4);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void deepNestingInlineBlocksTest01() throws IOException, InterruptedException {
        // TODO DEVSIX-1963
        String name = "deepNestingInlineBlocksTest01.pdf";
        String outFileName = destinationFolder + name;
        String cmpFileName = sourceFolder + "cmp_" + name;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Color[] colors = new Color[]{ColorConstants.BLUE, ColorConstants.RED, ColorConstants.LIGHT_GRAY, ColorConstants.ORANGE};
        int w = 60;
        int n = 6;

        Paragraph p = new Paragraph("hello world").setWidth(w);
        for (int i = 0; i < n; ++i) {
            Paragraph currP = new Paragraph().setWidth(i == 0 ? w * 1.1f * 3 : 450 + 5 * i);
            currP.add(p).add(p).add(p).setBorder(new DashedBorder(colors[i % colors.length], 0.5f));
            p = currP;
        }
        long start = SystemUtil.getRelativeTimeMillis();
        doc.add(p);
        System.out.println(SystemUtil.getRelativeTimeMillis() - start); // 606 on local machine (including jvm warming up)

        p = new Paragraph("hello world");
        for (int i = 0; i < n; ++i) {
            Paragraph currP = new Paragraph();
            currP.add(p).add(p).add(p).setBorder(new DashedBorder(colors[i % colors.length], 0.5f));
            p = currP;
        }
        start = SystemUtil.getRelativeTimeMillis();
        doc.add(p);
        System.out.println(SystemUtil.getRelativeTimeMillis() - start); // 4656 on local machine

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
