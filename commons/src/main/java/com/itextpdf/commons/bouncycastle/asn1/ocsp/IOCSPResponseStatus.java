package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IOCSPResponseStatus extends IASN1Encodable {
    int getSuccessful();
}
