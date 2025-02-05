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
package com.itextpdf.bouncycastlefips.tsp;

import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampRequest;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampRequest}.
 */
public class TimeStampRequestBCFips implements ITimeStampRequest {
    private final TimeStampRequest timeStampRequest;

    /**
     * Creates new wrapper instance for {@link TimeStampRequest}.
     *
     * @param timeStampRequest {@link TimeStampRequest} to be wrapped
     */
    public TimeStampRequestBCFips(TimeStampRequest timeStampRequest) {
        this.timeStampRequest = timeStampRequest;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampRequest}.
     */
    public TimeStampRequest getTimeStampRequest() {
        return timeStampRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampRequest.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getNonce() {
        return timeStampRequest.getNonce();
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
        TimeStampRequestBCFips that = (TimeStampRequestBCFips) o;
        return Objects.equals(timeStampRequest, that.timeStampRequest);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampRequest);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampRequest.toString();
    }
}
