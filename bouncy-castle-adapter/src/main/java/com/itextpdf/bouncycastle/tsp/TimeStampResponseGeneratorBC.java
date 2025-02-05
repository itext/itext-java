/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
