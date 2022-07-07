package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;

import java.io.IOException;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeStampRequestBCFips that = (TimeStampRequestBCFips) o;
        return Objects.equals(timeStampRequest, that.timeStampRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStampRequest);
    }

    @Override
    public String toString() {
        return timeStampRequest.toString();
    }
}
