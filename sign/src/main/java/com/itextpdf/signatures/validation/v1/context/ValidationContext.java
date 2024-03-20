package com.itextpdf.signatures.validation.v1.context;

import java.util.Objects;

public class ValidationContext {
    private final CertificateSource certificateSource;
    private final ValidatorContext validatorContext;
    private final TimeBasedContext timeBasedContext;

    public ValidationContext(ValidatorContext validatorContext, CertificateSource certificateSource,
                             TimeBasedContext timeBasedContext) {
        this.validatorContext = validatorContext;
        this.certificateSource = certificateSource;
        this.timeBasedContext = timeBasedContext;
    }

    public CertificateSource getCertificateSource() {
        return certificateSource;
    }

    public ValidationContext setCertificateSource(CertificateSource certificateSource) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext);
    }

    public TimeBasedContext getTimeBasedContext() {
        return timeBasedContext;
    }

    public ValidationContext setTimeBasedContext(TimeBasedContext timeBasedContext) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext);
    }


    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public ValidationContext setValidatorContext(ValidatorContext validatorContext) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext);
    }

    @Override
    public String toString() {
        return "ValidationContext{" +
                "certificateSource=" + certificateSource +
                ", validatorContext=" + validatorContext +
                ", timeBasedContext=" + timeBasedContext +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationContext that = (ValidationContext) o;
        return certificateSource == that.certificateSource
                && validatorContext == that.validatorContext
                && timeBasedContext == that.timeBasedContext;
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object)certificateSource, validatorContext, timeBasedContext);
    }
}
