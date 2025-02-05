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
package com.itextpdf.bouncycastle.asn1.cmp;

import com.itextpdf.bouncycastle.asn1.ASN1PrimitiveBC;
import com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo;

import org.bouncycastle.asn1.cmp.PKIFailureInfo;

/**
 * Wrapper class for {@link PKIFailureInfo}.
 */
public class PKIFailureInfoBC extends ASN1PrimitiveBC implements IPKIFailureInfo {
    /**
     * Creates new wrapper instance for {@link PKIFailureInfo}.
     *
     * @param pkiFailureInfo {@link PKIFailureInfo} to be wrapped
     */
    public PKIFailureInfoBC(PKIFailureInfo pkiFailureInfo) {
        super(pkiFailureInfo);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PKIFailureInfo}.
     */
    public PKIFailureInfo getPkiFailureInfo() {
        return (PKIFailureInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        return getPkiFailureInfo().intValue();
    }
}
