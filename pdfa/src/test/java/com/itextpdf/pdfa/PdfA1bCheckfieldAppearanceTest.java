package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
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
public class PdfA1bCheckfieldAppearanceTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfA1bCheckfieldAppearanceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA1bCheckfieldAppearanceTest/";

    @BeforeClass
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
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(doc, true);
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100,500,50,50), "name", "Off", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1B);
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
        PdfFormField chk = PdfFormField.createCheckBox(doc, new Rectangle(100,500,50,50), "name", "On", PdfFormField.TYPE_CHECK, PdfAConformanceLevel.PDF_A_1B);
        chk.setBorderColor(ColorConstants.BLACK);
        chk.setBorderWidth(1);
        form.addField(chk);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }
}
