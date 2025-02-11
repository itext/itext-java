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

import com.itextpdf.bouncycastle.asn1.cmp.PKIFailureInfoBC;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampResponse;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampResponse}.
 */
public class TimeStampResponseBC implements ITimeStampResponse {
    private final TimeStampResponse timeStampResponse;

    /**
     * Creates new wrapper instance for {@link TimeStampResponse}.
     *
     * @param timeStampResponse {@link TimeStampResponse} to be wrapped
     */
    public TimeStampResponseBC(TimeStampResponse timeStampResponse) {
        this.timeStampResponse = timeStampResponse;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampResponse}.
     */
    public TimeStampResponse getTimeStampResponse() {
        return timeStampResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ITimeStampRequest request) throws TSPExceptionBC {
        try {
            timeStampResponse.validate(((TimeStampRequestBC) request).getTimeStampRequest());
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPKIFailureInfo getFailInfo() {
        return new PKIFailureInfoBC(timeStampResponse.getFailInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampToken getTimeStampToken() {
        return new TimeStampTokenBC(timeStampResponse.getTimeStampToken());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusString() {
        return timeStampResponse.getStatusString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampResponse.getEncoded();
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
        TimeStampResponseBC that = (TimeStampResponseBC) o;
        return Objects.equals(timeStampResponse, that.timeStampResponse);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampResponse);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampResponse.toString();
    }
}

