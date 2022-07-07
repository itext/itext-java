package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface ISigningCertificate extends IASN1Encodable {
    IESSCertID[] getCerts();
}
