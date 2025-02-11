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

import com.itextpdf.commons.bouncycastle.asn1.IASN1UTCTime;

import org.bouncycastle.asn1.ASN1UTCTime;

/**
 * Wrapper class for {@link ASN1UTCTime}.
 */
public class ASN1UTCTimeBC extends ASN1PrimitiveBC implements IASN1UTCTime {
    /**
     * Creates new wrapper instance for {@link ASN1UTCTime}.
     *
     * @param asn1UTCTime {@link ASN1UTCTime} to be wrapped
     */
    public ASN1UTCTimeBC(ASN1UTCTime asn1UTCTime) {
        super(asn1UTCTime);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1UTCTime}.
     */
    public ASN1UTCTime getASN1UTCTime() {
        return (ASN1UTCTime) getEncodable();
    }
}
