/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for IssuingDistributionPoint that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IIssuingDistributionPoint extends IASN1Encodable {
    /**
     * Calls actual {@code getDistributionPoint} method for the wrapped IssuingDistributionPoint object.
     *
     * @return {@link IDistributionPointName} wrapped distribution point name.
     */
    IDistributionPointName getDistributionPoint();

    /**
     * Calls actual {@code onlyContainsUserCerts} method for the wrapped IssuingDistributionPoint object.
     *
     * @return true if onlyContainsUserCerts was set, false otherwise.
     */
    boolean onlyContainsUserCerts();

    /**
     * Calls actual {@code onlyContainsCACerts} method for the wrapped IssuingDistributionPoint object.
     *
     * @return true if onlyContainsCACerts was set, false otherwise.
     */
    boolean onlyContainsCACerts();

    /**
     * Calls actual {@code isIndirectCRL} method for the wrapped IssuingDistributionPoint object.
     *
     * @return boolean value identifying if CRL is indirect.
     */
    boolean isIndirectCRL();

    /**
     * Calls actual {@code onlyContainsAttributeCerts} method for the wrapped IssuingDistributionPoint object.
     *
     * @return true if onlyContainsAttributeCerts was set, false otherwise.
     */
    boolean onlyContainsAttributeCerts();

    /**
     * Calls actual {@code getOnlySomeReasons} method for the wrapped IssuingDistributionPoint object.
     *
     * @return {@link IReasonFlags} wrapped reason flags.
     */
    IReasonFlags getOnlySomeReasons();
}
