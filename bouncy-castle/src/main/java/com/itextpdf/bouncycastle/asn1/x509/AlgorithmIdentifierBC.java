package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AlgorithmIdentifierBC extends ASN1EncodableBC implements IAlgorithmIdentifier {
    public AlgorithmIdentifierBC(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier);
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return (AlgorithmIdentifier) getEncodable();
    }

    @Override
    public IASN1ObjectIdentifier getAlgorithm() {
        return new ASN1ObjectIdentifierBC(getAlgorithmIdentifier().getAlgorithm());
    }
}
