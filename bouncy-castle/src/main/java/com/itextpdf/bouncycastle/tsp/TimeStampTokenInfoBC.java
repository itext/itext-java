package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import org.bouncycastle.tsp.TimeStampTokenInfo;

public class TimeStampTokenInfoBC implements ITimeStampTokenInfo {
    private final TimeStampTokenInfo timeStampTokenInfo;

    public TimeStampTokenInfoBC(TimeStampTokenInfo timeStampTokenInfo) {
        this.timeStampTokenInfo = timeStampTokenInfo;
    }

    public TimeStampTokenInfo getTimeStampTokenInfo() {
        return timeStampTokenInfo;
    }

    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBC(timeStampTokenInfo.getHashAlgorithm());
    }

    @Override
    public ITSTInfo toASN1Structure() {
        return new TSTInfoBC(timeStampTokenInfo.toASN1Structure());
    }
}
