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
package com.itextpdf.signatures;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class LtvVerifierUnitTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/LtvVerifierUnitTest/";

    @Test
    public void setVerifierTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier1 = new LtvVerifier(new PdfDocument(new PdfReader(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));
        LtvVerifier verifier2 = new LtvVerifier(new PdfDocument(new PdfReader(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier1.setVerifier(verifier2);
        Assertions.assertSame(verifier2, verifier1.verifier);
    }

    @Test
    public void setVerifyRootCertificateTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.setVerifyRootCertificate(true);
        Assertions.assertTrue(verifier.verifyRootCertificate);
    }

    @Test
    public void verifyNotNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));
        verifier.pkcs7 = null;

        List<VerificationOK> list = Collections.<VerificationOK>emptyList();
        Assertions.assertSame(list, verifier.verify(list));
    }

    @Test
    public void getCRLsFromDSSCRLsNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.dss = new PdfDictionary();
        Assertions.assertEquals(new ArrayList<>(), verifier.getCRLsFromDSS());
    }

    @Test
    public void getOCSPResponsesFromDSSOCSPsNullTest() throws GeneralSecurityException, IOException {
        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "ltvDoc.pdf"))));

        verifier.dss = new PdfDictionary();
        Assertions.assertEquals(new ArrayList<>(), verifier.getOCSPResponsesFromDSS());
    }
}
