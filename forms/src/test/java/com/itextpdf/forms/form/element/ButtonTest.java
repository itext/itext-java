package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ButtonTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/ButtonTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/ButtonTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Button formButton = new Button("form button");
            formButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formButton.setProperty(FormProperty.FORM_FIELD_VALUE, "form button");
            formButton.add(new Paragraph("text to display"));
            document.add(formButton);

            Button flattenButton = new Button("flatten button");
            flattenButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenButton.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten button");
            formButton.add(new Paragraph("text to display"));
            document.add(flattenButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
