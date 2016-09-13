package com.itextpdf.pdfa;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.itextpdf.layout.element.Text;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfABackgroundColorTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2GraphicsCheckTest/";
    public static final String destinationFolder = "./target/test/PdfA2GraphicsCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldRenderPdfAWhenBackgroundColorIsSet() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_colorCheckTest2.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_colorCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A, new PdfOutputIntent("Custom", "", "http://www.color.org",
                "sRGB IEC61966-2.1", this.getClass().getClassLoader().getResourceAsStream("com/itextpdf/pdfa/sRGB Color Space Profile.icm")));

        PdfDictionary markInfo = new PdfDictionary();
        markInfo.put(PdfName.Marked, new PdfBoolean(true));
        doc.getCatalog().put(PdfName.MarkInfo, markInfo);

        doc.setTagged();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);

        com.itextpdf.layout.Document document = new Document(doc);
        Text foo = new Text("foo");
        foo.setFont(font);
        foo.setBackgroundColor(Color.BLUE);
        document.add(new Paragraph(foo));

        doc.close();
    }
}
