package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;

import java.io.IOException;
import org.bouncycastle.tsp.TimeStampRequest;

public class TimeStampRequestBCFips implements ITimeStampRequest {

    private final TimeStampRequest timeStampRequest;

    public TimeStampRequestBCFips(TimeStampRequest timeStampRequest) {
        this.timeStampRequest = timeStampRequest;
    }

    public TimeStampRequest getTimeStampRequest() {
        return timeStampRequest;
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampRequest.getEncoded();
    }
}
