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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class PdfImageXObjectTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfImageXObjectTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/parser/PdfImageXObjectTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    private void testFile(String filename, int page, String objectid) throws Exception {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + filename));
        try {
            PdfResources resources = pdfDocument.getPage(page).getResources();
            PdfDictionary xobjects = resources.getResource(PdfName.XObject);
            PdfObject obj = xobjects.get(new PdfName(objectid));
            if (obj == null) {
                throw new IllegalArgumentException("Reference " + objectid + " not found - Available keys are " + xobjects.keySet());
            }
            PdfImageXObject img = new PdfImageXObject((PdfStream) (obj.isIndirectReference() ? ((PdfIndirectReference) obj).getRefersTo() : obj));
            byte[] result = img.getImageBytes(true);
            Assert.assertNotNull(result);
            int zeroCount = 0;
            for (byte b : result) {
                if (b == 0) zeroCount++;
            }
            Assert.assertTrue(zeroCount > 0);
        } finally {
            pdfDocument.close();
        }
    }

    @Test
    public void testMultiStageFilters() throws Exception{
        testFile("multistagefilter1.pdf", 1, "Obj13");
    }

    @Test
    public void testAscii85Filters() throws Exception{
        testFile("ASCII85_RunLengthDecode.pdf", 1, "Im9");
    }

    @Test
    public void testCcittFilters() throws Exception{
        testFile("ccittfaxdecode.pdf", 1, "background0");
    }

    @Test
    public void testFlateDecodeFilters() throws Exception{
        testFile("flatedecode_runlengthdecode.pdf", 1, "Im9");
    }

    @Test
    public void testDctDecodeFilters() throws Exception{
        testFile("dctdecode.pdf", 1, "im1");
    }

    @Test
    public void testjbig2Filters() throws Exception{
        testFile("jbig2decode.pdf", 1, "2");
    }

    @Test
    public void createDictionaryFromMapIntArrayTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "createDictionaryFromMapIntArrayTest.pdf";
        String cmpfile = sourceFolder + "cmp_createDictionaryFromMapIntArrayTest.pdf";
        String image = sourceFolder + "image.png";


        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        new PdfCanvas(pdfDocument.getFirstPage()).addImage(ImageDataFactory.create(image),50,50,100,false);
        pdfDocument.close();


        assertNull(new CompareTool().compareByContent(filename, cmpfile, destinationFolder, "diff_"));
    }

}
