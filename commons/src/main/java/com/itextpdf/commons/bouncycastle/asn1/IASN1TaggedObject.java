package com.itextpdf.commons.bouncycastle.asn1;

public interface IASN1TaggedObject extends IASN1Primitive {
    IASN1Primitive getObject();

    int getTagNo();
}
