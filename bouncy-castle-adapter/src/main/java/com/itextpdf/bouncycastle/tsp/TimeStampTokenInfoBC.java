package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.tsp.TSTInfoBC;
import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampTokenInfo;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampTokenInfo}.
 */
public class TimeStampTokenInfoBC implements ITimeStampTokenInfo {
    private final TimeStampTokenInfo timeStampTokenInfo;

    /**
     * Creates new wrapper instance for {@link TimeStampTokenInfo}.
     *
     * @param timeStampTokenInfo {@link TimeStampTokenInfo} to be wrapped
     */
    public TimeStampTokenInfoBC(TimeStampTokenInfo timeStampTokenInfo) {
        this.timeStampTokenInfo = timeStampTokenInfo;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampTokenInfo}.
     */
    public TimeStampTokenInfo getTimeStampTokenInfo() {
        return timeStampTokenInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBC(timeStampTokenInfo.getHashAlgorithm());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITSTInfo toASN1Structure() {
        return new TSTInfoBC(timeStampTokenInfo.toASN1Structure());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getGenTime() {
        return timeStampTokenInfo.getGenTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampTokenInfo.getEncoded();
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
        TimeStampTokenInfoBC that = (TimeStampTokenInfoBC) o;
        return Objects.equals(timeStampTokenInfo, that.timeStampTokenInfo);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampTokenInfo);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampTokenInfo.toString();
    }
}
