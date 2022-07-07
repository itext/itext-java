package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.bouncycastlefips.asn1.tsp.TSTInfoBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import java.util.Date;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampTokenInfo;

public class TimeStampTokenInfoBCFips implements ITimeStampTokenInfo {
    private final TimeStampTokenInfo timeStampTokenInfo;

    public TimeStampTokenInfoBCFips(TimeStampTokenInfo timeStampTokenInfo) {
        this.timeStampTokenInfo = timeStampTokenInfo;
    }

    public TimeStampTokenInfo getTimeStampTokenInfo() {
        return timeStampTokenInfo;
    }

    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(timeStampTokenInfo.getHashAlgorithm());
    }

    @Override
    public ITSTInfo toASN1Structure() {
        return new TSTInfoBCFips(timeStampTokenInfo.toASN1Structure());
    }

    @Override
    public Date getGenTime() {
        return timeStampTokenInfo.getGenTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeStampTokenInfoBCFips that = (TimeStampTokenInfoBCFips) o;
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
