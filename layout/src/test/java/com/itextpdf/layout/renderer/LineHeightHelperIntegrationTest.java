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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.RenderingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LineHeightHelperIntegrationTest extends ExtendedITextTest {

    private static final String CMP = "./src/test/resources/com/itextpdf/layout/LineHeightHelperIntegrationTest/";
    private static final String DESTINATION = "./target/test/com/itextpdf/layout/LineHeightHelperTest/";
    private static final String TEXT_SAMPLE = "Effects present letters inquiry no an removed or friends. "
            + "Desire behind latter me though in. Supposing shameless am he engrossed up additions. "
            + "My possible peculiar together to. Desire so better am cannot he up before points. "
            + "Remember mistaken opinions it pleasure of debating. "
            + "Court front maids forty if aware their at. Chicken use are pressed removed.";

    @BeforeClass
    public static void createDestFolder() {
        createDestinationFolder(DESTINATION);
    }

    @Test
    public void courierTest() throws IOException, InterruptedException {
        String name =  "courierTest.pdf";
        String cmpPdf = CMP + "cmp_" + name;
        String outPdf = DESTINATION + name;
        testFont(PdfFontFactory.createFont(StandardFonts.COURIER), outPdf);
        new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION);
    }

    @Test
    public void helveticaTest() throws IOException, InterruptedException {
        String name =  "helveticaTest.pdf";
        String cmpPdf = CMP + "cmp_" + name;
        String outPdf = DESTINATION + name;
        testFont(PdfFontFactory.createFont(StandardFonts.HELVETICA), outPdf);
        new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION);
    }

    @Test
    public void timesRomanTest() throws IOException, InterruptedException {
        String name =  "timesRomanTest.pdf";
        String cmpPdf = CMP + "cmp_" + name;
        String outPdf = DESTINATION + name;
        testFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), outPdf);
        new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION);
    }

    private void testFont(PdfFont font, String outPdf) throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));
        document.setFont(font);
        Paragraph paragraph = new Paragraph(TEXT_SAMPLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        document.add(paragraph);
        document.close();
    }
}
