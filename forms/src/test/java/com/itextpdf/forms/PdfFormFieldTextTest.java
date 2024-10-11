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

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFormFieldTextTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldTextTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldTextTest/";
    private static final String TEXT = "Some text in Russian \u0442\u0435\u043A\u0441\u0442 (text)";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillFormWithAutosizeTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "fillFormWithAutosizeTest.pdf";
        String inPdf = sourceFolder + "fillFormWithAutosizeSource.pdf";
        String cmpPdf = sourceFolder + "cmp_fillFormWithAutosizeTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.get("First field").setValue("name name name ");
        fields.get("Second field").setValue("surname surname surname surname surname surname");
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void defaultAppearanceExtractionForNotMergedFieldsTest() throws IOException, InterruptedException {
        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "sourceDAExtractionTest.pdf"),
                new PdfWriter(destinationFolder + "defaultAppearanceExtractionTest.pdf"));
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, false);
        form.getField("First field").setValue("Your name");
        form.getField("Text1").setValue("Your surname");
        doc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destinationFolder + "defaultAppearanceExtractionTest.pdf",
                sourceFolder + "cmp_defaultAppearanceExtractionTest.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void fontsResourcesHelvFontTest() throws IOException {
        String filename = "fontsResourcesHelvFontTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "drWithHelv.pdf"),
                new PdfWriter(destinationFolder + filename));
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSans-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        font.setSubset(false);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        form.getField("description").setValue(TEXT, font, 12f);

        pdfDoc.close();

        PdfDocument document = new PdfDocument(new PdfReader(destinationFolder + filename));

        PdfDictionary actualDocumentFonts = PdfFormCreator.getAcroForm(document, false).getPdfObject()
                .getAsDictionary(PdfName.DR).getAsDictionary(PdfName.Font);

        // Note that we know the structure of the expected pdf file
        PdfString expectedFieldsDAFont = new PdfString("/F2 12 Tf");
        PdfObject actualFieldDAFont = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm)
                .getAsArray(PdfName.Fields).getAsDictionary(0).get(PdfName.DA);

        Assertions.assertEquals(new PdfName("Helvetica"),
                actualDocumentFonts.getAsDictionary(new PdfName("F1")).get(PdfName.BaseFont),
                "There is no Helvetica font within DR key");
        Assertions.assertEquals(new PdfName("NotoSans"),
                actualDocumentFonts.getAsDictionary(new PdfName("F2")).get(PdfName.BaseFont),
                "There is no NotoSans font within DR key.");
        Assertions.assertEquals(expectedFieldsDAFont, actualFieldDAFont, "There is no NotoSans(/F2) font within Fields DA key");

        document.close();

        ExtendedITextTest.printOutputPdfNameAndDir(destinationFolder + filename);
    }

    @Test
    public void fontsResourcesHelvCourierNotoFontTest() throws IOException {
        String filename = "fontsResourcesHelvCourierNotoFontTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "drWithHelvAndCourier.pdf"),
                new PdfWriter(destinationFolder + filename));
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSans-Regular.ttf",
                PdfEncodings.IDENTITY_H);
        font.setSubset(false);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, false);
        form.getField("description").setFont(font);
        form.getField("description").setValue(TEXT);

        pdfDoc.close();

        PdfDocument document = new PdfDocument(new PdfReader(destinationFolder + filename));

        // Note that we know the structure of the expected pdf file
        PdfString expectedAcroformDAFont = new PdfString("/F1 0 Tf 0 g ");
        PdfString expectedFieldsDAFont = new PdfString("/F3 12 Tf");

        PdfObject actualAcroFormDAFont = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm).get(PdfName.DA);
        PdfDictionary actualDocumentFonts = PdfFormCreator.getAcroForm(document, false).getPdfObject()
                .getAsDictionary(PdfName.DR).getAsDictionary(PdfName.Font);
        PdfObject actualFieldDAFont = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm)
                .getAsArray(PdfName.Fields).getAsDictionary(0).get(PdfName.DA);

        Assertions.assertEquals(new PdfName("Helvetica"),
                actualDocumentFonts.getAsDictionary(new PdfName("F1")).get(PdfName.BaseFont),
                "There is no Helvetica font within DR key");
        Assertions.assertEquals(new PdfName("Courier"),
                actualDocumentFonts.getAsDictionary(new PdfName("F2")).get(PdfName.BaseFont),
                "There is no Courier font within DR key.");
        Assertions.assertEquals(new PdfName("NotoSans"),
                actualDocumentFonts.getAsDictionary(new PdfName("F3")).get(PdfName.BaseFont),
                "There is no NotoSans font within DR key.");
        Assertions.assertEquals(expectedAcroformDAFont, actualAcroFormDAFont, "There is no Helvetica(/F1) font within AcroForm DA key");
        Assertions.assertEquals(expectedFieldsDAFont, actualFieldDAFont, "There is no NotoSans(/F3) font within Fields DA key");

        document.close();

        ExtendedITextTest.printOutputPdfNameAndDir(destinationFolder + filename);
    }

    @Test
    public void lineEndingsTest() throws IOException, InterruptedException {
        String destFilename = destinationFolder + "lineEndingsTest.pdf";
        String cmpFilename = sourceFolder + "cmp_lineEndingsTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destFilename))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

            PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "single")
                    .setWidgetRectangle(new Rectangle(50, 700, 500, 120)).createText();
            field.setValue("Line 1\nLine 2\rLine 3\r\nLine 4");
            form.addField(field);

            PdfTextFormField field2 = new TextFormFieldBuilder(pdfDoc, "multi")
                    .setWidgetRectangle(new Rectangle(50, 500, 500, 120)).createMultilineText();
            field2.setValue("Line 1\nLine 2\rLine 3\r\nLine 4");
            form.addField(field2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destFilename, cmpFilename, destinationFolder, "diff_"));
    }
}
