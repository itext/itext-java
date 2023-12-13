/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfCheckBoxFieldTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfCheckBoxFieldTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfCheckBoxFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }


    @Test
    public void checkBoxFontSizeTest01() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest01.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 6, 750, 7, 7);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest02.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 7, 7);
        // fallback to default fontsize — 12 is expected.
        addCheckBox(pdfDoc, -1, 710, 7, 7);

        addCheckBox(pdfDoc, 0, 640, 20, 20);
        addCheckBox(pdfDoc, 0, 600, 40, 20);
        addCheckBox(pdfDoc, 0, 550, 20, 40);

        addCheckBox(pdfDoc, 0, 520, 5, 5);
        addCheckBox(pdfDoc, 0, 510, 5, 3);
        addCheckBox(pdfDoc, 0, 500, 3, 5);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest03() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest03.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 2, 730, 7, 7);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest04() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest04.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 10, PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(50, 730, 10, 10), "cb_1", "YES", PdfFormField.TYPE_CIRCLE));
        addCheckBox(pdfDoc, 0, 700, 10, PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(50, 700, 10, 10), "cb_2", "YES", PdfFormField.TYPE_CROSS));
        addCheckBox(pdfDoc, 0, 670, 10, PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(50, 670, 10, 10), "cb_3", "YES", PdfFormField.TYPE_DIAMOND));
        addCheckBox(pdfDoc, 0, 640, 10, PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(50, 640, 10, 10), "cb_4", "YES", PdfFormField.TYPE_SQUARE));
        addCheckBox(pdfDoc, 0, 610, 10, PdfFormField
                .createCheckBox(pdfDoc, new Rectangle(50, 610, 10, 10), "cb_5", "YES", PdfFormField.TYPE_STAR));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest05() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest05.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest05.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 40, 40);
        addCheckBox(pdfDoc, 0, 600, 100, 100);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxToggleTest01() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "checkBoxToggledOn.pdf";
        String outPdf = destinationFolder + "checkBoxToggleTest01.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxToggleTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfFormField checkBox = form.getField("cb_fs_6_7_7");
        checkBox.setValue("Off");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxToggleTest02() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "checkBoxToggledOn.pdf";
        String outPdf = destinationFolder + "checkBoxToggleTest02.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxToggleTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfFormField checkBox = form.getField("cb_fs_6_7_7");
        checkBox.setValue("Off", false);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, float checkBoxH)
            throws IOException {
        Rectangle rect = new Rectangle(50, yPos, checkBoxW, checkBoxH);
        addCheckBox(pdfDoc, fontSize, yPos, checkBoxW, PdfFormField.createCheckBox(pdfDoc, rect,
                MessageFormatUtil.format("cb_fs_{0}_{1}_{2}", fontSize, checkBoxW, checkBoxH), "YES",
                PdfFormField.TYPE_CHECK));
    }

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, PdfFormField checkBox)
            throws IOException {
        PdfPage page = pdfDoc.getFirstPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        if (fontSize >= 0) {
            checkBox.setFontSize(fontSize);
        }
        checkBox.setBorderWidth(1);
        checkBox.setBorderColor(ColorConstants.BLACK);

        form.addField(checkBox, page);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(50 + checkBoxW + 10, yPos)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("okay?")
                .endText()
                .restoreState();
    }
}
