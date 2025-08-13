/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at http://itextpdf.com/sales.  For AGPL licensing, see below.

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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.SafeCallingAvoidantException;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Trusted certificates storage class for country specific Lotl trusted certificates.
 */
public class LotlTrustedStore {
    static final String REVOKED_CERTIFICATE = "Certificate {0} is revoked.";
    static final String CERTIFICATE_CHECK = "Certificate check.";
    static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted, revocation data checks are not required.";
    static final String CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT = "Certificate {0} is trusted for {1}, "
            + "but it is not used in this context. Validation will continue as usual.";
    static final String CERTIFICATE_SERVICE_TYPE_NOT_RECOGNIZED =
            "Certificate {0} is trusted, but it's service type {1} is not recognized.";
    static final String NOT_YET_VALID_CERTIFICATE = "Certificate {0} is not yet valid.";
    static final String SCOPE_SPECIFIED_WITH_INVALID_TYPES = "Certificate {0} is trusted for {1}, " +
            "which is incorrect scope for pdf validation.";
    static final String EXTENSIONS_CHECK = "Certificate extensions check.";

    private static final Map<String, Set<CertificateSource>> serviceTypeIdentifiersScope;
    private final Set<CountryServiceContext> contexts = new HashSet<>();
    private final ValidationReport report;

    static {
        Set<CertificateSource> crlOcspSignScope = new HashSet<>();
        crlOcspSignScope.add(CertificateSource.CRL_ISSUER);
        crlOcspSignScope.add(CertificateSource.OCSP_ISSUER);
        crlOcspSignScope.add(CertificateSource.SIGNER_CERT);
        Set<CertificateSource> ocspScope =
                new HashSet<>(Collections.singletonList(CertificateSource.OCSP_ISSUER));
        Set<CertificateSource> crlScope =
                new HashSet<>(Collections.singletonList(CertificateSource.CRL_ISSUER));
        Set<CertificateSource> timestampScope =
                new HashSet<>(Collections.singletonList(CertificateSource.TIMESTAMP));
        Set<CertificateSource> signScope =
                new HashSet<>(Collections.singletonList(CertificateSource.SIGNER_CERT));
        Map<String, Set<CertificateSource>> tempServiceTypeIdentifiersScope =
                new HashMap<>(ServiceTypeIdentifiersConstants.getAllValues().size());
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.CA_QC, crlOcspSignScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.CA_PKC, crlOcspSignScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.OCSP_QC, ocspScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.CRL_QC, crlScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TSA_QTST, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EDS_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.REM_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PSES_Q, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.QES_VALIDATION_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.REMOTE_Q_SIG_CD_MANAGEMENT_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.REMOTE_Q_SEAL_CD_MANAGEMENT_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EAA_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ELECTRONIC_ARCHIVING_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.LEDGERS_Q, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.OCSP, crlScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.CRL, crlScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TS, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TSA_TSS_QC, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TSA_TSS_ADES_Q_CAND_QES, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PSES, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ADES_VALIDATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ADES_GENERATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.REMOTE_SIG_CD_MANAGEMENT, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.REMOTE_SEAL_CD_MANAGEMENT, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EAA, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ELECTRONIC_ARCHIVING, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.LEDGERS, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PKC_VALIDATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PKC_PRESERVATION, timestampScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EAA_VALIDATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TST_VALIDATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EDS_VALIDATION, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.EAA_PUB_EAA, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.CERTS_FOR_OTHER_TYPES_OF_TS,
                signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.RA, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.RA_NOT_HAVING_PKI_ID, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.SIGNATURE_POLICY_AUTHORITY, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ARCHIV, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ARCHIV_NOT_HAVING_PKI_ID, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.ID_V, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.K_ESCROW, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.K_ESCROW_NOT_HAVING_PKI_ID, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PP_WD, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.PP_WD_NOT_HAVING_PKI_ID, signScope);
        tempServiceTypeIdentifiersScope.put(ServiceTypeIdentifiersConstants.TL_ISSUER, signScope);
        serviceTypeIdentifiersScope = Collections.unmodifiableMap(tempServiceTypeIdentifiersScope);
    }

    /**
     * Creates new instance of {@link LotlTrustedStore}. This constructor shall not be used directly.
     * Instead, in order to create such instance {@link ValidatorChainBuilder#getLotlTrustedStore()} shall be used.
     *
     * @param builder {@link ValidatorChainBuilder} which was responsible for creation
     */
    public LotlTrustedStore(ValidatorChainBuilder builder) {
        if (builder.isEuropeanLotlTrusted()) {
            LotlService lotlService = builder.getLotlService();
            if (lotlService == null || !lotlService.isCacheInitialized()) {
                throw new SafeCallingAvoidantException(SignExceptionMessageConstant.CACHE_NOT_INITIALIZED);
            }
            LotlValidator lotlValidator = builder.getLotlService().getLotlValidator();
            this.report = lotlValidator.validate();
            if (report.getValidationResult() == ValidationResult.VALID) {
                addCertificatesWithContext(mapIServiceContextToCountry(lotlValidator.getNationalTrustedCertificates()));
            }
        } else {
            this.report = new ValidationReport();
        }
    }

    /**
     * Gets all the certificates stored in this trusted store.
     *
     * @return {@link Certificate} stored
     */
    public Set<Certificate> getCertificates() {
        Set<Certificate> allCertificates = new HashSet<>();
        for (CountryServiceContext context : contexts) {
            allCertificates.addAll(context.getCertificates());
        }

        return allCertificates;
    }

    /**
     * Checks if given certificate is trusted according to context and time in which it is used.
     *
     * @param result         {@link ValidationReport} which stores check results
     * @param context        {@link ValidationContext} in which certificate is used
     * @param certificate    {@link X509Certificate} certificate to be checked
     * @param validationDate {@link Date} date time in which certificate is validated
     *
     * @return {@code true} if certificate is trusted, {@code false} otherwise
     */
    public boolean checkIfCertIsTrusted(ValidationReport result, ValidationContext context,
            X509Certificate certificate, Date validationDate) {
        Set<CountryServiceContext> currentContextSet = getCertificateContext(certificate);
        result.mergeWithDifferentStatus(report, ReportItemStatus.INFO);

        List<ReportItem> validationReportItems = new ArrayList<>();
        for (CountryServiceContext currentContext : currentContextSet) {
            ServiceChronologicalInfo chronologicalInfo = getCertificateChronologicalInfoByTime(validationReportItems,
                    certificate, currentContext, validationDate);
            if (chronologicalInfo == null ||
                    !isScopeCorrectlySpecified(validationReportItems, certificate, chronologicalInfo.getExtensions())) {
                continue;
            }

            Set<CertificateSource> currentScope =
                    getCertificateSourceBasedOnServiceType(currentContext.getServiceType());
            if (currentScope == null) {
                validationReportItems.add(new CertificateReportItem(certificate,
                        CERTIFICATE_CHECK, MessageFormatUtil.format(CERTIFICATE_SERVICE_TYPE_NOT_RECOGNIZED,
                        certificate.getSubjectX500Principal(), currentContext.getServiceType()),
                        ReportItem.ReportItemStatus.INFO));
            } else {
                for (CertificateSource source : currentScope) {
                    if (ValidationContext.checkIfContextChainContainsCertificateSource(context, source)) {
                        result.addReportItem(new CertificateReportItem(certificate,
                                CERTIFICATE_CHECK, MessageFormatUtil.format(
                                CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()),
                                ReportItem.ReportItemStatus.INFO));
                        return true;
                    } else {
                        validationReportItems.add(new CertificateReportItem(certificate,
                                CERTIFICATE_CHECK, MessageFormatUtil.format(CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
                                certificate.getSubjectX500Principal(), currentContext.getServiceType()),
                                ReportItem.ReportItemStatus.INFO));
                    }
                }
            }
        }

        for (ReportItem reportItem : validationReportItems) {
            result.addReportItem(reportItem);
        }

        return false;
    }

    /**
     * Gets set of {@link CertificateSource} items based on service type identifier of a given certificate in LOTL file.
     * <p>
     * Certificate source defines in which context this certificate is supposed to be trusted.
     *
     * @param serviceType {@link String} representing service type identifier field in LOTL file.
     *
     * @return set of {@link CertificateSource} representing contexts, in which certificate is supposed to be trusted.
     */
    protected Set<CertificateSource> getCertificateSourceBasedOnServiceType(String serviceType) {
        return serviceTypeIdentifiersScope.get(serviceType);
    }

    /**
     * Checks if scope specified by extensions contains valid types.
     *
     * @param reportItems {@link ValidationReport} which is populated with detailed validation results
     * @param certificate {@link X509Certificate} to be validated
     * @param extensions  {@link AdditionalServiceInformationExtension} that specify scope
     *
     * @return false if extensions specify scope only with invalid types.
     */
    protected boolean isScopeCorrectlySpecified(List<ReportItem> reportItems, X509Certificate certificate,
            List<AdditionalServiceInformationExtension> extensions) {
        List<ReportItem> currentReportItems = new ArrayList<>();
        for (AdditionalServiceInformationExtension extension : extensions) {
            if (extension.isScopeValid()) {
                return true;
            } else {
                currentReportItems.add(new CertificateReportItem(certificate, EXTENSIONS_CHECK,
                        MessageFormatUtil.format(SCOPE_SPECIFIED_WITH_INVALID_TYPES,
                                certificate.getSubjectX500Principal(), extension.getUri()),
                        ReportItem.ReportItemStatus.INVALID));
            }
        }

        if (currentReportItems.isEmpty()) {
            return true;
        } else {
            reportItems.addAll(currentReportItems);
            return false;
        }
    }

    static List<CountryServiceContext> mapIServiceContextToCountry(List<IServiceContext> serviceContexts) {
        List<CountryServiceContext> list = new ArrayList<>();
        for (IServiceContext serviceContext : serviceContexts) {
            CountryServiceContext countryServiceContext = serviceContext instanceof CountryServiceContext ?
                    (CountryServiceContext) serviceContext : null;
            if (countryServiceContext != null) {
                list.add(countryServiceContext);
            }
        }
        return list;
    }

    final void addCertificatesWithContext(Collection<CountryServiceContext> contexts) {
        this.contexts.addAll(contexts);
    }

    /**
     * Find {@link ServiceChronologicalInfo} corresponding to provided date. If Service wasn't operating at that date
     * report item will be added and null will be returned.
     *
     * @param reportItems    {@link ValidationReport} which is populated with detailed validation results
     * @param certificate    {@link X509Certificate} to be validated
     * @param currentContext {@link CountryServiceContext} which contains statuses and their starting time
     * @param validationDate {@link Date} against which certificate is expected to be validated. Usually signing
     *                       date
     *
     * @return {@link ServiceChronologicalInfo} which contains time specific service information.
     */
    ServiceChronologicalInfo getCertificateChronologicalInfoByTime(
            List<ReportItem> reportItems, X509Certificate certificate, CountryServiceContext currentContext,
            Date validationDate) {
        ServiceChronologicalInfo status = currentContext.getServiceChronologicalInfoByDate(
                DateTimeUtil.getRelativeTime(validationDate));
        if (status == null) {
            reportItems.add(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                    MessageFormatUtil.format(NOT_YET_VALID_CERTIFICATE,
                            certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INVALID));
            return null;
        }

        if (!ServiceChronologicalInfo.isStatusValid(status.getServiceStatus())) {
            reportItems.add(new CertificateReportItem(certificate, CERTIFICATE_CHECK,
                    MessageFormatUtil.format(REVOKED_CERTIFICATE, certificate.getSubjectX500Principal()),
                    ReportItem.ReportItemStatus.INVALID));
            return null;
        }

        return status;
    }

    private Set<CountryServiceContext> getCertificateContext(Certificate certificate) {
        Set<CountryServiceContext> contextSet = new HashSet<>();
        for (CountryServiceContext context : contexts) {
            if (context.getCertificates().contains(certificate)) {
                contextSet.add(context);
            }
        }
        return contextSet;
    }
}
