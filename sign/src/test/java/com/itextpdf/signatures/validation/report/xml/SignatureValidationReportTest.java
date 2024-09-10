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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;

@Tag("BouncyCastleIntegrationTest")
public class SignatureValidationReportTest extends AbstractIdentifiableObjectTest {

    private static CMSContainer signature1;
    private static CMSContainer signature2;
    private final ValidationObjects validationObjects = new ValidationObjects();

    @BeforeAll
    public static void setupFixture() throws CertificateException, IOException, CRLException {
        signature1 = new CMSContainer(Base64.decode(XmlReportTestHelper.SIGNATURE1_BASE64));
        signature2 = new CMSContainer(Base64.decode(XmlReportTestHelper.SIGNATURE2_BASE64));
    }

    @Test
    public void testCreation() {
        SignatureValidationReport sut = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertNotNull(sut);
    }

    @Test
    public void testSetSignatureValidationStatus() {
        SignatureValidationReport sut = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);

        SignatureValidationStatus status = new SignatureValidationStatus();
        sut.setSignatureValidationStatus(status);

        Assertions.assertEquals(status, sut.getSignatureValidationStatus());
    }

    @Override
    protected void performTestHashForEqualInstances() {
        SignatureValidationReport sut1 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        SignatureValidationReport sut2 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Override
    protected void performTestEqualsForEqualInstances() {
        SignatureValidationReport sut1 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        SignatureValidationReport sut2 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertEquals(sut1, sut2);
    }

    @Override
    protected void performTestEqualsForDifferentInstances() {
        SignatureValidationReport sut1 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        SignatureValidationReport sut2 = new SignatureValidationReport(validationObjects, signature2, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertNotEquals(sut1, sut2);
    }

    @Override
    protected void performTestHashForDifferentInstances() {
        SignatureValidationReport sut1 = new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        SignatureValidationReport sut2 = new SignatureValidationReport(validationObjects, signature2, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
        Assertions.assertNotEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Override
    protected AbstractIdentifiableObject getIdentifiableObjectUnderTest() {
        return new SignatureValidationReport(validationObjects, signature1, "signatureName",
                TimeTestUtil.TEST_DATE_TIME);
    }
}
