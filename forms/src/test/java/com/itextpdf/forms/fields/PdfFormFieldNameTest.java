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
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfFormFieldNameTest extends ExtendedITextTest {

    private PdfDocument outputDoc;
    private PdfAcroForm acroForm;

    @Before
    public void init() {
        outputDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        outputDoc.addNewPage();
        acroForm = PdfAcroForm.getAcroForm(outputDoc, true);
    }

    @After
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

        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("root.child1", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals("root.child1.child2", child2RecoveredField.getFieldName().toString());
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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals(".child1", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals(".child1.child2", child2RecoveredField.getFieldName().toString());
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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("ro\tot", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("ro\tot.child 1", child1RecoveredField.getFieldName().toString());
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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals("root..child2", child2RecoveredField.getFieldName().toString());

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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals("root..", child2RecoveredField.getFieldName().toString());
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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals("root..", child2RecoveredField.getFieldName().toString());
        Assert.assertEquals(child3, child3RecoveredField);
        Assert.assertEquals("root...child3", child3RecoveredField.getFieldName().toString());

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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("root", rootRecoveredField.getFieldName().toString());
        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals("root.", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals("root..child2", child2RecoveredField.getFieldName().toString());
        Assert.assertEquals(child3, child3RecoveredField);
        Assert.assertEquals("root..child2.", child3RecoveredField.getFieldName().toString());
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
        Assert.assertEquals(root, rootRecoveredField);
        Assert.assertEquals("", rootRecoveredField.getFieldName().toString());

        Assert.assertEquals(child1, child1RecoveredField);
        Assert.assertEquals(".child1", child1RecoveredField.getFieldName().toString());
        Assert.assertEquals(child2, child2RecoveredField);
        Assert.assertEquals(".child1.", child2RecoveredField.getFieldName().toString());
        Assert.assertEquals(child3, child3RecoveredField);
        Assert.assertEquals(".child1..child3", child3RecoveredField.getFieldName().toString());
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
        Assert.assertFalse(rootRecoveredField.getChildFields().contains(child1));
        Assert.assertTrue(rootRecoveredField.getChildFields().contains(child2));
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
        Assert.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assert.assertFalse(rootRecoveredField.getChildFields().contains(child2));
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
        Assert.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assert.assertFalse(rootRecoveredField.getChildFields().contains(child2));
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
        Assert.assertTrue(rootRecoveredField.getChildFields().contains(child1));
        Assert.assertFalse(rootRecoveredField.getChildFields().contains(child2));
        Assert.assertEquals(sizeBeforeRemoval - 1, acroForm.getAllFormFields().size());

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
        Assert.assertEquals(0, acroForm.getAllFormFields().size());
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
        Assert.assertEquals("root.", child1.getFieldName().toUnicodeString());
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
        Assert.assertEquals(".", child1.getFieldName().toUnicodeString());
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
        Assert.assertEquals("root.newName", child1.getFieldName().toUnicodeString());
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
        Assert.assertEquals(".newName", child1.getFieldName().toUnicodeString());
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
        Assert.assertEquals("root..newName", child2.getFieldName().toUnicodeString());
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
        Assert.assertEquals("..newName", child2.getFieldName().toUnicodeString());
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
        Assert.assertNull(acroForm.getField(""));
        Assert.assertNotNull(acroForm.getField("newName"));

        acroForm.renameField("newName.", "newName");
        Assert.assertEquals("newName.newName", child1.getFieldName().toUnicodeString());
        //ASSERT
        acroForm.renameField("newName.newName.", "newName");
        Assert.assertEquals("newName.newName.newName", child2.getFieldName().toUnicodeString());
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
            Assert.assertEquals("root." + i, child1.getFieldName().toUnicodeString());
        }
    }


    @Test
    public void copyFieldWithEmptyNamesWork() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        acroForm.addField(root);
        PdfFormField copy = acroForm.copyField("root.");
        Assert.assertNotNull( copy);
        Assert.assertEquals("root.", copy.getFieldName().toUnicodeString());
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
        Assert.assertNotNull(copy);
    }


    @Test
    public void replaceFieldReplacesItInTheChild() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "child");
        root.addKid(child1);
        acroForm.addField(root);

        PdfFormField toReplace = addDefaultTextFormField(outputDoc, "toReplace");
        acroForm.replaceField("root.child", toReplace);
        Assert.assertNull(acroForm.getField("toReplace"));
        Assert.assertNotNull(acroForm.getField("root.toReplace"));
    }

    @Test
    public void replaceFieldReplacesItInTheChildWithChildNameEmpty() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        acroForm.addField(root);

        PdfFormField toReplace = addDefaultTextFormField(outputDoc, "toReplace");
        acroForm.replaceField("root.", toReplace);
        Assert.assertNull(acroForm.getField("toReplace"));
        Assert.assertNotNull(acroForm.getField("root.toReplace"));
    }

    @Test
    public void copyFormFieldWithoutName() throws IOException {

        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);

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
            PdfAcroForm form = PdfAcroForm.getAcroForm(outputDoc, false);
            Assert.assertNotNull(form);
            Assert.assertEquals(2, form.getAllFormFields().size());
            Assert.assertNotNull(form.getField("root."));
            Assert.assertNotNull(form.getField("root"));
        }
    }


    @Test
    public void copyFormFieldWithoutRootName() throws IOException {
        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);

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
            PdfAcroForm form = PdfAcroForm.getAcroForm(outputDoc, false);
            Assert.assertNotNull(form);
            Assert.assertEquals(2, form.getAllFormFields().size());
            Assert.assertNotNull(form.getField("."));
            Assert.assertNotNull(form.getField(""));
        }
    }

    @Test
    public void copyFormFieldWithoutNameAdded2timesOverwritesTheFirst() throws IOException {

        ByteArrayOutputStream f = new ByteArrayOutputStream();
        PdfDocument originalDoc = new PdfDocument(new PdfWriter(f));
        originalDoc.addNewPage();
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(outputDoc, true);

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
            PdfAcroForm form = PdfAcroForm.getAcroForm(outputDoc, false);
            Assert.assertNotNull(form);
            Assert.assertEquals(2, form.getAllFormFields().size());
            Assert.assertNotNull(form.getField("root."));
            Assert.assertNotNull(form.getField("root"));
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
        Assert.assertEquals(2, root.getChildFields().size());
    }

    @Test
    public void addingSiblingsSameEmptyNamesMergesFieldsTogether() {
        PdfFormField root = addDefaultTextFormField(outputDoc, "root");
        PdfFormField child1 = addDefaultTextFormField(outputDoc, "");
        PdfFormField child2 = addDefaultTextFormField(outputDoc, "");
        root.addKid(child1);
        root.addKid(child2);
        acroForm.addField(root);
        Assert.assertEquals(2, root.getChildFields().size());
    }

    private static PdfFormField addDefaultTextFormField(PdfDocument doc, String name) {
        return new TextFormFieldBuilder(doc, name)
                .setWidgetRectangle(new Rectangle(100, 100, 100, 100)).createText();
    }
}
