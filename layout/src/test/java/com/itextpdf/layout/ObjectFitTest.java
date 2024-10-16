/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.ObjectFit;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ObjectFitTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ObjectFitTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ObjectFitTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_fill.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_fill.pdf";

        generateDocumentWithObjectFit(ObjectFit.FILL, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void coverObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_cover.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_cover.pdf";

        generateDocumentWithObjectFit(ObjectFit.COVER, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void containObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_contain.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_contain.pdf";

        generateDocumentWithObjectFit(ObjectFit.CONTAIN, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void scaleDownObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_scale_down.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_scale_down.pdf";

        generateDocumentWithObjectFit(ObjectFit.SCALE_DOWN, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void noneObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_none.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_none.pdf";

        generateDocumentWithObjectFit(ObjectFit.NONE, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void scaleDownSmallImageObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_scale_down_small_image.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_scale_down_small_image.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {
            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.SCALE_DOWN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void twoCoverObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_two_objects.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_two_objects.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.COVER);

            Image image2 = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            p.add(image2);
            doc.add(p);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void containWithEffectsObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_with_effects.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_with_effects.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setBorder(new SolidBorder(new DeviceGray(0), 5))
                    .setBorderRadius(new BorderRadius(100))
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA),
    })
    // TODO DEVSIX-4286 object-fit property combined with rotation is not processed correctly
    public void containWithRotationObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_with_rotation.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_with_rotation.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setRotationAngle(45)
                    .setBorder(new SolidBorder(new DeviceGray(0), 1))
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void generateDocumentWithObjectFit(ObjectFit objectFit, String outFileName) throws IOException {
        try(
            PdfWriter writer = new PdfWriter(outFileName);
            Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(objectFit);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }
    }
}
