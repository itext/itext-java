package com.itextpdf.commons.bouncycastle.asn1;

public interface IASN1EncodableWrapper extends IASN1Encodable {
    IASN1Primitive toASN1Primitive();
}
