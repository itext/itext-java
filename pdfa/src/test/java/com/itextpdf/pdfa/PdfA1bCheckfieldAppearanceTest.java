/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfA1bCheckfieldAppearanceTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfA1bCheckfieldAppearanceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA1bCheckfieldAppearanceTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pdfA1bCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1b_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        PdfFormField chk = new CheckBoxFormFieldBuilder(doc, "name").setWidgetRectangle(new Rectangle(100, 500, 50, 50))
                .setCheckType(CheckBoxType.CHECK).setConformance(PdfConformance.PDF_A_1B)
                .createCheckBox().setValue("Off");
        chk.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        chk.getFirstFormAnnotation().setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA1bCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1b_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformance.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(doc, true);
        PdfFormField chk = new CheckBoxFormFieldBuilder(doc, "name").setWidgetRectangle(new Rectangle(100, 500, 50, 50))
                .setCheckType(CheckBoxType.CHECK).setConformance(PdfConformance.PDF_A_1B)
                .createCheckBox().setValue("On");
        chk.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);
        chk.getFirstFormAnnotation().setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }
}
