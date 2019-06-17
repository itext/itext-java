package com.itextpdf.pdfa;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfALongStringTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfALongStringTest/";
    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis condimentum, tortor sit amet fermentum pharetra, sem felis finibus enim, vel consectetur nunc justo at nisi. In hac habitasse platea dictumst. Donec quis suscipit eros. Nam urna purus, scelerisque in placerat in, convallis vel sapien. Suspendisse sed lacus sit amet orci ornare vulputate. In hac habitasse platea dictumst. Ut eu aliquet felis, at consectetur neque.";
    private static final int STRING_LENGTH_LIMIT = 32767;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    //TODO(DEVSIX-2978): Produces non-conforming PDF/A document
    public void runTest() throws Exception {
        String file = "pdfALongString.pdf";
        String filename = destinationFolder + file;
        try (InputStream icm = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
             PdfADocument pdf = new PdfADocument(new PdfWriter(new FileOutputStream(filename)),
                     PdfAConformanceLevel.PDF_A_3U,
                     new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", icm));
             Document document = new Document(pdf)) {
            StringBuilder stringBuilder = new StringBuilder(LOREM_IPSUM);
            while (stringBuilder.length() < STRING_LENGTH_LIMIT) {
                stringBuilder.append(stringBuilder.toString());
            }
            PdfFontFactory.register(sourceFolder + "FreeSans.ttf",sourceFolder + "FreeSans.ttf");
            PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);
            Paragraph p = new Paragraph(stringBuilder.toString());
            p.setMinWidth(1e6f);
            p.setFont(font);
            document.add(p);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }
}
