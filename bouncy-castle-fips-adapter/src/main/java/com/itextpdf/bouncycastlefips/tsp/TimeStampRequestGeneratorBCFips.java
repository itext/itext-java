package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;

import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampRequestGenerator}.
 */
public class TimeStampRequestGeneratorBCFips implements ITimeStampRequestGenerator {
    private final TimeStampRequestGenerator requestGenerator;

    /**
     * Creates new wrapper instance for {@link TimeStampRequestGenerator}.
     *
     * @param requestGenerator {@link TimeStampRequestGenerator} to be wrapped
     */
    public TimeStampRequestGeneratorBCFips(TimeStampRequestGenerator requestGenerator) {
        this.requestGenerator = requestGenerator;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampRequestGenerator}.
     */
    public TimeStampRequestGenerator getRequestGenerator() {
        return requestGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCertReq(boolean var1) {
        requestGenerator.setCertReq(var1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReqPolicy(String reqPolicy) {
        requestGenerator.setReqPolicy(reqPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampRequest generate(IASN1ObjectIdentifier objectIdentifier, byte[] imprint, BigInteger nonce) {
        return new TimeStampRequestBCFips(requestGenerator.generate(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(), imprint, nonce));
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
        TimeStampRequestGeneratorBCFips that = (TimeStampRequestGeneratorBCFips) o;
        return Objects.equals(requestGenerator, that.requestGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(requestGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return requestGenerator.toString();
    }
}
