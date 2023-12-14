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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.property.BackgroundRepeat.BackgroundRepeatValue;

/**
 * Utilities class for CSS mapping operations.
 * @deprecated will be removed in 7.2, use {@link CssBackgroundUtils} instead
 */
@Deprecated
public final class CssMappingUtils {
    /**
     * Creates a new {@link CssMappingUtils} instance.
     */
    private CssMappingUtils() {
    }

    /**
     * Parses the background repeat string value.
     *
     * @param value the string which stores the background repeat value
     * @return the background repeat as a {@link BackgroundRepeatValue} instance
     * @deprecated will be removed in 7.2, use {@link CssBackgroundUtils#parseBackgroundRepeat(String)} instead
     */
    @Deprecated
    public static BackgroundRepeatValue parseBackgroundRepeat(String value) {
        return CssBackgroundUtils.parseBackgroundRepeat(value);
    }
}
