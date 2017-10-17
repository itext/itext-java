package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;

import static com.itextpdf.test.ITextTest.createDestinationFolder;

@Category(UnitTest.class)
public class Utf8FormsTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/Utf8FormsTest/";
    public static final String FONT = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/NotoSansCJKsc-Regular.otf";

    @Before
    public void before() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void readUtf8FieldName() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-field-name.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  لا
            Assert.assertEquals("\u0644\u0627", fldName);
        }
        pdfDoc.close();
    }

    @Test
    public void readUtf8TextAnnot() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-text-annot.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  福昕 福昕UTF8
            Assert.assertEquals("\u798F\u6615 \u798F\u6615UTF8", fields.get(fldName).getValueAsString());
        }
    }

    @Test
    public void writeUtf8FieldNameAndValue() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8FieldNameAndValue.pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfTextFormField field = PdfTextFormField.createText(pdfDoc,
                new Rectangle(99, 753, 425, 15), "", "");
        field.setFont(PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H));
        //  لا
        field.put(PdfName.T, new PdfString("\u0644\u0627", PdfEncodings.UTF8));
        //  福昕 福昕UTF8
        field.put(PdfName.V, new PdfString("\u798F\u6615 \u798F\u6615UTF8", PdfEncodings.UTF8));
        field.regenerateField();
        form.addField(field);
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8FieldNameAndValue.pdf", sourceFolder + "cmp_writeUtf8FieldNameAndValue.pdf", destinationFolder, "diffFieldNameAndValue_"));
    }
}
