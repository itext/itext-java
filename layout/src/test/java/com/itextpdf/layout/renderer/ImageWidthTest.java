/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ImageWidthTest extends ExtendedITextTest {

    private static final double EPSILON = 0.01;

    public static final String destinationFolder = TestUtil.getOutputPath() + "/layout/ImageWidthTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ImageWidthTest/";
    public static final String imageFolder = "./src/test/resources/com/itextpdf/layout/ImageTest/";

    @BeforeAll
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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageWidthTest02() throws IOException {

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(imageFolder + "Desert.jpg"));
        Image image = new Image(xObject);
        ImageRenderer renderer = new ImageRenderer(image);

        image.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(50));
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
        Assertions.assertEquals(50.0, minMaxWidth.getMaxWidth(), EPSILON);
        Assertions.assertEquals(0.0, minMaxWidth.getMaxWidth()-minMaxWidth.getMinWidth(), EPSILON);

        image.setProperty(Property.MAX_WIDTH, UnitValue.createPercentValue(50));
        minMaxWidth = renderer.getMinMaxWidth();
        Assertions.assertEquals(1024.0, minMaxWidth.getMaxWidth(), EPSILON);
        image.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(100f));
        minMaxWidth = renderer.getMinMaxWidth();
        Assertions.assertEquals( 100.0 * 1024.0 / 768.0, minMaxWidth.getMaxWidth(), EPSILON);

        image = new Image(xObject);
        renderer = new ImageRenderer(image);
        image.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(2000));
        image.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(3000));
        minMaxWidth = renderer.getMinMaxWidth();
        Assertions.assertEquals(2000.0, minMaxWidth.getMaxWidth(), EPSILON);
        Assertions.assertEquals(0.0, minMaxWidth.getMaxWidth() - minMaxWidth.getMinWidth(), EPSILON);
        image.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue(100f));
        image.setProperty(Property.HEIGHT, UnitValue.createPointValue(100f));
        minMaxWidth = renderer.getMinMaxWidth();
        Assertions.assertEquals( 100.0 * 1024.0 / 768.0, minMaxWidth.getMaxWidth(), EPSILON);
    }

}
