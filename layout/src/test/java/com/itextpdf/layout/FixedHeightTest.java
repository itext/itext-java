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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FixedHeightTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatAndAlignmentTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatAndAlignmentTest/";

    private static final String textByron =
            "When a man hath no freedom to fight for at home,\n" +
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

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 1),
            // TODO DEVSIX-1977 partial layout result due to fixed height should not contain not layouted kids
            @LogMessage(messageTemplate = IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, count = 6)
    })
    @Test
    public void divWithParagraphsAndFixedPositionTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockWithLimitedHeightAndFixedPositionTest.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithLimitedHeightAndFixedPositionTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div block = new Div();
        block.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        block.setHeight(120);

        for (String line : textByron.split("\n")) {
            Paragraph p = new Paragraph();
            p.add(new Text(line));
            p.setBorder(new SolidBorder(0.5f));
            block.add(p);
        }
        block.setFixedPosition(100, 600, 300);
        doc.add(block);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 1),
            // TODO DEVSIX-1977 partial layout result due to fixed height should not contain not layouted kids
            @LogMessage(messageTemplate = IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, count = 2)
    })
    @Test
    public void listWithFixedPositionTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "listWithFixedPositionTest.pdf";
        String cmpFileName = sourceFolder + "cmp_listWithFixedPositionTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        List list = new List();
        list.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        list.setHeight(120);

        for (String line : textByron.split("\n")) {
            list.add(line);
        }
        list.setFixedPosition(100, 600, 300);
        doc.add(list);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }
}
