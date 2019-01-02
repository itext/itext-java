/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.File;

@Category(IntegrationTest.class)
public class FormFieldAppendTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldAppendTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldAppendTest/";

    @BeforeClass
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

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        for (PdfFormField field : form.getFormFields().values()) {
            field.setValue("Test");
        }

        doc.close();

        flatten(temp, filename);

        File toDelete = new File(temp);
        toDelete.delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_empty.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
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

        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        for (PdfFormField field : form.getFormFields().values()) {
            field.setValue("Different");
        }

        doc.close();

        flatten(temp, filename);

        new File(temp).delete();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFillingAppend_form_filled.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    private void flatten(String src, String dest) throws IOException {
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        form.flattenFields();
        doc.close();
    }
}
