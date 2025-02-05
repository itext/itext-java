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

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;

/**
 * Wrapper class for {@link OCSPRespBuilder}.
 */
public class OCSPRespBuilderBCFips implements IOCSPRespBuilder {
    private static final OCSPRespBuilderBCFips INSTANCE = new OCSPRespBuilderBCFips(null);

    private static final int SUCCESSFUL = OCSPRespBuilder.SUCCESSFUL;

    private final OCSPRespBuilder ocspRespBuilder;

    /**
     * Creates new wrapper instance for {@link OCSPRespBuilder}.
     *
     * @param ocspRespBuilder {@link OCSPRespBuilder} to be wrapped
     */
    public OCSPRespBuilderBCFips(OCSPRespBuilder ocspRespBuilder) {
        this.ocspRespBuilder = ocspRespBuilder;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPRespBuilderBCFips} instance.
     */
    public static OCSPRespBuilderBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPRespBuilder}.
     */
    public OCSPRespBuilder getOcspRespBuilder() {
        return ocspRespBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IOCSPResp build(int i, IBasicOCSPResp basicOCSPResp) throws OCSPExceptionBCFips {
        try {
            return new OCSPRespBCFips(
                    ocspRespBuilder.build(i, ((BasicOCSPRespBCFips) basicOCSPResp).getBasicOCSPResp()));
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
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
        OCSPRespBuilderBCFips that = (OCSPRespBuilderBCFips) o;
        return Objects.equals(ocspRespBuilder, that.ocspRespBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspRespBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspRespBuilder.toString();
    }
}
