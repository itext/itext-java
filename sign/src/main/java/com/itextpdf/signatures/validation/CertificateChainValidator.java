/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.pkix.AbstractPKIXNameConstraintValidatorException;
import com.itextpdf.commons.bouncycastle.asn1.pkix.IPKIXConstraintValidator;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralSubtree;
import com.itextpdf.commons.bouncycastle.asn1.x509.INameConstraints;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.dataorigin.CertificateOrigin;
import com.itextpdf.signatures.validation.events.CertificateIssuerExternalRetrievalEvent;
import com.itextpdf.signatures.validation.events.CertificateIssuerRetrievedOutsideDSSEvent;
import com.itextpdf.signatures.validation.events.AlgorithmUsageEvent;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.DynamicCertificateExtension;
import com.itextpdf.signatures.validation.lotl.LotlTrustedStore;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import javax.security.auth.x500.X500Principal;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.signatures.validation.SafeCalling.onExceptionLog;
import static com.itextpdf.signatures.validation.SafeCalling.onRuntimeExceptionLog;

/**
 * Validator class, which is expected to be used for certificates chain validation.
 */
public class CertificateChainValidator {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final SignatureValidationProperties properties;
    private final IssuingCertificateRetriever certificateRetriever;
    private final RevocationDataValidator revocationDataValidator;
    private final LotlTrustedStore lotlTrustedStore;
    private final EventManager eventManager;

    static final String CERTIFICATE_CHECK = "Certificate check.";
    static final String VALIDITY_CHECK = "Certificate validity period check.";
    static final String EXTENSIONS_CHECK = "Required certificate extensions check.";

    static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted, revocation data checks are not required.";
    static final String CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT = "Certificate {0} is trusted for {1}, "
            + "but it is not used in this context. Validation will continue as usual.";
    static final String EXTENSION_MISSING = "Required extension validation failed: {0}";
    static final String ISSUER_MISSING = "Certificate {0} isn't trusted and issuer certificate isn't provided.";
    static final String EXPIRED_CERTIFICATE = "Certificate {0} is expired.";
    static final String NOT_YET_VALID_CERTIFICATE = "Certificate {0} is not yet valid.";
    static final String ISSUER_CANNOT_BE_VERIFIED =
            "Issuer certificate {0} for subject certificate {1} cannot be mathematically verified.";

    static final String ISSUER_VERIFICATION_FAILED =
            "Unexpected exception occurred while verifying issuer certificate.";
    static final String ISSUER_RETRIEVAL_FAILED =
            "Unexpected exception occurred while retrieving certificate issuer from IssuingCertificateRetriever.";
    static final String TRUSTSTORE_RETRIEVAL_FAILED =
            "Unexpected exception occurred while retrieving trust store from IssuingCertificateRetriever.";
    static final String REVOCATION_VALIDATION_FAILED =
            "Unexpected exception occurred while validating certificate revocation.";
    static final String VALIDITY_PERIOD_CHECK_FAILED =
            "Unexpected exception occurred while validating certificate validity period.";
    static final String CERTIFICATE_RETRIEVER_ORIGIN =
            "Trusted Certificate is taken from manually configured Trust List.";
    static final String CERTIFICATE_LOTL_ORIGIN =
            "Trusted Certificate is taken from European Union List of Trusted Certificates.";
    static final String CERTIFICATE_CUSTOM_ORIGIN = "Trusted Certificate is taken from {0}.";
    static final String NAME_CONSTRAINT_DIRECT_NAME_VIOLATION =
            "Certificate's direct name is not allowed according to Name Constraint extension.";
    static final String NAME_CONSTRAINT_DIRECT_NAME_EXCEPTION =
            "Exception occurred while trying to check certificate direct name against Name Constraint extension.";
    static final String NAME_CONSTRAINT_ALT_NAME_VIOLATION =
            "Certificate's alternative name is not allowed according to Name Constraint extension.";
    static final String NAME_CONSTRAINT_ALT_NAME_EXCEPTION =
            "Exception occurred while trying to check certificate alternative name against Name Constraint extension.";

    /**
     * Create new instance of {@link CertificateChainValidator}.
     *
     * @param builder See {@link  ValidatorChainBuilder}
     */
    protected CertificateChainValidator(ValidatorChainBuilder builder) {
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.revocationDataValidator = builder.getRevocationDataValidator();
        this.lotlTrustedStore = builder.getLotlTrustedStore();
        this.eventManager = builder.getEventManager();
    }

    /**
     * Validate given certificate using provided validation date and required extensions.
     *
     * @param context        the validation context in which to validate the certificate chain
     * @param certificate    {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing
     *                       date
     *
     * @return {@link ValidationReport} which contains detailed validation results.
     */
    public ValidationReport validateCertificate(ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        ValidationReport result = new ValidationReport();
        return validate(result, context, certificate, validationDate);
    }

    /**
     * Validate given certificate using provided validation date and required extensions.
     * Result is added into provided report.
     *
     * @param result         {@link ValidationReport} which is populated with detailed validation results
     * @param context        the context in which to perform the validation
     * @param certificate    {@link X509Certificate} to be validated
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing
     *                       date
     *
     * @return {@link ValidationReport} which contains both provided and new validation results.
     */
    public ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        return validate(result, context, certificate, validationDate, new ArrayList<>());
    }

    /**
     * Validates name constraint extension for complete certificate chain.
     *
     * @param report {@link ValidationReport} which is populated with detailed validation results
     * @param previousCertificates {@link List} of {@link X509Certificate}, which represent a complete chain,
     *                                         without a trusted root. List starts with a signing certificate.
     * @param trustedCertificate {@link X509Certificate} trusted root of this chain
     */
    protected void validateNameConstraints(ValidationReport report, List<X509Certificate> previousCertificates,
                                           X509Certificate trustedCertificate) {
        IPKIXConstraintValidator constraintValidator = FACTORY.createNameConstraintValidator();
        List<X509Certificate> certificateChain = new ArrayList<>(previousCertificates);
        Collections.reverse(certificateChain);

        updateConstraintValidator(constraintValidator, trustedCertificate);
        for (X509Certificate certificate : certificateChain) {
            X500Principal principal = certificate.getSubjectX500Principal();
            try (IASN1InputStream inputStream = FACTORY.createASN1InputStream(principal.getEncoded())) {
                IASN1Sequence directName = FACTORY.createASN1Sequence(inputStream.readObject());
                constraintValidator.checkPermittedDN(directName);
                constraintValidator.checkExcludedDN(directName);
            } catch (AbstractPKIXNameConstraintValidatorException e) {
                report.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                        NAME_CONSTRAINT_DIRECT_NAME_VIOLATION, e, ReportItemStatus.INVALID));
                return;
            } catch (Exception e) {
                report.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                        NAME_CONSTRAINT_DIRECT_NAME_EXCEPTION , e, ReportItemStatus.INVALID));
                return;
            }

            IASN1Sequence alternativeName = getAlternativeName(certificate);
            if (alternativeName != null && !alternativeName.isNull()) {
                for (int i = 0; i < alternativeName.size(); ++i) {
                    try {
                        IGeneralName generalName = FACTORY.createGeneralName(alternativeName.getObjectAt(i));
                        constraintValidator.checkPermitted(generalName);
                        constraintValidator.checkExcluded(generalName);
                    } catch (AbstractPKIXNameConstraintValidatorException e) {
                        report.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                                NAME_CONSTRAINT_ALT_NAME_VIOLATION, e, ReportItemStatus.INVALID));
                        return;
                    } catch (Exception e) {
                        report.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                                NAME_CONSTRAINT_ALT_NAME_EXCEPTION, e, ReportItemStatus.INVALID));
                        return;
                    }
                }
            }

            updateConstraintValidator(constraintValidator, certificate);
        }
    }

    private static IASN1Sequence getAlternativeName(X509Certificate certificate) {
        try {
            return FACTORY.createASN1Sequence(
                    CertificateUtil.getExtensionValue(certificate, OID.X509Extensions.SUBJECT_ALTERNATIVE_NAME));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void updateConstraintValidator(IPKIXConstraintValidator constraintValidator,
                                                  X509Certificate certificate) {
        INameConstraints nameConstraints;
        try {
            nameConstraints = FACTORY.createNameConstraints(
                    CertificateUtil.getExtensionValue(certificate, OID.X509Extensions.NAME_CONSTRAINTS));
        } catch (Exception ignored) {
            return;
        }
        if (nameConstraints != null && !nameConstraints.isNull()) {
            IGeneralSubtree[] permitted = nameConstraints.getPermittedSubtrees();
            if (permitted != null) {
                constraintValidator.intersectPermittedSubtree(permitted);
            }
            IGeneralSubtree[] excluded = nameConstraints.getExcludedSubtrees();
            if (excluded != null) {
                for (IGeneralSubtree iGeneralSubtree : excluded) {
                    constraintValidator.addExcludedSubtree(iGeneralSubtree);
                }
            }
        }
    }

    private ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate, List<X509Certificate> previousCertificates) {
        reportAlgorithmUsage(certificate);
        ValidationContext localContext = context.setValidatorContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR);
        validateRequiredExtensions(result, localContext, certificate, previousCertificates.size());
        if (stopValidation(result, localContext)) {
            return result;
        }

        if (onExceptionLog(
                () -> checkIfCertIsTrusted(result, localContext, certificate, validationDate, previousCertificates),
                Boolean.FALSE, result, e -> new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        TRUSTSTORE_RETRIEVAL_FAILED, e, ReportItemStatus.INFO))) {
            // We need to perform this check right after we allegedly found trusted root.
            validateNameConstraints(result, previousCertificates, certificate);
            return result;
        }
        handlePadesEvents(certificate);

        validateValidityPeriod(result, certificate, validationDate);
        validateRevocationData(result, localContext, certificate, validationDate);
        if (stopValidation(result, localContext)) {
            return result;
        }

        validateChain(result, localContext, certificate, validationDate, previousCertificates);
        return result;
    }

    private void reportAlgorithmUsage(X509Certificate certificate) {
        eventManager.onEvent(new AlgorithmUsageEvent(
                certificate.getSigAlgName(), certificate.getSigAlgOID(), CERTIFICATE_CHECK));
    }

    private void handlePadesEvents(X509Certificate certificate) {
        CertificateOrigin certificateOrigin = certificateRetriever.getCertificateOrigin(certificate);
        if (certificateOrigin == CertificateOrigin.OTHER) {
            eventManager.onEvent(new CertificateIssuerExternalRetrievalEvent(certificate));
        } else if (certificateOrigin != CertificateOrigin.LATEST_DSS) {
            eventManager.onEvent(new CertificateIssuerRetrievedOutsideDSSEvent(certificate));
        }
    }

    private boolean checkIfCertIsTrusted(ValidationReport result, ValidationContext context,
            X509Certificate certificate, Date validationDate, List<X509Certificate> previousCertificates) {
        if (certificateRetriever.getTrustedCertificatesStore().checkIfCertIsTrusted(result, context, certificate)) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, CERTIFICATE_RETRIEVER_ORIGIN,
                    ReportItemStatus.INFO));
            return true;
        }

        if (lotlTrustedStore == null) {
            return false;
        }

        if (lotlTrustedStore.setPreviousCertificates(previousCertificates)
                .checkIfCertIsTrusted(result, context, certificate, validationDate)) {
            if (lotlTrustedStore.getClass() == LotlTrustedStore.class){
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, CERTIFICATE_LOTL_ORIGIN,
                        ReportItemStatus.INFO));
            } else {
                result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                         CERTIFICATE_CUSTOM_ORIGIN, lotlTrustedStore.getClass().getName()),
                        ReportItemStatus.INFO));
            }

            return true;
        }

        return false;
    }

    private boolean stopValidation(ValidationReport result, ValidationContext context) {
        return result.getValidationResult() == ValidationResult.INVALID &&
                !properties.getContinueAfterFailure(context);
    }

    private void validateValidityPeriod(ValidationReport result, X509Certificate certificate,
            Date validationDate) {
        try {
            certificate.checkValidity(validationDate);
        } catch (CertificateExpiredException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    EXPIRED_CERTIFICATE, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        } catch (CertificateNotYetValidException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    NOT_YET_VALID_CERTIFICATE, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        } catch (RuntimeException e) {
            result.addReportItem(new CertificateReportItem(certificate, VALIDITY_CHECK, MessageFormatUtil.format(
                    VALIDITY_PERIOD_CHECK_FAILED, certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
        }
    }

    private void validateRequiredExtensions(ValidationReport result, ValidationContext context,
            X509Certificate certificate, int certificateChainSize) {
        List<CertificateExtension> requiredExtensions = properties.getRequiredExtensions(context);
        if (requiredExtensions != null) {
            for (CertificateExtension requiredExtension : requiredExtensions) {
                if (requiredExtension instanceof DynamicCertificateExtension) {
                    ((DynamicCertificateExtension) requiredExtension).withCertificateChainSize(certificateChainSize);
                }
                if (!requiredExtension.existsInCertificate(certificate)) {
                    result.addReportItem(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                            MessageFormatUtil.format(EXTENSION_MISSING, requiredExtension.getMessage()),
                            ReportItemStatus.INVALID));
                }
            }
        }
    }

    private void validateRevocationData(ValidationReport report, ValidationContext context, X509Certificate certificate,
            Date validationDate) {
        onRuntimeExceptionLog(() ->
                revocationDataValidator.validate(report, context, certificate, validationDate), report, e ->
                new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        REVOCATION_VALIDATION_FAILED, e, ReportItemStatus.INDETERMINATE));
    }

    private void validateChain(ValidationReport result, ValidationContext context, X509Certificate certificate,
            Date validationDate, List<X509Certificate> previousCertificates) {
        List<X509Certificate> issuerCertificates;
        try {
            issuerCertificates = certificateRetriever.retrieveIssuerCertificate(certificate);
        } catch (RuntimeException e) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                    ISSUER_RETRIEVAL_FAILED, e, ReportItemStatus.INDETERMINATE));
            return;
        }
        // We need to sort certificates to process them starting from those, better suited for PAdES validation.
        issuerCertificates = issuerCertificates.stream().sorted((issuer1, issuer2) -> Integer.compare(
                certificateRetriever.getCertificateOrigin(issuer1).ordinal(),
                certificateRetriever.getCertificateOrigin(issuer2).ordinal()))
                .filter(c -> !previousCertificates.contains(c))
                .collect(Collectors.toList());
        if (issuerCertificates.isEmpty()) {
            result.addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK, MessageFormatUtil.format(
                    ISSUER_MISSING, certificate.getSubjectX500Principal()), ReportItemStatus.INDETERMINATE));
            return;
        }
        ValidationReport[] candidateReports = new ValidationReport[issuerCertificates.size()];
        for (int i = 0; i < issuerCertificates.size(); i++) {
            candidateReports[i] = new ValidationReport();
            try {
                certificate.verify(issuerCertificates.get(i).getPublicKey());
            } catch (GeneralSecurityException e) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        MessageFormatUtil.format(ISSUER_CANNOT_BE_VERIFIED,
                                issuerCertificates.get(i).getSubjectX500Principal(),
                                certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
                continue;
            } catch (RuntimeException e) {
                candidateReports[i].addReportItem(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                        MessageFormatUtil.format(ISSUER_VERIFICATION_FAILED,
                                issuerCertificates.get(i).getSubjectX500Principal(),
                                certificate.getSubjectX500Principal()), e, ReportItemStatus.INVALID));
                continue;
            }
            previousCertificates.add(certificate);
            this.validate(candidateReports[i], context.setCertificateSource(CertificateSource.CERT_ISSUER),
                    issuerCertificates.get(i), validationDate, previousCertificates);
            previousCertificates.remove(previousCertificates.size() - 1);
            if (candidateReports[i].getValidationResult() == ValidationResult.VALID) {
                // We found valid issuer, no need to try other ones.
                result.merge(candidateReports[i]);
                return;
            }
        }
        // Valid issuer wasn't found, add all the reports
        for (ValidationReport candidateReport : candidateReports) {
            result.merge(candidateReport);
        }
    }
}
