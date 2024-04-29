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
