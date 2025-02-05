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
package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSequence;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;

/**
 * Wrapper class for {@link DERSequence}.
 */
public class DERSequenceBCFips extends ASN1SequenceBCFips implements IDERSequence {
    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param derSequence {@link DERSequence} to be wrapped
     */
    public DERSequenceBCFips(DERSequence derSequence) {
        super(derSequence);
    }

    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param vector {@link ASN1EncodableVector} to create {@link DERSequence}
     */
    public DERSequenceBCFips(ASN1EncodableVector vector) {
        super(new DERSequence(vector));
    }

    /**
     * Creates new wrapper instance for {@link DERSequence}.
     *
     * @param encodable {@link ASN1Encodable} to create {@link DERSequence}
     */
    public DERSequenceBCFips(ASN1Encodable encodable) {
        super(new DERSequence(encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERSequence}.
     */
    public DERSequence getDERSequence() {
        return (DERSequence) getEncodable();
    }
}
