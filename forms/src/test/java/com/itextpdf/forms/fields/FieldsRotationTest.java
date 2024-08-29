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
package com.itextpdf.forms.fields;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@Tag("IntegrationTest")
public class FieldsRotationTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/fields/FieldsRotationTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/fields/FieldsRotationTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> rotationRelatedProperties() {
        return Arrays.asList(new Object[][]{
                {new int[]{360, 90, 180, 270}, new int[]{0, 0, 0, 0}, true, "fieldsOnRotatedPagesDefault"},
                {new int[]{360, 90, 180, 270}, new int[]{0, 0, 0, 0}, false, "fieldsOnRotatedPages"},
                {new int[]{0, 0, 0, 0}, new int[]{360, 90, 180, 270}, true, "rotatedFieldsDefault"},
                {new int[]{90, 90, 90, 90}, new int[]{720, 90, 180, 270}, true, "rotatedFieldsPage90Default"},
                {new int[]{90, 90, 90, 90}, new int[]{0, -270, 180, -90}, false, "rotatedFieldsPage90"},
                {new int[]{0, 90, 180, 270}, new int[]{0, 90, 180, 270}, true, "rotatedFieldsOnRotatedPagesDefault"},
                {new int[]{0, 90, 180, 270}, new int[]{0, 90, 180, 270}, false, "rotatedFieldsOnRotatedPages"}
        });
    }

    @ParameterizedTest(name = "{3}; ignore page rotation: {2}")
    @MethodSource("rotationRelatedProperties")
    public void fieldRotationTest(int[] pageRotation, int[] fieldRotation, boolean ignorePageRotation, String testName)
            throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + testName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";

        fillForm(pageRotation, fieldRotation, ignorePageRotation, outFileName);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private void fillForm(int[] pageRotation, int[] fieldRotation, boolean ignorePageRotation, String outPdf)
            throws IOException {
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);

            for (int i = 1; i < 5; ++i) {
                String caption = generateCaption(pageRotation[i - 1], fieldRotation[i - 1]);

                document.getPdfDocument().addNewPage().setRotation(pageRotation[i - 1]);

                String buttonName = "button" + i;
                PdfButtonFormField button = new PushButtonFormFieldBuilder(document.getPdfDocument(), buttonName)
                        .setWidgetRectangle(new Rectangle(50, 570, 400, 200)).setPage(i).createPushButton();
                Button buttonField = new Button(buttonName);
                buttonField.setValue("button" + caption);
                button.getFirstFormAnnotation().setFormFieldElement(buttonField).setBorderColor(ColorConstants.GREEN)
                        .setRotation(fieldRotation[i - 1]);
                form.addField(button);

                String textName = "text" + i;
                PdfTextFormField text = new TextFormFieldBuilder(document.getPdfDocument(), textName)
                        .setWidgetRectangle(new Rectangle(50, 320, 400, 200)).setPage(i).createText();
                text.getFirstFormAnnotation().setBorderColor(ColorConstants.GREEN).setRotation(fieldRotation[i - 1]);
                form.addField(text);

                String signatureName = "signature" + i;
                PdfSignatureFormField signature = new SignatureFormFieldBuilder(document.getPdfDocument(), signatureName)
                        .setWidgetRectangle(new Rectangle(50, 70, 400, 200)).setPage(i).createSignature();
                SignatureFieldAppearance sigField = new SignatureFieldAppearance(signatureName)
                        .setContent("signature" + caption);
                signature.setIgnorePageRotation(ignorePageRotation).getFirstFormAnnotation().setFormFieldElement(sigField).setBorderColor(ColorConstants.GREEN)
                        .setRotation(fieldRotation[i - 1]);
                form.addField(signature);
            }
        }
    }

    private String generateCaption(int pageRotation, int fieldRotation) {
        String caption = ", page rotation: " + pageRotation + ", field rotation: " + fieldRotation;
        for (int i = 0; i < 3; ++i) {
            caption += caption;
        }
        return caption;
    }
}
