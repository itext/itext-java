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

import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;

import java.util.Objects;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;

/**
 * Wrapper class for {@link RecipientInformation}.
 */
public class RecipientInformationBC implements IRecipientInformation {
    private final RecipientInformation recipientInformation;

    /**
     * Creates new wrapper instance for {@link RecipientInformation}.
     *
     * @param recipientInformation {@link RecipientInformation} to be wrapped
     */
    public RecipientInformationBC(RecipientInformation recipientInformation) {
        this.recipientInformation = recipientInformation;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientInformation}.
     */
    public RecipientInformation getRecipientInformation() {
        return recipientInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent(IRecipient recipient) throws CMSExceptionBC {
        try {
            return recipientInformation.getContent(((RecipientBC) recipient).getRecipient());
        } catch (CMSException e) {
            throw new CMSExceptionBC(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientId getRID() {
        return new RecipientIdBC(recipientInformation.getRID());
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
        RecipientInformationBC that = (RecipientInformationBC) o;
        return Objects.equals(recipientInformation, that.recipientInformation);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipientInformation);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipientInformation.toString();
    }
}
