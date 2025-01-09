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
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class FloatExampleTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatExampleTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatExampleTest/";
    private static final Color imageBorderColor = ColorConstants.LIGHT_GRAY;
    private static final float BORDER_MARGIN = 5f;
    private static final float IMAGE_BORDER_WIDTH = 15f;
    private static final float DIV_BORDER_WIDTH = 1f;


    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void floatMaxWidthTest01() throws IOException, InterruptedException {
        /* This test illustrate behaviour of images with different width and mas_width properties, that have
         there is text paragraph below image,
         shown examples, wrapped and not wrapped in divs
         Divs have property CLEAR = BOTH, and different values of FLOAT
         */
        String cmpFileName = sourceFolder + "cmp_floatMaxWidthTest01.pdf";
        String outFile = destinationFolder + "floatMaxWidthTest01.pdf";
        // defined range is 0..3
        int firstImage = 0;
        int lastImage = 1;

        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(outFile);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);
        pdf.setTagged();

        // divWidthProperty, divWidth are n/a when not wrapping image in a div
        document.add(new Paragraph("IMAGE IS NOT WRAPPED IN A DIV.\n"));
        document.add(new Paragraph("Actual width of image -- no explicit width, no max.\n"));
        addContent(document, false, 0, null, 0, null,
                ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Width < actual width.\n"));
        addContent(document, false, Property.WIDTH, new UnitValue(UnitValue.PERCENT, 30f),
                0, null, ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Max width < actual width.\n"));
        addContent(document, false, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 30f),
                0, null, ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Max width > actual width.\n"));
        addContent(document, false, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 60f),
                0, null, ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        // Image wrapped in div
        document.add(new Paragraph("IMAGE IS WRAPPED IN A DIV.\n"));
        // Width of Paragraph inside Div if width of parent
        document.add(new Paragraph("No explicit width or max: Non-floating text width is parent width.\n"));
        addContent(document, true, 0, null, 0, null,
                ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Bug: Non-floating text width is parent width (limited by max).\n"));

        // 100% would require forced placement, since border box has width and is not included in 100% width
        addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 80f),
                Property.WIDTH, new UnitValue(UnitValue.PERCENT, 30f), ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Max width < actual width.\n"));

        // 100% would require forced placement, since border box has width and is not included in 100% width
        addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 80f),
                Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 30f), ClearPropertyValue.BOTH, firstImage, lastImage);
        document.add(new AreaBreak());

        document.add(new Paragraph("Bug: Non-floating text width is parent width (limited by max).\nMax width > actual width.\n"));

        // 100% would require forced placement, since border box has width and is not included in 100% width
        addContent(document, true, Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 80f),
                Property.MAX_WIDTH, new UnitValue(UnitValue.PERCENT, 60f), ClearPropertyValue.BOTH, firstImage, lastImage);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder,
                "diff01_"));
    }

    @Test
    public void floatMaxWidthTest02() throws IOException, InterruptedException {
        /* This test illustrate behaviour of images, wrapped in Div containers, that have paragraph below image
         Divs have property CLEAR = BOTH, and different values of FLOAT
         Text in divs has HorizontalAlignment.CENTER
         */
        String cmpFileName = sourceFolder + "cmp_floatMaxWidthTest02.pdf";
        String outFile = destinationFolder + "floatMaxWidthTest02.pdf";

        // defined range is 0..3
        int firstImage = 0;
        int lastImage = 2;

        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(outFile);

        //Initialize PDF document with non-default size
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new PageSize(new Rectangle(537, 800)));

        // Initialize document
        Document document = new Document(pdf);
        pdf.setTagged();

        document.add(new Paragraph("IMAGE IS WRAPPED IN A DIV.\n"));
        document.add(new Paragraph("No explicit width or max: Non-floating text width is parent width.\n"));
        addContent(document, true, 0, null, 0, null,
                ClearPropertyValue.BOTH, firstImage, lastImage);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder,
                "diff01_"));
    }


    private void addContent(Document document, boolean wrapImages, int imageWidthProperty, UnitValue imageWidth,
                            int divWidthProperty, UnitValue divWidth, ClearPropertyValue clearValue,
                            int firstImage, int lastImage) throws MalformedURLException {
        ImageProperties[] images = new ImageProperties[4];
        images[0] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER);
        images[1] = new ImageProperties(FloatPropertyValue.RIGHT, clearValue, HorizontalAlignment.CENTER);
        images[2] = new ImageProperties(FloatPropertyValue.LEFT, clearValue, HorizontalAlignment.CENTER);
        images[3] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER);
        Paragraph paragraph = new Paragraph()
                .add("Four images followed by two paragraphs.\n");
        if (wrapImages) {
            String s = "Each image is wrapped in a div.\n";
            s += "All divs specify CLEAR = " + clearValue;
            if (divWidthProperty > 0)
                s += ", " + ((divWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + "= " + divWidth;
            if (imageWidthProperty > 0)
                s += ".\nAll images specify " + ((imageWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + " = " + imageWidth;
            paragraph.add(s + ".\n");
        } else {
            String s = "All images specify CLEAR = " + clearValue;
            if (imageWidthProperty > 0)
                s += ", " + ((imageWidthProperty == Property.WIDTH) ? "WIDTH" : "MAX_WIDTH") + "= " + imageWidth;
            paragraph.add(s + ".\n");
        }
        for (int i = firstImage; i <= lastImage; i++) {
            paragraph.add((wrapImages ? "Div" : "Image") + " " + (i) + ": " + images[i] + "\n");
        }
        document.add(paragraph);

        for (int i = firstImage; i <= lastImage; i++) {
            int pictNumber = i + 1;
            Image image = new Image(ImageDataFactory.create(sourceFolder + pictNumber + ".png"))
                    .setBorder(new SolidBorder(imageBorderColor, IMAGE_BORDER_WIDTH))
                    .setHorizontalAlignment(images[i].horizontalAlignment);
            if (wrapImages) {
                Div div = new Div()
                        .setBorder(new SolidBorder(DIV_BORDER_WIDTH))
                        .setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
                div.setHorizontalAlignment(images[i].horizontalAlignment);
                div.setProperty(Property.CLEAR, images[i].clearPropertyValue);
                div.setProperty(Property.FLOAT, images[i].floatPropertyValue);
                if (divWidthProperty > 0)
                    div.setProperty(divWidthProperty, divWidth);
                if (imageWidthProperty > 0)
                    image.setProperty(imageWidthProperty, imageWidth);
                div.add(image);
                div.add(new Paragraph("Figure for Div" + i + ": This is longer text that wraps This is longer text that wraps")
                        .setTextAlignment(TextAlignment.CENTER)).simulateBold();
                document.add(div);
            } else {
                image.setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
                image.setProperty(Property.CLEAR, images[i].clearPropertyValue);
                image.setProperty(Property.FLOAT, images[i].floatPropertyValue);
                if (imageWidthProperty > 0)
                    image.setProperty(imageWidthProperty, imageWidth);
                document.add(image);
            }
        }

        document.add(new Paragraph("The following outline is provided as an over-view of and topical guide to Zambia:"));
        document.add(new Paragraph("Zambia – landlocked sovereign country located in Southern Africa.[1] Zambia has been inhabited for thousands of years by hunter-gatherers and migrating tribes. After sporadic visits by European explorers starting in the 18th century, Zambia was gradually claimed and occupied by the British as protectorate of Northern Rhodesia towards the end of the nineteenth century. On 24 October 1964, the protectorate gained independence with the new name of Zambia, derived from the Zam-bezi river which flows through the country. After independence the country moved towards a system of one party rule with Kenneth Kaunda as president. Kaunda dominated Zambian politics until multiparty elections were held in 1991."));

    }

    private class ImageProperties {
        FloatPropertyValue floatPropertyValue;
        ClearPropertyValue clearPropertyValue;
        HorizontalAlignment horizontalAlignment;

        ImageProperties(FloatPropertyValue floatPropertyValue, ClearPropertyValue clearPropertyValue,
                        HorizontalAlignment horizontalAlignment) {
            this.floatPropertyValue = floatPropertyValue;
            this.clearPropertyValue = clearPropertyValue;
            this.horizontalAlignment = horizontalAlignment;
        }

        public String toString() {
            return "float=" + floatPropertyValue + ", clear=" + clearPropertyValue + ", horiz_align=" + horizontalAlignment;
        }
    }
}
