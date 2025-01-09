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
import com.itextpdf.commons.bouncycastle.asn1.x509.IDistributionPointName;
import com.itextpdf.commons.bouncycastle.asn1.x509.IIssuingDistributionPoint;
import com.itextpdf.commons.bouncycastle.asn1.x509.IReasonFlags;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;

/**
 * Wrapper class for {@link IssuingDistributionPoint}.
 */
public class IssuingDistributionPointBC extends ASN1EncodableBC implements IIssuingDistributionPoint {
    /**
     * Creates new wrapper instance for {@link IssuingDistributionPoint}.
     *
     * @param issuingDistPoint {@link IssuingDistributionPoint} to be wrapped
     */
    public IssuingDistributionPointBC(IssuingDistributionPoint issuingDistPoint) {
        super(issuingDistPoint);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link IssuingDistributionPoint}.
     */
    public IssuingDistributionPoint getIssuingDistributionPoint() {
        return (IssuingDistributionPoint) getEncodable();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IDistributionPointName getDistributionPoint() {
        return new DistributionPointNameBC(getIssuingDistributionPoint().getDistributionPoint());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean onlyContainsUserCerts() {
        return getIssuingDistributionPoint().onlyContainsUserCerts();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean onlyContainsCACerts() {
        return getIssuingDistributionPoint().onlyContainsCACerts();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isIndirectCRL() {
        return getIssuingDistributionPoint().isIndirectCRL();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean onlyContainsAttributeCerts() {
        return getIssuingDistributionPoint().onlyContainsAttributeCerts();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IReasonFlags getOnlySomeReasons() {
        return new ReasonFlagsBC(getIssuingDistributionPoint().getOnlySomeReasons());
    }
}
