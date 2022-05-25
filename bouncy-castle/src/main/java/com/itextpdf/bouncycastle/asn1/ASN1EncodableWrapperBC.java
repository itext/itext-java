package com.itextpdf.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableWrapper;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;

public class ASN1EncodableWrapperBC implements ASN1EncodableBC, IASN1EncodableWrapper {
    private ASN1Encodable encodable;

    public ASN1EncodableWrapperBC(ASN1Encodable encodable) {
        this.encodable = encodable;
    }

    public ASN1Encodable getEncodable() {
        return encodable;
    }

    @Override
    public IASN1Primitive toASN1Primitive() {
        return new ASN1PrimitiveBC(encodable.toASN1Primitive());
    }
}
