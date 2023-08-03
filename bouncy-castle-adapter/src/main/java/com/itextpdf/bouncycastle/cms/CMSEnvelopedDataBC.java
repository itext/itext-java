/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformationStore;

import java.util.Objects;
import org.bouncycastle.cms.CMSEnvelopedData;

/**
 * Wrapper class for {@link CMSEnvelopedData}.
 */
public class CMSEnvelopedDataBC implements ICMSEnvelopedData {
    private final CMSEnvelopedData cmsEnvelopedData;

    /**
     * Creates new wrapper instance for {@link CMSEnvelopedData}.
     *
     * @param cmsEnvelopedData {@link CMSEnvelopedData} to be wrapped
     */
    public CMSEnvelopedDataBC(CMSEnvelopedData cmsEnvelopedData) {
        this.cmsEnvelopedData = cmsEnvelopedData;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CMSEnvelopedData}.
     */
    public CMSEnvelopedData getCmsEnvelopedData() {
        return cmsEnvelopedData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRecipientInformationStore getRecipientInfos() {
        return new RecipientInformationStoreBC(cmsEnvelopedData.getRecipientInfos());
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
        CMSEnvelopedDataBC that = (CMSEnvelopedDataBC) o;
        return Objects.equals(cmsEnvelopedData, that.cmsEnvelopedData);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(cmsEnvelopedData);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return cmsEnvelopedData.toString();
    }
}
