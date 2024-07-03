/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFontFamilies;
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
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Tag("IntegrationTest")
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

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void standardAndType3Fonts() throws Exception {
        String fileName = "taggedDocumentWithType3Font";
        String srcFileName = sourceFolder + "src_" + fileName + ".pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        PdfFontProvider sel = new PdfFontProvider();
        sel.addStandardPdfFonts();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                new PdfWriter(FileUtil.getFileOutputStream(outFileName)));
        PdfType3Font pdfType3Font = (PdfType3Font) PdfFontFactory.createFont((PdfDictionary) pdfDoc.getPdfObject(5));
        sel.addPdfFont(pdfType3Font, "CustomFont");

        Document doc = new Document(pdfDoc);
        doc.setFontProvider(sel);

        Paragraph paragraph = new Paragraph("Next paragraph contains a triangle, actually Type 3 Font");
        paragraph.setProperty(Property.FONT, new String[] {StandardFontFamilies.TIMES});
        doc.add(paragraph);


        paragraph = new Paragraph("A");
        paragraph.setFontFamily("CustomFont");
        doc.add(paragraph);
        paragraph = new Paragraph("Next paragraph");
        paragraph.setProperty(Property.FONT, new String[] {StandardFonts.COURIER});
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontProvider() throws Exception {
        String fileName = "customFontProvider.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider fontProvider = new FontProvider();

        // TODO DEVSIX-2119 Update if necessary
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_ROMAN, null, "times");
        fontProvider.getFontSet().addFont(StandardFonts.HELVETICA);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(fontProvider);

        Paragraph paragraph1 = new Paragraph("Default Helvetica should be selected.");
        doc.add(paragraph1);

        Paragraph paragraph2 = new Paragraph("Default Helvetica should be selected.").setFontFamily(StandardFonts.COURIER);
        doc.add(paragraph2);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontProvider2() throws Exception {
        String fileName = "customFontProvider2.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider fontProvider = new FontProvider();

        // bold font. shouldn't be selected
        // TODO DEVSIX-2119 Update if necessary
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_BOLD, null, "times");
        // monospace font. shouldn't be selected
        fontProvider.getFontSet().addFont(StandardFonts.COURIER);
        fontProvider.getFontSet().addFont(sourceFolder + "../fonts/FreeSans.ttf", PdfEncodings.IDENTITY_H);

        // TODO DEVSIX-2119 Update if necessary
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_ROMAN, null, "times");
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(fontProvider);

        Paragraph paragraph = new Paragraph("There is no default font (Helvetica) inside the used FontProvider's instance. So the first font, that has been added, should be selected. Here it's FreeSans.")
                .setFontFamily("ABRACADABRA_THERE_IS_NO_SUCH_FONT");
        doc.add(paragraph);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void fontProviderNotSetExceptionTest() throws Exception {
        String fileName = "fontProviderNotSetExceptionTest.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDoc);

            Paragraph paragraph = new Paragraph("Hello world!")
                    .setFontFamily("ABRACADABRA_NO_FONT_PROVIDER_ANYWAY");

            Exception e = Assertions.assertThrows(IllegalStateException.class, () -> doc.add(paragraph));
            Assertions.assertEquals(LayoutExceptionMessageConstant.FONT_PROVIDER_NOT_SET_FONT_FAMILY_NOT_RESOLVED, e.getMessage());
        }
    }
}
