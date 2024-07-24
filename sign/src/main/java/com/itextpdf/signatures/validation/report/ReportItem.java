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
package com.itextpdf.signatures.validation.report;

/**
 * Report item to be used for single failure or log message.
 */
public class ReportItem {
    private final String checkName;
    private final String message;
    private final Exception cause;
    private ReportItemStatus status;

    /**
     * Create {@link ReportItem} instance.
     *
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message   {@link String} with the exact report item message
     * @param status    {@link ReportItemStatus} report item status that determines validation result
     */
    public ReportItem(String checkName, String message, ReportItemStatus status) {
        this(checkName, message, null, status);
    }

    /**
     * Create {@link ReportItem} instance.
     *
     * @param checkName {@link String}, which represents a check name during which report item occurred
     * @param message   {@link String} with the exact report item message
     * @param cause     {@link Exception}, which caused this report item
     * @param status    {@link ReportItemStatus} report item status that determines validation result
     */
    public ReportItem(String checkName, String message, Exception cause, ReportItemStatus status) {
        this.checkName = checkName;
        this.message = message;
        this.cause = cause;
        this.status = status;
    }

    /**
     * Get the check name related to this report item.
     *
     * @return {@link String} check name related to this report item.
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * Get the message related to this report item.
     *
     * @return {@link String} message related to this report item.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the exception, which caused this report item.
     *
     * @return {@link Exception}, which cause this report item.
     */
    public Exception getExceptionCause() {
        return cause;
    }

    /**
     * Get report item status that determines validation result this report item corresponds to.
     *
     * @return {@link ReportItemStatus} report item status that determines validation result.
     */
    public ReportItemStatus getStatus() {
        return status;
    }

    /**
     * Set report item status that determines validation result this report item corresponds to.
     *
     * @param status {@link ReportItemStatus} report item status that determines validation result
     *
     * @return this {@link ReportItem} instance.
     */
    public ReportItem setStatus(ReportItemStatus status) {
        this.status = status;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        return "\nReportItem{" +
                "checkName='" + checkName + '\'' +
                ", message='" + message + '\'' +
                ", cause=" + cause +
                ", status=" + status +
                '}';
    }

    /**
     * Enum representing possible report item statuses that determine validation result.
     */
    public enum ReportItemStatus {
        /**
         * Report item status for info messages.
         */
        INFO,
        /**
         * Report item status that leads to invalid validation result.
         */
        INVALID,
        /**
         * Report item status that leads to indeterminate validation result.
         */
        INDETERMINATE
    }
}
