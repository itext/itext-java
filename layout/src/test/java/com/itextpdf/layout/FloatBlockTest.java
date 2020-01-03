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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class FloatBlockTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatBlockTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatBlockTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void floatImageInDivClearNoneTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatImageInDivClearNone.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        addFloatingImagesInDivs(document, new UnitValue(UnitValue.POINT, 200f), ClearPropertyValue.NONE);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatImageInDivClearNone.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatImageInDivClearNone.pdf"));
    }

    @Test
    public void floatImageInDivClearBothTest() throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String dest = destinationFolder + "floatImageInDivClearBoth.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        pdf.setTagged();

        addFloatingImagesInDivs(document, new UnitValue(UnitValue.POINT, 200f), ClearPropertyValue.BOTH);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(dest, sourceFolder + "cmp_floatImageInDivClearBoth.pdf", destinationFolder));
        Assert.assertNull(
                new CompareTool().compareTagStructures(dest, sourceFolder + "cmp_floatImageInDivClearBoth.pdf"));
    }

    @Test
    public void floatImageDifferentSizeInDivTest()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String cmpFileName = sourceFolder + "cmp_floatImageDifferentSizeInDiv.pdf";
        String outFile = destinationFolder + "floatImageInDiv.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);
        pdfDoc.setTagged();

        UnitValue width = new UnitValue(UnitValue.PERCENT, 33f);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "5.png"));
        image
                .setBorder(new SolidBorder(1f))
                .setWidth(width)
                .setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Div div = new Div();
        div.setBorder(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        div.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        div.add(image);
        doc.add(div);

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "4.png"));
        image1
                .setBorder(new SolidBorder(1f))
                .setWidth(width)
                .setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Div div1 = new Div();
        div1.setBorder(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        div1.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
        div1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        div1.add(image1);
        doc.add(div1);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder));
        Assert.assertNull(new CompareTool().compareTagStructures(outFile, cmpFileName));
    }

    private static void addFloatingImagesInDivs(Document document, UnitValue width, ClearPropertyValue clearValue)
            throws MalformedURLException {
        List<ImageProperties> imagePropertiesList = new ArrayList<>();
        imagePropertiesList.add(new ImageProperties(FloatPropertyValue.LEFT, clearValue, null, width));
        imagePropertiesList.add(new ImageProperties(FloatPropertyValue.RIGHT, clearValue, null, width));
        imagePropertiesList
                .add(new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER, width));
        imagePropertiesList.add(new ImageProperties(FloatPropertyValue.LEFT, clearValue, null, width));

        document.add(new Paragraph(
                "Four images followed by two paragraphs. All images are wrapped in Divs.\n" +
                        "All images specify WIDTH = " + width));

        for (int i = 0; i < imagePropertiesList.size(); i++) {
            document.add(new Paragraph("Image " + (i + 1) + ": " + imagePropertiesList.get(i)));
        }

        for (int i = 0; i < imagePropertiesList.size(); i++) {
            Image image = new Image(ImageDataFactory.create(MessageFormatUtil.format(sourceFolder + "{0}.png", i + 1)));
            image.setBorder(new SolidBorder(1f));
            image.setWidth(width);

            Div div = new Div();
            div.setProperty(Property.CLEAR, clearValue);
            div.setProperty(Property.FLOAT, imagePropertiesList.get(i).floatPropertyValue);
            div.add(image);
            document.add(div);
        }

        document.add(new Paragraph(
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

    private static class ImageProperties {

        FloatPropertyValue floatPropertyValue;
        ClearPropertyValue clearPropertyValue;
        HorizontalAlignment horizontalAlignment;
        UnitValue width;

        public ImageProperties(FloatPropertyValue floatPropertyValue, ClearPropertyValue clearPropertyValue,
                HorizontalAlignment horizontalAlignment, UnitValue width) {
            this.floatPropertyValue = floatPropertyValue;
            this.clearPropertyValue = clearPropertyValue;
            this.horizontalAlignment = horizontalAlignment;
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
                    .format("float={0} clear={1} horiz_align={2}", floatPropertyValue,
                            clearPropertyValue, hAlignString);
        }
    }
}
