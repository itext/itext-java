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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.OutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class PubKeySecurityHandlerTest extends ExtendedITextTest {
    
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void computeGlobalKeyDecryptTest() {
        PubKeySecurityHandler securityHandler = new TestSecurityHandler();
        Assertions.assertEquals(20, securityHandler.computeGlobalKey("SHA1", false).length);
    }
    
    private static class TestSecurityHandler extends PubKeySecurityHandler {
        @Override
        public OutputStreamEncryption getEncryptionStream(OutputStream os) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IDecryptor getDecryptor() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata,
                boolean embeddedFilesOnly) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected String getDigestAlgorithm() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void initKey(byte[] globalKey, int keyLength) {
            throw new UnsupportedOperationException();
        }
    }
}
