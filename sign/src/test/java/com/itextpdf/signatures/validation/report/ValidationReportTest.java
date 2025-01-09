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
package com.itextpdf.signatures.validation.report;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Tag("BouncyCastleUnitTest")
public class ValidationReportTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    @Test
    public void getValidationResultWithNoLogsShouldBeValid() {
        ValidationReport sut = new ValidationReport();
        Assertions.assertEquals(ValidationReport.ValidationResult.VALID, sut.getValidationResult());
    }

    @Test
    public void getValidationResultWithOnlyValidLogsShouldBeValid() {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        sut.addReportItem(new ReportItem("test2", "test2", ReportItem.ReportItemStatus.INFO));
        sut.addReportItem(new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INFO));
        Assertions.assertEquals(ValidationReport.ValidationResult.VALID, sut.getValidationResult());
    }

    @Test
    public void getValidationResultWithValidAndIndeterminateLogsShouldBeIndeterminate() {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        sut.addReportItem(new ReportItem("test2", "test2", ReportItem.ReportItemStatus.INDETERMINATE));
        sut.addReportItem(new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INFO));
        Assertions.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, sut.getValidationResult());
    }

    @Test
    public void getValidationResultWithInvalidLogsShouldBeInvalid() {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        sut.addReportItem(new ReportItem("test2", "test2", ReportItem.ReportItemStatus.INVALID));
        sut.addReportItem(new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INDETERMINATE));
        Assertions.assertEquals(ValidationReport.ValidationResult.INVALID, sut.getValidationResult());
    }

    @Test
    public void testGetFailures() {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        ReportItem failure1 = new ReportItem("test2", "test2", ReportItem.ReportItemStatus.INVALID);
        sut.addReportItem(failure1);
        ReportItem failure2 = new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INDETERMINATE);
        sut.addReportItem(failure2);
        Assertions.assertTrue(sut.getFailures().contains(failure1));
        Assertions.assertTrue(sut.getFailures().contains(failure2));
        Assertions.assertEquals(2, sut.getFailures().size());
    }

    @Test
    public void getCertificateFailuresTest() throws CertificateException, IOException {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        X509Certificate cert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.pem")[0];
        CertificateReportItem failure1 = new CertificateReportItem(cert, "test2", "test2", ReportItem.ReportItemStatus.INVALID);
        sut.addReportItem(failure1);
        ReportItem failure2 = new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INDETERMINATE);
        sut.addReportItem(failure2);
        Assertions.assertTrue(sut.getCertificateFailures().contains(failure1));
        Assertions.assertEquals(1, sut.getCertificateFailures().size());
    }

    @Test
    public void getLogsTest() throws CertificateException, IOException {
        ValidationReport sut = new ValidationReport();
        ReportItem item1 = new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO);
        sut.addReportItem(item1);
        X509Certificate cert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.pem")[0];
        CertificateReportItem failure1 = new CertificateReportItem(cert, "test2", "test2", ReportItem.ReportItemStatus.INVALID);
        sut.addReportItem(failure1);
        ReportItem failure2 = new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INDETERMINATE);
        sut.addReportItem(failure2);
        Assertions.assertEquals(item1, sut.getLogs().get(0));
        Assertions.assertEquals(failure1, sut.getLogs().get(1));
        Assertions.assertEquals(failure2, sut.getLogs().get(2));
        Assertions.assertEquals(3, sut.getLogs().size());
    }

    @Test
    public void getCertificateLogsTest() throws CertificateException, IOException {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1", "test1", ReportItem.ReportItemStatus.INFO));
        X509Certificate cert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.pem")[0];
        CertificateReportItem failure1 = new CertificateReportItem(cert, "test2", "test2", ReportItem.ReportItemStatus.INVALID);
        sut.addReportItem(failure1);
        ReportItem failure2 = new ReportItem("test3", "test3", ReportItem.ReportItemStatus.INDETERMINATE);
        sut.addReportItem(failure2);
        Assertions.assertTrue(sut.getCertificateLogs().contains(failure1));
        Assertions.assertEquals(1, sut.getCertificateLogs().size());
    }

    @Test
    public void toStringTest() throws CertificateException, IOException {
        ValidationReport sut = new ValidationReport();
        sut.addReportItem(new ReportItem("test1check", "test1message", ReportItem.ReportItemStatus.INFO));
        X509Certificate cert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.pem")[0];
        CertificateReportItem failure1 = new CertificateReportItem(cert, "test2check", "test2message", ReportItem.ReportItemStatus.INVALID);
        sut.addReportItem(failure1);
        ReportItem failure2 = new ReportItem("test3check", "test3message", ReportItem.ReportItemStatus.INDETERMINATE);
        sut.addReportItem(failure2);

        Assertions.assertTrue(sut.toString().contains("INVALID"));
        Assertions.assertTrue(sut.toString().contains("test1check"));
        Assertions.assertTrue(sut.toString().contains("test1message"));
        Assertions.assertTrue(sut.toString().contains("test2check"));
        Assertions.assertTrue(sut.toString().contains("test2message"));
        Assertions.assertTrue(sut.toString().contains("test3check"));
    }
}