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
package com.itextpdf.signatures.cms;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;

/**
 * This class represents algorithm identifier structure.
 */
public class AlgorithmIdentifier {

    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final String algorithm;

    private final IASN1Primitive parameters;

    /**
     * Creates an Algorithm identifier structure without parameters.
     *
     * @param algorithmId the Object id of the algorithm
     */
    public AlgorithmIdentifier(String algorithmId) {
        this.algorithm = algorithmId;
        parameters = null;
    }

    /**
     * Creates an Algorithm identifier structure with parameters.
     *
     * @param algorithmId the Object id of the algorithm
     * @param parameters  the algorithm parameters as an ASN1 structure
     */
    public AlgorithmIdentifier(String algorithmId, IASN1Primitive parameters) {
        this.algorithm = algorithmId;
        this.parameters = parameters;
    }

    /**
     * Creates an Algorithm identifier structure with parameters.
     *
     * @param asnStruct asn1 encodable to retrieve algorithm identifier
     */
    AlgorithmIdentifier(IASN1Encodable asnStruct) {
        IASN1Sequence algIdentifier = BC_FACTORY.createASN1Sequence(asnStruct);
        IASN1ObjectIdentifier algOid = BC_FACTORY.createASN1ObjectIdentifier(algIdentifier.getObjectAt(0));
        algorithm = algOid.getId();
        if (algIdentifier.size() > 1) {
            parameters = BC_FACTORY.createASN1Primitive(algIdentifier.getObjectAt(1));
        } else {
            parameters = null;
        }
    }

    /**
     * Return the OID of the algorithm.
     *
     * @return the OID of the algorithm.
     */
    public String getAlgorithmOid() {
        return algorithm;
    }

    /**
     * Return the parameters for the algorithm.
     *
     * @return the parameters for the algorithm.
     */
    public IASN1Primitive getParameters() {
        return parameters;
    }

    IASN1Sequence getAsASN1Sequence() {
        IASN1EncodableVector algorithmV = BC_FACTORY.createASN1EncodableVector();
        algorithmV.add(BC_FACTORY.createASN1ObjectIdentifier(algorithm));
        if (parameters != null) {
            algorithmV.add(parameters);
        }
        return BC_FACTORY.createDERSequence(algorithmV);
    }
}
