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

import com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;

/**
 * Wrapper class for {@link DERTaggedObject}.
 */
public class DERTaggedObjectBC extends ASN1TaggedObjectBC implements IDERTaggedObject {
    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param derTaggedObject {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBC(DERTaggedObject derTaggedObject) {
        super(derTaggedObject);
    }

    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param i         int value to create {@link DERTaggedObject} to be wrapped
     * @param encodable {@link ASN1Encodable} to create {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBC(int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(i, encodable));
    }

    /**
     * Creates new wrapper instance for {@link DERTaggedObject}.
     *
     * @param b         boolean to create {@link DERTaggedObject} to be wrapped
     * @param i         int value to create {@link DERTaggedObject} to be wrapped
     * @param encodable {@link ASN1Encodable} to create {@link DERTaggedObject} to be wrapped
     */
    public DERTaggedObjectBC(boolean b, int i, ASN1Encodable encodable) {
        super(new DERTaggedObject(b, i, encodable));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERTaggedObject}.
     */
    public DERTaggedObject getDERTaggedObject() {
        return (DERTaggedObject) getEncodable();
    }
}
