/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.forms.form.element;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class AccessibleElementTest extends ExtendedITextTest {
    public static Collection<Object[]> getDataTestFixtureData() {
        int amountOfEntries = 8;
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < amountOfEntries; i++) {
            data.add(new Object[]{new TestContainer(i)});
        }
        return data;
    }

    private Supplier<IFormField> getDataToTest(int index) {
        switch (index) {
            case 0:
                return () -> new InputField("inputField");
            case 1:
                return () -> new TextArea("textArea");
            case 2:
                return () -> new Radio("radioButton", "group");
            case 3:
                ComboBoxField field = new ComboBoxField("comboBox");
                field.addOption(new SelectFieldItem("option1"));
                field.addOption(new SelectFieldItem("option2"));
                return () -> field;
            case 4:
                ListBoxField field2 = new ListBoxField("listBox", 4, false);
                field2.addOption(new SelectFieldItem("option1"));
                field2.addOption(new SelectFieldItem("option2"));
                return () -> field2;
            case 5:
                return () -> new SignatureFieldAppearance("signatureField");
            case 6:
                return () -> new Button("button");
            case 7:
                return () -> new CheckBox("checkBox");
            default:
                throw new IllegalArgumentException("Invalid index");
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getDataTestFixtureData")
    public void testInteractive(TestContainer testContainer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        IFormField element = getDataToTest(testContainer.index).get();
        element.setInteractive(true);
        IAccessibleElement accessibleElement = (IAccessibleElement) element;
        accessibleElement.getAccessibilityProperties().setLanguage("en");

        document.add((IBlockElement) element);

        PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
        IStructureNode documentStruct = root.getKids().get(0);
        IStructureNode kid = documentStruct.getKids().get(0);
        PdfStructElem elem = (PdfStructElem) kid;
        PdfDictionary obj = elem.getPdfObject();
        Assertions.assertEquals(PdfName.Form, elem.getRole());
        Assertions.assertTrue(obj.containsKey(PdfName.Lang));
        Assertions.assertEquals("en", obj.getAsString(PdfName.Lang).getValue());
        document.close();
        pdfDocument.close();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getDataTestFixtureData")
    public void testNonInteractive(TestContainer testContainer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        IFormField element = getDataToTest(testContainer.index).get();
        IAccessibleElement accessibleElement = (IAccessibleElement) element;
        accessibleElement.getAccessibilityProperties().setLanguage("en");

        document.add((IBlockElement) element);

        PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
        IStructureNode documentStruct = root.getKids().get(0);
        IStructureNode kid = documentStruct.getKids().get(0);
        PdfStructElem elem = (PdfStructElem) kid;
        PdfDictionary obj = elem.getPdfObject();
        Assertions.assertEquals(PdfName.Form, elem.getRole());
        Assertions.assertTrue(obj.containsKey(PdfName.Lang));
        Assertions.assertEquals("en", obj.getAsString(PdfName.Lang).getValue());
        document.close();
        pdfDocument.close();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getDataTestFixtureData")
    public void testInteractiveProperty(TestContainer testContainer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        IFormField element = getDataToTest(testContainer.index).get();

        IFormField formField = (IFormField) element;
        formField.setInteractive(true);
        IAccessibleElement accessibleElement = (IAccessibleElement) formField;
        accessibleElement.getAccessibilityProperties().setLanguage("en");

        document.add((IBlockElement) element);

        PdfStructTreeRoot root = pdfDocument.getStructTreeRoot();
        IStructureNode documentStruct = root.getKids().get(0);
        IStructureNode kid = documentStruct.getKids().get(0);
        PdfStructElem elem = (PdfStructElem) kid;
        PdfDictionary obj = elem.getPdfObject();
        Assertions.assertEquals(PdfName.Form, elem.getRole());
        Assertions.assertTrue(obj.containsKey(PdfName.Lang));
        Assertions.assertEquals("en", obj.getAsString(PdfName.Lang).getValue());
        document.close();
        pdfDocument.close();
    }

    public static class TestContainer {

        public final int index;

        public TestContainer(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            switch (index) {
                case 0:
                    return "InputField";
                case 1:
                    return "TextArea";
                case 2:
                    return "Radio";
                case 3:
                    return "ComboBox";
                case 4:
                    return "ListBox";
                case 5:
                    return "SignatureField";
                case 6:
                    return "Button";
                case 7:
                    return "CheckBox";
                default:
                    throw new IllegalArgumentException("Invalid index");
            }
        }
    }
}

