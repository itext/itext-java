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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FlatteningRotatedTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningRotatedTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningRotatedTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot() throws IOException, InterruptedException {
        String srcFilePatternPattern = "FormFlatteningDefaultAppearance_{0}_";
        String destPatternPattern = "FormFlatteningDefaultAppearance_{0}_";

        String[] rotAngle = new String[] {"0", "90", "180", "270"};

        for (String angle : rotAngle) {
            String srcFilePattern = MessageFormatUtil.format(srcFilePatternPattern, angle);
            String destPattern = MessageFormatUtil.format(destPatternPattern, angle);
            for (int i = 0; i < 360; i += 90) {
                String src = sourceFolder + srcFilePattern + i + ".pdf";
                String dest = destinationFolder + destPattern + i + "_flattened.pdf";
                String cmp = sourceFolder + "cmp_" + srcFilePattern + i + ".pdf";
                PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

                PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
                for (PdfFormField field : form.getFormFields().values()) {
                    field.setValue("Test");
                }
                form.flattenFields();

                doc.close();

                Assert.assertNull(new CompareTool().compareByContent(dest, cmp, destinationFolder, "diff_"));
            }
        }
    }
}
