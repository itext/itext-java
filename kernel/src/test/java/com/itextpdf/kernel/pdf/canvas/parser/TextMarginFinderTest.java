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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextMarginFinder;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextMarginFinderTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextMarginFinderTest/";

    @Test
    public void test() throws Exception {
        TextMarginFinder finder = new TextMarginFinder();
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in.pdf"));
        new PdfCanvasProcessor(finder).processPageContent(pdfDocument.getPage(1));

        Rectangle textRect = finder.getTextRectangle();
        Assertions.assertEquals(1.42f * 72f, textRect.getX(), 0.01f);
        Assertions.assertEquals(7.42f * 72f, textRect.getX() + textRect.getWidth(), 0.01f);
        Assertions.assertEquals(2.42f * 72f, textRect.getY(), 0.01f);
        Assertions.assertEquals(10.42f * 72f, textRect.getY() + textRect.getHeight(), 0.01f);
    }
}
