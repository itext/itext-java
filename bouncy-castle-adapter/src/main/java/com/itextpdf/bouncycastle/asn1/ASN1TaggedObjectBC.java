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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1TaggedObject;

/**
 * Wrapper class for {@link ASN1TaggedObject}.
 */
public class ASN1TaggedObjectBC extends ASN1PrimitiveBC implements IASN1TaggedObject {
    /**
     * Creates new wrapper instance for {@link ASN1TaggedObject}.
     *
     * @param taggedObject {@link ASN1TaggedObject} to be wrapped
     */
    public ASN1TaggedObjectBC(ASN1TaggedObject taggedObject) {
        super(taggedObject);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1TaggedObject}.
     */
    public ASN1TaggedObject getASN1TaggedObject() {
        return (ASN1TaggedObject) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Primitive getObject() {
        return new ASN1PrimitiveBC(getASN1TaggedObject().getBaseObject().toASN1Primitive());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTagNo() {
        return getASN1TaggedObject().getTagNo();
    }
}
