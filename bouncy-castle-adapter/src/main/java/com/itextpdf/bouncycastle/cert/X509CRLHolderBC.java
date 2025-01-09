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
package com.itextpdf.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder;

import java.io.IOException;
import java.util.Objects;
import org.bouncycastle.cert.X509CRLHolder;

/**
 * Wrapper class for {@link X509CRLHolder}.
 */
public class X509CRLHolderBC implements IX509CRLHolder {
    private final X509CRLHolder x509CRLHolder;

    /**
     * Creates new wrapper instance for {@link X509CRLHolder}.
     *
     * @param x509CRLHolder {@link X509CRLHolder} to be wrapped
     */
    public X509CRLHolderBC(X509CRLHolder x509CRLHolder) {
        this.x509CRLHolder = x509CRLHolder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link X509CRLHolder}.
     */
    public X509CRLHolder getX509CRLHolder() {
        return x509CRLHolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return x509CRLHolder.getEncoded();
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
        X509CRLHolderBC that = (X509CRLHolderBC) o;
        return Objects.equals(x509CRLHolder, that.x509CRLHolder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x509CRLHolder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return x509CRLHolder.toString();
    }
}
