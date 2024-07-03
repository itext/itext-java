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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class BlockRendererTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/BlockRendererTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/BlockRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void applyMinHeightForSpecificDimensionsCausingFloatPrecisionErrorTest() {
        float divHeight = 42.55f;

        Div div = new Div();
        div.setHeight(UnitValue.createPointValue(divHeight));

        float occupiedHeight = 17.981995f;
        float leftHeight = 24.567993f;

        Assertions.assertTrue(occupiedHeight + leftHeight < divHeight);

        BlockRenderer blockRenderer = (BlockRenderer) div.createRendererSubTree();
        blockRenderer.occupiedArea = new LayoutArea(1, new Rectangle(0, 267.9681f, 0, occupiedHeight));
        AbstractRenderer renderer = blockRenderer.applyMinHeight(OverflowPropertyValue.FIT,
                new Rectangle(0, 243.40012f, 0, leftHeight));
        Assertions.assertNull(renderer);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, count = 2,
                    logLevel = LogLevelConstants.ERROR)
    })
    // TODO DEVSIX-6488 all elements should be layouted first in case when parent box should wrap around child boxes
    public void parentBoxWrapAroundChildBoxesTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_parentBoxWrapAroundChildBoxes.pdf";
        String outFile = DESTINATION_FOLDER + "parentBoxWrapAroundChildBoxes.pdf";
        int enoughDivsToOccupyWholePage = 30;
        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Div div = new Div();
        div.setBackgroundColor(ColorConstants.CYAN);
        div.setProperty(Property.POSITION, LayoutPosition.ABSOLUTE);

        Div childDiv = new Div();
        childDiv.add(new Paragraph("ChildDiv"));
        childDiv.setBackgroundColor(ColorConstants.YELLOW);
        childDiv.setWidth(100);

        for (int i = 0; enoughDivsToOccupyWholePage > i; i++) {
            div.add(childDiv);
        }
        Div divThatDoesntFitButItsWidthShouldBeConsidered = new Div();
        divThatDoesntFitButItsWidthShouldBeConsidered.add(new Paragraph("ChildDiv1"));
        divThatDoesntFitButItsWidthShouldBeConsidered.setBackgroundColor(ColorConstants.GREEN);
        divThatDoesntFitButItsWidthShouldBeConsidered.setWidth(200);

        div.add(divThatDoesntFitButItsWidthShouldBeConsidered);
        document.add(div);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void resolveFontTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Div div = new Div();
        div.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN));
        DivRenderer renderer = (DivRenderer) div.getRenderer();
        PdfFont font = renderer.getResolvedFont(pdfDocument);
        Assertions.assertEquals("Times-Roman", font.getFontProgram().getFontNames().getFontName());
    }


    @Test
    public void resolveFontWithPdfDocumentNullTest() {
        Div div = new Div();
        DivRenderer renderer = (DivRenderer) div.getRenderer();
        PdfFont font = renderer.getResolvedFont(null);
        Assertions.assertNull(font);
    }


    @Test
    public void resolveFontFromFontProviderTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Div div = new Div();

        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");
        div.setProperty(Property.FONT_PROVIDER, provider);
        div.setProperty(Property.FONT, new String[] {"courier"});
        DivRenderer renderer = (DivRenderer) div.getRenderer();
        PdfFont font = renderer.getResolvedFont(pdfDocument);
        Assertions.assertEquals("Courier", font.getFontProgram().getFontNames().getFontName());
    }


    @Test
    public void resolveFontFromFontProviderNullTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Div div = new Div();

        div.setProperty(Property.FONT_PROVIDER, null);
        div.setProperty(Property.FONT, new String[] {"courier"});
        DivRenderer renderer = (DivRenderer) div.getRenderer();
        PdfFont font = renderer.getResolvedFont(pdfDocument);
        Assertions.assertEquals("Helvetica", font.getFontProgram().getFontNames().getFontName());
    }

    @Test
    public void resolveFontFromFontProviderNullAndDocNullTest() {
        Div div = new Div();

        div.setProperty(Property.FONT_PROVIDER, null);
        div.setProperty(Property.FONT, new String[] {"courier"});
        DivRenderer renderer = (DivRenderer) div.getRenderer();
        PdfFont font = renderer.getResolvedFont(null);
        Assertions.assertNull(font);
    }

}








