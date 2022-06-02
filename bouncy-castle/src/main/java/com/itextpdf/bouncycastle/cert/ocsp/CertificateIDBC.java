package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;

import org.bouncycastle.cert.ocsp.CertificateID;

public class CertificateIDBC implements ICertificateID {
    private final CertificateID certificateID;

    public CertificateIDBC(CertificateID certificateID) {
        this.certificateID = certificateID;
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBC(certificateID.getHashAlgOID());
    }
}
