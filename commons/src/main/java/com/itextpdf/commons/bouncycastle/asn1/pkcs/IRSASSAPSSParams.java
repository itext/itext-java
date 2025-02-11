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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.math.BigInteger;

/**
 * Wrapper interface for BouncyCastle's representation of RSASSA-PSS parameters in ASN.1.
 */
public interface IRSASSAPSSParams extends IASN1Encodable {
    /**
     * Return the {@link IAlgorithmIdentifier} describing the digest algorithm to be used in the signature.
     *
     * @return an {@link IAlgorithmIdentifier}
     */
    IAlgorithmIdentifier getHashAlgorithm();

    /**
     * Return the {@link IAlgorithmIdentifier} describing the mask generation function to be used in the signature.
     *
     * @return an {@link IAlgorithmIdentifier}
     */
    IAlgorithmIdentifier getMaskGenAlgorithm();

    /**
     * Return the salt length parameter. This is a {@link BigInteger} for API consistency reasons, but typical
     * values will be small.
     *
     * @return the salt length parameter
     */
    BigInteger getSaltLength();

    /**
     * Return the trailer field parameter. This is a {@link BigInteger} for API consistency reasons, but typical
     * values will be small.
     *
     * @return the trailer field parameter
     */
    BigInteger getTrailerField();
}
