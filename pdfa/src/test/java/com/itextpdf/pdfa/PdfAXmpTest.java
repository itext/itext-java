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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
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
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfAXmpTest extends ExtendedITextTest {

    public static final String cmpFolder = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfAXmpTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAXmpTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void keywordsInfoTestPdfA1b() throws IOException, InterruptedException {
        String outFile = destinationFolder + "keywordsInfoTestPdfA1b.pdf";
        String cmpFile = cmpFolder + "cmp_keywordsInfoTestPdfA1b.pdf";

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(new PdfWriter(outFile), PdfAConformance.PDF_A_1B, outputIntent);
        doc.addNewPage();

        doc.getDocumentInfo().setKeywords("key1, key2 , key3;key4,key5");

        doc.close();

        CompareTool ct = new CompareTool();
        Assertions.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder));
        Assertions.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assertions.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void keywordsInfoTestPdfA2b() throws IOException, InterruptedException {
        String outFile = destinationFolder + "keywordsInfoTestPdfA2b.pdf";
        String cmpFile = cmpFolder + "cmp_keywordsInfoTestPdfA2b.pdf";

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument doc = new PdfADocument(new PdfWriter(outFile), PdfAConformance.PDF_A_2B, outputIntent);
        doc.addNewPage();

        doc.getDocumentInfo().setKeywords("key1, key2 , key3;key4,key5");

        doc.close();

        CompareTool ct = new CompareTool();
        Assertions.assertNull(ct.compareByContent(outFile, cmpFile, destinationFolder));
        Assertions.assertNull(ct.compareDocumentInfo(outFile, cmpFile));
        Assertions.assertNull(ct.compareXmp(outFile, cmpFile, true));
    }

    @Test
    public void saveAndReadDocumentWithCanonicalXmpMetadata() throws IOException, XMPException {
        String outFile = destinationFolder + "saveAndReadDocumentWithCanonicalXmpMetadata.pdf";
        String cmpFile = cmpFolder + "cmp_saveAndReadDocumentWithCanonicalXmpMetadata.pdf";
        PdfAConformance conformance = PdfAConformance.PDF_A_2B;
        PdfOutputIntent outputIntent;

        try (InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")) {
            outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        }

        try (PdfADocument doc = new PdfADocument(new PdfWriter(outFile), conformance, outputIntent)) {
            doc.addNewPage();
            XMPMeta xmp = XMPMetaFactory.create();
            xmp.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, conformance.getPart(), new PropertyOptions().setSchemaNode(true));
            xmp.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, conformance.getLevel(), new PropertyOptions().setSchemaNode(true));
            SerializeOptions options = new SerializeOptions().setUseCanonicalFormat(true).setUseCompactFormat(false);
            doc.setXmpMetadata(xmp, options);
            doc.setTagged();
        }
        // Closing document and reopening it to flush it XMP metadata ModifyDate
        try (PdfDocument doc = new PdfDocument(new PdfReader(outFile));
             PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpFile))) {
            byte[] rdf = doc.getXmpMetadataBytes();
            byte[] expectedRdf = cmpDoc.getXmpMetadataBytes();
            // Comparing angle brackets, since it's the main difference between canonical and compact format.
            Assertions.assertEquals(count(expectedRdf, (byte)'<'), count(rdf, (byte)'<'));
            Assertions.assertNull(new CompareTool().compareXmp(cmpFile, outFile, true));
        }
    }

    @Test
    public void testPdfUAExtensionMetadata() throws IOException {

        String outFile = destinationFolder + "testPdfUAExtensionMetadata.pdf";
        String cmpFile = cmpFolder + "cmp_testPdfUAExtensionMetadata.pdf";

        try (OutputStream fos = FileUtil.getFileOutputStream(outFile)) {
            generatePdfAWithUA(fos);
        }

        CompareTool ct = new CompareTool();
        Assertions.assertNull(ct.compareXmp(outFile, cmpFile, true));

    }

    @Test
    public void testPdfUAIdSchemaNameSpaceUriIsNotText() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generatePdfAWithUA(baos);

        // check whether the pdfuaid NS URI was properly encoded as a URI with rdf:resource
        PdfDocument readDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        String xmpString = new String(readDoc.getXmpMetadataBytes(), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                xmpString.contains("<pdfaSchema:namespaceURI rdf:resource=\"http://www.aiim.org/pdfua/ns/id/\"/>"),
                "Did not find expected namespaceURI definition");

    }

    private void generatePdfAWithUA(OutputStream os) throws IOException {
        WriterProperties wp = new WriterProperties().addPdfUaXmpMetadata(PdfUAConformance.PDF_UA_1);
        try (PdfWriter w = new PdfWriter(os, wp)) {
            PdfOutputIntent outputIntent;
            try (InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")) {
                outputIntent = new PdfOutputIntent(
                        "Custom", "",
                        "http://www.color.org",
                        "sRGB IEC61966-2.1",
                        is
                );
            }
            PdfDocument pdfDoc = new PdfADocument(w, PdfAConformance.PDF_A_2A, outputIntent);
            pdfDoc.setTagged();
            pdfDoc.getDocumentInfo().setTitle("Test document");
            pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
            pdfDoc.getCatalog().setLang(new PdfString("en"));
            pdfDoc.addNewPage();
            pdfDoc.close();
        }
    }

    private int count(byte[] array, byte b) {
        int counter = 0;
        for (byte each : array) {
            if (each == b) {
                counter++;
            }
        }
        return counter;
    }

}
