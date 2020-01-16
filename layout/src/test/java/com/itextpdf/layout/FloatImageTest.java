/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class FloatImageTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatImageTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatImageTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void floatAllClearNoneImageTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatAllClearNoneImage.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        List<ImagesContainer> images = new ArrayList<>();
        images.add(new ImagesContainer("1", new Image(ImageDataFactory.create(sourceFolder + "1.png")),
                FloatPropertyValue.LEFT, null, ClearPropertyValue.NONE, new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("2", new Image(ImageDataFactory.create(sourceFolder + "2.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.NONE, new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("3", new Image(ImageDataFactory.create(sourceFolder + "3.png")),
                FloatPropertyValue.NONE, HorizontalAlignment.CENTER, ClearPropertyValue.NONE,
                new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("4", new Image(ImageDataFactory.create(sourceFolder + "4.png")),
                FloatPropertyValue.LEFT, null, ClearPropertyValue.NONE, new UnitValue(UnitValue.POINT, 200f)));

        addFloatingImagesAndText(document, images);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatAllClearNoneImage.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatAllClearNoneImage.pdf"));
    }

    @Test
    public void floatAllClearBothImageTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatAllClearBothImage.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        List<ImagesContainer> images = new ArrayList<>();
        images.add(new ImagesContainer("1", new Image(ImageDataFactory.create(sourceFolder + "1.png")),
                FloatPropertyValue.LEFT, null, ClearPropertyValue.BOTH, new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("2", new Image(ImageDataFactory.create(sourceFolder + "2.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.BOTH, new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("3", new Image(ImageDataFactory.create(sourceFolder + "3.png")),
                FloatPropertyValue.NONE, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH,
                new UnitValue(UnitValue.POINT, 200f)));
        images.add(new ImagesContainer("4", new Image(ImageDataFactory.create(sourceFolder + "4.png")),
                FloatPropertyValue.LEFT, null, ClearPropertyValue.BOTH, new UnitValue(UnitValue.POINT, 200f)));

        addFloatingImagesAndText(document, images);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatAllClearBothImage.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatAllClearBothImage.pdf"));
    }


    @Test
    public void floatNoneRightClearBothImageTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatNoneRightClearBothImage.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        List<ImagesContainer> images = new ArrayList<>();
        images.add(new ImagesContainer("5", new Image(ImageDataFactory.create(sourceFolder + "5.png")),
                FloatPropertyValue.NONE, HorizontalAlignment.CENTER, ClearPropertyValue.BOTH,
                new UnitValue(UnitValue.PERCENT, 33f)));
        images.add(new ImagesContainer("6", new Image(ImageDataFactory.create(sourceFolder + "6.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.BOTH, new UnitValue(UnitValue.PERCENT, 33f)));
        images.add(new ImagesContainer("7", new Image(ImageDataFactory.create(sourceFolder + "7.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.BOTH, new UnitValue(UnitValue.PERCENT, 33f)));

        addFloatingImagesAndText(document, images);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatNoneRightClearBothImage.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatNoneRightClearBothImage.pdf"));
    }

    @Test
    public void floatNoneRightClearNoneImageTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatNoneRightClearNoneImage.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        List<ImagesContainer> images = new ArrayList<>();
        images.add(new ImagesContainer("5", new Image(ImageDataFactory.create(sourceFolder + "5.png")),
                FloatPropertyValue.NONE, HorizontalAlignment.CENTER, ClearPropertyValue.NONE,
                new UnitValue(UnitValue.PERCENT, 33f)));
        images.add(new ImagesContainer("6", new Image(ImageDataFactory.create(sourceFolder + "6.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.NONE, new UnitValue(UnitValue.PERCENT, 33f)));
        images.add(new ImagesContainer("7", new Image(ImageDataFactory.create(sourceFolder + "7.png")),
                FloatPropertyValue.RIGHT, null, ClearPropertyValue.NONE, new UnitValue(UnitValue.PERCENT, 33f)));

        addFloatingImagesAndText(document, images);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatNoneRightClearNoneImage.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatNoneRightClearNoneImage.pdf"));
    }

    private static void addFloatingImagesAndText(Document document, List<ImagesContainer> images) {
        document.add(new Paragraph("Images followed by two paragraphs.\n" + "Image properties: "));

        for (int i = 0; i < images.size(); i++) {
            document.add(new Paragraph(images.get(i).toString()));
        }

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i).img;
            image.setBorder(new SolidBorder(1f));
            image.setWidth(images.get(i).width);
            image.setProperty(Property.CLEAR, images.get(i).clearPropertyValue);
            image.setHorizontalAlignment(images.get(i).horizontalAlignment);
            image.setProperty(Property.FLOAT, images.get(i).floatPropertyValue);
            document.add(image);
        }

        document.add(
                new Paragraph(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor..."));
        document.add(new Paragraph(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
                        + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                        + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex "
                        + "ea commodo consequat. Duis aute irure dolor in reprehenderit in "
                        + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur"
                        + " sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt"
                        + " mollit anim id est laborum.\n"
                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod"
                        + " tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                        + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea "
                        + "commodo consequat. Duis aute irure dolor in ......"));
    }

    private static class ImagesContainer {

        String imageName;
        Image img;
        FloatPropertyValue floatPropertyValue;
        HorizontalAlignment horizontalAlignment;
        ClearPropertyValue clearPropertyValue;
        UnitValue width;

        public ImagesContainer(String imageName, Image img, FloatPropertyValue floatPropertyValue,
                HorizontalAlignment horizontalAlignment, ClearPropertyValue clearPropertyValue, UnitValue width) {
            this.imageName = imageName;
            this.img = img;
            this.floatPropertyValue = floatPropertyValue;
            this.horizontalAlignment = horizontalAlignment;
            this.clearPropertyValue = clearPropertyValue;
            this.width = width;
        }

        public String toString() {
            String hAlignString;
            if (horizontalAlignment == null) {
                hAlignString = "null";
            } else {
                hAlignString = horizontalAlignment.toString();
            }
            return MessageFormatUtil
                    .format("Image={0}, float={1}, horiz_align={2}, clear={3}, width={4}", imageName,
                            floatPropertyValue,
                            hAlignString, clearPropertyValue, width);
        }
    }
}
