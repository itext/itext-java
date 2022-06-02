package com.itextpdf.commons.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

public interface IASN1EncodableVector {
    void add(IASN1Primitive primitive);

    void add(IAttribute attribute);

    void add(IAlgorithmIdentifier element);
}
