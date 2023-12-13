/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class FlatteningTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void flatteningFormFieldNoSubtypeInAPTest() throws IOException, InterruptedException {
        String src = sourceFolder + "formFieldNoSubtypeInAPTest.pdf";
        String dest = destinationFolder + "flatteningFormFieldNoSubtypeInAPTest.pdf";
        String cmp = sourceFolder + "cmp_flatteningFormFieldNoSubtypeInAPTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        PdfAcroForm.getAcroForm(doc, false).flattenFields();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.N_ENTRY_IS_REQUIRED_FOR_APPEARANCE_DICTIONARY))
    public void formFlatteningTestWithoutNEntry() throws IOException, InterruptedException {
        String filename = "formFlatteningTestWithoutNEntry";
        String src = sourceFolder + filename + ".pdf";
        String dest = destinationFolder + filename + "_flattened.pdf";
        String cmp = sourceFolder + "cmp_" + filename + "_flattened.pdf";
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        PdfAcroForm.getAcroForm(doc, false).flattenFields();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
    }

}
