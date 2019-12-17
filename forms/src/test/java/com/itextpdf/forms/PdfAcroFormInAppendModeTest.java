/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAcroFormInAppendModeTest extends ExtendedITextTest {

    private static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfAcroFormInAppendModeTest/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfAcroFormInAppendModeTest/";
    private static final String inputFile = destinationFolder + "inputFile.pdf";

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException {
        createDestinationFolder(destinationFolder);
        createInputFile();
    }

    @Test
    public void createFieldInAppendModeTest() throws IOException, InterruptedException {
        String outputFile = "createFieldInAppendModeTest.pdf";
        PdfDocument outputDoc = createDocInAppendMode(destinationFolder + outputFile);
        PdfFormField field = PdfFormField.createCheckBox(
                outputDoc,
                new Rectangle(10, 10, 24, 24),
                "checkboxname", "On",
                PdfFormField.TYPE_CHECK);
        PdfAcroForm.getAcroForm(outputDoc, true).addField(field);
        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void removeFieldInAppendModeTest() throws IOException, InterruptedException {
        String outputFile = "removeFieldInAppendModeTest.pdf";
        PdfDocument outputDoc = createDocInAppendMode(destinationFolder + outputFile);
        PdfAcroForm.getAcroForm(outputDoc, true).removeField("textfield2");
        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void removeFieldWithParentInAppendModeTest() throws IOException, InterruptedException {
        // Creating input document
        String inputFile = "inputRemoveFieldWithParentInAppendModeTest.pdf";
        PdfDocument inDoc = new PdfDocument(new PdfWriter(destinationFolder + inputFile));
        inDoc.addNewPage();
        PdfFormField root = PdfFormField.createEmptyField(inDoc);
        root.setFieldName("root");
        PdfFormField child = PdfFormField.createEmptyField(inDoc);
        child.setFieldName("child");
        root.addKid(child);
        PdfAcroForm.getAcroForm(inDoc, true).addField(root);
        inDoc.close();

        // Creating stamping document
        String outputFile = "removeFieldWithParentInAppendModeTest.pdf";
        PdfReader reader = new PdfReader(destinationFolder + inputFile);
        PdfWriter writer = new PdfWriter(destinationFolder + outputFile);
        PdfDocument outputDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        PdfAcroForm.getAcroForm(outputDoc, true).removeField("root.child");

        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void replaceFieldInAppendModeTest() throws IOException, InterruptedException {
        String outputFile = "replaceFieldInAppendModeTest.pdf";
        PdfDocument outputDoc = createDocInAppendMode(destinationFolder + outputFile);
        PdfFormField newField = PdfFormField.createText(outputDoc, new Rectangle(20, 160, 100, 20), "newfield", "new field");
        PdfAcroForm.getAcroForm(outputDoc, true).replaceField("textfield1", newField);
        outputDoc.close();
        compareWithCmp(outputFile);
    }

    private static void createInputFile() throws FileNotFoundException {
        PdfDocument document = new PdfDocument(new PdfWriter(inputFile));
        document.addNewPage();
        PdfAcroForm.getAcroForm(document, true)
                .addField(PdfFormField.createText(document, new Rectangle(20, 160, 100, 20), "textfield1", "text1"));
        PdfAcroForm.getAcroForm(document, true)
                .addField(PdfFormField.createText(document, new Rectangle(20, 130, 100, 20), "textfield2", "text2"));
        document.close();
    }

    private static PdfDocument createDocInAppendMode(String outFile) throws IOException {
        PdfReader reader = new PdfReader(inputFile);
        PdfWriter writer = new PdfWriter(outFile);
        return new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
    }

    private static void compareWithCmp(String outputFile) throws IOException, InterruptedException {
        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + outputFile, sourceFolder + "cmp_" + outputFile, destinationFolder,
                        "diff_"));
    }
}
