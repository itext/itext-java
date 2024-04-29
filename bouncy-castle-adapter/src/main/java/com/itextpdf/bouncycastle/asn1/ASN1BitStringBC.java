/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.bouncycastle.asn1.IASN1BitString;

import org.bouncycastle.asn1.ASN1BitString;

/**
 * Wrapper class for {@link ASN1BitString}.
 */
public class ASN1BitStringBC extends ASN1PrimitiveBC implements IASN1BitString {
    /**
     * Creates new wrapper instance for {@link ASN1BitString}.
     *
     * @param asn1BitString {@link ASN1BitString} to be wrapped
     */
    public ASN1BitStringBC(ASN1BitString asn1BitString) {
        super(asn1BitString);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1BitString}.
     */
    public ASN1BitString getASN1BitString() {
        return (ASN1BitString) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return getASN1BitString().getString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        return getASN1BitString().intValue();
    }
}
