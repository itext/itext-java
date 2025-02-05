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
package com.itextpdf.forms.xfa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to process "classic" fields.
 */
class AcroFieldsSearch extends Xml2Som {
    private Map<String, String> acroShort2LongName;

    /**
     * Creates a new instance from a Collection with the full names.
     *
     * @param items the Collection
     */
    public AcroFieldsSearch(Collection<String> items) {
        inverseSearch = new HashMap<>();
        acroShort2LongName = new HashMap<>();
        for (String itemName : items) {
            String itemShort = getShortName(itemName);
            acroShort2LongName.put(itemShort, itemName);
            inverseSearchAdd(inverseSearch, splitParts(itemShort), itemName);
        }
    }

    /**
     * Gets the mapping from short names to long names. A long
     * name may contain the #subform name part.
     *
     * @return the mapping from short names to long names
     */
    public Map<String, String> getAcroShort2LongName() {
        return acroShort2LongName;
    }

    /**
     * Sets the mapping from short names to long names. A long
     * name may contain the #subform name part.
     *
     * @param acroShort2LongName the mapping from short names to long names
     */
    public void setAcroShort2LongName(Map<String, String> acroShort2LongName) {
        this.acroShort2LongName = acroShort2LongName;
    }
}
