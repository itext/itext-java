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
package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ocsp.BasicOCSPResponseBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;

import java.util.Date;
import java.util.Objects;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.cert.ocsp.SingleResp;

/**
 * Wrapper class for {@link SingleResp}.
 */
public class SingleRespBC implements ISingleResp {
    private final SingleResp singleResp;

    /**
     * Creates new wrapper instance for {@link SingleResp}.
     *
     * @param singleResp {@link SingleResp} to be wrapped
     */
    public SingleRespBC(SingleResp singleResp) {
        this.singleResp = singleResp;
    }

    /**
     * Creates new wrapper instance for {@link SingleResp}.
     *
     * @param basicResp {@link IBasicOCSPResponse} wrapper to get {@link SingleResp}
     */
    public SingleRespBC(IBasicOCSPResponse basicResp) {
        this(new SingleResp(SingleResponse.getInstance(((BasicOCSPResponseBC) basicResp).getBasicOCSPResponse()
                .getTbsResponseData().getResponses().getObjectAt(0))));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SingleResp}.
     */
    public SingleResp getSingleResp() {
        return singleResp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateID getCertID() {
        return new CertificateIDBC(singleResp.getCertID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateStatus getCertStatus() {
        return new CertificateStatusBC(singleResp.getCertStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getNextUpdate() {
        return singleResp.getNextUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getThisUpdate() {
        return singleResp.getThisUpdate();
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
        SingleRespBC that = (SingleRespBC) o;
        return Objects.equals(singleResp, that.singleResp);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(singleResp);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return singleResp.toString();
    }
}
