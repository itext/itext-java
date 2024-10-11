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
package com.itextpdf.forms;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.fields.AbstractPdfFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.fields.PdfFormAnnotationUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.util.List;

@Tag("UnitTest")
public class PdfAcroFormTest extends ExtendedITextTest {

    @Test
    public void setSignatureFlagsTest() {
        try(PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.setSignatureFlags(65);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject sigFlags = acroForm.getPdfObject().get(PdfName.SigFlags);
            outputDoc.close();

            Assertions.assertEquals(new PdfNumber(65), sigFlags);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void addChildToFormFieldTest() {
        try (PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            acroForm.addField(field);
            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText();
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText();
            root.addKid(child);
            acroForm.addField(root);
            Assertions.assertEquals(2, acroForm.fields.size());
            PdfArray fieldKids = root.getKids();
            Assertions.assertEquals(2, fieldKids.size());
        }
    }

    @Test
    public void addChildToWidgetTest() {
        try (PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfArray options = new PdfArray();
            options.add(new PdfString("1"));
            options.add(new PdfString("2"));
            PdfTextFormField text = new TextFormFieldBuilder(outputDoc, "text")
                    .setWidgetRectangle(new Rectangle(36, 696, 20, 20)).createText();
            PdfTextFormField childText = new TextFormFieldBuilder(outputDoc, "childText")
                    .setWidgetRectangle(new Rectangle(36, 696, 20, 20)).createText();
            text.addKid(childText);
            acroForm.addField(text);
            Assertions.assertEquals(1, acroForm.fields.size());
            List<AbstractPdfFormField> fieldKids = text.getChildFields();
            Assertions.assertEquals(2, fieldKids.size());
        }
    }

    @Test
    public void getFormFieldChildTest() {
        try(PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            acroForm.addField(field);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText();
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText();
            root.addKid(child);
            acroForm.addField(root);
            PdfFormField childField = acroForm.getField("root.child");
            Assertions.assertEquals("root.child", childField.getFieldName().toString());
        }
    }

    @Test
    public void getFormFieldWithEqualChildNamesTest() {
        try(PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            acroForm.addField(field);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText();
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "field")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText();
            PdfFormField child1 = new TextFormFieldBuilder(outputDoc, "field")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText();
            PdfFormField child2 = new TextFormFieldBuilder(outputDoc, "another_name")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText();
            child1.addKid(child2);
            child.addKid(child1);
            root.addKid(child);
            acroForm.addField(root);
            PdfFormField childField = acroForm.getField("root.field.field.another_name");
            Assertions.assertEquals("root.field.field.another_name", childField.getFieldName().toString());
        }
    }

    @Test
    public void changeFieldNameTest() {
        try(PdfDocument outputDoc = createDocument()) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText();
            acroForm.addField(field);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText();
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText();
            root.addKid(child);
            acroForm.addField(root);
            acroForm.getField("root").setFieldName("diff");
            PdfFormField childField = PdfFormCreator.getAcroForm(outputDoc, true).getField("diff.child");
            Assertions.assertEquals("diff.child", childField.getFieldName().toString());
        }
    }

    @Test
    public void removeChildFromFormFieldTest() {
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText().setValue("text1");
            acroForm.addField(field);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText().setValue("root");
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText().setValue("child");
            PdfFormField child1 = new TextFormFieldBuilder(outputDoc, "aaaaa")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20)).createText().setValue("aaaaa");
            PdfFormField child2 = new TextFormFieldBuilder(outputDoc, "bbbbb")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("bbbbb");
            child1.addKid(child2);
            child.addKid(child1);
            root.addKid(child);
            acroForm.addField(root);
            acroForm.removeField("root.child.aaaaa");
            Assertions.assertEquals(2, acroForm.fields.size());
            Assertions.assertEquals(2, root.getKids().size());
        }
    }

    @Test
    public void getChildFromFormFieldWithDifferentAmountOfChildrenTest() {
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfFormField field = new TextFormFieldBuilder(outputDoc, "text1")
                    .setWidgetRectangle(new Rectangle(100, 700, 200, 20)).createText().setValue("text1");
            acroForm.addField(field);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 20)).createText().setValue("root");
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child")
                    .setWidgetRectangle(new Rectangle(100, 300, 200, 20)).createText().setValue("child");
            PdfFormField child1 = new TextFormFieldBuilder(outputDoc, "aaaaa")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20)).createText().setValue("aaaaa");
            PdfFormField child2 = new TextFormFieldBuilder(outputDoc, "bbbbb")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("bbbbb");
            PdfFormField child3 = new TextFormFieldBuilder(outputDoc, "child1")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("child1");
            PdfFormField child4 = new TextFormFieldBuilder(outputDoc, "child2")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("child2");
            PdfFormField child5 = new TextFormFieldBuilder(outputDoc, "child2")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText().setValue("child2");
            child1.addKid(child2);
            child1.addKid(child3);
            child1.addKid(child4);
            child4.addKid(child5);
            child.addKid(child1);
            root.addKid(child);
            acroForm.addField(root);
            PdfFormField childField = acroForm.getField("root.child.aaaaa.child2");
            Assertions.assertEquals("root.child.aaaaa.child2", childField.getFieldName().toString());

            Assertions.assertEquals(2, acroForm.getFields().size());
            Assertions.assertEquals(2, root.getKids().size());
        }
    }

    @Test
    public void checkFormFieldsSizeTest() {
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            Assertions.assertEquals(0, acroForm.getAllFormFields().size());
            Assertions.assertEquals(0, acroForm.getAllFormFieldsAndAnnotations().size());

            PdfDictionary fieldDict = new PdfDictionary();
            fieldDict.put(PdfName.FT, PdfName.Tx);
            PdfFormField field = PdfFormField.makeFormField(fieldDict.makeIndirect(outputDoc), outputDoc);
            field.setFieldName("Field1");
            acroForm.addField(field);
            Assertions.assertEquals(1, acroForm.getAllFormFields().size());
            Assertions.assertEquals(1, acroForm.getAllFormFieldsAndAnnotations().size());

            PdfDictionary annotDict = new PdfDictionary();
            annotDict.put(PdfName.Subtype, PdfName.Widget);
            field.addKid(PdfFormAnnotation.makeFormAnnotation(annotDict, outputDoc));
            Assertions.assertEquals(1, acroForm.getAllFormFields().size());
            Assertions.assertEquals(2, acroForm.getAllFormFieldsAndAnnotations().size());
        }
    }

    @Test
    public void fieldKidsWithTheSameNamesTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root").createText().setValue("root");
            PdfFormField child = new TextFormFieldBuilder(outputDoc, "child").createText()
                    .setValue("child");
            PdfFormField sameChild = new TextFormFieldBuilder(outputDoc, "child").createText()
                    .setValue("child");
            PdfFormField child1 = new TextFormFieldBuilder(outputDoc, "aaaaa")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20)).createText()
                    .setValue("aaaaa");
            PdfFormField child2 = new TextFormFieldBuilder(outputDoc, "bbbbb")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText()
                    .setValue("bbbbb");
            PdfFormField child3 = new TextFormFieldBuilder(outputDoc, "aaaaa")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText()
                    .setValue("aaaaa");

            child.addKid(child1);
            child.addKid(child2);
            sameChild.addKid(child2);
            sameChild.addKid(child3);
            root.addKid(child);
            root.addKid(sameChild);
            acroForm.addField(root);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(1, root.getKids().size());
            Assertions.assertEquals(2, child.getKids().size());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.FORM_FIELD_MUST_HAVE_A_NAME))
    public void namelessFieldTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfDictionary fieldDict = new PdfDictionary();
            fieldDict.put(PdfName.FT, PdfName.Tx);
            PdfFormField field = PdfFormField.makeFormField(fieldDict.makeIndirect(outputDoc), outputDoc);
            Exception e = Assertions.assertThrows(PdfException.class, () -> acroForm.addField(field));
            Assertions.assertEquals(FormsExceptionMessageConstant.FORM_FIELD_MUST_HAVE_A_NAME, e.getMessage());

            outputDoc.addNewPage();
            PdfPage page = outputDoc.getLastPage();
            e = Assertions.assertThrows(PdfException.class, () -> acroForm.addField(field, page));
            Assertions.assertEquals(FormsExceptionMessageConstant.FORM_FIELD_MUST_HAVE_A_NAME, e.getMessage());

            acroForm.addField(field, page, false);

            Assertions.assertEquals(0, acroForm.getRootFormFields().size());
        }
    }

    @Test
    public void addRootFieldsWithTheSameNamesTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField root = new TextFormFieldBuilder(outputDoc, "root").createText().setValue("root");
            PdfFormField sameRoot = new TextFormFieldBuilder(outputDoc, "root").createText().setValue("root");

            PdfFormField child1 = new TextFormFieldBuilder(outputDoc, "aaaaa")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20)).createText()
                    .setValue("aaaaa");
            PdfFormField child2 = new TextFormFieldBuilder(outputDoc, "bbbbb")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 20)).createText()
                    .setValue("bbbbb");

            root.addKid(child1);
            sameRoot.addKid(child2);
            acroForm.addField(root);
            acroForm.addField(sameRoot);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(2, root.getKids().size());
        }
    }

    @Test
    public void addMergedRootFieldsWithTheSameNamesTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20))
                    .createText().setValue("root");
            PdfFormField secondField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30))
                    .createText().setValue("root");

            acroForm.addField(firstField);
            acroForm.addField(secondField);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(2, acroForm.getField("root").getKids().size());
            Assertions.assertEquals(2, acroForm.getField("root").getChildFields().size());
        }
    }

    @Test
    public void addFieldsWithTheSameNamesButDifferentValuesTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "root")
                    .createText().setValue("first");
            PdfFormField secondField = new TextFormFieldBuilder(outputDoc, "root")
                    .createText().setValue("second");

            acroForm.addField(firstField);

            Exception e = Assertions.assertThrows(PdfException.class, () -> acroForm.addField(secondField));
            Assertions.assertEquals(MessageFormatUtil.format(FormsExceptionMessageConstant.CANNOT_MERGE_FORMFIELDS, "root"),
                    e.getMessage());
        }
    }

    @Test
    public void addRootFieldWithMergedFieldKidTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "root")
                    .createText().setValue("root");
            PdfFormField mergedField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30))
                    .createText();

            firstField.addKid(mergedField);
            acroForm.addField(firstField);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(1, acroForm.getField("root").getKids().size());

            Assertions.assertTrue(PdfFormAnnotationUtil.isPureWidgetOrMergedField(
                    (PdfDictionary) acroForm.getField("root").getKids().get(0)));
            Assertions.assertFalse(PdfFormAnnotationUtil.isPureWidget(
                    (PdfDictionary) acroForm.getField("root").getKids().get(0)));
        }
    }

    @Test
    public void addRootFieldWithDirtyNamedAnnotationsTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField rootField = new TextFormFieldBuilder(outputDoc, "root")
                    .createText().setValue("root");
            PdfFormField firstDirtyAnnot = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30))
                    .createText();
            firstDirtyAnnot.getPdfObject().remove(PdfName.V);
            PdfFormField secondDirtyAnnot = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(200, 600, 300, 40))
                    .createText();
            secondDirtyAnnot.getPdfObject().remove(PdfName.V);

            rootField.addKid(firstDirtyAnnot);
            rootField.addKid(secondDirtyAnnot);

            Assertions.assertEquals(1, rootField.getKids().size());
            Assertions.assertEquals(2, firstDirtyAnnot.getKids().size());

            acroForm.addField(rootField);

            Assertions.assertEquals(1, acroForm.getFields().size());

            PdfArray fieldKids = acroForm.getField("root").getKids();
            Assertions.assertEquals(1, fieldKids.size());

            Assertions.assertFalse(PdfFormAnnotationUtil.isPureWidget((PdfDictionary) fieldKids.get(0)));
        }
    }

    @Test
    public void addRootFieldWithDirtyUnnamedAnnotationsTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField rootField = new TextFormFieldBuilder(outputDoc, "root")
                    .createText().setValue("root");
            PdfFormField firstDirtyAnnot = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30))
                    .createText();
            firstDirtyAnnot.getPdfObject().remove(PdfName.V);
            // Remove name in order to make dirty annotation being merged
            firstDirtyAnnot.getPdfObject().remove(PdfName.T);
            
            PdfFormField secondDirtyAnnot = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(200, 600, 300, 40))
                    .createText();
            secondDirtyAnnot.getPdfObject().remove(PdfName.V);
            // Remove name in order to make dirty annotation being merged
            secondDirtyAnnot.getPdfObject().remove(PdfName.T);

            rootField.addKid(firstDirtyAnnot);
            rootField.addKid(secondDirtyAnnot);

            Assertions.assertEquals(1, rootField.getKids().size());
            Assertions.assertEquals(2, firstDirtyAnnot.getKids().size());

            acroForm.addField(rootField);

            Assertions.assertEquals(1, acroForm.getFields().size());

            PdfArray fieldKids = acroForm.getField("root").getKids();
            Assertions.assertEquals(2, fieldKids.size());

            Assertions.assertTrue(PdfFormAnnotationUtil.isPureWidget((PdfDictionary) fieldKids.get(0)));
            Assertions.assertTrue(PdfFormAnnotationUtil.isPureWidget((PdfDictionary) fieldKids.get(1)));
        }
    }

    @Test
    public void mergeFieldsWhenKidsWasFlushedTest() {
        try (PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField firstField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 400, 200, 20)).setPage(1)
                    .createText().setValue("root");
            PdfFormField secondField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30)).setPage(2)
                    .createText().setValue("root");
            PdfFormField thirdField = new TextFormFieldBuilder(outputDoc, "root")
                    .setWidgetRectangle(new Rectangle(100, 600, 200, 40)).setPage(2)
                    .createText().setValue("root");

            acroForm.addField(firstField);
            acroForm.addField(secondField);

            // flush first page, also first widget will be flushed
            outputDoc.getPage(1).flush();

            // recreate acroform and add field
            acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.addField(thirdField);

            Assertions.assertEquals(1, acroForm.getFields().size());
            Assertions.assertEquals(3, acroForm.getField("root").getKids().size());
            Assertions.assertEquals(2, acroForm.getField("root").getChildFields().size());
        }
    }

    @Test
    public void addMergedRootFieldTest() {
        try (PdfDocument outputDoc = createDocument()) {
            PdfPage page = outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfFormField mergedField = new TextFormFieldBuilder(outputDoc, "root").setPage(page)
                    .setWidgetRectangle(new Rectangle(100, 500, 200, 30))
                    .createText().setValue("root");

            Assertions.assertEquals(0, page.getAnnotsSize());
            acroForm.addField(mergedField);
            Assertions.assertEquals(1, page.getAnnotsSize());

            Assertions.assertEquals(1, acroForm.getFields().size());
            PdfFormField root = acroForm.getField("root");
            Assertions.assertNull(root.getKids());
            Assertions.assertTrue(PdfFormAnnotationUtil.isPureWidgetOrMergedField(root.getPdfObject()));
            Assertions.assertFalse(PdfFormAnnotationUtil.isPureWidget(root.getPdfObject()));
        }
    }

    @Test
    public void setCalculationOrderTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfArray calculationOrderArray = new PdfArray(new int[] {1, 0});
            acroForm.setCalculationOrder(calculationOrderArray);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject calculationOrder = acroForm.getPdfObject().get(PdfName.CO);
            outputDoc.close();

            Assertions.assertEquals(calculationOrderArray, calculationOrder);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setDefaultAppearanceTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.setDefaultAppearance("default appearance");

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject calculationOrder = acroForm.getPdfObject().get(PdfName.DA);
            outputDoc.close();

            Assertions.assertEquals(new PdfString("default appearance"), calculationOrder);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setDefaultJustificationTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.setDefaultJustification(14);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject defaultJustification = acroForm.getPdfObject().get(PdfName.Q);
            outputDoc.close();

            Assertions.assertEquals(new PdfNumber(14), defaultJustification);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setDefaultResourcesTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);

            PdfDictionary dictionary = new PdfDictionary();
            PdfFormCreator.getAcroForm(outputDoc, true).setDefaultResources(dictionary);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject defaultResourcesDict = acroForm.getPdfObject().get(PdfName.DR);
            outputDoc.close();

            Assertions.assertEquals(dictionary, defaultResourcesDict);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setNeedAppearancesTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.setNeedAppearances(false);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject needAppearance = acroForm.getPdfObject().get(PdfName.NeedAppearances);

            outputDoc.close();

            Assertions.assertEquals(new PdfBoolean(false), needAppearance);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "NeedAppearances has been deprecated in PDF 2.0. Appearance streams are required in PDF 2.0."))
    public void setNeedAppearancesInPdf2Test() {
        PdfDocument outputDoc = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        outputDoc.addNewPage();

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
        acroForm.setNeedAppearances(false);

        boolean isModified = acroForm.getPdfObject().isModified();
        boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
        PdfObject needAppearance = acroForm.getPdfObject().get(PdfName.NeedAppearances);

        outputDoc.close();

        Assertions.assertNull(needAppearance);
        Assertions.assertTrue(isModified);
        Assertions.assertTrue(isReleaseForbidden);
    }

    @Test
    public void setGenerateAppearanceTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            acroForm.setNeedAppearances(false);
            acroForm.setGenerateAppearance(true);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            boolean isGenerateAppearance = acroForm.isGenerateAppearance();
            Object needAppearances = acroForm.getPdfObject().get(PdfName.NeedAppearances);
            outputDoc.close();

            Assertions.assertNull(needAppearances);
            Assertions.assertTrue(isGenerateAppearance);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setXFAResourcePdfArrayTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfArray array = new PdfArray();
            acroForm.setXFAResource(array);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject xfaObject = acroForm.getPdfObject().get(PdfName.XFA);
            outputDoc.close();

            Assertions.assertEquals(array, xfaObject);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public void setXFAResourcePdfStreamTest() {
        try (PdfDocument outputDoc = createDocument()) {

            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfStream stream = new PdfStream();
            acroForm.setXFAResource(stream);

            boolean isModified = acroForm.getPdfObject().isModified();
            boolean isReleaseForbidden = acroForm.getPdfObject().isReleaseForbidden();
            PdfObject xfaObject = acroForm.getPdfObject().get(PdfName.XFA);
            outputDoc.close();

            Assertions.assertEquals(stream, xfaObject);
            Assertions.assertTrue(isModified);
            Assertions.assertTrue(isReleaseForbidden);
        }
    }

    @Test
    public  void replaceFormFieldRootLevelReplacesExistingFieldTest() {
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfDictionary fieldDict = new PdfDictionary();
            fieldDict.put(PdfName.FT, PdfName.Tx);
            fieldDict.put(PdfName.T, new PdfString("field1"));
            PdfFormField field = PdfFormField.makeFormField(fieldDict.makeIndirect(outputDoc), outputDoc);

            assert field != null;
            acroForm.addField(field);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());


            PdfDictionary fieldDictReplace = new PdfDictionary();
            fieldDictReplace.put(PdfName.FT, PdfName.Tx);
            fieldDictReplace.put(PdfName.T, new PdfString("field2"));
            PdfFormField fieldReplace = PdfFormField.makeFormField(fieldDictReplace.makeIndirect(outputDoc), outputDoc);

            acroForm.replaceField("field1", fieldReplace);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());
            Assertions.assertEquals("field2", acroForm.getField("field2").getFieldName().toUnicodeString());

        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.PROVIDE_FORMFIELD_NAME))
    public void replaceWithNullNameLogsErrorTest(){
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfDictionary fieldDict = new PdfDictionary();
            fieldDict.put(PdfName.FT, PdfName.Tx);
            fieldDict.put(PdfName.T, new PdfString("field1"));
            PdfFormField field = PdfFormField.makeFormField(fieldDict.makeIndirect(outputDoc), outputDoc);

            assert field != null;
            acroForm.addField(field);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());


            PdfDictionary fieldDictReplace = new PdfDictionary();
            fieldDictReplace.put(PdfName.FT, PdfName.Tx);
            fieldDictReplace.put(PdfName.T, new PdfString("field2"));
            PdfFormField fieldReplace = PdfFormField.makeFormField(fieldDictReplace.makeIndirect(outputDoc), outputDoc);

            acroForm.replaceField(null, fieldReplace);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());
        }

    }

    @Test
    public  void replaceFormFieldOneDeepReplacesExistingFieldTest() {
        try(PdfDocument outputDoc = createDocument()) {
            outputDoc.addNewPage();
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(outputDoc, true);
            PdfDictionary fieldDict = new PdfDictionary();
            fieldDict.put(PdfName.FT, PdfName.Tx);
            fieldDict.put(PdfName.T, new PdfString("field1"));
            PdfFormField field = PdfFormField.makeFormField(fieldDict.makeIndirect(outputDoc), outputDoc);

            PdfDictionary fieldDictChild = new PdfDictionary();
            fieldDictChild.put(PdfName.FT, PdfName.Tx);
            fieldDictChild.put(PdfName.T, new PdfString("child1"));
            PdfFormField fieldChild = PdfFormField.makeFormField(fieldDictChild.makeIndirect(outputDoc), outputDoc);
            assert field != null;
            assert fieldChild != null;

            field.addKid(fieldChild);

            acroForm.addField(field);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());


            PdfDictionary fieldDictReplace = new PdfDictionary();
            fieldDictReplace.put(PdfName.FT, PdfName.Tx);
            fieldDictReplace.put(PdfName.T, new PdfString("field2"));
            PdfFormField fieldReplace = PdfFormField.makeFormField(fieldDictReplace.makeIndirect(outputDoc), outputDoc);

            acroForm.replaceField("field1.child1", fieldReplace);
            Assertions.assertEquals(1, acroForm.getRootFormFields().size());
            Assertions.assertEquals("field1.field2", acroForm.getField("field1.field2").getFieldName().toUnicodeString());

        }
    }


    private static PdfDocument createDocument() {
        PdfDocument outputDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        outputDoc.addNewPage();
        return outputDoc;
    }
}
