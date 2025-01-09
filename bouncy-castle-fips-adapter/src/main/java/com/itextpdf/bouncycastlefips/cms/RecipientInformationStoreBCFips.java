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
package com.itextpdf.bouncycastlefips.cms;

import com.itextpdf.commons.bouncycastle.cms.IRecipientId;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

/**
 * Wrapper class for {@link RecipientInformationStore}.
 */
public class RecipientInformationStoreBCFips implements IRecipientInformationStore {
    private final RecipientInformationStore recipientInformationStore;

    /**
     * Creates new wrapper instance for {@link RecipientInformationStore}.
     *
     * @param recipientInformationStore {@link RecipientInformationStore} to be wrapped
     */
    public RecipientInformationStoreBCFips(RecipientInformationStore recipientInformationStore) {
        this.recipientInformationStore = recipientInformationStore;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RecipientInformationStore}.
     */
    public RecipientInformationStore getRecipientInformationStore() {
        return recipientInformationStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IRecipientInformation> getRecipients() {
        ArrayList<IRecipientInformation> recipientInformation = new ArrayList<>();
        Collection<RecipientInformation> recipients = recipientInformationStore.getRecipients();
        for (RecipientInformation recipient : recipients) {
            recipientInformation.add(new RecipientInformationBCFips(recipient));
        }
        return recipientInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientInformation get(IRecipientId id) {
        return new RecipientInformationBCFips(recipientInformationStore.get(((RecipientIdBCFips) id).getRecipientId()));
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
        RecipientInformationStoreBCFips that = (RecipientInformationStoreBCFips) o;
        return Objects.equals(recipientInformationStore, that.recipientInformationStore);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(recipientInformationStore);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return recipientInformationStore.toString();
    }
}
