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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
import com.itextpdf.test.utils.FileUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUAXfaTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAXfaTest/";

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAXfaTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void dynamicRenderRequiredValueTest() throws IOException {
        String input = SOURCE_FOLDER + "dynamicRequired.pdf";
        String output = DESTINATION_FOLDER + "dynamicRequired_reopen.pdf";

        PdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfReader(input),
                new PdfWriter(output));

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.DYNAMIC_XFA_FORMS_SHALL_NOT_BE_USED, e.getMessage());

        FileUtil.copy(input, output);
        // VeraPdf also complains only about the dynamic XFA forms
        new VeraPdfValidator().validateFailure(output);  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void dynamicRenderForbiddenValueTest() throws IOException {
        String input = SOURCE_FOLDER + "dynamicForbidden.pdf";
        String output = DESTINATION_FOLDER + "dynamicForbidden_reopen.pdf";

        PdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfReader(input),
                new PdfWriter(output));

        AssertUtil.doesNotThrow(() -> pdfDoc.close());

        final String result = new VeraPdfValidator().validate(output);  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        Assertions.assertNull(result); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }
}
