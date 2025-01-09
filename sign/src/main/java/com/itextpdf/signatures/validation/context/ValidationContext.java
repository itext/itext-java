/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.signatures.validation.context;

import java.util.Objects;

/**
 * Validation context class, which encapsulates specific context values, related to validation process.
 */
public class ValidationContext {
    private final CertificateSource certificateSource;
    private final ValidatorContext validatorContext;
    private final TimeBasedContext timeBasedContext;
    private ValidationContext previousValidationContext;

    /**
     * Create {@link ValidationContext} instance using provided context values.
     *
     * @param validatorContext {@link ValidatorContext} value
     * @param certificateSource {@link CertificateSource} value
     * @param timeBasedContext {@link TimeBasedContext} value
     */
    public ValidationContext(ValidatorContext validatorContext, CertificateSource certificateSource,
            TimeBasedContext timeBasedContext) {
        this.validatorContext = validatorContext;
        this.certificateSource = certificateSource;
        this.timeBasedContext = timeBasedContext;
    }

    ValidationContext(ValidatorContext validatorContext, CertificateSource certificateSource,
            TimeBasedContext timeBasedContext, ValidationContext previousValidationContext) {
        this(validatorContext, certificateSource, timeBasedContext);
        this.previousValidationContext = previousValidationContext;
    }

    /**
     * Get previous validation context instance, from which this instance was created.
     *
     * @return previous {@link ValidatorContext} instance
     */
    public ValidationContext getPreviousValidationContext() {
        return previousValidationContext;
    }

    /**
     * Get specific certificate source context value.
     *
     * @return {@link CertificateSource} context value
     */
    public CertificateSource getCertificateSource() {
        return certificateSource;
    }

    /**
     * Create new {@link ValidationContext} instance with the provided certificate source context value.
     *
     * @param certificateSource {@link CertificateSource} value
     *
     * @return new {@link ValidationContext} instance
     */
    public ValidationContext setCertificateSource(CertificateSource certificateSource) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext, this);
    }

    /**
     * Get specific time-based context value.
     *
     * @return {@link TimeBasedContext} context value
     */
    public TimeBasedContext getTimeBasedContext() {
        return timeBasedContext;
    }

    /**
     * Create new {@link ValidationContext} instance with the provided certificate source context value.
     *
     * @param timeBasedContext {@link TimeBasedContext} value
     *
     * @return new {@link ValidationContext} instance
     */
    public ValidationContext setTimeBasedContext(TimeBasedContext timeBasedContext) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext, this);
    }

    /**
     * Get specific validator context value.
     *
     * @return {@link ValidatorContext} context value
     */
    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    /**
     * Create new {@link ValidationContext} instance with the provided certificate source context value.
     *
     * @param validatorContext {@link ValidatorContext} value
     *
     * @return new {@link ValidationContext} instance
     */
    public ValidationContext setValidatorContext(ValidatorContext validatorContext) {
        return new ValidationContext(validatorContext, certificateSource, timeBasedContext, this);
    }

    /**
     * Check if validation contexts chain contains specific {@link CertificateSource} value.
     *
     * @param context {@link ValidationContext} instance to start the check from
     * @param source {@link CertificateSource} value to check
     *
     * @return {@code true} if validation contexts chain contains provided certificate source, {@code false} otherwise
     */
    public static boolean checkIfContextChainContainsCertificateSource(ValidationContext context,
            CertificateSource source) {
        if (context == null) {
            return false;
        }
        if (source == context.getCertificateSource()) {
            return true;
        }
        return checkIfContextChainContainsCertificateSource(context.getPreviousValidationContext(), source);
    }

    /**
     * Return string representation of this {@link ValidationContext}.
     * Previous validation context is not a part of this representation.
     *
     * @return a string representation of the {@link ValidationContext}
     */
    @Override
    public String toString() {
        return "ValidationContext{" +
                "certificateSource=" + certificateSource +
                ", validatorContext=" + validatorContext +
                ", timeBasedContext=" + timeBasedContext +
                '}';
    }

    /**
     * Check if the provided object is equal to this one.
     * Previous validation context field is not taken into account during this comparison.
     *
     * @param o the reference object with which to compare
     *
     * @return {@code true} if provided object is equal to this one, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationContext that = (ValidationContext) o;
        return certificateSource == that.certificateSource
                && validatorContext == that.validatorContext
                && timeBasedContext == that.timeBasedContext;
    }

    /**
     * Return a hash code value for this validation context.
     * Previous validation context field is not taken into account during hash code calculation.
     *
     * @return a hash code value for this validation context
     */
    @Override
    public int hashCode() {
        return Objects.hash((Object)certificateSource, validatorContext, timeBasedContext);
    }
}
