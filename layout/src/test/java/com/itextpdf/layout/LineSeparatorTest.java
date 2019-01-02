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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LineSeparatorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LineSeparatorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LineSeparatorTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void lineSeparatorWidthPercentageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorWidthPercentageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorWidthPercentageTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        ILineDrawer line1 = new SolidLine();
        line1.setColor(ColorConstants.RED);
        ILineDrawer line2 = new SolidLine();
        document.add(new LineSeparator(line1).setWidth(50).setMarginBottom(10));
        document.add(new LineSeparator(line2).setWidth(UnitValue.createPercentValue(50)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lineSeparatorBackgroundTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorBackgroundTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorBackgroundTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        Style style = new Style();
        style.setBackgroundColor(ColorConstants.YELLOW);
        style.setMargin(10);
        document.add(new LineSeparator(new SolidLine()).addStyle(style));

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(ColorConstants.RED));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void rotatedLineSeparatorTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(ColorConstants.RED).setRotationAngle(Math.PI / 2));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotatedLineSeparatorTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest02.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello"));
        document.add(new LineSeparator(new DashedLine()).setWidth(100).setHorizontalAlignment(HorizontalAlignment.CENTER).
                setBackgroundColor(ColorConstants.GREEN).setRotationAngle(Math.PI / 4));
        document.add(new Paragraph("World"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
