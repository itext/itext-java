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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * Validator class responsible for XML signature validation.
 * This class is not intended to be used to validate anything besides Lotl files.
 */
public class XmlSignatureValidator {
    static final String XML_SIGNATURE_VERIFICATION = "XML Signature verification check.";
    static final String XML_SIGNATURE_VERIFICATION_EXCEPTION =
            "XML Signature verification threw exception. Validation wasn't successful.";
    static final String NO_CERTIFICATE =
            "XML signing certificate wasn't find in the document. Validation wasn't successful.";
    static final String XML_SIGNATURE_VERIFICATION_FAILED =
            "XML Signature verification wasn't successful. Signature is invalid.";
    static final String CERTIFICATE_TRUSTED =
            "Certificate {0} is trusted. Validation is successful.";
    static final String CERTIFICATE_NOT_TRUSTED =
            "Certificate {0} is NOT trusted. Validation isn't successful.";
    private final TrustedCertificatesStore trustedCertificatesStore;

    /**
     * Creates {@link XmlSignatureValidator} instance. This constructor shall not be used directly.
     * Instead, in order to create such instance {@link ValidatorChainBuilder#getXmlSignatureValidator()} shall be used.
     *
     * @param trustedCertificatesStore {@link TrustedCertificatesStore} which contains trusted certificates
     */
    public XmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        this.trustedCertificatesStore = trustedCertificatesStore;
    }

    /**
     * Validates provided XML Lotl file.
     *
     * @param xmlDocumentInputStream {@link InputStream} representing XML Lotl file to be validated
     *
     * @return {@link ValidationReport} containing all validation related information
     */
    protected ValidationReport validate(InputStream xmlDocumentInputStream) {
        ValidationReport report = new ValidationReport();
        CertificateSelector keySelector = new CertificateSelector();
        try {
            boolean coreValidity =
                    XmlValidationUtils.createXmlDocumentAndCheckValidity(xmlDocumentInputStream, keySelector);
            if (!coreValidity) {
                report.addReportItem(new ReportItem(
                        XML_SIGNATURE_VERIFICATION, XML_SIGNATURE_VERIFICATION_FAILED, ReportItemStatus.INVALID));
            }
        } catch (Exception e) {
            report.addReportItem(new ReportItem(
                    XML_SIGNATURE_VERIFICATION, XML_SIGNATURE_VERIFICATION_EXCEPTION, e, ReportItemStatus.INVALID));
        }
        if (report.getValidationResult() == ValidationReport.ValidationResult.INVALID) {
            return report;
        }

        if (keySelector.getCertificate() == null) {
            report.addReportItem(new ReportItem(XML_SIGNATURE_VERIFICATION, NO_CERTIFICATE, ReportItemStatus.INVALID));
            return report;
        }
        X509Certificate certificate = keySelector.getCertificate();
        if (trustedCertificatesStore.isCertificateGenerallyTrusted(certificate)) {
            report.addReportItem(new CertificateReportItem(certificate, XML_SIGNATURE_VERIFICATION,
                    MessageFormatUtil.format(CERTIFICATE_TRUSTED, certificate.getSubjectX500Principal()),
                    ReportItemStatus.INFO));
        } else {
            report.addReportItem(new CertificateReportItem(certificate, XML_SIGNATURE_VERIFICATION,
                    MessageFormatUtil.format(CERTIFICATE_NOT_TRUSTED, certificate.getSubjectX500Principal()),
                    ReportItemStatus.INVALID));
        }
        return report;
    }
}
