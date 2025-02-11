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

import com.itextpdf.bouncycastle.cert.X509CertificateHolderBC;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;

import java.util.Objects;
import org.bouncycastle.cms.RecipientId;

/**
 * Wrapper class for {@link RecipientId}.
 */
public class RecipientIdBC implements IRecipientId {
    private final RecipientId recipientId;

    /**
     * Creates new wrapper instance for {@link RecipientId}.
     *
     * @param recipientId {@link RecipientId} to be wrapped
     */
    public RecipientIdBC(RecipientId recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientId}.
     */
    public RecipientId getRecipientId() {
        return recipientId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(IX509CertificateHolder holder) {
        return recipientId.match(((X509CertificateHolderBC) holder).getCertificateHolder());
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
        RecipientIdBC that = (RecipientIdBC) o;
        return Objects.equals(recipientId, that.recipientId);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipientId);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipientId.toString();
    }
}
