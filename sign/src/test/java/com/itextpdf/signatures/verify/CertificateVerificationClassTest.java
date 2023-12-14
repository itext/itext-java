/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures.verify;

import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CertificateVerificationClassTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        ITextTest.removeCryptographyRestrictions();
    }

    @AfterClass
    public static void after() {
        ITextTest.restoreCryptographyRestrictions();
    }

    @Test
    public void validCertificateChain01() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchProviderException {
        Certificate[] certChain = Pkcs12FileHelper.readFirstChain(certsSrc + "signCertRsaWithChain.p12", password);

        String caCertFileName = certsSrc + "rootRsa.p12";
        KeyStore caKeyStore = Pkcs12FileHelper.initStore(caCertFileName, password);

        List<VerificationException> verificationExceptions = CertificateVerification.verifyCertificates(certChain, caKeyStore);

        Assert.assertTrue(verificationExceptions.isEmpty());
    }
}
