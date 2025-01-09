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

import java.math.BigInteger;
import java.util.Date;

/**
 * This interface represents the wrapper for TimeStampResponseGenerator that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampResponseGenerator {
    /**
     * Calls actual {@code generate} method for the wrapped TimeStampResponseGenerator object.
     *
     * @param request    the wrapper for request this response is for
     * @param bigInteger serial number for the response token
     * @param date       generation time for the response token
     *
     * @return {@link ITimeStampResponse} the wrapper for the generated TimeStampResponse object.
     * @throws AbstractTSPException if TSPException occurs during wrapped object method call.
     */
    ITimeStampResponse generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws AbstractTSPException;
}
