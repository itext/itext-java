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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;

/**
 * Wrapper class for {@link ASN1Sequence}.
 */
public class ASN1SequenceBC extends ASN1PrimitiveBC implements IASN1Sequence {
    /**
     * Creates new wrapper instance for {@link ASN1Sequence}.
     *
     * @param sequence {@link ASN1Sequence} to be wrapped
     */
    public ASN1SequenceBC(ASN1Sequence sequence) {
        super(sequence);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Sequence}.
     *
     * @param obj to get {@link ASN1Sequence} instance to be wrapped
     */
    public ASN1SequenceBC(Object obj) {
        super(ASN1Sequence.getInstance(obj));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Sequence}.
     */
    public ASN1Sequence getASN1Sequence() {
        return (ASN1Sequence) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getObjectAt(int i) {
        return new ASN1EncodableBC(getASN1Sequence().getObjectAt(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration getObjects() {
        return getASN1Sequence().getObjects();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return getASN1Sequence().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable[] toArray() {
        ASN1Encodable[] encodables = getASN1Sequence().toArray();
        ASN1EncodableBC[] encodablesBC = new ASN1EncodableBC[encodables.length];
        for (int i = 0; i < encodables.length; ++i) {
            encodablesBC[i] = new ASN1EncodableBC(encodables[i]);
        }
        return encodablesBC;
    }
}
