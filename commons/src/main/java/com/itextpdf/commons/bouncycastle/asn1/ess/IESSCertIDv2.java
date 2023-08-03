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
package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

/**
 * This interface represents the wrapper for ESSCertIDv2 that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IESSCertIDv2 extends IASN1Encodable {
    /**
     * Calls actual {@code getHashAlgorithm} method for the wrapped ESSCertIDv2 object.
     *
     * @return {@link IAlgorithmIdentifier} hash algorithm wrapper.
     */
    IAlgorithmIdentifier getHashAlgorithm();

    /**
     * Calls actual {@code getCertHash} method for the wrapped ESSCertIDv2 object.
     *
     * @return certificate hash byte array.
     */
    byte[] getCertHash();
}
