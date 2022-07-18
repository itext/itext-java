package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface ICRLReason extends IASN1Encodable {
    int getKeyCompromise();
}
