package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ASN1ObjectIdentifierBC extends ASN1PrimitiveBC implements IASN1ObjectIdentifier {
    public ASN1ObjectIdentifierBC(String identifier) {
        super(new ASN1ObjectIdentifier(identifier));
    }

    public ASN1ObjectIdentifierBC(ASN1ObjectIdentifier identifier) {
        super(identifier);
    }

    public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
        return (ASN1ObjectIdentifier) getPrimitive();
    }

    @Override
    public String getId() {
        return getASN1ObjectIdentifier().getId();
    }
}
