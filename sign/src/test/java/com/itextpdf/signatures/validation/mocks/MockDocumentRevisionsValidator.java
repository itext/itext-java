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
package com.itextpdf.signatures.validation.mocks;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.validation.DocumentRevisionsValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MockDocumentRevisionsValidator extends DocumentRevisionsValidator {

    public Consumer<RevisionsValidatorCall> onCallHandler;
    private ReportItemStatus reportItemStatus = ReportItemStatus.INFO;
    private List<RevisionsValidatorCall> calls = new ArrayList<>();

    public MockDocumentRevisionsValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public ValidationReport validateAllDocumentRevisions(ValidationContext context, PdfDocument document) {
        RevisionsValidatorCall call = new RevisionsValidatorCall(context, document);
        calls.add(call);
        if (onCallHandler != null) {
            onCallHandler.accept(call);
        }
        ValidationReport report = new ValidationReport();
        if (reportItemStatus != ReportItemStatus.INFO) {
            report.addReportItem(new ReportItem("test", "test", reportItemStatus));
        }
        return report;
    }

    public void setReportItemStatus(ReportItemStatus reportItemStatus) {
        this.reportItemStatus = reportItemStatus;
    }

    public void onCallDo(Consumer<RevisionsValidatorCall> callback) {
        onCallHandler = callback;
    }


    public static class RevisionsValidatorCall {
        public final ValidationContext context;
        public final PdfDocument document;

        public RevisionsValidatorCall(ValidationContext context, PdfDocument document) {
            this.context = context;
            this.document = document;
        }
    }
}
