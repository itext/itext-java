package com.itextpdf.signatures.validation;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Certificate validation report, which contains detailed validation results.
 */
public class CertificateValidationReport {

    private final X509Certificate investigatedCertificate;
    private final List<ReportItem> failures = new ArrayList<>();
    private final List<ReportItem> logs = new ArrayList<>();

    /**
     * Create new instance of {@link CertificateValidationReport}.
     *
     * @param certificate {@link X509Certificate}, against which validation process was started.
     */
    public CertificateValidationReport(X509Certificate certificate) {
        investigatedCertificate = certificate;
    }

    /**
     * Get the certificate, against which validation process was started.
     *
     * @return {@link X509Certificate}, against which validation process was started
     */
    public X509Certificate getInvestigatedCertificate() {
        return investigatedCertificate;
    }

    /**
     * Get the result of a validation process.
     *
     * @return {@link ValidationResult}, which represents the result of a validation
     */
    public ValidationResult getValidationResult() {
        if (failures.stream().anyMatch(reportItem -> reportItem.result == ValidationResult.INVALID)) {
            return ValidationResult.INVALID;
        }
        if (failures.stream().anyMatch(reportItem -> reportItem.result == ValidationResult.INDETERMINATE)) {
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
        return Collections.unmodifiableList(failures);
    }

    /**
     * Get all log messages reported during a validation process.
     *
     * @return report items {@link List}, which contains all reported log messages, related to validation
     */
    public List<ReportItem> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Add new failure message to the overall validation result.
     *
     * @param failingCertificate {@link X509Certificate} processing which failure occurred
     * @param check {@link String}, which represents a check name during which failure occurred
     * @param message {@link String} with the exact failure message
     * @param e {@link Exception}, which is the root cause of this failure
     * @param result {@link ValidationResult}, which shall be used as a validation result because of this failure
     */
    public void addFailure(X509Certificate failingCertificate, String check, String message, Exception e,
            ValidationResult result) {
        ReportItem item = new ReportItem(failingCertificate, check, message, e, result);
        failures.add(item);
        logs.add(item);
    }

    /**
     * Add new failure message to the overall validation result.
     *
     * @param failingCertificate {@link X509Certificate} processing which failure occurred
     * @param check {@link String}, which represents a check name during which failure occurred
     * @param message {@link String} with the exact failure message
     * @param e {@link Exception}, which is the root cause of this failure
     */
    public void addFailure(X509Certificate failingCertificate, String check, String message, Exception e) {
        addFailure(failingCertificate, check, message, e, ValidationResult.INVALID);
    }

    /**
     * Add new failure message to the overall validation result.
     *
     * @param failingCertificate {@link X509Certificate} processing which failure occurred
     * @param check {@link String}, which represents a check name during which failure occurred
     * @param message {@link String} with the exact failure message
     * @param result {@link ValidationResult}, which shall be used as a validation result because of this failure
     */
    public void addFailure(X509Certificate failingCertificate, String check, String message,
            ValidationResult result) {
        addFailure(failingCertificate, check, message, null, result);
    }

    /**
     * Add new failure message to the overall validation result.
     *
     * @param failingCertificate {@link X509Certificate} processing which failure occurred
     * @param check {@link String}, which represents a check name during which failure occurred
     * @param message {@link String} with the exact failure message
     */
    public void addFailure(X509Certificate failingCertificate, String check, String message) {
        addFailure(failingCertificate, check, message, (Exception) null);
    }

    /**
     * Add new log message to the overall validation result.
     *
     * @param currentCertificate {@link X509Certificate} processing which log message occurred
     * @param check {@link String}, which represents a check name during which log message occurred
     * @param message {@link String} with the exact log message
     */
    public void addLog(X509Certificate currentCertificate, String check, String message) {
        logs.add(new ReportItem(currentCertificate, check, message, ValidationResult.VALID));
    }

    /**
     * Report item to be used for single failure or log message.
     */
    public static class ReportItem {
        private final X509Certificate certificate;
        private final String checkName;
        private final String message;
        private final Exception cause;
        final ValidationResult result;

        /**
         * Create {@link ReportItem} instance.
         *
         * @param certificate {@link X509Certificate} processing which report item occurred
         * @param checkName {@link String}, which represents a check name during which report item occurred
         * @param message {@link String} with the exact report item message
         * @param result {@link ValidationResult}, which this report item leads to
         */
        public ReportItem(X509Certificate certificate, String checkName, String message, ValidationResult result) {
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
        public ReportItem(X509Certificate certificate, String checkName, String message, Exception cause,
                ValidationResult result) {
            this.certificate = certificate;
            this.checkName = checkName;
            this.message = message;
            this.cause = cause;
            this.result = result;
        }

        /**
         * Get the certificate related to this report item.
         *
         * @return {@link X509Certificate} related to this report item
         */
        public X509Certificate getCertificate() {
            return certificate;
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

    /**
     * Enum representing possible validation results.
     */
    public enum ValidationResult {
        /**
         * Result for valid certificate.
         */
        VALID,
        /**
         * Result for invalid certificate.
         */
        INVALID,
        /**
         * Result for certificate, which status is indeterminate.
         */
        INDETERMINATE
    }
}
