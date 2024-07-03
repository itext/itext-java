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
package com.itextpdf.kernel.crypto;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
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
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.OutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag("BouncyCastleIntegrationTest")
public class UnencryptedWrapperTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
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
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void extractStandardEncryptedDocumentTest() throws IOException, InterruptedException {
        extractEncrypted("standardEncryptedDocument.pdf", "standardUnencryptedWrapper.pdf", "World".getBytes(StandardCharsets.ISO_8859_1));
    }

    private void createWrapper(String encryptedName, String wrapperName, String cryptoFilter) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + encryptedName;
        String cmpPath = sourceFolder + "cmp_" + wrapperName;
        String outPath = destinationFolder + wrapperName;
        String diff = "diff_" + wrapperName + "_";

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outPath, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
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

        Assertions.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    private void extractEncrypted(String encryptedName, String wrapperName, byte[] password) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + wrapperName;
        String cmpPath = sourceFolder + "cmp_" + encryptedName;
        String outPath = destinationFolder + encryptedName;
        String diff = "diff_" + encryptedName + "_";

        PdfDocument document = new PdfDocument(new PdfReader(inPath));
        PdfEncryptedPayloadDocument encryptedDocument = document.getEncryptedPayloadDocument();
        byte[] encryptedDocumentBytes = encryptedDocument.getDocumentBytes();
        OutputStream fos = FileUtil.getFileOutputStream(outPath);
        fos.write(encryptedDocumentBytes);
        fos.close();
        document.close();

        PdfEncryptedPayload ep = encryptedDocument.getEncryptedPayload();
        Assertions.assertEquals(PdfEncryptedPayloadFileSpecFactory.generateFileDisplay(ep), encryptedDocument.getName());
        if (password != null) {
            Assertions.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff, password, password));
        } else {
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(cmpPath));
            byte[] cmpBytes = new byte[(int) raf.length()];
            raf.readFully(cmpBytes);
            raf.close();
            Assertions.assertArrayEquals(cmpBytes, encryptedDocumentBytes);
        }
    }
}
