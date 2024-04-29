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
package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason;

import org.bouncycastle.asn1.x509.CRLReason;

/**
 * Wrapper class for {@link CRLReason}.
 */
public class CRLReasonBC extends ASN1EncodableBC implements ICRLReason {
    private static final CRLReasonBC INSTANCE = new CRLReasonBC(null);

    private static final int KEY_COMPROMISE = CRLReason.keyCompromise;
    private static final int REMOVE_FROM_CRL = CRLReason.removeFromCRL;

    /**
     * Creates new wrapper instance for {@link CRLReason}.
     *
     * @param reason {@link CRLReason} to be wrapped
     */
    public CRLReasonBC(CRLReason reason) {
        super(reason);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link CRLReasonBC} instance.
     */
    public static CRLReasonBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CRLReason}.
     */
    public CRLReason getCRLReason() {
        return (CRLReason) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKeyCompromise() {
        return KEY_COMPROMISE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRemoveFromCRL() {
        return REMOVE_FROM_CRL;
    }
}
