package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;

import org.bouncycastle.asn1.ess.ESSCertID;

public class ESSCertIDBC extends ASN1EncodableBC implements IESSCertID {
    public ESSCertIDBC(ESSCertID essCertID) {
        super(essCertID);
    }

    public ESSCertID getEssCertID() {
        return (ESSCertID) getEncodable();
    }

    @Override
    public byte[] getCertHash() {
        return getEssCertID().getCertHash();
    }
}
