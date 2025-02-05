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
package com.itextpdf.bouncycastle.cms.jcajce;

import com.itextpdf.bouncycastle.cms.SignerInfoGeneratorBC;
import com.itextpdf.bouncycastle.operator.ContentSignerBC;
import com.itextpdf.bouncycastle.operator.DigestCalculatorProviderBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.cms.jcajce.IJcaSignerInfoGeneratorBuilder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * Wrapper class for {@link JcaSignerInfoGeneratorBuilder}.
 */
public class JcaSignerInfoGeneratorBuilderBC implements IJcaSignerInfoGeneratorBuilder {
    private final JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder;

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param jcaSignerInfoGeneratorBuilder {@link JcaSignerInfoGeneratorBuilder} to be wrapped
     */
    public JcaSignerInfoGeneratorBuilderBC(JcaSignerInfoGeneratorBuilder jcaSignerInfoGeneratorBuilder) {
        this.jcaSignerInfoGeneratorBuilder = jcaSignerInfoGeneratorBuilder;
    }

    /**
     * Creates new wrapper instance for {@link JcaSignerInfoGeneratorBuilder}.
     *
     * @param calculatorProvider DigestCalculatorProvider wrapper to create {@link JcaSignerInfoGeneratorBuilder}
     */
    public JcaSignerInfoGeneratorBuilderBC(IDigestCalculatorProvider calculatorProvider) {
        this(new JcaSignerInfoGeneratorBuilder(
                ((DigestCalculatorProviderBC) calculatorProvider).getCalculatorProvider()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaSignerInfoGeneratorBuilder}.
     */
    public JcaSignerInfoGeneratorBuilder getJcaSignerInfoGeneratorBuilder() {
        return jcaSignerInfoGeneratorBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISignerInfoGenerator build(IContentSigner signer, X509Certificate cert)
            throws OperatorCreationExceptionBC, CertificateEncodingException {
        try {
            return new SignerInfoGeneratorBC(jcaSignerInfoGeneratorBuilder.build(
                    ((ContentSignerBC) signer).getContentSigner(), cert));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
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
        JcaSignerInfoGeneratorBuilderBC that = (JcaSignerInfoGeneratorBuilderBC) o;
        return Objects.equals(jcaSignerInfoGeneratorBuilder, that.jcaSignerInfoGeneratorBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jcaSignerInfoGeneratorBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return jcaSignerInfoGeneratorBuilder.toString();
    }
}
