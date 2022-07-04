package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

public class TimeStampRequestGeneratorBC implements ITimeStampRequestGenerator {

    private final TimeStampRequestGenerator requestGenerator;

    public TimeStampRequestGeneratorBC(TimeStampRequestGenerator requestGenerator) {
        this.requestGenerator = requestGenerator;
    }

    public TimeStampRequestGenerator getRequestGenerator() {
        return requestGenerator;
    }

    @Override
    public void setCertReq(boolean var1) {
        requestGenerator.setCertReq(var1);
    }

    @Override
    public void setReqPolicy(String reqPolicy) {
        requestGenerator.setReqPolicy(reqPolicy);
    }

    @Override
    public ITimeStampRequest generate(IASN1ObjectIdentifier objectIdentifier, byte[] imprint, BigInteger nonce) {
        return new TimeStampRequestBC(requestGenerator.generate(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getObjectIdentifier(), imprint, nonce));
    }
}
