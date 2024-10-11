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
package com.itextpdf.forms.fields;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfFormFieldNameTest extends ExtendedITextTest {

    private PdfDocument outputDoc;
    private PdfAcroForm acroForm;

    @BeforeEach
    public void init() {
        outputDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        outputDoc.addNewPage();
        acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
    }

    @AfterEach
    public void shutdown() {
        outputDoc.close();
    }

    @Test
    public void getFormFieldWithNormalNames() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child2");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        PdfFormField child1RecoveredField = acroForm.getField("root.child1");
        PdfFormField child2RecoveredField = acroForm.getField("root.child1.child2");
        //ASSERT

        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("root.child1", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals("root.child1.child2", child2RecoveredField.getFieldName().toString());
    }

    @Test
    public void getFormFieldWithNormalNamesRootIsEmpty() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child2");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("");
        PdfFormField child1RecoveredField = acroForm.getField(".child1");
        PdfFormField child2RecoveredField = acroForm.getField(".child1.child2");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals(".child1", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals(".child1.child2", child2RecoveredField.getFieldName().toString());
    }

    @Test
    public void getFormFieldWithWhiteSpaceInNames() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "ro\tot");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child 1");
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("ro\tot");
        PdfFormField child1RecoveredField = acroForm.getField("ro\tot.child 1");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("ro\tot", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("ro\tot.child 1", child1RecoveredField.getFieldName().toString());
    }

    @Test
    public void getFormFieldWithEmptyStringAsName() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child2");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        PdfFormField child1RecoveredField = acroForm.getField("root.");
        PdfFormField child2RecoveredField = acroForm.getField("root..child2");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals("root..child2", child2RecoveredField.getFieldName().toString());

    }

    @Test
    public void getFormFieldWithTwoEmptyStringAsName() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        PdfFormField child1RecoveredField = acroForm.getField("root.");
        PdfFormField child2RecoveredField = acroForm.getField("root..");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals("root..", child2RecoveredField.getFieldName().toString());
    }

    @Test
    public void getFormFieldWithTwoEmptyStringAsNameFollowedByActualName() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child3 = addDefaultTextFormField(outputDoc, "child3");
        child2.addKid(child3);
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        PdfFormField child1RecoveredField = acroForm.getField("root.");
        PdfFormField child2RecoveredField = acroForm.getField("root..");
        PdfFormField child3RecoveredField = acroForm.getField("root...child3");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals("root..", child2RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child3, child3RecoveredField);
        Assertions.assertEquals("root...child3", child3RecoveredField.getFieldName().toString());

    }

    @Test
    public void getFormFieldWithAlternatingFilledInStartWithRootFilledIn() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child2");
        PdfFormField child3 = addDefaultTextFormField(outputDoc, "");

        child2.addKid(child3);
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        PdfFormField child1RecoveredField = acroForm.getField("root.");
        PdfFormField child2RecoveredField = acroForm.getField("root..child2");
        PdfFormField child3RecoveredField = acroForm.getField("root..child2.");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals("root..child2", child2RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child3, child3RecoveredField);
        Assertions.assertEquals("root..child2.", child3RecoveredField.getFieldName().toString());
    }

    @Test
    public void getFormFieldWithAlternatingFilledInStartWithRootEmpty() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child3 = addDefaultTextFormField(outputDoc, "child3");

        child2.addKid(child3);
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        PdfFormField rootRecoveredField = acroForm.getField("");
        PdfFormField child1RecoveredField = acroForm.getField(".child1");
        PdfFormField child2RecoveredField = acroForm.getField(".child1.");
        PdfFormField child3RecoveredField = acroForm.getField(".child1..child3");
        //ASSERT
        Assertions.assertEquals(root, rootRecoveredField);
        Assertions.assertEquals("", rootRecoveredField.getFieldName().toString());

        Assertions.assertEquals(child1, child1RecoveredField);
        Assertions.assertEquals(".child1", child1RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child2, child2RecoveredField);
        Assertions.assertEquals(".child1.", child2RecoveredField.getFieldName().toString());
        Assertions.assertEquals(child3, child3RecoveredField);
        Assertions.assertEquals(".child1..child3", child3RecoveredField.getFieldName().toString());
    }


    @Test
    public void removeFieldWithEmptyNameCorrectlyRemoved() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child2");

        root.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        acroForm.removeField("root.");

        //ASSERT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        Assertions.assertFalse(rootRecoveredField.getChildFields().contains(child1));
        Assertions.assertTrue(rootRecoveredField.getChildFields().contains(child2));
    }

    @Test
    public void removeFieldWithEmptyName2DeepCorrectlyRemoved() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");

        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        root.removeChild(child2);

        acroForm.removeField("root.child1.");

        //ASSERT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        Assertions.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assertions.assertFalse(rootRecoveredField.getChildFields().contains(child2));
    }

    @Test
    public void removeFieldWith2EmptyNamesCorrectlyRemoved() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");

        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        root.removeChild(child2);

        acroForm.removeField("root..");

        //ASSERT
        PdfFormField rootRecoveredField = acroForm.getField("root");
        Assertions.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assertions.assertFalse(rootRecoveredField.getChildFields().contains(child2));
    }

    @Test
    public void getFormFieldWithAllEmptyNamesCorrectlyRemoved() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");

        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        root.removeChild(child2);

        int sizeBeforeRemoval = acroForm.getAllFormFields().size();
        acroForm.removeField("..");

        //ASSERT
        PdfFormField rootRecoveredField = acroForm.getField("");
        Assertions.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assertions.assertFalse(rootRecoveredField.getChildFields().contains(child2));
        Assertions.assertEquals(sizeBeforeRemoval - 1, acroForm.getAllFormFields().size());

    }

    @Test
    public void removeFormFieldRemoveEmptyRootNoFieldsAnyMore() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");

        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        //ACT
        root.removeChild(child2);

        acroForm.removeField("");

        //ASSERT
        Assertions.assertEquals(0, acroForm.getAllFormFields().size());
    }


    @Test
    public void renameFieldToEmptyNamesGetsRenamed() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("root.child1", "");
        //ASSERT
        Assertions.assertEquals("root.", child1.getFieldName().toUnicodeString());
    }

    @Test
    public void renameFieldWithEmptyNameGetsRenamed() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "newName");
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("root", "");
        acroForm.renameField(".newName", "");
        //ASSERT
        Assertions.assertEquals(".", child1.getFieldName().toUnicodeString());
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    FormsLogMessageConstants.FIELDNAME_NOT_FOUND_OPERATION_CAN_NOT_BE_COMPLETED, count = 1)
    })
    public void renameFieldWithNotFoundNameLogsWarning() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "newName");
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("root.", "");
        Assertions.assertEquals("root.newName", child1.getFieldName().toUnicodeString());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    FormsLogMessageConstants.FIELDNAME_NOT_FOUND_OPERATION_CAN_NOT_BE_COMPLETED, count = 1)
    })
    public void renameFieldWithNotFoundNameLogsError() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "newName");
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("root", "");
        acroForm.renameField("root.newName", "");
        //ASSERT
        Assertions.assertEquals(".newName", child1.getFieldName().toUnicodeString());
    }

    @Test
    public void renameFieldWithEmptyName2DeepGetRenamed() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("root..", "newName");
        //ASSERT
        Assertions.assertEquals("root..newName", child2.getFieldName().toUnicodeString());
    }

    @Test
    public void renameFieldWithEmptyNameRootDeepGetRenamed() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("..", "newName");
        //ASSERT
        Assertions.assertEquals("..newName", child2.getFieldName().toUnicodeString());
    }

    @Test
    public void renameFieldRenameAllFromEmpty() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        acroForm.renameField("", "newName");
        Assertions.assertNull(acroForm.getField(""));
        Assertions.assertNotNull(acroForm.getField("newName"));

        acroForm.renameField("newName.", "newName");
        Assertions.assertEquals("newName.newName", child1.getFieldName().toUnicodeString());
        //ASSERT
        acroForm.renameField("newName.newName.", "newName");
        Assertions.assertEquals("newName.newName.newName", child2.getFieldName().toUnicodeString());
    }

    @Test
    public void renameMultipleTimesInLoop() {
        //ARRANGE
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "-1");
        root.addKid(child1);
        acroForm.addField(root);
        //ACT
        for (int i = 0; i < 100; i++) {
            acroForm.renameField("root." + (i - 1), "" + i);
            Assertions.assertEquals("root." + i, child1.getFieldName().toUnicodeString());
        }
    }


    @Test
    public void copyFieldWithEmptyNamesWork() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        acroForm.addField(root);
        PdfFormField copy = acroForm.copyField("root.");
        Assertions.assertNotNull( copy);
        Assertions.assertEquals("root.", copy.getFieldName().toUnicodeString());
    }

    @Test
    public void copyFieldWithEmptyNames2DeepWork() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        child1.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);
        PdfFormField copy = acroForm.copyField("root..");
        Assertions.assertNotNull(copy);
    }


    @Test
    public void replaceFieldReplacesItInTheChild() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child");
        root.addKid(child1);
        acroForm.addField(root);

        PdfFormField toReplace = addDefaultTextFormField(outputDoc, "toReplace");
        acroForm.replaceField("root.child", toReplace);
        Assertions.assertNull(acroForm.getField("toReplace"));
        Assertions.assertNotNull(acroForm.getField("root.toReplace"));
    }

    @Test
    public void replaceFieldReplacesItInTheChildWithChildNameEmpty() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        acroForm.addField(root);

        PdfFormField toReplace = addDefaultTextFormField(outputDoc, "toReplace");
        acroForm.replaceField("root.", toReplace);
        Assertions.assertNull(acroForm.getField("toReplace"));
        Assertions.assertNotNull(acroForm.getField("root.toReplace"));
    }

    @Test
    public void copyFormFieldWithoutName() throws IOException {

        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        acroForm.addField(root);

        originalDoc.close();

        InputStream i = new ByteArrayInputStream(f.toByteArray());
        PdfDocument loaded = new PdfDocument(new PdfReader(i));

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
            loaded.copyPagesTo(1, 1, newDoc, pdfPageFormCopier);
            PdfAcroForm form = PdfFormCreator.getAcroForm(outputDoc, false);
            Assertions.assertNotNull(form);
            Assertions.assertEquals(2, form.getAllFormFields().size());
            Assertions.assertNotNull(form.getField("root."));
            Assertions.assertNotNull(form.getField("root"));
        }
    }


    @Test
    public void copyFormFieldWithoutRootName() throws IOException {
        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

        PdfFormField root = addDefaultTextFormField(outputDoc, "");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");

        root.addKid(child1);
        acroForm.addField(root);

        originalDoc.close();

        InputStream i = new ByteArrayInputStream(f.toByteArray());
        PdfDocument loaded = new PdfDocument(new PdfReader(i));

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
            loaded.copyPagesTo(1, 1, newDoc, pdfPageFormCopier);
            PdfAcroForm form = PdfFormCreator.getAcroForm(outputDoc, false);
            Assertions.assertNotNull(form);
            Assertions.assertEquals(2, form.getAllFormFields().size());
            Assertions.assertNotNull(form.getField("."));
            Assertions.assertNotNull(form.getField(""));
        }
    }

    @Test
    public void copyFormFieldWithoutNameAdded2timesOverwritesTheFirst() throws IOException {

        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");

        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");

        root.addKid(child2);
        root.addKid(child1);
        acroForm.addField(root);

        originalDoc.close();

        InputStream i = new ByteArrayInputStream(f.toByteArray());
        PdfDocument loaded = new PdfDocument(new PdfReader(i));

        try (PdfDocument newDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfPageFormCopier pdfPageFormCopier = new PdfPageFormCopier();
            loaded.copyPagesTo(1, 1, newDoc, pdfPageFormCopier);
            PdfAcroForm form = PdfFormCreator.getAcroForm(outputDoc, false);
            Assertions.assertNotNull(form);
            Assertions.assertEquals(2, form.getAllFormFields().size());
            Assertions.assertNotNull(form.getField("root."));
            Assertions.assertNotNull(form.getField("root"));
        }
    }

    @Test
    public void addingSiblingsSameNameMergesFieldsTogether() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child1");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "child1");
        root.addKid(child1);
        root.addKid(child2);
        acroForm.addField(root);
        Assertions.assertEquals(2, root.getChildFields().size());
    }

    @Test
    public void addingSiblingsSameEmptyNamesMergesFieldsTogether() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        root.addKid(child2);
        acroForm.addField(root);
        Assertions.assertEquals(2, root.getChildFields().size());
    }

    private static PdfFormField addDefaultTextFormField(PdfDocument doc, String name) {
        return new TextFormFieldBuilder(doc, name)
                .setWidgetRectangle(new Rectangle(100, 100, 100, 100)).createText();
    }
}
