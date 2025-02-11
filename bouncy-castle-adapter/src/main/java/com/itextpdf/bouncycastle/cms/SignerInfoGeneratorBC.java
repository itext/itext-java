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
package com.itextpdf.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;

import java.util.Objects;
import org.bouncycastle.cms.SignerInfoGenerator;

/**
 * Wrapper class for {@link SignerInfoGenerator}.
 */
public class SignerInfoGeneratorBC implements ISignerInfoGenerator {
    private final SignerInfoGenerator signerInfoGenerator;

    /**
     * Creates new wrapper instance for {@link SignerInfoGenerator}.
     *
     * @param signerInfoGenerator {@link SignerInfoGenerator} to be wrapped
     */
    public SignerInfoGeneratorBC(SignerInfoGenerator signerInfoGenerator) {
        this.signerInfoGenerator = signerInfoGenerator;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SignerInfoGenerator}.
     */
    public SignerInfoGenerator getSignerInfoGenerator() {
        return signerInfoGenerator;
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
        SignerInfoGeneratorBC that = (SignerInfoGeneratorBC) o;
        return Objects.equals(signerInfoGenerator, that.signerInfoGenerator);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(signerInfoGenerator);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return signerInfoGenerator.toString();
    }
}
