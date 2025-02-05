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
package com.itextpdf.commons.bouncycastle.asn1.pkcs;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for PKCSObjectIdentifiers that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IPKCSObjectIdentifiers {
    /**
     * Gets {@code id_aa_signatureTimeStampToken} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_aa_signatureTimeStampToken wrapper.
     */
    IASN1ObjectIdentifier getIdAaSignatureTimeStampToken();

    /**
     * Gets {@code id_aa_ets_sigPolicyId} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_aa_ets_sigPolicyId wrapper.
     */
    IASN1ObjectIdentifier getIdAaEtsSigPolicyId();

    /**
     * Gets {@code id_spq_ets_uri} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.id_spq_ets_uri wrapper.
     */
    IASN1ObjectIdentifier getIdSpqEtsUri();

    /**
     * Gets {@code envelopedData} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.envelopedData wrapper.
     */
    IASN1ObjectIdentifier getEnvelopedData();

    /**
     * Gets {@code data} constant for the wrapped PKCSObjectIdentifiers.
     *
     * @return PKCSObjectIdentifiers.data wrapper.
     */
    IASN1ObjectIdentifier getData();
}
