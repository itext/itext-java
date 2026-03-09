/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralSubtree;
import com.itextpdf.commons.bouncycastle.asn1.x509.INameConstraints;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraints;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Wrapper class for {@link NameConstraints}
 */
public class NameConstraintsBCFips extends ASN1EncodableBCFips implements INameConstraints {
    /**
     * Creates new wrapper instance for {@link NameConstraints}.
     *
     * @param nameConstraints {@link NameConstraints} to be wrapped
     */
    public NameConstraintsBCFips(NameConstraints nameConstraints) {
        super(nameConstraints);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link NameConstraints}.
     */
    public NameConstraints getNameConstraints() {
        return (NameConstraints) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGeneralSubtree[] getPermittedSubtrees() {
        GeneralSubtree[] permittedSubtress = getNameConstraints().getPermittedSubtrees();
        if (permittedSubtress == null) {
            return new IGeneralSubtree[0];
        } else {
            return Arrays.stream(permittedSubtress).map(subtree -> new GeneralSubtreeBCFips(subtree))
                    .collect(Collectors.toList()).toArray(new GeneralSubtreeBCFips[0]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGeneralSubtree[] getExcludedSubtrees() {
        GeneralSubtree[] excludedSubtrees = getNameConstraints().getExcludedSubtrees();
        if (excludedSubtrees == null) {
            return new IGeneralSubtree[0];
        } else {
            return Arrays.stream(excludedSubtrees).map(subtree -> new GeneralSubtreeBCFips(subtree))
                    .collect(Collectors.toList()).toArray(new GeneralSubtreeBCFips[0]);
        }
    }
}