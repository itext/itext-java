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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OverflowTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/OverflowTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/OverflowTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void textOverflowTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        Paragraph p = new Paragraph(text.toString()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will not overflow").
                setFontSize(19).setFontColor(ColorConstants.RED);
        Text followText = new Text("This is a text which follows overflowed text and will be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will overflow").
                setFontSize(25).setFontColor(ColorConstants.RED);
        Text followText = new Text("This is a text which follows overflowed text and will not be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("ThisIsALongTextWithNoSpacesSoSplittingShouldBeForcedInThisCase").setFontSize(20));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedInlineContentOverflowHiddenTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedInlineContentOverflowHiddenTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_alignedInlineContentOverflowHiddenTest01.pdf";
        String imgPath = sourceFolder + "itis.jpg";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div div = new Div().setHeight(150f).setWidth(150f).setBorder(new SolidBorder(5f));
        div.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

        Image img = new Image(ImageDataFactory.create(imgPath));
        Paragraph p = new Paragraph().setTextAlignment(TextAlignment.CENTER);

        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        document.add(
                div.add(
                        p.add(img)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedInlineContentOverflowHiddenTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedInlineContentOverflowHiddenTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_alignedInlineContentOverflowHiddenTest02.pdf";
        String imgPath = sourceFolder + "itis.jpg";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Image img = new Image(ImageDataFactory.create(imgPath));
        Paragraph p = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER).setHeight(150f).setWidth(150f).setBorder(new SolidBorder(5f));
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

        img.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        document.add(
                p.add(img));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
