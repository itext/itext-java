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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.LtvVerifier;
import com.itextpdf.signatures.VerificationOK;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LtvVerifierTest extends ExtendedITextTest {
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/LtvVerifierTest/";
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
    public void validLtvDocTest01() throws IOException, GeneralSecurityException {
        String ltvTsFileName = sourceFolder + "ltvDoc.pdf";

        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(ltvTsFileName)));
        verifier.setCertificateOption(LtvVerification.CertificateOption.WHOLE_CHAIN);
        verifier.setRootStore(Pkcs12FileHelper.initStore(certsSrc + "rootStore.p12", password));
        List<VerificationOK> verificationMessages = verifier.verify(null);

        Assert.assertEquals(7, verificationMessages.size());
    }
    @Test
    public void validLtvDocTest02() throws IOException, GeneralSecurityException {
        String ltvTsFileName = sourceFolder + "ltvDoc.pdf";

        Security.addProvider(new BouncyCastleProvider());

        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(ltvTsFileName)), BouncyCastleProvider.PROVIDER_NAME);
        verifier.setCertificateOption(LtvVerification.CertificateOption.WHOLE_CHAIN);
        verifier.setRootStore(Pkcs12FileHelper.initStore(certsSrc + "rootStore.p12", password));
        List<VerificationOK> verificationMessages = verifier.verify(null);

        Assert.assertEquals(7, verificationMessages.size());
    }

}
