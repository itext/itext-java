/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ParagraphTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ParagraphTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ParagraphTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void cannotPlaceABigChunkOnALineTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cannotPlaceABigChunkOnALineTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_cannotPlaceABigChunkOnALineTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0));

        p.add(new Text("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").setBorder(new SolidBorder(ColorConstants.RED, 0)));
        p.add(new Text("b").setFontSize(100).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cannotPlaceABigChunkOnALineTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cannotPlaceABigChunkOnALineTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_cannotPlaceABigChunkOnALineTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0));
        p.add(new Text("smaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaall").setFontSize(5).setBorder(new SolidBorder(ColorConstants.RED, 0)));
        p.add(new Text("biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig").setFontSize(20).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));

        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void forceOverflowForTextRendererPartialResult01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "forceOverflowForTextRendererPartialResult01.pdf";
        String cmpFileName = sourceFolder + "cmp_forceOverflowForTextRendererPartialResult01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0)).setTextAlignment(TextAlignment.RIGHT);
        for (int i = 0; i < 5; i++) {
            p.add(new Text("aaaaaaaaaaaaaaaaaaaaa" + i).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));
        }

        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES,
                    logLevel = LogLevelConstants.INFO)
    })
    // TODO DEVSIX-4622
    public void wordWasSplitAndItWillFitOntoNextLineTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordWasSplitAndItWillFitOntoNextLineTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_wordWasSplitAndItWillFitOntoNextLineTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph paragraph = new Paragraph()
                .add(new Text("Short").setBackgroundColor(ColorConstants.YELLOW))
                .add(new Text(" Loooooooooooooooooooong").setBackgroundColor(ColorConstants.RED))
                .setWidth(90)
                .setBorder(new SolidBorder(1));

        document.add(paragraph);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
