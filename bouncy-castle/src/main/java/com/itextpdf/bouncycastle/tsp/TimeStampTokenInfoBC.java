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

    @Override
    public Date getGenTime() {
        return timeStampTokenInfo.getGenTime();
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampTokenInfo.getEncoded();
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(timeStampTokenInfo);
    }

    @Override
    public String toString() {
        return timeStampTokenInfo.toString();
    }
}
