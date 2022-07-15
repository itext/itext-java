package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;

import java.util.Objects;
import org.bouncycastle.cms.CMSException;

/**
 * Wrapper class for {@link CMSException}.
 */
public class CMSExceptionBCFips extends AbstractCMSException {
    private final CMSException exception;

    /**
     * Creates new wrapper instance for {@link CMSException}.
     *
     * @param exception {@link CMSException} to be wrapped
     */
    public CMSExceptionBCFips(CMSException exception) {
        this.exception = exception;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CMSException}.
     */
    public CMSException getCMSException() {
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
        CMSExceptionBCFips that = (CMSExceptionBCFips) o;
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
