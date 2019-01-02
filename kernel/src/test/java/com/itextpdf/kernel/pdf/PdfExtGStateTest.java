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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfExtGStateTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfExtGStateTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/PdfExtGStateTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void egsTest1() throws Exception {
        String destinationDocument = destinationFolder + "egsTest1.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationDocument));

        //Create page and canvas
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        //Create ExtGState and fill it with line width and font
        PdfExtGState egs = new PdfExtGState();
        egs.getPdfObject().put(PdfName.LW, new PdfNumber(5));
        PdfArray font = new PdfArray();
        PdfFont pdfFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        document.addFont(pdfFont);
        font.add(pdfFont.getPdfObject());
        font.add(new PdfNumber(24));
        egs.getPdfObject().put(PdfName.Font, font);

        //Write ExtGState
        canvas.setExtGState(egs);

        //Write text to check that font from ExtGState is applied
        canvas.beginText();
        canvas.moveText(50, 600);
        canvas.showText("Courier, 24pt");
        canvas.endText();

        //Draw line to check if ine width is applied
        canvas.moveTo(50, 500);
        canvas.lineTo(300, 500);
        canvas.stroke();

        //Write text again to check that font from page resources and font from ExtGState is the same.
        canvas.beginText();
        canvas.setFontAndSize(pdfFont, 36);
        canvas.moveText(50, 400);
        canvas.showText("Courier, 36pt");
        canvas.endText();
        canvas.release();

        page.flush();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_egsTest1.pdf", destinationFolder, "diff_"));
    }

}
