package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;

import org.bouncycastle.cert.ocsp.CertificateID;

public class CertificateIDBCFips implements ICertificateID {
    private final CertificateID certificateID;

    public CertificateIDBCFips(CertificateID certificateID) {
        this.certificateID = certificateID;
    }

    public CertificateID getCertificateID() {
        return certificateID;
    }

    @Override
    public IASN1ObjectIdentifier getHashAlgOID() {
        return new ASN1ObjectIdentifierBCFips(certificateID.getHashAlgOID());
    }
}
