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
 * Container class, which contains set of single {@link CertificateSource} values.
 */
public final class CertificateSources {
    private final EnumSet<CertificateSource> set;

    private CertificateSources(EnumSet<CertificateSource> set) {
        this.set = set;
    }

    /**
     * Creates {@link CertificateSources} container from several {@link CertificateSource} values.
     *
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain
     *
     * @return {@link CertificateSources} container, containing provided elements
     */
    public static CertificateSources of(CertificateSource first,  CertificateSource ... rest) {
        return new CertificateSources(EnumSet.<CertificateSource>of(first, rest));
    }

    /**
     * Creates {@link CertificateSources} containing all {@link CertificateSource} values.
     *
     * @return {@link CertificateSources} container containing all {@link CertificateSource} values
     */
    public static CertificateSources all() {
        return new CertificateSources(EnumSet.<CertificateSource>allOf(CertificateSource.class));
    }

    /**
     * Creates {@link CertificateSources} containing all the elements of this type
     * that are not contained in the specified set.
     *
     * @param other another {@link CertificateSources} from whose complement to initialize this container
     *
     * @return the complement of the specified {@link CertificateSources}.
     */
    public static CertificateSources complementOf(CertificateSources other) {
        EnumSet<CertificateSource> result = EnumSet.<CertificateSource>complementOf(other.set);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("CertificateSources all has no valid complement.");
        }
        return new CertificateSources(result);
    }

    /**
     * Gets encapsulated {@link EnumSet} containing {@link CertificateSource} elements.
     *
     * @return encapsulated {@link EnumSet} containing {@link CertificateSource} elements
     */
    public EnumSet<CertificateSource> getSet() {
        return set;
    }
}
