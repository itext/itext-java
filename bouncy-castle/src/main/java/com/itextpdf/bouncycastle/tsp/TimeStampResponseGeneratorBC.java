package com.itextpdf.bouncycastle.tsp;

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

public class TimeStampResponseGeneratorBC implements ITimeStampResponseGenerator {
    private final TimeStampResponseGenerator timeStampResponseGenerator;

    public TimeStampResponseGeneratorBC(TimeStampResponseGenerator timeStampResponseGenerator) {
        this.timeStampResponseGenerator = timeStampResponseGenerator;
    }

    public TimeStampResponseGeneratorBC(ITimeStampTokenGenerator tokenGenerator, Set<String> algorithms) {
        this(new TimeStampResponseGenerator(
                ((TimeStampTokenGeneratorBC) tokenGenerator).getTimeStampTokenGenerator(), algorithms));
    }

    public TimeStampResponseGenerator getTimeStampResponseGenerator() {
        return timeStampResponseGenerator;
    }

    @Override
    public ITimeStampResponse generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws TSPExceptionBC {
        try {
            return new TimeStampResponseBC(timeStampResponseGenerator.generate(
                    ((TimeStampRequestBC) request).getTimeStampRequest(), bigInteger, date));
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
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
        TimeStampResponseGeneratorBC that = (TimeStampResponseGeneratorBC) o;
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
