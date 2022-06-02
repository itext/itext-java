package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AlgorithmIdentifierBCFips implements IAlgorithmIdentifier {
    private final AlgorithmIdentifier algorithmIdentifier;

    public AlgorithmIdentifierBCFips(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithmIdentifier = algorithmIdentifier;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return algorithmIdentifier;
    }

    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBCFips(algorithmIdentifier.getAlgorithm());
    }
}
