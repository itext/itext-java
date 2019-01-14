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
package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
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
public class PdfACheckfieldTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfACheckfieldTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfACheckfieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pdfA1aCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1a_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA1aCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1a_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA1bCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1b_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA1bCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA1b_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2aCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2a_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2aCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2a_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2bCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2b_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2bCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2b_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2uCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2u_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2U, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2U);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA2uCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA2u_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2U, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_2U);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3aCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3a_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3aCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3a_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3A);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3bCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3b_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3bCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3b_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3uCheckFieldOffAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3u_checkFieldOffAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3U, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3U);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    @Test
    public void pdfA3uCheckFieldOnAppearanceTest() throws IOException, InterruptedException {
        String name = "pdfA3u_checkFieldOnAppearance";
        String outPath = destinationFolder + name + ".pdf";
        String cmpPath = cmpFolder + "cmp_" + name + ".pdf";
        String diff = "diff_" + name + "_";

        PdfWriter writer = new PdfWriter(outPath);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3U, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100, 500, 50, 50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_3U);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }
}
