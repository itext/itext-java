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
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;

import static com.itextpdf.test.ITextTest.createDestinationFolder;

@Category(UnitTest.class)
public class Utf8FormsTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/Utf8FormsTest/";
    public static final String FONT = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/NotoSansCJKsc-Regular.otf";

    @Before
    public void before() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void readUtf8FieldName() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-field-name.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  لا
            Assert.assertEquals("\u0644\u0627", fldName);
        }
        pdfDoc.close();
    }

    @Test
    public void readUtf8TextAnnot() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-text-annot.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  福昕 福昕UTF8
            Assert.assertEquals("\u798F\u6615 \u798F\u6615UTF8", fields.get(fldName).getValueAsString());
        }
    }

    @Test
    public void writeUtf8FieldNameAndValue() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8FieldNameAndValue.pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfTextFormField field = PdfTextFormField.createText(pdfDoc,
                new Rectangle(99, 753, 425, 15), "", "");
        field.setFont(PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H));
        //  لا
        field.put(PdfName.T, new PdfString("\u0644\u0627", PdfEncodings.UTF8));
        //  福昕 福昕UTF8
        field.put(PdfName.V, new PdfString("\u798F\u6615 \u798F\u6615UTF8", PdfEncodings.UTF8));
        field.regenerateField();
        form.addField(field);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8FieldNameAndValue.pdf", sourceFolder + "cmp_writeUtf8FieldNameAndValue.pdf", destinationFolder, "diffFieldNameAndValue_"));
    }
}
