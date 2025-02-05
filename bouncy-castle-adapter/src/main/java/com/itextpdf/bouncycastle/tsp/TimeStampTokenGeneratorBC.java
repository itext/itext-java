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
import com.itextpdf.bouncycastle.cert.jcajce.JcaCertStoreBC;
import com.itextpdf.bouncycastle.cms.SignerInfoGeneratorBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampRequest;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenGenerator;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampTokenGenerator;

/**
 * Wrapper class for {@link TimeStampTokenGenerator}.
 */
public class TimeStampTokenGeneratorBC implements ITimeStampTokenGenerator {
    private final TimeStampTokenGenerator timeStampTokenGenerator;

    /**
     * Creates new wrapper instance for {@link TimeStampTokenGenerator}.
     *
     * @param timeStampTokenGenerator {@link TimeStampTokenGenerator} to be wrapped
     */
    public TimeStampTokenGeneratorBC(TimeStampTokenGenerator timeStampTokenGenerator) {
        this.timeStampTokenGenerator = timeStampTokenGenerator;
    }

    /**
     * Creates new wrapper instance for {@link TimeStampTokenGenerator}.
     *
     * @param siGen  SignerInfoGenerator wrapper
     * @param dgCalc DigestCalculator wrapper
     * @param policy ASN1ObjectIdentifier wrapper
     * @throws TSPExceptionBC if {@link TSPException} occurs during wrapped object method call.
     */
    public TimeStampTokenGeneratorBC(ISignerInfoGenerator siGen, IDigestCalculator dgCalc, IASN1ObjectIdentifier policy)
            throws TSPExceptionBC {
        try {
            this.timeStampTokenGenerator = new TimeStampTokenGenerator(
                    ((SignerInfoGeneratorBC) siGen).getSignerInfoGenerator(),
                    ((DigestCalculatorBC) dgCalc).getDigestCalculator(),
                    ((ASN1ObjectIdentifierBC) policy).getASN1ObjectIdentifier());
        } catch (TSPException e) {
            throw new TSPExceptionBC(e);
        }
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TimeStampTokenGenerator}.
     */
    public TimeStampTokenGenerator getTimeStampTokenGenerator() {
        return timeStampTokenGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAccuracySeconds(int i) {
        timeStampTokenGenerator.setAccuracySeconds(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCertificates(IJcaCertStore jcaCertStore) {
        timeStampTokenGenerator.addCertificates(((JcaCertStoreBC) jcaCertStore).getJcaCertStore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITimeStampToken generate(ITimeStampRequest request, BigInteger bigInteger, Date date) throws TSPExceptionBC {
        try {
            return new TimeStampTokenBC(timeStampTokenGenerator.generate(
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
        TimeStampTokenGeneratorBC that = (TimeStampTokenGeneratorBC) o;
        return Objects.equals(timeStampTokenGenerator, that.timeStampTokenGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(timeStampTokenGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return timeStampTokenGenerator.toString();
    }
}
