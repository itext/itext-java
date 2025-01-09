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

import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;

import org.bouncycastle.asn1.DERIA5String;

/**
 * Wrapper class for {@link DERIA5String}.
 */
public class DERIA5StringBCFips extends ASN1PrimitiveBCFips implements IDERIA5String {
    /**
     * Creates new wrapper instance for {@link DERIA5String}.
     *
     * @param deria5String {@link DERIA5String} to be wrapped
     */
    public DERIA5StringBCFips(DERIA5String deria5String) {
        super(deria5String);
    }

    /**
     * Creates new wrapper instance for {@link DERIA5String}.
     *
     * @param str string to create {@link DERIA5String} to be wrapped
     */
    public DERIA5StringBCFips(String str) {
        this(new DERIA5String(str));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link DERIA5String}.
     */
    public DERIA5String getDerIA5String() {
        return (DERIA5String) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return getDerIA5String().getString();
    }
}
