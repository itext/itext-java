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
