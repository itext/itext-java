package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

public interface ISigningCertificateV2 extends IASN1Encodable {
    IESSCertIDv2[] getCerts();
}
