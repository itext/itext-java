/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.util.Arrays;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextRenderInfoUnitTest extends ExtendedITextTest {

    @Test
    public void textExtractGlyphAverageWidth() {
        String text = "i";
        PdfDictionary fontDict = initFontDictWithNotSpecifiedWidthOfSpaceChar();
        CanvasGraphicsState gs = new CanvasGraphicsState();
        gs.setFont(PdfFontFactory.createFont(fontDict));
        gs.setFontSize(12);

        TextRenderInfo testTRI = new TextRenderInfo(new PdfString(text), gs, new Matrix(), new Stack<CanvasTag>());

        float singleSpaceWidth = testTRI.getSingleSpaceWidth();
        float iWidth = testTRI.getUnscaledWidth();

        Assert.assertTrue(iWidth < singleSpaceWidth);
        Assert.assertEquals(6.671999931335449, singleSpaceWidth, 0);
    }

    private static PdfDictionary initFontDictWithNotSpecifiedWidthOfSpaceChar() {
        PdfDictionary fontDict = new PdfDictionary();

        // Initializing all the usual font dictionary entries to make this font more "normal".
        // The entries below should not affect the test.
        fontDict.put(PdfName.BaseFont, new PdfName("GIXYEW+CMMI10"));
        fontDict.put(PdfName.Type, PdfName.Font);
        fontDict.put(PdfName.LastChar, new PdfNumber(122));
        fontDict.put(PdfName.Subtype, PdfName.Type1);
        // /ToUnicode is omitted for the sake of test brevity.
        // Normally it should be in the font however it's not needed in this test and iText seems to process such font normally.
        // fontDict.put(PdfName.ToUnicode, ...);
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(694));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(683));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(-194));
        fontDescriptor.put(PdfName.Flags, new PdfNumber(4));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(Arrays.asList(new PdfObject[]{new PdfNumber(-32), new PdfNumber(-250), new PdfNumber(1048), new PdfNumber(750)})));
        // normally /FontFile should be in the font, however the situation here is the same as for /ToUnicode.
        // fontDescriptor.put(PdfName.FontFile, ...);
        fontDescriptor.put(PdfName.FontName, new PdfName("GIXYEW+CMMI10"));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(-14));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(72));
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.XHeight, new PdfNumber(431));
        fontDict.put(PdfName.FontDescriptor, fontDescriptor);


        // Specifying widths and /FirstChar. /MissingWidth is not specified. Here the result is that font does not specify
        // whitespace character width.
        fontDict.put(PdfName.FirstChar, new PdfNumber(82));
        String widths = "759 613 584 682 583 944 828 580 682 388 388 388 1000 1000 416 528" +
                " 429 432 520 465 489 477 576 344 411 520 298 878 600 484 503 446 451 468" +
                " 361 572 484 715 571 490 465";
        PdfArray widthsArray = new PdfArray();
        for (String w : widths.split(" ")) {
            widthsArray.add(new PdfNumber(Integer.parseInt(w)));
        }
        fontDict.put(PdfName.Widths, widthsArray);
        return fontDict;
    }
}
