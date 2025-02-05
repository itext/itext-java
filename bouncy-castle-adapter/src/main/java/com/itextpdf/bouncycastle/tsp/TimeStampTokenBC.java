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

import com.itextpdf.bouncycastle.cms.SignerInformationVerifierBC;
import com.itextpdf.commons.bouncycastle.cms.ISignerInformationVerifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampToken}.
 */
public class TimeStampTokenBC implements ITimeStampToken {
    private final TimeStampToken timeStampToken;

    /**
     * Creates new wrapper instance for {@link TimeStampToken}.
     *
     * @param timeStampToken {@link TimeStampToken} to be wrapped
     */
    public TimeStampTokenBC(TimeStampToken timeStampToken) {
        this.timeStampToken = timeStampToken;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampToken}.
     */
    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampTokenInfo getTimeStampInfo() {
        return new TimeStampTokenInfoBC(timeStampToken.getTimeStampInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return timeStampToken.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ISignerInformationVerifier verifier) throws TSPExceptionBC {
        try {
            timeStampToken.validate(((SignerInformationVerifierBC) verifier).getVerifier());
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
        TimeStampTokenBC that = (TimeStampTokenBC) o;
        return Objects.equals(timeStampToken, that.timeStampToken);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampToken);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampToken.toString();
    }
}
