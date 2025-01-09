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
package com.itextpdf.signatures.validation.context;

import java.util.EnumSet;

/**
 * Container class, which contains set of single {@link ValidatorContext} values.
 */
public final class ValidatorContexts {
    private final EnumSet<ValidatorContext> set;

    private ValidatorContexts(EnumSet<ValidatorContext> set) {
        this.set = set;
    }

    /**
     * Creates {@link ValidatorContexts} container from several {@link ValidatorContext} values.
     *
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain
     *
     * @return {@link ValidatorContexts} container, containing provided elements
     */
    public static ValidatorContexts of(ValidatorContext first,  ValidatorContext ... rest) {
        return new ValidatorContexts(EnumSet.<ValidatorContext> of(first, rest));
    }

    /**
     * Creates {@link ValidatorContexts} containing all {@link ValidatorContext} values.
     *
     * @return {@link ValidatorContexts} container containing all {@link ValidatorContext} values
     */
    public static ValidatorContexts all() {
        return new ValidatorContexts(EnumSet.<ValidatorContext> allOf(ValidatorContext.class));
    }

    /**
     * Creates {@link ValidatorContexts} containing all the elements of this type
     * that are not contained in the specified set.
     *
     * @param other another {@link ValidatorContexts} from whose complement to initialize this container
     *
     * @return the complement of the specified {@link ValidatorContexts}.
     */
    public static ValidatorContexts complementOf(ValidatorContexts other) {
        EnumSet<ValidatorContext> result = EnumSet.<ValidatorContext> complementOf(other.set);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("ValidatorContexts.all has no valid complement.");
        }
        return new ValidatorContexts(result);
    }

    /**
     * Gets encapsulated {@link EnumSet} containing {@link ValidatorContext} elements.
     *
     * @return encapsulated {@link EnumSet} containing {@link ValidatorContext} elements
     */
    public EnumSet<ValidatorContext> getSet() {
        return set;
    }
}
