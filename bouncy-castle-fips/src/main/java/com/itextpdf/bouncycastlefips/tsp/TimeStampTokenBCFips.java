package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import org.bouncycastle.tsp.TimeStampToken;

public class TimeStampTokenBCFips implements ITimeStampToken {
    private final TimeStampToken timeStampToken;

    public TimeStampTokenBCFips(TimeStampToken timeStampToken) {
        this.timeStampToken = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    @Override
    public ITimeStampTokenInfo getTimeStampInfo() {
        return new TimeStampTokenInfoBCFips(timeStampToken.getTimeStampInfo());
    }
}
