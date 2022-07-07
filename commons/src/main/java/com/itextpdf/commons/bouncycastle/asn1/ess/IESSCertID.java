package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface IESSCertID extends IASN1Encodable {
    byte[] getCertHash();
}
