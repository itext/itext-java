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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class PdfPKCS7BasicTest extends ExtendedITextTest {
    protected static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/PdfPKCS7Test/";

    protected static Certificate[] chain;

    protected static PrivateKey pk;

    protected static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());

        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
    }

    static void verifyIsoExtensionExample(String expectedSigMechanismName, String fileName)
            throws IOException, GeneralSecurityException {
        String infile = SOURCE_FOLDER + "extensions/" + fileName;
        try (PdfReader r = new PdfReader(infile); PdfDocument pdfDoc = new PdfDocument(r)) {
            SignatureUtil u = new SignatureUtil(pdfDoc);
            /*
            We specify the security provider explicitly; we're not testing security provider fallback here.

            Also, default providers (in 2022) don't always have the parameters for Brainpool curves,
            but a curve param mismatch doesn't factor into the algorithm support fallback logic, so
            it causes a runtime error.
            */
            PdfPKCS7 data = u.readSignatureData("Signature", BOUNCY_CASTLE_FACTORY.getProviderName());
            Assertions.assertEquals(expectedSigMechanismName, data.getSignatureMechanismName());
            Assertions.assertTrue(data.verifySignatureIntegrityAndAuthenticity());
        }
    }
}
