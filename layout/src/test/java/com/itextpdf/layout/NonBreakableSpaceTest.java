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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class NonBreakableSpaceTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NonBreakableSpaceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/NonBreakableSpaceTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleParagraphTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "simpleParagraphTest.pdf";
        String cmpFileName = sourceFolder + "cmp_simpleParagraphTest.pdf";
        String diffPrefix = "diff_simpleParagraphTest_";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));
        document.add(new Paragraph("aaa bbb\u00a0ccccccccccc").setWidth(100).setBorder(new SolidBorder(ColorConstants.RED, 10)));
        document.add(new Paragraph("aaa bbb ccccccccccc").setWidth(100).setBorder(new SolidBorder(ColorConstants.GREEN, 10)));
        document.add(new Paragraph("aaaaaaa\u00a0bbbbbbbbbbb").setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 10)));
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }

    @Test
    public void consecutiveSpacesTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "consecutiveSpacesTest.pdf";
        String cmpFileName = sourceFolder + "cmp_consecutiveSpacesTest.pdf";
        String diffPrefix = "diff_consecutiveSpacesTest_";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));
        document.add(new Paragraph("aaa\u00a0\u00a0\u00a0bbb").setWidth(100).setBorder(new SolidBorder(ColorConstants.RED, 10)));
        document.add(new Paragraph("aaa\u00a0bbb").setWidth(100).setBorder(new SolidBorder(ColorConstants.GREEN, 10)));
        document.add(new Paragraph("aaa   bbb").setWidth(100).setBorder(new SolidBorder(ColorConstants.BLUE, 10)));
        document.add(new Paragraph("aaa bbb").setWidth(100).setBorder(new SolidBorder(ColorConstants.BLACK, 10)));
        Paragraph p = new Paragraph();
        p.add("aaa\u00a0\u00a0\u00a0bbb").add("ccc   ddd");
        document.add(p);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}
