package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AlgorithmIdentifierBCFips extends ASN1EncodableBCFips implements IAlgorithmIdentifier {
    public AlgorithmIdentifierBCFips(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier);
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return (AlgorithmIdentifier) getEncodable();
    }

    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBCFips(getAlgorithmIdentifier().getAlgorithm());
    }
}
