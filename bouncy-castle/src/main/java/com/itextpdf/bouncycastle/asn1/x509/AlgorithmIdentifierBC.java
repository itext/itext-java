package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AlgorithmIdentifierBC implements IAlgorithmIdentifier {
    private final AlgorithmIdentifier algorithmIdentifier;

    public AlgorithmIdentifierBC(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithmIdentifier = algorithmIdentifier;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return algorithmIdentifier;
    }

    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBC(algorithmIdentifier.getAlgorithm());
    }
}
