/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.forms.form.element;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.FileOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class ButtonColorTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ButtonColorTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ButtonColorTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void buttonsWithColorTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "buttonsWithColor.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_buttonsWithColor.pdf";

        drawButtons(outPdf, cmpPdf, ColorConstants.RED);
    }

    @Test
    public void buttonsWithoutColorTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "buttonsWithoutColor.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_buttonsWithoutColor.pdf";

        drawButtons(outPdf, cmpPdf, null);
    }

    private static void drawButtons(String outPdf, String cmpPdf, Color color) throws IOException, InterruptedException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outPdf)));
             Document document = new Document(pdfDocument)) {

            Button button = new Button("button");
            button.add(new Paragraph("button child paragraph"));
            Button inputButton = new Button("input button");
            button.setInteractive(true);
            inputButton.setInteractive(true);
            button.add(new Paragraph("button value"));
            inputButton.setSingleLineValue("input button value");
            button.setProperty(Property.FONT_COLOR, color == null ? null : new TransparentColor(color));
            inputButton.setProperty(Property.BACKGROUND, color == null ? null : new Background(color));

            document.add(button);
            document.add(inputButton);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
