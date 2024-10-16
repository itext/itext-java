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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class CollapsingMarginsTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/CollapsingMarginsTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/layout/CollapsingMarginsTest/";

    private static final String TEXT_BYRON = "When a man hath no freedom to fight for at home,\n" +
            "    Let him combat for that of his neighbours;\n" +
            "Let him think of the glories of Greece and of Rome,\n" +
            "    And get knocked on the head for his labours.\n" +
            "\n" +
            "To do good to Mankind is the chivalrous plan,\n" +
            "    And is always as nobly requited;\n" +
            "Then battle for Freedom wherever you can,\n" +
            "    And, if not shot or hanged, you'll get knighted.";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void collapsingMarginsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 4);

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(TEXT_BYRON);
        for (int i = 0; i < 5; i++) {
            p.add(TEXT_BYRON);
        }

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

        div1.setMarginBottom(20);
        div2.setMarginTop(150);
        div2.setMarginBottom(150);

        Div div = new Div().setMarginTop(20).setMarginBottom(10).setBackgroundColor(new DeviceRgb(78, 151, 205));
        div.add(div1);
        div.add(div2);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 3);

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(TEXT_BYRON);
        for (int i = 0; i < 3; i++) {
            p.add(TEXT_BYRON);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "\n" +
                "To do good to Mankind is the chivalrous plan,\n");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

        div1.setMarginBottom(40);
        div2.setMarginTop(20);
        div2.setMarginBottom(150);

        Div div = new Div().setMarginTop(20).setMarginBottom(10).setBackgroundColor(new DeviceRgb(78, 151, 205));
        div.add(div1);
        div.add(div2);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 3);

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(TEXT_BYRON);
        for (int i = 0; i < 3; i++) {
            p.add(TEXT_BYRON);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "To do good to Mankind is the chivalrous plan,\n");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

        div1.setMarginBottom(80);
        div2.setMarginTop(80);
        div2.setMarginBottom(150);

        doc.add(div1);
        doc.add(div2);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 3);

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(TEXT_BYRON);
        for (int i = 0; i < 3; i++) {
            p.add(TEXT_BYRON);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "To do good to Mankind is the chivalrous plan,\n");
        p.add(new Text("small text").setFontSize(5.1f));
        p.add(
                "\nAnd is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "And, if not shot or hanged, you'll get knighted.");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

        div1.setMarginBottom(80);
        div2.setMarginTop(80);
        div2.setMarginBottom(150);

        doc.add(div1);
        doc.add(div2);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest05() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 2);

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        Paragraph p = new Paragraph(TEXT_BYRON).setBackgroundColor(ColorConstants.YELLOW);
        for (int i = 0; i < 3; i++) {
            p.add(TEXT_BYRON);
        }
        doc.add(p);

        p.setMarginTop(80);
        Div div = new Div();

        div.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));

        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void elementCollapsingMarginsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "elementCollapsingMarginsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_elementCollapsingMarginsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        drawPageBorders(pdfDocument, 1);

        Document doc = new Document(pdfDocument);

        Paragraph markerText = new Paragraph("Margin between this paragraph and next block is expected to be 170pt.")
                .setBackgroundColor(new DeviceRgb(65, 151, 29)); // greenish

        Div div = new Div();
        Paragraph p = new Paragraph(TEXT_BYRON);
        div.add(p).setBackgroundColor(new DeviceRgb(209,247,29)); // yellowish
        div.setProperty(Property.COLLAPSING_MARGINS, true);

        markerText.setMarginBottom(20);
        p.setMarginTop(50);
        div.setMarginTop(150);

        doc.add(markerText);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void drawPageBorders(PdfDocument pdfDocument, int pageNum) {
        for (int i = 1; i <= pageNum; ++i) {
            while (pdfDocument.getNumberOfPages() < i) {
                pdfDocument.addNewPage();
            }
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(i));
            canvas.saveState();
            canvas.setLineDash(5, 10);
            canvas.rectangle(36, 36, 595 - 36 * 2, 842 - 36 * 2);
            canvas.stroke();
            canvas.restoreState();
        }
    }

    @Test
    /* TODO DEVSIX-2901 the exception should not be thrown
       if after DEVSIX-2901 the exception persists,
       change the type of the expected exception to a more specific one to make the test stricter.
    */
    public void columnRendererTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "columnRendererTest.pdf";
        String cmpFileName = sourceFolder + "cmp_columnRendererTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        try (Document doc = new Document(pdfDocument)) {
            doc.setProperty(Property.COLLAPSING_MARGINS, true);

            Paragraph p = new Paragraph();
            for (int i = 0; i < 10; i++) {
                p.add(TEXT_BYRON);
            }

            Div div = new Div().add(p);
            List<Rectangle> areas = new ArrayList<>();
            areas.add(new Rectangle(30, 30, 150, 600));
            areas.add(new Rectangle(200, 30, 150, 600));
            areas.add(new Rectangle(370, 30, 150, 600));
            div.setNextRenderer(new CustomColumnDocumentRenderer(div, areas));

            Assertions.assertThrows(Exception.class, () -> doc.add(div));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static class CustomColumnDocumentRenderer extends DivRenderer {
        private List<Rectangle> areas;

        public CustomColumnDocumentRenderer(Div modelElement, List<Rectangle> areas) {
            super(modelElement);
            this.areas = areas;
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutResult result = super.layout(layoutContext);
            return result;
        }

        @Override
        public List<Rectangle> initElementAreas(LayoutArea area) {
            return areas;
        }

        @Override
        public IRenderer getNextRenderer() {
            return new CustomColumnDocumentRenderer((Div) modelElement, areas);
        }
    }
}
