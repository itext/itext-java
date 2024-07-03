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
package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfAAgnosticPdfDocumentUnitTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/AgnosticPdfDocumentUnitTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void loadPdfDocumentTest() throws IOException, XMPException {
        TestAgnosticPdfDocument pdfDoc = new TestAgnosticPdfDocument(new PdfReader(sourceFolder + "pdfs/simpleDoc.pdf"),
                new PdfWriter(new ByteArrayOutputStream()));

        pdfDoc.flushObjectPublic(pdfDoc.getPage(1).getPdfObject(), true);
        Assertions.assertTrue(pdfDoc.getPage(1).getPdfObject().isFlushed());

        pdfDoc.checkIsoConformancePublic(); // Does nothing for PdfDocument
        Assertions.assertFalse(pdfDoc.getPageFactoryPublic() instanceof PdfAPageFactory);
        Assertions.assertNull(pdfDoc.getConformanceLevel());

        pdfDoc.updateXmpMetadataPublic();
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(pdfDoc.getXmpMetadata(true));
        Assertions.assertNull(xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART));
        Assertions.assertNull(xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE));

        pdfDoc.close();
    }

    @Test
    public void getDefaultFont() throws IOException {
        TestAgnosticPdfDocument pdfDoc = new TestAgnosticPdfDocument(new PdfReader(sourceFolder + "pdfs/simpleDoc.pdf"),
                new PdfWriter(new ByteArrayOutputStream()));
        Assertions.assertNotNull(pdfDoc.getDefaultFont());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED)})
    public void loadPdfADocumentTest() throws IOException, XMPException {
        TestAgnosticPdfDocument pdfADoc = new TestAgnosticPdfDocument(new PdfReader(sourceFolder + "pdfs/pdfa.pdf"),
                new PdfWriter(new ByteArrayOutputStream()), new StampingProperties());

        pdfADoc.flushObjectPublic(pdfADoc.getPage(1).getPdfObject(), true);
        Assertions.assertFalse(pdfADoc.getPage(1).getPdfObject().isFlushed());

        pdfADoc.checkIsoConformancePublic();
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_2B, pdfADoc.getConformanceLevel());
        Assertions.assertTrue(pdfADoc.getPageFactoryPublic() instanceof PdfAPageFactory);

        pdfADoc.updateXmpMetadataPublic();
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(pdfADoc.getXmpMetadata(true));
        Assertions.assertNotNull(xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART));
        Assertions.assertNotNull(xmpMeta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE));

        // Extra PdfA error message check
        pdfADoc.flushObjectPublic(pdfADoc.getCatalog().getPdfObject(), true);
        Assertions.assertFalse(pdfADoc.getCatalog().getPdfObject().isFlushed());

        pdfADoc.close();
    }

    private class TestAgnosticPdfDocument extends PdfAAgnosticPdfDocument {

        public TestAgnosticPdfDocument(PdfReader reader, PdfWriter writer) {
            super(reader, writer, new StampingProperties());
        }

        public TestAgnosticPdfDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
            super(reader, writer, properties);
        }

        public void checkIsoConformancePublic() {
            super.checkIsoConformance();
        }

        public IPdfPageFactory getPageFactoryPublic() {
            return super.getPageFactory();
        }

        public void updateXmpMetadataPublic() {
            super.updateXmpMetadata();
        }

        public void flushObjectPublic(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
            super.flushObject(pdfObject, canBeInObjStm);
        }
    }
}
