/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FontToUnicodeTest extends ExtendedITextTest {
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/kernel/pdf/fonts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/FontToUnicodeTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    // TODO DEVSIX-3634. In the output now we don't expect the \u2F46 unicode range.
    // TODO DEVSIX-3634. SUBSTITUTE "Assert.assertEquals("\u2F46"..." to "Assert.assertEquals("\u65E0"..." after the fix
    public void severalUnicodesWithinOneGlyphTest() throws IOException {
        String outFileName = destinationFolder + "severalUnicodesWithinOneGlyphTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        PdfFont font = PdfFontFactory.createFont(fontsFolder + "NotoSansCJKjp-Bold.otf",
                PdfEncodings.IDENTITY_H);

        List<Glyph> glyphs = Collections.singletonList(font.getGlyph((int) '\u65E0'));
        GlyphLine glyphLine = new GlyphLine(glyphs);

        PdfCanvas canvas2 = new PdfCanvas(pdfDocument.addNewPage());
        canvas2
                .saveState()
                .beginText()
                .moveText(36, 800)
                .setFontAndSize(font, 12)
                .showText(glyphLine)
                .endText()
                .restoreState();

        pdfDocument.close();

        PdfDocument resultantPdfAsFile = new PdfDocument(new PdfReader(outFileName));
        String actualUnicode = PdfTextExtractor.getTextFromPage(resultantPdfAsFile.getFirstPage());

        Assert.assertEquals("\u2F46", actualUnicode);
    }
}
