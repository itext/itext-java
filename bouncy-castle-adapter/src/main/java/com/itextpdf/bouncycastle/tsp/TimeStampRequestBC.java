package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampRequest;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampRequest}.
 */
public class TimeStampRequestBC implements ITimeStampRequest {
    private final TimeStampRequest timeStampRequest;

    /**
     * Creates new wrapper instance for {@link TimeStampRequest}.
     *
     * @param timeStampRequest {@link TimeStampRequest} to be wrapped
     */
    public TimeStampRequestBC(TimeStampRequest timeStampRequest) {
        this.timeStampRequest = timeStampRequest;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampRequest}.
     */
    public TimeStampRequest getTimeStampRequest() {
        return timeStampRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampRequest.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getNonce() {
        return timeStampRequest.getNonce();
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
        TimeStampRequestBC that = (TimeStampRequestBC) o;
        return Objects.equals(timeStampRequest, that.timeStampRequest);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampRequest);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampRequest.toString();
    }
}
