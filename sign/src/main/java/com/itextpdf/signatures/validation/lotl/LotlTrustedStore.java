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
        Map<String, Set<CertificateSource>> tempServiceTypeIdentifiersScope = new HashMap<>();
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/CA/QC", crlOcspSignScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/CA/PKC", crlOcspSignScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC", ocspScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC", crlScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PSES/Q", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RemoteQSigCDManagement/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RemoteQSealCDManagement/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EAA/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Ledgers/Q", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP", crlScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL", crlScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TS/", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PSES", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/AdESValidation", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/AdESGeneration", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RemoteSigCDManagemen", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RemoteSealCDManagement", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EAA", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Ledgers", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PKCValidation", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PKCPreservation", timestampScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EAAValidation", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TSTValidation", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EDSValidation", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/EAA/Pub-EAA", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PKCValidation/CertsforOtherTypesOfTS",
                signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RA", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/IdV", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavingPKIid", signScope);
        tempServiceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/TLIssuer", signScope);
        serviceTypeIdentifiersScope = Collections.unmodifiableMap(tempServiceTypeIdentifiersScope);
    }

    /**
     * Creates new instance of {@link LotlTrustedStore}. This constructor shall not be used directly.
     * Instead, in order to create such instance {@link ValidatorChainBuilder#getLotlTrustedstore()} shall be used.
     *
     * @param builder {@link ValidatorChainBuilder} which was responsible for creation
     */
    public LotlTrustedStore(ValidatorChainBuilder builder) {
        if (builder.getLotlFetchingProperties() != null) {
            LotlValidator lotlValidator = builder.getLotlValidator();
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

            Set<CertificateSource> currentScope = serviceTypeIdentifiersScope.get(currentContext.getServiceType());
            if (currentScope == null) {
                validationReportItems.add(new CertificateReportItem(certificate,
                        CERTIFICATE_CHECK, MessageFormatUtil.format(CERTIFICATE_SERVICE_TYPE_NOT_RECOGNIZED,
                        certificate.getSubjectX500Principal(), currentContext.getServiceType()),
                        ReportItem.ReportItemStatus.INFO));
                continue;
            }
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

        for (ReportItem reportItem : validationReportItems) {
            result.addReportItem(reportItem);
        }

        return false;
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

    /**
     * Check if scope specified by extensions contains valid types.
     *
     * @param reportItems {@link ValidationReport} which is populated with detailed validation results
     * @param certificate {@link X509Certificate} to be validated
     * @param extensions  {@link AdditionalServiceInformationExtension} that specify scope
     *
     * @return false if extensions specify scope only with invalid types.
     */
    boolean isScopeCorrectlySpecified(List<ReportItem> reportItems, X509Certificate certificate,
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
