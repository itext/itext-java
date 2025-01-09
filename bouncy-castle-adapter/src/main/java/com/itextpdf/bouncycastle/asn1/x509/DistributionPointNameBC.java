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
package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;

import org.bouncycastle.asn1.x509.DistributionPointName;

/**
 * Wrapper class for {@link DistributionPointName}.
 */
public class DistributionPointNameBC extends ASN1EncodableBC implements IDistributionPointName {
    private static final DistributionPointNameBC INSTANCE = new DistributionPointNameBC(null);

    private static final int FULL_NAME = DistributionPointName.FULL_NAME;

    /**
     * Creates new wrapper instance for {@link DistributionPointName}.
     *
     * @param distributionPointName {@link DistributionPointName} to be wrapped
     */
    public DistributionPointNameBC(DistributionPointName distributionPointName) {
        super(distributionPointName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link DistributionPointNameBC} instance.
     */
    public static DistributionPointNameBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DistributionPointName}.
     */
    public DistributionPointName getDistributionPointName() {
        return (DistributionPointName) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return getDistributionPointName().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getName() {
        return new ASN1EncodableBC(getDistributionPointName().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFullName() {
        return FULL_NAME;
    }
}
