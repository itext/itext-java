/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.properties.Transform.SingleTransform;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class TransformTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/TransformTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/TransformTest/";

    private static Iterable<Object[]> transforms() {
        return Arrays.asList(new Object[][] {
                {new Transform(), "noOp"},
                {new Transform().addTransform(new SingleTransform()), "noOp2"},
                {new Transform(1, 0, 0, (float) Math.cos(Math.toRadians(60)),
                        UnitValue.createPointValue(0), UnitValue.createPointValue(0)), "manualRotateX"},
                {new Transform().translate(30, 0)
                        .translate(UnitValue.createPercentValue(0), UnitValue.createPercentValue(30)), "translate"},
                {new Transform().rotate((float) Math.PI / 4), "rotateCenter"},
                {new Transform().rotate((float) -Math.PI / 4), "rotateClockWiseCenter"},
                {new Transform().rotate((float) Math.PI / 4,
                        UnitValue.createPercentValue(-50), UnitValue.createPercentValue(-50)), "rotateBottomLeft"},
                {new Transform().rotate((float) Math.PI / 4,
                        UnitValue.createPercentValue(50), UnitValue.createPercentValue(50)), "rotateTopRight"},
                {new Transform().rotate((float) Math.PI / 4, -100, 56), "rotateImageTopLeft"},
                {new Transform().scaleX(2), "scaleX"},
                {new Transform().scaleX(0), "scaleX0"},
                {new Transform().scaleX(0.5f).scaleY(0.5f), "scaleY"},
                {new Transform().scaleX(-0.5f).scaleY(0.5f), "scaleYNegative"},
                {new Transform().skewX((float) Math.PI / 4), "skewX"},
                {new Transform().skewY((float) -Math.PI / 4), "skewY"},
                {new Transform().simulateRotateX((float) Math.PI * 2 / 3), "rotateX"},
                {new Transform().simulateRotateY((float) Math.PI / 3), "rotateY"},
                {new Transform().simulateRotateX((float) Math.PI), "rotateX180"},
                {new Transform().simulateRotateY((float) Math.PI), "rotateY180"},
                {new Transform().simulateRotateX((float) Math.PI * 2), "rotateX360"},
                {new Transform().simulateRotateY((float) Math.PI * 2), "rotateY360"},
                {new Transform().simulateRotateX((float) Math.PI / 2), "rotateX90"},
                {new Transform().simulateRotateY((float) Math.PI / 2), "rotateY90"}
        });
    }

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("transforms")
    public void commonTransformTest(Transform transform, String fileName) throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph(TestResourceUtil.getByronStanza()));

            doc.add(new Paragraph(TestResourceUtil.getByronStanza())
                    .setBackgroundColor(ColorConstants.GREEN).setTransform(transform));

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
            Image image = new Image(xObject, 200);
            image.setTransform(transform);
            doc.add(image);

            doc.add(new Paragraph(TestResourceUtil.getByronStanza()));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void transformStyleTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "transformStyle.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_transformStyle.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document doc = new Document(pdfDoc)) {
            doc.add(new Paragraph(TestResourceUtil.getByronStanza()));

            Table table = new Table(3);
            for (int i = 1; i <= 12; i++) {
                table.addCell(Integer.toString(i));
            }
            Style style = new Style().setTransform(new Transform().rotate(0.7f));
            table.addStyle(style);
            doc.add(table);

            doc.add(new Paragraph(TestResourceUtil.getByronStanza()));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }
}
