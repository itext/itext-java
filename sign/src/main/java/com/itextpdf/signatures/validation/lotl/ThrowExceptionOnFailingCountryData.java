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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

/**
 * This class implements the {@link IOnFailingCountryLotlData} interface and provides a strategy
 * for handling failures when fetching country-specific trusted list .
 * <p>
 * It throws an {@link InvalidLotlDataException} if the specific country fetch or the trusted lists validation fails.
 */
public class ThrowExceptionOnFailingCountryData implements IOnFailingCountryLotlData {

    /**
     * Creates an instance of {@link ThrowExceptionOnFailingCountryData}.
     */
    public ThrowExceptionOnFailingCountryData() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCountryFailure(CountrySpecificLotlFetcher.Result fetchResult) {
        CountrySpecificLotl country = fetchResult.getCountrySpecificLotl();
        throw new InvalidLotlDataException(
                MessageFormatUtil.format(SignExceptionMessageConstant.FAILED_TO_FETCH_LOTL_FOR_COUNTRY,
                        country.getSchemeTerritory(), country.getTslLocation(), fetchResult.getLocalReport()),
                fetchResult.getLocalReport());
    }
}
