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
package com.itextpdf.signatures.validation.v1.report;

import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validation report, which contains detailed validation results.
 */
public class ValidationReport {
    private final List<ReportItem> reportItems = new ArrayList<>();

    /**
     * Create new instance of {@link ValidationReport}.
     */
    public ValidationReport() {
        // Empty constructor.
    }

    /**
     * Get the result of a validation process.
     *
     * @return {@link ValidationResult}, which represents the result of a validation
     */
    public ValidationResult getValidationResult() {
        if (reportItems.stream().anyMatch(reportItem -> reportItem.getStatus() == ReportItemStatus.INVALID)) {
            return ValidationResult.INVALID;
        }
        if (reportItems.stream().anyMatch(reportItem -> reportItem.getStatus() == ReportItemStatus.INDETERMINATE)) {
            return ValidationResult.INDETERMINATE;
        }
        return ValidationResult.VALID;
    }

    /**
     * Get all failures recognized during a validation process.
     *
     * @return report items {@link List}, which contains all recognized failures
     */
    public List<ReportItem> getFailures() {
        return reportItems.stream().filter(item -> item.getStatus() != ReportItemStatus.INFO)
                .collect(Collectors.toList());
    }

    /**
     * Get list of failures, which are related to certificate validation.
     *
     * @return report items {@link List}, which contains only {@link CertificateReportItem} failures
     */
    public List<CertificateReportItem> getCertificateFailures() {
        return getFailures().stream().filter(item -> item instanceof CertificateReportItem)
                .map(item -> (CertificateReportItem) item).collect(Collectors.toList());
    }

    /**
     * Get all log messages reported during a validation process.
     *
     * @return report items {@link List}, which contains all reported log messages, related to validation
     */
    public List<ReportItem> getLogs() {
        return Collections.unmodifiableList(reportItems);
    }

    /**
     * Get list of log messages, which are related to certificate validation.
     *
     * @return report items {@link List}, which contains only {@link CertificateReportItem} log messages
     */
    public List<CertificateReportItem> getCertificateLogs() {
        return reportItems.stream().filter(item -> item instanceof CertificateReportItem)
                .map(item -> (CertificateReportItem) item).collect(Collectors.toList());
    }

    /**
     * Add new report item to the overall validation result.
     *
     * @param item {@link ReportItem} to be added
     */
    public void addReportItem(ReportItem item) {
        reportItems.add(item);
    }

    @Override
    public String toString() {
        return "ValidationReport{" +
                "reportItems=" + reportItems +
                '}';
    }

    /**
     * Enum representing possible validation results.
     */
    public enum ValidationResult {
        /**
         * Valid validation result.
         */
        VALID,
        /**
         * Invalid validation result.
         */
        INVALID,
        /**
         * Indeterminate validation result.
         */
        INDETERMINATE
    }
}
