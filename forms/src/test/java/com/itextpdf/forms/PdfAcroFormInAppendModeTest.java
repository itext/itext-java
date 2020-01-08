/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
    private static final String testFolder = "PdfAcroFormInAppendModeTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/forms/" + testFolder;
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/" + testFolder;

    private static final String inputFile = destinationFolder + "inputFile.pdf";
    private static final String inputFileWithIndirectFieldsArray = destinationFolder + "inputFileWithIndirectFieldsArray.pdf";

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException {
        createDestinationFolder(destinationFolder);
        createInputFile();
        createInputFileWithIndirectFieldsArray();
    }

    @Test
    public void addFieldTest() throws IOException, InterruptedException {
        String outputFile = "addFieldTest.pdf";
        PdfDocument outputDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(destinationFolder + outputFile),
                new StampingProperties().useAppendMode());
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
    public void removeFieldTest() throws IOException, InterruptedException {
        String outputFile = "removeFieldTest.pdf";
        PdfDocument outputDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(destinationFolder + outputFile),
                new StampingProperties().useAppendMode());
        PdfAcroForm.getAcroForm(outputDoc, true).removeField("textfield2");
        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void removeKidTest() throws IOException, InterruptedException {
        // Creating input document
        String inputFile = "in_removeKidTest.pdf";
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
        String outputFile = "removeKidTest.pdf";
        PdfReader reader = new PdfReader(destinationFolder + inputFile);
        PdfWriter writer = new PdfWriter(destinationFolder + outputFile);
        PdfDocument outputDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        PdfAcroForm.getAcroForm(outputDoc, true).removeField("root.child");

        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void replaceFieldTest() throws IOException, InterruptedException {
        String outputFile = "replaceFieldTest.pdf";
        PdfDocument outputDoc = new PdfDocument(new PdfReader(inputFile), new PdfWriter(destinationFolder + outputFile),
                new StampingProperties().useAppendMode());
        PdfFormField newField = PdfFormField
                .createText(outputDoc, new Rectangle(20, 160, 100, 20), "newfield", "new field");
        PdfAcroForm.getAcroForm(outputDoc, true).replaceField("textfield1", newField);
        outputDoc.close();
        compareWithCmp(outputFile);
    }

    @Test
    public void addFieldToIndirectFieldsArrayTest() throws IOException, InterruptedException {
        String outputFile = "addFieldToIndirectFieldsArrayTest.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inputFileWithIndirectFieldsArray),
                new PdfWriter(destinationFolder + outputFile), new StampingProperties().useAppendMode());

        PdfFormField field = PdfFormField.createCheckBox(
                document,
                new Rectangle(10, 10, 24, 24),
                "checkboxname", "On",
                PdfFormField.TYPE_CHECK);

        // Get an existing acroform and add new field to it
        PdfAcroForm.getAcroForm(document, false).addField(field);

        document.close();

        compareWithCmp(outputFile);
    }

    @Test
    public void removeFieldFromIndirectFieldsArrayTest() throws IOException, InterruptedException {
        String outputFile = "removeFieldFromIndirectFieldsArrayTest.pdf";
        PdfDocument outputDoc = new PdfDocument(new PdfReader(inputFileWithIndirectFieldsArray), new PdfWriter(destinationFolder + outputFile),
                new StampingProperties().useAppendMode());
        PdfAcroForm.getAcroForm(outputDoc, true).removeField("textfield2");
        outputDoc.close();
        compareWithCmp(outputFile);
    }


    @Test
    public void removeKidFromIndirectKidsArrayTest() throws IOException, InterruptedException {
        String inputFile = "in_removeKidFromIndirectKidsArrayTest.pdf";
        String outputFile = "removeKidFromIndirectKidsArrayTest.pdf";

        // Creating input document
        PdfDocument inDoc = new PdfDocument(new PdfWriter(destinationFolder + inputFile));
        inDoc.addNewPage();
        PdfFormField root = PdfFormField.createEmptyField(inDoc);
        root.setFieldName("root");
        PdfFormField child = PdfFormField.createEmptyField(inDoc);
        child.setFieldName("child");
        root.addKid(child);
        PdfAcroForm.getAcroForm(inDoc, true).addField(root);
        // make kids array indirect
        PdfAcroForm.getAcroForm(inDoc, true).getField("root").getKids().makeIndirect(inDoc);
        inDoc.close();

        // Creating stamping document
        PdfReader reader = new PdfReader(destinationFolder + inputFile);
        PdfWriter writer = new PdfWriter(destinationFolder + outputFile);
        PdfDocument outputDoc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());

        PdfAcroForm.getAcroForm(outputDoc, true).removeField("root.child");

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

    private static void createInputFileWithIndirectFieldsArray()
            throws FileNotFoundException {
        PdfDocument document = new PdfDocument(new PdfWriter(inputFileWithIndirectFieldsArray));
        document.addNewPage();
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        acroForm.getFields().makeIndirect(document);
        acroForm.addField(PdfFormField.createText(document, new Rectangle(20, 160, 100, 20), "textfield1", "text1"));
        acroForm.addField(PdfFormField.createText(document, new Rectangle(20, 130, 100, 20), "textfield2", "text2"));
        document.close();
    }

    private static void compareWithCmp(String outputFile) throws IOException, InterruptedException {
        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + outputFile, sourceFolder + "cmp_" + outputFile, destinationFolder,
                        "diff_"));
    }
}
