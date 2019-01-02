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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Michael Demey
 */
@Category(IntegrationTest.class)
public class PdfDocumentIdTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTestID/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTestID/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void changeIdTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        String value = "Modified ID 1234";
        writer.properties.setModifiedDocumentId(new PdfString(value));
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        Assert.assertNotNull(idArray);
        String extractedValue = idArray.getAsString(1).getValue();
        pdfDocument.close();

        Assert.assertEquals(value, extractedValue);
    }

    @Test
    public void changeIdTest02() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString initialId = new PdfString(md5.digest("Initial ID 56789".getBytes()));
        PdfWriter writer = new PdfWriter(baos, new WriterProperties().setInitialDocumentId(initialId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        byte[] documentBytes = baos.toByteArray();

        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(documentBytes));
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        Assert.assertNotNull(idArray);
        PdfString extractedString = idArray.getAsString(1);
        pdfDocument.close();

        Assert.assertEquals(initialId, extractedString);
    }

    @Test
    public void changeIdTest03() throws IOException {
        ByteArrayOutputStream baosInitial = new ByteArrayOutputStream();
        ByteArrayOutputStream baosModified = new ByteArrayOutputStream();

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(e);
        }
        PdfString initialId = new PdfString(md5.digest("Initial ID 56789".getBytes()));
        PdfString modifiedId = new PdfString("Modified ID 56789");

        PdfWriter writer = new PdfWriter(baosInitial, new WriterProperties()
                .setInitialDocumentId(initialId).setModifiedDocumentId(modifiedId));
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        pdfDocument.close();

        PdfReader reader = new PdfReader(new RandomAccessSourceFactory().createSource(baosInitial.toByteArray()), new ReaderProperties());
        pdfDocument = new PdfDocument(reader);
        PdfArray idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        pdfDocument.close();
        Assert.assertNotNull(idArray);
        PdfString extractedInitialValue = idArray.getAsString(0);
        Assert.assertEquals(initialId, extractedInitialValue);
        PdfString extractedModifiedValue = idArray.getAsString(1);
        Assert.assertEquals(modifiedId, extractedModifiedValue);


        pdfDocument = new PdfDocument(new PdfReader(new RandomAccessSourceFactory().createSource(baosInitial.toByteArray()), new ReaderProperties()),
                new PdfWriter(baosModified));
        new PdfCanvas(pdfDocument.addNewPage())
                .saveState()
                .lineTo(100, 100)
                .moveTo(100, 100)
                .stroke()
                .restoreState();
        pdfDocument.close();

        reader = new PdfReader(new RandomAccessSourceFactory().createSource(baosModified.toByteArray()), new ReaderProperties());
        pdfDocument = new PdfDocument(reader);
        idArray = pdfDocument.getTrailer().getAsArray(PdfName.ID);
        pdfDocument.close();
        Assert.assertNotNull(idArray);
        extractedInitialValue = idArray.getAsString(0);
        Assert.assertEquals(initialId, extractedInitialValue);
        extractedModifiedValue = idArray.getAsString(1);
        Assert.assertNotEquals(modifiedId, extractedModifiedValue);
    }

}
