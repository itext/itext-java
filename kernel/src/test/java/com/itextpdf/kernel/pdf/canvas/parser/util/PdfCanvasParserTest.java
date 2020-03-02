/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfCanvasParserTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfCanvasParserTest/";

    @Test
    public void innerArraysInContentStreamTest() throws IOException {
        String inputFileName = sourceFolder + "innerArraysInContentStream.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName));

        byte[] docInBytes = pdfDocument.getFirstPage().getContentBytes();

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();

        PdfTokenizer tokeniser = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(docInBytes)));
        PdfResources resources = pdfDocument.getPage(1).getResources();
        PdfCanvasParser ps = new PdfCanvasParser(tokeniser, resources);

        List<PdfObject> actual = ps.parse(null);

        List<PdfObject> expected = new ArrayList<PdfObject>();
        expected.add(new PdfString("Cyan"));
        expected.add(new PdfArray(new int[] {1, 0, 0, 0}));
        expected.add(new PdfString("Magenta"));
        expected.add(new PdfArray(new int[] {0, 1, 0, 0}));
        expected.add(new PdfString("Yellow"));
        expected.add(new PdfArray(new int[] {0, 0, 1, 0}));

        PdfArray cmpArray = new PdfArray(expected);

        Assert.assertTrue(new CompareTool().compareArrays(cmpArray,
                (((PdfDictionary) actual.get(1)).getAsArray(new PdfName("ColorantsDef")))));
    }
}
