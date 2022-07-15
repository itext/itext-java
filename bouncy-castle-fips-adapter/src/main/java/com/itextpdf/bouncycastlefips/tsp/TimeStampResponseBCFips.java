package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.bouncycastlefips.asn1.cmp.PKIFailureInfoBCFips;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampResponse}.
 */
public class TimeStampResponseBCFips implements ITimeStampResponse {
    private final TimeStampResponse timeStampResponse;

    /**
     * Creates new wrapper instance for {@link TimeStampResponse}.
     *
     * @param timeStampResponse {@link TimeStampResponse} to be wrapped
     */
    public TimeStampResponseBCFips(TimeStampResponse timeStampResponse) {
        this.timeStampResponse = timeStampResponse;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampResponse}.
     */
    public TimeStampResponse getTimeStampResponse() {
        return timeStampResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ITimeStampRequest request) throws TSPExceptionBCFips {
        try {
            timeStampResponse.validate(((TimeStampRequestBCFips) request).getTimeStampRequest());
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPKIFailureInfo getFailInfo() {
        return new PKIFailureInfoBCFips(timeStampResponse.getFailInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampToken getTimeStampToken() {
        return new TimeStampTokenBCFips(timeStampResponse.getTimeStampToken());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusString() {
        return timeStampResponse.getStatusString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampResponse.getEncoded();
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
        TimeStampResponseBCFips that = (TimeStampResponseBCFips) o;
        return Objects.equals(timeStampResponse, that.timeStampResponse);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampResponse);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampResponse.toString();
    }
}
