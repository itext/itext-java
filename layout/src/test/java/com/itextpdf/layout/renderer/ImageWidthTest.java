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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageWidthTest extends ExtendedITextTest {

    private static final double EPSILON = 0.01;

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ImageWidthTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageWidthTest/";
    public static final String imageFolder = "./src/test/resources/com/itextpdf/layout/ImageTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void imageWidthTest01() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "imageWidthTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageWidthTest01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);

        PdfDocument pdfDoc = new PdfDocument(writer);

        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(new Text("First Line")));
        Paragraph p = new Paragraph();
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "Desert.jpg"));
        Image image = new Image(xObject);
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPercentValue(100));
        p.add(image);
        doc.add(p);

        doc.add(new Paragraph(new Text("Second Line")));
        p = new Paragraph();
        xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "itis.jpg"));
        image = new Image(xObject);
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPercentValue(100));
        p.add(image);
        doc.add(p);

        doc.add(new Paragraph(new Text("Third Line")));
        p = new Paragraph();
        xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "Desert.jpg"));
        image = new Image(xObject);
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPercentValue(100));
        image.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(200f));
        p.add(image);
        doc.add(p);

        doc.add(new Paragraph(new Text("Fourth Line")));
        p = new Paragraph();
        xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "itis.jpg"));
        image = new Image(xObject);
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(100));
        p.add(image);
        doc.add(p);
        doc.add(new Paragraph(new Text("Fifth Line")));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageWidthTest02() throws IOException, InterruptedException {

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "Desert.jpg"));
        Image image = new Image(xObject);
        ImageRenderer renderer = new ImageRenderer(image);

        image.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(50));
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
        Assert.assertEquals(50.0, minMaxWidth.getMaxWidth(), EPSILON);
        Assert.assertEquals(0.0, minMaxWidth.getMaxWidth()-minMaxWidth.getMinWidth(), EPSILON);

        image.setProperty(Property.MAX_WIDTH, UnitValue.createPercentValue(50));
        minMaxWidth = renderer.getMinMaxWidth();
        Assert.assertEquals(1024.0, minMaxWidth.getMaxWidth(), EPSILON);
        image.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(100f));
        minMaxWidth = renderer.getMinMaxWidth();
        Assert.assertEquals( 100.0 * 1024.0 / 768.0, minMaxWidth.getMaxWidth(), EPSILON);

        image = new Image(xObject);
        renderer = new ImageRenderer(image);
        image.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(2000));
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(3000));
        minMaxWidth = renderer.getMinMaxWidth();
        Assert.assertEquals(2000.0, minMaxWidth.getMaxWidth(), EPSILON);
        Assert.assertEquals(0.0, minMaxWidth.getMaxWidth() - minMaxWidth.getMinWidth(), EPSILON);
        image.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100f));
        image.setProperty(Property.HEIGHT, UnitValue.createPointValue(100f));
        minMaxWidth = renderer.getMinMaxWidth();
        Assert.assertEquals( 100.0 * 1024.0 / 768.0, minMaxWidth.getMaxWidth(), EPSILON);
    }

}
