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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class PdfEncryptorTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfEncryptorTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfEncryptorTest/";


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void encryptFileTest() throws IOException {
        String outFileName = DESTINATION_FOLDER + "encryptFileTest.pdf";
        String initialFileName = SOURCE_FOLDER + "initial.pdf";
        PdfEncryptor encryptor = new PdfEncryptor();
        EncryptionProperties encryptionProperties = new EncryptionProperties();
        encryptionProperties.setStandardEncryption(new byte[16], new byte[16], 0, 0);
        encryptor.setEncryptionProperties(encryptionProperties);

        try (PdfReader initialFile = new PdfReader(initialFileName);
                OutputStream outputStream = FileUtil.getFileOutputStream(outFileName)) {
            encryptor.encrypt(initialFile, outputStream);
        }

        ReaderProperties readerProperties = new ReaderProperties();
        readerProperties.setPassword(new byte[16]);
        PdfReader outFile = new PdfReader(outFileName, readerProperties);
        PdfDocument doc = new PdfDocument(outFile);
        doc.close();
        Assert.assertTrue(outFile.isEncrypted());
    }
}
