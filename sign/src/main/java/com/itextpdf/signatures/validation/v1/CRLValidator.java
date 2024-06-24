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
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IIssuingDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IReasonFlags;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.extensions.DynamicBasicConstraintsExtension;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CRLReason;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.itextpdf.signatures.validation.v1.SafeCalling.onExceptionLog;

/**
 * Class that allows you to validate a certificate against a Certificate Revocation List (CRL) Response.
 */
public class CRLValidator {
    static final String CRL_CHECK = "CRL response check.";

    static final String ATTRIBUTE_CERTS_ASSERTED = "The onlyContainsAttributeCerts is asserted. Conforming CRLs " +
            "issuers MUST set the onlyContainsAttributeCerts boolean to FALSE.";
    static final String CERTIFICATE_IS_EXPIRED =
            "Certificate is expired on {0} and could have been removed from the CRL.";
    static final String CERTIFICATE_IS_UNREVOKED = "The certificate was unrevoked.";
    static final String CERTIFICATE_IS_NOT_IN_THE_CRL_SCOPE = "Certificate isn't in the current CRL scope.";
    static final String CERTIFICATE_REVOKED = "Certificate was revoked by {0} on {1}.";
    static final String CRL_ISSUER_NOT_FOUND = "Unable to validate CRL response: no issuer certificate found.";
    static final String CRL_ISSUER_REQUEST_FAILED =
            "Unable to validate CRL response: Unexpected exception occurred retrieving issuer certificate.";
    static final String CRL_ISSUER_CHAIN_FAILED =
            "Unable to validate CRL response: Unexpected exception occurred validating issuer certificate.";

    static final String CRL_ISSUER_NO_COMMON_ROOT =
            "The CRL issuer does not share the root of the inspected certificate.";
    static final String CRL_INVALID = "CRL response is invalid.";
    static final String FRESHNESS_CHECK = "CRL response is not fresh enough: " +
            "this update: {0}, validation date: {1}, freshness: {2}.";
    static final String ONLY_SOME_REASONS_CHECKED = "Revocation status cannot be determined since " +
            "not all reason codes are covered by the current CRL.";
    static final String SAME_REASONS_CHECK = "CRLs that cover the same reason codes were already verified.";
    static final String UPDATE_DATE_BEFORE_CHECK_DATE = "nextUpdate: {0} of CRLResponse is before validation date {1}.";

    // All reasons without unspecified.
    static final int ALL_REASONS = 32895;

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final Map<Certificate, Integer> checkedReasonsMask = new HashMap<>();
    private final IssuingCertificateRetriever certificateRetriever;
    private final SignatureValidationProperties properties;
    private final ValidatorChainBuilder builder;

    /**
     * Creates new {@link CRLValidator} instance.
     *
     * @param builder See {@link  ValidatorChainBuilder}
     */
    protected CRLValidator(ValidatorChainBuilder builder) {
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.builder = builder;
    }

    /**
     * Validates a certificate against Certificate Revocation List (CRL) Responses.
     *
     * @param report         to store all the chain verification results
     * @param context        the context in which to perform the validation
     * @param certificate    the certificate to check against CRL response
     * @param crl            the crl response to be validated
     * @param validationDate validation date to check for
     *
     * @deprecated starting from 8.0.5. TODO DEVSIX-8398 To be removed.
     */
    @Deprecated
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate, X509CRL crl,
            Date validationDate) {
        validate(report, context, certificate, crl, validationDate, DateTimeUtil.getCurrentTimeDate());
    }

    /**
     * Validates a certificate against Certificate Revocation List (CRL) Responses.
     *
     * @param report                 to store all the chain verification results
     * @param context                the context in which to perform the validation
     * @param certificate            the certificate to check against CRL response
     * @param crl                    the crl response to be validated
     * @param validationDate         validation date to check for
     * @param responseGenerationDate trusted date at which response is generated
     */
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate, X509CRL crl,
            Date validationDate, Date responseGenerationDate) {
        ValidationContext localContext = context.setValidatorContext(ValidatorContext.CRL_VALIDATOR);
        if (CertificateUtil.isSelfSigned(certificate)) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK,
                    RevocationDataValidator.SELF_SIGNED_CERTIFICATE, ReportItemStatus.INFO));
            return;
        }
        Duration freshness = properties.getFreshness(localContext);
        // Check that thisUpdate + freshness < validation.
        if (DateTimeUtil.addMillisToDate(crl.getThisUpdate(), (long) freshness.toMillis()).before(validationDate)) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK,
                    MessageFormatUtil.format(FRESHNESS_CHECK, crl.getThisUpdate(), validationDate, freshness),
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        // Check that the validation date is before the nextUpdate.
        if (crl.getNextUpdate() != TimestampConstants.UNDEFINED_TIMESTAMP_DATE &&
                validationDate.after(crl.getNextUpdate())) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, MessageFormatUtil.format(
                    UPDATE_DATE_BEFORE_CHECK_DATE, crl.getNextUpdate(), validationDate),
                    ReportItemStatus.INDETERMINATE));
            return;
        }

        // Check expiredCertOnCrl extension in case the certificate is expired.
        if (certificate.getNotAfter().before(crl.getThisUpdate())) {
            Date startExpirationDate = getExpiredCertsOnCRLExtensionDate(crl);
            if (TimestampConstants.UNDEFINED_TIMESTAMP_DATE == startExpirationDate ||
                    certificate.getNotAfter().before(startExpirationDate)) {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_IS_EXPIRED, certificate.getNotAfter()), ReportItemStatus.INDETERMINATE));
                return;
            }
        }

        IIssuingDistributionPoint issuingDistPoint = getIssuingDistributionPointExtension(crl);
        IDistributionPoint distributionPoint = null;
        if (!issuingDistPoint.isNull()) {
            // Verify that certificate is in the CRL scope using IDP extension.
            boolean basicConstraintsCaAsserted = new DynamicBasicConstraintsExtension().withCertificateChainSize(1)
                    .existsInCertificate(certificate);
            if ((issuingDistPoint.onlyContainsUserCerts() && basicConstraintsCaAsserted) ||
                    (issuingDistPoint.onlyContainsCACerts() && !basicConstraintsCaAsserted)) {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK,
                        CERTIFICATE_IS_NOT_IN_THE_CRL_SCOPE, ReportItemStatus.INDETERMINATE));
                return;
            }
            if (issuingDistPoint.onlyContainsAttributeCerts()) {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK,
                        ATTRIBUTE_CERTS_ASSERTED, ReportItemStatus.INDETERMINATE));
                return;
            }
            // Try to retrieve corresponding DP from the certificate by name specified in the IDP.
            if (!issuingDistPoint.getDistributionPoint().isNull()) {
                distributionPoint = CertificateUtil.getDistributionPointByName(certificate,
                        issuingDistPoint.getDistributionPoint());
            }
        }

        int interimReasonsMask = computeInterimReasonsMask(issuingDistPoint, distributionPoint);
        Integer reasonsMask = checkedReasonsMask.get(certificate);
        if (reasonsMask != null) {
            interimReasonsMask |= (int) reasonsMask;
        }

        // Verify the CRL issuer.
        verifyCrlIntegrity(report, localContext, certificate, crl, responseGenerationDate);

        // Check the status of the certificate.
        verifyRevocation(report, certificate, validationDate, crl);

        if (report.getValidationResult() == ValidationReport.ValidationResult.VALID) {
            checkedReasonsMask.put(certificate, interimReasonsMask);
        }

        // If ((reasons_mask is all-reasons) OR (cert_status is not UNREVOKED)),
        // then the revocation status has been determined.
        if (interimReasonsMask != ALL_REASONS) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK,
                    ONLY_SOME_REASONS_CHECKED, ReportItemStatus.INDETERMINATE));
        }
    }

    private static void verifyRevocation(ValidationReport report, X509Certificate certificate,
            Date verificationDate, X509CRL crl) {
        X509CRLEntry revocation = crl.getRevokedCertificate(certificate.getSerialNumber());
        if (revocation != null) {
            Date revocationDate = revocation.getRevocationDate();
            if (verificationDate.before(revocationDate)) {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, MessageFormatUtil.format(
                        SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED, revocationDate),
                        ReportItemStatus.INFO));
            } else if (CRLReason.REMOVE_FROM_CRL == revocation.getRevocationReason()) {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_IS_UNREVOKED, revocationDate),
                        ReportItemStatus.INFO));
            } else {
                report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, MessageFormatUtil.format(
                        CERTIFICATE_REVOKED, crl.getIssuerX500Principal(), revocation.getRevocationDate()),
                        ReportItemStatus.INVALID));
            }
        }
    }

    private static IIssuingDistributionPoint getIssuingDistributionPointExtension(X509CRL crl) {
        IASN1Primitive issuingDistPointExtension = null;
        try {
            issuingDistPointExtension = CertificateUtil.getExtensionValue(crl,
                    FACTORY.createExtension().getIssuingDistributionPoint().getId());
        } catch (IOException | RuntimeException e) {
            // Ignore exception.
        }
        return FACTORY.createIssuingDistributionPoint(issuingDistPointExtension);
    }

    private static Date getExpiredCertsOnCRLExtensionDate(X509CRL crl) {
        IASN1Encodable expiredCertsOnCRL = null;
        try {
            // The scope of a CRL containing this extension is extended to include the revocation status of the
            // certificates that expired after the date specified in ExpiredCertsOnCRL or at that date.
            expiredCertsOnCRL = CertificateUtil.getExtensionValue(crl,
                    FACTORY.createExtension().getExpiredCertsOnCRL().getId());
        } catch (IOException | RuntimeException e) {
            // Ignore exception.
        }
        if (expiredCertsOnCRL != null) {
            try {
                return FACTORY.createASN1GeneralizedTime(expiredCertsOnCRL).getDate();
            } catch (Exception e) {
                // Ignore exception.
            }
        }
        return (Date) TimestampConstants.UNDEFINED_TIMESTAMP_DATE;
    }

    private static int computeInterimReasonsMask(IIssuingDistributionPoint issuingDistPoint,
            IDistributionPoint distributionPoint) {
        int interimReasonsMask = ALL_REASONS;
        if (!issuingDistPoint.isNull()) {
            IReasonFlags onlySomeReasons = issuingDistPoint.getOnlySomeReasons();
            if (!onlySomeReasons.isNull()) {
                interimReasonsMask &= onlySomeReasons.intValue();
            }
        }
        if (distributionPoint != null) {
            IReasonFlags reasons = distributionPoint.getReasons();
            if (!reasons.isNull()) {
                interimReasonsMask &= reasons.intValue();
            }
        }
        return interimReasonsMask;
    }

    private void verifyCrlIntegrity(ValidationReport report, ValidationContext context, X509Certificate certificate,
            X509CRL crl, Date responseGenerationDate) {
        Certificate[] certs = null;
        try {
            certs = certificateRetriever.getCrlIssuerCertificates(crl);
        } catch (RuntimeException e) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, CRL_ISSUER_REQUEST_FAILED, e,
                    ReportItemStatus.INDETERMINATE));
            return;
        }

        if (certs == null || certs.length == 0) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, CRL_ISSUER_NOT_FOUND,
                    ReportItemStatus.INDETERMINATE));
            return;
        }

        Certificate crlIssuer = certs[0];
        Certificate crlIssuerRoot = getRoot(crlIssuer);
        Certificate subjectRoot = getRoot(certificate);
        if (!crlIssuerRoot.equals(subjectRoot)) {
            report.addReportItem(new CertificateReportItem(certificate, CRL_CHECK, CRL_ISSUER_NO_COMMON_ROOT,
                    ReportItemStatus.INDETERMINATE));
            return;
        }
        onExceptionLog(() -> crl.verify(crlIssuer.getPublicKey()), report,
                e -> new CertificateReportItem(certificate, CRL_CHECK, CRL_INVALID, e, ReportItemStatus.INDETERMINATE));
        ValidationReport responderReport = new ValidationReport();
        onExceptionLog(() -> builder.getCertificateChainValidator().validate(responderReport,
                context.setCertificateSource(CertificateSource.CRL_ISSUER),
                (X509Certificate) crlIssuer, responseGenerationDate), report, e ->
                new CertificateReportItem(certificate, CRL_CHECK, CRL_ISSUER_CHAIN_FAILED, e,
                        ReportItemStatus.INDETERMINATE));
        addResponderValidationReport(report, responderReport);
    }

    private Certificate getRoot(Certificate cert) {
        Certificate[] chain = certificateRetriever.retrieveMissingCertificates(new Certificate[] {cert});
        return chain[chain.length - 1];
    }

    private static void addResponderValidationReport(ValidationReport report, ValidationReport responderReport) {
        for (ReportItem reportItem : responderReport.getLogs()) {
            report.addReportItem(ReportItemStatus.INVALID == reportItem.getStatus() ?
                    reportItem.setStatus(ReportItemStatus.INDETERMINATE) : reportItem);
        }
    }
}
