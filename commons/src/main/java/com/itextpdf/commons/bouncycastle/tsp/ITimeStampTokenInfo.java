package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.io.IOException;
import java.util.Date;

public interface ITimeStampTokenInfo {
    IAlgorithmIdentifier getHashAlgorithm();

    ITSTInfo toASN1Structure();

    Date getGenTime();

    byte[] getEncoded() throws IOException;
}
