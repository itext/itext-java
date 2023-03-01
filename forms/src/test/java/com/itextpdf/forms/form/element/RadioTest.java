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
public class RadioTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/RadioTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/RadioTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicRadioTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicRadio.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicRadio.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio formRadio1 = new Radio("form radio button 1");
            formRadio1.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            // TODO DEVSIX-7360 Form field value is used as group name which is a little bit counterintuitive, maybe we
            //  we can come up with something more obvious.
            formRadio1.setProperty(FormProperty.FORM_FIELD_VALUE, "form radio group");
            formRadio1.setProperty(FormProperty.FORM_FIELD_CHECKED, false);
            document.add(formRadio1);

            Radio formRadio2 = new Radio("form radio button 2");
            formRadio2.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formRadio2.setProperty(FormProperty.FORM_FIELD_VALUE, "form radio group");
            // TODO DEVSIX-7360 True doesn't work and considered as checked radio button, it shouldn't be that way.
            formRadio2.setProperty(FormProperty.FORM_FIELD_CHECKED, null);
            document.add(formRadio2);

            Radio flattenRadio1 = new Radio("flatten radio button 1");
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten radio group");
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_CHECKED, false);
            document.add(flattenRadio1);

            Radio flattenRadio2 = new Radio("flatten radio button 2");
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten radio group");
            // TODO DEVSIX-7360 True doesn't work and considered as checked radio button, it shouldn't be that way.
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_CHECKED, null);
            document.add(flattenRadio2);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
