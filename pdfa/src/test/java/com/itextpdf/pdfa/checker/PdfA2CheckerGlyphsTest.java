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
package com.itextpdf.pdfa.checker;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfA2CheckerGlyphsTest extends ExtendedITextTest {

    private final PdfA2Checker pdfA2Checker = new PdfA2Checker(PdfAConformance.PDF_A_2B);

    @BeforeEach
    public void before() {
        pdfA2Checker.setFullCheckMode(true);
    }

    @Test
    public void checkValidFontGlyphsTest() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {
            document.addNewPage();

            PdfDictionary charProcs = new PdfDictionary();
            charProcs.put(PdfName.A, new PdfStream());
            charProcs.put(PdfName.B, new PdfStream());

            PdfArray differences = new PdfArray();
            differences.add(new PdfNumber(41));
            differences.add(PdfName.A);
            differences.add(new PdfNumber(82));
            differences.add(PdfName.B);

            PdfFont font = createFontWithCharProcsAndEncodingDifferences(document, charProcs, differences);

            // no assertions as we want to ensure that in this case the next method won't throw an exception
            pdfA2Checker.checkFontGlyphs(font, null);
        }
    }

    @Test
    public void checkInvalidFontGlyphsTest() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(bos);
                PdfDocument document = new PdfDocument(writer)) {
            document.addNewPage();

            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
            formXObject.getPdfObject().put(PdfName.Subtype2, PdfName.PS);

            PdfDictionary charProcs = new PdfDictionary();
            charProcs.put(PdfName.A, new PdfStream());
            charProcs.put(PdfName.B, formXObject.getPdfObject());

            PdfArray differences = new PdfArray();
            differences.add(new PdfNumber(41));
            differences.add(PdfName.A);
            differences.add(new PdfNumber(82));
            differences.add(PdfName.B);

            PdfFont font = createFontWithCharProcsAndEncodingDifferences(document, charProcs, differences);

            Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                    () -> pdfA2Checker.checkFontGlyphs(font, null)
            );
            Assertions.assertEquals(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_SUBTYPE2_KEY_WITH_A_VALUE_OF_PS, e.getMessage());
        }
    }

    private PdfFont createFontWithCharProcsAndEncodingDifferences(PdfDocument document,
            PdfDictionary charProcs, PdfArray differences) {
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.Type, PdfName.Encoding);
        encoding.put(PdfName.Differences, differences);

        PdfDictionary fontDictionary = new PdfDictionary();
        fontDictionary.put(PdfName.Type, PdfName.Font);
        fontDictionary.put(PdfName.Subtype, PdfName.Type3);
        fontDictionary.put(PdfName.Encoding, encoding);
        fontDictionary.put(PdfName.CharProcs, charProcs);
        fontDictionary.put(PdfName.FontMatrix, new PdfArray(new float[]{0f, 0f, 0f, 0f, 0f, 0f}));
        fontDictionary.put(PdfName.Widths, new PdfArray(new float[0]));

        fontDictionary.makeIndirect(document);

        return PdfFontFactory.createFont(fontDictionary);
    }
}
