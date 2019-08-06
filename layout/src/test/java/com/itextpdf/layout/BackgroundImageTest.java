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

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.BackgroundImage;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

@Category(IntegrationTest.class)
public class BackgroundImageTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/BackgroundImageTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/BackgroundImageTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void backgroundImage() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(xObject);

        Assert.assertTrue(backgroundImage.isRepeatX());
        Assert.assertTrue(backgroundImage.isRepeatY());

        backgroundImageGenericTest("backgroundImage", backgroundImage);
    }

    @Test
    public void backgroundImageWithoutRepeatX() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(xObject, false, true);

        Assert.assertFalse(backgroundImage.isRepeatX());
        Assert.assertTrue(backgroundImage.isRepeatY());

        backgroundImageGenericTest("backgroundImageWithoutRepeatX", backgroundImage);
    }

    @Test
    public void backgroundImageWithoutRepeatY() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(xObject, true, false);

        Assert.assertTrue(backgroundImage.isRepeatX());
        Assert.assertFalse(backgroundImage.isRepeatY());

        backgroundImageGenericTest("backgroundImageWithoutRepeatY", backgroundImage);
    }

    @Test
    public void backgroundImageWithoutRepeatXY() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(xObject, false, false);

        Assert.assertFalse(backgroundImage.isRepeatX());
        Assert.assertFalse(backgroundImage.isRepeatY());

        backgroundImageGenericTest("backgroundImageWithoutRepeatXY", backgroundImage);
    }

    @Test
    //TODO DEVSIX-3108
    public void backgroundXObject() throws IOException, InterruptedException {
        String filename = "backgroundXObject";

        String fileName = filename + ".pdf";
        String outFileName = destinationFolder + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage(createFormXObject(pdfDocument));

            Assert.assertTrue(backgroundImage.isRepeatX());
            Assert.assertTrue(backgroundImage.isRepeatY());

            backgroundXObjectGenericTest(filename,  backgroundImage, pdfDocument);
        }
    }

    @Test
    //TODO DEVSIX-3108
    public void backgroundXObjectWithoutRepeatX() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatX";

        String fileName = filename + ".pdf";
        String outFileName = destinationFolder + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage(createFormXObject(pdfDocument), false, true);

            Assert.assertFalse(backgroundImage.isRepeatX());
            Assert.assertTrue(backgroundImage.isRepeatY());

            backgroundXObjectGenericTest(filename,  backgroundImage, pdfDocument);
        }
    }

    @Test
    //TODO DEVSIX-3108
    public void backgroundXObjectWithoutRepeatY() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatY";

        String fileName = filename + ".pdf";
        String outFileName = destinationFolder + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage(createFormXObject(pdfDocument), true, false);

            Assert.assertTrue(backgroundImage.isRepeatX());
            Assert.assertFalse(backgroundImage.isRepeatY());

            backgroundXObjectGenericTest(filename,  backgroundImage, pdfDocument);
        }
    }

    @Test
    //TODO DEVSIX-3108
    public void backgroundXObjectWithoutRepeatXY() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatXY";

        String fileName = filename + ".pdf";
        String outFileName = destinationFolder + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage(createFormXObject(pdfDocument), false, false);

            Assert.assertFalse(backgroundImage.isRepeatX());
            Assert.assertFalse(backgroundImage.isRepeatY());

            backgroundXObjectGenericTest(filename,  backgroundImage, pdfDocument);
        }
    }

    @Test
    //TODO DEVSIX-3108
    public void backgroundXObjectAndImage() throws IOException, InterruptedException {
        String filename = "backgroundXObjectAndImage";

        String fileName = filename + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + filename + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)))) {

            Document doc = new Document(pdfDocument);

            String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                    "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                    "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                    "officia deserunt mollit anim id est laborum. ";


            Div div = new Div().add(new Paragraph(text + text + text));
            PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
            BackgroundImage backgroundImage = new BackgroundImage(imageXObject);

            div.setProperty(Property.BACKGROUND_IMAGE, backgroundImage);
            doc.add(div);


            BackgroundImage backgroundFormXObject = new BackgroundImage(createFormXObject(pdfDocument));
            div = new Div().add(new Paragraph(text + text + text));
            div.setProperty(Property.BACKGROUND_IMAGE, backgroundFormXObject);
            doc.add(div);

            pdfDocument.close();

            Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

        }
    }

    private PdfFormXObject createFormXObject(PdfDocument pdfDocument) throws MalformedURLException {
        ImageData image = ImageDataFactory.create(sourceFolder + "itis.jpg");
        PdfFormXObject template = new PdfFormXObject(new Rectangle(image.getWidth(), image.getHeight()));
        PdfCanvas canvas = new PdfCanvas(template, pdfDocument);
        canvas.addImage(image, 0, 0, image.getWidth(), false).flush();
        canvas.release();
        template.flush();

        return template;
    }


    private void backgroundImageGenericTest(String filename, BackgroundImage backgroundImage) throws IOException, InterruptedException {
        Assert.assertTrue(backgroundImage.isBackgroundSpecified());

        String outFileName = destinationFolder + filename + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + filename + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDocument);

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. ";


        Div div = new Div().add(new Paragraph(text + text + text));
        div.setProperty(Property.BACKGROUND_IMAGE, backgroundImage);
        doc.add(div);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void backgroundXObjectGenericTest(String filename, BackgroundImage backgroundImage, PdfDocument pdfDocument) throws IOException, InterruptedException {
        Assert.assertTrue(backgroundImage.isBackgroundSpecified());

        String outFileName = destinationFolder + filename + ".pdf";
        String cmpFileName = sourceFolder + "cmp_" + filename + ".pdf";
        Document doc = new Document(pdfDocument);

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. ";


        Div div = new Div().add(new Paragraph(text + text + text));
        div.setProperty(Property.BACKGROUND_IMAGE, backgroundImage);
        doc.add(div);

        pdfDocument.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}

