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
package com.itextpdf.forms.xfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XFAFormTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/xfa/XFAFormTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/xfa/XFAFormTest/";
    public static final String XML = sourceFolder + "xfa.xml";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createEmptyXFAFormTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createEmptyXFAFormTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_createEmptyXFAFormTest01.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm(doc);
        XfaForm.setXfaForm(xfa, doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void createEmptyXFAFormTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createEmptyXFAFormTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_createEmptyXFAFormTest02.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm();
        XfaForm.setXfaForm(xfa, doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void createXFAFormTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "createXFAFormTest.pdf";
        String cmpFileName = sourceFolder + "cmp_createXFAFormTest.pdf";

        PdfDocument doc = new PdfDocument(new PdfWriter(outFileName));
        XfaForm xfa = new XfaForm(new FileInputStream(XML));
        xfa.write(doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void readXFAFormTest() throws IOException {
        String inFileName = sourceFolder + "formTemplate.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        // Test that exception is not thrown
        PdfAcroForm.getAcroForm(pdfDocument, true);
    }

    @Test
    public void findFieldName() throws IOException {
        String inFileName = sourceFolder + "TextField1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, true);
        XfaForm xfaForm = acroForm.getXfaForm();
        xfaForm.findFieldName("TextField1");
        String secondRun = xfaForm.findFieldName("TextField1");
        Assert.assertNotNull(secondRun);
    }

    @Test
    public void findFieldNameWithoutDataSet() throws IOException {
        String inFileName = sourceFolder + "TextField1_empty.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, true);
        XfaForm xfaForm = acroForm.getXfaForm();
        String name = xfaForm.findFieldName("TextField1");
        Assert.assertNull(name);
    }

}
