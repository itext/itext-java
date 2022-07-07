package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponseGenerator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponseGenerator;

public class TimeStampResponseGeneratorBCFips implements ITimeStampResponseGenerator {
    private final TimeStampResponseGenerator timeStampResponseGenerator;

    public TimeStampResponseGeneratorBCFips(TimeStampResponseGenerator timeStampResponseGenerator) {
        this.timeStampResponseGenerator = timeStampResponseGenerator;
    }

    public TimeStampResponseGeneratorBCFips(ITimeStampTokenGenerator tokenGenerator, Set<String> algorithms) {
        this(new TimeStampResponseGenerator(
                ((TimeStampTokenGeneratorBCFips) tokenGenerator).getTimeStampTokenGenerator(), algorithms));
    }

    public TimeStampResponseGenerator getTimeStampResponseGenerator() {
        return timeStampResponseGenerator;
    }

    @Override
    public ITimeStampResponse generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws TSPExceptionBCFips {
        try {
            return new TimeStampResponseBCFips(timeStampResponseGenerator.generate(
                    ((TimeStampRequestBCFips) request).getTimeStampRequest(), bigInteger, date));
        } catch (TSPException e) {
            throw new TSPExceptionBCFips(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeStampResponseGeneratorBCFips that = (TimeStampResponseGeneratorBCFips) o;
        return Objects.equals(timeStampResponseGenerator, that.timeStampResponseGenerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStampResponseGenerator);
    }

    @Override
    public String toString() {
        return timeStampResponseGenerator.toString();
    }
}
