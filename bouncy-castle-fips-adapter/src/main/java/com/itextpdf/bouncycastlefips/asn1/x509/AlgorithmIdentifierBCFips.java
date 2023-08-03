/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Wrapper class for {@link AlgorithmIdentifier}.
 */
public class AlgorithmIdentifierBCFips extends ASN1EncodableBCFips implements IAlgorithmIdentifier {
    /**
     * Creates new wrapper instance for {@link AlgorithmIdentifier}.
     *
     * @param algorithmIdentifier {@link AlgorithmIdentifier} to be wrapped
     */
    public AlgorithmIdentifierBCFips(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link AlgorithmIdentifier}.
     */
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return (AlgorithmIdentifier) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBCFips(getAlgorithmIdentifier().getAlgorithm());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getParameters() {
        return new ASN1EncodableBCFips(getAlgorithmIdentifier().getParameters());
    }
}
