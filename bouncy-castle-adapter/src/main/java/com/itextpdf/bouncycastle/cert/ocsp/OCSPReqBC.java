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

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.x509.ExtensionBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IReq;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.Req;

/**
 * Wrapper class for {@link OCSPReq}.
 */
public class OCSPReqBC implements IOCSPReq {
    private final OCSPReq ocspReq;

    /**
     * Creates new wrapper instance for {@link OCSPReq}.
     *
     * @param ocspReq {@link OCSPReq} to be wrapped
     */
    public OCSPReqBC(OCSPReq ocspReq) {
        this.ocspReq = ocspReq;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPReq}.
     */
    public OCSPReq getOcspReq() {
        return ocspReq;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return ocspReq.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IReq[] getRequestList() {
        Req[] reqs = ocspReq.getRequestList();
        IReq[] reqsBC = new IReq[reqs.length];
        for (int i = 0; i < reqs.length; ++i) {
            reqsBC[i] = new ReqBC(reqs[i]);
        }
        return reqsBC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtension getExtension(IASN1ObjectIdentifier objectIdentifier) {
        return new ExtensionBC(ocspReq.getExtension(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier()));
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
        OCSPReqBC ocspReqBC = (OCSPReqBC) o;
        return Objects.equals(ocspReq, ocspReqBC.ocspReq);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspReq);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspReq.toString();
    }
}
