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

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequestGenerator;

import java.math.BigInteger;
import java.util.Objects;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TimeStampRequestGenerator}.
 */
public class TimeStampRequestGeneratorBC implements ITimeStampRequestGenerator {
    private final TimeStampRequestGenerator requestGenerator;

    /**
     * Creates new wrapper instance for {@link TimeStampRequestGenerator}.
     *
     * @param requestGenerator {@link TimeStampRequestGenerator} to be wrapped
     */
    public TimeStampRequestGeneratorBC(TimeStampRequestGenerator requestGenerator) {
        this.requestGenerator = requestGenerator;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampRequestGenerator}.
     */
    public TimeStampRequestGenerator getRequestGenerator() {
        return requestGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCertReq(boolean var1) {
        requestGenerator.setCertReq(var1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReqPolicy(String reqPolicy) {
        requestGenerator.setReqPolicy(reqPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampRequest generate(IASN1ObjectIdentifier objectIdentifier, byte[] imprint, BigInteger nonce) {
        return new TimeStampRequestBC(requestGenerator.generate(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(), imprint, nonce));
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
        TimeStampRequestGeneratorBC that = (TimeStampRequestGeneratorBC) o;
        return Objects.equals(requestGenerator, that.requestGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(requestGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return requestGenerator.toString();
    }
}
