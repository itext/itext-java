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
package com.itextpdf.signatures.validation.v1.mocks;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.validation.v1.DocumentRevisionsValidator;
import com.itextpdf.signatures.validation.v1.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

public class MockDocumentRevisionsValidator extends DocumentRevisionsValidator {

    private ReportItemStatus reportItemStatus = ReportItemStatus.INFO;

    public MockDocumentRevisionsValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public ValidationReport validateAllDocumentRevisions(ValidationContext context, PdfDocument document) {
        ValidationReport report = new ValidationReport();
        if (reportItemStatus != ReportItemStatus.INFO) {
            report.addReportItem(new ReportItem("test", "test", reportItemStatus));
        }
        return report;
    }

    public void setReportItemStatus(ReportItemStatus reportItemStatus) {
        this.reportItemStatus = reportItemStatus;
    }
}
