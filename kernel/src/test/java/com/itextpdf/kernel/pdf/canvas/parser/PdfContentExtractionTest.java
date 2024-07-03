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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperException;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.ClipperExceptionConstant;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfContentExtractionTest extends ExtendedITextTest {
    
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfContentExtractionTest/";

    @Test
    //TODO: remove the expected exception construct once the issue is fixed (DEVSIX-1279)
    public void contentExtractionInDocWithBigCoordinatesTest() throws IOException {
        String inputFileName = sourceFolder + "docWithBigCoordinates.pdf";
        //In this document the CTM shrinks coordinates and this coordinates are large numbers.
        // At the moment creation of this test clipper has a problem with handling large numbers
        // since internally it deals with integers and has to multiply large numbers even more
        // for internal purposes

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName));
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(pdfDocument);

        Exception e = Assertions.assertThrows(ClipperException.class,
                () -> contentParser.processContent(1, new LocationTextExtractionStrategy())
        );
        Assertions.assertEquals(ClipperExceptionConstant.COORDINATE_OUTSIDE_ALLOWED_RANGE, e.getMessage());
    }
}
