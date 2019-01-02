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
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class XMPWriterTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/XMPWriterTest/";
    public static final String DOG = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/dog.bmp";
    public static final String FONT = "./src/test/resources/com/itextpdf/layout/fonts/FreeSans.ttf";
    public static final String FOX = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/fox.bmp";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createPdfTest() throws IOException, XMPException {
        String fileName = "xmp_metadata.pdf";
        // step 1
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "xmp_metadata.pdf"));
        Document document = new Document(pdfDocument);
        // step 2

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        XMPMeta xmp = XMPMetaFactory.create();
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "Hello World", null);
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "XMP & Metadata", null);
        xmp.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY), "Metadata", null);
        pdfDocument.setXmpMetadata(xmp);

        // step 4
        document.add(new Paragraph("Hello World"));
        // step 5
        document.close();

        CompareTool ct = new CompareTool();
        Assert.assertNull(ct.compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void addUAXMPMetaDataNotTaggedTest() throws IOException {
        String fileName = "addUAXMPMetaDataNotTaggedTest.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().addUAXmpMetadata()));
        manipulatePdf(pdf, false);
        Assert.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void addUAXMPMetaDataTaggedTest() throws IOException, InterruptedException {
        String fileName = "addUAXMPMetaDataTaggedTest.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().addUAXmpMetadata()));
        manipulatePdf(pdf, true);
        Assert.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void doNotAddUAXMPMetaDataTaggedTest() throws IOException {
        String fileName = "doNotAddUAXMPMetaDataTaggedTest.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().addXmpMetadata()));
        manipulatePdf(pdf, true);
        Assert.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    private void manipulatePdf(PdfDocument pdfDocument, boolean setTagged) throws IOException {
        Document document = new Document(pdfDocument);
        if (setTagged)
            pdfDocument.setTagged();
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        pdfDocument.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("iText7 PDF/UA test");
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
        Paragraph p = new Paragraph();
        p.setFont(font);
        p.add(new Text("The quick brown "));
        Image foxImage = new Image(ImageDataFactory.create(FOX));
        foxImage.getAccessibilityProperties().setAlternateDescription("Fox");
        p.add(foxImage);
        p.add(" jumps over the lazy ");
        Image dogImage = new Image(ImageDataFactory.create(DOG));
        dogImage.getAccessibilityProperties().setAlternateDescription("Dog");
        p.add(dogImage);
        document.add(p);
        document.close();
    }
}
