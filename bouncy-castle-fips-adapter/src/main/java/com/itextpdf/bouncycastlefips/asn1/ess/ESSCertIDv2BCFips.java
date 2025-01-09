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
package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.x509.AlgorithmIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.ess.ESSCertIDv2;

/**
 * Wrapper class for {@link ESSCertIDv2}.
 */
public class ESSCertIDv2BCFips extends ASN1EncodableBCFips implements IESSCertIDv2 {
    /**
     * Creates new wrapper instance for {@link ESSCertIDv2}.
     *
     * @param essCertIDv2 {@link ESSCertIDv2} to be wrapped
     */
    public ESSCertIDv2BCFips(ESSCertIDv2 essCertIDv2) {
        super(essCertIDv2);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ESSCertIDv2}.
     */
    public ESSCertIDv2 getEssCertIDv2() {
        return (ESSCertIDv2) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getHashAlgorithm() {
        return new AlgorithmIdentifierBCFips(getEssCertIDv2().getHashAlgorithm());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getCertHash() {
        return getEssCertIDv2().getCertHash();
    }
}
