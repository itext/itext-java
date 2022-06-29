package com.itextpdf.commons.bouncycastle.asn1;

import java.util.Enumeration;

public interface IASN1Set extends IASN1Primitive {
    Enumeration getObjects();

    int size();

    IASN1Encodable getObjectAt(int index);
}
