package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockCrlValidator extends CRLValidator {

    public final List<CRLValidateCall> calls = new ArrayList<>();
    private Consumer<CRLValidateCall> onCallHandler;

    /**
     * Creates new {@link CRLValidator} instance.
     */
    public MockCrlValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate, X509CRL crl,
                         Date validationDate) {
        CRLValidateCall call = new CRLValidateCall(report, context, certificate, crl, validationDate);
        calls.add(call);
        if (onCallHandler != null) {
            onCallHandler.accept(calls.get(calls.size() - 1));
        }
    }

    public void onCallDo(Consumer<CRLValidateCall> c) {
        onCallHandler = c;
    }

    public final static class CRLValidateCall {
        public final Date timeStamp = DateTimeUtil.getCurrentTimeDate();
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final X509CRL crl;
        public final Date validationDate;

        public CRLValidateCall(ValidationReport report, ValidationContext context, X509Certificate certificate,
                               X509CRL crl, Date validationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.crl = crl;
            this.validationDate = validationDate;
        }
    }
}