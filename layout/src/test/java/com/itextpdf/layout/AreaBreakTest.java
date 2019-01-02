/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Category(IntegrationTest.class)
public class AreaBreakTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/AreaBreakTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/AreaBreakTest/";

    @BeforeClass
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest2() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "pageBreak2.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak2.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Hello World!")).add(new AreaBreak(new PageSize(200, 200)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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
