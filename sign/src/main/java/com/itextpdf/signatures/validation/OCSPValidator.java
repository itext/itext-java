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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.ExtendedKeyUsageExtension;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

/**
 * Class that allows you to validate a single OCSP response.
 */
public class OCSPValidator {
    static final String CERT_IS_REVOKED = "Certificate status is revoked.";
    static final String CERT_STATUS_IS_UNKNOWN = "Certificate status is unknown.";
    static final String INVALID_OCSP = "OCSP response is invalid.";
    static final String ISSUERS_DOES_NOT_MATCH = "OCSP: Issuers doesn't match.";
    static final String NO_USABLE_OCSP_WAS_FOUND = "No usable OCSP response was found.";
    static final String OCSP_COULD_NOT_BE_VERIFIED = "OCSP response could not be verified: " +
            "it does not contain responder in the certificate chain and response is not signed " +
            "by issuer certificate or any from the trusted store.";
    static final String OCSP_IS_NO_LONGER_VALID = "OCSP is no longer valid: {0} after {1}";

    static final String OCSP_CHECK = "OCSP response check.";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
    private CertificateChainValidator certificateChainValidator = new CertificateChainValidator();

    /**
     * Creates new {@link OCSPValidator} instance.
     */
    public OCSPValidator() {
        // Empty constructor.
    }

    /**
     * Set {@link IssuingCertificateRetriever} to be used for certificate chain building.
     *
     * @param certificateRetriever {@link IssuingCertificateRetriever} to restore certificates chain that can be used
     *                             to verify the signature on the OCSP response
     *
     * @return same instance of {@link OCSPValidator}.
     */
    public OCSPValidator setIssuingCertificateRetriever(IssuingCertificateRetriever certificateRetriever) {
        this.certificateRetriever = certificateRetriever;
        this.certificateChainValidator.setIssuingCertificateRetriever(certificateRetriever);
        return this;
    }

    /**
     * Set {@link CertificateChainValidator} for the OCSP responder certificate.
     *
     * @param validator {@link CertificateChainValidator} to be a validator for the OCSP responder certificate
     *
     * @return same instance of {@link OCSPValidator}.
     */
    public OCSPValidator setCertificateChainValidator(CertificateChainValidator validator) {
        this.certificateChainValidator = validator;
        return this;
    }

    /**
     * Validates a certificate against OCSP Response.
     *
     * @param report           to store all the chain verification results
     * @param certificate      the certificate to check for
     * @param ocspResp         basic OCSP response to check
     * @param verificationDate verification date to check for
     */
    public void validate(ValidationReport report, X509Certificate certificate, IBasicOCSPResp ocspResp,
                         Date verificationDate) {
        if (ocspResp == null) {
            return;
        }
        // Getting the responses.
        ISingleResp[] resp = ocspResp.getResponses();
        boolean workingOcspAvailable = false;
        for (ISingleResp iSingleResp : resp) {
            // SingleResp contains the basic information of the status of the certificate identified by the certID.
            // Check if the serial numbers of the signCert and certID corresponds:
            if (!certificate.getSerialNumber().equals(iSingleResp.getCertID().getSerialNumber())) {
                continue;
            }
            Certificate issuerCert = certificateRetriever.retrieveIssuerCertificate(certificate);
            // Check if the issuer of the certID and signCert matches, i.e. check that issuerNameHash and issuerKeyHash
            // fields of the certID is the hash of the issuer's name and public key:
            if (issuerCert == null) {
                issuerCert = certificate;
            }
            try {
                if (!CertificateUtil.checkIfIssuersMatch(iSingleResp.getCertID(), (X509Certificate) issuerCert)) {
                    report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, ISSUERS_DOES_NOT_MATCH,
                            ValidationReport.ValidationResult.VALID));
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            // So, since the issuer name and serial number identify a unique certificate, we found the single response
            // for the signCert.

            // TODO DEVSIX-8176 Implement RevocationDataValidator class: thisUpdate >= (verificationDate - freshness)

            // If nextUpdate is not set, the responder is indicating that newer revocation information
            // is available all the time.
            if (iSingleResp.getNextUpdate() != null && verificationDate.after(iSingleResp.getNextUpdate())) {
                report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                        MessageFormatUtil.format(OCSP_IS_NO_LONGER_VALID, verificationDate,
                                iSingleResp.getNextUpdate()), ValidationReport.ValidationResult.VALID));
                continue;
            }

            workingOcspAvailable = true;

            // Check the status of the certificate:
            ICertificateStatus status = iSingleResp.getCertStatus();
            IRevokedStatus revokedStatus = BOUNCY_CASTLE_FACTORY.createRevokedStatus(status);
            boolean isStatusGood = BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood().equals(status);
            if (isStatusGood || (revokedStatus != null && verificationDate.before(revokedStatus.getRevocationTime()))) {
                // Check if the OCSP response is genuine.
                verifyOcspResponder(report, ocspResp, (X509Certificate) issuerCert);
                if (!isStatusGood) {
                    report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK,
                            MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                                    revokedStatus.getRevocationTime()), ValidationReport.ValidationResult.VALID));
                }
            } else if (revokedStatus != null) {
                report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, CERT_IS_REVOKED,
                        ValidationReport.ValidationResult.INVALID));
            } else {
                report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, CERT_STATUS_IS_UNKNOWN,
                        ValidationReport.ValidationResult.INDETERMINATE));
            }
        }
        if (!workingOcspAvailable) {
            // TODO DEVSIX-8176 Implement RevocationDataValidator class: crls could be valid, it shouldn't be a failure.
            report.addReportItem(new CertificateReportItem(certificate, OCSP_CHECK, NO_USABLE_OCSP_WAS_FOUND,
                    ValidationReport.ValidationResult.INDETERMINATE));
        }
    }

    /**
     * Verifies if an OCSP response is genuine.
     * If it doesn't verify against the issuer certificate and response's certificates, it may verify
     * using a trusted anchor or cert.
     *
     * @param report     to store all the chain verification results
     * @param ocspResp   {@link IBasicOCSPResp} the OCSP response wrapper
     * @param issuerCert the issuer of the certificate for which the OCSP is checked
     */
    private void verifyOcspResponder(ValidationReport report, IBasicOCSPResp ocspResp,
                                     X509Certificate issuerCert) {
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
                        ValidationReport.ValidationResult.INDETERMINATE));
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
                            ValidationReport.ValidationResult.INVALID));
                    return;
                }

                // Validating of the ocsp signer's certificate (responderCert) described in the
                // RFC6960 4.2.2.2.1. Revocation Checking of an Authorized Responder.
            }
        }

        certificateChainValidator.validate(report, responderCert, ocspResp.getProducedAt(),
                Collections.singletonList((CertificateExtension) new ExtendedKeyUsageExtension(
                        Collections.singletonList(ExtendedKeyUsageExtension.OCSP_SIGNING))));
    }
}
