package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockOCSPValidator extends OCSPValidator {

    public final List<OCSPValidatorCall> calls = new ArrayList<>();
    private Consumer<OCSPValidatorCall> onCallHandler;

    /**
     * Creates new {@link OCSPValidator} instance.
     */
    public MockOCSPValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                         ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate) {
        OCSPValidatorCall call = new OCSPValidatorCall(report, context, certificate, singleResp, ocspResp, validationDate);
        calls.add(call);
        if (onCallHandler != null) {
            onCallHandler.accept(call);
        }
    }

    public void onCallDo(Consumer<OCSPValidatorCall> c) {
        onCallHandler = c;
    }

    public final static class OCSPValidatorCall {

        public final Date timeStamp = DateTimeUtil.getCurrentTimeDate();
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final ISingleResp singleResp;
        public final IBasicOCSPResp ocspResp;
        public final Date validationDate;

        public OCSPValidatorCall(ValidationReport report, ValidationContext context, X509Certificate certificate,
                                 ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.singleResp = singleResp;
            this.ocspResp = ocspResp;
            this.validationDate = validationDate;
        }
    }
}
