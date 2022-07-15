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

/**
 * Wrapper class for {@link TimeStampResponseGenerator}.
 */
public class TimeStampResponseGeneratorBC implements ITimeStampResponseGenerator {
    private final TimeStampResponseGenerator timeStampResponseGenerator;

    /**
     * Creates new wrapper instance for {@link TimeStampResponseGenerator}.
     *
     * @param timeStampResponseGenerator {@link TimeStampResponseGenerator} to be wrapped
     */
    public TimeStampResponseGeneratorBC(TimeStampResponseGenerator timeStampResponseGenerator) {
        this.timeStampResponseGenerator = timeStampResponseGenerator;
    }

    /**
     * Creates new wrapper instance for {@link TimeStampResponseGenerator}.
     *
     * @param tokenGenerator TimeStampTokenGenerator wrapper
     * @param algorithms     set of algorithm strings
     */
    public TimeStampResponseGeneratorBC(ITimeStampTokenGenerator tokenGenerator, Set<String> algorithms) {
        this(new TimeStampResponseGenerator(
                ((TimeStampTokenGeneratorBC) tokenGenerator).getTimeStampTokenGenerator(), algorithms));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampResponseGenerator}.
     */
    public TimeStampResponseGenerator getTimeStampResponseGenerator() {
        return timeStampResponseGenerator;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampResponseGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampResponseGenerator.toString();
    }
}
