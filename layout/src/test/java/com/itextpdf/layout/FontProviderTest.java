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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.PdfException;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Category(IntegrationTest.class)
public class FontProviderTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        String fileName = "taggedDocumentWithType3Font";
        String srcFileName = sourceFolder + "src_" + fileName + ".pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        PdfFontProvider sel = new PdfFontProvider();
        sel.addStandardPdfFonts();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new FileInputStream(srcFileName)), new PdfWriter(new FileOutputStream(outFileName)));
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontProvider() throws Exception {
        String fileName = "customFontProvider.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider fontProvider = new FontProvider();
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_ROMAN, null, "times"); // TODO DEVSIX-2119 Update if necessary
        fontProvider.getFontSet().addFont(StandardFonts.HELVETICA);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(fontProvider);

        Paragraph paragraph1 = new Paragraph("Default Helvetica should be selected.");
        doc.add(paragraph1);

        Paragraph paragraph2 = new Paragraph("Default Helvetica should be selected.").setFontFamily(StandardFonts.COURIER);
        doc.add(paragraph2);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void customFontProvider2() throws Exception {
        String fileName = "customFontProvider2.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + fileName + ".pdf";

        FontProvider fontProvider = new FontProvider();
        // bold font. shouldn't be selected
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_BOLD, null, "times"); // TODO DEVSIX-2119 Update if necessary
        // monospace font. shouldn't be selected
        fontProvider.getFontSet().addFont(StandardFonts.COURIER);
        fontProvider.getFontSet().addFont(sourceFolder + "../fonts/FreeSans.ttf", PdfEncodings.IDENTITY_H);
        fontProvider.getFontSet().addFont(StandardFonts.TIMES_ROMAN, null, "times"); // TODO DEVSIX-2119 Update if necessary
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);
        doc.setFontProvider(fontProvider);

        Paragraph paragraph = new Paragraph("There is no default font (Helvetica) inside the used FontProvider's instance. So the first font, that has been added, should be selected. Here it's FreeSans.")
                .setFontFamily("ABRACADABRA_THERE_IS_NO_SUCH_FONT");
        doc.add(paragraph);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + fileName));
    }

    @Test
    public void fontProviderNotSetExceptionTest() throws Exception {
        junitExpectedException.expect(IllegalStateException.class);
        junitExpectedException.expectMessage(PdfException.FontProviderNotSetFontFamilyNotResolved);

        String fileName = "fontProviderNotSetExceptionTest.pdf";
        String outFileName = destinationFolder + fileName + ".pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        Paragraph paragraph = new Paragraph("Hello world!")
                .setFontFamily("ABRACADABRA_NO_FONT_PROVIDER_ANYWAY");
        doc.add(paragraph);

        doc.close();
    }

}
