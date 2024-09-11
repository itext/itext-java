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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.validation.context.SignTypeValidationContext;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfADocumentTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void checkCadesSignatureTypeIsoConformance() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument document = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        document.checkIsoConformance(new SignTypeValidationContext(true));
    }

    @Test
    public void checkCMSSignatureTypeIsoConformance() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfADocument document = new PdfADocument(writer, PdfAConformance.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> document.checkIsoConformance(new SignTypeValidationContext(false)));
        Assertions.assertEquals(PdfaExceptionMessageConstant.SIGNATURE_SHALL_CONFORM_TO_ONE_OF_THE_PADES_PROFILE, e.getMessage());
    }

}
