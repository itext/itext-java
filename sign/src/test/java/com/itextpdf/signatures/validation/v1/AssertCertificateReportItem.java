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
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;

public class AssertCertificateReportItem {
    private final CertificateReportItem item;
    private final CheckChain chain = new StartOfChain();

    public AssertCertificateReportItem(CertificateReportItem item) {
        this.item = item;
    }

    public AssertCertificateReportItem hasMessage(String template, Object... arguments) {
         chain.setNext(new MessageChecker(MessageFormatUtil.format(template, arguments)));
         return this;
    }

    public AssertCertificateReportItem hasCheckName(String checkName) {
        chain.setNext(new CheckNameChecker(checkName));
        return this;
    }

    public AssertCertificateReportItem hasResult(ReportItemStatus reportItemStatus) {
        chain.setNext(new StatusChecker(reportItemStatus));
        return this;
    }

    public void doAssert() {
        CheckResult result = new CheckResult();
        chain.run(item, result);
        if (!result.success) {
            result.messageBuilder.append("\n For item: ").append(item);
            throw new AssertionError(result.messageBuilder.toString());
        }
    }

    private static class CheckResult {
        public StringBuilder messageBuilder = new StringBuilder("\n");
        public boolean success = true;
    }

    private static abstract class CheckChain {
        private CheckChain next;

        protected abstract void check(CertificateReportItem item, CheckResult result);

        public void run(CertificateReportItem item, CheckResult result) {
            check(item, result);
            next = getNext();
            if (next == null) {
                return;
            }
            next.run(item, result);
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
        protected void check(CertificateReportItem item, CheckResult result) {

        }
    }

    private static class MessageChecker extends  CheckChain {
        private final String message;

        public MessageChecker(String message) {
            super();
            this.message = message;
        }

        @Override
        protected void check(CertificateReportItem item, CheckResult result) {
            if (!message.equals(item.getMessage())) {
                result.success = false;
                result.messageBuilder.append("Expected message '")
                        .append(message)
                        .append("' but found '")
                        .append(item.getMessage())
                        .append("'.\n");
            }
        }
    }

    private static class CheckNameChecker extends CheckChain {
        private final String checkName;

        public CheckNameChecker(String checkName) {
            super();
            this.checkName = checkName;
        }

        @Override
        protected void check(CertificateReportItem item, CheckResult result) {
            if (!checkName.equals(item.getCheckName())) {
                result.success = false;
                result.messageBuilder.append("Expected check name '")
                        .append(checkName)
                        .append("' but found '")
                        .append(item.getCheckName())
                        .append("'.\n");
            }
        }
    }

    private static class StatusChecker extends CheckChain {
        private final ReportItemStatus reportItemStatus;

        public StatusChecker(ReportItemStatus reportItemStatus) {
            super();
            this.reportItemStatus = reportItemStatus;
        }

        @Override
        protected void check(CertificateReportItem item, CheckResult result) {
            if (!reportItemStatus.equals(item.getStatus())) {
                result.success = false;
                result.messageBuilder.append("Expected check name '")
                        .append(reportItemStatus)
                        .append("' but found '")
                        .append(item.getStatus())
                        .append("'.\n");
            }
        }
    }
}
