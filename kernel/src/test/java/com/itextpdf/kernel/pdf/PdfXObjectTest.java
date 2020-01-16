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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Category(IntegrationTest.class)
public class PdfXObjectTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfXObjectTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfXObjectTest/";

    public static final String[] images = new String[]{sourceFolder + "WP_20140410_001.bmp",
            sourceFolder + "WP_20140410_001.JPC",
            sourceFolder + "WP_20140410_001.jpg",
            sourceFolder + "WP_20140410_001.tif"};


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createDocumentFromImages1() throws IOException,  InterruptedException {
        final String destinationDocument = destinationFolder + "documentFromImages1.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        PdfImageXObject[] images = new PdfImageXObject[4];
        for (int i = 0; i < 4; i++) {
            images[i] = new PdfImageXObject(ImageDataFactory.create(PdfXObjectTest.images[i]));
            images[i].setLayer(new PdfLayer("layer" + i, document));
            if (i % 2 == 0)
                images[i].flush();
        }
        for (int i = 0; i < 4; i++) {
            PdfPage page = document.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.addXObject(images[i], PageSize.Default);
            page.flush();
        }
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addXObject(images[0], 0, 0, 200);
        canvas.addXObject(images[1], 300, 0, 200);
        canvas.addXObject(images[2], 0, 300, 200);
        canvas.addXObject(images[3], 300, 300, 200);
        canvas.release();
        page.flush();
        document.close();

        Assert.assertTrue(new File(destinationDocument).length() < 20 * 1024 * 1024);
        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentFromImages1.pdf", destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void createDocumentFromImages2() throws IOException,  InterruptedException {
        final String destinationDocument = destinationFolder + "documentFromImages2.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        ImageData image = ImageDataFactory.create(sourceFolder + "itext.jpg");
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImage(image, 50, 500, 100, true);
        canvas.addImage(image, 200, 500, 100, false).flush();
        canvas.release();
        page.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentFromImages2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void createDocumentWithForms() throws IOException,  InterruptedException {
        final String destinationDocument = destinationFolder + "documentWithForms1.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        //Create form XObject and flush to document.
        PdfFormXObject form = new PdfFormXObject(new Rectangle(0, 0, 50, 50));
        PdfCanvas canvas = new PdfCanvas(form, document);
        canvas.rectangle(10, 10, 30, 30);
        canvas.fill();
        canvas.release();
        form.flush();

        //Create page1 and add forms to the page.
        PdfPage page1 = document.addNewPage();
        canvas = new PdfCanvas(page1);
        canvas.addXObject(form, 0, 0).addXObject(form, 50, 0).addXObject(form, 0, 50).addXObject(form, 50, 50);
        canvas.release();

        //Create form from the page1 and flush it.
        form = new PdfFormXObject(page1);
        form.flush();

        //Now page1 can be flushed. It's not needed anymore.
        page1.flush();

        //Create page2 and add forms to the page.
        PdfPage page2 = document.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.addXObject(form, 0, 0);
        canvas.addXObject(form, 0, 200);
        canvas.addXObject(form, 200, 0);
        canvas.addXObject(form, 200, 200);
        canvas.release();
        page2.flush();

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_documentWithForms1.pdf", destinationFolder, "diff_"));

    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY))
    public void xObjectIterativeReference() throws IOException {

        // The input file contains circular references chain, see: 8 0 R -> 10 0 R -> 4 0 R -> 8 0 R.
        // Copying of such file even with smart mode is expected to be handled correctly.
        String src = sourceFolder + "checkboxes_XObject_iterative_reference.pdf";
        String dest = destinationFolder + "checkboxes_XObject_iterative_reference_out.pdf";

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest).setSmartMode(true));
        PdfReader pdfReader = new PdfReader(src);
        PdfDocument sourceDocumentPdf = new PdfDocument(pdfReader);
        sourceDocumentPdf.copyPagesTo(1, sourceDocumentPdf.getNumberOfPages(), pdf);

        //map <object pdf, count>
        HashMap<String, Integer> mapIn = new HashMap<>();
        HashMap<String, Integer> mapOut = new HashMap<>();

        //map <object pdf, list of object id referenceing that podf object>
        HashMap<String, List<Integer>> mapOutId = new HashMap<>();

        PdfObject obj;

        //create helpful data structures from pdf output
        for (int i = 1; i < pdf.getNumberOfPdfObjects(); i++) {
            obj = pdf.getPdfObject(i);
            String objString = obj.toString();
            Integer count = mapOut.get(objString);
            List<Integer> list;

            if (count == null) {
                count = 1;
                list = new ArrayList<Integer>();
                list.add(i);
            } else {
                count++;
                list = mapOutId.get(objString);
            }

            mapOut.put(objString, count);
            mapOutId.put(objString, list);
        }

        //create helpful data structures from pdf input
        for (int i = 1; i < sourceDocumentPdf.getNumberOfPdfObjects(); i++) {
            obj = sourceDocumentPdf.getPdfObject(i);
            String objString = obj.toString();
            Integer count = mapIn.get(objString);

            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            mapIn.put(objString, count);
        }

        pdf.close();

        //the following object is copied and reused. it appears 6 times in the original pdf file. just once in the output file
        String case1 = "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 12 /Matrix [1 0 0 1 0 0 ] /Resources <<>> /Subtype /Form /Type /XObject >>";
        Integer countOut1 = mapOut.get(case1);
        Integer countIn1 = mapIn.get(case1);
        Assert.assertTrue(countOut1.equals(1) && countIn1.equals(6));

        //the following object appears 1 time in the original pdf file and just once in the output file
        String case2 = "<</BaseFont /ZapfDingbats /Subtype /Type1 /Type /Font >>";
        Integer countOut2 = mapOut.get(case2);
        Integer countIn2 = mapIn.get(case2);
        Assert.assertTrue(countOut2.equals(countIn2) && countOut2.equals(1));

        //from the original pdf the object "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 70 /Matrix [1 0 0 1 0 0 ] /Resources <</Font <</ZaDb 2 0 R >> >> /Subtype /Form /Type /XObject >>";
        //is going to be found changed in the output pdf referencing the referenced object with another id which is retrieved through the hashmap
        String case3 = "<</BaseFont /ZapfDingbats /Subtype /Type1 /Type /Font >>";
        Integer countIdIn = mapOutId.get(case3).get(0);
        //EXPECTED to be as the original but with different referenced object and marked as modified
        String expected = "<</BBox [0 0 20 20 ] /Filter /FlateDecode /FormType 1 /Length 70 /Matrix [1 0 0 1 0 0 ] /Resources <</Font <</ZaDb " + countIdIn + " 0 R Modified; >> >> /Subtype /Form /Type /XObject >>";
        Assert.assertTrue(mapOut.get(expected).equals(1));
    }
}
