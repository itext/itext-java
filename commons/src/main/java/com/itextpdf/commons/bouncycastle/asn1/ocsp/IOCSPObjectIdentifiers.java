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
package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for OCSPObjectIdentifiers that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPObjectIdentifiers {
    /**
     * Gets {@code id_pkix_ocsp_basic} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_basic wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspBasic();

    /**
     * Gets {@code id_pkix_ocsp_nonce} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_nonce wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspNonce();

    /**
     * Gets {@code id_pkix_ocsp_nocheck} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_nocheck wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspNoCheck();

    /**
     * Gets {@code id_pkix_ocsp_archive_cutoff} constant for the wrapped OCSPObjectIdentifiers.
     *
     * @return OCSPObjectIdentifiers.id_pkix_ocsp_archive_cutoff wrapper.
     */
    IASN1ObjectIdentifier getIdPkixOcspArchiveCutoff();
}
