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
package com.itextpdf.kernel.crypto.pdfencryption;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;



@Tag("IntegrationTest")
public class PdfPreserveEncryptionTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/pdfencryption/PdfPreserveEncryptionTest/";

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/pdfencryption/PdfPreserveEncryptionTest/";

    public PdfEncryptionTestUtils encryptionUtil = new PdfEncryptionTestUtils(destinationFolder, sourceFolder);

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, ignore = true),
            @LogMessage(messageTemplate = VersionConforming.DEPRECATED_ENCRYPTION_ALGORITHMS)})
    public void stampAndUpdateVersionPreserveStandard40() throws InterruptedException, IOException {
        String filename = "stampAndUpdateVersionPreserveStandard40.pdf";
        PdfDocument doc = new PdfDocument(
                new PdfReader(sourceFolder + "encryptedWithPasswordStandard40.pdf",
                        new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                CompareTool.createTestPdfWriter(destinationFolder + filename,
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().preserveEncryption());
        doc.close();

        encryptionUtil.compareEncryptedPdf(filename);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, ignore = true),
            @LogMessage(messageTemplate = VersionConforming.DEPRECATED_AES256_REVISION)})
    public void stampAndUpdateVersionPreserveAes256() throws InterruptedException, IOException {
        String filename = "stampAndUpdateVersionPreserveAes256.pdf";
        PdfDocument doc = new PdfDocument(
                new PdfReader(sourceFolder + "encryptedWithPasswordAes256.pdf",
                        new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                CompareTool.createTestPdfWriter(destinationFolder + filename,
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                new StampingProperties().preserveEncryption());
        doc.close();
        encryptionUtil.compareEncryptedPdf(filename);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256EncryptedStampingPreserve() throws InterruptedException, IOException {
        String filename = "encryptAes256EncryptedStampingPreserve.pdf";
        String src = sourceFolder + "encryptedWithPlainMetadata.pdf";
        String out = destinationFolder + filename;

        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(src, new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                CompareTool.createTestPdfWriter(out, new WriterProperties()),
                new StampingProperties().preserveEncryption());

        pdfDoc.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(out, sourceFolder + "cmp_" + filename, destinationFolder,
                "diff_", PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.USER);
        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }

    @Test
    public void preserveEncryptionShorterDocumentId() throws IOException, InterruptedException {
        String filename = "preserveEncryptionWithShortId.pdf";
        String src = sourceFolder + "encryptedWithShortId.pdf";
        String out = destinationFolder + filename;

        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(src, new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                new PdfWriter(out, new WriterProperties()),
                new StampingProperties().preserveEncryption());

        pdfDoc.close();
        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(out, sourceFolder + "cmp_" + filename, destinationFolder,
                "diff_", null, null);
        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }
}
