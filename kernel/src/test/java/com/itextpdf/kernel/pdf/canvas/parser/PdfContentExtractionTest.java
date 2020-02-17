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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperException;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperExceptionConstant;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfContentExtractionTest extends ExtendedITextTest {
    
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfContentExtractionTest/";

    @Test
    //TODO: remove the expected exception construct once the issue is fixed (DEVSIX-1279)
    public void contentExtractionInDocWithBigCoordinatesTest() throws IOException {
        junitExpectedException.expect(ClipperException.class);
        junitExpectedException.expectMessage(ClipperExceptionConstant.COORDINATE_OUTSIDE_ALLOWED_RANGE);

        String inputFileName = sourceFolder + "docWithBigCoordinates.pdf";
        //In this document the CTM shrinks coordinates and this coordinates are large numbers.
        // At the moment creation of this test clipper has a problem with handling large numbers
        // since internally it deals with integers and has to multiply large numbers even more
        // for internal purposes

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName));
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(pdfDocument);
        contentParser.processContent(1, new LocationTextExtractionStrategy());
    }
}