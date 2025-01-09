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
package com.itextpdf.bouncycastlefips.operator;

import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.util.Objects;
import org.bouncycastle.operator.ContentVerifierProvider;

/**
 * Wrapper class for {@link ContentVerifierProvider}.
 */
public class ContentVerifierProviderBCFips implements IContentVerifierProvider {
    private final ContentVerifierProvider provider;

    /**
     * Creates new wrapper instance for {@link ContentVerifierProvider}.
     *
     * @param provider {@link ContentVerifierProvider} to be wrapped
     */
    public ContentVerifierProviderBCFips(ContentVerifierProvider provider) {
        this.provider = provider;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ContentVerifierProvider}.
     */
    public ContentVerifierProvider getProvider() {
        return provider;
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
        ContentVerifierProviderBCFips that = (ContentVerifierProviderBCFips) o;
        return Objects.equals(provider, that.provider);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return provider.toString();
    }
}
