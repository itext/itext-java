package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.cert.jcajce.JcaCertStoreBCFips;
import com.itextpdf.bouncycastlefips.cms.SignerInfoGeneratorBCFips;
import com.itextpdf.bouncycastlefips.operator.DigestCalculatorBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampTokenGenerator;

/**
 * Wrapper class for {@link TimeStampTokenGenerator}.
 */
public class TimeStampTokenGeneratorBCFips implements ITimeStampTokenGenerator {
    private final TimeStampTokenGenerator timeStampTokenGenerator;

    /**
     * Creates new wrapper instance for {@link TimeStampTokenGenerator}.
     *
     * @param timeStampTokenGenerator {@link TimeStampTokenGenerator} to be wrapped
     */
    public TimeStampTokenGeneratorBCFips(TimeStampTokenGenerator timeStampTokenGenerator) {
        this.timeStampTokenGenerator = timeStampTokenGenerator;
    }

    /**
     * Creates new wrapper instance for {@link TimeStampTokenGenerator}.
     *
     * @param siGen  SignerInfoGenerator wrapper
     * @param dgCalc DigestCalculator wrapper
     * @param policy ASN1ObjectIdentifier wrapper
     */
    public TimeStampTokenGeneratorBCFips(ISignerInfoGenerator siGen, IDigestCalculator dgCalc,
            IASN1ObjectIdentifier policy) throws TSPExceptionBCFips {
        try {
            this.timeStampTokenGenerator = new TimeStampTokenGenerator(
                    ((SignerInfoGeneratorBCFips) siGen).getSignerInfoGenerator(),
                    ((DigestCalculatorBCFips) dgCalc).getDigestCalculator(),
                    ((ASN1ObjectIdentifierBCFips) policy).getASN1ObjectIdentifier());
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampTokenGenerator}.
     */
    public TimeStampTokenGenerator getTimeStampTokenGenerator() {
        return timeStampTokenGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAccuracySeconds(int i) {
        timeStampTokenGenerator.setAccuracySeconds(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCertificates(IJcaCertStore jcaCertStore) {
        timeStampTokenGenerator.addCertificates(((JcaCertStoreBCFips) jcaCertStore).getJcaCertStore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampToken generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws TSPExceptionBCFips {
        try {
            return new TimeStampTokenBCFips(timeStampTokenGenerator.generate(
                    ((TimeStampRequestBCFips) request).getTimeStampRequest(), bigInteger, date));
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
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
        TimeStampTokenGeneratorBCFips that = (TimeStampTokenGeneratorBCFips) o;
        return Objects.equals(timeStampTokenGenerator, that.timeStampTokenGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampTokenGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampTokenGenerator.toString();
    }
}
