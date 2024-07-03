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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class EncryptionInApprovedModeTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto"
            + "/EncryptionInApprovedModeTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto"
            + "/EncryptionInApprovedModeTest/";

    /**
     * User password.
     */
    public static byte[] USER = "Hello".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Owner password.
     */
    public static byte[] OWNER = "World".getBytes(StandardCharsets.ISO_8859_1);

    @BeforeAll
    public static void beforeClass() {
        Assumptions.assumeTrue(FACTORY.isInApprovedOnlyMode());
        createOrClearDestinationFolder(destinationFolder);
        Security.addProvider(FACTORY.getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT))
    public void checkMD5LogMessageWhileReadingPdfTest() throws IOException {
        String fileName = "checkMD5LogMessageWhileReadingPdf.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + fileName))) {
            // this test checks log message
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT))
    public void checkMD5LogMessageWhileCreatingPdfTest() throws IOException {
        String fileName = "checkMD5LogMessageWhileCreatingPdf.pdf";
        try (PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName,
                new WriterProperties().setStandardEncryption(USER, OWNER, EncryptionConstants.ALLOW_SCREENREADERS,
                        EncryptionConstants.ENCRYPTION_AES_256).addXmpMetadata()))) {
            // this test checks log message
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            count = 3))
    public void checkMD5LogMessageForEachPdfTest() throws IOException {
        String fileName = "checkMD5LogMessageForEachPdf.pdf";
        for (int i = 0; i < 3; ++i) {
            try (PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName,
                    new WriterProperties().setStandardEncryption(USER, OWNER, EncryptionConstants.ALLOW_SCREENREADERS,
                            EncryptionConstants.ENCRYPTION_AES_256).addXmpMetadata()))) {
                // this test checks log message
            }
        }
    }
}
