package com.itextpdf.signatures.validation.v1;

import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class MockChainValidator extends CertificateChainValidator {

    public List<ValidationCallBack> verificationCalls = new ArrayList<ValidationCallBack>();

    MockChainValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate, Date verificationDate) {
        verificationCalls.add(new ValidationCallBack(certificate, verificationDate));
        return result;
    }

    public static class ValidationCallBack {
        public X509Certificate certificate;
        public Date checkDate;

        public ValidationCallBack(X509Certificate certificate, Date checkDate) {
            this.certificate = certificate;
            this.checkDate = checkDate;
        }
    }
}
