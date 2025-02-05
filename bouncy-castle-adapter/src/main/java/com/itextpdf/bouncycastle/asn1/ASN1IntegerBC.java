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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

/**
 * Wrapper class for {@link ASN1Integer}.
 */
public class ASN1IntegerBC extends ASN1PrimitiveBC implements IASN1Integer {
    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(ASN1Integer i) {
        super(i);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i int value to create {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(int i) {
        super(new ASN1Integer(i));
    }

    /**
     * Creates new wrapper instance for {@link ASN1Integer}.
     *
     * @param i BigInteger value to create {@link ASN1Integer} to be wrapped
     */
    public ASN1IntegerBC(BigInteger i) {
        super(new ASN1Integer(i));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Integer}.
     */
    public ASN1Integer getASN1Integer() {
        return (ASN1Integer) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getValue() {
        return getASN1Integer().getValue();
    }
}
