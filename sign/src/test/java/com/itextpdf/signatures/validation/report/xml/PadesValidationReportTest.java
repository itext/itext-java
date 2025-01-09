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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;

@Tag("BouncyCastleUnitTest")
public class PadesValidationReportTest extends ExtendedITextTest {
    private static CMSContainer signature;
    private final ValidationObjects validationObjects = new ValidationObjects();

    @BeforeAll
    public static void setupFixture() throws CertificateException, IOException, CRLException {
        signature = new CMSContainer(Base64.decode(XmlReportTestHelper.SIGNATURE1_BASE64));
    }

    @Test
    public void testCreation() {
        PadesValidationReport sut = new PadesValidationReport(validationObjects);
        Assertions.assertNotNull(sut);
    }

    @Test
    public void testAddSignatureValidationReport() {
        PadesValidationReport sut = new PadesValidationReport(validationObjects);

        SignatureValidationReport signatureValidationReport = new SignatureValidationReport(validationObjects,
                signature, "signatureName", TimeTestUtil.TEST_DATE_TIME);
        sut.addSignatureValidationReport(signatureValidationReport);

        // Collection should be returned
        Assertions.assertNotNull(sut.getSignatureValidationReports());

        // Collection should contain at least one element
        Assertions.assertFalse(sut.getSignatureValidationReports().isEmpty());
        // The added signature validation report should be in the collection
        Assertions.assertTrue(sut.getSignatureValidationReports().contains(signatureValidationReport));
    }

    @Test
    public void testAddMultipleSignatureValidationReports() {
        PadesValidationReport sut = new PadesValidationReport(validationObjects);

        SignatureValidationReport signatureValidationReport1 = new SignatureValidationReport(validationObjects,
                signature, "signatureName", TimeTestUtil.TEST_DATE_TIME);
        SignatureValidationReport signatureValidationReport2 = new SignatureValidationReport(validationObjects,
                signature, "signatureName", TimeTestUtil.TEST_DATE_TIME);
        sut.addSignatureValidationReport(signatureValidationReport1);
        sut.addSignatureValidationReport(signatureValidationReport2);

        Assertions.assertTrue(sut.getSignatureValidationReports().contains(signatureValidationReport1));
        Assertions.assertTrue(sut.getSignatureValidationReports().contains(signatureValidationReport2));
    }
}
