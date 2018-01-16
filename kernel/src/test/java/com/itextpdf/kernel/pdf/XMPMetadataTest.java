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

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XMPMetadataTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/XmpWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/XmpWriterTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createEmptyDocumentWithXmp() throws Exception {
        String filename = "emptyDocumentWithXmp.pdf";
        PdfWriter writer = new PdfWriter(destinationFolder + filename,  new WriterProperties().addXmpMetadata());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 7").
                setTitle("Empty iText 7 Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.ModDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();
        PdfReader reader = new PdfReader(destinationFolder + filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        byte[] outBytes = pdfDocument.getXmpMetadata();
        pdfDocument.close();

        byte[] cmpBytes = readFile(sourceFolder + "emptyDocumentWithXmp.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assert.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }

    @Test
    public void emptyDocumentWithXmpAppendMode01() throws Exception {
        String created = destinationFolder + "emptyDocumentWithXmpAppendMode01.pdf";
        String updated = destinationFolder + "emptyDocumentWithXmpAppendMode01_updated.pdf";
        String updatedAgain = destinationFolder + "emptyDocumentWithXmpAppendMode01_updatedAgain.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(created));
        pdfDocument.addNewPage();

        pdfDocument.getXmpMetadata(true); // create XMP metadata
        pdfDocument.close();

        pdfDocument = new PdfDocument(new PdfReader(created), new PdfWriter(updated), new StampingProperties().useAppendMode());
        pdfDocument.close();
        pdfDocument = new PdfDocument(new PdfReader(updated), new PdfWriter(updatedAgain), new StampingProperties().useAppendMode());
        pdfDocument.close();

        PdfReader reader = new PdfReader(updatedAgain);
        pdfDocument = new PdfDocument(reader);

        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertNotNull(pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata));

        PdfIndirectReference metadataRef = pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata).getIndirectReference();

        Assert.assertEquals(6, metadataRef.getObjNumber());
        Assert.assertEquals(0, metadataRef.getGenNumber());

        byte[] outBytes = pdfDocument.getXmpMetadata();
        pdfDocument.close();

        byte[] cmpBytes = readFile(sourceFolder + "emptyDocumentWithXmpAppendMode01.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assert.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }

    @Test
    public void emptyDocumentWithXmpAppendMode02() throws Exception {
        String created = destinationFolder + "emptyDocumentWithXmpAppendMode02.pdf";
        String updated = destinationFolder + "emptyDocumentWithXmpAppendMode02_updated.pdf";
        String updatedAgain = destinationFolder + "emptyDocumentWithXmpAppendMode02_updatedAgain.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(created));
        pdfDocument.addNewPage();
        pdfDocument.close();

        pdfDocument = new PdfDocument(new PdfReader(created), new PdfWriter(updated), new StampingProperties().useAppendMode());
        pdfDocument.getXmpMetadata(true); // create XMP metadata
        pdfDocument.close();
        
        pdfDocument = new PdfDocument(new PdfReader(updated), new PdfWriter(updatedAgain), new StampingProperties().useAppendMode());
        pdfDocument.close();

        PdfReader reader = new PdfReader(updatedAgain);
        pdfDocument = new PdfDocument(reader);

        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertNotNull(pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata));

        PdfIndirectReference metadataRef = pdfDocument.getCatalog().getPdfObject().getAsStream(PdfName.Metadata).getIndirectReference();

        Assert.assertEquals(6, metadataRef.getObjNumber());
        Assert.assertEquals(0, metadataRef.getGenNumber());

        byte[] outBytes = pdfDocument.getXmpMetadata();
        pdfDocument.close();

        byte[] cmpBytes = readFile(sourceFolder + "emptyDocumentWithXmpAppendMode02.xml");

        cmpBytes = removeAlwaysDifferentEntries(cmpBytes);
        outBytes = removeAlwaysDifferentEntries(outBytes);

        Assert.assertTrue(new CompareTool().compareXmls(outBytes, cmpBytes));
    }


    @Test
    public void createEmptyDocumentWithAbcXmp() throws IOException, XMPException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 7").
                setTitle("Empty iText 7 Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.ModDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata("abc".getBytes(StandardCharsets.ISO_8859_1));
        pdfDoc.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(fos.toByteArray()));
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertArrayEquals("abc".getBytes(StandardCharsets.ISO_8859_1), pdfDocument.getXmpMetadata());
        Assert.assertNotNull(pdfDocument.getPage(1));
        reader.close();
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
