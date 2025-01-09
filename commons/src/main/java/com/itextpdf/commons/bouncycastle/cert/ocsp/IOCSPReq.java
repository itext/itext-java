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
package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import java.io.IOException;

/**
 * This interface represents the wrapper for OCSPReq that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPReq {
    /**
     * Calls actual {@code getEncoded} method for the wrapped OCSPReq object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getRequestList} method for the wrapped OCSPReq object.
     *
     * @return {@link IReq} request wrappers list.
     */
    IReq[] getRequestList();

    /**
     * Calls actual {@code getExtension} method for the wrapped OCSPReq object.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     *
     * @return {@link IExtension} wrapper for received Extension.
     */
    IExtension getExtension(IASN1ObjectIdentifier objectIdentifier);
}
