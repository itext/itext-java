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

/**
 * Report item to be used for single failure or log message.
 */
public class ReportItem {
    private final String checkName;
    private final String message;
    private final Exception cause;
    final ValidationResult result;

    /**
     * Create {@link ReportItem} instance.
     *
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message {@link String} with the exact report item message
     * @param result {@link ValidationResult}, which this report item leads to
     */
    public ReportItem(String checkName, String message, ValidationResult result) {
        this(checkName, message, null, result);
    }

    /**
     * Create {@link ReportItem} instance.
     *
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message {@link String} with the exact report item message
     * @param cause {@link Exception}, which caused this report item
     * @param result {@link ValidationResult}, which this report item leads to
     */
    public ReportItem(String checkName, String message, Exception cause,
            ValidationResult result) {
        this.checkName = checkName;
        this.message = message;
        this.cause = cause;
        this.result = result;
    }

    /**
     * Get the check name related to this report item.
     *
     * @return {@link String} check name related to this report item
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * Get the message related to this report item.
     *
     * @return {@link String} message related to this report item
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the exception, which caused this report item.
     *
     * @return {@link Exception}, which cause this report item
     */
    public Exception getExceptionCause() {
        return cause;
    }

    /**
     * Get validation result this report item leads to.
     *
     * @return {@link ValidationResult} this report item leads to
     */
    public ValidationResult getValidationResult() {
        return result;
    }
}
