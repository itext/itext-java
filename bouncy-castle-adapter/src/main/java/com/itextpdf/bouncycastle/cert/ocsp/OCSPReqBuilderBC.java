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

import com.itextpdf.bouncycastle.asn1.x509.ExtensionsBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;

/**
 * Wrapper class for {@link OCSPReqBuilder}.
 */
public class OCSPReqBuilderBC implements IOCSPReqBuilder {
    private final OCSPReqBuilder reqBuilder;

    /**
     * Creates new wrapper instance for {@link OCSPReqBuilder}.
     *
     * @param reqBuilder {@link OCSPReqBuilder} to be wrapped
     */
    public OCSPReqBuilderBC(OCSPReqBuilder reqBuilder) {
        this.reqBuilder = reqBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPReqBuilder}.
     */
    public OCSPReqBuilder getReqBuilder() {
        return reqBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReqBuilder setRequestExtensions(IExtensions extensions) {
        reqBuilder.setRequestExtensions(((ExtensionsBC) extensions).getExtensions());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReqBuilder addRequest(ICertificateID certificateID) {
        reqBuilder.addRequest(((CertificateIDBC) certificateID).getCertificateID());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPReq build() throws OCSPExceptionBC {
        try {
            return new OCSPReqBC(reqBuilder.build());
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
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
        OCSPReqBuilderBC that = (OCSPReqBuilderBC) o;
        return Objects.equals(reqBuilder, that.reqBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(reqBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return reqBuilder.toString();
    }
}
