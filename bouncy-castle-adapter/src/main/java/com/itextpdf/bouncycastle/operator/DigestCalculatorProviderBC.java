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
package com.itextpdf.bouncycastle.operator;

import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculator;
import com.itextpdf.commons.bouncycastle.operator.IDigestCalculatorProvider;

import java.util.Objects;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * Wrapper class for {@link DigestCalculatorProvider}.
 */
public class DigestCalculatorProviderBC implements IDigestCalculatorProvider {
    private final DigestCalculatorProvider calculatorProvider;

    /**
     * Creates new wrapper instance for {@link DigestCalculatorProvider}.
     *
     * @param calculatorProvider {@link DigestCalculatorProvider} to be wrapped
     */
    public DigestCalculatorProviderBC(DigestCalculatorProvider calculatorProvider) {
        this.calculatorProvider = calculatorProvider;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DigestCalculatorProvider}.
     */
    public DigestCalculatorProvider getCalculatorProvider() {
        return calculatorProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws OperatorCreationExceptionBC {
        try {
            return new DigestCalculatorBC(calculatorProvider.get(
                    ((AlgorithmIdentifierBC) algorithmIdentifier).getAlgorithmIdentifier()));
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
        DigestCalculatorProviderBC that = (DigestCalculatorProviderBC) o;
        return Objects.equals(calculatorProvider, that.calculatorProvider);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(calculatorProvider);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return calculatorProvider.toString();
    }
}
