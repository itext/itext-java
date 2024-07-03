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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.OverflowWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextRendererIntegrationTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TextRendererIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TextRendererIntegrationTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void trimFirstJapaneseCharactersTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "trimFirstJapaneseCharacters.pdf";
        String cmpFileName = sourceFolder + "cmp_trimFirstJapaneseCharacters.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        // UTF-8 encoding table and Unicode characters
        byte[] bUtf16A = {(byte) 0xd8, (byte) 0x40, (byte) 0xdc, (byte) 0x0b};

        // This String is U+2000B
        String strUtf16A = new String(bUtf16A, "UTF-16BE");

        PdfFont font = PdfFontFactory
                .createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", PdfEncodings.IDENTITY_H);

        doc.add(new Paragraph(strUtf16A).setFont(font));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossTwoTextRenderers() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitAcrossTwoTextRenderers.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitAcrossTwoTextRenderers.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text placeho = new Text("placeho")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text lderInte = new Text("lder inte")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text gration = new Text("gration")
                .setFontColor(ColorConstants.ORANGE)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph fullWord = new Paragraph()
                .add(placeho)
                .add(lderInte)
                .add(gration)
                .setWidth(160)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));
        fullWord.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(fullWord);

        fullWord.deleteOwnProperty(Property.RENDERING_MODE);
        doc.add(fullWord);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossMultipleRenderers() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitAcrossMultipleRenderers.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitAcrossMultipleRenderers.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text placeho = new Text("placeho")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text lderIn = new Text("lder-in")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text te = new Text("te")
                .setFontColor(ColorConstants.ORANGE)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text gra = new Text("gra")
                .setFontColor(ColorConstants.GREEN)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text tionLoooooooooooooooong = new Text("tion loooooooooooooooong")
                .setFontColor(ColorConstants.BLUE)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph fullWord = new Paragraph()
                .add(placeho)
                .add(lderIn)
                .add(te)
                .add(gra)
                .add(tionLoooooooooooooooong)
                .setWidth(180)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));
        fullWord.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(fullWord);

        fullWord.deleteOwnProperty(Property.RENDERING_MODE);
        doc.add(fullWord);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordEndsAndFollowingTextRendererStartsWithWhitespaces01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordEndsAndFollowingTextRendererStartsWithWhitespaces01.pdf";
        String cmpFileName = sourceFolder + "cmp_wordEndsAndFollowingTextRendererStartsWithWhitespaces01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text firstText = new Text("firstTextRenderer")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text whitespaces = new Text("  ")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text secondText = new Text("      secondTextRenderer")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph paragraph = new Paragraph()
                .add(firstText)
                .add(whitespaces)
                .add(secondText)
                .setWidth(175)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordEndsAndFollowingTextRendererStartsWithWhitespaces02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordEndsAndFollowingTextRendererStartsWithWhitespaces02.pdf";
        String cmpFileName = sourceFolder + "cmp_wordEndsAndFollowingTextRendererStartsWithWhitespaces02.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text firstText = new Text("firstTextRenderer")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text secondText = new Text("      secondTextRenderer")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph paragraph = new Paragraph()
                .add(firstText)
                .add(secondText)
                .setWidth(175)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void forcedWordSplit() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "forcedWordSplit.pdf";
        String cmpFileName = sourceFolder + "cmp_forcedWordSplit.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text forcedWordSplit = new Text("forcedWordSplit forcedWordSplit")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);

        //ColumnDocumentRenderer is applied here only to fit content on one page
        doc.setRenderer(new ColumnDocumentRenderer(doc, new Rectangle[]
                {new Rectangle(10, 10, 70, 800),
                        new Rectangle(90, 10, 100, 800),
                        new Rectangle(210, 10, 130, 800),
                        new Rectangle(360, 10, 250, 800)}));

        Paragraph paragraph = new Paragraph()
                .add(forcedWordSplit)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));

        for (int i = 50; i <= 150; i += 5) {
            paragraph.setWidth(i);
            doc.add(paragraph);
        }

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossMultipleRenderersOverflowXVisibleWithPrecedingPlaceholder()
            throws IOException, InterruptedException {
        String outFileName = destinationFolder
                + "wordSplitAcrossMultipleRenderersOverflowXVisibleWithPrecedingPlaceholder.pdf";
        String cmpFileName = sourceFolder
                + "cmp_wordSplitAcrossMultipleRenderersOverflowXVisibleWithPrecedingPlaceholder.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text placeholder = new Text("placeholder ")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text oooooooooover = new Text("oooooooooover")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text flooooo = new Text("flooooo")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text ooooowNextWords = new Text("ooooow next words")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph paragraph = new Paragraph()
                .add(placeholder)
                .add(oooooooooover)
                .add(flooooo)
                .add(ooooowNextWords)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1))
                .setWidth(135);

        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossMultipleRenderersOverflowXVisible() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitAcrossMultipleRenderersOverflowXVisible.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitAcrossMultipleRenderersOverflowXVisible.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text oooooooooover = new Text("oooooooooover")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text flooooo = new Text("flooooo")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text ooooowNextWords = new Text("ooooow next words")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);

        Paragraph paragraph = new Paragraph()
                .add(oooooooooover)
                .add(flooooo)
                .add(ooooowNextWords)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(1));

        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        paragraph.setWidth(60);
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitRenderersWithFittingFloatingElementInBetween() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitRenderersWithFittingFloatingElementInBetween.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitRenderersWithFittingFloatingElementInBetween.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text reg = new Text("reg").setFontColor(ColorConstants.LIGHT_GRAY);
        Text ul = new Text("ul").setFontColor(ColorConstants.DARK_GRAY);
        Text aaaaaaaaaaaaaaaaaaaaaaati = new Text("aaaaaaaaaaaaaaaaaaaaaaati").setFontColor(ColorConstants.GRAY);
        Text ngAndRestOfText = new Text("ng overflow text renderers with floating elements between them")
                .setFontColor(ColorConstants.RED);


        Div floatDiv = new Div().setWidth(20).setHeight(60)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(2));
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph p = new Paragraph()
                .add(reg)
                .add(ul)
                .add(floatDiv)
                .add(aaaaaaaaaaaaaaaaaaaaaaati)
                .add(ngAndRestOfText)
                .setBackgroundColor(ColorConstants.CYAN)
                .setWidth(150)
                .setBorder(new SolidBorder(1));

        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(p);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitRenderersWithNotFittingFloatingElementInBetween() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitRenderersWithNotFittingFloatingElementInBetween.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitRenderersWithNotFittingFloatingElementInBetween.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text loooooooooooo = new Text("loooooooooooo")
                .setFontColor(ColorConstants.GREEN);
        Text oooongWords = new Text("oooong words")
                .setFontColor(ColorConstants.BLUE);

        Text floating = new Text("floating")
                .setFontColor(ColorConstants.RED);
        floating.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph paragraph = new Paragraph()
                .add(loooooooooooo)
                .add(floating)
                .add(oooongWords)
                .setBackgroundColor(ColorConstants.YELLOW)
                .setWidth(150)
                .setBorder(new SolidBorder(1));

        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitRenderersWithFittingFloatingInBetweenInSecondWord() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitRenderersWithFittingFloatingInBetweenInSecondWord.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitRenderersWithFittingFloatingInBetweenInSecondWord.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text itsAndSpace = new Text("It's ");
        Text reg = new Text("reg").setFontColor(ColorConstants.LIGHT_GRAY);
        Text ul = new Text("ul").setFontColor(ColorConstants.DARK_GRAY);
        Text aaaaaaaaaaaaaaaaaaaaaaati = new Text("aaaaaaaaaaaaaaaaaaaaaaati").setFontColor(ColorConstants.GRAY);
        Text ngAndRestOfText = new Text("ng overflow text renderers with floating elements between them")
                .setFontColor(ColorConstants.RED);


        Div floatDiv = new Div().setWidth(20).setHeight(60)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(2));
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph p = new Paragraph()
                .add(itsAndSpace)
                .add(reg)
                .add(ul)
                .add(floatDiv)
                .add(aaaaaaaaaaaaaaaaaaaaaaati)
                .add(ngAndRestOfText)
                .setBackgroundColor(ColorConstants.CYAN)
                .setWidth(150)
                .setBorder(new SolidBorder(1));

        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(p);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitRenderersWithOverflowedFloatingElementInBetween() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitRenderersWithOverflowedFloatingElementInBetween.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitRenderersWithOverflowedFloatingElementInBetween.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text reg = new Text("reg").setFontColor(ColorConstants.LIGHT_GRAY);
        Text ul = new Text("ul").setFontColor(ColorConstants.DARK_GRAY);
        Text aaaaaaaaaaaaaaaaaaaaaaati = new Text("aaaaaaaaaaaaaaaaaaaaaaati").setFontColor(ColorConstants.GRAY);
        Text ngAndRestOfText = new Text("ng overflow text renderers with floating elements between them")
                .setFontColor(ColorConstants.RED);


        Div floatDiv = new Div().setWidth(20).setHeight(60)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(2));
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph p = new Paragraph()
                .add(reg)
                .add(ul)
                .add(aaaaaaaaaaaaaaaaaaaaaaati)
                .add(floatDiv)
                .add(ngAndRestOfText)
                .setBackgroundColor(ColorConstants.CYAN)
                .setWidth(150)
                .setBorder(new SolidBorder(1));

        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        doc.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(p);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossMutipleTextRenderersWithinFloatingContainer() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitAcrossMutipleTextRenderersWithinFloatingContainer.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitAcrossMutipleTextRenderersWithinFloatingContainer.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text oooooooooover = new Text("oooooooooover")
                .setFontColor(ColorConstants.LIGHT_GRAY);
        Text flooooo = new Text("flooooo")
                .setFontColor(ColorConstants.GRAY);
        Text ooooowNextWords = new Text("ooooow next words")
                .setFontColor(ColorConstants.DARK_GRAY);

        Paragraph floatingParagraph = new Paragraph()
                .add(oooooooooover)
                .add(flooooo)
                .add(ooooowNextWords)
                .setBackgroundColor(ColorConstants.CYAN)
                .setWidth(150)
                .setBorder(new SolidBorder(1));
        floatingParagraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        floatingParagraph.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Text regularText = new Text("regular words regular words regular words regular words regular words regular " +
                "words regular words regular words regular words");
        Paragraph regularParagraph = new Paragraph(regularText)
                .setBackgroundColor(ColorConstants.MAGENTA);

        Div div = new Div()
                .add(floatingParagraph)
                .add(regularParagraph)
                .setMaxWidth(300)
                .setHeight(300)
                .setBackgroundColor(ColorConstants.YELLOW);

        div.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void wordSplitAcrossRenderersWithPrecedingImageRenderer() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordSplitAcrossRenderersWithPrecedingImageRenderer.pdf";
        String cmpFileName = sourceFolder + "cmp_wordSplitAcrossRenderersWithPrecedingImageRenderer.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "bulb.gif"));
        image.setWidth(30);

        Text loooooooooooo = new Text("loooooooooooo")
                .setFontColor(ColorConstants.GREEN);
        Text oooooooo = new Text("oooooooo")
                .setFontColor(ColorConstants.RED);
        Text oooongWords = new Text("oooong words")
                .setFontColor(ColorConstants.BLUE);

        Paragraph paragraph = new Paragraph()
                .add(image)
                .add(loooooooooooo)
                .add(oooooooo)
                .add(oooongWords)
                .setBackgroundColor(ColorConstants.YELLOW)
                .setWidth(300)
                .setBorder(new SolidBorder(1));

        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        doc.add(paragraph);

        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.DEFAULT_LAYOUT_MODE);

        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    public void minMaxWidthWordSplitAcrossMultipleTextRenderers() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "minMaxWidthWordSplitAcrossMultipleTextRenderers.pdf";
        String cmpFileName = sourceFolder + "cmp_minMaxWidthWordSplitAcrossMultipleTextRenderers.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        doc.setFontSize(20);

        Text wissen = new Text("Wissen")
                .setFontColor(ColorConstants.PINK)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text schaft = new Text("schaft")
                .setFontColor(ColorConstants.MAGENTA)
                .setBackgroundColor(ColorConstants.YELLOW);
        Text ler = new Text("ler is a long German word!")
                .setFontColor(ColorConstants.RED)
                .setBackgroundColor(ColorConstants.YELLOW);

        Image image = new Image(ImageDataFactory.create(sourceFolder + "bulb.gif"));
        image.setWidth(30);
        Paragraph text = new Paragraph()
                .add(wissen)
                .add(schaft)
                .add(ler);

        float[] colWidth = {10, 20, 30, 40, 50};

        Table table = new Table(UnitValue.createPercentArray(colWidth));
        for (int i = 0; i < colWidth.length; i++) {
            table.addCell(new Cell().add(text));
        }

        doc.add(table);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH))
    public void minWidthForWordInMultipleTextRenderersFollowedByFloatTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "minWidthForSpanningWordFollowedByFloat.pdf";
        String cmpFileName = sourceFolder + "cmp_minWidthForSpanningWordFollowedByFloat.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);
        doc.setFontSize(40);

        // add elements to the table in narrow parent div, so table width would be completely based on min-width
        Div narrowDivWithTable = new Div()
                .setBorder(new DashedBorder(ColorConstants.DARK_GRAY, 3))
                .setWidth(10);
        Table table = new Table(1);
        table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));

        Div floatingDiv = new Div();
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        floatingDiv.setWidth(40).setHeight(20).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        Paragraph paragraph = new Paragraph()
                .add(new Text("s"))
                .add(new Text("i"))
                .add(new Text("n"))
                .add(new Text("g"))
                .add(new Text("l"))
                .add(new Text("e"))
                .add(floatingDiv)
                .setBorder(new SolidBorder(1));
        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

        table.addCell(paragraph);
        narrowDivWithTable.add(table);
        doc.add(narrowDivWithTable);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void overflowWrapBreakWordWithOverflowXTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowWrapBreakWordWithOverflowXTest.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowWrapBreakWordWithOverflowXTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);
        doc.setFontSize(40);
        Text text = new Text("wow");
        Paragraph paragraph = new Paragraph()
                .add(text)
                .setBackgroundColor(ColorConstants.YELLOW)
                .setWidth(10)
                .setBorder(new SolidBorder(1));
        paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        paragraph.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.BREAK_WORD);
        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(paragraph);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.GET_NEXT_RENDERER_SHOULD_BE_OVERRIDDEN, count = 3)
    })
    public void customTextRendererShouldOverrideGetNextRendererTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "customTextRendererShouldOverrideGetNextRendererTest.pdf";
        String cmpFileName = sourceFolder + "cmp_customTextRendererShouldOverrideGetNextRendererTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Text text = new Text("If getNextRenderer() is not overridden and text overflows to the next line,"
                + " then customizations are not applied. ");
        text.setNextRenderer(new TextRenderer(text) {
            @Override
            public void draw(DrawContext drawContext) {
                drawContext.getCanvas()
                        .saveState()
                        .setFillColor(ColorConstants.RED)
                        .rectangle(occupiedArea.getBBox())
                        .fill()
                        .restoreState();
                super.draw(drawContext);
            }
        });
        doc.add(new Paragraph(text));

        text = new Text("If getNextRenderer() is overridden and text overflows to the next line, "
                + "then customizations are applied. ");
        text.setNextRenderer(new TextRenderer(text) {
            @Override
            public void draw(DrawContext drawContext) {
                drawContext.getCanvas()
                        .saveState()
                        .setFillColor(ColorConstants.RED)
                        .rectangle(occupiedArea.getBBox())
                        .fill()
                        .restoreState();
                super.draw(drawContext);
            }

            @Override
            public IRenderer getNextRenderer() {
                return new TextRendererWithOverriddenGetNextRenderer((Text) modelElement);
            }
        });
        doc.add(new Paragraph(text));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void nbspCannotBeFitAndIsTheOnlySymbolTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "nbspCannotBeFitAndIsTheOnlySymbolTest.pdf";
        String cmpFileName = sourceFolder + "cmp_nbspCannotBeFitAndIsTheOnlySymbolTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        // No place for any symbol (page width is fully occupied by margins)
        Document doc = new Document(pdfDocument, new PageSize(72, 1000));

        Paragraph paragraph = new Paragraph()
                .add(new Text("\u00A0"));

        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void nbspCannotBeFitAndMakesTheFirstChunkTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "nbspCannotBeFitAndMakesTheFirstChunkTest.pdf";
        String cmpFileName = sourceFolder + "cmp_nbspCannotBeFitAndMakesTheFirstChunkTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        // No place for any symbol (page width is fully occupied by margins)
        Document doc = new Document(pdfDocument, new PageSize(72, 1000));

        Paragraph paragraph = new Paragraph()
                .add(new Text("\u00A0"))
                .add(new Text("SecondChunk"));

        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void nbspCannotBeFitAndIsTheFirstSymbolOfChunkTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "nbspCannotBeFitAndIsTheFirstSymbolOfChunkTest.pdf";
        String cmpFileName = sourceFolder + "cmp_nbspCannotBeFitAndIsTheFirstSymbolOfChunkTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        // No place for any symbol (page width is fully occupied by margins)
        Document doc = new Document(pdfDocument, new PageSize(72, 1000));

        Paragraph paragraph = new Paragraph()
                .add(new Text("\u00A0First"));

        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void nbspCannotBeFitAndIsTheLastSymbolOfFirstChunkTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "nbspCannotBeFitAndIsTheLastSymbolOfFirstChunkTest.pdf";
        String cmpFileName = sourceFolder + "cmp_nbspCannotBeFitAndIsTheLastSymbolOfFirstChunkTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        // No place for the second symbol
        Document doc = new Document(pdfDocument, new PageSize(81, 1000));

        Paragraph paragraph = new Paragraph()
                .add(new Text("H\u00A0"))
                .add(new Text("ello"));

        paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CREATE_COPY_SHOULD_BE_OVERRIDDEN, count = 8)
    })
    public void customTextRendererShouldOverrideCreateCopyTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "customTextRendererShouldOverrideCreateCopyTest.pdf";
        String cmpFileName = sourceFolder + "cmp_customTextRendererShouldOverrideCreateCopyTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        FontProvider fontProvider = new FontProvider();
        Assertions.assertTrue(fontProvider.addFont(fontsFolder + "NotoSans-Regular.ttf"));

        doc.setFontProvider(fontProvider);
        // To trigger font selector related logic one need to apply some font on a document
        doc.setProperty(Property.FONT, new String[] {"SomeFont"});

        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            longTextBuilder.append("Дзень добры, свет! Hallo Welt! ");
        }

        Text text = new Text(longTextBuilder.toString());
        text.setNextRenderer(new TextRenderer(text) {
            @Override
            public void draw(DrawContext drawContext) {
                drawContext.getCanvas()
                        .saveState()
                        .setFillColor(ColorConstants.RED)
                        .rectangle(occupiedArea.getBBox())
                        .fill()
                        .restoreState();
                super.draw(drawContext);
            }

            @Override
            public IRenderer getNextRenderer() {
                return new TextRendererWithOverriddenGetNextRenderer((Text) modelElement);
            }
        });
        doc.add(new Paragraph(text));

        text.setNextRenderer(new TextRendererWithOverriddenGetNextRenderer(text));
        doc.add(new Paragraph(text));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    private static class TextRendererWithOverriddenGetNextRenderer extends TextRenderer {
        public TextRendererWithOverriddenGetNextRenderer(Text textElement) {
            super(textElement);
        }

        protected TextRendererWithOverriddenGetNextRenderer(TextRenderer other) {
            super(other);
        }

        @Override
        public void draw(DrawContext drawContext) {
            drawContext.getCanvas()
                    .saveState()
                    .setFillColor(ColorConstants.RED)
                    .rectangle(occupiedArea.getBBox())
                    .fill()
                    .restoreState();
            super.draw(drawContext);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new TextRendererWithOverriddenGetNextRenderer((Text) modelElement);
        }

        @Override
        protected TextRenderer createCopy(GlyphLine gl, PdfFont font) {
            TextRenderer copy = new TextRendererWithOverriddenGetNextRenderer(this);
            copy.setProcessedGlyphLineAndFont(gl, font);
            return copy;
        }
    }
}
