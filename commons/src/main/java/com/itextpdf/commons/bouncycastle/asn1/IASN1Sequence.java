package com.itextpdf.commons.bouncycastle.asn1;

public interface IASN1Sequence extends IASN1Primitive {
    IASN1Encodable getObjectAt(int i);

    int size();
}
