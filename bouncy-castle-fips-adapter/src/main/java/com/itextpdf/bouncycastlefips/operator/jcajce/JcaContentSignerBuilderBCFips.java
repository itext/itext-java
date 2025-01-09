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
package com.itextpdf.bouncycastlefips.operator.jcajce;

import com.itextpdf.bouncycastlefips.operator.ContentSignerBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.jcajce.IJcaContentSignerBuilder;

import java.security.PrivateKey;
import java.util.Objects;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Wrapper class for {@link JcaContentSignerBuilder}.
 */
public class JcaContentSignerBuilderBCFips implements IJcaContentSignerBuilder {
    private final JcaContentSignerBuilder jcaContentSignerBuilder;

    /**
     * Creates new wrapper instance for {@link JcaContentSignerBuilder}.
     *
     * @param jcaContentSignerBuilder {@link JcaContentSignerBuilder} to be wrapped
     */
    public JcaContentSignerBuilderBCFips(JcaContentSignerBuilder jcaContentSignerBuilder) {
        this.jcaContentSignerBuilder = jcaContentSignerBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaContentSignerBuilder}.
     */
    public JcaContentSignerBuilder getJcaContentSignerBuilder() {
        return jcaContentSignerBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContentSigner build(PrivateKey pk) throws OperatorCreationExceptionBCFips {
        try {
            return new ContentSignerBCFips(jcaContentSignerBuilder.build(pk));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaContentSignerBuilder setProvider(String providerName) {
        jcaContentSignerBuilder.setProvider(providerName);
        return this;
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
        JcaContentSignerBuilderBCFips that = (JcaContentSignerBuilderBCFips) o;
        return Objects.equals(jcaContentSignerBuilder, that.jcaContentSignerBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaContentSignerBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaContentSignerBuilder.toString();
    }
}
