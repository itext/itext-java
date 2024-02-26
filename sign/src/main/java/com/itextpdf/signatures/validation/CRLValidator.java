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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;


/**
 * Class that allows you to validate a certificate against a Certificate Revocation List (CRL) Response.
 */
public class CRLValidator {
    static final String CRL_ISSUER_NOT_FOUND = "Unable to validate CRL response: no issuer certificate found.";
    static final String CRL_ISSUER_NO_COMMON_ROOT =
            "The CRL issuer does not share the root of the inspected certificate.";
    static final String CRL_INVALID = "CRL response is invalid." ;
    static final String CERTIFICATE_REVOKED = "Certificate was revoked by {0} on {1}.";
    static final String UPDATE_DATE_BEFORE_CHECKDATE = "nextUpdate: {0} of CRLResponse is before validation date {1}.";
    static final String CHECK_NAME = "CRLValidator";
    public static final CertificateExtension KEY_USAGE_EXTENSION = new KeyUsageExtension(KeyUsage.CRL_SIGN);


    private IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
    private CertificateChainValidator certificateChainValidator = new CertificateChainValidator();

    /**
     * Instantiated a CRLValidator instance.
     * This class allows you to validate a certificate against a Certificate Revocation List (CRL) Response.
     */
    public CRLValidator() {

    }

    /**
     * Set {@link IssuingCertificateRetriever} to be used for certificate chain building.
     *
     * @param certificateRetriever {@link IssuingCertificateRetriever} to restore certificates chain that can be used
     *                             to verify the signature on the CRL response
     *
     * @return same instance of {@link CRLValidator}.
     */
    public CRLValidator setIssuingCertificateRetriever(IssuingCertificateRetriever certificateRetriever) {
        this.certificateRetriever = certificateRetriever;
        this.certificateChainValidator.setIssuingCertificateRetriever(certificateRetriever);
        return this;
    }

    /**
     * Set {@link CertificateChainValidator} for the CRL issuer certificate.
     *
     * @param validator {@link CertificateChainValidator} to be a validator for the CRL issuer certificate
     *
     * @return same instance of {@link CRLValidator}.
     */
    public CRLValidator setCertificateChainValidator(CertificateChainValidator validator) {
        this.certificateChainValidator = validator;
        return this;
    }


    /**
     * Validates a certificate against Certificate Revocation List (CRL) Responses.
     *
     * @param report           to store all the chain verification results
     * @param certificate      the certificate to check for
     * @param verificationDate verification date to check for
     */
    public void validate(ValidationReport report, X509Certificate certificate, byte[] encodedCrl,
                         Date verificationDate) {
        X509CRL crl;
        try {
            crl = (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(encodedCrl));
        } catch (Exception ignored) {
            // CRL parsing error
            report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, "CRL was incorrectly formatted",
                    ValidationReport.ValidationResult.INDETERMINATE));
            return;
        }

        // Check that the validation date is before the nextUpdate.
        if (crl.getNextUpdate() != null && verificationDate.after(crl.getNextUpdate())) {
            report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, MessageFormatUtil.format(
                    UPDATE_DATE_BEFORE_CHECKDATE, crl.getNextUpdate(), verificationDate),
                    ValidationReport.ValidationResult.INDETERMINATE));
        }
        // Verify the CRL issuer.
        verifyCrlIntegrity(report, certificate, crl);

        // Check the status of the certificate.
        verifyRevocation(report, certificate, verificationDate, crl);
    }

    private static void verifyRevocation(ValidationReport report, X509Certificate certificate,
                                         Date verificationDate, X509CRL crl) {
        X509CRLEntry revocation = crl.getRevokedCertificate(certificate.getSerialNumber());
        if (revocation != null) {
            Date revocationDate = revocation.getRevocationDate();
            if (verificationDate.before(revocationDate)) {
                report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, MessageFormatUtil.format(
                        SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED, revocationDate),
                        ValidationReport.ValidationResult.VALID));
            } else {
                report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, MessageFormatUtil.format(
                        CERTIFICATE_REVOKED, crl.getIssuerX500Principal(), revocation.getRevocationDate()),
                        ValidationReport.ValidationResult.INVALID));
            }
        }
    }

    private void verifyCrlIntegrity(ValidationReport report, X509Certificate certificate, X509CRL crl) {
        Certificate[] certs = certificateRetriever.getCrlIssuerCertificates(crl);
        if (certs.length == 0) {
            report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, CRL_ISSUER_NOT_FOUND,
                    ValidationReport.ValidationResult.INDETERMINATE));
            return;
        }
        Certificate crlIssuer = certs[0];
        Certificate crlIssuerRoot = getRoot(crlIssuer);
        Certificate subjectRoot = getRoot(certificate);
        if (!crlIssuerRoot.equals(subjectRoot)) {
            report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, CRL_ISSUER_NO_COMMON_ROOT,
                    ValidationReport.ValidationResult.INDETERMINATE));
        }
        try {
            crl.verify(crlIssuer.getPublicKey());
        } catch (Exception e) {
            report.addReportItem( new CertificateReportItem(certificate, CHECK_NAME, CRL_INVALID, e,
                    ValidationReport.ValidationResult.INDETERMINATE));
            return;
        }
        // ideally this data should be the date this response was retrieved from the server.
        Date crlIssuerDate;
        if ( null != crl.getNextUpdate()) {
            crlIssuerDate = crl.getNextUpdate();
            report.addReportItem( new CertificateReportItem((X509Certificate) crlIssuer, CHECK_NAME,
                    "Using crl nextUpdate date as validation date", ValidationReport.ValidationResult.VALID));
        } else {
            crlIssuerDate = crl.getThisUpdate();
            report.addReportItem( new CertificateReportItem((X509Certificate) crlIssuer, CHECK_NAME,
                    "Using crl thisUpdate date as validation date", ValidationReport.ValidationResult.VALID));
        }

        certificateChainValidator.validate(report, (X509Certificate) crlIssuer, crlIssuerDate ,
                Collections.singletonList(KEY_USAGE_EXTENSION));
    }

    private Certificate getRoot(Certificate cert) {
        Certificate[] chain = certificateRetriever.retrieveMissingCertificates(new Certificate[]{cert});
        return chain[chain.length-1];
    }
}
