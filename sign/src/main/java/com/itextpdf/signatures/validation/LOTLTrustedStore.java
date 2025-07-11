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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;

/**
 * Trusted certificates storage class for LOTL trusted certificates.
 */
class   LOTLTrustedStore {

    private final Set<CountryServiceContext> contexts = new HashSet<>();
    private static final Map<String, Set<CertificateSource>> serviceTypeIdentifiersScope = new HashMap<>();

    static final String REVOKED_CERTIFICATE = "Certificate {0} is revoked.";

    static {
        Set<CertificateSource> crlOcspSignScope = new HashSet<>();
        crlOcspSignScope.add(CertificateSource.CRL_ISSUER);
        crlOcspSignScope.add(CertificateSource.OCSP_ISSUER);
        crlOcspSignScope.add(CertificateSource.SIGNER_CERT);
        Set<CertificateSource> ocspScope =
                new HashSet<>(Collections.<CertificateSource>singletonList(CertificateSource.OCSP_ISSUER));
        Set<CertificateSource> crlScope =
                new HashSet<>(Collections.<CertificateSource>singletonList(CertificateSource.CRL_ISSUER));
        Set<CertificateSource> timestampScope =
                new HashSet<>(Collections.<CertificateSource>singletonList(CertificateSource.TIMESTAMP));
        Set<CertificateSource> signScope =
                new HashSet<>(Collections.<CertificateSource>singletonList(CertificateSource.SIGNER_CERT));
        serviceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/CA/QC", crlOcspSignScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/CA/PKC/", crlOcspSignScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC/", ocspScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC/", crlScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TSA/QTST/", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EDS/Q/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PSES/Q/", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/RemoteQSigCDManagement/Q", signScope);
        serviceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/RemoteQSealCDManagement/Q", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EAA/Q", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving/Q", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Ledgers/Q", signScope);
        serviceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP", crlScope);
        serviceTypeIdentifiersScope.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL", crlScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TSA/", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC/", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES/", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PSES/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/AdESValidation/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/AdESGeneration/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/RemoteSigCDManagemen", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/RemoteSealCDManagement", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EAA", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/ElectronicArchiving", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Ledgers", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PKCValidation", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PKCPreservation", timestampScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EAAValidation", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TSTValidation", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EDSValidation", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/EAA/Pub-EAA", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PKCValidation/CertsforOtherTypesOfTS",
                signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/RA/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Archiv/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/IdV/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/KEscrow/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PPwd/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavingPKIid/", signScope);
        serviceTypeIdentifiersScope.put("https://uri.etsi.org/TrstSvc/Svctype/TLIssuer/", signScope);

    }

    LOTLTrustedStore() {
        //empty constructor
    }

    Set<Certificate> getCertificates() {
        Set<Certificate> allCertificates = new HashSet<>();
        for (CountryServiceContext context : contexts) {
            allCertificates.addAll(context.getCertificates());
        }


        return allCertificates;
    }

    void addCertificatesWithContext(Collection<CountryServiceContext> contexts) {
        this.contexts.addAll(contexts);
    }

    boolean checkIfCertIsTrusted(ValidationReport result, ValidationContext context,
                                 X509Certificate certificate, Date validationDate) {
        Set<CountryServiceContext> currentContextSet = getCertificateContext(certificate);
        if (currentContextSet.isEmpty()) {
            return false;
        }

        List<ReportItem>  validationReportItems = new ArrayList<>();
        for (CountryServiceContext currentContext : currentContextSet) {
            if (!isCertificateValidInTime(validationReportItems, certificate, currentContext, validationDate)) {
                continue;
            }

            Set<CertificateSource> currentScope = serviceTypeIdentifiersScope.get(currentContext.getServiceType());
            for (CertificateSource source : currentScope) {
                if (ValidationContext.checkIfContextChainContainsCertificateSource(context, source)) {
                    result.addReportItem(new CertificateReportItem(certificate,
                            CertificateChainValidator.CERTIFICATE_CHECK, MessageFormatUtil.format(
                            CertificateChainValidator.CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()),
                            ReportItem.ReportItemStatus.INFO));
                    return true;
                } else {
                    validationReportItems.add(new CertificateReportItem(certificate,
                            CertificateChainValidator.CERTIFICATE_CHECK, MessageFormatUtil.format(
                                    CertificateChainValidator.CERTIFICATE_TRUSTED_FOR_DIFFERENT_CONTEXT,
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

    boolean isCertificateValidInTime(List<ReportItem> reportItems, X509Certificate certificate,
                                     CountryServiceContext currentContext, Date validationDate) {
        String status = currentContext.getServiceStatusByDate(DateTimeUtil.getRelativeTime(validationDate));
        if (status == null) {
            reportItems.add(new CertificateReportItem(certificate, CertificateChainValidator.VALIDITY_CHECK,
                    MessageFormatUtil.format(CertificateChainValidator.NOT_YET_VALID_CERTIFICATE,
                            certificate.getSubjectX500Principal()), ReportItem.ReportItemStatus.INVALID));
            return false;
        }

        if (!ServiceStatusInfo.isStatusValid(status)) {
            reportItems.add(new CertificateReportItem(certificate, CertificateChainValidator.VALIDITY_CHECK,
                    MessageFormatUtil.format(REVOKED_CERTIFICATE, certificate.getSubjectX500Principal()),
                    ReportItem.ReportItemStatus.INVALID));
            return false;
        }

        return true;
    }

    Set<CountryServiceContext> getCertificateContext(Certificate certificate) {
        Set<CountryServiceContext> contextSet = new HashSet<>();
        for (CountryServiceContext context : contexts) {
            if (context.getCertificates().contains(certificate)) {
                contextSet.add(context);
            }
        }

        return contextSet;
    }
}
