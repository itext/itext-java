/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
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

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class XMPWriterTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/XMPWriterTest/";
    public static final String DOG = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/dog.bmp";
    public static final String FONT = "./src/test/resources/com/itextpdf/layout/fonts/FreeSans.ttf";
    public static final String FOX = "./src/test/resources/com/itextpdf/layout/XMPWriterTest/fox.bmp";

    @BeforeAll
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
        Assertions.assertNull(ct.compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void addUAXMPMetaDataNotTaggedTest() throws IOException {
        String fileName = "addUAXMPMetaDataNotTaggedTest.pdf";
        WriterProperties writerProperties = new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1);
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, writerProperties));
        manipulatePdf(pdf, false);
        Assertions.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void addUAXMPMetaDataTaggedTest() throws IOException {
        String fileName = "addUAXMPMetaDataTaggedTest.pdf";
        WriterProperties writerProperties = new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1);
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, writerProperties));
        manipulatePdf(pdf, true);
        Assertions.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    @Test
    public void doNotAddUAXMPMetaDataTaggedTest() throws IOException {
        String fileName = "doNotAddUAXMPMetaDataTaggedTest.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().addXmpMetadata()));
        manipulatePdf(pdf, true);
        Assertions.assertNull(new CompareTool().compareXmp(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, true));
    }

    private void manipulatePdf(PdfDocument pdfDocument, boolean setTagged) throws IOException {
        Document document = new Document(pdfDocument);
        if (setTagged)
            pdfDocument.setTagged();
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        pdfDocument.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("iText PDF/UA test");
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
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
