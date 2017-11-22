package com.itextpdf.layout;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Category(IntegrationTest.class)
public class FontProviderTest extends ExtendedITextTest {

    private static class PdfFontProvider extends FontProvider {

        private List<FontInfo> pdfFontInfos = new ArrayList<>();

        public void addPdfFont(PdfFont font, String alias) {
            FontInfo fontInfo = FontInfo.create(font.getFontProgram(), null, alias);
            // stored FontInfo will be used in FontSelector collection.
            pdfFontInfos.add(fontInfo);
            // first of all FOntProvider search PdfFont in pdfFonts.
            pdfFonts.put(fontInfo, font);
        }

        @Override
        protected FontSelector createFontSelector(Collection<FontInfo> fonts, List<String> fontFamilies, FontCharacteristics fc) {
            List<FontInfo> newFonts = new ArrayList<>(fonts);
            newFonts.addAll(pdfFontInfos);
            return super.createFontSelector(newFonts, fontFamilies, fc);
        }
    }

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FontProviderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FontProviderTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void standardAndType3Fonts() throws Exception {
        String srcFileName = sourceFolder + "src_taggedDocumentWithType3Font.pdf";
        String outFileName = destinationFolder + "taggedDocumentWithType3Font.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedDocumentWithType3Font.pdf";

        PdfFontProvider sel = new PdfFontProvider();
        sel.addStandardPdfFonts();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(srcFileName)), new PdfWriter(new FileOutputStream(outFileName)));
        PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont((PdfDictionary) pdfDoc.getPdfObject(5));
        sel.addPdfFont(pdfType3Font, "CustomFont");

        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Paragraph paragraph = new Paragraph("Next paragraph contains a triangle, actually Type 3 Font");
        paragraph.setProperty(Property.FONT, StandardFonts.TIMES_ROMAN);
        doc.add(paragraph);


        paragraph = new Paragraph("A");
        paragraph.setFont("CustomFont");
        doc.add(paragraph);
        paragraph = new Paragraph("Next paragraph");
        paragraph.setProperty(Property.FONT, StandardFonts.COURIER);
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
