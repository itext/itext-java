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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
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
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Class that allows you to validate a single OCSP response.
 */
public class OCSPValidator {
    static final String CERT_IS_EXPIRED = "Certificate is expired on {0}. Its revocation status could have been "
            + "removed from the database, so the OCSP response status could be falsely valid.";
    static final String CERT_IS_REVOKED = "Certificate status is revoked.";
    static final String CERT_STATUS_IS_UNKNOWN = "Certificate status is unknown.";
    static final String INVALID_OCSP = "OCSP response is invalid.";
    static final String ISSUERS_DO_NOT_MATCH = "OCSP: Issuers don't match.";
    static final String ISSUER_MISSING = "Issuer certificate wasn't found.";
    static final String FRESHNESS_CHECK =
            "OCSP response is not fresh enough: " + "this update: {0}, validation date: {1}, freshness: {2}.";
    static final String OCSP_COULD_NOT_BE_VERIFIED = "OCSP response could not be verified: "
            + "it does not contain responder in the certificate chain and response is not signed "
            + "by issuer certificate or any from the trusted store.";
    static final String OCSP_RESPONDER_NOT_RETRIEVED = "OCSP response could not be verified: \" +\n"
            + "            \"Unexpected exception occurred retrieving responder.";
    static final String OCSP_RESPONDER_NOT_VERIFIED = "OCSP response could not be verified: \" +\n"
            + "            \" Unexpected exception occurred while validating responder certificate.";

    static final String OCSP_RESPONDER_DID_NOT_SIGN = "OCSP response could not be verified against this responder.";

    static final String OCSP_RESPONDER_TRUST_NOT_RETRIEVED = "OCSP response could not be verified: \" +\n"
            + "            \"responder trust state could not be retrieved.";
    static final String OCSP_RESPONDER_TRUSTED = "Responder certificate is a trusted certificate.";
    static final String OCSP_RESPONDER_IS_CA = "Responder certificate is the CA certificate.";
    static final String OCSP_IS_NO_LONGER_VALID = "OCSP is no longer valid: {0} after {1}";
    static final String SERIAL_NUMBERS_DO_NOT_MATCH = "OCSP: Serial numbers don't match.";
    static final String UNABLE_TO_CHECK_IF_ISSUERS_MATCH = "OCSP response could not be verified: Unexpected exception"
            + " occurred checking if issuers match.";

    static final String UNABLE_TO_RETRIEVE_ISSUER = "OCSP response could not be verified: Unexpected exception "
            + "occurred while retrieving issuer";

    static final String OCSP_CHECK = "OCSP response check.";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final IssuingCertificateRetriever certificateRetriever;
    private final SignatureValidationProperties properties;
    private final ValidatorChainBuilder builder;

    /**
     * Creates new {@link OCSPValidator} instance.
     *
     * @param builder See {@link  ValidatorChainBuilder}
     */
    protected OCSPValidator(ValidatorChainBuilder builder) {
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.builder = builder;
    }

    /**
     * Validates a certificate against single OCSP Response.
     *
     * @param report                 to store all the chain verification results
     * @param context                the context in which to perform the validation
     * @param certificate            the certificate to check for
     * @param singleResp             single response to check
     * @param ocspResp               basic OCSP response which contains single response to check
     * @param validationDate         validation date to check for
     * @param responseGenerationDate trusted date at which response is generated
     */
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
            ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate, Date responseGenerationDate) {
        ValidationContext localContext = context.setValidatorContext(ValidatorContext.OCSP_VALIDATOR);
        if (CertificateUtil.isSelfSigned(certificate)) {
            report.addReportItem(
                    new CertificateReportItem(certificate, OCSP_CHECK, RevocationDataValidator.SELF_SIGNED_CERTIFICATE,
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
        List<X509Certificate> issuerCerts;
        try {
            issuerCerts = certificateRetriever.retrieveIssuerCertificate(certificate);
        } catch (RuntimeException e) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, UNABLE_TO_RETRIEVE_ISSUER, e,
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        if (issuerCerts.isEmpty()) {
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, MessageFormatUtil.format(
                    ISSUER_MISSING, certificate.getSubjectX500Principal()), ReportItemStatus.INDETERMINATE));
            return;
        }
        ValidationReport[] candidateReports = new ValidationReport[issuerCerts.size()];
        for (int i = 0; i < issuerCerts.size(); i++) {
            candidateReports[i] = new ValidationReport();
            // Check if the issuer of the certID and signCert matches, i.e. check that issuerNameHash and issuerKeyHash
            // fields of the certID is the hash of the issuer's name and public key:
            try {
                if (!CertificateUtil.checkIfIssuersMatch(singleResp.getCertID(), issuerCerts.get(i))) {
                    candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                            ISSUERS_DO_NOT_MATCH, ReportItemStatus.INDETERMINATE));
                    continue;
                }
            } catch (Exception e) {
                candidateReports[i].addReportItem(
                        new CertificateReportItem(certificate, OCSP_CHECK, UNABLE_TO_CHECK_IF_ISSUERS_MATCH, e,
                                ReportItemStatus.INDETERMINATE));
                continue;
            }
            // So, since the issuer name and serial number identify a unique certificate, we found the single response
            // for the provided certificate.

            Duration freshness = properties.getFreshness(localContext);
            // Check that thisUpdate + freshness < validation.
            if (DateTimeUtil.addMillisToDate(singleResp.getThisUpdate(), (long) freshness.toMillis())
                    .before(validationDate)) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                        MessageFormatUtil.format(FRESHNESS_CHECK, singleResp.getThisUpdate(), validationDate,
                                freshness),
                        ReportItemStatus.INDETERMINATE));
                continue;
            }

            // If nextUpdate is not set, the responder is indicating that newer revocation information
            // is available all the time.
            if (singleResp.getNextUpdate() != TimestampConstants.UNDEFINED_TIMESTAMP_DATE && validationDate.after(
                    singleResp.getNextUpdate())) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                        MessageFormatUtil.format(OCSP_IS_NO_LONGER_VALID, validationDate, singleResp.getNextUpdate()),
                        ReportItemStatus.INDETERMINATE));
                continue;
            }

            // Check the status of the certificate:
            ICertificateStatus status = singleResp.getCertStatus();
            IRevokedStatus revokedStatus = BOUNCY_CASTLE_FACTORY.createRevokedStatus(status);
            boolean isStatusGood = BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(status);

            // Check OCSP Archive Cutoff extension in case OCSP response was generated after the certificate is expired.
            if (isStatusGood && certificate.getNotAfter().before(ocspResp.getProducedAt())) {
                Date startExpirationDate = getArchiveCutoffExtension(ocspResp);
                if (TimestampConstants.UNDEFINED_TIMESTAMP_DATE == startExpirationDate || certificate.getNotAfter()
                        .before(startExpirationDate)) {
                    candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                            MessageFormatUtil.format(CERT_IS_EXPIRED, certificate.getNotAfter()),
                            ReportItemStatus.INDETERMINATE));
                    continue;
                }
            }

            if (isStatusGood || (revokedStatus != null && validationDate.before(revokedStatus.getRevocationTime()))) {
                // Check if the OCSP response is genuine.
                verifyOcspResponder(candidateReports[i], localContext, ocspResp, issuerCerts.get(i),
                        responseGenerationDate);
                if (!isStatusGood) {
                    candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                            MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                                    revokedStatus.getRevocationTime()), ReportItemStatus.INFO));
                }
            } else if (revokedStatus != null) {
                candidateReports[i].addReportItem(
                        new CertificateReportItem(certificate, OCSP_CHECK, CERT_IS_REVOKED, ReportItemStatus.INVALID));
            } else {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                        CERT_STATUS_IS_UNKNOWN, ReportItemStatus.INDETERMINATE));
            }
            if (candidateReports[i].getValidationResult() == ValidationResult.VALID) {
                // We found valid issuer, no need to try other ones.
                report.merge(candidateReports[i]);
                return;
            }
        }
        // Valid issuer wasn't found, add all the reports
        for (ValidationReport candidateReport : candidateReports) {
            report.merge(candidateReport);
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
            X509Certificate issuerCert, Date responseGenerationDate) {
        ValidationContext localContext = context.setCertificateSource(CertificateSource.OCSP_ISSUER);
        // OCSP response might be signed by the issuer certificate or
        // the Authorized OCSP responder certificate containing the id-kp-OCSPSigning extended key usage extension.

        // First check if the issuer certificate signed the response since it is expected to be the most common case:
        // the CA will already be validated by the chain validator
        if (CertificateUtil.isSignatureValid(ocspResp, issuerCert)) {
            report.addReportItem(new CertificateReportItem(issuerCert, OCSP_CHECK, OCSP_RESPONDER_IS_CA,
                    ReportItemStatus.INFO));
            return;
        }

        // If the issuer certificate didn't sign the ocsp response, look for authorized ocsp responses
        // from the properties or from the certificate chain received with response.
        Set<Certificate> candidates = SafeCalling.onRuntimeExceptionLog(
                    () -> certificateRetriever.retrieveOCSPResponderByNameCertificate(ocspResp),
                    Collections.<Certificate>emptySet(), report,
                    e -> new CertificateReportItem(issuerCert, OCSP_CHECK, OCSP_RESPONDER_NOT_RETRIEVED, e,
                    ReportItemStatus.INDETERMINATE));

        if (candidates.isEmpty()) {
            report.addReportItem(new CertificateReportItem(issuerCert, OCSP_CHECK, OCSP_COULD_NOT_BE_VERIFIED,
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        ValidationReport[] candidateReports = new ValidationReport[candidates.size()];
        int reportIndex = 0;
        for (Certificate cert : candidates) {
            X509Certificate responderCert = (X509Certificate) cert;
            ValidationReport candidateReport = new ValidationReport();
            candidateReports[reportIndex++] = candidateReport;

            // if the response was not signed by this candidate we can stop further processing
            if (!CertificateUtil.isSignatureValid(ocspResp, responderCert)) {
                candidateReport.addReportItem(new CertificateReportItem(responderCert,
                        OCSP_CHECK, OCSP_RESPONDER_DID_NOT_SIGN, ReportItemStatus.INDETERMINATE));
                continue;
            }

            // if the responder is trusted validation is successful
            try {
                if (certificateRetriever.getTrustedCertificatesStore().isCertificateTrustedForOcsp(responderCert)
                   || certificateRetriever.getTrustedCertificatesStore().isCertificateGenerallyTrusted(responderCert)) {
                    candidateReport.addReportItem(new CertificateReportItem(responderCert,
                            OCSP_CHECK, OCSP_RESPONDER_TRUSTED, ReportItemStatus.INFO));
                    report.merge(candidateReport);
                    return;
                }
            } catch (RuntimeException e) {
                report.addReportItem(new CertificateReportItem(responderCert, OCSP_CHECK,
                        OCSP_RESPONDER_TRUST_NOT_RETRIEVED, e,
                        ReportItemStatus.INDETERMINATE));
                continue;
            }

            // RFC 6960 4.2.2.2. Authorized Responders:
            // "Systems relying on OCSP responses MUST recognize a delegation certificate as being issued
            // by the CA that issued the certificate in question only if the delegation certificate and the
            // certificate being checked for revocation were signed by the same key."
            // and "This certificate MUST be issued directly by the CA that is identified in the request".
            try {
                responderCert.verify(issuerCert.getPublicKey());
            } catch (Exception e) {
                candidateReport.addReportItem(new CertificateReportItem(responderCert, OCSP_CHECK, INVALID_OCSP, e,
                        ReportItemStatus.INVALID));
                continue;
            }

            // Validating of the ocsp signer's certificate (responderCert) described in the
            // RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.
            ValidationReport responderReport = new ValidationReport();
            try {
                builder.getCertificateChainValidator()
                        .validate(responderReport, localContext, responderCert, responseGenerationDate);
            } catch (RuntimeException e) {
                candidateReport.addReportItem(
                        new CertificateReportItem(responderCert, OCSP_CHECK, OCSP_RESPONDER_NOT_VERIFIED, e,
                                ReportItemStatus.INDETERMINATE));
                continue;
            }
            addResponderValidationReport(candidateReport, responderReport);
            if (candidateReport.getValidationResult() == ValidationResult.VALID) {
                addResponderValidationReport(report, candidateReport);
                return;
            }
        }
        //if we get here, none of the candidates were successful
        for (ValidationReport subReport : candidateReports) {
            report.merge(subReport);
        }
    }

    private static void addResponderValidationReport(ValidationReport report, ValidationReport responderReport) {
        for (ReportItem reportItem : responderReport.getLogs()) {
            report.addReportItem(ReportItemStatus.INVALID == reportItem.getStatus() ? reportItem.setStatus(
                    ReportItemStatus.INDETERMINATE) : reportItem);
        }
    }

    private Date getArchiveCutoffExtension(IBasicOCSPResp ocspResp) {
        // OCSP containing this extension specifies the reliable revocation status of the certificate
        // that expired after the date specified in the Archive Cutoff extension or at that date.
        IASN1Encodable archiveCutoff = ocspResp.getExtensionParsedValue(
                BOUNCY_CASTLE_FACTORY.createOCSPObjectIdentifiers().getIdPkixOcspArchiveCutoff());
        if (!archiveCutoff.isNull()) {
            try {
                return BOUNCY_CASTLE_FACTORY.createASN1GeneralizedTime(archiveCutoff).getDate();
            } catch (Exception e) {
                // Ignore exception.
            }
        }
        return (Date) TimestampConstants.UNDEFINED_TIMESTAMP_DATE;
    }
}
