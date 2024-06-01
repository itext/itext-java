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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssertValidationReport implements AutoCloseable {
    private final ValidationReport report;

    private final CheckChain chain = new StartOfChain();
    private boolean asserted = false;

    private AssertValidationReport(ValidationReport report) {
        this.report = report;
    }

    public static void assertThat(ValidationReport report, Consumer<AssertValidationReport> c) {
        AssertValidationReport assertion = new AssertValidationReport(report);
        c.accept(assertion);
        assertion.doAssert();
    }

    private void doAssert() {
        asserted = true;
        CheckResult result = new CheckResult();
        chain.run(report, result);
        if (!result.success) {
            result.messageBuilder.append("\n For report: ").append(report);
            throw new AssertionError(result.messageBuilder.toString());
        }
    }

    public AssertValidationReport hasNumberOfFailures(int i) {
        chain.setNext(new FailureCountCheck(i));
        return this;
    }


    public AssertValidationReport hasNumberOfLogs(int i) {
        chain.setNext(new LogCountCheck(i));
        return this;
    }

    public AssertValidationReport hasLogItem(ReportItem logItem) {
        chain.setNext(new LogItemCheck(logItem));
        return this;
    }

    public AssertValidationReport hasLogItem(Consumer<AssertValidationReportLogItem> c) {
        AssertValidationReportLogItem asserter = new AssertValidationReportLogItem(1, 1);
        c.accept(asserter);
        asserter.addToChain(this);
        return this;
    }

    public AssertValidationReport hasLogItems(int minCount, int maxCount, Consumer<AssertValidationReportLogItem> c) {
        AssertValidationReportLogItem asserter = new AssertValidationReportLogItem(minCount, maxCount);
        c.accept(asserter);
        asserter.addToChain(this);
        return this;
    }

    public AssertValidationReport hasLogItems(int count, Consumer<AssertValidationReportLogItem> c) {
        return this.hasLogItems(count, count, c);
    }


    public AssertValidationReport hasStatus(ValidationReport.ValidationResult expectedStatus) {
        chain.setNext((new StatusCheck(expectedStatus)));
        return this;
    }

    @Override
    public void close() throws Exception {
        if (!asserted) {
            throw new IllegalStateException("AssertValidationReport not asserted!");
        }
    }

    public static class AssertValidationReportLogItem {


        private final ValidationReportLogItemCheck check;

        public AssertValidationReportLogItem(int minCount, int maxCount) {
            this.check = new ValidationReportLogItemCheck(minCount, maxCount);
        }


        public AssertValidationReportLogItem withCheckName(String checkName) {
            check.withCheckName(checkName);
            return this;
        }

        @SafeVarargs
        public final AssertValidationReportLogItem withMessage(String message, Function<ReportItem, Object>... params) {
            check.withMessage(message, params);
            return this;
        }

        public AssertValidationReportLogItem withStatus(ReportItem.ReportItemStatus status) {
            check.withStatus(status);
            return this;
        }

        public AssertValidationReportLogItem withCertificate(X509Certificate certificate) {
            check.withCertificate(certificate);
            return this;
        }

        public AssertValidationReportLogItem withExceptionCauseType(Class exceptionType) {
            check.withExceptionCauseType(exceptionType);
            return this;
        }

        public void addToChain(AssertValidationReport asserter) {
            asserter.chain.setNext(check);
        }
    }

    private static class CheckResult {
        public StringBuilder messageBuilder = new StringBuilder("\n");
        public boolean success = true;
    }

    private static abstract class CheckChain {
        private CheckChain next;

        protected abstract void check(ValidationReport report, CheckResult result);

        public void run(ValidationReport report, CheckResult result) {
            check(report, result);
            next = getNext();
            if (next == null) {
                return;
            }
            next.run(report, result);
        }

        public CheckChain getNext() {
            return next;
        }

        public void setNext(CheckChain next) {
            if (this.next == null) {
                this.next = next;
            } else {
                this.next.setNext(next);
            }
        }
    }

    private static class StartOfChain extends CheckChain {

        @Override
        protected void check(ValidationReport report, CheckResult result) {

        }
    }

    private static class FailureCountCheck extends CheckChain {
        private final int expected;

        public FailureCountCheck(int expected) {
            super();
            this.expected = expected;
        }

        @Override
        protected void check(ValidationReport report, CheckResult result) {
            if (report.getFailures().size() != expected) {
                result.success = false;
                result.messageBuilder
                        .append("\nExpected ")
                        .append(expected)
                        .append(" failures but found ")
                        .append(report.getFailures().size());
            }
        }
    }

    private static class ValidationReportLogItemCheck extends CheckChain {

        private final int minCount;
        private final int maxCount;
        private final List<Function<ReportItem, Object>> messageParams = new ArrayList<Function<ReportItem, Object>>();
        private final StringBuilder errorMessage = new StringBuilder();
        private String checkName;
        private String message;
        private ReportItem.ReportItemStatus status;
        private boolean checkStatus = false;
        private X509Certificate certificate;
        private Class exceptionType;


        public ValidationReportLogItemCheck(int minCount, int maxCount) {
            this.minCount = minCount;
            this.maxCount = maxCount;
            errorMessage.append("\nExpected between ")
                    .append(minCount)
                    .append(" and ")
                    .append(maxCount)
                    .append(" message with ");
        }


        public void withCheckName(String checkName) {
            this.checkName = checkName;
            errorMessage.append(" check name '")
                    .append(checkName)
                    .append("'");
        }

        public void withMessage(String message, Function<ReportItem, Object>... params) {
            this.message = message;
            Collections.addAll(messageParams, params);
            errorMessage.append(" message '")
                    .append(message)
                    .append("'");
        }

        public void withStatus(ReportItem.ReportItemStatus status) {
            this.status = status;
            checkStatus = true;
            errorMessage.append(" status '")
                    .append(status)
                    .append("'");
        }

        public void withCertificate(X509Certificate certificate) {
            this.certificate = certificate;
            errorMessage.append(" certificate '")
                    .append(certificate.getSubjectX500Principal())
                    .append("'");
        }

        public void withExceptionCauseType(Class exceptionType) {
            this.exceptionType = exceptionType;
            errorMessage.append(" with exception cause '")
                    .append(exceptionType.getName())
                    .append("'");
        }

        @Override
        protected void check(ValidationReport report, final CheckResult result) {
            errorMessage.append("\n");
            List<ReportItem> prefiltered;
            if (message != null) {
                prefiltered = report.getLogs().stream().filter(i -> {
                    Object[] params = new Object[messageParams.size()];
                    for (int p = 0; p < messageParams.size(); p++) {
                        params[p] = messageParams.get(p).apply(i);
                    }
                    return i.getMessage().equals(MessageFormatUtil.format(message, params));
                }).collect(Collectors.toList());
                errorMessage.append("found ").append(prefiltered.size()).append(" matches after message filter\n");
            } else {
                prefiltered = report.getLogs();
            }
            if (checkName != null) {
                prefiltered = prefiltered.stream().filter(i -> (checkName.equals(i.getCheckName())))
                        .collect(Collectors.toList());
                errorMessage.append("found ").append(prefiltered.size()).append(" matches after check name filter\n");
            }
            if (checkStatus) {
                prefiltered = prefiltered.stream().filter(i -> (status.equals(i.getStatus())))
                        .collect(Collectors.toList());
                errorMessage.append("found ").append(prefiltered.size()).append(" matches after status filter\n");
            }
            if (certificate != null) {
                prefiltered = prefiltered.stream().filter(i ->
                        certificate.equals(((CertificateReportItem) i).getCertificate())).collect(Collectors.toList());
                errorMessage.append("found ").append(prefiltered.size()).append(" matches after certificate filter\n");
            }
            if (exceptionType != null) {
                prefiltered = prefiltered.stream().filter(i -> i.getExceptionCause() != null
                                && exceptionType.isAssignableFrom(i.getExceptionCause().getClass()))
                        .collect(Collectors.toList());
                errorMessage.append("found ").append(prefiltered.size()).append(" matches after exception cause filter\n");
            }
            long foundCount = prefiltered.size();
            if (foundCount < minCount || foundCount > maxCount) {
                result.success = false;
                result.messageBuilder
                        .append(errorMessage);
            }
        }

        @Override
        public String toString() {
            return
                    "checkName='" + checkName + '\'' +
                            ", message='" + message + '\'' +
                            ", status=" + status +
                            ", certificate=" + (certificate == null ? "null" : certificate.getSubjectX500Principal().toString()) +
                            ", exceptionType=" + (exceptionType == null ? "null" : exceptionType.getName());
        }
    }

    private static class LogCountCheck extends CheckChain {
        private final int expected;

        public LogCountCheck(int expected) {
            super();
            this.expected = expected;
        }

        @Override
        protected void check(ValidationReport report, CheckResult result) {
            if (report.getLogs().size() != expected) {
                result.success = false;
                result.messageBuilder
                        .append("\nExpected ")
                        .append(expected)
                        .append(" logs but found ")
                        .append(report.getLogs().size());
            }
        }
    }

    private static class LogItemCheck extends CheckChain {

        private final ReportItem expectedItem;

        public LogItemCheck(ReportItem expectedItem) {
            super();
            this.expectedItem = expectedItem;
        }

        @Override
        protected void check(ValidationReport report, CheckResult result) {

            if (!report.getLogs().contains(expectedItem)) {
                result.success = false;
                result.messageBuilder
                        .append("\nExpected report item not found:")
                        .append(expectedItem);
            }
        }
    }

    private static class StatusCheck extends CheckChain {
        private final ValidationReport.ValidationResult expectedStatus;

        public StatusCheck(ValidationReport.ValidationResult expectedStatus) {
            super();
            this.expectedStatus = expectedStatus;
        }

        @Override
        protected void check(ValidationReport report, CheckResult result) {
            if (!expectedStatus.equals(report.getValidationResult())) {
                result.success = false;
                result.messageBuilder
                        .append("\nExpetected validationResult of ")
                        .append(expectedStatus)
                        .append(" but found ")
                        .append(report.getValidationResult());
            }
        }
    }
}
