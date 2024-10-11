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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class PreLayoutTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PreLayoutTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/PreLayoutTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void preLayoutTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName)).setTagged();

        Document document = new Document(pdfDocument, PageSize.DEFAULT, false);

        List<Text> pageNumberTexts = new ArrayList<>();
        List<IRenderer> pageNumberRenderers = new ArrayList<>();

        document.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.HELVETICA));

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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void preLayoutTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "preLayoutTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_preLayoutTest02.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDoc, PageSize.DEFAULT, false);

        document.add(new Paragraph("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1200; i++) {
            text.append("A very long text is here...");
        }
        Paragraph twoColumnParagraph = new Paragraph();
        twoColumnParagraph.setNextRenderer(new TwoColumnParagraphRenderer(twoColumnParagraph));
        Text textElement = new Text(text.toString());
        twoColumnParagraph.add(textElement).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
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

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void columnDocumentRendererRelayoutTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "columnDocumentRendererRelayoutTest.pdf";
        String cmpFileName = sourceFolder + "cmp_columnDocumentRendererRelayoutTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName)).setTagged();

        Document document = new Document(pdfDoc, PageSize.DEFAULT, false);
        Rectangle column1 = new Rectangle(40, 40, 200, 720);
        Rectangle column2 = new Rectangle(300, 40, 200, 720);
        document.setRenderer(new ColumnDocumentRenderer(document, false, new Rectangle[] {column1, column2}));

        String text = "The series continues with Harry Potter and the Chamber of Secrets, describing Harry's second year at Hogwarts. He and his friends investigate a 50-year-old mystery that appears uncannily related to recent sinister events at the school. Ron's younger sister, Ginny Weasley, enrols in her first year at Hogwarts, and finds an old notebook which turns out to be a previous student's diary, Tom Marvolo Riddle, who later turns out to be Voldemort. The memory of Tom Riddle is inside of the diary and when Ginny begins to confide in the diary Voldemort begins to possess her. Ginny becomes possessed by Voldemort through the diary and unconsciously opens the \"Chamber of Secrets\", unleashing an ancient monster, later revealed to be a basilisk, which begins attacking students at Hogwarts. The novel delves into the history of Hogwarts and a legend revolving around the Chamber that soon frightens everyone in the school. The book also introduces a new Defence Against the Dark Arts teacher, Gilderoy Lockhart, a highly cheerful, self-conceited wizard who goes around as if he is the most wonderful person who ever existed, who knows absolutely every single thing there is to know about everything, who later turns out to be a fraud. Harry discovers that prejudice exists in the wizarding world, and learns that Voldemort's reign of terror was often directed at wizards who were descended from muggles. Harry also learns that his ability to speak the snake language Parseltongue is rare and often associated with the Dark Arts. The novel ends after Harry saves Ginny's life by destroying the basilisk and the enchanted diary which has been the source of the problems.";
        for (int i = 0; i < 3; i++) {
            text = text + " " + text;
        }

        document.add(new Paragraph(text));

        document.relayout();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
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
        public IRenderer getNextRenderer() {
            return new TwoColumnParagraphRenderer((Paragraph) modelElement, oneColumnPage);
        }
    }
}
