package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import java.util.Objects;
import org.bouncycastle.tsp.TSPException;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TSPException}.
 */
public class TSPExceptionBCFips extends AbstractTSPException {
    private final TSPException tspException;

    /**
     * Creates new wrapper instance for {@link TSPException}.
     *
     * @param tspException {@link TSPException} to be wrapped
     */
    public TSPExceptionBCFips(TSPException tspException) {
        this.tspException = tspException;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TSPException}.
     */
    public TSPException getTSPException() {
        return tspException;
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
        TSPExceptionBCFips that = (TSPExceptionBCFips) o;
        return Objects.equals(tspException, that.tspException);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(tspException);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return tspException.toString();
    }

    /**
     * Delegates {@code getMessage} method call to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return tspException.getMessage();
    }
}
