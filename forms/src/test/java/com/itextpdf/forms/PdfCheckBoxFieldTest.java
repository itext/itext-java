/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
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
        addCheckBox(pdfDoc, 0, 730, 10, PdfFormField.createCheckBox(pdfDoc, new Rectangle(50, 730, 10, 10), "cb_1", "YES", PdfFormField.TYPE_CIRCLE));
        addCheckBox(pdfDoc, 0, 700, 10, PdfFormField.createCheckBox(pdfDoc, new Rectangle(50, 700, 10, 10), "cb_2", "YES", PdfFormField.TYPE_CROSS));
        addCheckBox(pdfDoc, 0, 670, 10, PdfFormField.createCheckBox(pdfDoc, new Rectangle(50, 670, 10, 10), "cb_3", "YES", PdfFormField.TYPE_DIAMOND));
        addCheckBox(pdfDoc, 0, 640, 10, PdfFormField.createCheckBox(pdfDoc, new Rectangle(50, 640, 10, 10), "cb_4", "YES", PdfFormField.TYPE_SQUARE));
        addCheckBox(pdfDoc, 0, 610, 10, PdfFormField.createCheckBox(pdfDoc, new Rectangle(50, 610, 10, 10), "cb_5", "YES", PdfFormField.TYPE_STAR));

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

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, float checkBoxH) throws IOException {
        Rectangle rect = new Rectangle(50, yPos, checkBoxW, checkBoxH);
        addCheckBox(pdfDoc, fontSize, yPos, checkBoxW, PdfFormField.createCheckBox(pdfDoc, rect, MessageFormatUtil.format("cb_fs_{0}_{1}_{2}", fontSize, checkBoxW, checkBoxH), "YES", PdfFormField.TYPE_CHECK));
    }

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, PdfFormField checkBox) throws IOException {
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
