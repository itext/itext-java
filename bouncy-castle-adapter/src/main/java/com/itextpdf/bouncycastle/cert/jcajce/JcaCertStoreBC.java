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
package com.itextpdf.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;

import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaCertStore;

/**
 * Wrapper class for {@link JcaCertStore}.
 */
public class JcaCertStoreBC implements IJcaCertStore {
    private final JcaCertStore jcaCertStore;

    /**
     * Creates new wrapper instance for {@link JcaCertStore}.
     *
     * @param jcaCertStore {@link JcaCertStore} to be wrapped
     */
    public JcaCertStoreBC(JcaCertStore jcaCertStore) {
        this.jcaCertStore = jcaCertStore;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaCertStore}.
     */
    public JcaCertStore getJcaCertStore() {
        return jcaCertStore;
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
        JcaCertStoreBC that = (JcaCertStoreBC) o;
        return Objects.equals(jcaCertStore, that.jcaCertStore);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaCertStore);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaCertStore.toString();
    }
}
