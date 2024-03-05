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

import com.itextpdf.signatures.validation.ValidationReport.ValidationResult;

import java.security.cert.X509Certificate;

/**
 * Report item to be used for single certificate related failure or log message.
 */
public class CertificateReportItem extends ReportItem {
    private final X509Certificate certificate;

    /**
     * Create {@link ReportItem} instance.
     *
     * @param certificate {@link X509Certificate} processing which report item occurred
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message {@link String} with the exact report item message
     * @param result {@link ValidationResult}, which this report item leads to
     */
    public CertificateReportItem(X509Certificate certificate, String checkName, String message,
            ValidationResult result) {
        this(certificate, checkName, message, null, result);
    }

    /**
     * Create {@link ReportItem} instance.
     *
     * @param certificate {@link X509Certificate} processing which report item occurred
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message {@link String} with the exact report item message
     * @param cause {@link Exception}, which caused this report item
     * @param result {@link ValidationResult}, which this report item leads to
     */
    public CertificateReportItem(X509Certificate certificate, String checkName, String message, Exception cause,
            ValidationResult result) {
        super(checkName, message, cause, result);
        this.certificate = certificate;
    }

    /**
     * Get the certificate related to this report item.
     *
     * @return {@link X509Certificate} related to this report item
     */
    public X509Certificate getCertificate() {
        return certificate;
    }
}
