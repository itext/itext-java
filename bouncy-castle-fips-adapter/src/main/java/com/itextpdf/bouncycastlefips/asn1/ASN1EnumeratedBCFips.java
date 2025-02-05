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

import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;

import org.bouncycastle.asn1.ASN1Enumerated;

/**
 * Wrapper class for {@link ASN1Enumerated}.
 */
public class ASN1EnumeratedBCFips extends ASN1PrimitiveBCFips implements IASN1Enumerated {
    /**
     * Creates new wrapper instance for {@link ASN1Enumerated}.
     *
     * @param asn1Enumerated {@link ASN1Enumerated} to be wrapped
     */
    public ASN1EnumeratedBCFips(ASN1Enumerated asn1Enumerated) {
        super(asn1Enumerated);
    }

    /**
     * Creates new wrapper instance for {@link ASN1Enumerated}.
     *
     * @param i int value to create {@link ASN1Enumerated} to be wrapped
     */
    public ASN1EnumeratedBCFips(int i) {
        super(new ASN1Enumerated(i));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1Enumerated}.
     */
    public ASN1Enumerated getASN1Enumerated() {
        return (ASN1Enumerated) getEncodable();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int intValueExact() {
        return getASN1Enumerated().getValue().intValueExact();
    }
}
