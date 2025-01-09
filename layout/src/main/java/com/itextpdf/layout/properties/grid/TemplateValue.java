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
package com.itextpdf.layout.properties.grid;

/**
 * A class that indicates its descendant class can be used as a grid template value.
 */
public abstract class TemplateValue {
    /**
     * Template value type.
     */
    protected final ValueType type;

    /**
     * Creates template value with a given type.
     *
     * @param type template value type
     */
    protected TemplateValue(ValueType type) {
        this.type = type;
    }

    /**
     * Gets template value type.
     *
     * @return template value type
     */
    public ValueType getType() {
        return type;
    }

    /**
     * Enum of sizing value types.
     */
    public enum ValueType {
        /**
         * Type which represents absolute point value.
         */
        POINT,
        /**
         * Type which represents relative percent value.
         */
        PERCENT,
        /**
         * Type which represents relative auto value.
         */
        AUTO,
        /**
         * Type which represents relative min content value.
         */
        MIN_CONTENT,
        /**
         * Type which represents relative max content value.
         */
        MAX_CONTENT,
        /**
         * Type which presents fit content function value.
         */
        FIT_CONTENT,
        /**
         * Type which represents minmax function value.
         */
        MINMAX,
        /**
         * Type which represents relative flexible value.
         */
        FLEX,
        /**
         * Type which represents fixed repeat value.
         */
        FIXED_REPEAT,
        /**
         * Type which represents auto-repeat value.
         */
        AUTO_REPEAT
    }
}
