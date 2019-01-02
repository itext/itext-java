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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.FileUtil;
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

}
