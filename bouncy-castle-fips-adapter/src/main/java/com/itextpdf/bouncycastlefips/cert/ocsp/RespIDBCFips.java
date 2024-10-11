/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IResponderID;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.RespID;

/**
 * Wrapper class for {@link RespID}.
 */
public class RespIDBCFips implements IRespID {
    private final RespID respID;

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param respID {@link RespID} to be wrapped
     */
    public RespIDBCFips(RespID respID) {
        this.respID = respID;
    }

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param x500Name X500Name wrapper to create {@link RespID}
     */
    public RespIDBCFips(IX500Name x500Name) {
        this(new RespID(((X500NameBCFips) x500Name).getX500Name()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IResponderID toASN1Primitive() {
        return new ResponderIDBCFips(respID.toASN1Primitive());
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RespID}.
     */
    public RespID getRespID() {
        return respID;
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
        RespIDBCFips that = (RespIDBCFips) o;
        return Objects.equals(respID, that.respID);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(respID);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return respID.toString();
    }

}
