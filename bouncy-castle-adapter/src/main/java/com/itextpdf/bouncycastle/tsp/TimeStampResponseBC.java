package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.bouncycastle.asn1.cmp.PKIFailureInfoBC;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;

public class TimeStampResponseBC implements ITimeStampResponse {

    private final TimeStampResponse timeStampResponse;

    public TimeStampResponseBC(TimeStampResponse timeStampRequest) {
        this.timeStampResponse = timeStampRequest;
    }

    public TimeStampResponse getTimeStampResponse() {
        return timeStampResponse;
    }

    @Override
    public void validate(ITimeStampRequest request) throws TSPExceptionBC {
        try {
            timeStampResponse.validate(((TimeStampRequestBC) request).getTimeStampRequest());
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    @Override
    public IPKIFailureInfo getFailInfo() {
        return new PKIFailureInfoBC(timeStampResponse.getFailInfo());
    }

    @Override
    public ITimeStampToken getTimeStampToken() {
        return new TimeStampTokenBC(timeStampResponse.getTimeStampToken());
    }

    @Override
    public String getStatusString() {
        return timeStampResponse.getStatusString();
    }

    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampResponse.getEncoded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeStampResponseBC that = (TimeStampResponseBC) o;
        return Objects.equals(timeStampResponse, that.timeStampResponse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStampResponse);
    }

    @Override
    public String toString() {
        return timeStampResponse.toString();
    }
}

