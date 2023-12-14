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
package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
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
        PdfFormField button = PdfFormField.createPushButton(doc, rect, "push button", "push", font, 12, PdfAConformanceLevel.PDF_A_1B);
        form.addField(button);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
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
        PdfFormField button = PdfFormField.createPushButton(doc, rect, "push button", "push", font, 12, PdfAConformanceLevel.PDF_A_1B);
        button.regenerateField();
        form.addField(button);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
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
        PdfFormField button = PdfFormField.createPushButton(doc, rect, "push button", "push", font, 12, PdfAConformanceLevel.PDF_A_1B);
        button.setValue("button");
        form.addField(button);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }
}
