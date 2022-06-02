package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import org.bouncycastle.tsp.TimeStampToken;

public class TimeStampTokenBC implements ITimeStampToken {
    private final TimeStampToken timeStampToken;

    public TimeStampTokenBC(TimeStampToken timeStampToken) {
        this.timeStampToken = timeStampToken;
    }

    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    @Override
    public ITimeStampTokenInfo getTimeStampInfo() {
        return new TimeStampTokenInfoBC(timeStampToken.getTimeStampInfo());
    }
}
