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
package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ocsp.OCSPResponseBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;

/**
 * Wrapper class for {@link OCSPResp}.
 */
public class OCSPRespBCFips implements IOCSPResp {
    private static final OCSPRespBCFips INSTANCE = new OCSPRespBCFips((OCSPResp) null);

    private static final int SUCCESSFUL = OCSPResp.SUCCESSFUL;

    private final OCSPResp ocspResp;

    /**
     * Creates new wrapper instance for {@link OCSPResp}.
     *
     * @param ocspResp {@link OCSPResp} to be wrapped
     */
    public OCSPRespBCFips(OCSPResp ocspResp) {
        this.ocspResp = ocspResp;
    }

    /**
     * Creates new wrapper instance for {@link OCSPResp}.
     *
     * @param ocspResponse OCSPResponse wrapper
     */
    public OCSPRespBCFips(IOCSPResponse ocspResponse) {
        this(new OCSPResp(((OCSPResponseBCFips) ocspResponse).getOcspResponse()));
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPRespBCFips} instance.
     */
    public static OCSPRespBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResp}.
     */
    public OCSPResp getOcspResp() {
        return ocspResp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return ocspResp.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return ocspResp.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResponseObject() throws OCSPExceptionBCFips {
        try {
            return ocspResp.getResponseObject();
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
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
        OCSPRespBCFips that = (OCSPRespBCFips) o;
        return Objects.equals(ocspResp, that.ocspResp);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspResp);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspResp.toString();
    }
}
