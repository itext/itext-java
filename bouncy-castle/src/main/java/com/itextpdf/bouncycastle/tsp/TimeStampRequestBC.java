package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;

import java.io.IOException;
import org.bouncycastle.tsp.TimeStampRequest;

public class TimeStampRequestBC implements ITimeStampRequest {

    private final TimeStampRequest timeStampRequest;

    public TimeStampRequestBC(TimeStampRequest timeStampRequest) {
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
