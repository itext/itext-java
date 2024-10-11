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
package com.itextpdf.signatures.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class ReadSignedMacProtectedDocumentTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/mac/ReadSignedMacProtectedDocumentTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/mac/ReadSignedMacProtectedDocumentTest/";
    private static final byte[] ENCRYPTION_PASSWORD = "123".getBytes();

    @BeforeAll
    public static void before() {
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void readSignedMacProtectedInvalidDocTest() {
        String srcFileName = SOURCE_FOLDER + "signedMacProtectedInvalidDoc.pdf";

        String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument ignored = new PdfDocument(
                    new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD)))) {
                // Do nothing.
            }
        }).getMessage();
        Assertions.assertEquals(KernelExceptionMessageConstant.MAC_VALIDATION_FAILED, exceptionMessage);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void updateSignedMacProtectedDocumentTest() throws Exception {
        String fileName = "updateSignedMacProtectedDocumentTest.pdf";
        String srcFileName = SOURCE_FOLDER + "thirdPartyMacProtectedAndSignedDocument.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        try (PdfDocument ignored = new PdfDocument(
                new PdfReader(srcFileName, new ReaderProperties().setPassword(ENCRYPTION_PASSWORD)),
                new PdfWriter(FileUtil.getFileOutputStream(outputFileName)),
                new StampingProperties().useAppendMode())) {
            // Do nothing.
        }

        // This call produces INFO log from AESCipher caused by exception while decrypting. The reason is that,
        // while comparing encrypted signed documents, CompareTool needs to mark signature value as unencrypted.
        // Instead, it tries to decrypt not encrypted value which results in exception.
        Assertions.assertNull(new CompareTool().compareByContent(
                outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", ENCRYPTION_PASSWORD, ENCRYPTION_PASSWORD));
    }
}
