package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;

import java.util.Objects;
import org.bouncycastle.cert.CertIOException;

/**
 * Wrapper class for {@link CertIOException}.
 */
public class CertIOExceptionBC extends AbstractCertIOException {
    private final CertIOException exception;

    /**
     * Creates new wrapper instance for {@link CertIOException}.
     *
     * @param exception {@link CertIOException} to be wrapped
     */
    public CertIOExceptionBC(CertIOException exception) {
        this.exception = exception;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CertIOException}.
     */
    public CertIOException getException() {
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
        CertIOExceptionBC that = (CertIOExceptionBC) o;
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
