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
package com.itextpdf.forms.xfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Node;

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
        XfaForm xfa = new XfaForm(FileUtil.getInputStreamForFile(XML));
        xfa.write(doc);
        doc.addNewPage();
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void readXFAFormTest() throws IOException {
        String inFileName = sourceFolder + "formTemplate.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        AssertUtil.doesNotThrow(() -> PdfFormCreator.getAcroForm(pdfDocument, true));
    }

    @Test
    public void findFieldName() throws IOException {
        String inFileName = sourceFolder + "TextField1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        XfaForm xfaForm = acroForm.getXfaForm();
        xfaForm.findFieldName("TextField1");
        String secondRun = xfaForm.findFieldName("TextField1");
        Assert.assertNotNull(secondRun);
    }

    @Test
    public void findFieldNameWithoutDataSet() throws IOException {
        String inFileName = sourceFolder + "TextField1_empty.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        XfaForm xfaForm = acroForm.getXfaForm();
        String name = xfaForm.findFieldName("TextField1");
        Assert.assertNull(name);
    }

    @Test
    public void extractXFADataTest() throws IOException {
        String src = sourceFolder + "xfaFormWithDataSet.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(src));
        XfaForm xfa = new XfaForm(pdfDocument);

        Node node = xfa.findDatasetsNode("Number1");
        Assert.assertNotNull(node);
        Assert.assertEquals("Number1", node.getNodeName());
    }

    @Test
    public void extractNodeTextByPathText() throws IOException {
        String inFileName = sourceFolder + "TextField1.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inFileName))) {
            XfaForm xfaForm = new XfaForm(pdfDocument);
            Assert.assertEquals("Test", xfaForm.getNodeTextByPath("xdp.datasets.data.form1"));
            Assert.assertNull(xfaForm.getNodeTextByPath("xdp.datasets.noElement"));
        }
    }
}
