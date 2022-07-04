package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;

public class ASN1OctetStringBC extends ASN1PrimitiveBC implements IASN1OctetString {
    public ASN1OctetStringBC(ASN1OctetString string) {
        super(string);
    }
    
    public ASN1OctetStringBC(IASN1TaggedObject taggedObject, boolean b) {
        super(ASN1OctetString.getInstance(((ASN1TaggedObjectBC) taggedObject).getTaggedObject(), b));
    }

    public ASN1OctetString getOctetString() {
        return (ASN1OctetString) getPrimitive();
    }

    @Override
    public byte[] getOctets() {
        return getOctetString().getOctets();
    }
}
