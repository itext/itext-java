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
package com.itextpdf.signatures.validation.report;

import com.itextpdf.commons.json.IJsonSerializable;
import com.itextpdf.commons.json.JsonArray;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validation report, which contains detailed validation results.
 */
public class ValidationReport implements IJsonSerializable {
    private static final String JSON_KEY_REPORT_ITEMS = "reportItems";

    private final List<ReportItem> reportItems = new ArrayList<>();

    /**
     * Create new instance of {@link ValidationReport}.
     */
    public ValidationReport() {
        // Declaring default constructor explicitly to avoid removing it unintentionally.
    }


    /**
     * Create a copy of another validation report.
     *
     * @param report to be copied
     */
    public ValidationReport(ValidationReport report) {
        for (ReportItem item : report.reportItems){
            this.addReportItem(new ReportItem(item));
        }
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ValidationReport{validationResult=");
        sb.append(getValidationResult())
                .append("\nreportItems=");
        for (ReportItem i : reportItems) {
            sb.append(i).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Merge all {@link ReportItem} objects from sub report into this one.
     *
     * @param subReport report from which items will be merged
     *
     * @return {@link ValidationReport} the same updated validation report instance.
     */
    public ValidationReport merge(ValidationReport subReport) {
        if (subReport != null) {
            for (ReportItem item : subReport.getLogs()) {
                addReportItem(item);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        // Sort the items by check name to ensure consistent order
        List<ReportItem> sortedItems = getLogs().stream().sorted((item1, item2) -> {
            if (item1.getCheckName() == null && item2.getCheckName() == null) {
                return 0;
            } else if (item1.getCheckName() == null) {
                return -1;
            } else if (item2.getCheckName() == null) {
                return 1;
            } else {
                return item1.getCheckName().compareTo(item2.getCheckName());
            }
        }).collect(Collectors.toList());

        JsonArray reportItemsJson = new JsonArray();
        for (ReportItem reportItem : sortedItems) {
            reportItemsJson.add(reportItem.toJson());
        }
        JsonObject validationReportJson = new JsonObject();
        validationReportJson.add(JSON_KEY_REPORT_ITEMS, reportItemsJson);
        return validationReportJson;
    }

    /**
     * Deserializes {@link JsonValue} into {@link ValidationReport}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link ValidationReport}
     */
    public static ValidationReport fromJson(JsonValue jsonValue) {
        JsonObject validationReportJson = (JsonObject) jsonValue;
        JsonArray reportItemsJson =
                (JsonArray) validationReportJson.getField(JSON_KEY_REPORT_ITEMS);
        ValidationReport validationReportFromJson = new ValidationReport();
        for (ReportItem reportItem : reportItemsJson.getValues().stream().map(
                reportItemJson -> ReportItem.fromJson(reportItemJson)).collect(Collectors.toList())) {
            validationReportFromJson.addReportItem(reportItem);
        }
        return validationReportFromJson;
    }

    /**
     * Merge all {@link ReportItem} objects from sub report into this one with different status.
     *
     * @param subReport report from which items will be merged
     * @param newStatus {@link ReportItemStatus} which will be used instead of provided ones
     *
     * @return {@link ValidationReport} the same updated validation report instance.
     */
    public ValidationReport mergeWithDifferentStatus(ValidationReport subReport, ReportItemStatus newStatus) {
        if (subReport != null) {
            for (ReportItem item : subReport.getLogs()) {
                addReportItem(new ReportItem(item).setStatus(newStatus));
            }
        }
        return this;
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
