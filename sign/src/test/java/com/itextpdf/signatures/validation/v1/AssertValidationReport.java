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

import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.util.function.Function;
import java.util.function.Predicate;

public class AssertValidationReport {
    private final ValidationReport report;

    private final CheckChain chain = new StartOfChain();
    public AssertValidationReport(ValidationReport report) {
        this.report = report;
    }

    public void doAssert() {
        CheckResult result = new CheckResult();
        chain.run(report, result);
        if (!result.success) {
            result.messageBuilder.append("\n For item: ").append(report);
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

    public AssertValidationReport hasLogItem( Function<ReportItem, Boolean> check, String itemDescription) {
        chain.setNext(new ItemCheck(check, 1, itemDescription));
        return this;
    }

    public AssertValidationReport hasLogItems( Function<ReportItem, Boolean> check, int count, String itemDescription) {
        chain.setNext(new ItemCheck(check, count, itemDescription));
        return this;
    }

    public AssertValidationReport hasStatus(ValidationReport.ValidationResult expectedStatus) {
        chain.setNext((new StatusCheck(expectedStatus)));
        return this;
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

    private static class ItemCheck extends CheckChain {
        private final Function<ReportItem, Boolean> check;
        private final String message;
        private final int expectedCount;

        public ItemCheck(Function<ReportItem, Boolean>check, int count, String itemDescription) {
            super();
            this.check = check;
            this.expectedCount = count;
            this.message = itemDescription;
        }

        @Override
        protected void check(ValidationReport report, CheckResult result) {
            long foundCount = report.getLogs().stream().filter(i -> check.apply(i)).count();
            if (foundCount != expectedCount) {
                result.success = false;
                result.messageBuilder
                        .append("\nExpected ")
                        .append(expectedCount)
                        .append(" report logs like '")
                        .append(message)
                        .append("' but found ")
                        .append(foundCount);
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
