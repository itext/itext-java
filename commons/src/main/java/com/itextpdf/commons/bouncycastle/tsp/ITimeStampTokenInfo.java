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
package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import java.io.IOException;
import java.util.Date;

/**
 * This interface represents the wrapper for TimeStampTokenInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampTokenInfo {
    /**
     * Calls actual {@code getHashAlgorithm} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link IAlgorithmIdentifier} the wrapper for the received AlgorithmIdentifier object.
     */
    IAlgorithmIdentifier getHashAlgorithm();

    /**
     * Calls actual {@code toASN1Structure} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link ITSTInfo} TSTInfo wrapper.
     */
    ITSTInfo toASN1Structure();

    /**
     * Calls actual {@code getGenTime} method for the wrapped TimeStampTokenInfo object.
     *
     * @return {@link Date} the received genTime.
     */
    Date getGenTime();

    /**
     * Calls actual {@code getEncoded} method for the wrapped TimeStampTokenInfo object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;
}
