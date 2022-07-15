package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;

import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * Wrapper class for {@link OperatorCreationException}.
 */
public class OperatorCreationExceptionBCFips extends AbstractOperatorCreationException {
    private final OperatorCreationException exception;

    /**
     * Creates new wrapper instance for {@link OperatorCreationException}.
     *
     * @param exception {@link OperatorCreationException} to be wrapped
     */
    public OperatorCreationExceptionBCFips(OperatorCreationException exception) {
        this.exception = exception;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OperatorCreationException}.
     */
    public OperatorCreationException getException() {
        return exception;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(exception);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return exception.toString();
    }

    /**
     * Delegates {@code getMessage} method call to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
