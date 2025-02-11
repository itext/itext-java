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
package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IDERSet;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;

/**
 * Wrapper class for {@link DERSet}.
 */
public class DERSetBC extends ASN1SetBC implements IDERSet {
    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param derSet {@link DERSet} to be wrapped
     */
    public DERSetBC(DERSet derSet) {
        super(derSet);
    }

    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param vector {@link ASN1EncodableVector} to create {@link DERSet}
     */
    public DERSetBC(ASN1EncodableVector vector) {
        super(new DERSet(vector));
    }

    /**
     * Creates new wrapper instance for {@link DERSet}.
     *
     * @param encodable {@link ASN1Encodable} to create {@link DERSet}
     */
    public DERSetBC(ASN1Encodable encodable) {
        super(new DERSet(encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERSet}.
     */
    public DERSet getDERSet() {
        return (DERSet) getEncodable();
    }
}
