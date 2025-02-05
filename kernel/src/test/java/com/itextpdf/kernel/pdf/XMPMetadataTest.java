/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class XMPMetadataTest extends ExtendedITextTest{

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/XmpWriterTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/XmpWriterTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }
    
    @Test
    public void createEmptyDocumentWithXmp() throws Exception {
        String filename = "emptyDocumentWithXmp.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename,  new WriterProperties().addXmpMetadata());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText").
                setTitle("Empty iText Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.ModDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();
        PdfReader reader = CompareTool.createOutputReader(DESTINATION_FOLDER + filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        byte[] outBytes = pdfDocument.getXmpMetadataBytes();
        pdfDocument.close();

        byte[] cmpBytes = readFile(SOURCE_FOLDER + "emptyDocumentWithXmp.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assertions.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }

    @Test
    public void emptyDocumentWithXmpAppendMode01() throws Exception {
        String created = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode01.pdf";
        String updated = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode01_updated.pdf";
        String updatedAgain = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode01_updatedAgain.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(created));
        pdfDocument.addNewPage();

        // create XMP metadata
        pdfDocument.getXmpMetadata(true);
        pdfDocument.close();

        pdfDocument = new PdfDocument(CompareTool.createOutputReader(created), CompareTool.createTestPdfWriter(updated), new StampingProperties().useAppendMode());
        pdfDocument.close();
        pdfDocument = new PdfDocument(CompareTool.createOutputReader(updated), CompareTool.createTestPdfWriter(updatedAgain), new StampingProperties().useAppendMode());
        pdfDocument.close();

        PdfReader reader = CompareTool.createOutputReader(updatedAgain);
        pdfDocument = new PdfDocument(reader);

        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertNotNull(pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata));

        PdfIndirectReference metadataRef = pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata).getIndirectReference();

        Assertions.assertEquals(6, metadataRef.getObjNumber());
        Assertions.assertEquals(0, metadataRef.getGenNumber());

        byte[] outBytes = pdfDocument.getXmpMetadataBytes();
        pdfDocument.close();

        byte[] cmpBytes = readFile(SOURCE_FOLDER + "emptyDocumentWithXmpAppendMode01.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assertions.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }

    @Test
    public void emptyDocumentWithXmpAppendMode02() throws Exception {
        String created = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode02.pdf";
        String updated = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode02_updated.pdf";
        String updatedAgain = DESTINATION_FOLDER + "emptyDocumentWithXmpAppendMode02_updatedAgain.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(created));
        pdfDocument.addNewPage();
        pdfDocument.close();

        pdfDocument = new PdfDocument(CompareTool.createOutputReader(created), CompareTool.createTestPdfWriter(updated), new StampingProperties().useAppendMode());
        // create XMP metadata
        pdfDocument.getXmpMetadata(true);
        pdfDocument.close();

        pdfDocument = new PdfDocument(CompareTool.createOutputReader(updated), CompareTool.createTestPdfWriter(updatedAgain), new StampingProperties().useAppendMode());
        pdfDocument.close();

        PdfReader reader = CompareTool.createOutputReader(updatedAgain);
        pdfDocument = new PdfDocument(reader);

        Assertions.assertEquals(false, reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertNotNull(pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata));

        PdfIndirectReference metadataRef = pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata).getIndirectReference();

        Assertions.assertEquals(6, metadataRef.getObjNumber());
        Assertions.assertEquals(0, metadataRef.getGenNumber());

        byte[] outBytes = pdfDocument.getXmpMetadataBytes();
        pdfDocument.close();

        byte[] cmpBytes = readFile(SOURCE_FOLDER + "emptyDocumentWithXmpAppendMode02.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assertions.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, count = 2)
    })
    public void createEmptyDocumentWithAbcXmp() throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText").
                setTitle("Empty iText Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.ModDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata("abc".getBytes(StandardCharsets.ISO_8859_1));
        pdfDoc.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(fos.toByteArray()));
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assertions.assertFalse(reader.hasRebuiltXref(), "Rebuilt");
        Assertions.assertArrayEquals("abc".getBytes(StandardCharsets.ISO_8859_1), pdfDocument.getXmpMetadataBytes());
        Assertions.assertNotNull(pdfDocument.getPage(1));
        reader.close();
    }

    @Test
    @Disabled("DEVSIX-1899: fails in .NET passes in Java")
    public void customXmpTest() throws IOException, InterruptedException, XMPException {
        runCustomXmpTest("customXmp",
                "<?xpacket begin='' id='W5M0MpCehiHzreSzNTczkc9d' bytes='770'?>\n" +
                "\n" +
                "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
                " xmlns:iX='http://ns.adobe.com/iX/1.0/'>\n" +
                "\n" +
                " <rdf:Description about=''\n" +
                "  xmlns='http://ns.adobe.com/pdf/1.3/'\n" +
                "  xmlns:pdf='http://ns.adobe.com/pdf/1.3/'>\n" +
                "  <pdf:ModDate>2001-03-28T15:17:00-08:00</pdf:ModDate>\n" +
                "  <pdf:CreationDate>2001-03-28T15:19:45-08:00</pdf:CreationDate>\n" +
                " </rdf:Description>\n" +
                "\n" +
                " <rdf:Description about=''\n" +
                "  xmlns='http://ns.adobe.com/xap/1.0/'\n" +
                "  xmlns:xap='http://ns.adobe.com/xap/1.0/'>\n" +
                "  <xap:ModifyDate>2001-03-28T15:17:00-08:00</xap:ModifyDate>\n" +
                "  <xap:CreateDate>2001-03-28T15:19:45-08:00</xap:CreateDate>\n" +
                "  <xap:MetadataDate>2001-03-28T15:17:00-08:00</xap:MetadataDate>\n" +
                " </rdf:Description>\n" +
                "\n" +
                "</rdf:RDF>\n" +
                "<?xpacket end='r'?>");
    }

    @Test
    @Disabled("DEVSIX-1899: fails in .NET passes in Java")
    public void customXmpTest02() throws IOException, InterruptedException, XMPException {
        runCustomXmpTest("customXmp02",
                "<?xpacket begin='' id='W5M0MpCehiHzreSzNTczkc9d' bytes='1026'?><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:iX='http://ns.adobe.com/iX/1.0/'><rdf:Description about='' xmlns='http://ns.adobe.com/pdf/1.3/' xmlns:pdf='http://ns.adobe.com/pdf/1.3/' pdf:CreationDate='2016-01-27T13:07:23Z' pdf:ModDate='2016-01-27T13:07:23Z' pdf:Producer='Acrobat Distiller 5.0.5 (Windows)' pdf:Author='Koeck' pdf:Creator='PScript5.dll Version 5.2.2' pdf:Title='Rasant_ACE.indd'/>\n" +
                "<rdf:Description about='' xmlns='http://ns.adobe.com/xap/1.0/' xmlns:xap='http://ns.adobe.com/xap/1.0/' xap:CreateDate='2016-01-27T13:07:23Z' xap:ModifyDate='2016-01-27T13:07:23Z' xap:Author='Koeck' xap:MetadataDate='2016-01-27T13:07:23Z'><xap:Title><rdf:Alt><rdf:li xml:lang='x-default'>Rasant_ACE.indd</rdf:li></rdf:Alt></xap:Title></rdf:Description>\n" +
                "<rdf:Description about='' xmlns='http://purl.org/dc/elements/1.1/' xmlns:dc='http://purl.org/dc/elements/1.1/' dc:creator='Koeck' dc:title='Rasant_ACE.indd'/>\n" +
                "</rdf:RDF><?xpacket end='r'?>");
    }

    private void runCustomXmpTest(String name, String xmp) throws IOException, InterruptedException {
        String outPath = DESTINATION_FOLDER + name + ".pdf";
        String cmpPath = SOURCE_FOLDER + "cmp_" + name + ".pdf";

        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPath));
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata(xmp.getBytes(StandardCharsets.ISO_8859_1));
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        Assertions.assertNull(compareTool.compareByContent(outPath, cmpPath, DESTINATION_FOLDER, "diff_" + name + "_"));
        Assertions.assertNull(compareTool.compareDocumentInfo(outPath, cmpPath));
    }

    private byte[] removeAlwaysDifferentEntries(byte[] cmpBytes) throws XMPException {
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(cmpBytes);

        XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.CreateDate, true, true);
        XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.ModifyDate, true, true);
        XMPUtils.removeProperties(xmpMeta, XMPConst.NS_XMP, PdfConst.MetadataDate, true, true);
        XMPUtils.removeProperties(xmpMeta, XMPConst.NS_PDF, PdfConst.Producer, true, true);

        cmpBytes = XMPMetaFactory.serializeToBuffer(xmpMeta, new SerializeOptions(SerializeOptions.SORT));
        return cmpBytes;
    }
}
