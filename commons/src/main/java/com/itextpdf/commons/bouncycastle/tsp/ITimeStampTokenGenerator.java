package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;

import java.math.BigInteger;
import java.util.Date;

public interface ITimeStampTokenGenerator {
    void setAccuracySeconds(int i);

    void addCertificates(IJcaCertStore jcaCertStore);

    ITimeStampToken generate(ITimeStampRequest request, BigInteger bigInteger, Date date) throws AbstractTSPException;
}
