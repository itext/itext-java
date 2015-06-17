package com.itextpdf.core.pdf;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.core.pdf.formfield.PdfAcroForm;
import com.itextpdf.core.pdf.formfield.PdfFormField;
import com.itextpdf.core.pdf.formfield.PdfTextFormField;
import com.itextpdf.core.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfFormFieldTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfFormFieldTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfFormFieldTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void formFieldTest01() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "formFieldFile.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfAcroForm form = pdfDoc.getAcroForm();

        ArrayList<PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get(1);

        Assert.assertTrue(fields.size() == 4);
        Assert.assertTrue(field.getFieldName().toUnicodeString().equals("Text1"));
        Assert.assertTrue(field.getValue().toString().equals("TestField"));
    }

    @Test
    public void formFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = pdfDoc.createAcroForm();
        form.put(PdfName.NeedAppearances, new PdfBoolean(true));

        PdfPage page = pdfDoc.addNewPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(pdfDoc, rect);
        page.addAnnotation(annot);
        PdfTextFormField field = new PdfTextFormField(pdfDoc, annot);

        field.setFieldName("TestField");
        field.setValue(new PdfString("some value"));
        pdfDoc.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest03() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "formFieldFile.pdf");
        String filename = destinationFolder + "formFieldTest03.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = pdfDoc.getAcroForm();
        form.put(PdfName.NeedAppearances, new PdfBoolean(true));

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);
        PdfWidgetAnnotation annot = new PdfWidgetAnnotation(pdfDoc, rect);
        page.addAnnotation(annot);
        PdfTextFormField field = new PdfTextFormField(pdfDoc, annot);

        field.setFieldName("TestField");
        field.setValue(new PdfString("some value"));
        pdfDoc.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
