package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static com.itextpdf.test.ITextTest.createOrClearDestinationFolder;


@Tag("IntegrationTest")
public class RectangleIntegrationTest {

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/forms/RectangleTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/RectangleTest/";

    @BeforeAll
    public static void initDestinationFolder() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void createPdfWithSignatureFields() throws IOException, InterruptedException {

        String outPdf = DESTINATION_FOLDER + "RectangleTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_RectangleTest.pdf";

        PdfWriter writer = new PdfWriter(DESTINATION_FOLDER + "RectangleTest.pdf");
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        for (int i = 0; i <= 3; i++) {
            int rotation = 90 * i;
            PdfPage page = pdfDoc.addNewPage();
            page.setRotation(rotation);

            float x = 20, y = 500, width = 100, height = 50, spacing = 50;

            for (int j = 1; j <= 3; j++) {
                Rectangle rect = new Rectangle(x, y, width, height);
                String fieldName = "page" + i + "_Signature" + j;

                PdfFormField signatureField = new SignatureFormFieldBuilder(pdfDoc, fieldName)
                        .setPage(page)
                        .setWidgetRectangle(rect)
                        .createSignature();

                form.addField(signatureField);
                x += width + spacing;
            }
        }

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
    }
}
