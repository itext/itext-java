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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;

import static com.itextpdf.signatures.validation.v1.RevocationDataValidator.SELF_SIGNED_CERTIFICATE;

/**
 * Class that allows you to validate a single OCSP response.
 */
public class OCSPValidator {
    static final String CERT_IS_REVOKED = "Certificate status is revoked.";
    static final String CERT_STATUS_IS_UNKNOWN = "Certificate status is unknown.";
    static final String INVALID_OCSP = "OCSP response is invalid.";
    static final String ISSUERS_DO_NOT_MATCH = "OCSP: Issuers don't match.";
    static final String FRESHNESS_CHECK = "OCSP response is not fresh enough: " +
            "this update: {0}, validation date: {1}, freshness: {2}.";
    static final String OCSP_COULD_NOT_BE_VERIFIED = "OCSP response could not be verified: " +
            "it does not contain responder in the certificate chain and response is not signed " +
            "by issuer certificate or any from the trusted store.";
    static final String OCSP_IS_NO_LONGER_VALID = "OCSP is no longer valid: {0} after {1}";
    static final String SERIAL_NUMBERS_DO_NOT_MATCH = "OCSP: Serial numbers don't match.";
    static final String UNABLE_TO_CHECK_IF_ISSUERS_MATCH = "OCSP response could not be verified: unable to check" +
            " if issuers match.";

    static final String OCSP_CHECK = "OCSP response check.";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final IssuingCertificateRetriever certificateRetriever;
    private final SignatureValidationProperties properties;
    private final ValidatorChainBuilder builder;

    /**
     * Creates new {@link OCSPValidator} instance.

     * @param builder See {@link  ValidatorChainBuilder}
     */
    OCSPValidator(ValidatorChainBuilder builder) {
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.builder = builder;
    }

    /**
     * Validates a certificate against single OCSP Response.
     *
     * @param report         to store all the chain verification results
     * @param context        the context in which to perform the validation
     * @param certificate    the certificate to check for
     * @param singleResp     single response to check
     * @param ocspResp       basic OCSP response which contains single response to check
     * @param validationDate validation date to check for
     */
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
            ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate) {
        ValidationContext localContext = context.setValidatorContext(ValidatorContext.OCSP_VALIDATOR);
        if (CertificateUtil.isSelfSigned(certificate)) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, SELF_SIGNED_CERTIFICATE,
                    ReportItemStatus.INFO));
            return;
        }
        // SingleResp contains the basic information of the status of the certificate identified by the certID.
        // Check if the serial numbers of the signCert and certID corresponds:
        if (!certificate.getSerialNumber().equals(singleResp.getCertID().getSerialNumber())) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, SERIAL_NUMBERS_DO_NOT_MATCH,
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        Certificate issuerCert = certificateRetriever.retrieveIssuerCertificate(certificate);
        // Check if the issuer of the certID and signCert matches, i.e. check that issuerNameHash and issuerKeyHash
        // fields of the certID is the hash of the issuer's name and public key:
        try {
            if (!CertificateUtil.checkIfIssuersMatch(singleResp.getCertID(), (X509Certificate) issuerCert)) {
                report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, ISSUERS_DO_NOT_MATCH,
                        ReportItemStatus.INDETERMINATE));
                return;
            }
        } catch (Exception e) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, UNABLE_TO_CHECK_IF_ISSUERS_MATCH,
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        // So, since the issuer name and serial number identify a unique certificate, we found the single response
        // for the provided certificate.

        // Check that thisUpdate >= (validationDate - freshness).
        Duration freshness = properties.getFreshness(localContext);
        if (singleResp.getThisUpdate().before(
                DateTimeUtil.addMillisToDate(validationDate, -(long)freshness.toMillis()))) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                    MessageFormatUtil.format(FRESHNESS_CHECK, singleResp.getThisUpdate(), validationDate,
                            freshness), ReportItemStatus.INDETERMINATE));
            return;
        }

        // If nextUpdate is not set, the responder is indicating that newer revocation information
        // is available all the time.
        if (singleResp.getNextUpdate() != TimestampConstants.UNDEFINED_TIMESTAMP_DATE &&
                validationDate.after(singleResp.getNextUpdate())) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                    MessageFormatUtil.format(OCSP_IS_NO_LONGER_VALID, validationDate,
                            singleResp.getNextUpdate()), ReportItemStatus.INDETERMINATE));
            return;
        }

        // Check the status of the certificate:
        ICertificateStatus status = singleResp.getCertStatus();
        IRevokedStatus revokedStatus = BOUNCY_CASTLE_FACTORY.createRevokedStatus(status);
        boolean isStatusGood = BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(status);
        if (isStatusGood || (revokedStatus != null && validationDate.before(revokedStatus.getRevocationTime()))) {
            // Check if the OCSP response is genuine.
            verifyOcspResponder(report, localContext, ocspResp, (X509Certificate) issuerCert);
            if (!isStatusGood) {
                report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                        MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                                revokedStatus.getRevocationTime()), ReportItemStatus.INFO));
            }
        } else if (revokedStatus != null) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, CERT_IS_REVOKED,
                    ReportItemStatus.INVALID));
        } else {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, CERT_STATUS_IS_UNKNOWN,
                    ReportItemStatus.INDETERMINATE));
        }
    }

    /**
     * Verifies if an OCSP response is genuine.
     * If it doesn't verify against the issuer certificate and response's certificates, it may verify
     * using a trusted anchor or cert.
     *
     * @param report     to store all the chain verification results
     * @param context    the context in which to perform the validation
     * @param ocspResp   {@link IBasicOCSPResp} the OCSP response wrapper
     * @param issuerCert the issuer of the certificate for which the OCSP is checked
     */
    private void verifyOcspResponder(ValidationReport report, ValidationContext context, IBasicOCSPResp ocspResp,
            X509Certificate issuerCert) {
        ValidationContext localContext = context.setCertificateSource(CertificateSource.OCSP_ISSUER);
        ValidationReport responderReport = new ValidationReport();

        // OCSP response might be signed by the issuer certificate or
        // the Authorized OCSP responder certificate containing the id-kp-OCSPSigning extended key usage extension.
        X509Certificate responderCert = null;

        // First check if the issuer certificate signed the response since it is expected to be the most common case:
        if (CertificateUtil.isSignatureValid(ocspResp, issuerCert)) {
            responderCert = issuerCert;
        }

        // If the issuer certificate didn't sign the ocsp response, look for authorized ocsp responses
        // from the properties or from the certificate chain received with response.
        if (responderCert == null) {
            responderCert = (X509Certificate) certificateRetriever.retrieveOCSPResponderCertificate(ocspResp);
            if (responderCert == null) {
                report.addReportItem(new CertificateReportItem(issuerCert, OCSP_CHECK, OCSP_COULD_NOT_BE_VERIFIED,
                        ReportItemStatus.INDETERMINATE));
                return;
            }
            if (!certificateRetriever.isCertificateTrusted(responderCert)) {
                // RFC 6960 4.2.2.2. Authorized Responders:
                // "Systems relying on OCSP responses MUST recognize a delegation certificate as being issued
                // by the CA that issued the certificate in question only if the delegation certificate and the
                // certificate being checked for revocation were signed by the same key."
                // and "This certificate MUST be issued directly by the CA that is identified in the request".
                try {
                    responderCert.verify(issuerCert.getPublicKey());
                } catch (Exception e) {
                    report.addReportItem(new CertificateReportItem(responderCert, OCSP_CHECK, INVALID_OCSP, e,
                            ReportItemStatus.INVALID));
                    return;
                }

                // Validating of the ocsp signer's certificate (responderCert) described in the
                // RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.
                builder.getCertificateChainValidator().validate(responderReport,
                        localContext,
                        responderCert, ocspResp.getProducedAt());
                addResponderValidationReport(report, responderReport);
                return;
            }
        }
        builder.getCertificateChainValidator().validate(responderReport,
                localContext.setCertificateSource(CertificateSource.TRUSTED),
                responderCert, ocspResp.getProducedAt());
        addResponderValidationReport(report, responderReport);
    }

    private void addResponderValidationReport(ValidationReport report, ValidationReport responderReport) {
        for (ReportItem reportItem : responderReport.getLogs()) {
            report.addReportItem(ReportItemStatus.INVALID == reportItem.getStatus() ?
                    reportItem.setStatus(ReportItemStatus.INDETERMINATE) : reportItem);
        }
    }
}
