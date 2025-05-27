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
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.InputStream;

class XmlSignatureValidator {
    static final String XML_SIGNATURE_VERIFICATION = "XML Signature verification check.";
    static final String XML_SIGNATURE_VERIFICATION_EXCEPTION =
            "XML Signature verification threw exception. Validation wasn't successful.";
    static final String NO_CERTIFICATE =
            "XML signing certificate wasn't find in the document. Validation wasn't successful.";
    static final String XML_SIGNATURE_VERIFICATION_FAILED =
            "XML Signature verification wasn't successful. Signature is invalid.";
    private final CertificateChainValidator certificateChainValidator;
    private final SignatureValidationProperties properties;
    private final ValidationContext context;

    XmlSignatureValidator(ValidatorChainBuilder builder) {
        this.certificateChainValidator = builder.getCertificateChainValidator();
        this.properties = builder.getProperties();
        this.context = new ValidationContext(
                ValidatorContext.XML_SIGNATURE_VALIDATOR, CertificateSource.LOTL_CERT, TimeBasedContext.PRESENT);
    }

    ValidationReport validate(InputStream xmlDocumentInputStream) {
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
        if (stopValidation(report, context)) {
            return report;
        }

        if (keySelector.getCertificate() == null) {
            report.addReportItem(new ReportItem(XML_SIGNATURE_VERIFICATION, NO_CERTIFICATE, ReportItemStatus.INVALID));
            return report;
        }
        certificateChainValidator.validate(
                report, context, keySelector.getCertificate(), DateTimeUtil.getCurrentTimeDate());
        return report;
    }

    private boolean stopValidation(ValidationReport result, ValidationContext context) {
        return !properties.getContinueAfterFailure(context)
                && result.getValidationResult() == ValidationReport.ValidationResult.INVALID;
    }
}
