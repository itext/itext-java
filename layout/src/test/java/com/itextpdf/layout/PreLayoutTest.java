/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Category(IntegrationTest.class)
public class PreLayoutTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PreLayoutTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/PreLayoutTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void preLayoutTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument, PageSize.Default, false);

        List<Text> pageNumberTexts = new ArrayList<>();
        List<IRenderer> pageNumberRenderers = new ArrayList<>();

        document.setProperty(Property.FONT, PdfFontFactory.createFont(FontConstants.HELVETICA));

        for (int i = 0; i < 200; i++) {
            document.add(new Paragraph("This is just junk text"));
            if (i % 10 == 0) {
                Text pageNumberText = new Text("Page #: {pageNumber}");
                IRenderer renderer = new TextRenderer(pageNumberText);
                pageNumberText.setNextRenderer(renderer);
                pageNumberRenderers.add(renderer);

                Paragraph pageNumberParagraph = new Paragraph().add(pageNumberText);
                pageNumberTexts.add(pageNumberText);
                document.add(pageNumberParagraph);
            }
        }

        for (IRenderer renderer : pageNumberRenderers) {
            String currentData = renderer.toString().replace("{pageNumber}", String.valueOf(renderer.getOccupiedArea().getPageNumber()));
            ((TextRenderer)renderer).setText(currentData);
            ((Text)renderer.getModelElement()).setNextRenderer(renderer);
        }

        document.relayout();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void preLayoutTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDoc, PageSize.Default, false);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1200; i++) {
            text.append("A very long text is here...");
        }
        Paragraph twoColumnParagraph = new Paragraph();
        twoColumnParagraph.setNextRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph));
        Text textElement = new Text(text.toString());
        twoColumnParagraph.add(textElement).setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
        document.add(twoColumnParagraph);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        int paragraphLastPageNumber = -1;
        List<IRenderer> documentChildRenderers = document.getRenderer().getChildRenderers();
        for (int i = documentChildRenderers.size() - 1; i >= 0; i--) {
            if (documentChildRenderers.get(i).getModelElement() == twoColumnParagraph) {
                paragraphLastPageNumber = documentChildRenderers.get(i).getOccupiedArea().getPageNumber();
                break;
            }
        }

        twoColumnParagraph.setNextRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph, paragraphLastPageNumber));
        document.relayout();

        //Close document. Drawing of content is happened on close
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void preLayoutTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest03.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDoc, PageSize.Default, false);
        document.add(new Paragraph("A"));

        IRenderer documentRenderer = document.getRenderer();
        IRenderer paragraphRenderer = documentRenderer.getChildRenderers().get(0);
        IRenderer textRenderer = paragraphRenderer.getChildRenderers().get(0);
        Assert.assertEquals(documentRenderer, paragraphRenderer.getParent());
        Assert.assertEquals(documentRenderer, textRenderer.getRoot());
        Assert.assertNull(documentRenderer.getParent());
        Assert.assertEquals(documentRenderer, documentRenderer.getRoot());
    }

    static class TwoColumnParagraphRenderer extends ParagraphRenderer {

        int oneColumnPage = -1;

        public TwoColumnParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        public TwoColumnParagraphRenderer(Paragraph modelElement, int oneColumnPage) {
            this(modelElement);
            this.oneColumnPage = oneColumnPage;
        }

        @Override
        public List<Rectangle> initElementAreas(LayoutArea area) {
            List<Rectangle> areas = new ArrayList<Rectangle>();
            if (area.getPageNumber() != oneColumnPage) {
                Rectangle firstArea = area.getBBox().clone();
                Rectangle secondArea = area.getBBox().clone();
                firstArea.setWidth(firstArea.getWidth() / 2 - 5);
                secondArea.setX(secondArea.getX() + secondArea.getWidth() / 2 + 5);
                secondArea.setWidth(firstArea.getWidth());
                areas.add(firstArea);
                areas.add(secondArea);
            } else {
                areas.add(area.getBBox());
            }
            return areas;
        }

        @Override
        public ParagraphRenderer getNextRenderer() {
            return new TwoColumnParagraphRenderer((Paragraph) modelElement, oneColumnPage);
        }
    }
}
