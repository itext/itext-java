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
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(IntegrationTest.class)
public class FlatteningRotatedTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningRotatedTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningRotatedTest/";

    private final String inputPdfFileName;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> inputFileNames() {
        List<Object[]> inputFileNames = new ArrayList<Object[]>();
        for (int pageRot = 0; pageRot < 360; pageRot += 90) {
            for (int fieldRot = 0; fieldRot < 360; fieldRot += 90) {
                inputFileNames.add(new Object[] {"FormFlatteningDefaultAppearance_" + pageRot + "_" + fieldRot});
            }
        }
        return inputFileNames;
    }

    public FlatteningRotatedTest(Object inputPdfFileName) {
        this.inputPdfFileName = (String) inputPdfFileName;
    }

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot() throws IOException, InterruptedException {
        String src = sourceFolder + inputPdfFileName + ".pdf";
        String dest = destinationFolder + inputPdfFileName + ".pdf";
        String dest_flattened = destinationFolder + inputPdfFileName + "_flattened.pdf";
        String cmp = sourceFolder + "cmp_" + inputPdfFileName + ".pdf";
        String cmp_flattened = sourceFolder + "cmp_" + inputPdfFileName + "_flattened.pdf";

        try (PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
            for (PdfFormField field : form.getAllFormFields().values()) {
                field.setValue("Long Long Text");
                field.getFirstFormAnnotation().setBorderWidth(1);
                field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLUE);
            }
        }
        Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));

        try (PdfDocument doc = new PdfDocument(new PdfReader(dest), new PdfWriter(dest_flattened))) {
            PdfFormCreator.getAcroForm(doc, true).flattenFields();
        }
        Assert.assertNull(new CompareTool().compareByContent(dest_flattened, cmp_flattened, destinationFolder, "diff_"));
    }
}
