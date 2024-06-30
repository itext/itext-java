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

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;

import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.itextpdf.signatures.validation.v1.SafeCalling.onExceptionLog;
import static com.itextpdf.signatures.validation.v1.SafeCalling.onRuntimeExceptionLog;

/**
 * Validator class, which is expected to be used for signatures validation.
 */
public class SignatureValidator {
    public static final String VALIDATING_SIGNATURE_NAME = "Validating signature {0}";
    static final String TIMESTAMP_VERIFICATION = "Timestamp verification check.";
    static final String SIGNATURE_VERIFICATION = "Signature verification check.";
    static final String CANNOT_PARSE_CERT_FROM_DSS =
            "Certificate {0} stored in DSS dictionary cannot be parsed.";
    static final String CANNOT_PARSE_OCSP_FROM_DSS =
            "OCSP response {0} stored in DSS dictionary cannot be parsed.";
    static final String CANNOT_PARSE_CRL_FROM_DSS =
            "CRL {0} stored in DSS dictionary cannot be parsed.";
    static final String CANNOT_VERIFY_SIGNATURE = "Signature {0} cannot be mathematically verified.";
    static final String DOCUMENT_IS_NOT_COVERED = "Signature {0} doesn't cover entire document.";
    static final String CANNOT_VERIFY_TIMESTAMP = "Signature timestamp attribute cannot be verified.";
    static final String TIMESTAMP_VERIFICATION_FAILED =
            "Unexpected exception occurred during mathematical verification of time stamp signature.";
    static final String REVISIONS_RETRIEVAL_FAILED =
            "Unexpected exception occurred during document revisions retrieval.";
    static final String TIMESTAMP_EXTRACTION_FAILED =
            "Unexpected exception occurred retrieving prove of existence from timestamp signature";
    static final String CHAIN_VALIDATION_FAILED =
            "Unexpected exception occurred during certificate chain validation.";
    static final String REVISIONS_VALIDATION_FAILED = "Unexpected exception occurred during revisions validation.";
    static final String ADD_KNOWN_CERTIFICATES_FAILED =
            "Unexpected exception occurred adding known certificates to certificate retriever.";
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private ValidationContext validationContext = new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private final CertificateChainValidator certificateChainValidator;
    private final DocumentRevisionsValidator documentRevisionsValidator;
    private final IssuingCertificateRetriever certificateRetriever;
    private final SignatureValidationProperties properties;
    private Date lastKnownPoE = DateTimeUtil.getCurrentTimeDate();
    private IMetaInfo metaInfo = new ValidationMetaInfo();
    private final PdfDocument originalDocument;
    private ValidationOcspClient validationOcspClient;
    private ValidationCrlClient validationCrlClient;

    /**
     * Creates new instance of {@link SignatureValidator}.
     *
     * @param originalDocument {@link PdfDocument} instance which will be validated
     * @param builder          see {@link ValidatorChainBuilder}
     */
    protected SignatureValidator(PdfDocument originalDocument, ValidatorChainBuilder builder) {
        this.originalDocument = originalDocument;
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.certificateChainValidator = builder.getCertificateChainValidator();
        this.documentRevisionsValidator = builder.getDocumentRevisionsValidator();
        findValidationClients();
    }

    /**
     * Sets the {@link IMetaInfo} that will be used during new {@link PdfDocument} creations.
     *
     * @param metaInfo meta info to set
     *
     * @return the same {@link SignatureValidator} instance
     */
    public SignatureValidator setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    /**
     * Validate all signatures in the document.
     *
     * @return {@link ValidationReport} which contains detailed validation results
     */
    public ValidationReport validateSignatures() {
        ValidationReport report = new ValidationReport();
        onRuntimeExceptionLog(() -> {
            documentRevisionsValidator.setEventCountingMetaInfo(metaInfo);
            ValidationReport revisionsValidationReport =
                    documentRevisionsValidator.validateAllDocumentRevisions(validationContext, originalDocument);
            report.merge(revisionsValidationReport);
        }, report, e ->
                new ReportItem(SIGNATURE_VERIFICATION, REVISIONS_VALIDATION_FAILED, e, ReportItemStatus.INDETERMINATE));

        if (stopValidation(report, validationContext)) {
            return report;
        }

        SignatureUtil util = new SignatureUtil(originalDocument);
        List<String> signatureNames = util.getSignatureNames();
        Collections.reverse(signatureNames);

        for (String fieldName : signatureNames) {
            try (PdfDocument doc = new PdfDocument(new PdfReader(util.extractRevision(fieldName)),
                    new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
                ValidationReport subReport = validateLatestSignature(doc);
                report.merge(subReport);
                if (stopValidation(report, validationContext)) {
                    return report;
                }
            } catch (IOException | RuntimeException e) {
                report.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, REVISIONS_RETRIEVAL_FAILED,
                        e, ReportItemStatus.INDETERMINATE));
            }
        }
        return report;
    }

    ValidationReport validateLatestSignature(PdfDocument document) {
        ValidationReport validationReport = new ValidationReport();
        PdfPKCS7 pkcs7 = mathematicallyVerifySignature(validationReport, document);
        updateValidationClients(pkcs7, validationReport, validationContext, document);
        // We only retrieve not signed revocation data at the very beginning of signature processing.
        retrieveNotSignedRevocationInfoFromSignatureContainer(pkcs7, validationContext);
        if (stopValidation(validationReport, validationContext)) {
            return validationReport;
        }

        List<Certificate> certificatesFromDss = getCertificatesFromDss(validationReport, document);
        onRuntimeExceptionLog(() -> certificateRetriever.addKnownCertificates(certificatesFromDss),
                validationReport, e -> new ReportItem(SIGNATURE_VERIFICATION, ADD_KNOWN_CERTIFICATES_FAILED, e,
                        ReportItemStatus.INFO));

        if (pkcs7.isTsp()) {
            validateTimestampChain(validationReport, pkcs7.getCertificates(), pkcs7.getSigningCertificate());
            if (updateLastKnownPoE(validationReport, pkcs7.getTimeStampTokenInfo())) {
                updateValidationClients(pkcs7, validationReport, validationContext, document);
            }
            return validationReport;
        }

        boolean isPoEUpdated = false;
        Date previousLastKnowPoE = lastKnownPoE;
        ValidationContext previousValidationContext = validationContext;
        if (pkcs7.getTimeStampTokenInfo() != null) {
            ValidationReport tsValidationReport = validateEmbeddedTimestamp(pkcs7);
            isPoEUpdated = updateLastKnownPoE(tsValidationReport, pkcs7.getTimeStampTokenInfo());
            if (isPoEUpdated) {
                PdfPKCS7 timestampSignatureContainer = pkcs7.getTimestampSignatureContainer();
                retrieveSignedRevocationInfoFromSignatureContainer(timestampSignatureContainer, validationContext);
                updateValidationClients(pkcs7, tsValidationReport, validationContext, document);
            }
            validationReport.merge(tsValidationReport);
            if (stopValidation(tsValidationReport, validationContext)) {
                return validationReport;
            }
        }

        Certificate[] certificates = pkcs7.getCertificates();
        onRuntimeExceptionLog(() -> certificateRetriever.addKnownCertificates(Arrays.asList(certificates)),
                validationReport, e -> new ReportItem(SIGNATURE_VERIFICATION, ADD_KNOWN_CERTIFICATES_FAILED, e,
                        ReportItemStatus.INFO));

        X509Certificate signingCertificate = pkcs7.getSigningCertificate();

        ValidationReport signatureReport = new ValidationReport();
        onExceptionLog(() ->
                        certificateChainValidator.validate(signatureReport, validationContext,
                                signingCertificate, lastKnownPoE),
                validationReport, e -> new CertificateReportItem(signingCertificate, SIGNATURE_VERIFICATION,
                        CHAIN_VALIDATION_FAILED, e, ReportItemStatus.INDETERMINATE));
        if (isPoEUpdated && signatureReport.getValidationResult() != ValidationResult.VALID) {
            // We can only use PoE retrieved from timestamp attribute in case main signature validation is successful.
            // That's why if the result is not valid, we set back lastKnownPoE value, validation context and rev data.
            lastKnownPoE = previousLastKnowPoE;
            validationContext = previousValidationContext;
            PdfPKCS7 timestampSignatureContainer = pkcs7.getTimestampSignatureContainer();
            retrieveSignedRevocationInfoFromSignatureContainer(timestampSignatureContainer, validationContext);
            updateValidationClients(pkcs7, validationReport, validationContext, document);
        }
        return validationReport.merge(signatureReport);
    }

    private void findValidationClients() {
        for (IOcspClient ocspClient : this.properties.getOcspClients()) {
            if (ocspClient.getClass() == ValidationOcspClient.class) {
                validationOcspClient = (ValidationOcspClient) ocspClient;
                break;
            }
        }
        for (ICrlClient crlClient : this.properties.getCrlClients()) {
            if (crlClient.getClass() == ValidationCrlClient.class) {
                validationCrlClient = (ValidationCrlClient) crlClient;
                break;
            }
        }
    }

    private PdfPKCS7 mathematicallyVerifySignature(ValidationReport validationReport, PdfDocument document) {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        List<String> signatures = signatureUtil.getSignatureNames();
        String latestSignatureName = signatures.get(signatures.size() - 1);
        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(latestSignatureName);
        validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION,
                MessageFormatUtil.format(VALIDATING_SIGNATURE_NAME, latestSignatureName), ReportItemStatus.INFO));

        if (!signatureUtil.signatureCoversWholeDocument(latestSignatureName)) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION,
                    MessageFormatUtil.format(DOCUMENT_IS_NOT_COVERED, latestSignatureName), ReportItemStatus.INVALID));
        }
        try {
            if (!pkcs7.verifySignatureIntegrityAndAuthenticity()) {
                validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                        CANNOT_VERIFY_SIGNATURE, latestSignatureName), ReportItemStatus.INVALID));
            }
        } catch (GeneralSecurityException | RuntimeException e) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                    CANNOT_VERIFY_SIGNATURE, latestSignatureName), e, ReportItemStatus.INVALID));
        }
        return pkcs7;
    }

    private ValidationReport validateEmbeddedTimestamp(PdfPKCS7 pkcs7) {
        ValidationReport tsValidationReport = new ValidationReport();
        try {
            if (!pkcs7.verifyTimestampImprint()) {
                tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP,
                        ReportItemStatus.INVALID));
            }
        } catch (GeneralSecurityException e) {
            tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP, e,
                    ReportItemStatus.INVALID));
        } catch (RuntimeException e) {
            tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION_FAILED,
                    TIMESTAMP_VERIFICATION_FAILED, e, ReportItemStatus.INVALID));
        }
        if (stopValidation(tsValidationReport, validationContext)) {
            return tsValidationReport;
        }

        PdfPKCS7 timestampSignatureContainer = pkcs7.getTimestampSignatureContainer();
        retrieveSignedRevocationInfoFromSignatureContainer(timestampSignatureContainer, validationContext);
        try {
            if (!timestampSignatureContainer.verifySignatureIntegrityAndAuthenticity()) {
                tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION,
                        CANNOT_VERIFY_TIMESTAMP, ReportItemStatus.INVALID));
            }
        } catch (GeneralSecurityException e) {
            tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION,
                    CANNOT_VERIFY_TIMESTAMP, e, ReportItemStatus.INVALID));
        } catch (RuntimeException e) {
            tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION_FAILED,
                    TIMESTAMP_VERIFICATION_FAILED, e, ReportItemStatus.INVALID));
        }
        if (stopValidation(tsValidationReport, validationContext)) {
            return tsValidationReport;
        }

        Certificate[] timestampCertificates = timestampSignatureContainer.getCertificates();
        validateTimestampChain(tsValidationReport, timestampCertificates,
                timestampSignatureContainer.getSigningCertificate());
        return tsValidationReport;
    }

    private void validateTimestampChain(ValidationReport validationReport, Certificate[] knownCerts,
            X509Certificate signingCert) {
        onExceptionLog(() ->
                certificateRetriever.addKnownCertificates(Arrays.asList(knownCerts)), validationReport, e ->
                new ReportItem(SIGNATURE_VERIFICATION, ADD_KNOWN_CERTIFICATES_FAILED, e,
                        ReportItemStatus.INFO));

        try {
            certificateChainValidator.validate(validationReport,
                    validationContext.setCertificateSource(CertificateSource.TIMESTAMP),
                    signingCert, lastKnownPoE);
        } catch (RuntimeException e) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, CHAIN_VALIDATION_FAILED, e,
                    ReportItemStatus.INFO));
        }
    }

    private boolean updateLastKnownPoE(ValidationReport tsValidationReport, ITSTInfo timeStampTokenInfo) {
        if (tsValidationReport.getValidationResult() == ValidationResult.VALID) {
            try {
                lastKnownPoE = timeStampTokenInfo.getGenTime();
                if (validationContext.getTimeBasedContext() == TimeBasedContext.PRESENT) {
                    validationContext = validationContext.setTimeBasedContext(TimeBasedContext.HISTORICAL);
                }
                return true;
            } catch (Exception e) {
                tsValidationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, TIMESTAMP_EXTRACTION_FAILED, e,
                        ReportItemStatus.INDETERMINATE));
            }
        }
        return false;
    }

    private void updateValidationClients(PdfPKCS7 pkcs7, ValidationReport validationReport,
            ValidationContext validationContext, PdfDocument document) {
        retrieveOcspResponsesFromDss(validationReport, validationContext, document);
        retrieveCrlResponsesFromDss(validationReport, validationContext, document);
        retrieveSignedRevocationInfoFromSignatureContainer(pkcs7, validationContext);
    }

    private void retrieveSignedRevocationInfoFromSignatureContainer(PdfPKCS7 pkcs7,
            ValidationContext validationContext) {
        if (pkcs7.getCRLs() != null) {
            for (CRL crl : pkcs7.getCRLs()) {
                validationCrlClient.addCrl((X509CRL) crl, lastKnownPoE, validationContext.getTimeBasedContext());
            }
        }
        if (pkcs7.getOcsp() != null) {
            validationOcspClient.addResponse(BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(pkcs7.getOcsp()), lastKnownPoE,
                    validationContext.getTimeBasedContext());
        }
    }

    private void retrieveNotSignedRevocationInfoFromSignatureContainer(PdfPKCS7 pkcs7,
            ValidationContext validationContext) {
        for (CRL crl : pkcs7.getSignedDataCRLs()) {
            validationCrlClient.addCrl((X509CRL) crl, lastKnownPoE, validationContext.getTimeBasedContext());
        }
        for (IBasicOCSPResponse oscp : pkcs7.getSignedDataOcsps()) {
            validationOcspClient.addResponse(BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(oscp), lastKnownPoE,
                    validationContext.getTimeBasedContext());
        }
    }

    private void retrieveOcspResponsesFromDss(ValidationReport validationReport, ValidationContext context,
            PdfDocument document) {
        PdfDictionary dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        if (dss != null) {
            PdfArray ocsps = dss.getAsArray(PdfName.OCSPs);
            if (ocsps != null) {
                for (int i = 0; i < ocsps.size(); ++i) {
                    PdfStream ocspStream = ocsps.getAsStream(i);
                    try {
                        validationOcspClient.addResponse(BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                                        BOUNCY_CASTLE_FACTORY.createOCSPResp(ocspStream.getBytes()).getResponseObject()),
                                lastKnownPoE, context.getTimeBasedContext());
                    } catch (IOException | AbstractOCSPException | RuntimeException e ) {
                        validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                                CANNOT_PARSE_OCSP_FROM_DSS, ocspStream), e, ReportItemStatus.INFO));
                    }
                }
            }
        }
    }

    private void retrieveCrlResponsesFromDss(ValidationReport validationReport, ValidationContext context,
            PdfDocument document) {
        PdfDictionary dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        if (dss != null) {
            PdfArray crls = dss.getAsArray(PdfName.CRLs);
            if (crls != null) {
                for (int i = 0; i < crls.size(); ++i) {
                    PdfStream crlStream = crls.getAsStream(i);
                    onExceptionLog(() ->
                            validationCrlClient.addCrl(
                                    (X509CRL) CertificateUtil.parseCrlFromBytes(crlStream.getBytes()),
                                    lastKnownPoE, context.getTimeBasedContext()), validationReport, e ->
                            new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                                    CANNOT_PARSE_CRL_FROM_DSS, crlStream), e, ReportItemStatus.INFO));
                }
            }
        }
    }

    private List<Certificate> getCertificatesFromDss(ValidationReport validationReport, PdfDocument document) {
        PdfDictionary dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        List<Certificate> certificatesFromDss = new ArrayList<>();
        if (dss != null) {
            PdfArray certs = dss.getAsArray(PdfName.Certs);
            if (certs != null) {
                for (int i = 0; i < certs.size(); ++i) {
                    PdfStream certStream = certs.getAsStream(i);
                    try {
                        certificatesFromDss.add(CertificateUtil.generateCertificate(
                                new ByteArrayInputStream(certStream.getBytes())));
                    } catch (GeneralSecurityException | RuntimeException e) {
                        validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                                CANNOT_PARSE_CERT_FROM_DSS, certStream), e, ReportItemStatus.INFO));
                    }
                }
            }
        }
        return certificatesFromDss;
    }

    private boolean stopValidation(ValidationReport result, ValidationContext validationContext) {
        return !properties.getContinueAfterFailure(validationContext)
                && result.getValidationResult() == ValidationResult.INVALID;
    }
}
