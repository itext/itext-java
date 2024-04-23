package com.itextpdf.signatures.validation.v1;

import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockRevocationDataValidator extends RevocationDataValidator {

    public List<ICrlClient> crlClientsAdded = new ArrayList<>();
    public List<IOcspClient> ocspClientsAdded = new ArrayList<>();

    public List<RevocationDataValidatorCall> calls = new ArrayList<>();

    /**
     * Creates new {@link RevocationDataValidator} instance to validate certificate revocation data.
     */
    MockRevocationDataValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public RevocationDataValidator addCrlClient(ICrlClient crlClient) {
        crlClientsAdded.add(crlClient);
        return this;
    }

    @Override
    public RevocationDataValidator addOcspClient(IOcspClient ocspClient) {
        ocspClientsAdded.add(ocspClient);
        return this;
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                         Date validationDate) {
        calls.add(new RevocationDataValidatorCall(report, context, certificate, validationDate));
    }

    public final static class RevocationDataValidatorCall {
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final Date validationDate;

        public RevocationDataValidatorCall(ValidationReport report, ValidationContext context,
                                           X509Certificate certificate, Date validationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.validationDate = validationDate;
        }
    }
}
