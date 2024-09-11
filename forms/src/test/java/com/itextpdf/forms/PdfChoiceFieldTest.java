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

import com.itextpdf.forms.fields.ChoiceFormFieldBuilder;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfChoiceFieldTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfChoiceFieldTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfChoiceFieldTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void choiceFieldsWithUnicodeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "choiceFieldsWithUnicodeTest.pdf";
        String cmpPdf = sourceFolder + "cmp_choiceFieldsWithUnicodeTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
        font.setSubset(false);

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        pdfDoc.addNewPage();

        // 规
        PdfFormField field = new ChoiceFormFieldBuilder(pdfDoc, "combo1")
                .setWidgetRectangle(new Rectangle(36, 666, 40, 80)).setOptions(new String[]{"\u89c4", "\u89c9"})
                .setConformance(null).createComboBox()
                .setValue("\u89c4");
        field.setFont(font);
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        form.addField(field);

        // 觉
        field = new ChoiceFormFieldBuilder(pdfDoc, "combo2")
                .setWidgetRectangle(new Rectangle(136, 666, 40, 80)).setOptions(new String[]{"\u89c4", "\u89c9"})
                .setConformance(null).createComboBox();
        field.setValue("\u89c4").setFont(font);
        field.setValue("\u89c9");
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        form.addField(field);

        // 规
        field = new ChoiceFormFieldBuilder(pdfDoc, "list1")
                .setWidgetRectangle(new Rectangle(236, 666, 50, 80)).setOptions(new String[]{"\u89c4", "\u89c9"})
                .setConformance(null).createList()
                .setValue("\u89c4");
        field.setFont(font);
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        form.addField(field);

        // 觉
        field = new ChoiceFormFieldBuilder(pdfDoc, "list2")
                .setWidgetRectangle(new Rectangle(336, 666, 50, 80)).setOptions(new String[]{"\u89c4", "\u89c9"})
                .setConformance(null).createList();
        field.setValue("\u89c4").setFont(font);
        field.setValue("\u89c9");
        field.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void choiceFieldsSetValueTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "choiceFieldsWithUnnecessaryIEntries.pdf";
        String outPdf = destinationFolder + "choiceFieldsSetValueTest.pdf";
        String cmpPdf = sourceFolder + "cmp_choiceFieldsSetValueTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, false);
        PdfFormField firstField = form.getField("First");
        PdfFormField secondField = form.getField("Second");
        firstField.setValue("First");
        secondField.setValue("Second");

        PdfArray indicesFirst = ((PdfChoiceFormField) firstField).getIndices();
        PdfArray indicesSecond = ((PdfChoiceFormField) secondField).getIndices();
        PdfArray expectedIndicesFirst = new PdfArray(new int[] {1});
        PdfArray expectedIndicesSecond = new PdfArray(new int[] {2});

        CompareTool compareTool = new CompareTool();
        Assertions.assertTrue(compareTool.compareArrays(indicesFirst, expectedIndicesFirst));
        Assertions.assertTrue(compareTool.compareArrays(indicesSecond, expectedIndicesSecond));
        pdfDocument.close();

        Assertions.assertNull(compareTool.compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void comboNoHighlightCenteredTextOfChosenFirstItemTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "comboNoHighlightCenteredTextOfChosenFirstItemTest.pdf";
        String outPdf = destinationFolder + "comboNoHighlightCenteredTextOfChosenFirstItemTest.pdf";
        String cmpPdf = sourceFolder + "cmp_comboNoHighlightCenteredTextOfChosenFirstItemTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, false);
        form.getField("First").setValue("Default");
        // flattening is only used for the sake of ease to see what appearance is generated by iText
        form.flattenFields();
        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void noWarningOnValueNotOfOptComboEditTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "noWarningOnValueNotOfOptComboEditTest.pdf";
        String outPdf = destinationFolder + "noWarningOnValueNotOfOptComboEditTest.pdf";
        String cmpPdf = sourceFolder + "cmp_noWarningOnValueNotOfOptComboEditTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, false);
        form.getField("First").setValue("Value not of /Opt array");
        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY, count = 2)})
    public void multiSelectByValueTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "multiSelectByValueTest.pdf";
        String cmpPdf = sourceFolder + "cmp_multiSelectByValueTest.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outPdf));
        document.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);
        PdfChoiceFormField choice = (PdfChoiceFormField) new ChoiceFormFieldBuilder(document, "choice")
                .setWidgetRectangle(new Rectangle(336, 666, 50, 80)).setOptions(new String[]{"one", "two", "three", "four"})
                .setConformance(null).createList()
                .setValue("two").setFont(null);
        choice.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        choice.setMultiSelect(true);

        choice.setListSelected(new String[] {"one", "three", "eins", "drei"});

        Assertions.assertArrayEquals(new int[] {0, 2}, choice.getIndices().toIntArray());
        PdfArray values = (PdfArray) choice.getValue();
        String[] valuesAsStrings = new String [values.size()];
        for (int i = 0; i < values.size(); i++) {
            valuesAsStrings[i] = values.getAsString(i).toUnicodeString();
        }
        Assertions.assertArrayEquals(new String[] {"one", "three", "eins", "drei"}, valuesAsStrings);

        form.addField(choice);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void corruptedOptAndValueSetToNullTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "corruptedOptAndValueSetToNullTest.pdf";
        String outPdf = destinationFolder + "corruptedOptAndValueSetToNullTest.pdf";
        String cmpPdf = sourceFolder + "cmp_corruptedOptAndValueSetToNullTest.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(document, false);
        PdfChoiceFormField choice = (PdfChoiceFormField) form.getField("choice");
        choice.setListSelected(new String[] {null, "three"});

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FIELD_VALUE_IS_NOT_CONTAINED_IN_OPT_ARRAY)})
    public void multiSelectByValueRemoveIKeyTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "listWithPreselectedValue.pdf";
        String outPdf = destinationFolder + "selectByValueRemoveIKeyTest.pdf";
        String cmpPdf = sourceFolder + "cmp_selectByValueRemoveIKeyTest.pdf";
        String value = "zwei";
        PdfDocument document = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        document.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);
        PdfFormField field = form.getField("choice");
        field.setValue(value);
        Assertions.assertNull(field.getPdfObject().get(PdfName.I));
        CompareTool compareTool = new CompareTool();
        Assertions.assertTrue(compareTool.compareStrings(new PdfString(value), field.getPdfObject().getAsString(PdfName.V)));
        document.close();
        Assertions.assertNull(compareTool.compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void multiSelectByIndexOutOfBoundsTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "multiSelectTest.pdf";
        String outPdf = destinationFolder + "multiSelectByIndexOutOfBoundsTest.pdf";
        String cmpPdf = sourceFolder + "cmp_multiSelectByIndexOutOfBoundsTest.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, false);
        PdfChoiceFormField field = (PdfChoiceFormField) form.getField("choice");
        field.setListSelected(new int[] {5});
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void notInstanceOfPdfChoiceFormFieldTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "notInstanceOfPdfChoiceFormFieldTest.pdf";
        String cmpPdf = sourceFolder + "cmp_notInstanceOfPdfChoiceFormFieldTest.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(outPdf));
        PdfPage page = document.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);

        PdfDictionary fieldDictionary = new PdfDictionary();
        fieldDictionary.put(PdfName.FT, PdfName.Ch);
        PdfArray opt = new PdfArray();
        opt.add(new PdfString("one", PdfEncodings.UNICODE_BIG));
        opt.add(new PdfString("two", PdfEncodings.UNICODE_BIG));
        opt.add(new PdfString("three", PdfEncodings.UNICODE_BIG));
        opt.add(new PdfString("four", PdfEncodings.UNICODE_BIG));
        fieldDictionary.put(PdfName.Opt, opt);
        fieldDictionary.put(PdfName.P, page.getPdfObject().getIndirectReference());
        fieldDictionary.put(PdfName.Rect, new PdfArray(new int[] {330, 660, 380, 740}));
        fieldDictionary.put(PdfName.Subtype, PdfName.Widget);
        fieldDictionary.put(PdfName.T, new PdfString("choice", PdfEncodings.UNICODE_BIG));
        fieldDictionary.makeIndirect(document);

        PdfFormField field = PdfFormCreator.createFormField(fieldDictionary);

        field.setValue("two");
        form.addField(field);

        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    public void topIndexTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "choiceFieldNotFittingTest.pdf";
        String outPdf = destinationFolder + "topIndexTest.pdf";
        String cmpPdf = sourceFolder + "cmp_topIndexTest.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(document, false);
        PdfChoiceFormField field = (PdfChoiceFormField) form.getField("choice");
        field.setListSelected(new String[] {"seven"});
        int topIndex = field.getIndices().getAsNumber(0).intValue();
        field.setTopIndex(topIndex);
        document.close();
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder));
    }

    @Test
    // TODO DEVSIX-4480 iText wraps the text into more than one line when generating listbox appearance
    public void longOptionWrappedIntoTwoLinesTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "longOptionWrappedIntoTwoLinesTest.pdf";
        String cmpFileName = sourceFolder + "cmp_longOptionWrappedIntoTwoLinesTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);

        String shortOption = "Short option";
        String longOption = "Long long long long long long long option";

        String[] options = new String[]{shortOption, longOption};
        Rectangle rect = new Rectangle(50, 650, 100, 100);

        PdfChoiceFormField choice = new ChoiceFormFieldBuilder(pdfDocument, "List")
                .setWidgetRectangle(rect).setOptions(options).createList();
        choice.setValue("Short option", true);
        form.addField(choice);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }
}
