package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.cert.jcajce.JcaCertStoreBC;
import com.itextpdf.bouncycastle.cms.SignerInfoGeneratorBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
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

public class TimeStampTokenGeneratorBC implements ITimeStampTokenGenerator {
    private final TimeStampTokenGenerator timeStampTokenGenerator;

    public TimeStampTokenGeneratorBC(TimeStampTokenGenerator timeStampTokenGenerator) {
        this.timeStampTokenGenerator = timeStampTokenGenerator;
    }

    public TimeStampTokenGeneratorBC(ISignerInfoGenerator siGen, IDigestCalculator dgCalc, IASN1ObjectIdentifier policy)
            throws TSPExceptionBC {
        try {
            this.timeStampTokenGenerator = new TimeStampTokenGenerator(
                    ((SignerInfoGeneratorBC) siGen).getSignerInfoGenerator(),
                    ((DigestCalculatorBC) dgCalc).getDigestCalculator(),
                    ((ASN1ObjectIdentifierBC) policy).getASN1ObjectIdentifier());
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    public TimeStampTokenGenerator getTimeStampTokenGenerator() {
        return timeStampTokenGenerator;
    }

    @Override
    public void setAccuracySeconds(int i) {
        timeStampTokenGenerator.setAccuracySeconds(i);
    }

    @Override
    public void addCertificates(IJcaCertStore jcaCertStore) {
        timeStampTokenGenerator.addCertificates(((JcaCertStoreBC) jcaCertStore).getJcaCertStore());
    }

    @Override
    public ITimeStampToken generate(ITimeStampRequest request, BigInteger bigInteger, Date date) throws TSPExceptionBC {
        try {
            return new TimeStampTokenBC(timeStampTokenGenerator.generate(
                    ((TimeStampRequestBC) request).getTimeStampRequest(), bigInteger, date));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeStampTokenGeneratorBC that = (TimeStampTokenGeneratorBC) o;
        return Objects.equals(timeStampTokenGenerator, that.timeStampTokenGenerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStampTokenGenerator);
    }

    @Override
    public String toString() {
        return timeStampTokenGenerator.toString();
    }
}
