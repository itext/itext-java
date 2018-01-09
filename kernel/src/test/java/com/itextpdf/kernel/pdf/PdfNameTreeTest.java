/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotationAppearance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Map;

@Category(IntegrationTest.class)
public class PdfNameTreeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfNameTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfNameTreeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void EmbeddedFileAndJavascriptTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "FileWithSingleAttachment.pdf"));
        PdfNameTree embeddedFilesNameTree = pdfDocument.getCatalog().getNameTree(PdfName.EmbeddedFiles);
        Map<String, PdfObject> objs = embeddedFilesNameTree.getNames();
        PdfNameTree javascript = pdfDocument.getCatalog().getNameTree(PdfName.JavaScript);
        Map<String, PdfObject> objs2 = javascript.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
        Assert.assertEquals(1, objs2.size());
    }

    @Test
    public void AnnotationAppearanceTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "AnnotationAppearanceTest.pdf"));
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.MAGENTA).beginText().setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 30)
                .setTextMatrix(25, 500).showText("This file has AP key in Names dictionary").endText();
        PdfArray array = new PdfArray();
        array.add(new PdfString("normalAppearance"));
        array.add(new PdfAnnotationAppearance().setState(PdfName.N, new PdfFormXObject(new Rectangle(50, 50 , 50, 50))).getPdfObject());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Names, array);
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.AP, dict);
        pdfDocument.getCatalog().getPdfObject().put(PdfName.Names, dictionary);

        PdfNameTree appearance = pdfDocument.getCatalog().getNameTree(PdfName.AP);
        Map<String, PdfObject> objs = appearance.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
    }
}
