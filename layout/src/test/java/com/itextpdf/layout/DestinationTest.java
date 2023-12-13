/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import com.itextpdf.io.util.MessageFormatUtil;

@Category(IntegrationTest.class)
public class DestinationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/DestinationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/DestinationTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void destinationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "destinationTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_destinationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        Text text = new Text(MessageFormatUtil.format("Page {0}", 10));
        text.setProperty(Property.DESTINATION, "p10");
        doc.add(new Paragraph(text).setFixedPosition(1, 549, 742, 40).setMargin(0));
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
