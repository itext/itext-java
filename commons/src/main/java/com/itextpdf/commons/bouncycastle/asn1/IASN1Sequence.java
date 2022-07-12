package com.itextpdf.commons.bouncycastle.asn1;

import java.util.Enumeration;

public interface IASN1Sequence extends IASN1Primitive {
    IASN1Encodable getObjectAt(int i);

    Enumeration getObjects();

    int size();

    IASN1Encodable[] toArray();
}
