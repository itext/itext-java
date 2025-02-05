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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.IPropertyContainer;

/**
 * A specialized enum holding the possible values
 * for a list {@link com.itextpdf.layout.element.List}'s entry prefix.
 * This class is meant to be used as the value for the {@link Property#LIST_SYMBOL} key
 * in an {@link IPropertyContainer}.
 */
public enum ListNumberingType {
    DECIMAL,
    DECIMAL_LEADING_ZERO,
    ROMAN_LOWER,
    ROMAN_UPPER,
    ENGLISH_LOWER,
    ENGLISH_UPPER,
    GREEK_LOWER,
    GREEK_UPPER,
    /** Zapfdingbats font characters in range [172; 181] */
    ZAPF_DINGBATS_1,
    /** Zapfdingbats font characters in range [182; 191] */
    ZAPF_DINGBATS_2,
    /** Zapfdingbats font characters in range [192; 201] */
    ZAPF_DINGBATS_3,
    /** Zapfdingbats font characters in range [202; 221] */
    ZAPF_DINGBATS_4
}
