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
package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AnonymousBox;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class AnonymousBoxTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/AnonymousBoxTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/AnonymousBoxTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void relativeHeightTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "relativeHeightTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_relativeHeightTest.pdf";

        try(PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName))) {
            Document doc = new Document(pdfDocument);

            Div div = new Div();
            div.setHeight(500);

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
            Image image = new Image(xObject, 100);
            image.setHeight(UnitValue.createPercentValue(50));

            AnonymousBox ab = new AnonymousBox();
            ab.add(image);

            div.add(ab);
            doc.add(div);

            doc.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
