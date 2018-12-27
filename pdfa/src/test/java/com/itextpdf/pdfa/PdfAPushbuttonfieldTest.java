package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfFormField button = PdfFormField.createPushButton(doc, rect, "push button", "push", font, 12, PdfAConformanceLevel.PDF_A_1B);
        button.setValue("button");
        form.addField(button);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }
}
