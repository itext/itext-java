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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1BitStringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IReasonFlags;
import org.bouncycastle.asn1.x509.ReasonFlags;

/**
 * Wrapper class for {@link ReasonFlags}.
 */
public class ReasonFlagsBCFips extends ASN1BitStringBCFips implements IReasonFlags {
    /**
     * Creates new wrapper instance for {@link ReasonFlags}.
     *
     * @param reasonFlags {@link ReasonFlags} to be wrapped
     */
    public ReasonFlagsBCFips(ReasonFlags reasonFlags) {
        super(reasonFlags);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ReasonFlags}.
     */
    public ReasonFlags getReasonFlags() {
        return (ReasonFlags) getEncodable();
    }
}
