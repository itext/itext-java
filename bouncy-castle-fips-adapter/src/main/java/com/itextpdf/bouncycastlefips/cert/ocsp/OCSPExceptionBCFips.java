package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;

/**
 * Wrapper class for {@link OCSPException}.
 */
public class OCSPExceptionBCFips extends AbstractOCSPException {
    private final OCSPException exception;

    /**
     * Creates new wrapper instance for {@link OCSPException}.
     *
     * @param exception {@link OCSPException} to be wrapped
     */
    public OCSPExceptionBCFips(OCSPException exception) {
        this.exception = exception;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPException}.
     */
    public OCSPException getException() {
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
        OCSPExceptionBCFips that = (OCSPExceptionBCFips) o;
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
