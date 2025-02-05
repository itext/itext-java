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

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Wrapper class for {@link ASN1ObjectIdentifier}.
 */
public class ASN1ObjectIdentifierBCFips extends ASN1PrimitiveBCFips implements IASN1ObjectIdentifier {
    /**
     * Creates new wrapper instance for {@link ASN1ObjectIdentifier}.
     *
     * @param identifier string to create {@link ASN1ObjectIdentifier}
     */
    public ASN1ObjectIdentifierBCFips(String identifier) {
        super(new ASN1ObjectIdentifier(identifier));
    }

    /**
     * Creates new wrapper instance for {@link ASN1ObjectIdentifier}.
     *
     * @param identifier {@link ASN1ObjectIdentifier} to be wrapped
     */
    public ASN1ObjectIdentifierBCFips(ASN1ObjectIdentifier identifier) {
        super(identifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1ObjectIdentifier}.
     */
    public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
        return (ASN1ObjectIdentifier) getPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return getASN1ObjectIdentifier().getId();
    }
}
