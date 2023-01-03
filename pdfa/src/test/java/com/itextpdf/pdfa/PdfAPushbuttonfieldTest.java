/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfAPushbuttonfieldTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfAPushbuttonfieldTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAPushbuttonfieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    // TODO: DEVSIX-3913 update this test after the ticket will be resolved
    public void pdfA1bButtonAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1b_ButtonAppearanceTest";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

        Rectangle rect = new Rectangle(36, 626, 100, 40);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfFormField button = new PushButtonFormFieldBuilder(doc, "push button").setWidgetRectangle(rect)
                .setCaption("push").setConformanceLevel(PdfAConformanceLevel.PDF_A_1B)
                .createPushButton().setFont(font).setFontSize(12);
        form.addField(button);

        Exception exception = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());

        Assert.assertEquals(MessageFormatUtil.format(
                PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "Helvetica"),
                exception.getMessage());
    }

    @Test
    // TODO: DEVSIX-3913 update this test after the ticket will be resolved
    public void pdfA1bButtonAppearanceRegenerateTest() throws IOException, InterruptedException {
        String name = "pdfA1b_ButtonAppearanceRegenerateTest";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

        Rectangle rect = new Rectangle(36, 626, 100, 40);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfFormField button = new PushButtonFormFieldBuilder(doc, "push button").setWidgetRectangle(rect)
                .setCaption("push").setConformanceLevel(PdfAConformanceLevel.PDF_A_1B)
                .createPushButton().setFont(font).setFontSize(12);
        button.regenerateField();
        form.addField(button);

        Exception exception = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());

        Assert.assertEquals(MessageFormatUtil.format(
                PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "Helvetica"),
                exception.getMessage());
    }

    @Test
    // TODO: DEVSIX-3913 update this test after the ticket will be resolved
    public void pdfA1bButtonAppearanceSetValueTest() throws IOException, InterruptedException {
        String name = "pdfA1b_ButtonAppearanceSetValueTest";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);

        Rectangle rect = new Rectangle(36, 626, 100, 40);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfFormField button = new PushButtonFormFieldBuilder(doc, "push button").setWidgetRectangle(rect)
                .setCaption("push").setConformanceLevel(PdfAConformanceLevel.PDF_A_1B)
                .createPushButton().setFont(font).setFontSize(12);
        button.setValue("button");
        form.addField(button);

        Exception exception = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());

        Assert.assertEquals(MessageFormatUtil.format(
                PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "Helvetica"),
                exception.getMessage());
    }
}
