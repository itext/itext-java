package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;

import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;

public class OperatorCreationExceptionBCFips extends AbstractOperatorCreationException {
    private final OperatorCreationException exception;

    public OperatorCreationExceptionBCFips(OperatorCreationException exception) {
        this.exception = exception;
    }

    public OperatorCreationException getException() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperatorCreationExceptionBCFips that = (OperatorCreationExceptionBCFips) o;
        return Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exception);
    }

    @Override
    public String toString() {
        return exception.toString();
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
