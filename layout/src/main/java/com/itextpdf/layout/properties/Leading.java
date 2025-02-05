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

import java.util.Objects;

/**
 * A specialized class that specifies the leading, "the vertical distance between
 * the baselines of adjacent lines of text" (ISO-32000-1, section 9.3.5).
 * Allows to use either an absolute (constant) leading value, or one
 * determined by font size. Pronounce as 'ledding' (cfr. Led Zeppelin).
 *
 * This class is meant to be used as the value for the
 * {@link Property#LEADING} key in an {@link IPropertyContainer}.
 */
public class Leading {
    /**
     * A leading type independent of font size.
     */
    public static final int FIXED = 1;

    /**
     * A leading type related to the font size and the resulting bounding box.
     */
    public static final int MULTIPLIED = 2;

    protected int type;
    protected float value;

    /**
     * Creates a Leading object.
     *
     * @param type a constant type that defines the calculation of actual
     * leading distance. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     * @param value to be used as a basis for the leading calculation.
     */
    public Leading(int type, float value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the calculation type of the Leading object.
     *
     * @return the calculation type. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the value to be used as the basis for the leading calculation.
     * @return a calculation value
     */
    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && type == ((Leading) obj).type && value == ((Leading) obj).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
