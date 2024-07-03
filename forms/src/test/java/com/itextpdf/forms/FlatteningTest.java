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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class FlatteningTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void flatteningFormFieldNoSubtypeInAPTest() throws IOException, InterruptedException {
        String src = sourceFolder + "formFieldNoSubtypeInAPTest.pdf";
        String dest = destinationFolder + "flatteningFormFieldNoSubtypeInAPTest.pdf";
        String cmp = sourceFolder + "cmp_flatteningFormFieldNoSubtypeInAPTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        PdfFormCreator.getAcroForm(doc, false).flattenFields();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.N_ENTRY_IS_REQUIRED_FOR_APPEARANCE_DICTIONARY))
    public void formFlatteningTestWithoutNEntry() throws IOException, InterruptedException {
        String filename = "formFlatteningTestWithoutNEntry";
        String src = sourceFolder + filename + ".pdf";
        String dest = destinationFolder + filename + "_flattened.pdf";
        String cmp = sourceFolder + "cmp_" + filename + "_flattened.pdf";
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, false);
        acroForm.setGenerateAppearance(false);
        acroForm.flattenFields();
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    //TODO: Adapt assertion after DEVSIX-3079 is fixed
    public void hiddenFieldsFlatten() throws IOException {
        String filename = "hiddenField";
        String src = sourceFolder + filename + ".pdf";
        String dest = destinationFolder + filename + "_flattened.pdf";
        final PdfDocument document = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
        acroForm.getField("hiddenField").getPdfObject().put(PdfName.F, new PdfNumber(2));
        acroForm.flattenFields();
        String textAfterFlatten = PdfTextExtractor.getTextFromPage(document.getPage(1));
        document.close();
        Assertions.assertTrue(textAfterFlatten.contains("hiddenFieldValue"), "Pdf does not contain the expected text");
    }
}
