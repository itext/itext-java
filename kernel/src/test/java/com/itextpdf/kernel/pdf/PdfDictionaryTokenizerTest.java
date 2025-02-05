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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class PdfDictionaryTokenizerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDictionaryTokenizerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDictionaryTokenizerTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void readerTurnsCorrectlyNotWellFormattedValueInDictionary_01() throws IOException {

        String inPath = sourceFolder + "documentWithMalformedNumberOnDictionary.pdf";
        String EXPECTED="-12.";

        /*
        The following is the content included in the pdf file

        <</Ascent 800
        /CapHeight 700
        /Descent -200
        /Flags 32
        /FontBBox[-631 -462 1632 1230]
        /FontFamily(FreeSans)
        /FontFile2 8 0 R
        /FontName/XVVAXW+FreeSans
        /FontWeight 400
        /ItalicAngle -12.-23
        /StemV 80
        /Type/FontDescriptor>>
        */

        // ItalicAngle -12.-23 turns into -12.

        String result = getItalicAngleValue(inPath);
        Assertions.assertEquals(EXPECTED, result);
    }

    @Test
    public void readerTurnsCorrectlyNotWellFormattedValueInDictionary_02() throws IOException {
        String inPath = sourceFolder + "documentWithMalformedNumberOnDictionary2.pdf";
        String EXPECTED="-12.";

        /*
        The following is the content included in the pdf file

        <</Ascent 800
        /CapHeight 700
        /Descent -200
        /Flags 32
        /FontBBox[-631 -462 1632 1230]
        /FontFamily(FreeSans)
        /FontFile2 8 0 R
        /FontName/XVVAXW+FreeSans
        /FontWeight 400
        /StemV 80
        /Type/FontDescriptor
        /ItalicAngle -12.-23>>
        */

        // ItalicAngle -12.-23 turns into -12.

        String result = getItalicAngleValue(inPath);
        Assertions.assertEquals(EXPECTED, result);
    }

    private String getItalicAngleValue(String inPath) throws IOException {
        String result = "";
        PdfReader pdfR = new PdfReader(inPath);
        PdfDocument attachmentPDF  = new PdfDocument(pdfR);
        int max = attachmentPDF.getNumberOfPdfObjects();
        for (int i=0;i<max;i++){
            PdfObject obj = attachmentPDF.getPdfObject(i);
            if (obj!=null ){
                PdfDictionary pdfDict = (PdfDictionary) obj;
                PdfObject x = pdfDict.get(PdfName.Type);
                if(x!=null && x.equals(PdfName.FontDescriptor)){
                    PdfObject italicAngle = pdfDict.get(PdfName.ItalicAngle);
                    result = italicAngle.toString();
                }
            }
        }
        attachmentPDF.close();
        return result;
    }
}
