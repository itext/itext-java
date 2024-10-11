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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class OverflowTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/OverflowTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/OverflowTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void textOverflowTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            text.append("This is a waaaaay tooo long text...");
        }

        Paragraph p = new Paragraph(text.toString()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will not overflow").
                setFontSize(19).setFontColor(ColorConstants.RED);
        Text followText = new Text("This is a text which follows overflowed text and will be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Text overflowText = new Text("This is a long-long and large text which will overflow").
                setFontSize(25).setFontColor(ColorConstants.RED);
        Text followText = new Text("This is a text which follows overflowed text and will not be wrapped");

        document.add(new Paragraph().add(overflowText).add(followText));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textOverflowTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOverflowTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_textOverflowTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("ThisIsALongTextWithNoSpacesSoSplittingShouldBeForcedInThisCase").setFontSize(20));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedInlineContentOverflowHiddenTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedInlineContentOverflowHiddenTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_alignedInlineContentOverflowHiddenTest01.pdf";
        String imgPath = sourceFolder + "itis.jpg";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div div = new Div().setHeight(150f).setWidth(150f).setBorder(new SolidBorder(5f));
        div.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

        Image img = new Image(ImageDataFactory.create(imgPath));
        Paragraph p = new Paragraph().setTextAlignment(TextAlignment.CENTER);

        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        document.add(
                div.add(
                        p.add(img)));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedInlineContentOverflowHiddenTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedInlineContentOverflowHiddenTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_alignedInlineContentOverflowHiddenTest02.pdf";
        String imgPath = sourceFolder + "itis.jpg";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Image img = new Image(ImageDataFactory.create(imgPath));
        Paragraph p = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER).setHeight(150f).setWidth(150f).setBorder(new SolidBorder(5f));
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

        img.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        document.add(
                p.add(img));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowHiddenOnCanvasTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowHiddenOnCanvasTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowHiddenOnCanvasTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        PdfPage page = pdfDocument.addNewPage();
        Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize().clone().applyMargins(36, 36, 36, 36, false));

        addParaWithImgSetOverflowX(canvas, OverflowPropertyValue.HIDDEN);

        canvas.close();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowHiddenOnCanvasTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowHiddenOnCanvasTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowHiddenOnCanvasTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        PdfPage page = pdfDocument.addNewPage();
        Canvas canvas = new Canvas(page, page.getPageSize().clone().applyMargins(36, 36, 36, 36, false));

        addParaWithImgSetOverflowX(canvas, OverflowPropertyValue.HIDDEN);

        canvas.close();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowVisibleOnCanvasTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowVisibleOnCanvasTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowVisibleOnCanvasTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        PdfPage page = pdfDocument.addNewPage();
        Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize().clone().applyMargins(36, 36, 36, 36, false));

        addParaWithImgSetOverflowX(canvas, OverflowPropertyValue.VISIBLE);

        canvas.close();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowVisibleOnCanvasTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowVisibleOnCanvasTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowVisibleOnCanvasTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        PdfPage page = pdfDocument.addNewPage();
        Canvas canvas = new Canvas(page, page.getPageSize().clone().applyMargins(36, 36, 36, 36, false));

        addParaWithImgSetOverflowX(canvas, OverflowPropertyValue.VISIBLE);

        canvas.close();
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static void addParaWithImgSetOverflowX(Canvas canvas, OverflowPropertyValue overflowX) throws MalformedURLException {
        String imgPath = sourceFolder + "itis.jpg";
        Image img = new Image(ImageDataFactory.create(imgPath));
        Paragraph p = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER).setHeight(150f).setWidth(150f).setBorder(new SolidBorder(5f));
        p.setProperty(Property.OVERFLOW_X, overflowX);
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);

        img.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        img.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        canvas.add(
                p.add(img));
    }

    @Test
    public void forcedPlacementTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "forcedPlacementTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_forcedPlacementTest01.pdf";
        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));

        String text = "Text that is not fitting into single line, but requires several of them. " +
                "It should be repeated twice and all of it should be shown in the document. ";

        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(750).setWidth(600);
        div.add(img);
        div.add(new Paragraph(text + text));

        // Warning! Property.FORCED_PLACEMENT is for internal usage only!
        // It is highly advised not to use it unless you know what you are doing.
        // It is used here for specific testing purposes.
        div.setProperty(Property.FORCED_PLACEMENT, true);

        document.add(div);
        document.close();

        // TODO DEVSIX-1655: text might be lost later in the element if previously forced placement was applied.
        // This test is really artificial in fact, since FORCED_PLACEMENT is set explicitly. Even though at the moment
        // of test creation such situation in fact really happens during elements layout.

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED))
    public void forcedPlacementTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "forcedPlacementTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_forcedPlacementTest02.pdf";
        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));

        String text = "Text that is not fitting into single line, but requires several of them. " +
                "It should be repeated twice and all of it should be shown in the document. ";

        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.MAGENTA, 2));

        Image img = new Image(ImageDataFactory.create(sourceFolder + "itis.jpg")).setHeight(750).setWidth(600);
        div.add(img);
        div.add(new Paragraph().add(text).add(text));

        // Warning! Property.FORCED_PLACEMENT is for internal usage only!
        // It is highly advised not to use it unless you know what you are doing.
        // It is used here for specific testing purposes.
        div.setProperty(Property.FORCED_PLACEMENT, true);

        document.add(div);
        document.close();

        // TODO DEVSIX-1655: text might be lost later in the element if previously forced placement was applied.
        // This test is really artificial in fact, since FORCED_PLACEMENT is set explicitly. Even though at the moment
        // of test creation such situation in fact really happens during elements layout

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
