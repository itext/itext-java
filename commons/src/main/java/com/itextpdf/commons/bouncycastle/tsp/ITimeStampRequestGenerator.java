package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import java.math.BigInteger;

public interface ITimeStampRequestGenerator {

    void setCertReq(boolean var1);

    void setReqPolicy(String reqPolicy);

    ITimeStampRequest generate(IASN1ObjectIdentifier objectIdentifier, byte[] imprint, BigInteger nonce);
}
