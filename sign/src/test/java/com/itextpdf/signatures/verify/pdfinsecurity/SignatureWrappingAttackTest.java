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
package com.itextpdf.signatures.verify.pdfinsecurity;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ExtendedITextTest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class SignatureWrappingAttackTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/verify/pdfinsecurity/SignatureWrappingAttackTest/";

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void testSWA01() throws IOException, GeneralSecurityException {
        String filePath = sourceFolder + "siwa.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);
        Assertions.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assertions.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));
        document.close();
    }
}
