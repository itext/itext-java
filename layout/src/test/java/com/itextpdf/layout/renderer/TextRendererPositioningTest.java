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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TextRendererPositioningTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/TextRendererPositioningTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/TextRendererPositioningTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void marginTopTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginTopTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_marginTopTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Text text1 = new Text("Text1");
        text1.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text1.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));

        Text text2 = new Text("Text2");
        text2.setBorder(new SolidBorder(ColorConstants.BLUE, 1));

        Paragraph paragraph = new Paragraph().setBorder(new SolidBorder(1));
        paragraph.add(text1);
        paragraph.add(text2);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginBottomTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginBottomTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_marginBottomTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Text text1 = new Text("Text1");
        text1.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text1.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(40));

        Text text2 = new Text("Text2");
        text2.setBorder(new SolidBorder(ColorConstants.BLUE, 1));

        Paragraph paragraph = new Paragraph().setBorder(new SolidBorder(1));
        paragraph.add(text1);
        paragraph.add(text2);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginTopBottomTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginTopBottomTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_marginTopBottomTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Text text1 = new Text("Text1");
        text1.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text1.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(20));
        text1.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(40));

        Text text2 = new Text("Text2");
        text2.setBorder(new SolidBorder(ColorConstants.BLUE, 1));

        Paragraph paragraph = new Paragraph().setBorder(new SolidBorder(1));
        paragraph.add(text1);
        paragraph.add(text2);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void diffFontSizeTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "diffFontSizeTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_diffFontSizeTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Text text1 = new Text("Text1");
        text1.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text1.setFontSize(50);

        Text text2 = new Text("Text2");
        text2.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text2.setFontSize(80);

        Paragraph paragraph = new Paragraph().setBorder(new SolidBorder(1));
        paragraph.add(text1);
        paragraph.add(text2);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginAndPaddingTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginAndPaddingTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_marginAndPaddingTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        Text text1 = new Text("Text1");
        text1.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        text1.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(10));
        text1.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(10));
        text1.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(20));
        text1.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(20));

        Text text2 = new Text("Text2");
        text2.setBorder(new SolidBorder(ColorConstants.BLUE, 1));

        Paragraph paragraph = new Paragraph().setBorder(new SolidBorder(1));
        paragraph.add(text1);
        paragraph.add(text2);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
