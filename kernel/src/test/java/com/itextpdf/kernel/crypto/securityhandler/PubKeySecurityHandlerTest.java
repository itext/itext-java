package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PubKeySecurityHandlerTest extends ExtendedITextTest {
    
    @Test
    public void computeGlobalKeyDecryptTest() {
        PubKeySecurityHandler securityHandler = new TestSecurityHandler();
        Assert.assertEquals(20, securityHandler.computeGlobalKey("SHA1", false).length);
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
