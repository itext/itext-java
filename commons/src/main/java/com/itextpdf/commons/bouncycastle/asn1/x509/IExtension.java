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
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for Extension that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IExtension extends IASN1Encodable {
    /**
     * Gets {@code cRLDistributionPoints} constant for the wrapped Extension.
     *
     * @return Extension.cRLDistributionPoints wrapper.
     */
    IASN1ObjectIdentifier getCRlDistributionPoints();

    /**
     * Gets {@code issuingDistributionPoint} constant for the wrapped Extension.
     *
     * @return Extension.issuingDistributionPoint wrapper.
     */
    IASN1ObjectIdentifier getIssuingDistributionPoint();

    /**
     * Gets {@code authorityInfoAccess} constant for the wrapped Extension.
     *
     * @return Extension.authorityInfoAccess wrapper.
     */
    IASN1ObjectIdentifier getAuthorityInfoAccess();

    /**
     * Gets {@code basicConstraints} constant for the wrapped Extension.
     *
     * @return Extension.basicConstraints wrapper.
     */
    IASN1ObjectIdentifier getBasicConstraints();

    /**
     * Gets {@code keyUsage} constant for the wrapped Extension.
     *
     * @return Extension.keyUsage wrapper.
     */
    IASN1ObjectIdentifier getKeyUsage();

    /**
     * Gets {@code extendedKeyUsage} constant for the wrapped Extension.
     *
     * @return Extension.extendedKeyUsage wrapper.
     */
    IASN1ObjectIdentifier getExtendedKeyUsage();

    /**
     * Gets {@code authorityKeyIdentifier} constant for the wrapped Extension.
     *
     * @return Extension.authorityKeyIdentifier wrapper.
     */
    IASN1ObjectIdentifier getAuthorityKeyIdentifier();

    /**
     * Gets {@code subjectKeyIdentifier} constant for the wrapped Extension.
     *
     * @return Extension.subjectKeyIdentifier wrapper.
     */
    IASN1ObjectIdentifier getSubjectKeyIdentifier();

    /**
     * Gets {@code expiredCertsOnCRL} constant for the wrapped Extension.
     *
     * @return Extension.expiredCertsOnCRL wrapper.
     */
    IASN1ObjectIdentifier getExpiredCertsOnCRL();
}
