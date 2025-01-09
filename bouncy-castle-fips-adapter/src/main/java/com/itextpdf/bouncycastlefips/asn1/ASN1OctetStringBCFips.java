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

import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;

import org.bouncycastle.asn1.ASN1OctetString;

/**
 * Wrapper class for {@link ASN1OctetString}.
 */
public class ASN1OctetStringBCFips extends ASN1PrimitiveBCFips implements IASN1OctetString {
    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param string {@link ASN1OctetString} to be wrapped
     */
    public ASN1OctetStringBCFips(ASN1OctetString string) {
        super(string);
    }

    /**
     * Creates new wrapper instance for {@link ASN1OctetString}.
     *
     * @param taggedObject ASN1TaggedObject wrapper to create {@link ASN1OctetString}
     * @param b            boolean to create {@link ASN1OctetString}
     */
    public ASN1OctetStringBCFips(IASN1TaggedObject taggedObject, boolean b) {
        super(ASN1OctetString.getInstance(((ASN1TaggedObjectBCFips) taggedObject).getTaggedObject(), b));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1OctetString}.
     */
    public ASN1OctetString getOctetString() {
        return (ASN1OctetString) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getOctets() {
        return getOctetString().getOctets();
    }
}
