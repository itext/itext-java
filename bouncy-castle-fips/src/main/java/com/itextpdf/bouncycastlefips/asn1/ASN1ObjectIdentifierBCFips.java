package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ASN1ObjectIdentifierBCFips extends ASN1PrimitiveBCFips implements IASN1ObjectIdentifier {

    public ASN1ObjectIdentifierBCFips(String identifier) {
        super(new ASN1ObjectIdentifier(identifier));
    }

    public ASN1ObjectIdentifierBCFips(ASN1ObjectIdentifier identifier) {
        super(identifier);
    }

    public ASN1ObjectIdentifier getObjectIdentifier() {
        return (ASN1ObjectIdentifier) getPrimitive();
    }

    @Override
    public String getId() {
        return getObjectIdentifier().getId();
    }
}
