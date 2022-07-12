package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import java.io.IOException;

public interface IOCSPReq {
    byte[] getEncoded() throws IOException;

    IReq[] getRequestList();

    IExtension getExtension(IASN1ObjectIdentifier objectIdentifier);
}
