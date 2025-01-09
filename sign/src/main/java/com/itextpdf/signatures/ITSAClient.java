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
package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * Time Stamp Authority client (caller) interface.
 * <p>
 * Interface used by the PdfPKCS7 digital signature builder to call
 * Time Stamp Authority providing RFC 3161 compliant time stamp token.
 */
public interface ITSAClient {

    /**
     * Get the time stamp estimated token size.
     * Implementation must return value large enough to accommodate the
     * entire token returned by {@link #getTimeStampToken(byte[])} prior
     * to actual {@link #getTimeStampToken(byte[])} call.
     *
     * @return an estimate of the token size
     */
    int getTokenSizeEstimate();

    /**
     * Returns the {@link MessageDigest} to digest the data imprint
     *
     * @return The {@link MessageDigest} object.
     * @throws GeneralSecurityException the general security exception
     */
    MessageDigest getMessageDigest() throws GeneralSecurityException;

    /**
     * Returns RFC 3161 timeStampToken.
     *
     * @param imprint byte[] - data imprint to be time-stamped
     * @return byte[] - encoded, TSA signed data of the timeStampToken
     * @throws Exception - TSA request failed
     */
    byte[] getTimeStampToken(byte[] imprint) throws Exception;
}
