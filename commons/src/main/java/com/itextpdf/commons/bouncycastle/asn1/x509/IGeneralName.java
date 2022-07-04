package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IGeneralName extends IASN1Encodable {
    int getTagNo();
    
    int getUniformResourceIdentifier();
}
