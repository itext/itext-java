package com.itextpdf.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;

import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;

public class OperatorCreationExceptionBC extends AbstractOperatorCreationException {
    private final OperatorCreationException exception;

    public OperatorCreationExceptionBC(OperatorCreationException exception) {
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
        OperatorCreationExceptionBC that = (OperatorCreationExceptionBC) o;
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
}
