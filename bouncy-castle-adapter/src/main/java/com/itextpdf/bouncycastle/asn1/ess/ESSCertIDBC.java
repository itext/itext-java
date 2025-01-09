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
package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;

import org.bouncycastle.asn1.ess.ESSCertID;

/**
 * Wrapper class for {@link ESSCertID}.
 */
public class ESSCertIDBC extends ASN1EncodableBC implements IESSCertID {
    /**
     * Creates new wrapper instance for {@link ESSCertID}.
     *
     * @param essCertID {@link ESSCertID} to be wrapped
     */
    public ESSCertIDBC(ESSCertID essCertID) {
        super(essCertID);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ESSCertID}.
     */
    public ESSCertID getEssCertID() {
        return (ESSCertID) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCertHash() {
        return getEssCertID().getCertHash();
    }
}
