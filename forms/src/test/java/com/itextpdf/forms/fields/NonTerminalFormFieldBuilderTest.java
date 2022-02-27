package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Category(UnitTest.class)
public class NonTerminalFormFieldBuilderTest extends ExtendedITextTest {
    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";

    @Test
    public void constructorTest() {
        NonTerminalFormFieldBuilder builder = new NonTerminalFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);

        Assert.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assert.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void createNonTerminalFormField() {
        PdfFormField nonTerminalFormField =
                new NonTerminalFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createNonTerminalFormField();

        compareNonTerminalFormFields(nonTerminalFormField);
    }

    private static void compareNonTerminalFormFields(PdfFormField nonTerminalFormField) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        List<PdfWidgetAnnotation> widgets = nonTerminalFormField.getWidgets();

        Assert.assertEquals(0, widgets.size());

        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        nonTerminalFormField.makeIndirect(DUMMY_DOCUMENT);
        Assert.assertNull(new CompareTool().compareDictionariesStructure(
                expectedDictionary, nonTerminalFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}