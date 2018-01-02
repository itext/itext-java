/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class FormFieldFlatteningTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldFlatteningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldFlatteningTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFlatteningTest01() throws IOException, InterruptedException {
        String srcFilename = sourceFolder + "formFlatteningSource.pdf";
        String filename = destinationFolder + "formFlatteningTest01.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(srcFilename), new PdfWriter(filename));

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        form.flattenFields();

        doc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFlatteningTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot0() throws IOException, InterruptedException {
        String srcFilePattern = "FormFlatteningDefaultAppearance_0_";
        String destPattern = "FormFlatteningDefaultAppearance_0_";

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

            CompareTool compareTool = new CompareTool();
            String errorMessage = compareTool.compareByContent(dest, cmp, destinationFolder, "diff_");
            if (errorMessage != null) {
                Assert.fail(errorMessage);
            }
        }

    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot90() throws IOException, InterruptedException {
        String srcFilePattern = "FormFlatteningDefaultAppearance_90_";
        String destPattern = "FormFlatteningDefaultAppearance_90_";

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

            CompareTool compareTool = new CompareTool();
            String errorMessage = compareTool.compareByContent(dest, cmp, destinationFolder, "diff_");
            if (errorMessage != null) {
                Assert.fail(errorMessage);
            }
        }

    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot180() throws IOException, InterruptedException {
        String srcFilePattern = "FormFlatteningDefaultAppearance_180_";
        String destPattern = "FormFlatteningDefaultAppearance_180_";

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

            CompareTool compareTool = new CompareTool();
            String errorMessage = compareTool.compareByContent(dest, cmp, destinationFolder, "diff_");
            if (errorMessage != null) {
                Assert.fail(errorMessage);
            }
        }

    }

    @Test
    public void formFlatteningTest_DefaultAppearanceGeneration_Rot270() throws IOException, InterruptedException {
        String srcFilePattern = "FormFlatteningDefaultAppearance_270_";
        String destPattern = "FormFlatteningDefaultAppearance_270_";

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

            CompareTool compareTool = new CompareTool();
            String errorMessage = compareTool.compareByContent(dest, cmp, destinationFolder, "diff_");
            if (errorMessage != null) {
                Assert.fail(errorMessage);
            }
        }

    }
}
