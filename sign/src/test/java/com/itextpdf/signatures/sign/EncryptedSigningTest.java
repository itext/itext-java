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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class EncryptedSigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/EncryptedSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/EncryptedSigningTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void signEncryptedPdfTest() throws GeneralSecurityException, IOException {
        String srcFile = SOURCE_FOLDER + "encrypted.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signedEncrypted.pdf";
        String outPdf = DESTINATION_FOLDER + "signedEncrypted.pdf";

        String fieldName = "Signature1";

        byte[] ownerPass = "World".getBytes();
        PdfReader reader = new PdfReader(srcFile, new ReaderProperties().setPassword(ownerPass));
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(outPdf),
                new StampingProperties().useAppendMode());

        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        signer.setSignerProperties(signerProperties);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        //Password to open out and cmp files are the same
        ReaderProperties properties = new ReaderProperties().setPassword(ownerPass);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf, properties, properties));
    }

    @Test
    public void signCertificateSecurityPdfTest() throws IOException, GeneralSecurityException {
        //RSA keys in FIPS are supported for signature verification only
        if (!FACTORY.getProviderName().contains("FIPS")) {
            String srcFile = SOURCE_FOLDER + "signCertificateSecurityPdf.pdf";
            String cmpPdf = SOURCE_FOLDER + "cmp_signCertificateSecurityPdf.pdf";
            String outPdf = DESTINATION_FOLDER + "signCertificateSecurityPdf.pdf";

            PdfReader reader = new PdfReader(srcFile, new ReaderProperties()
                    .setPublicKeySecurityParams(chain[0], pk, FACTORY.getProviderName(), null));
            PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(outPdf),
                    new StampingProperties().useAppendMode());

            // Creating the signature
            IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                    FACTORY.getProviderName());
            signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0,
                    PdfSigner.CryptoStandard.CADES);

            ReaderProperties properties = new ReaderProperties().setPublicKeySecurityParams(chain[0], pk,
                    FACTORY.getProviderName(), null);

            //Public key to open out and cmp files are the same
            Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf, properties, properties));
        }
    }
}
