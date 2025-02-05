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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;
import java.io.File;

@Tag("IntegrationTest")
public class FormFieldAppendTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldAppendTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldAppendTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFillingAppend_form_empty_Test() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "Form_Empty.pdf";
        String temp = destinationFolder + "temp_empty.pdf";
        String filename = destinationFolder + "formFillingAppend_form_empty.pdf";
        StampingProperties props = new StampingProperties();
        props.useAppendMode();

        PdfDocument doc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(temp), props);

        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        for (PdfFormField field : form.getAllFormFields().values()) {
            field.setValue("Test");
        }

        doc.close();

        flatten(temp, filename);

        File toDelete = new File(temp);
        toDelete.delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_empty.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void formFillingAppend_form_filled_Test() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "Form_Empty.pdf";
        String temp = destinationFolder + "temp_filled.pdf";
        String filename = destinationFolder + "formFillingAppend_form_filled.pdf";
        StampingProperties props = new StampingProperties();
        props.useAppendMode();

        PdfDocument doc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(temp), props);

        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        for (PdfFormField field : form.getAllFormFields().values()) {
            field.setValue("Different");
        }

        doc.close();

        flatten(temp, filename);

        new File(temp).delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_filled.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    private void flatten(String src, String dest) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        form.flattenFields();
        doc.close();
    }
}
