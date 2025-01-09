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
package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfAStampingModeTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAStampingModeTest/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfAStampingModeTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void pdfA1FieldStampingModeTest01() throws IOException, InterruptedException {
        String fileName = "pdfA1FieldStampingModeTest01.pdf";
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "pdfs/pdfA1DocumentWithPdfA1Fields01.pdf"), new PdfWriter(destinationFolder + fileName));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        form.getField("checkBox").setValue("0");
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        Assertions.assertNull(compareTool.compareByContent(destinationFolder + fileName, cmpFolder + "cmp_" + fileName, destinationFolder, "diff_"));
        Assertions.assertNull(compareTool.compareXmp(destinationFolder + fileName, cmpFolder + "cmp_" + fileName, true));
        Assertions.assertNull(new VeraPdfValidator().validate(destinationFolder + fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfA2FieldStampingModeTest01() throws IOException, InterruptedException {
        String fileName = "pdfA2FieldStampingModeTest01.pdf";
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "pdfs/pdfA2DocumentWithPdfA2Fields01.pdf"), new PdfWriter(destinationFolder + fileName));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        form.getField("checkBox").setValue("0");
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        Assertions.assertNull(compareTool.compareByContent(destinationFolder + fileName, cmpFolder + "cmp_" + fileName, destinationFolder, "diff_"));
        Assertions.assertNull(compareTool.compareXmp(destinationFolder + fileName, cmpFolder + "cmp_" + fileName, true));
        Assertions.assertNull(new VeraPdfValidator().validate(destinationFolder + fileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }
}
