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
package com.itextpdf.kernel.crypto;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryptedPayload;
import com.itextpdf.kernel.pdf.PdfEncryptedPayloadDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfEncryptedPayloadFileSpecFactory;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Category(IntegrationTest.class)
public class UnencryptedWrapperTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createSimpleWrapperDocumentTest() throws IOException, InterruptedException {
        createWrapper("customEncryptedDocument.pdf", "simpleUnencryptedWrapper.pdf", "iText");
    }

    @Test
    public void extractCustomEncryptedDocumentTest() throws IOException, InterruptedException {
        extractEncrypted("customEncryptedDocument.pdf", "simpleUnencryptedWrapper.pdf", null);
    }

    @Test
    public void createWrapperForStandardEncryptedTest() throws IOException, InterruptedException {
        createWrapper("standardEncryptedDocument.pdf", "standardUnencryptedWrapper.pdf", "Standard");
    }

    @Test
    public void extractStandardEncryptedDocumentTest() throws IOException, InterruptedException {
        extractEncrypted("standardEncryptedDocument.pdf", "standardUnencryptedWrapper.pdf", "World".getBytes(StandardCharsets.ISO_8859_1));
    }

    private void createWrapper(String encryptedName, String wrapperName, String cryptoFilter) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + encryptedName;
        String cmpPath = sourceFolder + "cmp_" + wrapperName;
        String outPath = destinationFolder + wrapperName;
        String diff = "diff_" + wrapperName + "_";

        PdfDocument document = new PdfDocument(new PdfWriter(outPath, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfFileSpec fs = PdfEncryptedPayloadFileSpecFactory.create(document, inPath, new PdfEncryptedPayload(cryptoFilter));
        document.setEncryptedPayload(fs);

        PdfFont font = PdfFontFactory.createFont();
        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 750).
                setFontAndSize(font, 30).
                showText("Hi! I'm wrapper document.").
                endText().
                restoreState();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    private void extractEncrypted(String encryptedName, String wrapperName, byte[] password) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + wrapperName;
        String cmpPath = sourceFolder + "cmp_" + encryptedName;
        String outPath = destinationFolder + encryptedName;
        String diff = "diff_" + encryptedName + "_";

        PdfDocument document = new PdfDocument(new PdfReader(inPath));
        PdfEncryptedPayloadDocument encryptedDocument = document.getEncryptedPayloadDocument();
        byte[] encryptedDocumentBytes = encryptedDocument.getDocumentBytes();
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(encryptedDocumentBytes);
        fos.close();
        document.close();

        PdfEncryptedPayload ep = encryptedDocument.getEncryptedPayload();
        Assert.assertEquals(PdfEncryptedPayloadFileSpecFactory.generateFileDisplay(ep), encryptedDocument.getName());
        if (password != null) {
            Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff, password, password));
        } else {
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(cmpPath));
            byte[] cmpBytes = new byte[(int) raf.length()];
            raf.readFully(cmpBytes);
            raf.close();
            Assert.assertArrayEquals(cmpBytes, encryptedDocumentBytes);
        }
    }
}
