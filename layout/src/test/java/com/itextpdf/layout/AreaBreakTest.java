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

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag("IntegrationTest")
public class AreaBreakTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/AreaBreakTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/AreaBreakTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pageBreakTest1() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "pageBreak1.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak());

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest2() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "pageBreak2.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak2.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Hello World!")).add(new AreaBreak(new PageSize(200, 200)));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "pageBreak3.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak3.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.setRenderer(new ColumnDocumentRenderer(document, new Rectangle[] {new Rectangle(30, 30, 200, 600), new Rectangle(300, 30, 200, 600)}));

        document.add(new Paragraph("Hello World!")).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("New page hello world"));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lastPageAreaBreakTest01() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "input.pdf";
        String cmpFileName = sourceFolder + "cmp_lastPageAreaBreakTest01.pdf";
        String outFileName = destinationFolder + "lastPageAreaBreakTest01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName), new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("Hello there on the last page!").setFontSize(30).setWidth(200).setMarginTop(250));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lastPageAreaBreakTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_lastPageAreaBreakTest02.pdf";
        String outFileName = destinationFolder + "lastPageAreaBreakTest02.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        pdfDocument.addNewPage();

        Document document = new Document(pdfDocument);

        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("Hello there on the last page!").setFontSize(30).setWidth(200).setMarginTop(250));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lastPageAreaBreakTest03() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_lastPageAreaBreakTest03.pdf";
        String outFileName = destinationFolder + "lastPageAreaBreakTest03.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        pdfDocument.addNewPage();
        pdfDocument.addNewPage();

        Document document = new Document(pdfDocument);

        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("Hello there on the last page!").setFontSize(30).setWidth(200).setMarginTop(250));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lastPageAreaBreakTest04() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "input.pdf";
        String cmpFileName = sourceFolder + "cmp_lastPageAreaBreakTest04.pdf";
        String outFileName = destinationFolder + "lastPageAreaBreakTest04.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName), new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new AreaBreak(AreaBreakType.LAST_PAGE))
                .add(new AreaBreak(AreaBreakType.LAST_PAGE))
                .add(new Paragraph("Hello there on the last page!").setFontSize(30).setWidth(200).setMarginTop(250));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void areaBreakInsideDiv01Test() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "areaBreakInsideDiv01.pdf";
        String cmpFileName = sourceFolder + "cmp_areaBreakInsideDiv01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Div div = new Div().add(new Paragraph("Hello")).add(new AreaBreak()).add(new Paragraph("World"));
        document.add(div);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void areaBreakInsideDiv02Test() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "areaBreakInsideDiv02.pdf";
        String cmpFileName = sourceFolder + "cmp_areaBreakInsideDiv02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Div div = new Div().add(new Paragraph("Hello")).add(new AreaBreak(PageSize.A5)).add(new AreaBreak(PageSize.A6)).add(new Paragraph("World"));
        document.add(div);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void areaBreakInsideDiv03Test() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "areaBreakInsideDiv03.pdf";
        String cmpFileName = sourceFolder + "cmp_areaBreakInsideDiv03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        Div div = new Div().add(new Paragraph("Hello")).add(new AreaBreak()).add(new Paragraph("World"));
        div.setNextRenderer(new DivRendererWithAreas(div));
        document.add(div);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void areaBreakInsideDiv04Test() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "areaBreakInsideDiv04.pdf";
        String cmpFileName = sourceFolder + "cmp_areaBreakInsideDiv04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Div div = new Div().add(new Paragraph("Hello")).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("World"));
        div.setNextRenderer(new DivRendererWithAreas(div));
        document.add(div);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static class DivRendererWithAreas extends DivRenderer {

        public DivRendererWithAreas(Div modelElement) {
            super(modelElement);
        }

        @Override
        public List<Rectangle> initElementAreas(LayoutArea area) {
            return Arrays.asList(new Rectangle(area.getBBox()).setWidth(area.getBBox().getWidth() / 2),
                    new Rectangle(area.getBBox()).setWidth(area.getBBox().getWidth() / 2).moveRight(area.getBBox().getWidth() / 2));
        }

        @Override
        public IRenderer getNextRenderer() {
            return new DivRendererWithAreas((Div) modelElement);
        }
    }

}
