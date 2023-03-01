package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class InputButtonTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/InputButtonTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/InputButtonTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicInputButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicInputButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicInputButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputButton formInputButton = new InputButton("form input button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formInputButton.setProperty(FormProperty.FORM_FIELD_VALUE, "form input button");
            document.add(formInputButton);

            InputButton flattenInputButton = new InputButton("flatten input button");
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input button");
            document.add(flattenInputButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
