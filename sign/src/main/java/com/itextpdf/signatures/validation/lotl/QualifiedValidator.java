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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.x509.qualified.IQCStatement;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Validator class which performs qualification validation for signatures.
 */
public class QualifiedValidator {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final Date EIDAS = DateTimeUtil.createUtcDateTime(2016, 5, 30, 22, 0, 0);
    static final String QUALIFICATION_CHECK = "Qualification check.";
    static final String NOT_ACCREDITED_STATUS = "Trusted Certificate {0} is not considered to be qualified, " +
            "because it's status is not \"Accredited\", \"Under Supervision\" or \"Supervision in Cessation\".";
    static final String NOT_GRANTED_STATUS = "Trusted Certificate {0} is not considered to be qualified, " +
            "because it's status is not \"Granted\".";
    static final String CONTRADICTING_QSCD = "Trusted Certificate {0} is not considered to be created by a " +
            "Qualified Signature Creation Device, because it has contradicting QC_XX_QSCD Qualifier values.";
    static final String CONTRADICTING_QC_FOR = "Trusted Certificate {0} type is not identifiable, " +
            "because it has contradicting QCFor_XX Qualifier values.";
    static final String CONTRADICTING_QC_STATEMENT = "Trusted Certificate {0} is not considered to be qualified, " +
            "because it has contradicting QC Statement Qualifier values.";
    static final String QC_WSA = "Trusted Certificate {0} is not considered to be qualified, " +
            "because it has QCForWSA Qualifier value.";
    static final String QC_TYPE_WSA = "Certificate {0} is not considered to be qualified, " +
            "because it has WSA value in the QcType certificate extension.";
    static final String EXCEPTION_STATEMENT_PARSING =
            "Exception thrown during Certificate {0} QC Statement extension parsing. " +
                    "The conclusion will be made as if this extension is missing.";
    static final String CONTRADICTING_QC_TYPE = "Certificate {0} has contradicting QcType extension values. " +
            "The conclusion will be made as if this extension is missing.";
    static final String CERT_NOT_QUALIFIED = "Certificate {0} is not qualified " +
            "according to the corresponding Trusted List Qualifier Extensions and Certificate Extensions.";
    static final String NOT_QSCD = "Certificate {0} is qualified, " +
            "but it's private key doesn't reside in Qualified Signature Creation device.";
    static final String TYPE_UNDEFINED = "Certificate {0} type (either eSig or eSeal) cannot be defined.";
    static final String TYPE_CONTRADICTS_WITH_SI = "Certificate {0} type (either eSig or eSeal) contradicts with " +
            "the type provided in Additional Service Information extension value.";
    static final String CERTIFICATE_VALIDATION_EXCEPTION =
            "Exception occurred while validating qualification status of a {0} certificate.";
    static final String MULTIPLE_CA_QC_ENTRIES = "Multiple CA/QC entries correspond to the given signing certificate " +
            "and their conclusions on qualification status are different.";

    private static final String QC_WITH_QSCD = "QcWithQSCD";
    private static final String NO_QSCD = "QcNoQSCD";
    private static final String QSCD_MANAGED_ON_BEHALF = "QCQSCDManagedOnBehalf";
    private static final String QSCD_STATUS_AS_IN_CERT = "QcQSCDStatusAsInCert";
    private static final String QC_STATEMENTS_EXTENSION = "1.3.6.1.5.5.7.1.3";
    private static final String QC_COMPLIANCE_EXTENSION = "0.4.0.1862.1.1";
    private static final String QSCD_EXTENSION = "0.4.0.1862.1.4";
    private static final String QUALIFIED_TYPE_EXTENSION = "0.4.0.1862.1.6";
    private static final String ESIG_TYPE_EXTENSION = "0.4.0.1862.1.6.1";
    private static final String ESEAL_TYPE_EXTENSION = "0.4.0.1862.1.6.2";
    private static final String WSA_TYPE_EXTENSION = "0.4.0.1862.1.6.3";
    private static final String CERTIFICATE_POLICIES_EXTENSION = "2.5.29.32";
    private static final String QCP_EXTENSION = "0.4.0.1456.1.2";
    private static final String QCP_PLUS_EXTENSION = "0.4.0.1456.1.1";
    
    private final Map<String, QualificationValidationData> signaturesValidationResults = new HashMap<>();
    private QualificationValidationData signatureValidationData;
    private QualificationValidationData currentValidationData;

    /**
     * Creates a new instance of {@link QualifiedValidator}.
     */
    public QualifiedValidator() {
        // Empty constructor.
    }

    /**
     * Gets and removes qualification validation results for requested signature.
     *
     * @param signatureName signature name, for which the results are obtained
     *
     * @return {@link QualificationValidationData} representing qualification validation result
     */
    public QualificationValidationData obtainQualificationValidationResultForSignature(String signatureName) {
        return signaturesValidationResults.remove(signatureName);
    }

    /**
     * Gets and removes qualification validation results for all the signatures being validated.
     *
     * @return qualification validation results for all the signatures being validated
     */
    public Map<String, QualificationValidationData> obtainAllSignaturesValidationResults() {
        Map<String, QualificationValidationData> results = new HashMap<>(signaturesValidationResults);
        signaturesValidationResults.clear();
        return results;
    }

    /**
     * Starts new validation iteration for a given signature. Called automatically when signature validation starts.
     *
     * @param signatureName the name of a signature to be validated
     */
    public void startSignatureValidation(String signatureName) {
        signatureValidationData = new QualificationValidationData();
        signaturesValidationResults.put(signatureName, signatureValidationData);
    }

    /**
     * Ensures that the same instance of {@link QualifiedValidator} was not used twice for different
     * documents without the results being obtained.
     */
    public void ensureValidatorIsEmpty() {
        if (!signaturesValidationResults.isEmpty()) {
            throw new PdfException(SignExceptionMessageConstant.QUALIFIED_VALIDATOR_ALREADY_USED);
        }
    }

    /**
     * Checks signature qualification status for a provided set of parameters corresponding to an entry in a TL.
     *
     * @param previousCertificates list of {@link X509Certificate} objects in the validated chain
     * @param currentContext {@link CountryServiceContext} corresponding to this entry in a TL
     * @param trustedCertificate {@link X509Certificate} trusted certificate from this TL entry
     * @param validationDate {@link Date} at which validation happens
     * @param context {@link ValidationContext} corresponding to the provided certificates chain
     */
    protected void checkSignatureQualification(List<X509Certificate> previousCertificates,
                                               CountryServiceContext currentContext, X509Certificate trustedCertificate,
                                               Date validationDate, ValidationContext context) {
        currentValidationData = new QualificationValidationData();
        try {
            if (!getQualifiedServiceTypes().contains(currentContext.getServiceType())) {
                currentValidationData.qualificationConclusion = QualificationConclusion.NOT_CATCHING;
                return;
            }

            if (ValidationContext.checkIfContextChainContainsCertificateSource(
                    context, CertificateSource.OCSP_ISSUER) ||
                    ValidationContext.checkIfContextChainContainsCertificateSource(
                            context, CertificateSource.CRL_ISSUER) ||
                    ValidationContext.checkIfContextChainContainsCertificateSource(
                            context, CertificateSource.TIMESTAMP)) {
                // For any of these chains we don't actually need to make sure the certificates are qualified.
                // The only requirement is that trusted certificate comes from a qualified TSP.
                return;
            }

            if (!ServiceTypeIdentifiersConstants.CA_QC.equals(currentContext.getServiceType())) {
                // For the signing certificate chain, the only allowed trust root is CA/QC.
                currentValidationData.qualificationConclusion = QualificationConclusion.NOT_CATCHING;
                return;
            }

            X509Certificate signCert =
                    previousCertificates.isEmpty() ? trustedCertificate : previousCertificates.get(0);
            String trustedCertificateName = trustedCertificate.getSubjectX500Principal().getName();
            FinalQualificationData finalDataAtSigning;
            FinalQualificationData finalDataAtIssuing;
            try {
                finalDataAtSigning =
                        isCertificateQualified(signCert, trustedCertificateName, currentContext, validationDate);
                finalDataAtIssuing = isCertificateQualified(signCert, trustedCertificateName,
                        currentContext, signCert.getNotBefore());
            } catch (Exception e) {
                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                            CERTIFICATE_VALIDATION_EXCEPTION, signCert.getSubjectX500Principal()),
                            e, ReportItem.ReportItemStatus.INFO));
                currentValidationData.qualificationConclusion = QualificationConclusion.NOT_CATCHING;
                return;
            }

            if (currentValidationData.qualificationConclusion == QualificationConclusion.NOT_CATCHING) {
                // Trusted certificate is not caught.
                return;
            }

            boolean finalQualification;
            boolean finalQualifiedCreationDevice;
            CertificateType finalCertType;

            if (finalDataAtSigning.finalQualification == finalDataAtIssuing.finalQualification) {
                finalQualification = finalDataAtSigning.finalQualification;
            } else {
                finalQualification = false;
            }
            finalQualifiedCreationDevice = finalDataAtSigning.finalQualifiedCreationDevice;
            if (finalDataAtSigning.finalCertType == finalDataAtIssuing.finalCertType) {
                finalCertType = finalDataAtSigning.finalCertType;
            } else {
                finalCertType = CertificateType.UNDEFINED;
            }

            if (finalCertType == CertificateType.WSA) {
                currentValidationData.qualificationConclusion = QualificationConclusion.NOT_QUALIFIED;
                return;
            }

            // Check if certificate is qualified.
            if (finalQualification) {
                if (finalQualifiedCreationDevice) {
                    if (finalCertType == CertificateType.E_SIG) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.ESIG_WITH_QC_AND_QSCD;
                    } else if (finalCertType == CertificateType.E_SEAL) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.ESEAL_WITH_QC_AND_QSCD;
                    } else if (finalCertType == CertificateType.INCOHERENT) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.UNKNOWN_QC_AND_QSCD;
                    } else {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(TYPE_UNDEFINED,
                                signCert.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                        currentValidationData.qualificationConclusion = QualificationConclusion.UNKNOWN_QC_AND_QSCD;
                    }
                } else {
                    addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                            NOT_QSCD, signCert.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                    if (finalCertType == CertificateType.E_SIG) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.ESIG_WITH_QC;
                    } else if (finalCertType == CertificateType.E_SEAL) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.ESEAL_WITH_QC;
                    } else if (finalCertType == CertificateType.INCOHERENT) {
                        currentValidationData.qualificationConclusion = QualificationConclusion.UNKNOWN_QC;
                    } else {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(TYPE_UNDEFINED,
                                signCert.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                        currentValidationData.qualificationConclusion = QualificationConclusion.UNKNOWN_QC;
                    }
                }
            } else {
                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                        CERT_NOT_QUALIFIED, signCert.getSubjectX500Principal()),
                        ReportItem.ReportItemStatus.INFO));
                if (finalCertType == CertificateType.E_SIG) {
                    currentValidationData.qualificationConclusion = QualificationConclusion.NOT_QUALIFIED_ESIG;
                } else if (finalCertType == CertificateType.E_SEAL) {
                    currentValidationData.qualificationConclusion = QualificationConclusion.NOT_QUALIFIED_ESEAL;
                } else if (finalCertType == CertificateType.INCOHERENT) {
                    currentValidationData.qualificationConclusion = QualificationConclusion.UNKNOWN;
                } else {
                    addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(TYPE_UNDEFINED,
                            signCert.getSubjectX500Principal()), ReportItem.ReportItemStatus.INFO));
                    currentValidationData.qualificationConclusion = QualificationConclusion.NOT_QUALIFIED;
                }
            }
        } finally {
            updateSignatureQualification();
        }
    }

    private static Set<String> getQualifiedServiceTypes() {
        Set<String> qualifiedServiceTypes = new HashSet<>();
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.CA_QC);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.OCSP_QC);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.CRL_QC);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.TSA_QTST);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.EDS_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.REM_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.PSES_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.QES_VALIDATION_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.REMOTE_Q_SIG_CD_MANAGEMENT_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.REMOTE_Q_SEAL_CD_MANAGEMENT_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.EAA_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.ELECTRONIC_ARCHIVING_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.LEDGERS_Q);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.TSA_TSS_QC);
        qualifiedServiceTypes.add(ServiceTypeIdentifiersConstants.TSA_TSS_ADES_Q_CAND_QES);
        return qualifiedServiceTypes;
    }

    private boolean checkServiceStatus(ServiceChronologicalInfo chronologicalInfo, boolean isBeforeEIDAS,
                                       String trustedCertificateName) {
        String serviceStatus = chronologicalInfo.getServiceStatus();
        if (isBeforeEIDAS) {
            if (!ServiceChronologicalInfo.ACCREDITED.equals(serviceStatus) &&
                    !ServiceChronologicalInfo.UNDER_SUPERVISION.equals(serviceStatus) &&
                    !ServiceChronologicalInfo.SUPERVISION_IN_CESSATION.equals(serviceStatus)) {
                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                        NOT_ACCREDITED_STATUS, trustedCertificateName),
                        ReportItem.ReportItemStatus.INFO));
                return false;
            }
        } else {
            if (!ServiceChronologicalInfo.GRANTED.equals(serviceStatus)) {
                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                        NOT_GRANTED_STATUS, trustedCertificateName),
                        ReportItem.ReportItemStatus.INFO));
                return false;
            }
        }
        return true;
    }

    private Boolean parseQualifiedCreationDeviceOverrule(List<String> applicableQualifiers,
                                                         String trustedCertificateName, boolean isBeforeEidas) {
        String qualifiedCreationDeviceTL = null;
        Boolean qualifiedCreationDeviceOverrule = null;
        List<String> modifiedQualifiers;
        if (isBeforeEidas) {
            // Before eIDAS, QSCD entries are ignored and SSCD are taken into account.
            modifiedQualifiers = applicableQualifiers.stream().filter(
                    applicableQualifier -> applicableQualifier.contains("SSCD")).collect(Collectors.toList());
        } else {
            // After eIDAS, SSCD entries are ignored and QSCD are taken into account.
            modifiedQualifiers = applicableQualifiers.stream().filter(
                    applicableQualifier -> applicableQualifier.contains("QSCD")).collect(Collectors.toList());
        }
        for (String applicableQualifier : modifiedQualifiers) {
            String qualifier = applicableQualifier.replace("SSCD", "QSCD");
            switch (qualifier) {
                // One shall not be able to conclude both QSCD and not QSCD.
                // The following combinations are inconsistent:
                // - QcNoQSCD together with any of the following statements: QcWithQSCD, QcQSCDManagedOnBehalf or
                // QcQSCDStatusAsInCert,
                // - QcWithQSCD and QcQSCDStatusAsInCert,
                // - QcQSCDManagedOnBehalf and QcQSCDStatusAsInCert.
                // - The same 3 combinations, with “QSCD” replaced by “SSCD” in all statements.
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCQSCDManagedOnBehalf":
                    if (NO_QSCD.equals(qualifiedCreationDeviceTL) ||
                            QSCD_STATUS_AS_IN_CERT.equals(qualifiedCreationDeviceTL)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QSCD, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    qualifiedCreationDeviceTL = QSCD_MANAGED_ON_BEHALF;
                    qualifiedCreationDeviceOverrule = true;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCQSCDStatusAsInCert":
                    if (NO_QSCD.equals(qualifiedCreationDeviceTL) ||
                            QSCD_MANAGED_ON_BEHALF.equals(qualifiedCreationDeviceTL) ||
                            QC_WITH_QSCD.equals(qualifiedCreationDeviceTL)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QSCD, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    qualifiedCreationDeviceTL = QSCD_STATUS_AS_IN_CERT;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCWithQSCD":
                    if (NO_QSCD.equals(qualifiedCreationDeviceTL) ||
                            QSCD_STATUS_AS_IN_CERT.equals(qualifiedCreationDeviceTL)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QSCD, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    qualifiedCreationDeviceTL = QC_WITH_QSCD;
                    qualifiedCreationDeviceOverrule = true;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCNoQSCD":
                    if (QC_WITH_QSCD.equals(qualifiedCreationDeviceTL) ||
                            QSCD_STATUS_AS_IN_CERT.equals(qualifiedCreationDeviceTL) ||
                            QSCD_MANAGED_ON_BEHALF.equals(qualifiedCreationDeviceTL)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QSCD, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    qualifiedCreationDeviceTL = NO_QSCD;
                    qualifiedCreationDeviceOverrule = false;
                    break;
            }
        }
        return qualifiedCreationDeviceOverrule;
    }

    private Boolean parseCertificateQualificationOverrule(List<String> applicableQualifiers,
                                                          String trustedCertificateName) {
        Boolean certQualificationOverrule = null;
        for (String applicableQualifier : applicableQualifiers) {
            switch (applicableQualifier) {
                // The following Sie:Q:* statements are mutually exclusive and will raise an error:
                // - QcStatement and NotQualified for the same sigCert under consideration.
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCStatement":
                    if (Boolean.FALSE.equals(certQualificationOverrule)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QC_STATEMENT, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    certQualificationOverrule = true;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/NotQualified":
                    if (Boolean.TRUE.equals(certQualificationOverrule)) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QC_STATEMENT, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return false;
                    }
                    certQualificationOverrule = false;
                    break;
            }
        }
        return certQualificationOverrule;
    }

    private CertificateType parseCertTypeOverrule(List<String> applicableQualifiers, String trustedCertificateName) {
        CertificateType certTypeOverrule = CertificateType.UNDEFINED;
        for (String applicableQualifier : applicableQualifiers) {
            switch (applicableQualifier) {
                // The following Sie:Q:* statements are mutually exclusive and will raise an error:
                // - QcForeSig, QcForeSeal, QcForWSA for the same sigCert under consideration.
                // - QcForLegalPerson, QcForeSig for the same sigCert under consideration.
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESig":
                    if (certTypeOverrule == CertificateType.E_SEAL) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QC_FOR, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return CertificateType.INCOHERENT;
                    }
                    certTypeOverrule = CertificateType.E_SIG;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForESeal":
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForLegalPerson":
                    if (certTypeOverrule == CertificateType.E_SIG) {
                        addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                CONTRADICTING_QC_FOR, trustedCertificateName),
                                ReportItem.ReportItemStatus.INFO));
                        return CertificateType.INCOHERENT;
                    }
                    certTypeOverrule = CertificateType.E_SEAL;
                    break;
                case "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForWSA":
                    addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                            QC_WSA, trustedCertificateName), ReportItem.ReportItemStatus.INFO));
                    return CertificateType.WSA;
            }
        }
        return certTypeOverrule;
    }

    private CertificateType parseTypeFromCertificate(List<IQCStatement> qcStatements, X509Certificate certificate) {
        CertificateType certType = CertificateType.UNDEFINED;
        for (IQCStatement qcStatement : qcStatements) {
            if (QUALIFIED_TYPE_EXTENSION.equals(qcStatement.getStatementId().getId())) {
                IASN1Encodable qcType = qcStatement.getStatementInfo();
                IASN1Sequence typeSequence = FACTORY.createASN1Sequence(qcType);
                List<String> typeIds = Arrays.stream(typeSequence.toArray())
                        .map(type -> FACTORY.createASN1ObjectIdentifier(type).getId()).collect(Collectors.toList());
                for (String typeId : typeIds) {
                    switch (typeId) {
                        case ESIG_TYPE_EXTENSION:
                            if (certType != CertificateType.UNDEFINED && certType != CertificateType.E_SIG) {
                                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                        CONTRADICTING_QC_TYPE, certificate.getSubjectX500Principal()),
                                        ReportItem.ReportItemStatus.INFO));
                                return CertificateType.INCOHERENT;
                            }
                            certType = CertificateType.E_SIG;
                            break;
                        case ESEAL_TYPE_EXTENSION:
                            if (certType != CertificateType.UNDEFINED && certType != CertificateType.E_SEAL) {
                                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                        CONTRADICTING_QC_TYPE, certificate.getSubjectX500Principal()),
                                        ReportItem.ReportItemStatus.INFO));
                                return CertificateType.INCOHERENT;
                            }
                            certType = CertificateType.E_SEAL;
                            break;
                        case WSA_TYPE_EXTENSION:
                            addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                                    QC_TYPE_WSA, certificate.getSubjectX500Principal()),
                                    ReportItem.ReportItemStatus.INFO));
                            return CertificateType.WSA;
                    }
                }
            }
        }
        return certType;
    }

    private FinalQualificationData isCertificateQualified(X509Certificate certificate, String trustedCertificateName,
                                                          CountryServiceContext countryServiceContext, Date date) {
        ServiceChronologicalInfo chronologicalInfo =
                countryServiceContext.getServiceChronologicalInfoByDate(DateTimeUtil.getRelativeTime(date));
        boolean isBeforeEIDAS = date.before(EIDAS);

        List<QualifierExtension> qualifiers = chronologicalInfo.getQualifierExtensions();
        List<String> applicableQualifiers = new ArrayList<>();
        for (QualifierExtension qualifier : qualifiers) {
            if (qualifier.checkCriteria(certificate)) {
                applicableQualifiers.addAll(qualifier.getQualifiers());
            }
        }

        CertificateType certTypeOverrule = CertificateType.UNDEFINED;
        // QCForXX is ignored before eIDAS, as the only type existing before eIDAS is for electronic signature
        if (!isBeforeEIDAS) {
            certTypeOverrule = parseCertTypeOverrule(applicableQualifiers, trustedCertificateName);
        }
        if (certTypeOverrule == CertificateType.WSA) {
            return new FinalQualificationData(false, false, certTypeOverrule);
        }
        Boolean certQualificationOverrule =
                parseCertificateQualificationOverrule(applicableQualifiers, trustedCertificateName);
        Boolean qualifiedCreationDeviceOverrule =
                parseQualifiedCreationDeviceOverrule(applicableQualifiers, trustedCertificateName, isBeforeEIDAS);

        // Get type, qualification and creation device values from the certificate.
        byte[] qcStatementsExtensionValue =
                CertificateUtil.getExtensionValueByOid(certificate, QC_STATEMENTS_EXTENSION);
        List<IQCStatement> qcStatements = null;
        try {
            qcStatements = FACTORY.parseQcStatement(qcStatementsExtensionValue);
        } catch (Exception e) {
            addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                    EXCEPTION_STATEMENT_PARSING, certificate.getSubjectX500Principal()), e,
                    ReportItem.ReportItemStatus.INFO));
        }

        Boolean certificateQualification = null;
        Boolean qualifiedCreationDevice = null;
        CertificateType certType = CertificateType.UNDEFINED;

        if (qcStatements != null) {
            for (IQCStatement qcStatement : qcStatements) {
                switch (qcStatement.getStatementId().getId()) {
                    case QC_COMPLIANCE_EXTENSION:
                        certificateQualification = true;
                        break;
                    case QSCD_EXTENSION:
                        qualifiedCreationDevice = true;
                        break;
                }
            }
            certType = parseTypeFromCertificate(qcStatements, certificate);
        }
        if (certType == CertificateType.WSA) {
            return new FinalQualificationData(false, false, certType);
        }
        if (isBeforeEIDAS) {
            try {
                byte[] policyIdExtension =
                        CertificateUtil.getExtensionValueByOid(certificate, CERTIFICATE_POLICIES_EXTENSION);
                if (policyIdExtension != null) {
                    List<String> policyIds = FACTORY.getPoliciesIds(policyIdExtension);
                    for (String policyId : policyIds) {
                        if (QCP_EXTENSION.equals(policyId)) {
                            certificateQualification = true;
                        }
                        if (QCP_PLUS_EXTENSION.equals(policyId)) {
                            certificateQualification = true;
                            qualifiedCreationDevice = true;
                        }
                    }
                }
            } catch (Exception e) {
                addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                        EXCEPTION_STATEMENT_PARSING, certificate.getSubjectX500Principal()), e,
                        ReportItem.ReportItemStatus.INFO));
            }
        }

        boolean finalQualification;
        boolean finalQualifiedCreationDevice;
        CertificateType finalCertType;

        if (certQualificationOverrule != null) {
            finalQualification = (boolean) certQualificationOverrule;
        } else if (certificateQualification != null) {
            finalQualification = (boolean) certificateQualification;
        } else {
            finalQualification = false;
        }

        if (qualifiedCreationDeviceOverrule != null) {
            finalQualifiedCreationDevice = (boolean) qualifiedCreationDeviceOverrule;
        } else if (qualifiedCreationDevice != null) {
            finalQualifiedCreationDevice = (boolean) qualifiedCreationDevice;
        } else {
            finalQualifiedCreationDevice = false;
        }

        if (certTypeOverrule != CertificateType.UNDEFINED && finalQualification) {
            finalCertType = certTypeOverrule;
        } else if (certType != CertificateType.UNDEFINED) {
            finalCertType = certType;
        } else if (Boolean.TRUE.equals(certificateQualification)) {
            // QcCompliance in the absence of QcType (and in the absence of overruling in the TL)
            // shall lead to conclude that the sigCert is QC for eSig.
            finalCertType = CertificateType.E_SIG;
        } else {
            finalCertType = CertificateType.UNDEFINED;
        }

        // Check Service Information Extension compliance.
        if (!isBeforeEIDAS) {
            List<String> serviceExtensions = chronologicalInfo.getServiceExtensions().stream().map(
                    serviceExtension -> serviceExtension.getUri()).collect(Collectors.toList());
            if (finalCertType == CertificateType.E_SEAL) {
                if (!serviceExtensions.contains(AdditionalServiceInformationExtension.FOR_E_SEALS)) {
                    addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                            TYPE_CONTRADICTS_WITH_SI, certificate.getSubjectX500Principal()),
                            ReportItem.ReportItemStatus.INFO));
                    currentValidationData.qualificationConclusion = QualificationConclusion.NOT_CATCHING;
                    finalQualification = false;
                }
            } else if (finalCertType == CertificateType.E_SIG || finalCertType == CertificateType.UNDEFINED) {
                if (!serviceExtensions.contains(AdditionalServiceInformationExtension.FOR_E_SIGNATURES)) {
                    addReportItem(new ReportItem(QUALIFICATION_CHECK, MessageFormatUtil.format(
                            TYPE_CONTRADICTS_WITH_SI, certificate.getSubjectX500Principal()),
                            ReportItem.ReportItemStatus.INFO));
                    currentValidationData.qualificationConclusion = QualificationConclusion.NOT_CATCHING;
                    finalQualification = false;
                }
            }
        }

        if (!checkServiceStatus(chronologicalInfo, isBeforeEIDAS, trustedCertificateName)) {
            finalQualification = false;
        }

        return new FinalQualificationData(finalQualification, finalQualifiedCreationDevice, finalCertType);
    }

    private void updateSignatureQualification() {
        if (signatureValidationData == null) {
            throw new PdfException(SignExceptionMessageConstant.SIGNATURE_NAME_NOT_PROVIDED);
        }
        // We only update overall qualification validation results, if previous result was "NOT_CATCHING".
        // In all the other cases results contradict, and therefore the overall result is INCOHERENT.
        if (signatureValidationData.qualificationConclusion == null ||
                signatureValidationData.qualificationConclusion == QualificationConclusion.NOT_CATCHING) {
            signatureValidationData.qualificationConclusion = currentValidationData.qualificationConclusion;
            signatureValidationData.validationReport = currentValidationData.validationReport;
        } else if (currentValidationData.qualificationConclusion != QualificationConclusion.NOT_CATCHING &&
                currentValidationData.qualificationConclusion != signatureValidationData.qualificationConclusion) {
            signatureValidationData.qualificationConclusion = QualificationConclusion.INCOHERENT;
            signatureValidationData.validationReport.merge(currentValidationData.validationReport);
            signatureValidationData.validationReport.addReportItem(
                    new ReportItem(QUALIFICATION_CHECK, MULTIPLE_CA_QC_ENTRIES, ReportItem.ReportItemStatus.INFO));
        }
    }

    private void addReportItem(ReportItem reportItem) {
        currentValidationData.getValidationReport().addReportItem(reportItem);
    }

    /**
     * Qualification validation data containing {@link QualificationConclusion} and {@link ValidationReport}.
     */
    public static class QualificationValidationData {
        QualificationConclusion qualificationConclusion;
        ValidationReport validationReport = new ValidationReport();

        QualificationValidationData() {
            // Empty constructor.
        }

        /**
         * Gets {@link QualificationConclusion} for this {@link QualificationValidationData}.
         *
         * @return {@link QualificationConclusion}
         */
        public QualificationConclusion getQualificationConclusion() {
            return qualificationConclusion == null ||
                    qualificationConclusion == QualificationConclusion.NOT_CATCHING ||
                    qualificationConclusion == QualificationConclusion.INCOHERENT
                    ? QualificationConclusion.NOT_APPLICABLE : qualificationConclusion;
        }

        /**
         * Gets {@link ValidationReport} for this {@link QualificationValidationData}.
         *
         * @return {@link ValidationReport}
         */
        public ValidationReport getValidationReport() {
            return validationReport;
        }
    }

    /**
     * Enum representing possible signature qualification conclusions.
     */
    public enum QualificationConclusion {
        /**
         * Electronic Signature with Qualified Signing Certificate,
         * which private key resides in Qualified Signature Creation Device.
         */
        ESIG_WITH_QC_AND_QSCD,
        /**
         * Electronic Seal with Qualified Signing Certificate,
         * which private key resides in Qualified Signature Creation Device.
         */
        ESEAL_WITH_QC_AND_QSCD,
        /**
         * Electronic Signature with Qualified Signing Certificate.
         */
        ESIG_WITH_QC,
        /**
         * Electronic Seal with Qualified Signing Certificate.
         */
        ESEAL_WITH_QC,
        /**
         * Not qualified Electronic Signature.
         */
        NOT_QUALIFIED_ESIG,
        /**
         * Not qualified Electronic Seal.
         */
        NOT_QUALIFIED_ESEAL,
        /**
         * Signature of an unknown type with Qualified Signing Certificate,
         * which private key resides in Qualified Signature Creation Device.
         */
        UNKNOWN_QC_AND_QSCD,
        /**
         * Signature of an unknown type with Qualified Signing Certificate.
         */
        UNKNOWN_QC,
        /**
         * Signature of an unknown type.
         */
        UNKNOWN,
        /**
         * Signature, which properties cannot be established, because the corresponding values contradict.
         */
        INCOHERENT,
        /**
         * Signature, for which qualification status is not applicable.
         */
        NOT_APPLICABLE,
        /**
         * Signature, for which there is not corresponding TL entry.
         */
        NOT_CATCHING,
        /**
         * Not qualified signature.
         */
        NOT_QUALIFIED
    }

    private static class FinalQualificationData {
        final boolean finalQualification;
        final boolean finalQualifiedCreationDevice;
        final CertificateType finalCertType;

        FinalQualificationData(boolean finalQualification, boolean finalQualifiedCreationDevice,
                               CertificateType finalCertType) {
            this.finalQualification = finalQualification;
            this.finalQualifiedCreationDevice = finalQualifiedCreationDevice;
            this.finalCertType = finalCertType;
        }
    }

    private enum CertificateType {
        E_SIG,
        E_SEAL,
        WSA,
        UNDEFINED,
        INCOHERENT
    }
}
